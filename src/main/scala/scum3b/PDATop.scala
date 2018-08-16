package scum3b

import PacketAssembler.PacketDisAssembler

import chisel3._
import chisel3.core.UserModule
import chisel3.util._
import chisel3.experimental.{withClockAndReset, withClock, withReset}

class PDATopWrapper(debug: Boolean = false) extends Module {
  // Tests PDATop using a single clock domain.

  val pda = Module(new PDATop(debug))
  val io = IO(chiselTypeOf(pda.io))
  pda.io <> io
}

class PDATop(debug: Boolean = false) extends UserModule {
  val io = IO(new Bundle {

    // BLE PDA I/O
    val ARM_Switch_i = Input(Bool()) // switch between PREAMBLE and IDLE states
    val ARM_AA_i = Input(UInt(32.W))
    val ARM_CRC_Seed_i = Input(UInt(24.W))
    val ARM_DeWhite_Seed_i = Input(UInt(7.W))

	// TODO: Should ideally get rid of ready/valid interface?
    val ARM_Data_o = Decoupled(UInt(8.W))//decouple(sink): data, puch, full
    val ARM_Length_o = Decoupled(UInt(8.W))
    val ARM_Flag_AA_o = Decoupled(Bool())
    val ARM_Flag_CRC_o = Decoupled(Bool())

    // scum3b CDR result connections
    val CDR_recovered_clk = Input(Bool())
    val CDR_recovered_data = Input(Bool())

    // scum3b ARM data
    val ARM_data_i = Decoupled(UInt(1.W)) // note: BLE data input expects a decoupled interface. will need to generate ready/valid signals to feed in
	
	// Choose between CDR and ARM data inputs to PDA
    val choose_data_src = Input(Bool()) // 0 = CDR data; 1 = ARM data
    
    // Clock domains of CDR, PDA, ARM modules
    val clk_CDR = Input(Bool())
    val clk_PDA = Input(Bool())
    val clk_ARM = Input(Bool())
  })

  val pda = Module(new PacketDisAssembler())

   // TODO: does it matter how deep this is? Should be 2048, or is arbitrary depth OK? Might not even need to be asynchronous
  
  val cdrFIFO_wire = Wire(Decoupled(UInt(1.W)))

	withClockAndReset(clock=false.B.asClock, reset=false.B) {
		val cdrFIFO = Module(new AsyncQueue(UInt(1.W), 2048))
		// enqueue from CDR
		cdrFIFO.io.enq_clock := io.clk_CDR
		cdrFIFO.io.enq.bits := io.CDR_recovered_data
		cdrFIFO.io.enq.valid := io.CDR_recovered_clk

		// dequeue to PDA data input mux
		cdrFIFO.io.deq_clock := io.clk_PDA
		cdrFIFO_wire.bits := cdrFIFO.io.deq.bits
		cdrFIFO_wire.valid := cdrFIFO.io.deq.valid
		cdrFIFO.io.deq.ready := pda.io.AFIFO_Data_i.ready // output from PDA

	}
  // Choose either the CDR or ARM data to disassemble
  pda.io.AFIFO_Data_i.bits := Mux(io.choose_data_src, cdrFIFO_wire.bits, io.ARM_data_i.bits) // bits output from cdrFIFO and ARM_data_i
  pda.io.AFIFO_Data_i.valid := Mux(io.choose_data_src, cdrFIFO_wire.valid, io.ARM_data_i.valid) // bits output from cdrFIFO and ARM_data_i
  io.ARM_data_i.ready := pda.io.AFIFO_Data_i.ready // output from PDA

	def createARMFIFO[T <: Data](gen: => T, armSide: DecoupledIO[T], bleSide: DecoupledIO[T]): Unit = {
		withClockAndReset(clock=false.B.asClock, reset=false.B) {
			val fifo = Module(new AsyncQueue(gen, 2048)) // write 8 bits per clock cycle
			// enqueue from PDA
			fifo.io.enq_clock := io.clk_PDA
			fifo.io.enq <> bleSide

			// dequeue to ARM
			fifo.io.deq_clock := io.clk_ARM
			fifo.io.deq <> armSide
		}
	}

	createARMFIFO(UInt(8.W), io.ARM_Data_o, pda.io.DMA_Data_o)
	createARMFIFO(UInt(8.W), io.ARM_Length_o, pda.io.DMA_Length_o)
	createARMFIFO(Bool(), io.ARM_Flag_AA_o, pda.io.DMA_Flag_AA_o)
	createARMFIFO(Bool(), io.ARM_Flag_CRC_o, pda.io.DMA_Flag_CRC_o)
}
