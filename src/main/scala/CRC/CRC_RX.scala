//CRC_RX
package utils

import chisel3._
import chisel3.util._



class Rx_Crc (input_length: Int = 8)extends Module { 
  val io = IO(new Bundle {
    val operand      = new DecoupledIO(UInt(8.W)).flip()  // input 1 byte each time
    val result       = new DecoupledIO(UInt(24.W))         // output 1 bit each time

    val seed         = Input(UInt(24.W))               // the module load the seed when init signal is set
  } )
