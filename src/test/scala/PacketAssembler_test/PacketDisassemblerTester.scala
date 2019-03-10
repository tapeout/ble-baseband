package PacketDisAssembler.test

import PacketDisAssembler._
import SoftwareGoldenModel._
import chisel3._
import chisel3.util._
import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}

class PacketDisAssemblerTest(c: PacketDisAssembler) extends PeekPokeTester(c) {

  def sendBits(
      data_in: BigInt,
      expected_out: BigInt,
      length: Int
  ) = {

    for (j <- 0 to length - 1) {
      poke(c.io.in.bits.data, data_in.U(j))
      poke(c.io.in.valid, true.B)
      poke(c.io.out.ready, true.B)
      if (j % 8 == 7) {
        step(1)
        poke(c.io.in.valid, false.B)
        expect(c.io.out.bits.data, expected_out.U(j, j - 7))
        expect(c.io.out.valid, true.B)
        //println(s"j=" + j + s"\n${peek(c.io.out.bits.data)}\t${peek(expected_out.U(j, j - 7))}")
      } else {
        expect(c.io.out.valid, false.B)
      }
      step(1)
      poke(c.io.out.ready, false.B)
    }
    poke(c.io.in.valid, false.B)
    step(10)
  }

for (i <- 0 to 100){  

  val packet = SoftwareGoldenModel.getRandomPackets()
  val sw_out = SoftwareGoldenModel.pa_sw(packet).map(x => Integer.parseInt(x,2))
  val packetInt = packet.map(x => Integer.parseInt(x,2))
  val len = packet.size

  //throughout packet
  poke(c.io.param.crcSeed, "b010101010101010101010101".U)
  poke(c.io.param.whiteSeed, "b1100101".U)
  val aa = BigInt(packet(3) + packet(2) + packet(1) + packet(0), 2)
  poke(c.io.param.aa, aa.U)

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
  step(2)
  poke(c.io.in.bits.switch, false.B)

  //PREAMBLE
  for (j <- 0 to 7) {
    poke(c.io.out.ready, true.B)
    poke(c.io.in.bits.data, sw_out(0).U(j)) //note: U to B
    expect(c.io.out.valid, false.B)
    step(1)
    poke(c.io.out.ready, false.B)
  }
  poke(c.io.in.valid, false.B)
  step(10)

  //AA and PDU
  for(j <- 0 to len - 1){
    sendBits(sw_out(j + 1), packetInt(j), 8)
  }

  expect(c.io.out.bits.flag_aa.bits, true.B)
  expect(c.io.out.bits.flag_aa.valid, true.B)

  // CRC
  for (j <- 0 to 23) {
    poke(c.io.in.bits.data, sw_out(len + 1 + j / 8).U(j % 8)) 
    poke(c.io.in.valid, true.B)
    poke(c.io.out.ready, true.B)
    if (j % 8 == 7) {
      step(1)
      poke(c.io.in.valid, false.B)
      expect(c.io.out.valid, true.B)
    } else {
      expect(c.io.out.valid, false.B)
    }
    step(1)
    poke(c.io.out.ready, false.B)
  }
  poke(c.io.in.valid, false.B)
  step(10)

  expect(c.io.out.bits.flag_crc.bits, true.B)
  expect(c.io.out.bits.flag_crc.valid, true.B)
  expect(c.io.out.bits.done, true.B)

  poke(c.io.out.ready, true.B)
  step(10)
}
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
