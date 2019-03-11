package PacketAssembler.test

import PacketAssembler._
import SoftwareGoldenModel._
import chisel3._
import chisel3.util._
import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}

class PacketAssemblerTest(c: PacketAssembler) extends PeekPokeTester(c) {

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
    expect(c.io.out.bits.data, expected_out.U(j))
    expect(c.io.out.valid, true.B)
    //println(s"j="+j+s"\n${peek(c.io.out.bits.data)}\t${peek(expected_out.U(j))}")
    step(1)
    poke(c.io.out.ready, false.B)
    }
    step(10)
  }

for(i <- 0 to 100){   
  
  val packet = SoftwareGoldenModel.getRandomPackets()
  val sw_out = SoftwareGoldenModel.pa_sw(packet).map(x => Integer.parseInt(x,2))
  val packetInt = packet.map(x => Integer.parseInt(x,2))
  val len = packet.size

  //throughout packet
  poke(c.io.param.crcSeed, "b010101010101010101010101".U)
  poke(c.io.param.whiteSeed, "b1100101".U)

  //initialize
  poke(c.io.in.bits.trigger, false.B)
  poke(c.io.in.valid, false.B)
  poke(c.io.in.bits.data, 0.U)
  poke(c.io.out.ready, false.B)

  step(2)

  //trigger
  poke(c.io.in.bits.trigger, true.B)
  poke(c.io.in.valid, true.B)
  step(1)
  poke(c.io.in.bits.trigger, false.B)

  //Preamble
  sendBits(packetInt(0), sw_out(0), 8)

  //AA and PDU
  for(j <- 0 to len - 1){
     sendBits(packetInt(j), sw_out(j+1), 8)
  }

  //CRC
  for (j <- 0 to 23) {
    step(5)
    poke(c.io.out.ready, true.B)
    expect(c.io.out.bits.data, sw_out(len + 1 + j / 8).U(j % 8)) //note
    //println(s"j="+j+s"\n${peek(c.io.out.bits.data)}\t${peek(sw_out(len + 1 + j / 8).U(j % 8))}")
    step(1)
    poke(c.io.out.ready, false.B)
  } 

  //expect(c.io.out.bits.done, true.B)
}

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
