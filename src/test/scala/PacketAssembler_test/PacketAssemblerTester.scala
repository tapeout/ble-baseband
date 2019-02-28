package PacketAssembler.test

import PacketAssembler._
import chisel3._
import chisel3.util._
import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}

class PacketAssemblerTest(c: PacketAssembler) extends PeekPokeTester(c) {

  val preamble_rec = BigInt("AA", 16)
  val data_AA = BigInt("8E89BED6", 16)
  val data_AA_rec = BigInt("6B7D9171", 16)
  val data_pduH = BigInt("0F02", 16)
  val data_pduH_rec = BigInt("F1BB", 16)
  val data_pduAA = BigInt("0002723280C6", 16)
  val data_pduAA_rec = BigInt("8984F0AB260D", 16)
  val data_pduData1 = BigInt("0805050102", 16)
  val data_pduData1_rec = BigInt("EE0C28B279", 16)
  val data_pduData2 = BigInt("43303932", 16)
  val data_pduData2_rec = BigInt("A283CBA0", 16)
  val data_crc = BigInt("748AAD", 16)
  val pdu_length_1 = 40
  val pdu_length_2 = 32

  //reset
  //reset(3)

 def sendBits(
      data_in: BigInt,
      expected_out: BigInt,
      length: Int
  ) = {

  for (j <- 0 to length - 1) {
    if (j % 8 == 0) {
      poke(c.io.in.bits.data, data_in.U(j + 7, j))
      poke(c.io.in.valid, true.B)
    } else {
      poke(c.io.in.valid, false.B)
    }
    step(5)
    poke(c.io.out.ready, true.B)
    expect(c.io.out.bits.data, expected_out.U(length - 1 - j)) //note
    //println(s"j="+j+s"\n${peek(c.io.out.bits.data)}\t${peek(expected_out.U(length - 1 - j))}")
    step(1)
    poke(c.io.out.ready, false.B) //need to test two ready
    }
    step(10)
  } 


  //throughout packet
  poke(c.io.in.bits.crcSeed, "b010101010101010101010101".U)
  poke(c.io.in.bits.whiteSeed, "b1100101".U)

  //initialize
  poke(c.io.in.bits.trigger, false.B)
  poke(c.io.in.valid, false.B)
  poke(c.io.in.bits.data, 0.U)
  poke(c.io.out.ready, false.B)

  step(2)

  //trigger
  poke(c.io.in.bits.trigger, true.B)
  poke(c.io.in.valid, true.B)
  poke(c.io.in.bits.data, data_AA.U(7, 0))
  step(1)
  poke(c.io.in.bits.trigger, false.B)

  //PREAMBLE
  for (j <- 0 to 7) {
    //step(Random_Num(1,100))
    step(5)
    poke(c.io.out.ready, true.B)
    expect(c.io.out.bits.data, preamble_rec.U(j)) //note: U to B
    //println(s"${peek(c.io.out.bits.data)}")
    //println(s"${peek(preamble_rev(j))}")
    step(1)
    poke(c.io.out.ready, false.B) //need to test two ready
  }
  //step(Random_Num(8,100))
  step(10)
  //AA
  sendBits(data_AA, data_AA_rec, 32)

  //PDU_HEADER
  sendBits(data_pduH, data_pduH_rec, 16)

  //PDU_AA
  sendBits(data_pduAA, data_pduAA_rec, 48)

  //PDU Data 1
  sendBits(data_pduData1, data_pduData1_rec, pdu_length_1)

  //PDU Data 2
  sendBits(data_pduData2, data_pduData2_rec, pdu_length_2)

  //CRC
  for (j <- 0 to 23) {
    //step(Random_Num(1,100))
    step(5)
    poke(c.io.out.ready, true.B)
    expect(c.io.out.bits.data, data_crc.U(23 - j)) //note
    //println(s"j="+j+s"\n${peek(c.io.out.bits.data)}\t${peek(data_crc.U(23 - j))}")
    step(1)
    poke(c.io.out.ready, false.B) //need to test two ready consequently
  }
  
  //expect(c.io.out.bits.done, true.B)

}

class PacketAssemblerTester extends ChiselFlatSpec {
  behavior of "PacketAssembler"
  backends foreach { backend =>
    it should s"perform correct operation as an packet assembler $backend" in {
      Driver(() => new PacketAssembler, "verilator") { (c) =>
        new PacketAssemblerTest(c)
      } should be(true)
    }
  }
}
