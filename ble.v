`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif

module ble_crc( // @[:@3.2]
  input         clock, // @[:@4.4]
  input         reset, // @[:@5.4]
  input         io_operand_valid, // @[:@6.4]
  input         io_operand_bits, // @[:@6.4]
  output [23:0] io_result_bits, // @[:@6.4]
  input  [23:0] io_seed, // @[:@6.4]
  input         io_init // @[:@6.4]
);
  reg [23:0] lfsr; // @[Serial_CRC.scala 73:19:@8.4]
  reg [31:0] _RAND_0;
  wire  _T_18; // @[Serial_CRC.scala 74:20:@9.4]
  wire  inv; // @[Serial_CRC.scala 74:24:@10.4]
  wire [15:0] _T_22; // @[Bitwise.scala 109:18:@14.6]
  wire [7:0] _T_27; // @[Bitwise.scala 103:21:@17.6]
  wire [15:0] _T_28; // @[Bitwise.scala 103:31:@18.6]
  wire [7:0] _T_29; // @[Bitwise.scala 103:46:@19.6]
  wire [15:0] _GEN_4; // @[Bitwise.scala 103:65:@20.6]
  wire [15:0] _T_30; // @[Bitwise.scala 103:65:@20.6]
  wire [15:0] _T_32; // @[Bitwise.scala 103:75:@22.6]
  wire [15:0] _T_33; // @[Bitwise.scala 103:39:@23.6]
  wire [11:0] _T_37; // @[Bitwise.scala 103:21:@27.6]
  wire [15:0] _GEN_5; // @[Bitwise.scala 103:31:@28.6]
  wire [15:0] _T_38; // @[Bitwise.scala 103:31:@28.6]
  wire [11:0] _T_39; // @[Bitwise.scala 103:46:@29.6]
  wire [15:0] _GEN_6; // @[Bitwise.scala 103:65:@30.6]
  wire [15:0] _T_40; // @[Bitwise.scala 103:65:@30.6]
  wire [15:0] _T_42; // @[Bitwise.scala 103:75:@32.6]
  wire [15:0] _T_43; // @[Bitwise.scala 103:39:@33.6]
  wire [13:0] _T_47; // @[Bitwise.scala 103:21:@37.6]
  wire [15:0] _GEN_7; // @[Bitwise.scala 103:31:@38.6]
  wire [15:0] _T_48; // @[Bitwise.scala 103:31:@38.6]
  wire [13:0] _T_49; // @[Bitwise.scala 103:46:@39.6]
  wire [15:0] _GEN_8; // @[Bitwise.scala 103:65:@40.6]
  wire [15:0] _T_50; // @[Bitwise.scala 103:65:@40.6]
  wire [15:0] _T_52; // @[Bitwise.scala 103:75:@42.6]
  wire [15:0] _T_53; // @[Bitwise.scala 103:39:@43.6]
  wire [14:0] _T_57; // @[Bitwise.scala 103:21:@47.6]
  wire [15:0] _GEN_9; // @[Bitwise.scala 103:31:@48.6]
  wire [15:0] _T_58; // @[Bitwise.scala 103:31:@48.6]
  wire [14:0] _T_59; // @[Bitwise.scala 103:46:@49.6]
  wire [15:0] _GEN_10; // @[Bitwise.scala 103:65:@50.6]
  wire [15:0] _T_60; // @[Bitwise.scala 103:65:@50.6]
  wire [15:0] _T_62; // @[Bitwise.scala 103:75:@52.6]
  wire [15:0] _T_63; // @[Bitwise.scala 103:39:@53.6]
  wire [7:0] _T_64; // @[Bitwise.scala 109:44:@54.6]
  wire [3:0] _T_69; // @[Bitwise.scala 103:21:@57.6]
  wire [7:0] _T_70; // @[Bitwise.scala 103:31:@58.6]
  wire [3:0] _T_71; // @[Bitwise.scala 103:46:@59.6]
  wire [7:0] _GEN_11; // @[Bitwise.scala 103:65:@60.6]
  wire [7:0] _T_72; // @[Bitwise.scala 103:65:@60.6]
  wire [7:0] _T_74; // @[Bitwise.scala 103:75:@62.6]
  wire [7:0] _T_75; // @[Bitwise.scala 103:39:@63.6]
  wire [5:0] _T_79; // @[Bitwise.scala 103:21:@67.6]
  wire [7:0] _GEN_12; // @[Bitwise.scala 103:31:@68.6]
  wire [7:0] _T_80; // @[Bitwise.scala 103:31:@68.6]
  wire [5:0] _T_81; // @[Bitwise.scala 103:46:@69.6]
  wire [7:0] _GEN_13; // @[Bitwise.scala 103:65:@70.6]
  wire [7:0] _T_82; // @[Bitwise.scala 103:65:@70.6]
  wire [7:0] _T_84; // @[Bitwise.scala 103:75:@72.6]
  wire [7:0] _T_85; // @[Bitwise.scala 103:39:@73.6]
  wire [6:0] _T_89; // @[Bitwise.scala 103:21:@77.6]
  wire [7:0] _GEN_14; // @[Bitwise.scala 103:31:@78.6]
  wire [7:0] _T_90; // @[Bitwise.scala 103:31:@78.6]
  wire [6:0] _T_91; // @[Bitwise.scala 103:46:@79.6]
  wire [7:0] _GEN_15; // @[Bitwise.scala 103:65:@80.6]
  wire [7:0] _T_92; // @[Bitwise.scala 103:65:@80.6]
  wire [7:0] _T_94; // @[Bitwise.scala 103:75:@82.6]
  wire [7:0] _T_95; // @[Bitwise.scala 103:39:@83.6]
  wire [23:0] _T_96; // @[Cat.scala 30:58:@84.6]
  wire  _T_101; // @[Serial_CRC.scala 95:28:@92.8]
  wire  _T_102; // @[Serial_CRC.scala 95:32:@93.8]
  wire  _T_103; // @[Serial_CRC.scala 95:42:@94.8]
  wire  _T_104; // @[Serial_CRC.scala 95:51:@95.8]
  wire  _T_105; // @[Serial_CRC.scala 95:55:@96.8]
  wire  _T_106; // @[Serial_CRC.scala 95:64:@97.8]
  wire  _T_107; // @[Serial_CRC.scala 95:68:@98.8]
  wire  _T_108; // @[Serial_CRC.scala 95:77:@99.8]
  wire  _T_109; // @[Serial_CRC.scala 95:86:@100.8]
  wire  _T_110; // @[Serial_CRC.scala 95:90:@101.8]
  wire  _T_111; // @[Serial_CRC.scala 95:100:@102.8]
  wire  _T_112; // @[Serial_CRC.scala 96:13:@103.8]
  wire  _T_113; // @[Serial_CRC.scala 96:22:@104.8]
  wire  _T_114; // @[Serial_CRC.scala 96:26:@105.8]
  wire  _T_115; // @[Serial_CRC.scala 96:35:@106.8]
  wire  _T_116; // @[Serial_CRC.scala 96:39:@107.8]
  wire  _T_117; // @[Serial_CRC.scala 96:48:@108.8]
  wire  _T_118; // @[Serial_CRC.scala 96:57:@109.8]
  wire  _T_119; // @[Serial_CRC.scala 96:66:@110.8]
  wire  _T_120; // @[Serial_CRC.scala 96:75:@111.8]
  wire  _T_121; // @[Serial_CRC.scala 96:84:@112.8]
  wire  _T_122; // @[Serial_CRC.scala 96:92:@113.8]
  wire  _T_123; // @[Serial_CRC.scala 97:13:@114.8]
  wire  _T_124; // @[Serial_CRC.scala 97:22:@115.8]
  wire  _T_125; // @[Serial_CRC.scala 97:30:@116.8]
  wire  _T_126; // @[Serial_CRC.scala 97:38:@117.8]
  wire  _T_127; // @[Serial_CRC.scala 97:46:@118.8]
  wire  _T_128; // @[Serial_CRC.scala 97:54:@119.8]
  wire  _T_129; // @[Serial_CRC.scala 97:62:@120.8]
  wire [1:0] _T_130; // @[Cat.scala 30:58:@121.8]
  wire [2:0] _T_131; // @[Cat.scala 30:58:@122.8]
  wire [1:0] _T_132; // @[Cat.scala 30:58:@123.8]
  wire [2:0] _T_133; // @[Cat.scala 30:58:@124.8]
  wire [5:0] _T_134; // @[Cat.scala 30:58:@125.8]
  wire [1:0] _T_135; // @[Cat.scala 30:58:@126.8]
  wire [2:0] _T_136; // @[Cat.scala 30:58:@127.8]
  wire [1:0] _T_137; // @[Cat.scala 30:58:@128.8]
  wire [2:0] _T_138; // @[Cat.scala 30:58:@129.8]
  wire [5:0] _T_139; // @[Cat.scala 30:58:@130.8]
  wire [11:0] _T_140; // @[Cat.scala 30:58:@131.8]
  wire [1:0] _T_141; // @[Cat.scala 30:58:@132.8]
  wire [2:0] _T_142; // @[Cat.scala 30:58:@133.8]
  wire [1:0] _T_143; // @[Cat.scala 30:58:@134.8]
  wire [2:0] _T_144; // @[Cat.scala 30:58:@135.8]
  wire [5:0] _T_145; // @[Cat.scala 30:58:@136.8]
  wire [1:0] _T_146; // @[Cat.scala 30:58:@137.8]
  wire [2:0] _T_147; // @[Cat.scala 30:58:@138.8]
  wire [1:0] _T_148; // @[Cat.scala 30:58:@139.8]
  wire [2:0] _T_149; // @[Cat.scala 30:58:@140.8]
  wire [5:0] _T_150; // @[Cat.scala 30:58:@141.8]
  wire [11:0] _T_151; // @[Cat.scala 30:58:@142.8]
  wire [23:0] _T_152; // @[Cat.scala 30:58:@143.8]
  wire [23:0] _GEN_0; // @[Serial_CRC.scala 91:47:@91.6]
  wire [23:0] _GEN_2; // @[Serial_CRC.scala 85:31:@13.4]
  assign _T_18 = lfsr[0]; // @[Serial_CRC.scala 74:20:@9.4]
  assign inv = _T_18 ^ io_operand_bits; // @[Serial_CRC.scala 74:24:@10.4]
  assign _T_22 = io_seed[15:0]; // @[Bitwise.scala 109:18:@14.6]
  assign _T_27 = _T_22[15:8]; // @[Bitwise.scala 103:21:@17.6]
  assign _T_28 = {{8'd0}, _T_27}; // @[Bitwise.scala 103:31:@18.6]
  assign _T_29 = _T_22[7:0]; // @[Bitwise.scala 103:46:@19.6]
  assign _GEN_4 = {{8'd0}, _T_29}; // @[Bitwise.scala 103:65:@20.6]
  assign _T_30 = _GEN_4 << 8; // @[Bitwise.scala 103:65:@20.6]
  assign _T_32 = _T_30 & 16'hff00; // @[Bitwise.scala 103:75:@22.6]
  assign _T_33 = _T_28 | _T_32; // @[Bitwise.scala 103:39:@23.6]
  assign _T_37 = _T_33[15:4]; // @[Bitwise.scala 103:21:@27.6]
  assign _GEN_5 = {{4'd0}, _T_37}; // @[Bitwise.scala 103:31:@28.6]
  assign _T_38 = _GEN_5 & 16'hf0f; // @[Bitwise.scala 103:31:@28.6]
  assign _T_39 = _T_33[11:0]; // @[Bitwise.scala 103:46:@29.6]
  assign _GEN_6 = {{4'd0}, _T_39}; // @[Bitwise.scala 103:65:@30.6]
  assign _T_40 = _GEN_6 << 4; // @[Bitwise.scala 103:65:@30.6]
  assign _T_42 = _T_40 & 16'hf0f0; // @[Bitwise.scala 103:75:@32.6]
  assign _T_43 = _T_38 | _T_42; // @[Bitwise.scala 103:39:@33.6]
  assign _T_47 = _T_43[15:2]; // @[Bitwise.scala 103:21:@37.6]
  assign _GEN_7 = {{2'd0}, _T_47}; // @[Bitwise.scala 103:31:@38.6]
  assign _T_48 = _GEN_7 & 16'h3333; // @[Bitwise.scala 103:31:@38.6]
  assign _T_49 = _T_43[13:0]; // @[Bitwise.scala 103:46:@39.6]
  assign _GEN_8 = {{2'd0}, _T_49}; // @[Bitwise.scala 103:65:@40.6]
  assign _T_50 = _GEN_8 << 2; // @[Bitwise.scala 103:65:@40.6]
  assign _T_52 = _T_50 & 16'hcccc; // @[Bitwise.scala 103:75:@42.6]
  assign _T_53 = _T_48 | _T_52; // @[Bitwise.scala 103:39:@43.6]
  assign _T_57 = _T_53[15:1]; // @[Bitwise.scala 103:21:@47.6]
  assign _GEN_9 = {{1'd0}, _T_57}; // @[Bitwise.scala 103:31:@48.6]
  assign _T_58 = _GEN_9 & 16'h5555; // @[Bitwise.scala 103:31:@48.6]
  assign _T_59 = _T_53[14:0]; // @[Bitwise.scala 103:46:@49.6]
  assign _GEN_10 = {{1'd0}, _T_59}; // @[Bitwise.scala 103:65:@50.6]
  assign _T_60 = _GEN_10 << 1; // @[Bitwise.scala 103:65:@50.6]
  assign _T_62 = _T_60 & 16'haaaa; // @[Bitwise.scala 103:75:@52.6]
  assign _T_63 = _T_58 | _T_62; // @[Bitwise.scala 103:39:@53.6]
  assign _T_64 = io_seed[23:16]; // @[Bitwise.scala 109:44:@54.6]
  assign _T_69 = _T_64[7:4]; // @[Bitwise.scala 103:21:@57.6]
  assign _T_70 = {{4'd0}, _T_69}; // @[Bitwise.scala 103:31:@58.6]
  assign _T_71 = _T_64[3:0]; // @[Bitwise.scala 103:46:@59.6]
  assign _GEN_11 = {{4'd0}, _T_71}; // @[Bitwise.scala 103:65:@60.6]
  assign _T_72 = _GEN_11 << 4; // @[Bitwise.scala 103:65:@60.6]
  assign _T_74 = _T_72 & 8'hf0; // @[Bitwise.scala 103:75:@62.6]
  assign _T_75 = _T_70 | _T_74; // @[Bitwise.scala 103:39:@63.6]
  assign _T_79 = _T_75[7:2]; // @[Bitwise.scala 103:21:@67.6]
  assign _GEN_12 = {{2'd0}, _T_79}; // @[Bitwise.scala 103:31:@68.6]
  assign _T_80 = _GEN_12 & 8'h33; // @[Bitwise.scala 103:31:@68.6]
  assign _T_81 = _T_75[5:0]; // @[Bitwise.scala 103:46:@69.6]
  assign _GEN_13 = {{2'd0}, _T_81}; // @[Bitwise.scala 103:65:@70.6]
  assign _T_82 = _GEN_13 << 2; // @[Bitwise.scala 103:65:@70.6]
  assign _T_84 = _T_82 & 8'hcc; // @[Bitwise.scala 103:75:@72.6]
  assign _T_85 = _T_80 | _T_84; // @[Bitwise.scala 103:39:@73.6]
  assign _T_89 = _T_85[7:1]; // @[Bitwise.scala 103:21:@77.6]
  assign _GEN_14 = {{1'd0}, _T_89}; // @[Bitwise.scala 103:31:@78.6]
  assign _T_90 = _GEN_14 & 8'h55; // @[Bitwise.scala 103:31:@78.6]
  assign _T_91 = _T_85[6:0]; // @[Bitwise.scala 103:46:@79.6]
  assign _GEN_15 = {{1'd0}, _T_91}; // @[Bitwise.scala 103:65:@80.6]
  assign _T_92 = _GEN_15 << 1; // @[Bitwise.scala 103:65:@80.6]
  assign _T_94 = _T_92 & 8'haa; // @[Bitwise.scala 103:75:@82.6]
  assign _T_95 = _T_90 | _T_94; // @[Bitwise.scala 103:39:@83.6]
  assign _T_96 = {_T_63,_T_95}; // @[Cat.scala 30:58:@84.6]
  assign _T_101 = lfsr[23]; // @[Serial_CRC.scala 95:28:@92.8]
  assign _T_102 = _T_101 ^ inv; // @[Serial_CRC.scala 95:32:@93.8]
  assign _T_103 = lfsr[22]; // @[Serial_CRC.scala 95:42:@94.8]
  assign _T_104 = lfsr[21]; // @[Serial_CRC.scala 95:51:@95.8]
  assign _T_105 = _T_104 ^ inv; // @[Serial_CRC.scala 95:55:@96.8]
  assign _T_106 = lfsr[20]; // @[Serial_CRC.scala 95:64:@97.8]
  assign _T_107 = _T_106 ^ inv; // @[Serial_CRC.scala 95:68:@98.8]
  assign _T_108 = lfsr[19]; // @[Serial_CRC.scala 95:77:@99.8]
  assign _T_109 = lfsr[18]; // @[Serial_CRC.scala 95:86:@100.8]
  assign _T_110 = _T_109 ^ inv; // @[Serial_CRC.scala 95:90:@101.8]
  assign _T_111 = lfsr[17]; // @[Serial_CRC.scala 95:100:@102.8]
  assign _T_112 = lfsr[16]; // @[Serial_CRC.scala 96:13:@103.8]
  assign _T_113 = lfsr[15]; // @[Serial_CRC.scala 96:22:@104.8]
  assign _T_114 = _T_113 ^ inv; // @[Serial_CRC.scala 96:26:@105.8]
  assign _T_115 = lfsr[14]; // @[Serial_CRC.scala 96:35:@106.8]
  assign _T_116 = _T_115 ^ inv; // @[Serial_CRC.scala 96:39:@107.8]
  assign _T_117 = lfsr[13]; // @[Serial_CRC.scala 96:48:@108.8]
  assign _T_118 = lfsr[12]; // @[Serial_CRC.scala 96:57:@109.8]
  assign _T_119 = lfsr[11]; // @[Serial_CRC.scala 96:66:@110.8]
  assign _T_120 = lfsr[10]; // @[Serial_CRC.scala 96:75:@111.8]
  assign _T_121 = lfsr[9]; // @[Serial_CRC.scala 96:84:@112.8]
  assign _T_122 = lfsr[8]; // @[Serial_CRC.scala 96:92:@113.8]
  assign _T_123 = lfsr[7]; // @[Serial_CRC.scala 97:13:@114.8]
  assign _T_124 = lfsr[6]; // @[Serial_CRC.scala 97:22:@115.8]
  assign _T_125 = lfsr[5]; // @[Serial_CRC.scala 97:30:@116.8]
  assign _T_126 = lfsr[4]; // @[Serial_CRC.scala 97:38:@117.8]
  assign _T_127 = lfsr[3]; // @[Serial_CRC.scala 97:46:@118.8]
  assign _T_128 = lfsr[2]; // @[Serial_CRC.scala 97:54:@119.8]
  assign _T_129 = lfsr[1]; // @[Serial_CRC.scala 97:62:@120.8]
  assign _T_130 = {_T_127,_T_128}; // @[Cat.scala 30:58:@121.8]
  assign _T_131 = {_T_130,_T_129}; // @[Cat.scala 30:58:@122.8]
  assign _T_132 = {_T_124,_T_125}; // @[Cat.scala 30:58:@123.8]
  assign _T_133 = {_T_132,_T_126}; // @[Cat.scala 30:58:@124.8]
  assign _T_134 = {_T_133,_T_131}; // @[Cat.scala 30:58:@125.8]
  assign _T_135 = {_T_121,_T_122}; // @[Cat.scala 30:58:@126.8]
  assign _T_136 = {_T_135,_T_123}; // @[Cat.scala 30:58:@127.8]
  assign _T_137 = {_T_118,_T_119}; // @[Cat.scala 30:58:@128.8]
  assign _T_138 = {_T_137,_T_120}; // @[Cat.scala 30:58:@129.8]
  assign _T_139 = {_T_138,_T_136}; // @[Cat.scala 30:58:@130.8]
  assign _T_140 = {_T_139,_T_134}; // @[Cat.scala 30:58:@131.8]
  assign _T_141 = {_T_114,_T_116}; // @[Cat.scala 30:58:@132.8]
  assign _T_142 = {_T_141,_T_117}; // @[Cat.scala 30:58:@133.8]
  assign _T_143 = {_T_110,_T_111}; // @[Cat.scala 30:58:@134.8]
  assign _T_144 = {_T_143,_T_112}; // @[Cat.scala 30:58:@135.8]
  assign _T_145 = {_T_144,_T_142}; // @[Cat.scala 30:58:@136.8]
  assign _T_146 = {_T_105,_T_107}; // @[Cat.scala 30:58:@137.8]
  assign _T_147 = {_T_146,_T_108}; // @[Cat.scala 30:58:@138.8]
  assign _T_148 = {inv,_T_102}; // @[Cat.scala 30:58:@139.8]
  assign _T_149 = {_T_148,_T_103}; // @[Cat.scala 30:58:@140.8]
  assign _T_150 = {_T_149,_T_147}; // @[Cat.scala 30:58:@141.8]
  assign _T_151 = {_T_150,_T_145}; // @[Cat.scala 30:58:@142.8]
  assign _T_152 = {_T_151,_T_140}; // @[Cat.scala 30:58:@143.8]
  assign _GEN_0 = io_operand_valid ? _T_152 : lfsr; // @[Serial_CRC.scala 91:47:@91.6]
  assign _GEN_2 = io_init ? _T_96 : _GEN_0; // @[Serial_CRC.scala 85:31:@13.4]
  assign io_result_bits = lfsr;
`ifdef RANDOMIZE
  integer initvar;
  initial begin
    `ifndef verilator
      #0.002 begin end
    `endif
  `ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{$random}};
  lfsr = _RAND_0[23:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge clock) begin
    if (reset) begin
      lfsr <= 24'h0;
    end else begin
      if (io_init) begin
        lfsr <= _T_96;
      end else begin
        if (io_operand_valid) begin
          lfsr <= _T_152;
        end
      end
    end
  end
endmodule
module ble_whitening( // @[:@156.2]
  input        clock, // @[:@157.4]
  input        reset, // @[:@158.4]
  input        io_operand_valid, // @[:@159.4]
  input        io_operand_bits, // @[:@159.4]
  output       io_result_bits, // @[:@159.4]
  input  [6:0] io_seed, // @[:@159.4]
  input        io_init // @[:@159.4]
);
  reg [6:0] whitening_lfsr; // @[whitening.scala 47:29:@161.4]
  reg [31:0] _RAND_0;
  wire  inv; // @[whitening.scala 48:39:@162.4]
  wire  _T_29; // @[whitening.scala 68:50:@177.8]
  wire  _T_30; // @[whitening.scala 68:69:@178.8]
  wire  _T_31; // @[whitening.scala 68:87:@179.8]
  wire  _T_32; // @[whitening.scala 68:105:@180.8]
  wire  _T_33; // @[whitening.scala 68:108:@181.8]
  wire  _T_34; // @[whitening.scala 68:127:@182.8]
  wire  _T_35; // @[whitening.scala 68:145:@183.8]
  wire [1:0] _T_36; // @[Cat.scala 30:58:@184.8]
  wire [2:0] _T_37; // @[Cat.scala 30:58:@185.8]
  wire [1:0] _T_38; // @[Cat.scala 30:58:@186.8]
  wire [1:0] _T_39; // @[Cat.scala 30:58:@187.8]
  wire [3:0] _T_40; // @[Cat.scala 30:58:@188.8]
  wire [6:0] _T_41; // @[Cat.scala 30:58:@189.8]
  wire  _T_42; // @[whitening.scala 72:27:@191.8]
  wire [6:0] _GEN_0; // @[whitening.scala 67:47:@176.6]
  wire  _GEN_1; // @[whitening.scala 67:47:@176.6]
  wire [6:0] _GEN_3; // @[whitening.scala 59:31:@169.4]
  wire  output_bit; // @[whitening.scala 59:31:@169.4]
  assign inv = whitening_lfsr[0]; // @[whitening.scala 48:39:@162.4]
  assign _T_29 = whitening_lfsr[6]; // @[whitening.scala 68:50:@177.8]
  assign _T_30 = whitening_lfsr[5]; // @[whitening.scala 68:69:@178.8]
  assign _T_31 = whitening_lfsr[4]; // @[whitening.scala 68:87:@179.8]
  assign _T_32 = whitening_lfsr[3]; // @[whitening.scala 68:105:@180.8]
  assign _T_33 = _T_32 ^ inv; // @[whitening.scala 68:108:@181.8]
  assign _T_34 = whitening_lfsr[2]; // @[whitening.scala 68:127:@182.8]
  assign _T_35 = whitening_lfsr[1]; // @[whitening.scala 68:145:@183.8]
  assign _T_36 = {_T_33,_T_34}; // @[Cat.scala 30:58:@184.8]
  assign _T_37 = {_T_36,_T_35}; // @[Cat.scala 30:58:@185.8]
  assign _T_38 = {_T_30,_T_31}; // @[Cat.scala 30:58:@186.8]
  assign _T_39 = {inv,_T_29}; // @[Cat.scala 30:58:@187.8]
  assign _T_40 = {_T_39,_T_38}; // @[Cat.scala 30:58:@188.8]
  assign _T_41 = {_T_40,_T_37}; // @[Cat.scala 30:58:@189.8]
  assign _T_42 = inv ^ io_operand_bits; // @[whitening.scala 72:27:@191.8]
  assign _GEN_0 = io_operand_valid ? _T_41 : whitening_lfsr; // @[whitening.scala 67:47:@176.6]
  assign _GEN_1 = io_operand_valid ? _T_42 : 1'h0; // @[whitening.scala 67:47:@176.6]
  assign _GEN_3 = io_init ? io_seed : _GEN_0; // @[whitening.scala 59:31:@169.4]
  assign output_bit = io_init ? 1'h0 : _GEN_1; // @[whitening.scala 59:31:@169.4]
  assign io_result_bits = output_bit;
`ifdef RANDOMIZE
  integer initvar;
  initial begin
    `ifndef verilator
      #0.002 begin end
    `endif
  `ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{$random}};
  whitening_lfsr = _RAND_0[6:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge clock) begin
    if (reset) begin
      whitening_lfsr <= 7'h0;
    end else begin
      if (io_init) begin
        whitening_lfsr <= io_seed;
      end else begin
        if (io_operand_valid) begin
          whitening_lfsr <= _T_41;
        end
      end
    end
  end
endmodule
module ble_packetassembler( // @[:@202.2]
  input         clock, // @[:@203.4]
  input         reset, // @[:@204.4]
  input         io_DMA_Trigger_i, // @[:@205.4]
  output        io_DMA_Data_i_ready, // @[:@205.4]
  input         io_DMA_Data_i_valid, // @[:@205.4]
  input  [7:0]  io_DMA_Data_i_bits, // @[:@205.4]
  input  [23:0] io_REG_CRC_Seed_i, // @[:@205.4]
  input  [6:0]  io_REG_White_Seed_i, // @[:@205.4]
  output        io_DMA_Done_o, // @[:@205.4]
  input         io_AFIFO_Data_o_ready, // @[:@205.4]
  output        io_AFIFO_Data_o_valid, // @[:@205.4]
  output        io_AFIFO_Data_o_bits // @[:@205.4]
);
  reg [2:0] state_r; // @[assembler.scala 47:30:@220.4]
  reg [31:0] _RAND_0;
  reg [7:0] counter_r; // @[assembler.scala 50:32:@222.4]
  reg [31:0] _RAND_1;
  reg [2:0] counter_byte_r; // @[assembler.scala 53:37:@224.4]
  reg [31:0] _RAND_2;
  reg [7:0] PDU_Length_r; // @[assembler.scala 55:35:@225.4]
  reg [31:0] _RAND_3;
  reg  DMA_Data_Ready_r; // @[assembler.scala 62:39:@228.4]
  reg [31:0] _RAND_4;
  reg  AFIFO_Valid_r; // @[assembler.scala 66:36:@230.4]
  reg [31:0] _RAND_5;
  reg [7:0] data_r; // @[assembler.scala 71:29:@233.4]
  reg [31:0] _RAND_6;
  wire  CRC_inst_clock; // @[assembler.scala 349:30:@663.4]
  wire  CRC_inst_reset; // @[assembler.scala 349:30:@663.4]
  wire  CRC_inst_io_operand_valid; // @[assembler.scala 349:30:@663.4]
  wire  CRC_inst_io_operand_bits; // @[assembler.scala 349:30:@663.4]
  wire [23:0] CRC_inst_io_result_bits; // @[assembler.scala 349:30:@663.4]
  wire [23:0] CRC_inst_io_seed; // @[assembler.scala 349:30:@663.4]
  wire  CRC_inst_io_init; // @[assembler.scala 349:30:@663.4]
  wire  WHITE_inst_clock; // @[assembler.scala 359:32:@672.4]
  wire  WHITE_inst_reset; // @[assembler.scala 359:32:@672.4]
  wire  WHITE_inst_io_operand_valid; // @[assembler.scala 359:32:@672.4]
  wire  WHITE_inst_io_operand_bits; // @[assembler.scala 359:32:@672.4]
  wire  WHITE_inst_io_result_bits; // @[assembler.scala 359:32:@672.4]
  wire [6:0] WHITE_inst_io_seed; // @[assembler.scala 359:32:@672.4]
  wire  WHITE_inst_io_init; // @[assembler.scala 359:32:@672.4]
  wire  DMA_Data_Fire_w; // @[assembler.scala 89:48:@245.4]
  wire  AFIFO_Fire_w; // @[assembler.scala 91:47:@248.4]
  wire  _T_65; // @[assembler.scala 110:22:@252.4]
  wire  _T_67; // @[assembler.scala 113:30:@257.6]
  wire  _T_68; // @[assembler.scala 113:56:@258.6]
  wire  _T_69; // @[assembler.scala 113:45:@259.6]
  wire  _T_70; // @[assembler.scala 113:83:@260.6]
  wire  _T_71; // @[assembler.scala 113:72:@261.6]
  wire [7:0] _T_72; // @[assembler.scala 116:55:@266.8]
  wire  _T_73; // @[assembler.scala 116:55:@267.8]
  wire  WHITE_Result_w; // @[assembler.scala 84:34:@242.4]
  wire  _GEN_0; // @[assembler.scala 113:91:@262.6]
  wire  _GEN_1; // @[assembler.scala 110:31:@253.4]
  wire  _T_76; // @[assembler.scala 120:43:@272.4]
  wire  _T_77; // @[assembler.scala 120:30:@273.4]
  wire  _T_79; // @[assembler.scala 120:69:@274.4]
  wire  _T_80; // @[assembler.scala 120:51:@275.4]
  wire  _T_83; // @[assembler.scala 120:77:@277.4]
  wire [2:0] _GEN_3; // @[assembler.scala 137:50:@294.6]
  wire [7:0] _GEN_4; // @[assembler.scala 137:50:@294.6]
  wire [2:0] _GEN_5; // @[assembler.scala 137:50:@294.6]
  wire  _T_91; // @[assembler.scala 144:28:@304.6]
  wire  _T_93; // @[assembler.scala 145:32:@306.8]
  wire  _T_96; // @[assembler.scala 145:40:@308.8]
  wire  _T_99; // @[assembler.scala 145:66:@310.8]
  wire [8:0] _T_107; // @[assembler.scala 153:63:@322.14]
  wire [7:0] _T_108; // @[assembler.scala 153:63:@323.14]
  wire [3:0] _T_111; // @[assembler.scala 156:73:@328.14]
  wire [2:0] _T_112; // @[assembler.scala 156:73:@329.14]
  wire [7:0] _GEN_6; // @[assembler.scala 152:61:@321.12]
  wire [2:0] _GEN_7; // @[assembler.scala 152:61:@321.12]
  wire [7:0] _GEN_8; // @[assembler.scala 151:54:@319.10]
  wire [2:0] _GEN_9; // @[assembler.scala 151:54:@319.10]
  wire [2:0] _GEN_10; // @[assembler.scala 145:93:@311.8]
  wire [7:0] _GEN_11; // @[assembler.scala 145:93:@311.8]
  wire [2:0] _GEN_12; // @[assembler.scala 145:93:@311.8]
  wire  _T_113; // @[assembler.scala 160:28:@336.8]
  wire  _T_115; // @[assembler.scala 161:32:@338.10]
  wire  _T_118; // @[assembler.scala 161:40:@340.10]
  wire  _T_121; // @[assembler.scala 161:66:@342.10]
  wire [2:0] _GEN_17; // @[assembler.scala 161:93:@343.10]
  wire [7:0] _GEN_18; // @[assembler.scala 161:93:@343.10]
  wire [2:0] _GEN_19; // @[assembler.scala 161:93:@343.10]
  wire  _T_137; // @[assembler.scala 177:32:@370.12]
  wire  _T_140; // @[assembler.scala 177:40:@372.12]
  wire  _T_143; // @[assembler.scala 177:66:@374.12]
  wire [2:0] _GEN_24; // @[assembler.scala 177:93:@375.12]
  wire [7:0] _GEN_25; // @[assembler.scala 177:93:@375.12]
  wire [2:0] _GEN_26; // @[assembler.scala 177:93:@375.12]
  wire [8:0] _T_159; // @[assembler.scala 193:48:@402.14]
  wire [8:0] _T_160; // @[assembler.scala 193:48:@403.14]
  wire [7:0] _T_161; // @[assembler.scala 193:48:@404.14]
  wire  _T_162; // @[assembler.scala 193:32:@405.14]
  wire  _T_165; // @[assembler.scala 193:53:@407.14]
  wire  _T_168; // @[assembler.scala 193:79:@409.14]
  wire [2:0] _GEN_31; // @[assembler.scala 193:106:@410.14]
  wire [7:0] _GEN_32; // @[assembler.scala 193:106:@410.14]
  wire [2:0] _GEN_33; // @[assembler.scala 193:106:@410.14]
  wire  _T_187; // @[assembler.scala 209:40:@439.16]
  wire  _T_190; // @[assembler.scala 209:66:@441.16]
  wire [2:0] _GEN_38; // @[assembler.scala 209:93:@442.16]
  wire [7:0] _GEN_39; // @[assembler.scala 209:93:@442.16]
  wire [2:0] _GEN_40; // @[assembler.scala 209:93:@442.16]
  wire [2:0] _GEN_41; // @[assembler.scala 208:36:@436.14]
  wire [7:0] _GEN_42; // @[assembler.scala 208:36:@436.14]
  wire [2:0] _GEN_43; // @[assembler.scala 208:36:@436.14]
  wire [2:0] _GEN_44; // @[assembler.scala 192:44:@401.12]
  wire [7:0] _GEN_45; // @[assembler.scala 192:44:@401.12]
  wire [2:0] _GEN_46; // @[assembler.scala 192:44:@401.12]
  wire [2:0] _GEN_47; // @[assembler.scala 176:43:@369.10]
  wire [7:0] _GEN_48; // @[assembler.scala 176:43:@369.10]
  wire [2:0] _GEN_49; // @[assembler.scala 176:43:@369.10]
  wire [2:0] _GEN_50; // @[assembler.scala 160:35:@337.8]
  wire [7:0] _GEN_51; // @[assembler.scala 160:35:@337.8]
  wire [2:0] _GEN_52; // @[assembler.scala 160:35:@337.8]
  wire [2:0] _GEN_53; // @[assembler.scala 144:41:@305.6]
  wire [7:0] _GEN_54; // @[assembler.scala 144:41:@305.6]
  wire [2:0] _GEN_55; // @[assembler.scala 144:41:@305.6]
  wire [2:0] state_w; // @[assembler.scala 136:31:@292.4]
  wire [7:0] counter_w; // @[assembler.scala 136:31:@292.4]
  wire [2:0] counter_byte_w; // @[assembler.scala 136:31:@292.4]
  wire  _T_207; // @[assembler.scala 230:37:@471.4]
  wire [7:0] _GEN_59; // @[assembler.scala 230:58:@472.4]
  wire  _T_210; // @[assembler.scala 237:29:@479.4]
  wire  _T_212; // @[assembler.scala 237:55:@481.4]
  wire  _T_219; // @[assembler.scala 238:46:@488.6]
  wire  _T_222; // @[assembler.scala 238:80:@490.6]
  wire  _T_225; // @[assembler.scala 238:106:@492.6]
  wire  _T_231; // @[assembler.scala 240:51:@499.8]
  wire  _GEN_60; // @[assembler.scala 242:55:@505.10]
  wire  _GEN_61; // @[assembler.scala 240:78:@500.8]
  wire  _GEN_62; // @[assembler.scala 238:133:@493.6]
  wire  _T_239; // @[assembler.scala 248:43:@514.6]
  wire  _T_242; // @[assembler.scala 248:64:@516.6]
  wire  _T_245; // @[assembler.scala 248:90:@518.6]
  wire  _GEN_64; // @[assembler.scala 237:82:@482.4]
  wire  _GEN_65; // @[assembler.scala 260:93:@538.8]
  wire  _GEN_66; // @[assembler.scala 266:93:@553.10]
  wire  _GEN_67; // @[assembler.scala 274:55:@569.12]
  wire  _GEN_68; // @[assembler.scala 272:72:@564.10]
  wire  _GEN_69; // @[assembler.scala 265:36:@547.8]
  wire  _GEN_70; // @[assembler.scala 259:41:@532.6]
  wire  _GEN_71; // @[assembler.scala 257:31:@527.4]
  wire [7:0] _GEN_72; // @[assembler.scala 281:49:@580.6]
  wire  _T_289; // @[assembler.scala 287:40:@590.8]
  wire  _T_291; // @[assembler.scala 287:44:@591.8]
  wire [7:0] _GEN_73; // @[assembler.scala 287:52:@592.8]
  wire  _T_294; // @[assembler.scala 294:32:@602.10]
  wire [23:0] CRC_Result_w; // @[assembler.scala 77:32:@237.4]
  wire [7:0] _T_295; // @[assembler.scala 295:47:@604.12]
  wire  _T_297; // @[assembler.scala 296:38:@608.12]
  wire [7:0] _T_298; // @[assembler.scala 297:47:@610.14]
  wire  _T_300; // @[assembler.scala 298:38:@614.14]
  wire [7:0] _T_301; // @[assembler.scala 299:47:@616.16]
  wire [7:0] _GEN_74; // @[assembler.scala 298:46:@615.14]
  wire [7:0] _GEN_75; // @[assembler.scala 296:46:@609.12]
  wire [7:0] _GEN_76; // @[assembler.scala 294:40:@603.10]
  wire [7:0] _GEN_77; // @[assembler.scala 292:36:@601.8]
  wire [7:0] _GEN_78; // @[assembler.scala 286:41:@589.6]
  wire [7:0] data_w; // @[assembler.scala 280:82:@578.4]
  wire  CRC_Data_w; // @[assembler.scala 309:64:@631.4]
  wire  CRC_Valid_w; // @[assembler.scala 309:64:@631.4]
  wire  WHITE_Data_w; // @[assembler.scala 321:83:@648.4]
  wire  WHITE_Valid_w; // @[assembler.scala 321:83:@648.4]
  ble_crc CRC_inst ( // @[assembler.scala 349:30:@663.4]
    .clock(CRC_inst_clock),
    .reset(CRC_inst_reset),
    .io_operand_valid(CRC_inst_io_operand_valid),
    .io_operand_bits(CRC_inst_io_operand_bits),
    .io_result_bits(CRC_inst_io_result_bits),
    .io_seed(CRC_inst_io_seed),
    .io_init(CRC_inst_io_init)
  );
  ble_whitening WHITE_inst ( // @[assembler.scala 359:32:@672.4]
    .clock(WHITE_inst_clock),
    .reset(WHITE_inst_reset),
    .io_operand_valid(WHITE_inst_io_operand_valid),
    .io_operand_bits(WHITE_inst_io_operand_bits),
    .io_result_bits(WHITE_inst_io_result_bits),
    .io_seed(WHITE_inst_io_seed),
    .io_init(WHITE_inst_io_init)
  );
  assign DMA_Data_Fire_w = io_DMA_Data_i_ready & io_DMA_Data_i_valid; // @[assembler.scala 89:48:@245.4]
  assign AFIFO_Fire_w = io_AFIFO_Data_o_ready & io_AFIFO_Data_o_valid; // @[assembler.scala 91:47:@248.4]
  assign _T_65 = state_r == 3'h0; // @[assembler.scala 110:22:@252.4]
  assign _T_67 = state_r == 3'h3; // @[assembler.scala 113:30:@257.6]
  assign _T_68 = state_r == 3'h4; // @[assembler.scala 113:56:@258.6]
  assign _T_69 = _T_67 | _T_68; // @[assembler.scala 113:45:@259.6]
  assign _T_70 = state_r == 3'h5; // @[assembler.scala 113:83:@260.6]
  assign _T_71 = _T_69 | _T_70; // @[assembler.scala 113:72:@261.6]
  assign _T_72 = data_r >> counter_byte_r; // @[assembler.scala 116:55:@266.8]
  assign _T_73 = _T_72[0]; // @[assembler.scala 116:55:@267.8]
  assign WHITE_Result_w = WHITE_inst_io_result_bits; // @[assembler.scala 84:34:@242.4]
  assign _GEN_0 = _T_71 ? WHITE_Result_w : _T_73; // @[assembler.scala 113:91:@262.6]
  assign _GEN_1 = _T_65 ? 1'h0 : _GEN_0; // @[assembler.scala 110:31:@253.4]
  assign _T_76 = counter_r == 8'h2; // @[assembler.scala 120:43:@272.4]
  assign _T_77 = _T_70 & _T_76; // @[assembler.scala 120:30:@273.4]
  assign _T_79 = counter_byte_r == 3'h7; // @[assembler.scala 120:69:@274.4]
  assign _T_80 = _T_77 & _T_79; // @[assembler.scala 120:51:@275.4]
  assign _T_83 = _T_80 & AFIFO_Fire_w; // @[assembler.scala 120:77:@277.4]
  assign _GEN_3 = io_DMA_Trigger_i ? 3'h1 : 3'h0; // @[assembler.scala 137:50:@294.6]
  assign _GEN_4 = io_DMA_Trigger_i ? 8'h0 : counter_r; // @[assembler.scala 137:50:@294.6]
  assign _GEN_5 = io_DMA_Trigger_i ? 3'h0 : counter_byte_r; // @[assembler.scala 137:50:@294.6]
  assign _T_91 = state_r == 3'h1; // @[assembler.scala 144:28:@304.6]
  assign _T_93 = counter_r == 8'h0; // @[assembler.scala 145:32:@306.8]
  assign _T_96 = _T_93 & _T_79; // @[assembler.scala 145:40:@308.8]
  assign _T_99 = _T_96 & AFIFO_Fire_w; // @[assembler.scala 145:66:@310.8]
  assign _T_107 = counter_r + 8'h1; // @[assembler.scala 153:63:@322.14]
  assign _T_108 = _T_107[7:0]; // @[assembler.scala 153:63:@323.14]
  assign _T_111 = counter_byte_r + 3'h1; // @[assembler.scala 156:73:@328.14]
  assign _T_112 = _T_111[2:0]; // @[assembler.scala 156:73:@329.14]
  assign _GEN_6 = _T_79 ? _T_108 : counter_r; // @[assembler.scala 152:61:@321.12]
  assign _GEN_7 = _T_79 ? 3'h0 : _T_112; // @[assembler.scala 152:61:@321.12]
  assign _GEN_8 = AFIFO_Fire_w ? _GEN_6 : counter_r; // @[assembler.scala 151:54:@319.10]
  assign _GEN_9 = AFIFO_Fire_w ? _GEN_7 : counter_byte_r; // @[assembler.scala 151:54:@319.10]
  assign _GEN_10 = _T_99 ? 3'h2 : 3'h1; // @[assembler.scala 145:93:@311.8]
  assign _GEN_11 = _T_99 ? 8'h0 : _GEN_8; // @[assembler.scala 145:93:@311.8]
  assign _GEN_12 = _T_99 ? 3'h0 : _GEN_9; // @[assembler.scala 145:93:@311.8]
  assign _T_113 = state_r == 3'h2; // @[assembler.scala 160:28:@336.8]
  assign _T_115 = counter_r == 8'h3; // @[assembler.scala 161:32:@338.10]
  assign _T_118 = _T_115 & _T_79; // @[assembler.scala 161:40:@340.10]
  assign _T_121 = _T_118 & AFIFO_Fire_w; // @[assembler.scala 161:66:@342.10]
  assign _GEN_17 = _T_121 ? 3'h3 : 3'h2; // @[assembler.scala 161:93:@343.10]
  assign _GEN_18 = _T_121 ? 8'h0 : _GEN_8; // @[assembler.scala 161:93:@343.10]
  assign _GEN_19 = _T_121 ? 3'h0 : _GEN_9; // @[assembler.scala 161:93:@343.10]
  assign _T_137 = counter_r == 8'h1; // @[assembler.scala 177:32:@370.12]
  assign _T_140 = _T_137 & _T_79; // @[assembler.scala 177:40:@372.12]
  assign _T_143 = _T_140 & AFIFO_Fire_w; // @[assembler.scala 177:66:@374.12]
  assign _GEN_24 = _T_143 ? 3'h4 : 3'h3; // @[assembler.scala 177:93:@375.12]
  assign _GEN_25 = _T_143 ? 8'h0 : _GEN_8; // @[assembler.scala 177:93:@375.12]
  assign _GEN_26 = _T_143 ? 3'h0 : _GEN_9; // @[assembler.scala 177:93:@375.12]
  assign _T_159 = PDU_Length_r - 8'h1; // @[assembler.scala 193:48:@402.14]
  assign _T_160 = $unsigned(_T_159); // @[assembler.scala 193:48:@403.14]
  assign _T_161 = _T_160[7:0]; // @[assembler.scala 193:48:@404.14]
  assign _T_162 = counter_r == _T_161; // @[assembler.scala 193:32:@405.14]
  assign _T_165 = _T_162 & _T_79; // @[assembler.scala 193:53:@407.14]
  assign _T_168 = _T_165 & AFIFO_Fire_w; // @[assembler.scala 193:79:@409.14]
  assign _GEN_31 = _T_168 ? 3'h5 : 3'h4; // @[assembler.scala 193:106:@410.14]
  assign _GEN_32 = _T_168 ? 8'h0 : _GEN_8; // @[assembler.scala 193:106:@410.14]
  assign _GEN_33 = _T_168 ? 3'h0 : _GEN_9; // @[assembler.scala 193:106:@410.14]
  assign _T_187 = _T_76 & _T_79; // @[assembler.scala 209:40:@439.16]
  assign _T_190 = _T_187 & AFIFO_Fire_w; // @[assembler.scala 209:66:@441.16]
  assign _GEN_38 = _T_190 ? 3'h0 : 3'h5; // @[assembler.scala 209:93:@442.16]
  assign _GEN_39 = _T_190 ? 8'h0 : _GEN_8; // @[assembler.scala 209:93:@442.16]
  assign _GEN_40 = _T_190 ? 3'h0 : _GEN_9; // @[assembler.scala 209:93:@442.16]
  assign _GEN_41 = _T_70 ? _GEN_38 : 3'h0; // @[assembler.scala 208:36:@436.14]
  assign _GEN_42 = _T_70 ? _GEN_39 : counter_r; // @[assembler.scala 208:36:@436.14]
  assign _GEN_43 = _T_70 ? _GEN_40 : counter_byte_r; // @[assembler.scala 208:36:@436.14]
  assign _GEN_44 = _T_68 ? _GEN_31 : _GEN_41; // @[assembler.scala 192:44:@401.12]
  assign _GEN_45 = _T_68 ? _GEN_32 : _GEN_42; // @[assembler.scala 192:44:@401.12]
  assign _GEN_46 = _T_68 ? _GEN_33 : _GEN_43; // @[assembler.scala 192:44:@401.12]
  assign _GEN_47 = _T_67 ? _GEN_24 : _GEN_44; // @[assembler.scala 176:43:@369.10]
  assign _GEN_48 = _T_67 ? _GEN_25 : _GEN_45; // @[assembler.scala 176:43:@369.10]
  assign _GEN_49 = _T_67 ? _GEN_26 : _GEN_46; // @[assembler.scala 176:43:@369.10]
  assign _GEN_50 = _T_113 ? _GEN_17 : _GEN_47; // @[assembler.scala 160:35:@337.8]
  assign _GEN_51 = _T_113 ? _GEN_18 : _GEN_48; // @[assembler.scala 160:35:@337.8]
  assign _GEN_52 = _T_113 ? _GEN_19 : _GEN_49; // @[assembler.scala 160:35:@337.8]
  assign _GEN_53 = _T_91 ? _GEN_10 : _GEN_50; // @[assembler.scala 144:41:@305.6]
  assign _GEN_54 = _T_91 ? _GEN_11 : _GEN_51; // @[assembler.scala 144:41:@305.6]
  assign _GEN_55 = _T_91 ? _GEN_12 : _GEN_52; // @[assembler.scala 144:41:@305.6]
  assign state_w = _T_65 ? _GEN_3 : _GEN_53; // @[assembler.scala 136:31:@292.4]
  assign counter_w = _T_65 ? _GEN_4 : _GEN_54; // @[assembler.scala 136:31:@292.4]
  assign counter_byte_w = _T_65 ? _GEN_5 : _GEN_55; // @[assembler.scala 136:31:@292.4]
  assign _T_207 = _T_67 & _T_137; // @[assembler.scala 230:37:@471.4]
  assign _GEN_59 = _T_207 ? data_r : PDU_Length_r; // @[assembler.scala 230:58:@472.4]
  assign _T_210 = _T_113 | _T_67; // @[assembler.scala 237:29:@479.4]
  assign _T_212 = _T_210 | _T_68; // @[assembler.scala 237:55:@481.4]
  assign _T_219 = _T_68 & _T_162; // @[assembler.scala 238:46:@488.6]
  assign _T_222 = _T_219 & _T_79; // @[assembler.scala 238:80:@490.6]
  assign _T_225 = _T_222 & AFIFO_Fire_w; // @[assembler.scala 238:106:@492.6]
  assign _T_231 = _T_79 & AFIFO_Fire_w; // @[assembler.scala 240:51:@499.8]
  assign _GEN_60 = DMA_Data_Fire_w ? 1'h0 : DMA_Data_Ready_r; // @[assembler.scala 242:55:@505.10]
  assign _GEN_61 = _T_231 ? 1'h1 : _GEN_60; // @[assembler.scala 240:78:@500.8]
  assign _GEN_62 = _T_225 ? 1'h0 : _GEN_61; // @[assembler.scala 238:133:@493.6]
  assign _T_239 = _T_91 & _T_93; // @[assembler.scala 248:43:@514.6]
  assign _T_242 = _T_239 & _T_79; // @[assembler.scala 248:64:@516.6]
  assign _T_245 = _T_242 & AFIFO_Fire_w; // @[assembler.scala 248:90:@518.6]
  assign _GEN_64 = _T_212 ? _GEN_62 : _T_245; // @[assembler.scala 237:82:@482.4]
  assign _GEN_65 = _T_99 ? 1'h0 : 1'h1; // @[assembler.scala 260:93:@538.8]
  assign _GEN_66 = _T_190 ? 1'h0 : 1'h1; // @[assembler.scala 266:93:@553.10]
  assign _GEN_67 = DMA_Data_Fire_w ? 1'h1 : AFIFO_Valid_r; // @[assembler.scala 274:55:@569.12]
  assign _GEN_68 = _T_231 ? 1'h0 : _GEN_67; // @[assembler.scala 272:72:@564.10]
  assign _GEN_69 = _T_70 ? _GEN_66 : _GEN_68; // @[assembler.scala 265:36:@547.8]
  assign _GEN_70 = _T_91 ? _GEN_65 : _GEN_69; // @[assembler.scala 259:41:@532.6]
  assign _GEN_71 = _T_65 ? 1'h0 : _GEN_70; // @[assembler.scala 257:31:@527.4]
  assign _GEN_72 = DMA_Data_Fire_w ? io_DMA_Data_i_bits : data_r; // @[assembler.scala 281:49:@580.6]
  assign _T_289 = io_DMA_Data_i_bits[0]; // @[assembler.scala 287:40:@590.8]
  assign _T_291 = _T_289 == 1'h0; // @[assembler.scala 287:44:@591.8]
  assign _GEN_73 = _T_291 ? 8'haa : 8'h55; // @[assembler.scala 287:52:@592.8]
  assign _T_294 = counter_w == 8'h0; // @[assembler.scala 294:32:@602.10]
  assign CRC_Result_w = CRC_inst_io_result_bits; // @[assembler.scala 77:32:@237.4]
  assign _T_295 = CRC_Result_w[7:0]; // @[assembler.scala 295:47:@604.12]
  assign _T_297 = counter_w == 8'h1; // @[assembler.scala 296:38:@608.12]
  assign _T_298 = CRC_Result_w[15:8]; // @[assembler.scala 297:47:@610.14]
  assign _T_300 = counter_w == 8'h2; // @[assembler.scala 298:38:@614.14]
  assign _T_301 = CRC_Result_w[23:16]; // @[assembler.scala 299:47:@616.16]
  assign _GEN_74 = _T_300 ? _T_301 : _T_295; // @[assembler.scala 298:46:@615.14]
  assign _GEN_75 = _T_297 ? _T_298 : _GEN_74; // @[assembler.scala 296:46:@609.12]
  assign _GEN_76 = _T_294 ? _T_295 : _GEN_75; // @[assembler.scala 294:40:@603.10]
  assign _GEN_77 = _T_70 ? _GEN_76 : 8'h0; // @[assembler.scala 292:36:@601.8]
  assign _GEN_78 = _T_91 ? _GEN_73 : _GEN_77; // @[assembler.scala 286:41:@589.6]
  assign data_w = _T_212 ? _GEN_72 : _GEN_78; // @[assembler.scala 280:82:@578.4]
  assign CRC_Data_w = _T_69 ? _T_73 : 1'h0; // @[assembler.scala 309:64:@631.4]
  assign CRC_Valid_w = _T_69 ? AFIFO_Fire_w : 1'h0; // @[assembler.scala 309:64:@631.4]
  assign WHITE_Data_w = _T_71 ? _T_73 : 1'h0; // @[assembler.scala 321:83:@648.4]
  assign WHITE_Valid_w = _T_71 ? AFIFO_Fire_w : 1'h0; // @[assembler.scala 321:83:@648.4]
  assign io_DMA_Data_i_ready = DMA_Data_Ready_r;
  assign io_DMA_Done_o = _T_83;
  assign io_AFIFO_Data_o_valid = AFIFO_Valid_r;
  assign io_AFIFO_Data_o_bits = _GEN_1;
  assign CRC_inst_clock = clock;
  assign CRC_inst_reset = reset;
  assign CRC_inst_io_operand_valid = CRC_Valid_w;
  assign CRC_inst_io_operand_bits = CRC_Data_w;
  assign CRC_inst_io_seed = io_REG_CRC_Seed_i;
  assign CRC_inst_io_init = io_DMA_Trigger_i;
  assign WHITE_inst_clock = clock;
  assign WHITE_inst_reset = reset;
  assign WHITE_inst_io_operand_valid = WHITE_Valid_w;
  assign WHITE_inst_io_operand_bits = WHITE_Data_w;
  assign WHITE_inst_io_seed = io_REG_White_Seed_i;
  assign WHITE_inst_io_init = io_DMA_Trigger_i;
`ifdef RANDOMIZE
  integer initvar;
  initial begin
    `ifndef verilator
      #0.002 begin end
    `endif
  `ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{$random}};
  state_r = _RAND_0[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{$random}};
  counter_r = _RAND_1[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{$random}};
  counter_byte_r = _RAND_2[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{$random}};
  PDU_Length_r = _RAND_3[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{$random}};
  DMA_Data_Ready_r = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{$random}};
  AFIFO_Valid_r = _RAND_5[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{$random}};
  data_r = _RAND_6[7:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge clock) begin
    if (reset) begin
      state_r <= 3'h0;
    end else begin
      if (_T_65) begin
        if (io_DMA_Trigger_i) begin
          state_r <= 3'h1;
        end else begin
          state_r <= 3'h0;
        end
      end else begin
        if (_T_91) begin
          if (_T_99) begin
            state_r <= 3'h2;
          end else begin
            state_r <= 3'h1;
          end
        end else begin
          if (_T_113) begin
            if (_T_121) begin
              state_r <= 3'h3;
            end else begin
              state_r <= 3'h2;
            end
          end else begin
            if (_T_67) begin
              if (_T_143) begin
                state_r <= 3'h4;
              end else begin
                state_r <= 3'h3;
              end
            end else begin
              if (_T_68) begin
                if (_T_168) begin
                  state_r <= 3'h5;
                end else begin
                  state_r <= 3'h4;
                end
              end else begin
                if (_T_70) begin
                  if (_T_190) begin
                    state_r <= 3'h0;
                  end else begin
                    state_r <= 3'h5;
                  end
                end else begin
                  state_r <= 3'h0;
                end
              end
            end
          end
        end
      end
    end
    if (reset) begin
      counter_r <= 8'h0;
    end else begin
      if (_T_65) begin
        if (io_DMA_Trigger_i) begin
          counter_r <= 8'h0;
        end
      end else begin
        if (_T_91) begin
          if (_T_99) begin
            counter_r <= 8'h0;
          end else begin
            if (AFIFO_Fire_w) begin
              if (_T_79) begin
                counter_r <= _T_108;
              end
            end
          end
        end else begin
          if (_T_113) begin
            if (_T_121) begin
              counter_r <= 8'h0;
            end else begin
              if (AFIFO_Fire_w) begin
                if (_T_79) begin
                  counter_r <= _T_108;
                end
              end
            end
          end else begin
            if (_T_67) begin
              if (_T_143) begin
                counter_r <= 8'h0;
              end else begin
                if (AFIFO_Fire_w) begin
                  if (_T_79) begin
                    counter_r <= _T_108;
                  end
                end
              end
            end else begin
              if (_T_68) begin
                if (_T_168) begin
                  counter_r <= 8'h0;
                end else begin
                  if (AFIFO_Fire_w) begin
                    if (_T_79) begin
                      counter_r <= _T_108;
                    end
                  end
                end
              end else begin
                if (_T_70) begin
                  if (_T_190) begin
                    counter_r <= 8'h0;
                  end else begin
                    counter_r <= _GEN_8;
                  end
                end
              end
            end
          end
        end
      end
    end
    if (reset) begin
      counter_byte_r <= 3'h0;
    end else begin
      if (_T_65) begin
        if (io_DMA_Trigger_i) begin
          counter_byte_r <= 3'h0;
        end
      end else begin
        if (_T_91) begin
          if (_T_99) begin
            counter_byte_r <= 3'h0;
          end else begin
            if (AFIFO_Fire_w) begin
              if (_T_79) begin
                counter_byte_r <= 3'h0;
              end else begin
                counter_byte_r <= _T_112;
              end
            end
          end
        end else begin
          if (_T_113) begin
            if (_T_121) begin
              counter_byte_r <= 3'h0;
            end else begin
              if (AFIFO_Fire_w) begin
                if (_T_79) begin
                  counter_byte_r <= 3'h0;
                end else begin
                  counter_byte_r <= _T_112;
                end
              end
            end
          end else begin
            if (_T_67) begin
              if (_T_143) begin
                counter_byte_r <= 3'h0;
              end else begin
                if (AFIFO_Fire_w) begin
                  if (_T_79) begin
                    counter_byte_r <= 3'h0;
                  end else begin
                    counter_byte_r <= _T_112;
                  end
                end
              end
            end else begin
              if (_T_68) begin
                if (_T_168) begin
                  counter_byte_r <= 3'h0;
                end else begin
                  if (AFIFO_Fire_w) begin
                    if (_T_79) begin
                      counter_byte_r <= 3'h0;
                    end else begin
                      counter_byte_r <= _T_112;
                    end
                  end
                end
              end else begin
                if (_T_70) begin
                  if (_T_190) begin
                    counter_byte_r <= 3'h0;
                  end else begin
                    counter_byte_r <= _GEN_9;
                  end
                end
              end
            end
          end
        end
      end
    end
    if (reset) begin
      PDU_Length_r <= 8'h0;
    end else begin
      if (_T_207) begin
        PDU_Length_r <= data_r;
      end
    end
    if (reset) begin
      DMA_Data_Ready_r <= 1'h0;
    end else begin
      if (_T_212) begin
        if (_T_225) begin
          DMA_Data_Ready_r <= 1'h0;
        end else begin
          if (_T_231) begin
            DMA_Data_Ready_r <= 1'h1;
          end else begin
            if (DMA_Data_Fire_w) begin
              DMA_Data_Ready_r <= 1'h0;
            end
          end
        end
      end else begin
        DMA_Data_Ready_r <= _T_245;
      end
    end
    if (reset) begin
      AFIFO_Valid_r <= 1'h0;
    end else begin
      if (_T_65) begin
        AFIFO_Valid_r <= 1'h0;
      end else begin
        if (_T_91) begin
          if (_T_99) begin
            AFIFO_Valid_r <= 1'h0;
          end else begin
            AFIFO_Valid_r <= 1'h1;
          end
        end else begin
          if (_T_70) begin
            if (_T_190) begin
              AFIFO_Valid_r <= 1'h0;
            end else begin
              AFIFO_Valid_r <= 1'h1;
            end
          end else begin
            if (_T_231) begin
              AFIFO_Valid_r <= 1'h0;
            end else begin
              if (DMA_Data_Fire_w) begin
                AFIFO_Valid_r <= 1'h1;
              end
            end
          end
        end
      end
    end
    if (reset) begin
      data_r <= 8'h0;
    end else begin
      if (_T_212) begin
        if (DMA_Data_Fire_w) begin
          data_r <= io_DMA_Data_i_bits;
        end
      end else begin
        if (_T_91) begin
          if (_T_291) begin
            data_r <= 8'haa;
          end else begin
            data_r <= 8'h55;
          end
        end else begin
          if (_T_70) begin
            if (_T_294) begin
              data_r <= _T_295;
            end else begin
              if (_T_297) begin
                data_r <= _T_298;
              end else begin
                if (_T_300) begin
                  data_r <= _T_301;
                end else begin
                  data_r <= _T_295;
                end
              end
            end
          end else begin
            data_r <= 8'h0;
          end
        end
      end
    end
  end
endmodule
module ble_packetdisassembler( // @[:@881.2]
  input         clock, // @[:@882.4]
  input         reset, // @[:@883.4]
  input         io_DMA_Switch_i, // @[:@884.4]
  input  [31:0] io_REG_AA_i, // @[:@884.4]
  input  [23:0] io_REG_CRC_Seed_i, // @[:@884.4]
  input  [6:0]  io_REG_DeWhite_Seed_i, // @[:@884.4]
  input         io_DMA_Data_o_ready, // @[:@884.4]
  output        io_DMA_Data_o_valid, // @[:@884.4]
  output [7:0]  io_DMA_Data_o_bits, // @[:@884.4]
  input         io_DMA_Length_o_ready, // @[:@884.4]
  output        io_DMA_Length_o_valid, // @[:@884.4]
  output [7:0]  io_DMA_Length_o_bits, // @[:@884.4]
  input         io_DMA_Flag_AA_o_ready, // @[:@884.4]
  output        io_DMA_Flag_AA_o_valid, // @[:@884.4]
  output        io_DMA_Flag_AA_o_bits, // @[:@884.4]
  input         io_DMA_Flag_CRC_o_ready, // @[:@884.4]
  output        io_DMA_Flag_CRC_o_valid, // @[:@884.4]
  output        io_DMA_Flag_CRC_o_bits, // @[:@884.4]
  output        io_AFIFO_Data_i_ready, // @[:@884.4]
  input         io_AFIFO_Data_i_valid, // @[:@884.4]
  input         io_AFIFO_Data_i_bits // @[:@884.4]
);
  reg [2:0] state_r; // @[disassembler.scala 63:24:@901.4]
  reg [31:0] _RAND_0;
  reg [7:0] counter_r; // @[disassembler.scala 66:26:@903.4]
  reg [31:0] _RAND_1;
  reg [2:0] counter_byte_r; // @[disassembler.scala 69:31:@905.4]
  reg [31:0] _RAND_2;
  reg [7:0] PDU_Length_r; // @[disassembler.scala 72:29:@906.4]
  reg [31:0] _RAND_3;
  reg  PDU_Length_Valid_r; // @[disassembler.scala 73:35:@907.4]
  reg [31:0] _RAND_4;
  reg  Flag_AA_r; // @[disassembler.scala 74:26:@908.4]
  reg [31:0] _RAND_5;
  reg  Flag_AA_Valid_r; // @[disassembler.scala 75:32:@909.4]
  reg [31:0] _RAND_6;
  reg  Flag_CRC_r; // @[disassembler.scala 76:27:@910.4]
  reg [31:0] _RAND_7;
  reg  Flag_CRC_Valid_r; // @[disassembler.scala 77:33:@911.4]
  reg [31:0] _RAND_8;
  reg  DMA_Data_Valid_r; // @[disassembler.scala 85:33:@915.4]
  reg [31:0] _RAND_9;
  reg  AFIFO_Ready_r; // @[disassembler.scala 89:30:@917.4]
  reg [31:0] _RAND_10;
  reg [7:0] data_r; // @[disassembler.scala 95:23:@920.4]
  reg [31:0] _RAND_11;
  wire  CRC_inst_clock; // @[disassembler.scala 423:24:@1485.4]
  wire  CRC_inst_reset; // @[disassembler.scala 423:24:@1485.4]
  wire  CRC_inst_io_operand_valid; // @[disassembler.scala 423:24:@1485.4]
  wire  CRC_inst_io_operand_bits; // @[disassembler.scala 423:24:@1485.4]
  wire [23:0] CRC_inst_io_result_bits; // @[disassembler.scala 423:24:@1485.4]
  wire [23:0] CRC_inst_io_seed; // @[disassembler.scala 423:24:@1485.4]
  wire  CRC_inst_io_init; // @[disassembler.scala 423:24:@1485.4]
  wire  WHITE_inst_clock; // @[disassembler.scala 433:26:@1494.4]
  wire  WHITE_inst_reset; // @[disassembler.scala 433:26:@1494.4]
  wire  WHITE_inst_io_operand_valid; // @[disassembler.scala 433:26:@1494.4]
  wire  WHITE_inst_io_operand_bits; // @[disassembler.scala 433:26:@1494.4]
  wire  WHITE_inst_io_result_bits; // @[disassembler.scala 433:26:@1494.4]
  wire [6:0] WHITE_inst_io_seed; // @[disassembler.scala 433:26:@1494.4]
  wire  WHITE_inst_io_init; // @[disassembler.scala 433:26:@1494.4]
  wire  DMA_Data_Fire_w; // @[disassembler.scala 113:42:@931.4]
  wire  AFIFO_Fire_w; // @[disassembler.scala 114:41:@933.4]
  wire  _T_102; // @[disassembler.scala 122:19:@937.4]
  wire  _T_104; // @[disassembler.scala 122:23:@938.4]
  wire [7:0] Preamble01; // @[disassembler.scala 122:31:@939.4]
  wire  _T_105; // @[disassembler.scala 130:16:@945.4]
  wire  _T_106; // @[disassembler.scala 130:36:@946.4]
  wire  _T_107; // @[disassembler.scala 130:25:@947.4]
  wire [7:0] _GEN_5; // @[disassembler.scala 130:49:@948.4]
  wire  _T_109; // @[disassembler.scala 150:24:@965.4]
  wire  _T_110; // @[disassembler.scala 150:24:@966.4]
  wire  _T_111; // @[disassembler.scala 150:24:@967.4]
  wire  _T_112; // @[disassembler.scala 150:24:@968.4]
  wire  _T_113; // @[disassembler.scala 150:24:@969.4]
  wire  _T_114; // @[disassembler.scala 150:24:@970.4]
  wire  _T_115; // @[disassembler.scala 150:24:@971.4]
  wire  _T_116; // @[disassembler.scala 150:24:@972.4]
  wire [2:0] _GEN_6; // @[disassembler.scala 172:37:@998.6]
  wire  _T_348; // @[disassembler.scala 353:16:@1341.4]
  wire  _T_349; // @[disassembler.scala 353:42:@1342.4]
  wire  _T_350; // @[disassembler.scala 353:31:@1343.4]
  wire  _T_351; // @[disassembler.scala 353:69:@1344.4]
  wire  _T_352; // @[disassembler.scala 353:58:@1345.4]
  wire  DEWHITE_Result_w; // @[disassembler.scala 108:30:@929.4]
  wire  _T_356; // @[disassembler.scala 356:28:@1349.8]
  wire  _GEN_99; // @[disassembler.scala 357:32:@1351.10]
  wire  _GEN_107; // @[disassembler.scala 359:32:@1354.10]
  wire  _GEN_115; // @[disassembler.scala 356:35:@1350.8]
  wire  _GEN_123; // @[disassembler.scala 354:34:@1348.6]
  wire  _T_375; // @[disassembler.scala 372:25:@1379.10]
  wire  _GEN_132; // @[disassembler.scala 372:32:@1380.10]
  wire  _GEN_140; // @[disassembler.scala 363:34:@1362.8]
  wire  _T_403; // @[disassembler.scala 379:22:@1429.8]
  wire  _T_407; // @[disassembler.scala 382:32:@1433.12]
  wire  _GEN_163; // @[disassembler.scala 382:39:@1434.12]
  wire  _GEN_171; // @[disassembler.scala 380:34:@1432.10]
  wire  _GEN_179; // @[disassembler.scala 379:29:@1430.8]
  wire  _GEN_188; // @[disassembler.scala 362:35:@1360.6]
  wire  data_w_1; // @[disassembler.scala 353:77:@1346.4]
  wire  _GEN_98; // @[disassembler.scala 357:32:@1351.10]
  wire  _GEN_106; // @[disassembler.scala 359:32:@1354.10]
  wire  _GEN_114; // @[disassembler.scala 356:35:@1350.8]
  wire  _GEN_122; // @[disassembler.scala 354:34:@1348.6]
  wire  _T_370; // @[disassembler.scala 372:25:@1371.10]
  wire  _GEN_131; // @[disassembler.scala 372:32:@1372.10]
  wire  _GEN_139; // @[disassembler.scala 363:34:@1362.8]
  wire  _GEN_162; // @[disassembler.scala 382:39:@1434.12]
  wire  _GEN_170; // @[disassembler.scala 380:34:@1432.10]
  wire  _GEN_178; // @[disassembler.scala 379:29:@1430.8]
  wire  _GEN_187; // @[disassembler.scala 362:35:@1360.6]
  wire  data_w_0; // @[disassembler.scala 353:77:@1346.4]
  wire [1:0] _T_127; // @[disassembler.scala 178:17:@1008.8]
  wire  _GEN_101; // @[disassembler.scala 357:32:@1351.10]
  wire  _GEN_109; // @[disassembler.scala 359:32:@1354.10]
  wire  _GEN_117; // @[disassembler.scala 356:35:@1350.8]
  wire  _GEN_125; // @[disassembler.scala 354:34:@1348.6]
  wire  _T_385; // @[disassembler.scala 372:25:@1395.10]
  wire  _GEN_134; // @[disassembler.scala 372:32:@1396.10]
  wire  _GEN_142; // @[disassembler.scala 363:34:@1362.8]
  wire  _GEN_165; // @[disassembler.scala 382:39:@1434.12]
  wire  _GEN_173; // @[disassembler.scala 380:34:@1432.10]
  wire  _GEN_181; // @[disassembler.scala 379:29:@1430.8]
  wire  _GEN_190; // @[disassembler.scala 362:35:@1360.6]
  wire  data_w_3; // @[disassembler.scala 353:77:@1346.4]
  wire  _GEN_100; // @[disassembler.scala 357:32:@1351.10]
  wire  _GEN_108; // @[disassembler.scala 359:32:@1354.10]
  wire  _GEN_116; // @[disassembler.scala 356:35:@1350.8]
  wire  _GEN_124; // @[disassembler.scala 354:34:@1348.6]
  wire  _T_380; // @[disassembler.scala 372:25:@1387.10]
  wire  _GEN_133; // @[disassembler.scala 372:32:@1388.10]
  wire  _GEN_141; // @[disassembler.scala 363:34:@1362.8]
  wire  _GEN_164; // @[disassembler.scala 382:39:@1434.12]
  wire  _GEN_172; // @[disassembler.scala 380:34:@1432.10]
  wire  _GEN_180; // @[disassembler.scala 379:29:@1430.8]
  wire  _GEN_189; // @[disassembler.scala 362:35:@1360.6]
  wire  data_w_2; // @[disassembler.scala 353:77:@1346.4]
  wire [1:0] _T_128; // @[disassembler.scala 178:17:@1009.8]
  wire [3:0] _T_129; // @[disassembler.scala 178:17:@1010.8]
  wire  _GEN_103; // @[disassembler.scala 357:32:@1351.10]
  wire  _GEN_111; // @[disassembler.scala 359:32:@1354.10]
  wire  _GEN_119; // @[disassembler.scala 356:35:@1350.8]
  wire  _GEN_127; // @[disassembler.scala 354:34:@1348.6]
  wire  _T_395; // @[disassembler.scala 372:25:@1411.10]
  wire  _GEN_136; // @[disassembler.scala 372:32:@1412.10]
  wire  _GEN_144; // @[disassembler.scala 363:34:@1362.8]
  wire  _GEN_167; // @[disassembler.scala 382:39:@1434.12]
  wire  _GEN_175; // @[disassembler.scala 380:34:@1432.10]
  wire  _GEN_183; // @[disassembler.scala 379:29:@1430.8]
  wire  _GEN_192; // @[disassembler.scala 362:35:@1360.6]
  wire  data_w_5; // @[disassembler.scala 353:77:@1346.4]
  wire  _GEN_102; // @[disassembler.scala 357:32:@1351.10]
  wire  _GEN_110; // @[disassembler.scala 359:32:@1354.10]
  wire  _GEN_118; // @[disassembler.scala 356:35:@1350.8]
  wire  _GEN_126; // @[disassembler.scala 354:34:@1348.6]
  wire  _T_390; // @[disassembler.scala 372:25:@1403.10]
  wire  _GEN_135; // @[disassembler.scala 372:32:@1404.10]
  wire  _GEN_143; // @[disassembler.scala 363:34:@1362.8]
  wire  _GEN_166; // @[disassembler.scala 382:39:@1434.12]
  wire  _GEN_174; // @[disassembler.scala 380:34:@1432.10]
  wire  _GEN_182; // @[disassembler.scala 379:29:@1430.8]
  wire  _GEN_191; // @[disassembler.scala 362:35:@1360.6]
  wire  data_w_4; // @[disassembler.scala 353:77:@1346.4]
  wire [1:0] _T_130; // @[disassembler.scala 178:17:@1011.8]
  wire  _GEN_105; // @[disassembler.scala 357:32:@1351.10]
  wire  _GEN_113; // @[disassembler.scala 359:32:@1354.10]
  wire  _GEN_121; // @[disassembler.scala 356:35:@1350.8]
  wire  _GEN_129; // @[disassembler.scala 354:34:@1348.6]
  wire  _GEN_130; // @[disassembler.scala 365:39:@1364.10]
  wire  _GEN_138; // @[disassembler.scala 363:34:@1362.8]
  wire  _GEN_169; // @[disassembler.scala 382:39:@1434.12]
  wire  _GEN_177; // @[disassembler.scala 380:34:@1432.10]
  wire  _GEN_185; // @[disassembler.scala 379:29:@1430.8]
  wire  _GEN_186; // @[disassembler.scala 362:35:@1360.6]
  wire  data_w_7; // @[disassembler.scala 353:77:@1346.4]
  wire  _GEN_104; // @[disassembler.scala 357:32:@1351.10]
  wire  _GEN_112; // @[disassembler.scala 359:32:@1354.10]
  wire  _GEN_120; // @[disassembler.scala 356:35:@1350.8]
  wire  _GEN_128; // @[disassembler.scala 354:34:@1348.6]
  wire  _T_400; // @[disassembler.scala 372:25:@1419.10]
  wire  _GEN_137; // @[disassembler.scala 372:32:@1420.10]
  wire  _GEN_145; // @[disassembler.scala 363:34:@1362.8]
  wire  _GEN_168; // @[disassembler.scala 382:39:@1434.12]
  wire  _GEN_176; // @[disassembler.scala 380:34:@1432.10]
  wire  _GEN_184; // @[disassembler.scala 379:29:@1430.8]
  wire  _GEN_193; // @[disassembler.scala 362:35:@1360.6]
  wire  data_w_6; // @[disassembler.scala 353:77:@1346.4]
  wire [1:0] _T_131; // @[disassembler.scala 178:17:@1012.8]
  wire [3:0] _T_132; // @[disassembler.scala 178:17:@1013.8]
  wire [7:0] _T_133; // @[disassembler.scala 178:17:@1014.8]
  wire  _T_134; // @[disassembler.scala 178:24:@1015.8]
  wire [2:0] _GEN_7; // @[disassembler.scala 178:39:@1016.8]
  wire [7:0] _GEN_8; // @[disassembler.scala 178:39:@1016.8]
  wire [2:0] _GEN_9; // @[disassembler.scala 178:39:@1016.8]
  wire  _T_139; // @[disassembler.scala 186:20:@1028.10]
  wire  _T_142; // @[disassembler.scala 186:28:@1030.10]
  wire [8:0] _T_148; // @[disassembler.scala 193:31:@1040.14]
  wire [7:0] _T_149; // @[disassembler.scala 193:31:@1041.14]
  wire [7:0] _GEN_10; // @[disassembler.scala 192:39:@1039.12]
  wire  _T_153; // @[disassembler.scala 196:29:@1046.14]
  wire [3:0] _T_156; // @[disassembler.scala 199:43:@1051.16]
  wire [2:0] _T_157; // @[disassembler.scala 199:43:@1052.16]
  wire [2:0] _GEN_11; // @[disassembler.scala 196:37:@1047.14]
  wire [2:0] _GEN_12; // @[disassembler.scala 195:36:@1045.12]
  wire [2:0] _GEN_13; // @[disassembler.scala 186:58:@1031.10]
  wire [7:0] _GEN_14; // @[disassembler.scala 186:58:@1031.10]
  wire [2:0] _GEN_15; // @[disassembler.scala 186:58:@1031.10]
  wire  _T_160; // @[disassembler.scala 204:20:@1061.12]
  wire  _T_163; // @[disassembler.scala 204:28:@1063.12]
  wire [2:0] _GEN_19; // @[disassembler.scala 204:58:@1064.12]
  wire [7:0] _GEN_20; // @[disassembler.scala 204:58:@1064.12]
  wire [2:0] _GEN_21; // @[disassembler.scala 204:58:@1064.12]
  wire [8:0] _T_181; // @[disassembler.scala 222:36:@1094.14]
  wire [8:0] _T_182; // @[disassembler.scala 222:36:@1095.14]
  wire [7:0] _T_183; // @[disassembler.scala 222:36:@1096.14]
  wire  _T_184; // @[disassembler.scala 222:20:@1097.14]
  wire  _T_187; // @[disassembler.scala 222:41:@1099.14]
  wire [2:0] _GEN_25; // @[disassembler.scala 222:71:@1100.14]
  wire [7:0] _GEN_26; // @[disassembler.scala 222:71:@1100.14]
  wire [2:0] _GEN_27; // @[disassembler.scala 222:71:@1100.14]
  wire  _T_205; // @[disassembler.scala 240:20:@1130.16]
  wire  _T_208; // @[disassembler.scala 240:28:@1132.16]
  wire [2:0] _GEN_31; // @[disassembler.scala 240:58:@1133.16]
  wire [7:0] _GEN_32; // @[disassembler.scala 240:58:@1133.16]
  wire [2:0] _GEN_33; // @[disassembler.scala 240:58:@1133.16]
  wire  _T_224; // @[disassembler.scala 257:22:@1161.16]
  wire  _T_229; // @[disassembler.scala 258:44:@1165.18]
  wire  _T_232; // @[disassembler.scala 258:81:@1167.18]
  wire [2:0] _GEN_34; // @[disassembler.scala 258:120:@1168.18]
  wire [2:0] _GEN_35; // @[disassembler.scala 257:36:@1162.16]
  wire [2:0] _GEN_36; // @[disassembler.scala 239:30:@1129.14]
  wire [7:0] _GEN_37; // @[disassembler.scala 239:30:@1129.14]
  wire [2:0] _GEN_38; // @[disassembler.scala 239:30:@1129.14]
  wire [2:0] _GEN_39; // @[disassembler.scala 221:38:@1093.12]
  wire [7:0] _GEN_40; // @[disassembler.scala 221:38:@1093.12]
  wire [2:0] _GEN_41; // @[disassembler.scala 221:38:@1093.12]
  wire [2:0] _GEN_42; // @[disassembler.scala 203:37:@1060.10]
  wire [7:0] _GEN_43; // @[disassembler.scala 203:37:@1060.10]
  wire [2:0] _GEN_44; // @[disassembler.scala 203:37:@1060.10]
  wire [2:0] _GEN_45; // @[disassembler.scala 185:29:@1027.8]
  wire [7:0] _GEN_46; // @[disassembler.scala 185:29:@1027.8]
  wire [2:0] _GEN_47; // @[disassembler.scala 185:29:@1027.8]
  wire [2:0] _GEN_48; // @[disassembler.scala 177:35:@1007.6]
  wire [7:0] _GEN_49; // @[disassembler.scala 177:35:@1007.6]
  wire [2:0] _GEN_50; // @[disassembler.scala 177:35:@1007.6]
  wire [7:0] _GEN_51; // @[disassembler.scala 165:25:@990.4]
  wire  _GEN_52; // @[disassembler.scala 165:25:@990.4]
  wire  _GEN_53; // @[disassembler.scala 165:25:@990.4]
  wire  _GEN_54; // @[disassembler.scala 165:25:@990.4]
  wire  _GEN_55; // @[disassembler.scala 165:25:@990.4]
  wire  _GEN_56; // @[disassembler.scala 165:25:@990.4]
  wire [2:0] state_w; // @[disassembler.scala 165:25:@990.4]
  wire [7:0] counter_w; // @[disassembler.scala 165:25:@990.4]
  wire [2:0] counter_byte_w; // @[disassembler.scala 165:25:@990.4]
  wire  _T_236; // @[disassembler.scala 269:31:@1180.4]
  wire  _T_239; // @[disassembler.scala 269:52:@1182.4]
  wire  _T_241; // @[disassembler.scala 272:22:@1188.6]
  wire  _GEN_60; // @[disassembler.scala 272:32:@1189.6]
  wire [7:0] _GEN_61; // @[disassembler.scala 269:82:@1183.4]
  wire  _GEN_62; // @[disassembler.scala 269:82:@1183.4]
  wire  _T_245; // @[disassembler.scala 279:36:@1195.4]
  wire  _T_246; // @[disassembler.scala 279:23:@1196.4]
  wire  _T_249; // @[disassembler.scala 279:44:@1198.4]
  wire [7:0] _T_250; // @[disassembler.scala 280:32:@1200.6]
  wire  _T_251; // @[disassembler.scala 280:17:@1201.6]
  wire  _GEN_63; // @[disassembler.scala 280:38:@1202.6]
  wire  _GEN_64; // @[disassembler.scala 280:38:@1202.6]
  wire  _T_257; // @[disassembler.scala 284:29:@1210.6]
  wire  _T_260; // @[disassembler.scala 284:50:@1212.6]
  wire [7:0] _T_261; // @[disassembler.scala 285:32:@1214.8]
  wire  _T_262; // @[disassembler.scala 285:17:@1215.8]
  wire  _GEN_65; // @[disassembler.scala 285:39:@1216.8]
  wire  _GEN_66; // @[disassembler.scala 285:39:@1216.8]
  wire  _T_268; // @[disassembler.scala 289:29:@1224.8]
  wire  _T_271; // @[disassembler.scala 289:50:@1226.8]
  wire [7:0] _T_272; // @[disassembler.scala 290:32:@1228.10]
  wire  _T_273; // @[disassembler.scala 290:17:@1229.10]
  wire  _GEN_67; // @[disassembler.scala 290:40:@1230.10]
  wire  _GEN_68; // @[disassembler.scala 290:40:@1230.10]
  wire  _T_279; // @[disassembler.scala 294:29:@1238.10]
  wire  _T_282; // @[disassembler.scala 294:50:@1240.10]
  wire [7:0] _T_283; // @[disassembler.scala 295:32:@1242.12]
  wire  _T_284; // @[disassembler.scala 295:17:@1243.12]
  wire  _GEN_69; // @[disassembler.scala 295:40:@1244.12]
  wire  _GEN_71; // @[disassembler.scala 294:80:@1241.10]
  wire  _GEN_72; // @[disassembler.scala 294:80:@1241.10]
  wire  _GEN_73; // @[disassembler.scala 289:80:@1227.8]
  wire  _GEN_74; // @[disassembler.scala 289:80:@1227.8]
  wire  _GEN_75; // @[disassembler.scala 284:80:@1213.6]
  wire  _GEN_76; // @[disassembler.scala 284:80:@1213.6]
  wire  _GEN_77; // @[disassembler.scala 279:74:@1199.4]
  wire  _GEN_78; // @[disassembler.scala 279:74:@1199.4]
  wire  _T_291; // @[disassembler.scala 306:24:@1256.4]
  wire  _T_294; // @[disassembler.scala 306:45:@1258.4]
  wire [23:0] CRC_Result_w; // @[disassembler.scala 101:26:@924.4]
  wire [7:0] _T_295; // @[disassembler.scala 307:33:@1260.6]
  wire  _T_296; // @[disassembler.scala 307:17:@1261.6]
  wire  _GEN_79; // @[disassembler.scala 307:39:@1262.6]
  wire  _GEN_80; // @[disassembler.scala 307:39:@1262.6]
  wire  _T_302; // @[disassembler.scala 311:30:@1270.6]
  wire  _T_305; // @[disassembler.scala 311:51:@1272.6]
  wire [7:0] _T_306; // @[disassembler.scala 312:33:@1274.8]
  wire  _T_307; // @[disassembler.scala 312:17:@1275.8]
  wire  _GEN_81; // @[disassembler.scala 312:40:@1276.8]
  wire  _GEN_82; // @[disassembler.scala 312:40:@1276.8]
  wire  _T_313; // @[disassembler.scala 316:30:@1284.8]
  wire  _T_316; // @[disassembler.scala 316:51:@1286.8]
  wire [7:0] _T_317; // @[disassembler.scala 317:33:@1288.10]
  wire  _T_318; // @[disassembler.scala 317:17:@1289.10]
  wire  _GEN_83; // @[disassembler.scala 317:41:@1290.10]
  wire  _GEN_85; // @[disassembler.scala 316:81:@1287.8]
  wire  _GEN_86; // @[disassembler.scala 316:81:@1287.8]
  wire  _GEN_87; // @[disassembler.scala 311:81:@1273.6]
  wire  _GEN_88; // @[disassembler.scala 311:81:@1273.6]
  wire  _GEN_89; // @[disassembler.scala 306:75:@1259.4]
  wire  _GEN_90; // @[disassembler.scala 306:75:@1259.4]
  wire  _T_330; // @[disassembler.scala 332:33:@1309.6]
  wire  _GEN_91; // @[disassembler.scala 334:43:@1315.8]
  wire  _GEN_92; // @[disassembler.scala 332:60:@1310.6]
  wire  _GEN_93; // @[disassembler.scala 329:49:@1303.4]
  wire  _GEN_94; // @[disassembler.scala 347:43:@1337.10]
  wire  _GEN_95; // @[disassembler.scala 345:60:@1332.8]
  wire  _GEN_96; // @[disassembler.scala 342:35:@1325.6]
  wire  _GEN_97; // @[disassembler.scala 340:25:@1320.4]
  wire  CRC_Data_w; // @[disassembler.scala 394:58:@1449.4]
  wire  CRC_Valid_w; // @[disassembler.scala 394:58:@1449.4]
  wire  DEWHITE_Data_w; // @[disassembler.scala 406:77:@1465.4]
  wire  DEWHITE_Valid_w; // @[disassembler.scala 406:77:@1465.4]
  ble_crc CRC_inst ( // @[disassembler.scala 423:24:@1485.4]
    .clock(CRC_inst_clock),
    .reset(CRC_inst_reset),
    .io_operand_valid(CRC_inst_io_operand_valid),
    .io_operand_bits(CRC_inst_io_operand_bits),
    .io_result_bits(CRC_inst_io_result_bits),
    .io_seed(CRC_inst_io_seed),
    .io_init(CRC_inst_io_init)
  );
  ble_whitening WHITE_inst ( // @[disassembler.scala 433:26:@1494.4]
    .clock(WHITE_inst_clock),
    .reset(WHITE_inst_reset),
    .io_operand_valid(WHITE_inst_io_operand_valid),
    .io_operand_bits(WHITE_inst_io_operand_bits),
    .io_result_bits(WHITE_inst_io_result_bits),
    .io_seed(WHITE_inst_io_seed),
    .io_init(WHITE_inst_io_init)
  );
  assign DMA_Data_Fire_w = io_DMA_Data_o_ready & io_DMA_Data_o_valid; // @[disassembler.scala 113:42:@931.4]
  assign AFIFO_Fire_w = io_AFIFO_Data_i_ready & io_AFIFO_Data_i_valid; // @[disassembler.scala 114:41:@933.4]
  assign _T_102 = io_REG_AA_i[0]; // @[disassembler.scala 122:19:@937.4]
  assign _T_104 = _T_102 == 1'h0; // @[disassembler.scala 122:23:@938.4]
  assign Preamble01 = _T_104 ? 8'haa : 8'h55; // @[disassembler.scala 122:31:@939.4]
  assign _T_105 = state_r == 3'h0; // @[disassembler.scala 130:16:@945.4]
  assign _T_106 = state_r == 3'h1; // @[disassembler.scala 130:36:@946.4]
  assign _T_107 = _T_105 | _T_106; // @[disassembler.scala 130:25:@947.4]
  assign _GEN_5 = _T_107 ? 8'h0 : data_r; // @[disassembler.scala 130:49:@948.4]
  assign _T_109 = data_r[0]; // @[disassembler.scala 150:24:@965.4]
  assign _T_110 = data_r[1]; // @[disassembler.scala 150:24:@966.4]
  assign _T_111 = data_r[2]; // @[disassembler.scala 150:24:@967.4]
  assign _T_112 = data_r[3]; // @[disassembler.scala 150:24:@968.4]
  assign _T_113 = data_r[4]; // @[disassembler.scala 150:24:@969.4]
  assign _T_114 = data_r[5]; // @[disassembler.scala 150:24:@970.4]
  assign _T_115 = data_r[6]; // @[disassembler.scala 150:24:@971.4]
  assign _T_116 = data_r[7]; // @[disassembler.scala 150:24:@972.4]
  assign _GEN_6 = io_DMA_Switch_i ? 3'h1 : 3'h0; // @[disassembler.scala 172:37:@998.6]
  assign _T_348 = state_r == 3'h3; // @[disassembler.scala 353:16:@1341.4]
  assign _T_349 = state_r == 3'h4; // @[disassembler.scala 353:42:@1342.4]
  assign _T_350 = _T_348 | _T_349; // @[disassembler.scala 353:31:@1343.4]
  assign _T_351 = state_r == 3'h5; // @[disassembler.scala 353:69:@1344.4]
  assign _T_352 = _T_350 | _T_351; // @[disassembler.scala 353:58:@1345.4]
  assign DEWHITE_Result_w = WHITE_inst_io_result_bits; // @[disassembler.scala 108:30:@929.4]
  assign _T_356 = DEWHITE_Result_w == 1'h0; // @[disassembler.scala 356:28:@1349.8]
  assign _GEN_99 = 3'h1 == counter_byte_r ? 1'h0 : _T_110; // @[disassembler.scala 357:32:@1351.10]
  assign _GEN_107 = 3'h1 == counter_byte_r ? 1'h1 : _T_110; // @[disassembler.scala 359:32:@1354.10]
  assign _GEN_115 = _T_356 ? _GEN_99 : _GEN_107; // @[disassembler.scala 356:35:@1350.8]
  assign _GEN_123 = AFIFO_Fire_w ? _GEN_115 : _T_110; // @[disassembler.scala 354:34:@1348.6]
  assign _T_375 = _T_111 == 1'h0; // @[disassembler.scala 372:25:@1379.10]
  assign _GEN_132 = _T_375 ? 1'h0 : 1'h1; // @[disassembler.scala 372:32:@1380.10]
  assign _GEN_140 = AFIFO_Fire_w ? _GEN_132 : _T_110; // @[disassembler.scala 363:34:@1362.8]
  assign _T_403 = state_r == 3'h2; // @[disassembler.scala 379:22:@1429.8]
  assign _T_407 = io_AFIFO_Data_i_bits == 1'h0; // @[disassembler.scala 382:32:@1433.12]
  assign _GEN_163 = _T_407 ? _GEN_99 : _GEN_107; // @[disassembler.scala 382:39:@1434.12]
  assign _GEN_171 = AFIFO_Fire_w ? _GEN_163 : _T_110; // @[disassembler.scala 380:34:@1432.10]
  assign _GEN_179 = _T_403 ? _GEN_171 : _T_110; // @[disassembler.scala 379:29:@1430.8]
  assign _GEN_188 = _T_106 ? _GEN_140 : _GEN_179; // @[disassembler.scala 362:35:@1360.6]
  assign data_w_1 = _T_352 ? _GEN_123 : _GEN_188; // @[disassembler.scala 353:77:@1346.4]
  assign _GEN_98 = 3'h0 == counter_byte_r ? 1'h0 : _T_109; // @[disassembler.scala 357:32:@1351.10]
  assign _GEN_106 = 3'h0 == counter_byte_r ? 1'h1 : _T_109; // @[disassembler.scala 359:32:@1354.10]
  assign _GEN_114 = _T_356 ? _GEN_98 : _GEN_106; // @[disassembler.scala 356:35:@1350.8]
  assign _GEN_122 = AFIFO_Fire_w ? _GEN_114 : _T_109; // @[disassembler.scala 354:34:@1348.6]
  assign _T_370 = _T_110 == 1'h0; // @[disassembler.scala 372:25:@1371.10]
  assign _GEN_131 = _T_370 ? 1'h0 : 1'h1; // @[disassembler.scala 372:32:@1372.10]
  assign _GEN_139 = AFIFO_Fire_w ? _GEN_131 : _T_109; // @[disassembler.scala 363:34:@1362.8]
  assign _GEN_162 = _T_407 ? _GEN_98 : _GEN_106; // @[disassembler.scala 382:39:@1434.12]
  assign _GEN_170 = AFIFO_Fire_w ? _GEN_162 : _T_109; // @[disassembler.scala 380:34:@1432.10]
  assign _GEN_178 = _T_403 ? _GEN_170 : _T_109; // @[disassembler.scala 379:29:@1430.8]
  assign _GEN_187 = _T_106 ? _GEN_139 : _GEN_178; // @[disassembler.scala 362:35:@1360.6]
  assign data_w_0 = _T_352 ? _GEN_122 : _GEN_187; // @[disassembler.scala 353:77:@1346.4]
  assign _T_127 = {data_w_1,data_w_0}; // @[disassembler.scala 178:17:@1008.8]
  assign _GEN_101 = 3'h3 == counter_byte_r ? 1'h0 : _T_112; // @[disassembler.scala 357:32:@1351.10]
  assign _GEN_109 = 3'h3 == counter_byte_r ? 1'h1 : _T_112; // @[disassembler.scala 359:32:@1354.10]
  assign _GEN_117 = _T_356 ? _GEN_101 : _GEN_109; // @[disassembler.scala 356:35:@1350.8]
  assign _GEN_125 = AFIFO_Fire_w ? _GEN_117 : _T_112; // @[disassembler.scala 354:34:@1348.6]
  assign _T_385 = _T_113 == 1'h0; // @[disassembler.scala 372:25:@1395.10]
  assign _GEN_134 = _T_385 ? 1'h0 : 1'h1; // @[disassembler.scala 372:32:@1396.10]
  assign _GEN_142 = AFIFO_Fire_w ? _GEN_134 : _T_112; // @[disassembler.scala 363:34:@1362.8]
  assign _GEN_165 = _T_407 ? _GEN_101 : _GEN_109; // @[disassembler.scala 382:39:@1434.12]
  assign _GEN_173 = AFIFO_Fire_w ? _GEN_165 : _T_112; // @[disassembler.scala 380:34:@1432.10]
  assign _GEN_181 = _T_403 ? _GEN_173 : _T_112; // @[disassembler.scala 379:29:@1430.8]
  assign _GEN_190 = _T_106 ? _GEN_142 : _GEN_181; // @[disassembler.scala 362:35:@1360.6]
  assign data_w_3 = _T_352 ? _GEN_125 : _GEN_190; // @[disassembler.scala 353:77:@1346.4]
  assign _GEN_100 = 3'h2 == counter_byte_r ? 1'h0 : _T_111; // @[disassembler.scala 357:32:@1351.10]
  assign _GEN_108 = 3'h2 == counter_byte_r ? 1'h1 : _T_111; // @[disassembler.scala 359:32:@1354.10]
  assign _GEN_116 = _T_356 ? _GEN_100 : _GEN_108; // @[disassembler.scala 356:35:@1350.8]
  assign _GEN_124 = AFIFO_Fire_w ? _GEN_116 : _T_111; // @[disassembler.scala 354:34:@1348.6]
  assign _T_380 = _T_112 == 1'h0; // @[disassembler.scala 372:25:@1387.10]
  assign _GEN_133 = _T_380 ? 1'h0 : 1'h1; // @[disassembler.scala 372:32:@1388.10]
  assign _GEN_141 = AFIFO_Fire_w ? _GEN_133 : _T_111; // @[disassembler.scala 363:34:@1362.8]
  assign _GEN_164 = _T_407 ? _GEN_100 : _GEN_108; // @[disassembler.scala 382:39:@1434.12]
  assign _GEN_172 = AFIFO_Fire_w ? _GEN_164 : _T_111; // @[disassembler.scala 380:34:@1432.10]
  assign _GEN_180 = _T_403 ? _GEN_172 : _T_111; // @[disassembler.scala 379:29:@1430.8]
  assign _GEN_189 = _T_106 ? _GEN_141 : _GEN_180; // @[disassembler.scala 362:35:@1360.6]
  assign data_w_2 = _T_352 ? _GEN_124 : _GEN_189; // @[disassembler.scala 353:77:@1346.4]
  assign _T_128 = {data_w_3,data_w_2}; // @[disassembler.scala 178:17:@1009.8]
  assign _T_129 = {_T_128,_T_127}; // @[disassembler.scala 178:17:@1010.8]
  assign _GEN_103 = 3'h5 == counter_byte_r ? 1'h0 : _T_114; // @[disassembler.scala 357:32:@1351.10]
  assign _GEN_111 = 3'h5 == counter_byte_r ? 1'h1 : _T_114; // @[disassembler.scala 359:32:@1354.10]
  assign _GEN_119 = _T_356 ? _GEN_103 : _GEN_111; // @[disassembler.scala 356:35:@1350.8]
  assign _GEN_127 = AFIFO_Fire_w ? _GEN_119 : _T_114; // @[disassembler.scala 354:34:@1348.6]
  assign _T_395 = _T_115 == 1'h0; // @[disassembler.scala 372:25:@1411.10]
  assign _GEN_136 = _T_395 ? 1'h0 : 1'h1; // @[disassembler.scala 372:32:@1412.10]
  assign _GEN_144 = AFIFO_Fire_w ? _GEN_136 : _T_114; // @[disassembler.scala 363:34:@1362.8]
  assign _GEN_167 = _T_407 ? _GEN_103 : _GEN_111; // @[disassembler.scala 382:39:@1434.12]
  assign _GEN_175 = AFIFO_Fire_w ? _GEN_167 : _T_114; // @[disassembler.scala 380:34:@1432.10]
  assign _GEN_183 = _T_403 ? _GEN_175 : _T_114; // @[disassembler.scala 379:29:@1430.8]
  assign _GEN_192 = _T_106 ? _GEN_144 : _GEN_183; // @[disassembler.scala 362:35:@1360.6]
  assign data_w_5 = _T_352 ? _GEN_127 : _GEN_192; // @[disassembler.scala 353:77:@1346.4]
  assign _GEN_102 = 3'h4 == counter_byte_r ? 1'h0 : _T_113; // @[disassembler.scala 357:32:@1351.10]
  assign _GEN_110 = 3'h4 == counter_byte_r ? 1'h1 : _T_113; // @[disassembler.scala 359:32:@1354.10]
  assign _GEN_118 = _T_356 ? _GEN_102 : _GEN_110; // @[disassembler.scala 356:35:@1350.8]
  assign _GEN_126 = AFIFO_Fire_w ? _GEN_118 : _T_113; // @[disassembler.scala 354:34:@1348.6]
  assign _T_390 = _T_114 == 1'h0; // @[disassembler.scala 372:25:@1403.10]
  assign _GEN_135 = _T_390 ? 1'h0 : 1'h1; // @[disassembler.scala 372:32:@1404.10]
  assign _GEN_143 = AFIFO_Fire_w ? _GEN_135 : _T_113; // @[disassembler.scala 363:34:@1362.8]
  assign _GEN_166 = _T_407 ? _GEN_102 : _GEN_110; // @[disassembler.scala 382:39:@1434.12]
  assign _GEN_174 = AFIFO_Fire_w ? _GEN_166 : _T_113; // @[disassembler.scala 380:34:@1432.10]
  assign _GEN_182 = _T_403 ? _GEN_174 : _T_113; // @[disassembler.scala 379:29:@1430.8]
  assign _GEN_191 = _T_106 ? _GEN_143 : _GEN_182; // @[disassembler.scala 362:35:@1360.6]
  assign data_w_4 = _T_352 ? _GEN_126 : _GEN_191; // @[disassembler.scala 353:77:@1346.4]
  assign _T_130 = {data_w_5,data_w_4}; // @[disassembler.scala 178:17:@1011.8]
  assign _GEN_105 = 3'h7 == counter_byte_r ? 1'h0 : _T_116; // @[disassembler.scala 357:32:@1351.10]
  assign _GEN_113 = 3'h7 == counter_byte_r ? 1'h1 : _T_116; // @[disassembler.scala 359:32:@1354.10]
  assign _GEN_121 = _T_356 ? _GEN_105 : _GEN_113; // @[disassembler.scala 356:35:@1350.8]
  assign _GEN_129 = AFIFO_Fire_w ? _GEN_121 : _T_116; // @[disassembler.scala 354:34:@1348.6]
  assign _GEN_130 = _T_407 ? 1'h0 : 1'h1; // @[disassembler.scala 365:39:@1364.10]
  assign _GEN_138 = AFIFO_Fire_w ? _GEN_130 : _T_116; // @[disassembler.scala 363:34:@1362.8]
  assign _GEN_169 = _T_407 ? _GEN_105 : _GEN_113; // @[disassembler.scala 382:39:@1434.12]
  assign _GEN_177 = AFIFO_Fire_w ? _GEN_169 : _T_116; // @[disassembler.scala 380:34:@1432.10]
  assign _GEN_185 = _T_403 ? _GEN_177 : _T_116; // @[disassembler.scala 379:29:@1430.8]
  assign _GEN_186 = _T_106 ? _GEN_138 : _GEN_185; // @[disassembler.scala 362:35:@1360.6]
  assign data_w_7 = _T_352 ? _GEN_129 : _GEN_186; // @[disassembler.scala 353:77:@1346.4]
  assign _GEN_104 = 3'h6 == counter_byte_r ? 1'h0 : _T_115; // @[disassembler.scala 357:32:@1351.10]
  assign _GEN_112 = 3'h6 == counter_byte_r ? 1'h1 : _T_115; // @[disassembler.scala 359:32:@1354.10]
  assign _GEN_120 = _T_356 ? _GEN_104 : _GEN_112; // @[disassembler.scala 356:35:@1350.8]
  assign _GEN_128 = AFIFO_Fire_w ? _GEN_120 : _T_115; // @[disassembler.scala 354:34:@1348.6]
  assign _T_400 = _T_116 == 1'h0; // @[disassembler.scala 372:25:@1419.10]
  assign _GEN_137 = _T_400 ? 1'h0 : 1'h1; // @[disassembler.scala 372:32:@1420.10]
  assign _GEN_145 = AFIFO_Fire_w ? _GEN_137 : _T_115; // @[disassembler.scala 363:34:@1362.8]
  assign _GEN_168 = _T_407 ? _GEN_104 : _GEN_112; // @[disassembler.scala 382:39:@1434.12]
  assign _GEN_176 = AFIFO_Fire_w ? _GEN_168 : _T_115; // @[disassembler.scala 380:34:@1432.10]
  assign _GEN_184 = _T_403 ? _GEN_176 : _T_115; // @[disassembler.scala 379:29:@1430.8]
  assign _GEN_193 = _T_106 ? _GEN_145 : _GEN_184; // @[disassembler.scala 362:35:@1360.6]
  assign data_w_6 = _T_352 ? _GEN_128 : _GEN_193; // @[disassembler.scala 353:77:@1346.4]
  assign _T_131 = {data_w_7,data_w_6}; // @[disassembler.scala 178:17:@1012.8]
  assign _T_132 = {_T_131,_T_130}; // @[disassembler.scala 178:17:@1013.8]
  assign _T_133 = {_T_132,_T_129}; // @[disassembler.scala 178:17:@1014.8]
  assign _T_134 = _T_133 == Preamble01; // @[disassembler.scala 178:24:@1015.8]
  assign _GEN_7 = _T_134 ? 3'h2 : 3'h1; // @[disassembler.scala 178:39:@1016.8]
  assign _GEN_8 = _T_134 ? 8'h0 : counter_r; // @[disassembler.scala 178:39:@1016.8]
  assign _GEN_9 = _T_134 ? 3'h0 : counter_byte_r; // @[disassembler.scala 178:39:@1016.8]
  assign _T_139 = counter_r == 8'h3; // @[disassembler.scala 186:20:@1028.10]
  assign _T_142 = _T_139 & DMA_Data_Fire_w; // @[disassembler.scala 186:28:@1030.10]
  assign _T_148 = counter_r + 8'h1; // @[disassembler.scala 193:31:@1040.14]
  assign _T_149 = _T_148[7:0]; // @[disassembler.scala 193:31:@1041.14]
  assign _GEN_10 = DMA_Data_Fire_w ? _T_149 : counter_r; // @[disassembler.scala 192:39:@1039.12]
  assign _T_153 = counter_byte_r == 3'h7; // @[disassembler.scala 196:29:@1046.14]
  assign _T_156 = counter_byte_r + 3'h1; // @[disassembler.scala 199:43:@1051.16]
  assign _T_157 = _T_156[2:0]; // @[disassembler.scala 199:43:@1052.16]
  assign _GEN_11 = _T_153 ? 3'h0 : _T_157; // @[disassembler.scala 196:37:@1047.14]
  assign _GEN_12 = AFIFO_Fire_w ? _GEN_11 : counter_byte_r; // @[disassembler.scala 195:36:@1045.12]
  assign _GEN_13 = _T_142 ? 3'h3 : 3'h2; // @[disassembler.scala 186:58:@1031.10]
  assign _GEN_14 = _T_142 ? 8'h0 : _GEN_10; // @[disassembler.scala 186:58:@1031.10]
  assign _GEN_15 = _T_142 ? 3'h0 : _GEN_12; // @[disassembler.scala 186:58:@1031.10]
  assign _T_160 = counter_r == 8'h1; // @[disassembler.scala 204:20:@1061.12]
  assign _T_163 = _T_160 & DMA_Data_Fire_w; // @[disassembler.scala 204:28:@1063.12]
  assign _GEN_19 = _T_163 ? 3'h4 : 3'h3; // @[disassembler.scala 204:58:@1064.12]
  assign _GEN_20 = _T_163 ? 8'h0 : _GEN_10; // @[disassembler.scala 204:58:@1064.12]
  assign _GEN_21 = _T_163 ? 3'h0 : _GEN_12; // @[disassembler.scala 204:58:@1064.12]
  assign _T_181 = PDU_Length_r - 8'h1; // @[disassembler.scala 222:36:@1094.14]
  assign _T_182 = $unsigned(_T_181); // @[disassembler.scala 222:36:@1095.14]
  assign _T_183 = _T_182[7:0]; // @[disassembler.scala 222:36:@1096.14]
  assign _T_184 = counter_r == _T_183; // @[disassembler.scala 222:20:@1097.14]
  assign _T_187 = _T_184 & DMA_Data_Fire_w; // @[disassembler.scala 222:41:@1099.14]
  assign _GEN_25 = _T_187 ? 3'h5 : 3'h4; // @[disassembler.scala 222:71:@1100.14]
  assign _GEN_26 = _T_187 ? 8'h0 : _GEN_10; // @[disassembler.scala 222:71:@1100.14]
  assign _GEN_27 = _T_187 ? 3'h0 : _GEN_12; // @[disassembler.scala 222:71:@1100.14]
  assign _T_205 = counter_r == 8'h2; // @[disassembler.scala 240:20:@1130.16]
  assign _T_208 = _T_205 & DMA_Data_Fire_w; // @[disassembler.scala 240:28:@1132.16]
  assign _GEN_31 = _T_208 ? 3'h6 : 3'h5; // @[disassembler.scala 240:58:@1133.16]
  assign _GEN_32 = _T_208 ? 8'h0 : _GEN_10; // @[disassembler.scala 240:58:@1133.16]
  assign _GEN_33 = _T_208 ? 3'h0 : _GEN_12; // @[disassembler.scala 240:58:@1133.16]
  assign _T_224 = state_r == 3'h6; // @[disassembler.scala 257:22:@1161.16]
  assign _T_229 = io_DMA_Length_o_ready & io_DMA_Flag_AA_o_ready; // @[disassembler.scala 258:44:@1165.18]
  assign _T_232 = _T_229 & io_DMA_Flag_CRC_o_ready; // @[disassembler.scala 258:81:@1167.18]
  assign _GEN_34 = _T_232 ? 3'h0 : 3'h6; // @[disassembler.scala 258:120:@1168.18]
  assign _GEN_35 = _T_224 ? _GEN_34 : 3'h0; // @[disassembler.scala 257:36:@1162.16]
  assign _GEN_36 = _T_351 ? _GEN_31 : _GEN_35; // @[disassembler.scala 239:30:@1129.14]
  assign _GEN_37 = _T_351 ? _GEN_32 : counter_r; // @[disassembler.scala 239:30:@1129.14]
  assign _GEN_38 = _T_351 ? _GEN_33 : counter_byte_r; // @[disassembler.scala 239:30:@1129.14]
  assign _GEN_39 = _T_349 ? _GEN_25 : _GEN_36; // @[disassembler.scala 221:38:@1093.12]
  assign _GEN_40 = _T_349 ? _GEN_26 : _GEN_37; // @[disassembler.scala 221:38:@1093.12]
  assign _GEN_41 = _T_349 ? _GEN_27 : _GEN_38; // @[disassembler.scala 221:38:@1093.12]
  assign _GEN_42 = _T_348 ? _GEN_19 : _GEN_39; // @[disassembler.scala 203:37:@1060.10]
  assign _GEN_43 = _T_348 ? _GEN_20 : _GEN_40; // @[disassembler.scala 203:37:@1060.10]
  assign _GEN_44 = _T_348 ? _GEN_21 : _GEN_41; // @[disassembler.scala 203:37:@1060.10]
  assign _GEN_45 = _T_403 ? _GEN_13 : _GEN_42; // @[disassembler.scala 185:29:@1027.8]
  assign _GEN_46 = _T_403 ? _GEN_14 : _GEN_43; // @[disassembler.scala 185:29:@1027.8]
  assign _GEN_47 = _T_403 ? _GEN_15 : _GEN_44; // @[disassembler.scala 185:29:@1027.8]
  assign _GEN_48 = _T_106 ? _GEN_7 : _GEN_45; // @[disassembler.scala 177:35:@1007.6]
  assign _GEN_49 = _T_106 ? _GEN_8 : _GEN_46; // @[disassembler.scala 177:35:@1007.6]
  assign _GEN_50 = _T_106 ? _GEN_9 : _GEN_47; // @[disassembler.scala 177:35:@1007.6]
  assign _GEN_51 = _T_105 ? 8'h0 : PDU_Length_r; // @[disassembler.scala 165:25:@990.4]
  assign _GEN_52 = _T_105 ? 1'h0 : PDU_Length_Valid_r; // @[disassembler.scala 165:25:@990.4]
  assign _GEN_53 = _T_105 ? 1'h0 : Flag_AA_r; // @[disassembler.scala 165:25:@990.4]
  assign _GEN_54 = _T_105 ? 1'h0 : Flag_AA_Valid_r; // @[disassembler.scala 165:25:@990.4]
  assign _GEN_55 = _T_105 ? 1'h0 : Flag_CRC_r; // @[disassembler.scala 165:25:@990.4]
  assign _GEN_56 = _T_105 ? 1'h0 : Flag_CRC_Valid_r; // @[disassembler.scala 165:25:@990.4]
  assign state_w = _T_105 ? _GEN_6 : _GEN_48; // @[disassembler.scala 165:25:@990.4]
  assign counter_w = _T_105 ? counter_r : _GEN_49; // @[disassembler.scala 165:25:@990.4]
  assign counter_byte_w = _T_105 ? counter_byte_r : _GEN_50; // @[disassembler.scala 165:25:@990.4]
  assign _T_236 = _T_348 & _T_160; // @[disassembler.scala 269:31:@1180.4]
  assign _T_239 = _T_236 & DMA_Data_Fire_w; // @[disassembler.scala 269:52:@1182.4]
  assign _T_241 = state_w == 3'h0; // @[disassembler.scala 272:22:@1188.6]
  assign _GEN_60 = _T_241 ? 1'h0 : _GEN_52; // @[disassembler.scala 272:32:@1189.6]
  assign _GEN_61 = _T_239 ? data_r : _GEN_51; // @[disassembler.scala 269:82:@1183.4]
  assign _GEN_62 = _T_239 ? 1'h1 : _GEN_60; // @[disassembler.scala 269:82:@1183.4]
  assign _T_245 = counter_r == 8'h0; // @[disassembler.scala 279:36:@1195.4]
  assign _T_246 = _T_403 & _T_245; // @[disassembler.scala 279:23:@1196.4]
  assign _T_249 = _T_246 & DMA_Data_Fire_w; // @[disassembler.scala 279:44:@1198.4]
  assign _T_250 = io_REG_AA_i[7:0]; // @[disassembler.scala 280:32:@1200.6]
  assign _T_251 = data_r != _T_250; // @[disassembler.scala 280:17:@1201.6]
  assign _GEN_63 = _T_251 ? 1'h1 : _GEN_53; // @[disassembler.scala 280:38:@1202.6]
  assign _GEN_64 = _T_251 ? 1'h1 : _GEN_54; // @[disassembler.scala 280:38:@1202.6]
  assign _T_257 = _T_403 & _T_160; // @[disassembler.scala 284:29:@1210.6]
  assign _T_260 = _T_257 & DMA_Data_Fire_w; // @[disassembler.scala 284:50:@1212.6]
  assign _T_261 = io_REG_AA_i[15:8]; // @[disassembler.scala 285:32:@1214.8]
  assign _T_262 = data_r != _T_261; // @[disassembler.scala 285:17:@1215.8]
  assign _GEN_65 = _T_262 ? 1'h1 : _GEN_53; // @[disassembler.scala 285:39:@1216.8]
  assign _GEN_66 = _T_262 ? 1'h1 : _GEN_54; // @[disassembler.scala 285:39:@1216.8]
  assign _T_268 = _T_403 & _T_205; // @[disassembler.scala 289:29:@1224.8]
  assign _T_271 = _T_268 & DMA_Data_Fire_w; // @[disassembler.scala 289:50:@1226.8]
  assign _T_272 = io_REG_AA_i[23:16]; // @[disassembler.scala 290:32:@1228.10]
  assign _T_273 = data_r != _T_272; // @[disassembler.scala 290:17:@1229.10]
  assign _GEN_67 = _T_273 ? 1'h1 : _GEN_53; // @[disassembler.scala 290:40:@1230.10]
  assign _GEN_68 = _T_273 ? 1'h1 : _GEN_54; // @[disassembler.scala 290:40:@1230.10]
  assign _T_279 = _T_403 & _T_139; // @[disassembler.scala 294:29:@1238.10]
  assign _T_282 = _T_279 & DMA_Data_Fire_w; // @[disassembler.scala 294:50:@1240.10]
  assign _T_283 = io_REG_AA_i[31:24]; // @[disassembler.scala 295:32:@1242.12]
  assign _T_284 = data_r != _T_283; // @[disassembler.scala 295:17:@1243.12]
  assign _GEN_69 = _T_284 ? 1'h1 : _GEN_53; // @[disassembler.scala 295:40:@1244.12]
  assign _GEN_71 = _T_282 ? _GEN_69 : _GEN_53; // @[disassembler.scala 294:80:@1241.10]
  assign _GEN_72 = _T_282 ? 1'h1 : _GEN_54; // @[disassembler.scala 294:80:@1241.10]
  assign _GEN_73 = _T_271 ? _GEN_67 : _GEN_71; // @[disassembler.scala 289:80:@1227.8]
  assign _GEN_74 = _T_271 ? _GEN_68 : _GEN_72; // @[disassembler.scala 289:80:@1227.8]
  assign _GEN_75 = _T_260 ? _GEN_65 : _GEN_73; // @[disassembler.scala 284:80:@1213.6]
  assign _GEN_76 = _T_260 ? _GEN_66 : _GEN_74; // @[disassembler.scala 284:80:@1213.6]
  assign _GEN_77 = _T_249 ? _GEN_63 : _GEN_75; // @[disassembler.scala 279:74:@1199.4]
  assign _GEN_78 = _T_249 ? _GEN_64 : _GEN_76; // @[disassembler.scala 279:74:@1199.4]
  assign _T_291 = _T_351 & _T_245; // @[disassembler.scala 306:24:@1256.4]
  assign _T_294 = _T_291 & DMA_Data_Fire_w; // @[disassembler.scala 306:45:@1258.4]
  assign CRC_Result_w = CRC_inst_io_result_bits; // @[disassembler.scala 101:26:@924.4]
  assign _T_295 = CRC_Result_w[7:0]; // @[disassembler.scala 307:33:@1260.6]
  assign _T_296 = data_r != _T_295; // @[disassembler.scala 307:17:@1261.6]
  assign _GEN_79 = _T_296 ? 1'h1 : _GEN_55; // @[disassembler.scala 307:39:@1262.6]
  assign _GEN_80 = _T_296 ? 1'h1 : _GEN_56; // @[disassembler.scala 307:39:@1262.6]
  assign _T_302 = _T_351 & _T_160; // @[disassembler.scala 311:30:@1270.6]
  assign _T_305 = _T_302 & DMA_Data_Fire_w; // @[disassembler.scala 311:51:@1272.6]
  assign _T_306 = CRC_Result_w[15:8]; // @[disassembler.scala 312:33:@1274.8]
  assign _T_307 = data_r != _T_306; // @[disassembler.scala 312:17:@1275.8]
  assign _GEN_81 = _T_307 ? 1'h1 : _GEN_55; // @[disassembler.scala 312:40:@1276.8]
  assign _GEN_82 = _T_307 ? 1'h1 : _GEN_56; // @[disassembler.scala 312:40:@1276.8]
  assign _T_313 = _T_351 & _T_205; // @[disassembler.scala 316:30:@1284.8]
  assign _T_316 = _T_313 & DMA_Data_Fire_w; // @[disassembler.scala 316:51:@1286.8]
  assign _T_317 = CRC_Result_w[23:16]; // @[disassembler.scala 317:33:@1288.10]
  assign _T_318 = data_r != _T_317; // @[disassembler.scala 317:17:@1289.10]
  assign _GEN_83 = _T_318 ? 1'h1 : _GEN_55; // @[disassembler.scala 317:41:@1290.10]
  assign _GEN_85 = _T_316 ? _GEN_83 : _GEN_55; // @[disassembler.scala 316:81:@1287.8]
  assign _GEN_86 = _T_316 ? 1'h1 : _GEN_56; // @[disassembler.scala 316:81:@1287.8]
  assign _GEN_87 = _T_305 ? _GEN_81 : _GEN_85; // @[disassembler.scala 311:81:@1273.6]
  assign _GEN_88 = _T_305 ? _GEN_82 : _GEN_86; // @[disassembler.scala 311:81:@1273.6]
  assign _GEN_89 = _T_294 ? _GEN_79 : _GEN_87; // @[disassembler.scala 306:75:@1259.4]
  assign _GEN_90 = _T_294 ? _GEN_80 : _GEN_88; // @[disassembler.scala 306:75:@1259.4]
  assign _T_330 = _T_153 & AFIFO_Fire_w; // @[disassembler.scala 332:33:@1309.6]
  assign _GEN_91 = DMA_Data_Fire_w ? 1'h0 : DMA_Data_Valid_r; // @[disassembler.scala 334:43:@1315.8]
  assign _GEN_92 = _T_330 ? 1'h1 : _GEN_91; // @[disassembler.scala 332:60:@1310.6]
  assign _GEN_93 = _T_107 ? 1'h0 : _GEN_92; // @[disassembler.scala 329:49:@1303.4]
  assign _GEN_94 = DMA_Data_Fire_w ? 1'h1 : AFIFO_Ready_r; // @[disassembler.scala 347:43:@1337.10]
  assign _GEN_95 = _T_330 ? 1'h0 : _GEN_94; // @[disassembler.scala 345:60:@1332.8]
  assign _GEN_96 = _T_106 ? 1'h1 : _GEN_95; // @[disassembler.scala 342:35:@1325.6]
  assign _GEN_97 = _T_105 ? 1'h0 : _GEN_96; // @[disassembler.scala 340:25:@1320.4]
  assign CRC_Data_w = _T_350 ? DEWHITE_Result_w : 1'h0; // @[disassembler.scala 394:58:@1449.4]
  assign CRC_Valid_w = _T_350 ? AFIFO_Fire_w : 1'h0; // @[disassembler.scala 394:58:@1449.4]
  assign DEWHITE_Data_w = _T_352 ? io_AFIFO_Data_i_bits : 1'h0; // @[disassembler.scala 406:77:@1465.4]
  assign DEWHITE_Valid_w = _T_352 ? AFIFO_Fire_w : 1'h0; // @[disassembler.scala 406:77:@1465.4]
  assign io_DMA_Data_o_valid = DMA_Data_Valid_r;
  assign io_DMA_Data_o_bits = _GEN_5;
  assign io_DMA_Length_o_valid = PDU_Length_Valid_r;
  assign io_DMA_Length_o_bits = PDU_Length_r;
  assign io_DMA_Flag_AA_o_valid = Flag_AA_Valid_r;
  assign io_DMA_Flag_AA_o_bits = Flag_AA_r;
  assign io_DMA_Flag_CRC_o_valid = Flag_CRC_Valid_r;
  assign io_DMA_Flag_CRC_o_bits = Flag_CRC_r;
  assign io_AFIFO_Data_i_ready = AFIFO_Ready_r;
  assign CRC_inst_clock = clock;
  assign CRC_inst_reset = reset;
  assign CRC_inst_io_operand_valid = CRC_Valid_w;
  assign CRC_inst_io_operand_bits = CRC_Data_w;
  assign CRC_inst_io_seed = io_REG_CRC_Seed_i;
  assign CRC_inst_io_init = _T_105;
  assign WHITE_inst_clock = clock;
  assign WHITE_inst_reset = reset;
  assign WHITE_inst_io_operand_valid = DEWHITE_Valid_w;
  assign WHITE_inst_io_operand_bits = DEWHITE_Data_w;
  assign WHITE_inst_io_seed = io_REG_DeWhite_Seed_i;
  assign WHITE_inst_io_init = _T_105;
`ifdef RANDOMIZE
  integer initvar;
  initial begin
    `ifndef verilator
      #0.002 begin end
    `endif
  `ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{$random}};
  state_r = _RAND_0[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{$random}};
  counter_r = _RAND_1[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{$random}};
  counter_byte_r = _RAND_2[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{$random}};
  PDU_Length_r = _RAND_3[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{$random}};
  PDU_Length_Valid_r = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{$random}};
  Flag_AA_r = _RAND_5[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{$random}};
  Flag_AA_Valid_r = _RAND_6[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{$random}};
  Flag_CRC_r = _RAND_7[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_8 = {1{$random}};
  Flag_CRC_Valid_r = _RAND_8[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_9 = {1{$random}};
  DMA_Data_Valid_r = _RAND_9[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_10 = {1{$random}};
  AFIFO_Ready_r = _RAND_10[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_11 = {1{$random}};
  data_r = _RAND_11[7:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge clock) begin
    if (reset) begin
      state_r <= 3'h0;
    end else begin
      if (_T_105) begin
        if (io_DMA_Switch_i) begin
          state_r <= 3'h1;
        end else begin
          state_r <= 3'h0;
        end
      end else begin
        if (_T_106) begin
          if (_T_134) begin
            state_r <= 3'h2;
          end else begin
            state_r <= 3'h1;
          end
        end else begin
          if (_T_403) begin
            if (_T_142) begin
              state_r <= 3'h3;
            end else begin
              state_r <= 3'h2;
            end
          end else begin
            if (_T_348) begin
              if (_T_163) begin
                state_r <= 3'h4;
              end else begin
                state_r <= 3'h3;
              end
            end else begin
              if (_T_349) begin
                if (_T_187) begin
                  state_r <= 3'h5;
                end else begin
                  state_r <= 3'h4;
                end
              end else begin
                if (_T_351) begin
                  if (_T_208) begin
                    state_r <= 3'h6;
                  end else begin
                    state_r <= 3'h5;
                  end
                end else begin
                  if (_T_224) begin
                    if (_T_232) begin
                      state_r <= 3'h0;
                    end else begin
                      state_r <= 3'h6;
                    end
                  end else begin
                    state_r <= 3'h0;
                  end
                end
              end
            end
          end
        end
      end
    end
    if (reset) begin
      counter_r <= 8'h0;
    end else begin
      if (!(_T_105)) begin
        if (_T_106) begin
          if (_T_134) begin
            counter_r <= 8'h0;
          end
        end else begin
          if (_T_403) begin
            if (_T_142) begin
              counter_r <= 8'h0;
            end else begin
              if (DMA_Data_Fire_w) begin
                counter_r <= _T_149;
              end
            end
          end else begin
            if (_T_348) begin
              if (_T_163) begin
                counter_r <= 8'h0;
              end else begin
                if (DMA_Data_Fire_w) begin
                  counter_r <= _T_149;
                end
              end
            end else begin
              if (_T_349) begin
                if (_T_187) begin
                  counter_r <= 8'h0;
                end else begin
                  if (DMA_Data_Fire_w) begin
                    counter_r <= _T_149;
                  end
                end
              end else begin
                if (_T_351) begin
                  if (_T_208) begin
                    counter_r <= 8'h0;
                  end else begin
                    if (DMA_Data_Fire_w) begin
                      counter_r <= _T_149;
                    end
                  end
                end
              end
            end
          end
        end
      end
    end
    if (reset) begin
      counter_byte_r <= 3'h0;
    end else begin
      if (!(_T_105)) begin
        if (_T_106) begin
          if (_T_134) begin
            counter_byte_r <= 3'h0;
          end
        end else begin
          if (_T_403) begin
            if (_T_142) begin
              counter_byte_r <= 3'h0;
            end else begin
              if (AFIFO_Fire_w) begin
                if (_T_153) begin
                  counter_byte_r <= 3'h0;
                end else begin
                  counter_byte_r <= _T_157;
                end
              end
            end
          end else begin
            if (_T_348) begin
              if (_T_163) begin
                counter_byte_r <= 3'h0;
              end else begin
                if (AFIFO_Fire_w) begin
                  if (_T_153) begin
                    counter_byte_r <= 3'h0;
                  end else begin
                    counter_byte_r <= _T_157;
                  end
                end
              end
            end else begin
              if (_T_349) begin
                if (_T_187) begin
                  counter_byte_r <= 3'h0;
                end else begin
                  if (AFIFO_Fire_w) begin
                    if (_T_153) begin
                      counter_byte_r <= 3'h0;
                    end else begin
                      counter_byte_r <= _T_157;
                    end
                  end
                end
              end else begin
                if (_T_351) begin
                  if (_T_208) begin
                    counter_byte_r <= 3'h0;
                  end else begin
                    if (AFIFO_Fire_w) begin
                      if (_T_153) begin
                        counter_byte_r <= 3'h0;
                      end else begin
                        counter_byte_r <= _T_157;
                      end
                    end
                  end
                end
              end
            end
          end
        end
      end
    end
    if (reset) begin
      PDU_Length_r <= 8'h0;
    end else begin
      if (_T_239) begin
        PDU_Length_r <= data_r;
      end else begin
        if (_T_105) begin
          PDU_Length_r <= 8'h0;
        end
      end
    end
    if (reset) begin
      PDU_Length_Valid_r <= 1'h0;
    end else begin
      if (_T_239) begin
        PDU_Length_Valid_r <= 1'h1;
      end else begin
        if (_T_241) begin
          PDU_Length_Valid_r <= 1'h0;
        end else begin
          if (_T_105) begin
            PDU_Length_Valid_r <= 1'h0;
          end
        end
      end
    end
    if (reset) begin
      Flag_AA_r <= 1'h0;
    end else begin
      if (_T_249) begin
        if (_T_251) begin
          Flag_AA_r <= 1'h1;
        end else begin
          if (_T_105) begin
            Flag_AA_r <= 1'h0;
          end
        end
      end else begin
        if (_T_260) begin
          if (_T_262) begin
            Flag_AA_r <= 1'h1;
          end else begin
            if (_T_105) begin
              Flag_AA_r <= 1'h0;
            end
          end
        end else begin
          if (_T_271) begin
            if (_T_273) begin
              Flag_AA_r <= 1'h1;
            end else begin
              if (_T_105) begin
                Flag_AA_r <= 1'h0;
              end
            end
          end else begin
            if (_T_282) begin
              if (_T_284) begin
                Flag_AA_r <= 1'h1;
              end else begin
                if (_T_105) begin
                  Flag_AA_r <= 1'h0;
                end
              end
            end else begin
              Flag_AA_r <= _GEN_53;
            end
          end
        end
      end
    end
    if (reset) begin
      Flag_AA_Valid_r <= 1'h0;
    end else begin
      if (_T_249) begin
        if (_T_251) begin
          Flag_AA_Valid_r <= 1'h1;
        end else begin
          if (_T_105) begin
            Flag_AA_Valid_r <= 1'h0;
          end
        end
      end else begin
        if (_T_260) begin
          if (_T_262) begin
            Flag_AA_Valid_r <= 1'h1;
          end else begin
            if (_T_105) begin
              Flag_AA_Valid_r <= 1'h0;
            end
          end
        end else begin
          if (_T_271) begin
            if (_T_273) begin
              Flag_AA_Valid_r <= 1'h1;
            end else begin
              if (_T_105) begin
                Flag_AA_Valid_r <= 1'h0;
              end
            end
          end else begin
            if (_T_282) begin
              Flag_AA_Valid_r <= 1'h1;
            end else begin
              if (_T_105) begin
                Flag_AA_Valid_r <= 1'h0;
              end
            end
          end
        end
      end
    end
    if (reset) begin
      Flag_CRC_r <= 1'h0;
    end else begin
      if (_T_294) begin
        if (_T_296) begin
          Flag_CRC_r <= 1'h1;
        end else begin
          if (_T_105) begin
            Flag_CRC_r <= 1'h0;
          end
        end
      end else begin
        if (_T_305) begin
          if (_T_307) begin
            Flag_CRC_r <= 1'h1;
          end else begin
            if (_T_105) begin
              Flag_CRC_r <= 1'h0;
            end
          end
        end else begin
          if (_T_316) begin
            if (_T_318) begin
              Flag_CRC_r <= 1'h1;
            end else begin
              if (_T_105) begin
                Flag_CRC_r <= 1'h0;
              end
            end
          end else begin
            if (_T_105) begin
              Flag_CRC_r <= 1'h0;
            end
          end
        end
      end
    end
    if (reset) begin
      Flag_CRC_Valid_r <= 1'h0;
    end else begin
      if (_T_294) begin
        if (_T_296) begin
          Flag_CRC_Valid_r <= 1'h1;
        end else begin
          if (_T_105) begin
            Flag_CRC_Valid_r <= 1'h0;
          end
        end
      end else begin
        if (_T_305) begin
          if (_T_307) begin
            Flag_CRC_Valid_r <= 1'h1;
          end else begin
            if (_T_105) begin
              Flag_CRC_Valid_r <= 1'h0;
            end
          end
        end else begin
          if (_T_316) begin
            Flag_CRC_Valid_r <= 1'h1;
          end else begin
            if (_T_105) begin
              Flag_CRC_Valid_r <= 1'h0;
            end
          end
        end
      end
    end
    if (reset) begin
      DMA_Data_Valid_r <= 1'h0;
    end else begin
      if (_T_107) begin
        DMA_Data_Valid_r <= 1'h0;
      end else begin
        if (_T_330) begin
          DMA_Data_Valid_r <= 1'h1;
        end else begin
          if (DMA_Data_Fire_w) begin
            DMA_Data_Valid_r <= 1'h0;
          end
        end
      end
    end
    if (reset) begin
      AFIFO_Ready_r <= 1'h0;
    end else begin
      if (_T_105) begin
        AFIFO_Ready_r <= 1'h0;
      end else begin
        if (_T_106) begin
          AFIFO_Ready_r <= 1'h1;
        end else begin
          if (_T_330) begin
            AFIFO_Ready_r <= 1'h0;
          end else begin
            if (DMA_Data_Fire_w) begin
              AFIFO_Ready_r <= 1'h1;
            end
          end
        end
      end
    end
    if (reset) begin
      data_r <= 8'h0;
    end else begin
      data_r <= _T_133;
    end
  end
endmodule
