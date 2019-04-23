package PacketAssembler //note

import chisel3._
import chisel3.util._
import CRC._
import Whitening._

/**
  * PAInputBundle: input of packet assembler
  * @param trigger indicates the start of a new packet
  * @param data 8-bit decoupled input of data
  */
class PAInputBundle extends Bundle {
  val trigger = Input(Bool())
  val data = Flipped(Decoupled(UInt(8.W)))

  override def cloneType: this.type = PAInputBundle().asInstanceOf[this.type]
}

object PAInputBundle {
  def apply(): PAInputBundle = new PAInputBundle
}

/**
  * ParameterBundle: parameters needed in packet assembler/disassembler
  * @param crcSeed initial lfsr value for crc
  * @param whiteSeed initial lfsr value for whitening
  * @param aaDisassembler reference access address; used by packet disassembler only
  */
class ParameterBundle extends Bundle {
  val crcSeed = UInt(24.W)
  val whiteSeed = UInt(7.W)
  val aaDisassembler = UInt(32.W)
  val thresDisassembler = UInt(3.W)
  override def cloneType: this.type = ParameterBundle().asInstanceOf[this.type]
}

object ParameterBundle {
  def apply(): ParameterBundle = new ParameterBundle
}

/**
  * PAOutputBundle: output of packet assembler
  * @param data 1-bit decoupled output data
  * @param done boolean value that indicates the end of current packet
  */
class PAOutputBundle extends Bundle {
  val data = Decoupled(UInt(1.W))
  val done = Output(Bool())

  override def cloneType: this.type = PAOutputBundle().asInstanceOf[this.type]
}

object PAOutputBundle {
  def apply(): PAOutputBundle = new PAOutputBundle
}

class PacketAssemblerIO extends Bundle {
  val in = new PAInputBundle
  val param = Input(ParameterBundle())
  val out = new PAOutputBundle

  override def cloneType: this.type =
    PacketAssemblerIO().asInstanceOf[this.type]
}

object PacketAssemblerIO {
  def apply(): PacketAssemblerIO = new PacketAssemblerIO
}

class PacketAssembler extends Module {

  /**
    * stateUpdate
    * function that updates the finite state machine inside packet assembler
    * @param currentState current state of FSM
    * @param nextState supposed next state of FSM
    * @param length the value that counter needs to reach in order to move to next state; related to the length of packet subsections
    * @param counter counter of bytes
    * @param counterByte counter of bits within a byte
    * @param condition additional condition needed for state transition; usually output fire
    * @return the function returns a tuple (stateOut, counterOut, counterByteOut): the resulting state, counter and counterByte according to input
    */
  def stateUpdate(
      currentState: UInt,
      nextState: UInt,
      length: UInt,
      counter: UInt,
      counterByte: UInt,
      condition: Bool
  ) = {
    val stateOut = Wire(UInt(3.W))
    val counterOut = Wire(UInt(8.W))
    val counterByteOut = Wire(UInt(3.W))
    counterOut := counter
    counterByteOut := counterByte

    when (counter === length - 1.U && counterByte === 7.U && condition) {
      stateOut := nextState
      counterOut := 0.U
      counterByteOut := 0.U
    } .otherwise {
      stateOut := currentState
      when (condition) {
        when (counterByte === 7.U) {
          counterOut := counter + 1.U
          counterByteOut := 0.U
        } .otherwise {
          counterByteOut := counterByte + 1.U
        }
      }
    }
    (stateOut, counterOut, counterByteOut)
  }

  val io = IO(new PacketAssemblerIO)

  //state parameter
  val idle :: preamble :: aa :: pdu_header :: pdu_payload :: crc :: Nil = Enum(
    6
  )
  val state = RegInit(idle)

  val counter = RegInit(0.U(8.W)) //counter for bytes in packet
  val counter_byte = RegInit(0.U(3.W)) //counter for bits in bytes

  val pdu_length = RegInit(0.U(8.W))

  //Preamble & Pre-preamble
  val preamble0 = "b10101010".U //flipped preamble; start with least significant bit
  val preamble1 = "b01010101".U
  val prepreamble0 = "b00001111".U
  val prepreamble1 = "b11110000".U

  //Handshake parameters
  val in_ready = RegInit(false.B)
  val out_valid = RegInit(false.B)
  val in_fire = io.in.data.ready && io.in.data.valid
  val out_fire = io.out.data.ready && io.out.data.valid

  //data registers
  val data = RegInit(0.U(8.W))

  //CRC
  val crc_reset = io.in.trigger
  val crc_data = Wire(UInt(1.W))
  val crc_valid = Wire(Bool())
  val crc_result = Wire(UInt(24.W))
  val crc_seed = Wire(UInt(24.W))

  //whitening
  val white_reset = io.in.trigger
  val white_data = Wire(UInt(1.W))
  val white_valid = Wire(Bool())
  val white_result = Wire(UInt(1.W))
  val white_seed = Wire(UInt(7.W))

  //hardcode seed initiation
  //crc_seed := "b010101010101010101010101".U
  //white_seed := "b1100101".U
  crc_seed := io.param.crcSeed
  white_seed := io.param.whiteSeed

  //decouple assignments
  io.in.data.ready := in_ready
  io.out.data.valid := out_valid

  //output bits
  when (state === idle) {
    io.out.data.bits := 0.U
  } .otherwise {
    when (state === pdu_header || state === pdu_payload || state === crc) {
      io.out.data.bits := white_result
    } .otherwise { //PREAMBLE, aa
      io.out.data.bits := data(counter_byte)
    }
  }

  when (state === crc && counter === 2.U && counter_byte === 7.U && out_fire) { //end of the packet
    io.out.done := true.B
  } .otherwise {
    io.out.done := false.B
  }

  //State Transition with counter updates
  when(state === idle) {
      when (io.in.trigger) {
        state := preamble
        counter := 0.U
        counter_byte := 0.U
      } .otherwise {
        state := idle
      }
    }.elsewhen(state === preamble) {
      val (stateOut, counterOut, counterByteOut) =
        stateUpdate(preamble, aa, 2.U, counter, counter_byte, out_fire)
      state := stateOut
      counter := counterOut
      counter_byte := counterByteOut
    }.elsewhen(state === aa) {
      val (stateOut, counterOut, counterByteOut) =
        stateUpdate(aa, pdu_header, 4.U, counter, counter_byte, out_fire)
      state := stateOut
      counter := counterOut
      counter_byte := counterByteOut
    }.elsewhen(state === pdu_header) {
      val (stateOut, counterOut, counterByteOut) = stateUpdate(
        pdu_header,
        pdu_payload,
        2.U,
        counter,
        counter_byte,
        out_fire
      )
      state := stateOut
      counter := counterOut
      counter_byte := counterByteOut
    }.elsewhen(state === pdu_payload) {
      val (stateOut, counterOut, counterByteOut) = stateUpdate(
        pdu_payload,
        crc,
        pdu_length,
        counter,
        counter_byte,
        out_fire
      )
      state := stateOut
      counter := counterOut
      counter_byte := counterByteOut
    }.elsewhen(state === crc) {
      val (stateOut, counterOut, counterByteOut) =
        stateUpdate(crc, idle, 3.U, counter, counter_byte, out_fire)
      state := stateOut
      counter := counterOut
      counter_byte := counterByteOut
    }.otherwise {
	state := idle
    }
  //PDU_Length
  when (state === pdu_header && counter === 1.U) {
    pdu_length := data
  } .otherwise {
    //do nothing: registers preserve value//note
  }

  //in_ready //note:check corner cases
  when (state === aa || state === pdu_header || state === pdu_payload) {
    when (
      state === pdu_payload && counter === pdu_length - 1.U && counter_byte === 7.U && out_fire
    ) {
      in_ready := false.B //special case at the end of PAYLOAD
    } .elsewhen (counter_byte === 7.U && out_fire) {
        in_ready := true.B
      }
      .elsewhen (in_fire === 1.U) {
        in_ready := false.B
      }
      .otherwise {}
  } .otherwise { //IDLE, PREAMBLE, CRC
    when (
      state === preamble && counter === 1.U && counter_byte === 7.U && out_fire
    ) {
      in_ready := true.B //special case at the end of PREAMBLE: aa starts with ready
    }
    .otherwise {
        in_ready := false.B
      }
  }

  //output valid
  when (state === idle) {
    out_valid := false.B
  } .elsewhen (state === preamble) {
      when (counter === 1.U && counter_byte === 7.U && out_fire) {
        out_valid := false.B //special case at the end of PREAMBLE: aa starts with invalid
      } .elsewhen (io.in.data.valid) {
        out_valid := true.B
      }
    }
    .elsewhen (state === crc) {
      when (counter === 2.U && counter_byte === 7.U && out_fire) {
        out_valid := false.B //special case at the end of CRC
      } .otherwise {
        out_valid := true.B
      }
    }
    .otherwise { //aa, pdu_header, pdu_payload
      when (counter_byte === 7.U && out_fire) {
        out_valid := false.B
      } .elsewhen (in_fire === 1.U) {
        out_valid := true.B
      }
    }

  //data
  when (state === aa || state === pdu_header || state === pdu_payload) {
    when (in_fire) {
      data := io.in.data.bits
    } .otherwise {
      data := data
    }
  } .elsewhen (state === preamble) {
      when (io.in.data.valid) {
        when(counter === 0.U){
          data := Mux(io.in.data.bits(0) === 0.U, prepreamble0, prepreamble1)
        }.otherwise {
          data := Mux(io.in.data.bits(0) === 0.U, preamble0, preamble1)
        }
      } .otherwise {
        data := data
      }
    }
    .elsewhen (state === crc) {
      when (counter === 0.U) {
        data := crc_result(7, 0)
      } .elsewhen (counter === 1.U) {
          data := crc_result(15, 8)
        }
        .elsewhen (counter === 2.U) {
          data := crc_result(23, 16)
        }
        .otherwise {
          data := crc_result(7, 0) //error
        }
    }
    .otherwise { //IDLE
      data := 0.U //or preserve
    }

  //Set CRC Parameters
  when (state === pdu_header || state === pdu_payload) {
    crc_data := data(counter_byte)
    crc_valid := out_fire
  } .otherwise {
    crc_data := 0.U
    crc_valid := false.B
  }

  //Set Whitening Parameters
  when (state === pdu_header || state === pdu_payload) {
    white_data := data(counter_byte) //note
    white_valid := out_fire
  } .elsewhen (state === crc) {
      white_data := crc_result(counter * 8.U + counter_byte)
      white_valid := out_fire
    }
    .otherwise {
      white_data := 0.U
      white_valid := false.B
    }

  //Instantiate CRC Module
  val serial_crc = Module(new Serial_CRC)

  serial_crc.io.init := crc_reset
  serial_crc.io.operand.bits := crc_data
  serial_crc.io.operand.valid := crc_valid
  crc_result := serial_crc.io.result
  serial_crc.io.seed := crc_seed

  //Instantiate Whitening Module
  val white = Module(new Whitening)

  white.io.init := white_reset
  white.io.operand.bits := white_data
  white.io.operand.valid := white_valid
  white_result := white.io.result.bits
  white.io.result.ready := true.B
  white.io.seed := white_seed
}
