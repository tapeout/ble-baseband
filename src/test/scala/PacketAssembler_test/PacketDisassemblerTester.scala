package PacketDisAssembler.test

import PacketDisAssembler._
import chisel3._
import chisel3.util._
import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}

class PacketDisAssemblerTest(c: PacketDisAssembler) extends PeekPokeTester(c) {

  val data_preamble = BigInt("55", 16)
  val data_AA = BigInt("6B7D9171", 16)
  val data_AA_rec = BigInt("8E89BED6", 16)
  val data_pduH = BigInt("F1BB", 16)
  val data_pduH_rec = BigInt("0F02", 16)
  val data_pduAA = BigInt("8984F0AB260D", 16)
  val data_pduAA_rec = BigInt("0002723280C6", 16)
  val data_pduData1 = BigInt("EE0C28B279", 16)
  val data_pduData1_rec = BigInt("0805050102", 16)
  val data_pduData2 = BigInt("A283CBA0", 16)
  val data_pduData2_rec = BigInt("43303932", 16)
  val data_crc = BigInt("748AAD", 16)
  val data_crc_rec = BigInt("65FAC7", 16)
  val pdu_length_1 = 40
  val pdu_length_2 = 32

  def sendBits(
      data_in: BigInt,
      expected_out: BigInt,
      length: Int
  ) = {

    for (j <- 0 to length - 1) {
      poke(c.io.in.bits.data, data_in.U(length - 1 - j))
      poke(c.io.in.valid, true.B)
      poke(c.io.out.ready, true.B)
      if (j % 8 == 7) {
        step(1)
        poke(c.io.in.valid, false.B)
        expect(c.io.out.bits.data, expected_out.U(j, j - 7))
        expect(c.io.out.valid, true.B)
        println(
          s"j=" + j + s"\n${peek(c.io.out.bits.data)}\t${peek(expected_out.U(j, j - 7))}"
        )
      } else {
        expect(c.io.out.valid, false.B)
      }
      step(1)
      poke(c.io.out.ready, false.B)
    }
    poke(c.io.in.valid, false.B)
    step(10)
  }

  //reset
  //reset(3)

  //throughout packet
  //poke(c.io.in.bits.crcSeed, "b010101010101010101010101".U)
  //poke(c.io.in.bits.whiteSeed, "b1100101".U)

  //initialize
  poke(c.io.in.bits.switch, false.B)
  poke(c.io.in.valid, false.B)
  poke(c.io.in.bits.data, 0.U)
  poke(c.io.out.ready, false.B)

  step(2)

  //trigger
  poke(c.io.in.bits.switch, true.B)
  poke(c.io.in.bits.data, 0.U)
  poke(c.io.in.valid, true.B)
  step(1)
  poke(c.io.in.bits.switch, false.B)

  //PREAMBLE
  for (j <- 0 to 7) {
    poke(c.io.out.ready, true.B)
    poke(c.io.in.bits.data, data_preamble.U(7 - j)) //note: U to B
    expect(c.io.out.valid, false.B)
    step(1)
    poke(c.io.out.ready, false.B)
  }
  poke(c.io.in.valid, false.B)
  step(10)

  //AA
  sendBits(data_AA, data_AA_rec, 32)
  expect(c.io.out.bits.flag_aa.bits, false.B)
  expect(c.io.out.bits.flag_aa.valid, true.B)
  //PDU_HEADER
  sendBits(data_pduH, data_pduH_rec, 16)

  //PDU_AA
  sendBits(data_pduAA, data_pduAA_rec, 48)

  //PDU Data 1
  sendBits(data_pduData1, data_pduData1_rec, pdu_length_1)

  //PDU Data 2
  sendBits(data_pduData2, data_pduData2_rec, pdu_length_2)

  //CRC
  sendBits(data_crc, data_crc_rec, 24)
  expect(c.io.out.bits.flag_crc.bits, false.B)
  expect(c.io.out.bits.flag_crc.valid, true.B)

  expect(c.io.out.bits.done, true.B)
  //todo: add FIFO
  //todo: add invalid DMA
  //todo: check output: DMA_ready

}

class PacketDisAssemblerTester extends ChiselFlatSpec {
  behavior of "PacketDisAssembler"
  backends foreach { backend =>
    it should s"perform correct operation as an packet disassembler $backend" in {
      Driver(() => new PacketDisAssembler, "verilator") { (c) =>
        new PacketDisAssemblerTest(c)
      } should be(true)
    }
  }
}
