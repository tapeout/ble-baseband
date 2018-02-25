class Assembler extends Module {
  val io = IO(new Bundle {
	//DMA, REG
    val DMA_Trigger_i = Input(Bool())
    val DMA_Data_i = Flipped(Decoupled(UInt(8.W)))//decouple(sink): data, pop, empty
    val REG_CRC_Seed_i = Input(UInt(24.W))
    val REG_White_Seed_i = Input(UInt(7.W))

    val DMA_Done_o = Output(Bool())	
	
    //AFIFO
    val AFIFO_Data_o = Decoupled(UInt(1.W))//decouple(source): data, puch, full

  })

//scala declaration(note: can be a class)
	//state parameter
	val IDLE :: PREAMBLE :: AA :: PDU_HEADER :: PDU_PAYLOAD :: CRC :: Nil = Enum(6)
	initial_state = IDLE
	state_list = List(IDLE, PREAMBLE, AA, PDU_HEADER, PDU_PAYLOAD, CRC)
	val input_bundle = new Bundle{
	    val DMA_Trigger = Wire(Bool())
	    val DMA_Data = Flipped(Decoupled(UInt(8.W)))//decouple(sink): data, pop, empty
	    val REG_CRC_Seed = Wire(UInt(24.W))
	    val REG_White_Seed = Wire(UInt(7.W))	
	}
	val output_bundle = new Bundle{
	    val DMA_Done = Wire(Bool())	
	    val AFIFO_Data = Decoupled(UInt(1.W))//decouple(source): data, puch, full		
	}
	input_bundle.DMA_Trigger := io.DMA_Trigger_i
	input_bundle.DMA_Data := io.DMA_Data_i
	input_bundle.REG_CRC_Seed := io.REG_CRC_Seed_i
	input_bundle.REG_White_Seed := io.REG_White_Seed_i
	io.DMA_Done_o := output_bundle.DMA_Done
	io.AFIFO_Data_o := output_bundle.AFIFO_Data

	val val_bundle = new Bundle{
	//reg, wire
		//FSM
		val state_w = Wire(UInt(3.W))
		val state_r = RegInit(UInt(3.W), initial_state.U)

		val counter_w = Wire(UInt(8.W))//at most 255 for PDU
		val counter_r = RegInit(UInt(8.W), 0.U)

		val counter_byte_w = Wire(UInt(3.W))//byte in bit out
		val counter_byte_r = RegInit(UInt(3.W), 0.U)	

		val PDU_Length_r = RegInit(UInt(8.W), 0.U)

		//DMA_Data
		val DMA_Data_Ready_r = Wire(Bool())
		val DMA_Data_Fire_w = Wire(Bool())

		//AFIFO
		val AFIFO_Valid_r = Wire(Bool())
		val AFIFO_Fire_w = Wire(Bool())

		//data registers
		val data_w = Wire(UInt(8.W))
		val data_r = RegInit(UInt(8.W), 0.U)

		//CRC
		val CRC_Reset_w = Wire(Bool())
		val CRC_Data_w = Wire(UInt(8.W))
		val CRC_Valid_w = Wire(UInt(1.W))
		val CRC_Result_w = Wire(UInt(24.W))
		val CRC_Seed_w = Wire(UInt(24.W))

		//whitening
		val WHITE_Reset_w = Wire(Bool())
		val WHITE_Data_w = Wire(UInt(1.W))
		val WHITE_Valid_w = Wire(UInt(1.W))	
		val WHITE_Result_w = Wire(UInt(1.W))
		val WHITE_Seed_w = Wire(UInt(7.W))			
	}

	//decouple assignments
	input_bundle.DMA_Data.ready := DMA_Data_Ready_r
	DMA_Data_Fire_w := io.DMA_Data_i.ready & io.DMA_Data_i.valid	
	output_bundle.AFIFO_Data.valid := AFIFO_Valid_r
	AFIFO_Fire_w := io.AFIFO_Data_o.ready & io.AFIFO_Data_o.valid


//combinational logic
	/*for (states_num <- state_list){
		when (state_r === state_num.U){
			//state_w := StateTransition(input_bundle, val_bundle).U
			output_bundle := OutputFunction(input_bundle, val_bundle)
			val_bundle := CombLogic(input_bundle, val_bundle)
		}
	}*/
	output_bundle <> OutputFunction(input_bundle, val_bundle)
	val_bundle <> CombLogic(input_bundle, val_bundle)	



//sequential logic
	val_bundle.state_r 			:= val_bundle.state_w
	val_bundle.counter_r 		:= val_bundle.counter_w
	val_bundle.counter_byte_r	:= val_bundle.counter_byte_w
	val_bundle.data_r			:= val_bundle.data_w


//OutputFunction
	def OutputFunction(input_bundle: Class_input_bundle, val_bundle: Class_val_bundle): Class_output_bundle ={//note
		//OutputFunction.AFIFO_Data.valid := val_bundle.AFIFO_Valid_w
		when(val_bundle.state_r === IDLE){
			OutputFunction.AFIFO_Data.bits := 0.U
		}.otherwise{
			OutputFunction.AFIFO_Data.bits := val_bundle.data_r(val_bundle.counter_byte_r)
		}

		when(val_bundle.state_r ===  CRC && val_bundle.counter_r === 2.U && val_bundle.counter_byte_r === 7.U && val_bundle.AFIFO_Fire_w === true){//note
			OutputFunction.DMA_Done := true	
		}.otherwise{
			OutputFunction.DMA_Done := false
		}
	}

	def CombLogic(input_bundle: Class_input_bundle, val_bundle: Class_val_bundle): Class_val_bundle ={	
	//StateTransition with counter updates
		when(CombLogic.state_r === IDLE){
			when(input_bundle.DMA_Trigger === true){
				state_w := PREAMBLE
				val_bundle.counter_w := 0.U
				val_bundle.counter_byte_w := 0.U
			}.otherwise{
				state_w := IDLE
			}
		}.elsewhen(CombLogic.state_r === PREAMBLE){
			when(val_bundle.counter_r === 0.U && val_bundle.counter_byte_r === 7.U && val_bundle.AFIFO_Fire_w === true){//note
				state_w := AA
				val_bundle.counter_w := 0.U
				val_bundle.counter_byte_w := 0.U				
			}.otherwise{
				state_w := PREAMBLE
				when(val_bundle.AFIFO_Fire_w === true){
					when(val_bundle.counter_byte_r === 7.U){
						val_bundle.counter_w := val_bundle.counter_r+1.U
						val_bundle.counter_byte_w := 0.U
					}.otherwise{
						val_bundle.counter_byte_w := val_bundle.counter_byte_r+1.U					
					}
				}
			}		
		}.elsewhen(CombLogic.state_r === AA){
			when(val_bundle.counter_r === 3.U && val_bundle.counter_byte_r === 7.U && val_bundle.AFIFO_Fire_w === true){//note
				state_w := PDU_HEADER
				val_bundle.counter_w := 0.U
				val_bundle.counter_byte_w := 0.U
			}.otherwise{
				state_w := AA
				when(val_bundle.AFIFO_Fire_w === true){
					when(val_bundle.counter_byte_r === 7.U){
						val_bundle.counter_w := val_bundle.counter_r+1.U
						val_bundle.counter_byte_w := 0.U
					}.otherwise{
						val_bundle.counter_byte_w := val_bundle.counter_byte_r+1.U					
					}
				}				
			}			
		}.elsewhen(CombLogic.state_r === PDU_HEADER){
			when(val_bundle.counter_r === 1.U && val_bundle.counter_byte_r === 7.U && val_bundle.AFIFO_Fire_w === true){//note
				state_w := PDU_PAYLOAD
				val_bundle.counter_w := 0.U
				val_bundle.counter_byte_w := 0.U
			}.otherwise{
				state_w := PDU_HEADER
				when(val_bundle.AFIFO_Fire_w === true){
					when(val_bundle.counter_byte_r === 7.U){
						val_bundle.counter_w := val_bundle.counter_r+1.U
						val_bundle.counter_byte_w := 0.U
					}.otherwise{
						val_bundle.counter_byte_w := val_bundle.counter_byte_r+1.U					
					}
				}
			}			
		}.elsewhen(CombLogic.state_r === PDU_PAYLOAD){
			when(val_bundle.counter_r === val_bundle.PDU_Length_r && val_bundle.counter_byte_r === 7.U && val_bundle.AFIFO_Fire_w === true){//note
				state_w := CRC
				val_bundle.counter_w := 0.U
				val_bundle.counter_byte_w := 0.U
			}.otherwise{
				state_w := PDU_PAYLOAD
				when(val_bundle.AFIFO_Fire_w === true){
					when(val_bundle.counter_byte_r === 7.U){
						val_bundle.counter_w := val_bundle.counter_r+1.U
						val_bundle.counter_byte_w := 0.U
					}.otherwise{
						val_bundle.counter_byte_w := val_bundle.counter_byte_r+1.U					
					}
				}
			}			
		}.elsewhen(CombLogic.state_r === CRC){
			when(val_bundle.counter_r === 2.U && val_bundle.counter_byte_r === 7.U && val_bundle.AFIFO_Fire_w === true){//note
				state_w := IDLE
				val_bundle.counter_w := 0.U
				val_bundle.counter_byte_w := 0.U
			}.otherwise{
				state_w := CRC
				when(val_bundle.AFIFO_Fire_w === true){
					when(val_bundle.counter_byte_r === 7.U){
						val_bundle.counter_w := val_bundle.counter_r+1.U
						val_bundle.counter_byte_w := 0.U
					}.otherwise{
						val_bundle.counter_byte_w := val_bundle.counter_byte_r+1.U					
					}
				}
			}		
		}.otherwise
			state_w := IDLE//error
		}

	//PDU_Length
	when(val_bundle.state_r === PDU_HEADER && val_bundle.counter_r === 1.U){
		PDU_Length_r := data_r
	}.otherwise{
		//do nothing: registers preserve value
	}

	//DMA_Data_Ready_r//note:check corner cases
	when(val_bundle.state_r === AA || val_bundle.state_r === PDU_HEADER || val_bundle.state_r === PDU_PAYLOAD){
		when(val_bundle.counter_byte_r === 7.U && val_bundle.AFIFO_Fire_w === true){
			val_bundle.DMA_Data_Ready_r := true
		}.elsewhen(val_bundle.DMA_Data_Fire_w === true){
			val_bundle.DMA_Data_Ready_r := false		
		}.otherwise{
			//do nothing
		}		
	}.otherwise{
		val_bundle.DMA_Data_Ready_r := false		
	}


	//AFIFO_Valid_w//note:check corner cases//rewrite into state split
		//val_bundle.AFIFO_Valid_w := ~val_bundle.DMA_Data_Ready_r
	when(val_bundle.state_r != IDLE){
		when(val_bundle.counter_byte_r === 7.U && val_bundle.AFIFO_Fire_w === true){
			val_bundle.AFIFO_Valid_r := false
		}.elsewhen((val_bundle.state_r === AA || val_bundle.state_r === PDU_HEADER || val_bundle.state_r === PDU_PAYLOAD) && val_bundle.DMA_Data_Fire_w === true){
			val_bundle.AFIFO_Valid_r := true		
		}.elsewhen(val_bundle.state_r === PREAMBLE || val_bundle.state_r === CRC){
			val_bundle.AFIFO_Valid_r := true				
		}.otherwise{
			//do nothing
		}
	}


	//data
	when(val_bundle.state_r === AA || val_bundle.state_r === PDU_HEADER || val_bundle.state_r === PDU_PAYLOAD){
		when(val_bundle.DMA_Data_Fire_w === true){
			val_bundle.data_w := input_bundle.DMA_Data.bits
		}.otherwise{
			//do nothing
		}		
	}.elsewhen(val_bundle.state_r === PREAMBLE){

	}.elsewhen(val_bundle.state_r === CRC){

	}.otherwise{

	}

	//CRC
	val_bundle.CRC_Reset_w := input_bundle.DMA_Trigger
	when(val_bundle.state_r === CRC){
		val_bundle.CRC_Data_w := val_bundle.data_r(val_bundle.counter_byte_r)
		val_bundle.CRC_Valid_w := val_bundle.AFIFO_Valid_w	
	}
	//val_bundle.CRC_Result_w wires to CRC module
	val_bundle.CRC_Seed_w := input_bundle.REG_CRC_Seed

	//whitening
	val WHITE_Reset_w = Wire(Bool())
	val WHITE_Data_w = Wire(UInt(1.W))
	val WHITE_Valid_w = Wire(UInt(1.W))	
	val WHITE_Result_w = Wire(UInt(1.W))
	val WHITE_Seed_w = Wire(UInt(7.W))		


}







input_bundle.DMA_Data.ready??

			when(OutputFunction.AFIFO_Data.Full === false){




















class DisAssembler extends Module {
  val io = IO(new Bundle {
	//dma(AES)
    val rx_trigger = Input(UInt(1.W))
    val rx_dma_ready = Input(UInt(1.W))	

	val rx_dma_data = Output(UInt(8.W))//AA, PDU, CRC
    val rx_dma_valid = Output(UInt(1.W))
    val rx_done = Output(UInt(1.W))	
	
	//send(whitening)
    val rx_receive_data = Input(UInt(8.W))
    val rx_receive_valid = Input(UInt(1.W))

    val rx_receive_ready = Output(UInt(1.W))
  })
  io.out := io.in
}