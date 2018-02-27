package Whitening // belongs to folder

import chisel3._
import chisel3.util._


// CRC module for data[0:0] ,   crc[6:0]=1+x^4+x^7;
/*
module crc(
  input [0:0] data_in,
  input crc_en,
  output [6:0] crc_out,
  input rst,
  input clk);
  reg [6:0] lfsr_q,lfsr_c;
  assign crc_out = lfsr_q;
  always @(*) begin
    lfsr_c[0] = lfsr_q[6] ^ data_in[0];
    lfsr_c[1] = lfsr_q[0];
    lfsr_c[2] = lfsr_q[1];
    lfsr_c[3] = lfsr_q[2];
    lfsr_c[4] = lfsr_q[3] ^ lfsr_q[6] ^ data_in[0];
    lfsr_c[5] = lfsr_q[4];
    lfsr_c[6] = lfsr_q[5];
  end // always
  always @(posedge clk, posedge rst) begin
    if(rst) begin
      lfsr_q <= {7{1'b1}};
    end
    else begin
      lfsr_q <= crc_en ? lfsr_c : lfsr_q;
    end
  end // always
endmodule // crc
*/

class Whitening extends Module {
    val io      = IO(new Bundle {
    val operand  = new DecoupledIO(UInt(1.W)).flip()
    val result   = new DecoupledIO(UInt(1.W))

    val seed       = Input(UInt(7.W))
    val init       = Input(Bool())        // to init the seed
  })

    val whitening_lfsr = Reg(init = 0.U(7.W)) // 1+x^4+x^7;
    val inv           = whitening_lfsr(0)

    // val output_bit_reg = Reg(init = 0.U(1.W))
    // val output_valid_reg = Reg(Bool())

    val output_bit = Wire(UInt(1.W))
    val output_valid = Wire(Bool())

    output_bit := 0.U
    output_valid := false.B
    io.operand.ready := true.B              // This module shouldn't need this signal
    when (io.init === true.B) { 
        whitening_lfsr := io.seed
        // output_bit_reg := 0.U
        // output_valid_reg := false.B
        output_bit := 0.U
        output_valid := false.B
        // io.operand.ready := true.B

    } .elsewhen (io.operand.valid === true.B) {
        whitening_lfsr := Cat(inv, whitening_lfsr(6), whitening_lfsr(5),whitening_lfsr(4),whitening_lfsr(3)^inv,whitening_lfsr(2),whitening_lfsr(1))
        // output_bit_reg := inv ^ io.operand.bits
        // output_valid_reg := true.B
        // io.operand.ready := false.B
        output_bit := inv ^ io.operand.bits
        output_valid := true.B
        
    } .otherwise {
        whitening_lfsr := whitening_lfsr
        // output_valid_reg := false.B
        // io.operand.ready := true.B
        output_valid := false.B
    }

    // io.result.valid := output_valid_reg
    // io.result.bits  := output_bit_reg
    io.result.valid := output_valid
    io.result.bits := output_bit
    
}