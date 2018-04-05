package PacketAssembler_test

import PacketAssembler._
import chisel3._
import chisel3.util._
import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}


class PacketDisAssemblerTest(c: PacketDisAssembler) extends PeekPokeTester(c) {
//IO reference
    /*
    //DMA, REG
    val DMA_Switch_i = Input(Bool())
    val REG_AA_i = Input(UInt(32.W))   
    val REG_CRC_Seed_i = Input(UInt(24.W))
    val REG_DeWhite_Seed_i = Input(UInt(7.W))

    val DMA_Data_o = DecoupledIO(UInt(8.W))//decouple(sink): data, puch, full
    val DMA_Length_o = Decoupled(UInt(8.W))
    val DMA_Flag_AA_o = Decoupled(Bool())
    val DMA_Flag_CRC_o = Decoupled(Bool())     

    //AFIFO
    val AFIFO_Data_i = Flipped(DecoupledIO(UInt(1.W)))//decouple(source): data, pop, empty*/

//scala declaration
/*
	//hard-coded arbitrary test
	val wholepacket = "hefefee_eeffefefeffefe_f8ef_01101001_55".U//AA first bit: 1; length:7; total length: 26(13 bytes); wrong CRC
	val wholepacket_neg = "h101011_11001010100101_0710_feefeffe_aa".U
	val AA = "h01100001".U//a wrong one
	//val preamble = "b01010101".U
	//val CRC = "h101001".U
	val random_sequence = "hf7293742343023801b".U//72 bits//note not to poke preamble here
*/
	//hard-coded David's test
	//LSB-MSB
	val wholepacket_dig = "b01101011011111011001000101110001_0100000000001000_011000110000000101001100010011100100000000000000_01000000100000001010000001100000000100001100101011000010101010101011001011001100".U
	val wholepacket_rad = "b01101011011111011001000101110001_1111000101000011_100010011000010011110000101010110010011000001101_11101110000011000010100001110010011110010010010011011101011011011101000001011011".U
	//---------------------_--------------AA----------------_---PDU header---_-----------PDU advertiser address---------------_-----------------PDU data1 and data2--------------------------------------------
	val pre_preamble = "b000111".U//pre-preamble
	val preamble = "b01010101".U
	val AA = "b01101011011111011001000101110001".U
	val CRC_rad = "b001101000100101101101110".U

	//MSB-LSB
	val wholepacket_dig_rev = "b00110011010011010101010101000011010100110000100000000110000001010000000100000010000000000000001001110010001100101000000011000110000100000000001010001110100010011011111011010110".U
	val wholepacket_rad_rev = "b11011010000010111011011010111011001001001001111001001110000101000011000001110111101100000110010011010101000011110010000110010001110000101000111110001110100010011011111011010110".U
	
	val random_sequence_rev = "hf7293742343023801b".U//72 bits//note not to poke preamble here	
	val pre_preamble_rev = "b111000".U
	val preamble_rev = "b10101010".U
	val AA_rev = "b10001110100010011011111011010110".U
	val CRC_rad_rev = "b011101101101001000101100".U

	val CRC_Seed = "b010101010101010101010101".U
	val DeWhite_Seed = "b1100101".U

//reset
	reset(1)

//throughout packet
	poke(c.io.REG_AA_i, AA_rev)
	poke(c.io.REG_CRC_Seed_i, CRC_Seed)
	poke(c.io.REG_DeWhite_Seed_i, DeWhite_Seed)

//initialize
	poke(c.io.DMA_Switch_i,false.B)

	poke(c.io.DMA_Data_o.ready,false.B)
	poke(c.io.DMA_Length_o.ready,false.B)
	poke(c.io.DMA_Flag_AA_o.ready,false.B)
	poke(c.io.DMA_Flag_CRC_o.ready,false.B)	

	poke(c.io.AFIFO_Data_i.valid,false.B)
	poke(c.io.AFIFO_Data_i.bits,0.U)

	step(2)

//start of receiving packet
	poke(c.io.DMA_Switch_i,true.B)

	poke(c.io.DMA_Data_o.ready,true.B)
	poke(c.io.DMA_Length_o.ready,true.B)
	poke(c.io.DMA_Flag_AA_o.ready,true.B)
	poke(c.io.DMA_Flag_CRC_o.ready,true.B)	

	poke(c.io.AFIFO_Data_i.valid,false.B)
	poke(c.io.AFIFO_Data_i.bits,0.U)


//random sequence before pre_preamble
	var j:Int = 0
	for(j<-0 to 71){ 	
		poke(c.io.AFIFO_Data_i.valid,true.B)
		poke(c.io.AFIFO_Data_i.bits,random_sequence_rev(j))
   		step(1)
	}

//pre_preamble
	for(j<-0 to 5){ 	
		poke(c.io.AFIFO_Data_i.valid,true.B)
		poke(c.io.AFIFO_Data_i.bits,pre_preamble_rev(j))
   		step(1)
	}
	expect(c.io.DMA_Data_o.bits, 0.U)//note
	expect(c.io.DMA_Data_o.valid, false.B)//note
	println(s"after random_sequence\n${peek(c.io.DMA_Data_o.bits)}\t0.U")

//PREAMBLE
	for(j<-0 to 7){
		poke(c.io.AFIFO_Data_i.valid,true.B)
		poke(c.io.AFIFO_Data_i.bits,preamble_rev(j))
   		step(1)	
	}
	expect(c.io.DMA_Data_o.valid, false.B)//note
	println(s"after preamble\n${peek(c.io.DMA_Data_o.valid)}\tfalse")

//AA
	for(j<-0 to 31){
		poke(c.io.AFIFO_Data_i.valid,true.B)
		poke(c.io.AFIFO_Data_i.bits,wholepacket_rad_rev(j))

		step(1)
		poke(c.io.AFIFO_Data_i.valid,false.B)		
		if(j%8==7){
			//poke(c.io.DMA_Data_o.ready,true.B)
   			expect(c.io.DMA_Data_o.bits, wholepacket_dig_rev((j/8)*8+7,(j/8)*8))
   			expect(c.io.DMA_Data_o.valid, true.B)	
   			println(s"j="+j+s"\n${peek(c.io.DMA_Data_o.bits)}\t${peek(wholepacket_dig_rev((j/8)*8+7,(j/8)*8))}")
		}
   		//println(s"j="+j+s"\n${peek(c.io.AFIFO_Data_o.bits)}\t${peek(wholepacket(j))}")
   		step(1)
 		//poke(c.io.DMA_Data_o.ready,false.B) 				
	}
	step(1)
	expect(c.io.DMA_Flag_AA_o.bits,false.B)
	expect(c.io.DMA_Flag_AA_o.valid,true.B)
   	println(s"j=flagAA\n${peek(c.io.DMA_Flag_AA_o.bits)}\ttrue")
   	println(s"j=flagAA\n${peek(c.io.DMA_Flag_AA_o.valid)}\ttrue")

//PDU_HEADER
	for(j<-32 to 47){
		poke(c.io.AFIFO_Data_i.valid,true.B)
		poke(c.io.AFIFO_Data_i.bits,wholepacket_rad_rev(j))

		step(1)
		poke(c.io.AFIFO_Data_i.valid,false.B)		
		if(j%8==7){
			//poke(c.io.DMA_Data_o.ready,true.B)
   			expect(c.io.DMA_Data_o.bits, wholepacket_dig_rev((j/8)*8+7,(j/8)*8))
   			expect(c.io.DMA_Data_o.valid, true.B)	
   			//println(s"j="+j+s"\n${peek(c.io.DMA_Data_o.bits)}\t${peek(wholepacket((j/8)*8+7,(j/8)*8))}")
		}
   		//println(s"j="+j+s"\n${peek(c.io.AFIFO_Data_o.bits)}\t${peek(wholepacket(j))}")
   		step(1)
 		//poke(c.io.DMA_Data_o.ready,false.B) 				
	}
	//step(1)
	expect(c.io.DMA_Length_o.bits,16.U)
	expect(c.io.DMA_Length_o.valid,true.B)
   	println(s"j=DMA_Length_o\n${peek(c.io.DMA_Length_o.bits)}\t16.U")

	step(1)
	poke(c.io.DMA_Length_o.ready,false.B)

//PDU_PAYLOAD
	for(j<-48 to 22*8-1){
		poke(c.io.AFIFO_Data_i.valid,true.B)
		poke(c.io.AFIFO_Data_i.bits,wholepacket_rad_rev(j))

		step(1)
		poke(c.io.AFIFO_Data_i.valid,false.B)		
		if(j%8==7){
			//poke(c.io.DMA_Data_o.ready,true.B)
   			expect(c.io.DMA_Data_o.bits, wholepacket_dig_rev((j/8)*8+7,(j/8)*8))
   			expect(c.io.DMA_Data_o.valid, true.B)	
   			println(s"j="+j+s"\n${peek(c.io.DMA_Data_o.bits)}\t${peek(wholepacket_dig_rev((j/8)*8+7,(j/8)*8))}")
		}
   		//println(s"j="+j+s"\n${peek(c.io.AFIFO_Data_o.bits)}\t${peek(wholepacket(j))}")
   		step(1)
 		//poke(c.io.DMA_Data_o.ready,false.B) 				
	}

//CRC
	for(j<-0 to 23){
		poke(c.io.AFIFO_Data_i.valid,true.B)
		poke(c.io.AFIFO_Data_i.bits,CRC_rad_rev(j))

		step(1)
		poke(c.io.AFIFO_Data_i.valid,false.B)		
   		//println(s"j="+j+s"\n${peek(c.io.AFIFO_Data_o.bits)}\t${peek(wholepacket(j))}")
   		step(1)
 		//poke(c.io.DMA_Data_o.ready,false.B) 				
	}

	step(2)
	expect(c.io.DMA_Flag_CRC_o.bits,false.B)
	expect(c.io.DMA_Flag_CRC_o.valid,true.B)
   	println(s"j=flagCRC_bits\n${peek(c.io.DMA_Flag_CRC_o.bits)}\tfalse")
   	println(s"j=flagCRC_valid\n${peek(c.io.DMA_Flag_CRC_o.valid)}\ttrue")

	step(2)

	poke(c.io.DMA_Flag_AA_o.ready,false.B)
	poke(c.io.DMA_Flag_CRC_o.ready,false.B)




//todo: add FIFO
//todo: add invalid DMA
//todo: check output: DMA_ready

//todo: AA, CRC correct
//todo: DMA_Switch_i OFF
//todo: ready, valid always ON
}

class PacketDisAssemblerTester extends ChiselFlatSpec {
	behavior of "PacketDisAssembler"
	backends foreach {backend =>
		it should s"perform correct operation as an packet disassembler $backend" in { 
			Driver(() => new PacketDisAssembler, "verilator") { 
				(c) => new PacketDisAssemblerTest(c)} should be (true)
			}
		}
}