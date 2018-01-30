// Whitening TX
package utils

import chisel3._
import chisel3.util._


class Whitening (input_length: Int = 8, output_length: Int = 8)extends Module { 
  val io = IO(new Bundle {
    val operand      = new DecoupledIO(UInt(8.W)).flip()  // input 1 byte each time
    val result       = new DecoupledIO(UInt(1.W))         // output 1 bit each time

    val seed         = Input(UInt(7.W))               // the module load the seed when init signal is set
  } )