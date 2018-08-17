package scum3b.test

import scum3b._
import PacketAssembler.test.PacketDisAssemblerTestUtils
import chisel3._
import chisel3.util._
import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}

class PDATopTest(c: PDATopWrapper, armInput: Boolean = true) extends PeekPokeTester(c) {
  import PacketDisAssemblerTestUtils.Testcase

  // Constants that remain throughout packet disassembly
  PacketDisAssemblerTestUtils.setRegisterConstants(this,
    c.io.ARM_AA_i, Testcase.AA_rev.litValue,
    c.io.ARM_CRC_Seed_i, Testcase.CRC_Seed.litValue,
    c.io.ARM_DeWhite_Seed_i, Testcase.DeWhite_Seed.litValue
  )

  poke(c.io.choose_data_src, if (armInput) 1 else 0)

  assert(armInput, "Only armInput supported at this time")

  //initialize
  poke(c.io.ARM_Switch_i, false.B)

  poke(c.io.ARM_Data_o.ready, false.B)
  poke(c.io.ARM_Length_o.ready, false.B)
  poke(c.io.ARM_Flag_AA_o.ready, false.B)
  poke(c.io.ARM_Flag_CRC_o.ready, false.B)

  val armFIFO = c.io.ARM_data_i
  val dataOut = c.io.ARM_Data_o

  if (armInput) {
    poke(armFIFO.valid, false.B)
    poke(armFIFO.bits, 0.U)
  } else {
    ???
  }

  step(2)

  //start of receiving packet
  poke(c.io.ARM_Switch_i, true.B)

  poke(c.io.ARM_Data_o.ready, true.B)
  poke(c.io.ARM_Length_o.ready, true.B)
  poke(c.io.ARM_Flag_AA_o.ready, true.B)
  poke(c.io.ARM_Flag_CRC_o.ready, true.B)

  if (armInput) {
    poke(armFIFO.valid, false.B)
    poke(armFIFO.bits, 0.U)
  } else {
    ???
  }

  // Random sequence before pre_preamble
  if (armInput) {
    PacketDisAssemblerTestUtils.writeBitsToFIFO(this, armFIFO, data = Testcase.random_sequence_rev, numBits = 72)
  } else {
    ???
  }

  // pre_preamble
  if (armInput) {
    PacketDisAssemblerTestUtils.writeBitsToFIFO(this, armFIFO, data = Testcase.pre_preamble_rev, numBits = 6)
  } else {
    ???
  }
  expect(dataOut.valid, false.B) //note
  //println(s"after random_sequence\n${peek(c.io.DMA_Data_o.bits)}\t0.U")

  // PREAMBLE
  if (armInput) {
    PacketDisAssemblerTestUtils.writeBitsToFIFO(this, armFIFO, data = Testcase.preamble_rev, numBits = 8)
  } else {
    ???
  }
  expect(dataOut.valid, false.B) //note
  println(s"after preamble\n${peek(dataOut.valid)}\tfalse")

  println(s"INFO: at cycle $t")

  // AA
  if (armInput) {
    PacketDisAssemblerTestUtils.writeBitsToFIFOAndCheck(this, fifo = armFIFO, writeData = Testcase.wholepacket_rad_rev,
      startBit = 0, endBit = 31,
      outputByteFifo = dataOut, checkData = Testcase.wholepacket_dig_rev)
  } else {
    ???
  }
  step(1) // need an extra cycle for DMA_Flag_AA_o to be valid
  expect(c.io.ARM_Flag_AA_o.bits, false.B)
  expect(c.io.ARM_Flag_AA_o.valid, true.B)
  println(s"j=flagAA\n${peek(c.io.ARM_Flag_AA_o.bits)}\ttrue")
  println(s"j=flagAA\n${peek(c.io.ARM_Flag_AA_o.valid)}\ttrue")

  // PDU_HEADER
  if (armInput) {
    PacketDisAssemblerTestUtils.writeBitsToFIFOAndCheck(this, fifo = armFIFO, writeData = Testcase.wholepacket_rad_rev,
      startBit = 32, endBit = 47,
      outputByteFifo = dataOut, checkData = Testcase.wholepacket_dig_rev)
  }
  step(1) // need an extra cycle before DMA_Length_o is valid
  expect(c.io.ARM_Length_o.bits, 16.U)
  expect(c.io.ARM_Length_o.valid, true.B)
  println(s"j=DMA_Length_o\n${peek(c.io.ARM_Length_o.bits)}\t16.U")
  step(1)
  poke(c.io.ARM_Length_o.ready, false.B)
/*
  // PDU_PAYLOAD
  PacketDisAssemblerTestUtils.writeBitsToFIFOAndCheck(this, fifo = c.io.AFIFO_Data_i, writeData = Testcase.wholepacket_rad_rev,
    startBit = 48, endBit = 22 * 8 - 1,
    outputByteFifo = c.io.DMA_Data_o, checkData = Testcase.wholepacket_dig_rev)

  // CRC
  PacketDisAssemblerTestUtils.writeBitsToFIFO(this, c.io.AFIFO_Data_i, data = Testcase.CRC_rad_rev, numBits = 24)

  step(2)
  expect(c.io.DMA_Flag_CRC_o.bits, false.B)
  expect(c.io.DMA_Flag_CRC_o.valid, true.B)
  println(s"j=flagCRC_bits\n${peek(c.io.DMA_Flag_CRC_o.bits)}\tfalse")
  println(s"j=flagCRC_valid\n${peek(c.io.DMA_Flag_CRC_o.valid)}\ttrue")

  step(2)

  poke(c.io.DMA_Flag_AA_o.ready, false.B)
  poke(c.io.DMA_Flag_CRC_o.ready, false.B)

*/
  //todo: add FIFO
  //todo: add invalid DMA
  //todo: check output: DMA_ready

  //todo: AA, CRC correct
  //todo: DMA_Switch_i OFF
  //todo: ready, valid always ON
}

class PDATopTester extends ChiselFlatSpec {
  behavior of "PDATop"
  backends foreach { backend =>
    it should s"perform correct operation in ARM mode" in {
      Driver(() => new PDATopWrapper, "verilator") {
        (c) => new PDATopTest(c, armInput=true)
      } should be(true)
    }
  }
}
