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
    c.io.pdaio.ARM_AA_i, Testcase.AA_rev.litValue,
    c.io.pdaio.ARM_CRC_Seed_i, Testcase.CRC_Seed.litValue,
    c.io.pdaio.ARM_DeWhite_Seed_i, Testcase.DeWhite_Seed.litValue
  )

  poke(c.io.pdaio.choose_data_src, if (armInput) 1 else 0)

  //initialize
  poke(c.io.pdaio.ARM_Switch_i, false.B)

  poke(c.io.pdaio.ARM_Data_o.ready, false.B)
  poke(c.io.pdaio.ARM_Length_o.ready, false.B)
  poke(c.io.pdaio.ARM_Flag_AA_o.ready, false.B)
  poke(c.io.pdaio.ARM_Flag_CRC_o.ready, false.B)

  val armFIFO = c.io.pdaio.ARM_data_i
  val inputFIFO = if (armInput) armFIFO else c.io.cdrDecoupled
  val dataOut = c.io.pdaio.ARM_Data_o

  poke(inputFIFO.valid, false.B)
  poke(inputFIFO.bits, 0.U)

  step(2)

  //start of receiving packet
  poke(c.io.pdaio.ARM_Switch_i, true.B)

  poke(c.io.pdaio.ARM_Data_o.ready, true.B)
  poke(c.io.pdaio.ARM_Length_o.ready, true.B)
  poke(c.io.pdaio.ARM_Flag_AA_o.ready, true.B)
  poke(c.io.pdaio.ARM_Flag_CRC_o.ready, true.B)

  poke(inputFIFO.valid, false.B)
  poke(inputFIFO.bits, 0.U)

  // Random sequence before pre_preamble
  PacketDisAssemblerTestUtils.writeBitsToFIFO(this, inputFIFO, data = Testcase.random_sequence_rev, numBits = 72)

  // pre_preamble
  PacketDisAssemblerTestUtils.writeBitsToFIFO(this, inputFIFO, data = Testcase.pre_preamble_rev, numBits = 6)
  expect(dataOut.valid, false.B) //note
  //println(s"after random_sequence\n${peek(c.io.pdaio.DMA_Data_o.bits)}\t0.U")

  // PREAMBLE
  PacketDisAssemblerTestUtils.writeBitsToFIFO(this, inputFIFO, data = Testcase.preamble_rev, numBits = 8)
  expect(dataOut.valid, false.B) //note
  println(s"after preamble\n${peek(dataOut.valid)}\tfalse")

  println(s"INFO: at cycle $t")

  // AA
  PacketDisAssemblerTestUtils.writeBitsToFIFOAndCheck(this, fifo = inputFIFO, writeData = Testcase.wholepacket_rad_rev,
    startBit = 0, endBit = 31,
    outputByteFifo = dataOut, checkData = Testcase.wholepacket_dig_rev)
  step(1) // need an extra cycle for DMA_Flag_AA_o to be valid
  expect(c.io.pdaio.ARM_Flag_AA_o.bits, false.B)
  expect(c.io.pdaio.ARM_Flag_AA_o.valid, true.B)
  println(s"j=flagAA\n${peek(c.io.pdaio.ARM_Flag_AA_o.bits)}\ttrue")
  println(s"j=flagAA\n${peek(c.io.pdaio.ARM_Flag_AA_o.valid)}\ttrue")

  // PDU_HEADER
  PacketDisAssemblerTestUtils.writeBitsToFIFOAndCheck(this, fifo = inputFIFO, writeData = Testcase.wholepacket_rad_rev,
    startBit = 32, endBit = 47,
    outputByteFifo = dataOut, checkData = Testcase.wholepacket_dig_rev)
  step(1) // need an extra cycle before DMA_Length_o is valid
  expect(c.io.pdaio.ARM_Length_o.bits, 16.U)
  expect(c.io.pdaio.ARM_Length_o.valid, true.B)
  println(s"j=DMA_Length_o\n${peek(c.io.pdaio.ARM_Length_o.bits)}\t16.U")
  step(1)
  poke(c.io.pdaio.ARM_Length_o.ready, false.B)

  // PDU_PAYLOAD
  PacketDisAssemblerTestUtils.writeBitsToFIFOAndCheck(this, fifo = inputFIFO, writeData = Testcase.wholepacket_rad_rev,
    startBit = 48, endBit = 22 * 8 - 1,
    outputByteFifo = dataOut, checkData = Testcase.wholepacket_dig_rev)

  // CRC
  PacketDisAssemblerTestUtils.writeBitsToFIFO(this, inputFIFO, data = Testcase.CRC_rad_rev, numBits = 24)

  // Wait until Flag_CRC is valid
  var steps = 0
  while (peek(c.io.pdaio.ARM_Flag_CRC_o.valid) == 0) {
    step(1)
    steps += 1
    assert(steps < 12)
  }
  expect(c.io.pdaio.ARM_Flag_CRC_o.bits, false.B)
  expect(c.io.pdaio.ARM_Flag_CRC_o.valid, true.B)
  println(s"j=flagCRC_bits\n${peek(c.io.pdaio.ARM_Flag_CRC_o.bits)}\tfalse")
  println(s"j=flagCRC_valid\n${peek(c.io.pdaio.ARM_Flag_CRC_o.valid)}\ttrue")
}

class PDATopTester extends ChiselFlatSpec {
  behavior of "PDATop"
  backends foreach { backend =>
    it should s"perform correct operation in ARM mode" in {
      Driver(() => new PDATopWrapper, "verilator") {
        (c) => new PDATopTest(c, armInput=true)
      } should be(true)
    }
    it should s"perform correct operation in recovered CDR mode" in {
      Driver(() => new PDATopWrapper, "verilator") {
        (c) => new PDATopTest(c, armInput=false)
      } should be(true)
    }
  }
}
