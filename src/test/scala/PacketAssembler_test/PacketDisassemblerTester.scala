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
	val wholepacket = "hefefee_eeffefefeffefe_f8ef_01101001_55".U//AA first bit: 1; length:7; total length: 26(13 bytes); wrong CRC
	val wholepacket_neg = "h101011_11001010100101_0710_feefeffe_aa".U
	val AA = "h01100001".U//a wrong one
	//val preamble = "b01010101".U
	//val CRC = "h101001".U
	val random_sequence = "hf7293742343023801b".U//72 bits//note not to poke preamble here

//reset
	//reset(3)

//throughout packet
	poke(c.io.REG_AA_i, AA)
	poke(c.io.REG_CRC_Seed_i,"h00000f".U)
	poke(c.io.REG_DeWhite_Seed_i,"b0001100".U)

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

	poke(c.io.DMA_Data_o.ready,false.B)
	poke(c.io.DMA_Length_o.ready,true.B)
	poke(c.io.DMA_Flag_AA_o.ready,true.B)
	poke(c.io.DMA_Flag_CRC_o.ready,true.B)	

	poke(c.io.AFIFO_Data_i.valid,false.B)
	poke(c.io.AFIFO_Data_i.bits,0.U)


//random sequence before preamble
	var j:Int = 0
	for(j<-0 to 71){
		poke(c.io.AFIFO_Data_i.valid,false.B)//need to test two ready  	
		//step(Random_Num(1,100))
		step(1)
		poke(c.io.AFIFO_Data_i.valid,true.B)
		poke(c.io.AFIFO_Data_i.bits,random_sequence(j))
   		step(1)
	}
	expect(c.io.DMA_Data_o.bits, 0.U)//note
	println(s"after random_sequence\n${peek(c.io.DMA_Data_o.bits)}\t0.U")
	//step(Random_Num(8,100))
//PREAMBLE
	for(j<-0 to 7){
		//step(Random_Num(1,100))
		poke(c.io.AFIFO_Data_i.valid,true.B)
		poke(c.io.AFIFO_Data_i.bits,wholepacket(j))
		//println(s"after preamble\n${peek(c.io.DMA_Data_o.bits)}\t85")
   		step(1)
   		//poke(c.io.AFIFO_Data_i.valid,false.B)//need to test two ready  	
	}
	println(s"after preamble\n${peek(c.io.DMA_Data_o.bits)}\t85")
//AA
	for(j<-8 to 39){
		poke(c.io.AFIFO_Data_i.valid,true.B)
		poke(c.io.AFIFO_Data_i.bits,wholepacket(j))

		//println(s"${(j/8)*8}")
		//step(Random_Num(2,100))//minimun for DMA_fire: 2

		step(1)
		poke(c.io.AFIFO_Data_i.valid,false.B)		
		if(j%8==7){
			poke(c.io.DMA_Data_o.ready,true.B)
   			expect(c.io.DMA_Data_o.bits, wholepacket((j/8)*8+7,(j/8)*8))//note
   			println(s"j="+j+s"\n${peek(c.io.DMA_Data_o.bits)}\t${peek(wholepacket((j/8)*8+7,(j/8)*8))}")
		}
   		//println(s"j="+j+s"\n${peek(c.io.AFIFO_Data_o.bits)}\t${peek(wholepacket(j))}")
   		step(1)
 		//poke(c.io.DMA_Data_o.ready,false.B) 				
	}
	step(1)
	expect(c.io.DMA_Flag_AA_o.bits,true.B)
	expect(c.io.DMA_Flag_AA_o.valid,true.B)
   	println(s"j=flagAA\n${peek(c.io.DMA_Flag_AA_o.bits)}\ttrue")


	//step(Random_Num(8,100))
	step(2)
//PDU_HEADER
	for(j<-40 to 55){
		poke(c.io.AFIFO_Data_i.valid,true.B)
		poke(c.io.AFIFO_Data_i.bits,wholepacket(j))

		//println(s"${(j/8)*8}")
		//step(Random_Num(2,100))//minimun for DMA_fire: 2

		step(1)
		poke(c.io.AFIFO_Data_i.valid,false.B)	
		if(j%8==7){
			poke(c.io.DMA_Data_o.ready,true.B)
   			expect(c.io.DMA_Data_o.bits, wholepacket_neg((j/8)*8+7,(j/8)*8))//note
   			println(s"j="+j+s"\n${peek(c.io.DMA_Data_o.bits)}\t${peek(wholepacket_neg((j/8)*8+7,(j/8)*8))}")
		}
   		//println(s"j="+j+s"\n${peek(c.io.AFIFO_Data_o.bits)}\t${peek(wholepacket(j))}")
   		step(1)
 		//poke(c.io.DMA_Data_o.ready,false.B) 				
	}
	step(1)
	expect(c.io.DMA_Length_o.bits,7.U)
	expect(c.io.DMA_Length_o.valid,true.B)
   	println(s"j=DMA_Length_o\n${peek(c.io.DMA_Length_o.bits)}\t7.U")

	//step(Random_Num(8,100))
	step(2)
	poke(c.io.DMA_Length_o.ready,false.B)

//PDU_PAYLOAD
	for(j<-56 to 14*8-1){
		poke(c.io.AFIFO_Data_i.valid,true.B)
		poke(c.io.AFIFO_Data_i.bits,wholepacket(j))

		//println(s"${(j/8)*8}")
		//step(Random_Num(2,100))//minimun for DMA_fire: 2

		step(1)
		poke(c.io.AFIFO_Data_i.valid,false.B)	
		if(j%8==7){
			poke(c.io.DMA_Data_o.ready,true.B)
   			expect(c.io.DMA_Data_o.bits, wholepacket_neg((j/8)*8+7,(j/8)*8))//note
   			println(s"j="+j+s"\n${peek(c.io.DMA_Data_o.bits)}\t${peek(wholepacket_neg((j/8)*8+7,(j/8)*8))}")
		}
   		//println(s"j="+j+s"\n${peek(c.io.AFIFO_Data_o.bits)}\t${peek(wholepacket(j))}")
   		step(1)
 		//poke(c.io.DMA_Data_o.ready,false.B) 				
	}
	//step(Random_Num(8,100))
	step(2)

//CRC
	for(j<-14*8 to 17*8-1){
		poke(c.io.AFIFO_Data_i.valid,true.B)
		poke(c.io.AFIFO_Data_i.bits,wholepacket(j))

		//println(s"${(j/8)*8}")
		//step(Random_Num(2,100))//minimun for DMA_fire: 2

		step(1)
		poke(c.io.AFIFO_Data_i.valid,false.B)	
		if(j%8==7){
			poke(c.io.DMA_Data_o.ready,true.B)
   			expect(c.io.DMA_Data_o.bits, wholepacket_neg((j/8)*8+7,(j/8)*8))//note
   			println(s"j="+j+s"\n${peek(c.io.DMA_Data_o.bits)}\t${peek(wholepacket_neg((j/8)*8+7,(j/8)*8))}")
		   	println(s"j=flagCRC_bits\n${peek(c.io.DMA_Flag_CRC_o.bits)}\ttrue")
		   	println(s"j=flagCRC_valid\n${peek(c.io.DMA_Flag_CRC_o.valid)}\ttrue")  
		}
   		step(1)
 		//poke(c.io.DMA_Data_o.ready,false.B) 				
	}

	expect(c.io.DMA_Flag_CRC_o.bits,true.B)
	expect(c.io.DMA_Flag_CRC_o.valid,true.B)
   	println(s"j=flagCRC_bits\n${peek(c.io.DMA_Flag_CRC_o.bits)}\ttrue")
   	println(s"j=flagCRC_valid\n${peek(c.io.DMA_Flag_CRC_o.valid)}\ttrue")



	//step(Random_Num(8,100))
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