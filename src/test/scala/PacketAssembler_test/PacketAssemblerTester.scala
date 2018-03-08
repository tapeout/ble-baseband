package PacketAssembler_test

import PacketAssembler._
import chisel3._
import chisel3.util._
import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}


class PacketAssemblerTest(c: PacketAssembler) extends PeekPokeTester(c) {
//IO reference
    /*val DMA_Trigger_i = Input(Bool())
    val DMA_Data_i = Flipped(Decoupled(UInt(8.W)))//decouple(sink): data, pop, empty
    val REG_CRC_Seed_i = Input(UInt(24.W))
    val REG_White_Seed_i = Input(UInt(7.W))

    val DMA_Done_o = Output(Bool())	
	
    //AFIFO
    val AFIFO_Data_o = Decoupled(UInt(1.W))//decouple(source): data, puch, full*/

//scala declaration
	val wholepacket = "h11001010100101071001101001".U//AA first bit: 1; length:7; total length: 26(13 bytes)
	//val wholepacket_neg = ~wholepacket
	val preamble = "b01010101".U
	val CRC = "h101001".U

//reset
	//reset(3)

//throughout packet
	poke(c.io.REG_CRC_Seed_i,"h00000f".U)
	poke(c.io.REG_White_Seed_i,"b0001100".U)

//initialize
	poke(c.io.DMA_Trigger_i,false.B)
	poke(c.io.DMA_Data_i.valid,false.B)
	poke(c.io.DMA_Data_i.bits,0.U)
	poke(c.io.AFIFO_Data_o.ready,false.B)

	step(2)

//trigger
	poke(c.io.DMA_Trigger_i,true.B)
	poke(c.io.DMA_Data_i.valid,true.B)
	poke(c.io.DMA_Data_i.bits,wholepacket(7,0))

	step(1)
	poke(c.io.DMA_Trigger_i,false.B)

//PREAMBLE
	var j:Int = 0
	for(j<-0 to 7){
		//step(Random_Num(1,100))
		step(5)
		poke(c.io.AFIFO_Data_o.ready,true.B)
   		expect(c.io.AFIFO_Data_o.bits, preamble(j))//note: U to B
   		//println(s"${peek(c.io.AFIFO_Data_o.bits)}")
   		//println(s"${peek(preamble(j))}")
   		step(1)
 		poke(c.io.AFIFO_Data_o.ready,false.B)//need to test two ready  				
	}
	//step(Random_Num(8,100))
	step(10)
//AA
	for(j<-0 to 31){
		if(j%8==0){
			poke(c.io.DMA_Data_i.bits,wholepacket((j/8)*8+7,(j/8)*8))
			poke(c.io.DMA_Data_i.valid,true.B)
		}else{
			poke(c.io.DMA_Data_i.valid,false.B)			
		}
		//println(s"${(j/8)*8}")
		//step(Random_Num(2,100))//minimun for DMA_fire: 2
		step(5)
		poke(c.io.AFIFO_Data_o.ready,true.B)
   		expect(c.io.AFIFO_Data_o.bits, wholepacket(j))//note
   		//println(s"j="+j+s"\n${peek(c.io.AFIFO_Data_o.bits)}\t${peek(wholepacket(j))}")
   		step(1)
 		poke(c.io.AFIFO_Data_o.ready,false.B)//need to test two ready  				
	}
	//step(Random_Num(8,100))
	step(10)
//PDU_HEADER
	for(j<-32 to 47){
		if(j%8==0){
			poke(c.io.DMA_Data_i.bits,wholepacket((j/8)*8+7,(j/8)*8))
			poke(c.io.DMA_Data_i.valid,true.B)
		}else{
			poke(c.io.DMA_Data_i.valid,false.B)			
		}
		
		//step(Random_Num(2,100))//minimun for DMA_fire: 2
		step(5)
		poke(c.io.AFIFO_Data_o.ready,true.B)
   		//println(s"j="+j+s"\n${peek(c.io.AFIFO_Data_o.bits)}\t${peek(wholepacket(j))}")		
		if(peek(wholepacket(j))==0){
   			expect(c.io.AFIFO_Data_o.bits, 1)//note
   			println("this is 1, expect 0")			
		}else{
   			expect(c.io.AFIFO_Data_o.bits, 0)//note				
		}
   		step(1)
 		poke(c.io.AFIFO_Data_o.ready,false.B)//need to test two ready  				
	}
	//step(Random_Num(8,100))
	step(10)
//PDU_PAYLOAD
	for(j<-48 to 13*8-1){
		if(j%8==0){
			poke(c.io.DMA_Data_i.bits,wholepacket((j/8)*8+7,(j/8)*8))
			poke(c.io.DMA_Data_i.valid,true.B)
		}else{
			poke(c.io.DMA_Data_i.valid,false.B)			
		}
		//step(Random_Num(2,100))//minimun for DMA_fire: 2
		step(5)
		poke(c.io.AFIFO_Data_o.ready,true.B)
		if(peek(wholepacket(j))==0){
   			expect(c.io.AFIFO_Data_o.bits, 1)//note		
		}else{
   			expect(c.io.AFIFO_Data_o.bits, 0)//note				
		}
   		step(1)
 		poke(c.io.AFIFO_Data_o.ready,false.B)//need to test two ready  				
	}
	//step(Random_Num(8,100))
	step(10)
//CRC
	for(j<-0 to 21){
		if(j%8==0){
			poke(c.io.DMA_Data_i.bits,CRC((j/8)*8+7,(j/8)*8))
			poke(c.io.DMA_Data_i.valid,true.B)
		}else{
			poke(c.io.DMA_Data_i.valid,false.B)			
		}
		//step(Random_Num(1,100))
		step(5)
		poke(c.io.AFIFO_Data_o.ready,true.B)
		println(s"j="+j+s"\n${peek(c.io.AFIFO_Data_o.bits)}\t${peek(CRC(j))}")
		if(peek(CRC(j))==0){
   			expect(c.io.AFIFO_Data_o.bits, 1)//note			
		}else{
   			expect(c.io.AFIFO_Data_o.bits, 0)//note				
		}
   		step(1)
 		poke(c.io.AFIFO_Data_o.ready,false.B)//need to test two ready consequently		
	}
	j=22
	//poke(c.io.DMA_Data_i.bits,CRC((j/8)*8+7,(j/8)*8))
	step(1)
	poke(c.io.AFIFO_Data_o.ready,true.B)
	if(peek(CRC(j))==0){
			expect(c.io.AFIFO_Data_o.bits, 1)//note
			println("this is 1, expect 0")			
	}else{
			expect(c.io.AFIFO_Data_o.bits, 0)//note				
	}
	j=23
	//poke(c.io.DMA_Data_i.bits,CRC((j/8)*8+7,(j/8)*8))
	step(1)
	poke(c.io.AFIFO_Data_o.ready,true.B)
	if(peek(CRC(j))==0){
			expect(c.io.AFIFO_Data_o.bits, 1)//note
			println("this is 1, expect 0")			
	}else{
			expect(c.io.AFIFO_Data_o.bits, 0)//note				
	}
	expect(c.io.DMA_Done_o, true.B)//note	



//todo: add FIFO
//todo: add invalid DMA
//todo: check output: DMA_ready

}

class PacketAssemblerTester extends ChiselFlatSpec {
	behavior of "PacketAssembler"
	backends foreach {backend =>
		it should s"perform correct operation as an packet assembler $backend" in { 
			Driver(() => new PacketAssembler, "verilator") { 
				(c) => new PacketAssemblerTest(c)} should be (true)
			}
		}
}