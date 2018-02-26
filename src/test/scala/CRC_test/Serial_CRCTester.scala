package CRC_test

import CRC._
import chisel3._
import chisel3.util._
import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}


class Serial_CRCTests(c: Serial_CRC) extends PeekPokeTester(c) {

   poke(c.io.init,true.B)
   poke(c.io.seed,"h00000f".U)
   poke(c.io.operand.valid,false.B)
   step(1)

   poke(c.io.init,false.B)
   step(1)


   // poke(c.io.operand.bits, "b1".U)
   // poke(c.io.operand.valid,true.B)
    step(1)

   expect(c.io.result.bits, "hf00000".U)
   expect(c.io.result.valid, true.B)

/*
   poke(c.io.operand.bits, "b0".U)
   poke(c.io.operand.valid,true.B)

   step(1)
   expect(c.io.result.bits, "b1"U)
   expect(c.io.result.valid, true.B)

   poke(c.io.operand.bits, "b0".U)
   poke(c.io.operand.valid,true.B)

   step(1)
   expect(c.io.result.bits, "b1"U)
   expect(c.io.result.valid, true.B)

   poke(c.io.operand.bits, "b0".U)
   poke(c.io.operand.valid,true.B)

   step(1)
   expect(c.io.result.bits, "b0"U)
   expect(c.io.result.valid, true.B)


   poke(c.io.operand.bits, "b0".U)
   poke(c.io.operand.valid,true.B)

   step(1)
   expect(c.io.result.bits, "b0"U)
   expect(c.io.result.valid, true.B)

   poke(c.io.operand.bits, "b1".U)
   poke(c.io.operand.valid,true.B)


   step(1)
   expect(c.io.result.bits, "b1"U)
   expect(c.io.result.valid, true.B)

   poke(c.io.operand.bits, "b0".U)
   poke(c.io.operand.valid,true.B)

   step(1)
   expect(c.io.result.bits, "b1"U)
   expect(c.io.result.valid, true.B)

   poke(c.io.operand.bits, "b1".U)
   poke(c.io.operand.valid,true.B)

   step(1)
   expect(c.io.result.bits, "b0"U)
   expect(c.io.result.valid, true.B)

   poke(c.io.operand.valid,false.B)

  step(1)
   expect(c.io.result.valid, false.B)
*/
   step(2)
}

class Serial_CRCTester extends ChiselFlatSpec {
   behavior of "Serial_CRC"
   backends foreach {backend =>
     it should s"perform correct operation as an Whitening $backend" in { 
       Driver(() => new Serial_CRC, "verilator") { 
         (c) => new Serial_CRCTests(c)} should be (true)
    }
  }
}

