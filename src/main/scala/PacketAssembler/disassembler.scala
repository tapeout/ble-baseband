package PacketDisAssembler //note

import PacketAssembler._
import chisel3._
import chisel3.util._
import CRC._
import Whitening._

/**
  * PDAInputBundle: input of packet disassembler
  * @param switch indicates the start of a new packet
  * @param data 1-bit input of data
  */
class PDAInputBundle extends Bundle {
  val switch = Output(Bool())
  val data = Output(UInt(1.W)) //decouple(source): data, pop, empty

  override def cloneType: this.type = PDAInputBundle().asInstanceOf[this.type]
}

object PDAInputBundle {
  def apply(): PDAInputBundle = new PDAInputBundle
}

/**
  * PDAOutputBundle: output of packet disassembler
  * @param data 8-bit output data
  * @param length length of the packet
  * @param flag_aa checks if the access address of the packet matches the reference; false if not match, also contains a valid interface
  * @param flag_crc checks if the crc matches; false if not match, also contains a valid interface
  * @param done boolean value that indicates the end of current packet
  */
class PDAOutputBundle extends Bundle {
  val data = Decoupled(UInt(8.W)) //decouple(sink): data, push, full
  val length = Decoupled(UInt(8.W))
  val flag_aa = Decoupled(Bool())
  val flag_crc = Decoupled(Bool())
  val done = Output(Bool())

  override def cloneType: this.type = PDAOutputBundle().asInstanceOf[this.type]
}

object PDAOutputBundle {
  def apply(): PDAOutputBundle = new PDAOutputBundle
}

class PacketDisAssemblerIO extends Bundle {
  val in = Flipped(Decoupled(PDAInputBundle()))
  val param = Input(ParameterBundle())
  val out = new PDAOutputBundle

  override def cloneType: this.type =
    PacketDisAssemblerIO().asInstanceOf[this.type]
}

object PacketDisAssemblerIO {
  def apply(): PacketDisAssemblerIO = new PacketDisAssemblerIO
}

class PacketDisAssembler extends Module {

  /**
    * stateUpdate
    * function that updates the finite state machine inside packet disassembler
    * @param currentState current state of FSM
    * @param nextState supposed next state of FSM
    * @param length the value that counter needs to reach in order to move to next state; related to the length of packet subsections
    * @param counter counter of bytes
    * @param counterByte counter of bits within a byte
    * @param out_condition output condition needed for state transition; usually output fire
    * @param in_condition input condition needed for state transition; usually input fire
    * @return the function returns a tuple (stateOut, counterOut, counterByteOut): the resulting state, counter and counterByte according to input
    */
  def stateUpdate(
      currentState: UInt,
      nextState: UInt,
      length: UInt,
      counter: UInt,
      counterByte: UInt,
      out_condition: Bool,
      in_condition: Bool
  ): (UInt, UInt, UInt) = {

    val stateOut = Wire(UInt(3.W))
    val counterOut = Wire(UInt(8.W))
    val counterByteOut = Wire(UInt(3.W))
    counterOut := counter
    counterByteOut := counterByte

    when (counter === length - 1.U && out_condition) {
      stateOut := nextState
      counterOut := 0.U
      counterByteOut := 0.U
    } .otherwise {
      stateOut := currentState
      when (out_condition) {
        counterOut := counter + 1.U
      }
      when (in_condition) {
        when (counterByte === 7.U) {
          counterByteOut := 0.U
        } .otherwise {
          counterByteOut := counterByte + 1.U
        }
      }
    }
    (stateOut, counterOut, counterByteOut)
  }

  val io = IO(new PacketDisAssemblerIO)

  val idle :: preamble :: aa :: pdu_header :: pdu_payload :: crc :: wait_dma :: Nil =
    Enum(7)
  val state = RegInit(idle)

  val reg_aa = io.param.aaDisassembler

  val counter = RegInit(0.U(8.W)) //counter for bytes in packet
  val counter_byte = RegInit(0.U(3.W)) //counter for bits in bytes

  //packet status
  val pdu_length = RegInit(0.U(8.W))
  val pdu_length_valid = RegInit(false.B)
  val done = RegInit(false.B)
  val flag_aa = RegInit(true.B)
  val flag_aa_valid = RegInit(false.B)
  val flag_crc = RegInit(true.B)
  val flag_crc_valid = RegInit(false.B)

  //Preamble
  val preamble0 = "b10101010".U
  val preamble1 = "b01010101".U
  val preamble01 = Mux(reg_aa(0) === 0.U, preamble0, preamble1)
  val threshold = 8.U

  //Handshake Parameters
  val out_valid = RegInit(false.B)
  val out_fire = io.out.data.ready && io.out.data.valid
  val in_ready = RegInit(Bool(), false.B)
  val in_fire = io.in.ready && io.in.valid

  //data registers

  val data = RegInit(VecInit(Seq.fill(8)(false.B)))

  //crc
  val crc_reset = (state === idle)
  val crc_data = Wire(UInt(1.W))
  val crc_valid = Wire(Bool())
  val crc_result = Wire(UInt(24.W))
  val crc_seed = io.param.crcSeed

  //whitening
  val dewhite_reset = (state === idle)
  val dewhite_data = Wire(UInt(1.W))
  val dewhite_valid = Wire(Bool())
  val dewhite_result = Wire(UInt(1.W))
  val dewhite_seed = io.param.whiteSeed

  //output function
  when (state === idle || state === preamble) {
    io.out.data.bits := 0.U
  } .otherwise { //aa, pdu_header, pdu_payload, crc
    io.out.data.bits := data.asUInt
  }

  when (state === wait_dma) {
    done := true.B
  } .otherwise {
    done := false.B
  }

  io.out.length.bits := pdu_length
  io.out.length.valid := pdu_length_valid
  io.out.flag_aa.bits := flag_aa
  io.out.flag_aa.valid := flag_aa_valid
  io.out.flag_crc.bits := flag_crc
  io.out.flag_crc.valid := flag_crc_valid
  io.out.done := done

  io.out.data.valid := out_valid
  io.in.ready := in_ready

  when(state === idle) {
      when (io.in.bits.switch === true.B && io.in.valid) { //note: switch usage
        state := preamble
      } .otherwise {
        state := idle
      }
    }.elsewhen(state === preamble) {
      val cor = ~(data.asUInt ^ preamble01)
      val ones = PopCount(cor)
      when (ones >= threshold) {
        state := aa
        counter := 0.U
        counter_byte := 0.U
      } .otherwise {
        state := preamble
      }
    }.elsewhen(state === aa) {
      val (stateOut, counterOut, counterByteOut) =
        stateUpdate(
          aa,
          pdu_header,
          4.U,
          counter,
          counter_byte,
          out_fire,
          in_fire
        )
      state := stateOut
      counter := counterOut
      counter_byte := counterByteOut
    }.elsewhen(state === pdu_header) {
      val (stateOut, counterOut, counterByteOut) =
        stateUpdate(
          pdu_header,
          pdu_payload,
          2.U,
          counter,
          counter_byte,
          out_fire,
          in_fire
        )
      state := stateOut
      counter := counterOut
      counter_byte := counterByteOut
    }.elsewhen(state === pdu_payload) {
      val (stateOut, counterOut, counterByteOut) =
        stateUpdate(
          pdu_payload,
          crc,
          pdu_length,
          counter,
          counter_byte,
          out_fire,
          in_fire
        )
      state := stateOut
      counter := counterOut
      counter_byte := counterByteOut
    }.elsewhen(state === crc) {
      val (stateOut, counterOut, counterByteOut) =
        stateUpdate(
          crc,
          wait_dma,
          3.U,
          counter,
          counter_byte,
          out_fire,
          in_fire
        )
      state := stateOut
      counter := counterOut
      counter_byte := counterByteOut
    }.elsewhen(state === wait_dma) {
      when (io.out.data.ready === true.B) {
        state := idle
      } .otherwise {
        state := wait_dma
      }
    }.otherwise{
	state := idle
    }

  //PDU_Length
  when (state === pdu_header && counter === 1.U && out_fire === true.B) {
    pdu_length := data.asUInt
    pdu_length_valid := true.B
  } .elsewhen (state === idle) {
    pdu_length := 0.U
    pdu_length_valid := false.B
  }

  //Flag_aa
  when (state === aa && counter === 0.U && out_fire === true.B) { //note: same as above
    when (data.asUInt =/= reg_aa(7, 0)) {
      flag_aa := false.B
    }
  } .elsewhen (state === aa && counter === 1.U && out_fire === true.B) {
      when (data.asUInt =/= reg_aa(15, 8)) {
        flag_aa := false.B
      }
    }
    .elsewhen (state === aa && counter === 2.U && out_fire === true.B) {
      when (data.asUInt =/= reg_aa(23, 16)) {
        flag_aa := false.B
      }
    }
    .elsewhen (state === aa && counter === 3.U && out_fire === true.B) {
      when (data.asUInt =/= reg_aa(31, 24)) {
        flag_aa := false.B
      }
      flag_aa_valid := true.B
    }
    .elsewhen (state === idle) {
      flag_aa_valid := false.B
    }

  //Flag_crc
  when (state === crc && counter === 0.U && out_fire === true.B) { //note: same as above
    when (data.asUInt =/= crc_result(7, 0)) {
      flag_crc := false.B
    }
  } .elsewhen (state === crc && counter === 1.U && out_fire === true.B) {
      when (data.asUInt =/= crc_result(15, 8)) {
        flag_crc := false.B
      }
    }
    .elsewhen (state === crc && counter === 2.U && out_fire === true.B) {
      when (data.asUInt =/= crc_result(23, 16)) {
        flag_crc := false.B
      }
      flag_crc_valid := true.B
    }
    .elsewhen (state === idle) {
      flag_crc_valid := false.B
    }

  //out_valid
  when (state === idle || state === preamble) {
    out_valid := false.B
  } .otherwise { //aa, pdu_header, pdu_payload, crc
    when (counter_byte === 7.U && in_fire === true.B) {
      out_valid := true.B
    } .elsewhen (out_fire === true.B) {
      out_valid := false.B
    }
  }

  //AFIFO_Ready_w//note:check corner cases
  when (state === idle) {
    in_ready := false.B
  } .elsewhen (state === preamble) {
      in_ready := true.B
    }
    .otherwise { //aa, pdu_header, pdu_payload, crc
      when (counter_byte === 7.U && in_fire === true.B) {
        in_ready := false.B
      } .elsewhen (out_fire === true.B) {
        in_ready := true.B
      }
    }

  //data
  when (state === pdu_header || state === pdu_payload || state === crc) {
    when (in_fire === true.B) {
      //data(counter_byte) := dewhite_result.toBools
      when (dewhite_result === 0.U) {
        data(counter_byte) := false.B
      } .otherwise {
        data(counter_byte) := true.B
      }
    }
  } .elsewhen (state === preamble) {
      when (in_fire === true.B) {
        //data(7) := io.in.bits.data.toBools //note: subword assignment
        when (io.in.bits.data === 0.U) {
          data(7) := false.B
        } .otherwise {
          data(7) := true.B
        }
        for (i <- 0 to 6) { //value shifting
          data(i) := data(i + 1)
        }
      }
    }
    .elsewhen (state === aa) {
      when (in_fire === true.B) {
        //data(counter_byte) := io.in.bits.data.toBools
        when (io.in.bits.data === 0.U) {
          data(counter_byte) := false.B
        } .otherwise {
          data(counter_byte) := true.B
        }
      }
    }
    .otherwise { //idle
      for (i <- 0 to 7) {
        data(i) := false.B
      }
    }

  //crc
  when (state === pdu_header || state === pdu_payload) { //check corner cases
    crc_data := dewhite_result
    crc_valid := in_fire
  } .otherwise {
    crc_data := 0.U
    crc_valid := false.B
  }

  //dewhitening
  when (state === pdu_header || state === pdu_payload || state === crc) { //check corner cases
    dewhite_data := io.in.bits.data
    dewhite_valid := in_fire
  } .otherwise {
    dewhite_data := 0.U
    dewhite_valid := false.B
  }

  //crc instantiate
  val crc_inst = Module(new Serial_CRC)

  crc_inst.io.init := crc_reset
  crc_inst.io.operand.bits := crc_data
  crc_inst.io.operand.valid := crc_valid
  crc_result := crc_inst.io.result
  crc_inst.io.seed := crc_seed

  //whitening instantiate
  val white = Module(new Whitening)

  white.io.init := dewhite_reset
  white.io.operand.bits := dewhite_data
  white.io.operand.valid := dewhite_valid
  dewhite_result := white.io.result.bits
  white.io.result.ready := true.B
  white.io.seed := dewhite_seed

}
