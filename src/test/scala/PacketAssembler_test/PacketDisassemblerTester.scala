package PacketDisAssembler.test

import PacketDisAssembler._
import chisel3._
import chisel3.util._
import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}

class PacketDisAssemblerTest(c: PacketDisAssembler) extends PeekPokeTester(c) {

  val data_preamble = "h55".U
  val data_AA = "h6B7D9171".U
  val data_AA_rec = "h8E89BED6".U
  val data_pduH = "hF1BB".U
  val data_pduH_rec = "h0F02".U
  val data_pduAA = "h8984F0AB260D".U
  val data_pduAA_rec = "h0002723280C6".U
  val data_pduData1 = "hEE0C28B279".U
  val data_pduData1_rec = "h0805050102".U
  val data_pduData2 = "hA283CBA0".U
  val data_pduData2_rec = "h43303932".U
  val data_crc = "h748AAD".U
  val data_crc_rec = "h65FAC7".U


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
    poke(c.io.in.bits.data, data_preamble(7-j)) //note: U to B
    expect(c.io.out.valid, false.B)
    step(1)
    poke(c.io.out.ready, false.B)
  }
  poke(c.io.in.valid, false.B)
  step(10)

  //AA
  for (j <- 0 to 31) {    
    poke(c.io.in.bits.data, data_AA(31-j))
    poke(c.io.in.valid, true.B)
    poke(c.io.out.ready, true.B)
    if(j % 8 == 7) {
        step(1)
        poke(c.io.in.valid, false.B)
        expect(c.io.out.bits.data, data_AA_rec(j, j-7))
        println(s"j="+j+s"\n${peek(c.io.out.bits.data)}\t${peek(data_AA_rec(j, j-7))}")  
    }
    step(1)
    poke(c.io.out.ready, false.B)
  }
  poke(c.io.in.valid, false.B)
  step(10)

  //PDU_HEADER
  for (j <- 0 to 15) {
    poke(c.io.in.bits.data, data_pduH(15-j))
    poke(c.io.in.valid, true.B)
    poke(c.io.out.ready, true.B)
    if(j % 8 == 7) {
        step(1)
        poke(c.io.in.valid, false.B)
        expect(c.io.out.bits.data, data_pduH_rec(j, j-7))
        println(s"j="+j+s"\n${peek(c.io.out.bits.data)}\t${peek(data_pduH_rec(j, j-7))}")
    }
    step(1)
    poke(c.io.out.ready, false.B)
  }
  poke(c.io.in.valid, false.B)
  step(10)

  //PDU_AA
  for (j <- 0 to 47) {
    poke(c.io.in.bits.data, data_pduAA(47-j))
    poke(c.io.in.valid, true.B)
    poke(c.io.out.ready, true.B)
    if(j % 8 == 7) {
        step(1)
        poke(c.io.in.valid, false.B)
        expect(c.io.out.bits.data, data_pduAA_rec(j, j-7))
        println(s"j="+j+s"\n${peek(c.io.out.bits.data)}\t${peek(data_pduAA_rec(j, j-7))}")
    }
    step(1)
    poke(c.io.out.ready, false.B)
  }
  poke(c.io.in.valid, false.B)
  step(10)

//PDU Data 1
  for (j <- 0 to 39) {
    poke(c.io.in.bits.data, data_pduData1(39-j))
    poke(c.io.in.valid, true.B)
    poke(c.io.out.ready, true.B)
    if(j % 8 == 7) {
        step(1)
        poke(c.io.in.valid, false.B)
        expect(c.io.out.bits.data, data_pduData1_rec(j, j-7))
        println(s"j="+j+s"\n${peek(c.io.out.bits.data)}\t${peek(data_pduData1_rec(j, j-7))}")
    }
    step(1)
    poke(c.io.out.ready, false.B)
  }
  poke(c.io.in.valid, false.B)
  step(10)

//PDU Data 2
  for (j <- 0 to 31) {
    poke(c.io.in.bits.data, data_pduData2(31-j))
    poke(c.io.in.valid, true.B)
    poke(c.io.out.ready, true.B)
    if(j % 8 == 7) {
        step(1)
        poke(c.io.in.valid, false.B)
        expect(c.io.out.bits.data, data_pduData2_rec(j, j-7))
        println(s"j="+j+s"\n${peek(c.io.out.bits.data)}\t${peek(data_pduData2_rec(j, j-7))}")
    }    
    step(1)
    poke(c.io.out.ready, false.B)
  }
  poke(c.io.in.valid, false.B)
  step(10)

  //CRC
  for (j <- 0 to 23) {
    poke(c.io.in.bits.data, data_crc(23-j))
    poke(c.io.in.valid, true.B)
    poke(c.io.out.ready, true.B)
    if(j % 8 == 7) {
        step(1)
        poke(c.io.in.valid, false.B)
        expect(c.io.out.bits.data, data_crc_rec(j, j-7))
        println(s"j="+j+s"\n${peek(c.io.out.bits.data)}\t${peek(data_crc_rec(j, j-7))}")
    }    
    step(1)
    poke(c.io.out.ready, false.B)
  }
  poke(c.io.in.valid, false.B)
  step(10)

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
