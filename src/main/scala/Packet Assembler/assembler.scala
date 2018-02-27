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
	val initial_state = IDLE
	val state_list = List(IDLE, PREAMBLE, AA, PDU_HEADER, PDU_PAYLOAD, CRC)
	val input_bundle = new Bundle{
	    val DMA_Trigger = Wire(Bool())
	    val DMA_Data = Flipped(Decoupled(UInt(8.W)))//decouple(sink): data, pop, empty//note
	    val REG_CRC_Seed = Wire(UInt(24.W))
	    val REG_White_Seed = Wire(UInt(7.W))	
	}
	val output_bundle = new Bundle{
	    val DMA_Done = Wire(Bool())	
	    val AFIFO_Data = Decoupled(UInt(1.W))//decouple(source): data, puch, full		
	}
	input_bundle.DMA_Trigger := io.DMA_Trigger_i
	input_bundle.DMA_Data <> io.DMA_Data_i
	input_bundle.REG_CRC_Seed := io.REG_CRC_Seed_i
	input_bundle.REG_White_Seed := io.REG_White_Seed_i
	io.DMA_Done_o := output_bundle.DMA_Done
	io.AFIFO_Data_o <> output_bundle.AFIFO_Data

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

		//Preamble
		val Preamble0 = Wire(UInt(8.W))
		val Preamble1 = Wire(UInt(8.W))

		//DMA_Data
		val DMA_Data_Ready_r = RegInit(Bool(), false.B)
		val DMA_Data_Fire_w = Wire(Bool())

		//AFIFO
		val AFIFO_Valid_r = RegInit(Bool(), false.B)
		val AFIFO_Fire_w = Wire(Bool())

		//data registers
		val data_w = Wire(UInt(8.W))
		val data_r = RegInit(UInt(8.W), 0.U)

		//CRC
		val CRC_Reset_w = Wire(Bool())
		val CRC_Data_w = Wire(UInt(1.W))
		val CRC_Valid_w = Wire(Bool())
		val CRC_Result_w = Wire(UInt(24.W))
		val CRC_Seed_w = Wire(UInt(24.W))

		//whitening
		val WHITE_Reset_w = Wire(Bool())
		val WHITE_Data_w = Wire(UInt(1.W))
		val WHITE_Valid_w = Wire(Bool())	
		val WHITE_Result_w = Wire(UInt(1.W))
		val WHITE_Seed_w = Wire(UInt(7.W))			
	}

	//decouple assignments
	input_bundle.DMA_Data.ready := val_bundle.DMA_Data_Ready_r
	val_bundle.DMA_Data_Fire_w := io.DMA_Data_i.ready & io.DMA_Data_i.valid	
	output_bundle.AFIFO_Data.valid := val_bundle.AFIFO_Valid_r
	val_bundle.AFIFO_Fire_w := io.AFIFO_Data_o.ready & io.AFIFO_Data_o.valid

	//Preamble assignments
	val_bundle.Preamble0 := "b01010101".U
	val_bundle.Preamble1 := "b10101010".U


//combinational logic
	/*for (states_num <- state_list){
		when (state_r === state_num.U){
			//state_w := StateTransition(input_bundle, val_bundle).U
			output_bundle := OutputFunction(input_bundle, val_bundle)
			val_bundle := CombLogic(input_bundle, val_bundle)
		}
	}
	output_bundle <> OutputFunction(input_bundle, val_bundle)
	val_bundle <> CombLogic(input_bundle, val_bundle)*/

	//output function
	when(val_bundle.state_r === IDLE){
		output_bundle.AFIFO_Data.bits := 0.U
	}.otherwise{
		when(val_bundle.state_r === PDU_HEADER || val_bundle.state_r === PDU_PAYLOAD || val_bundle.state_r === CRC){
			output_bundle.AFIFO_Data.bits := val_bundle.WHITE_Result_w
		}.otherwise{
			output_bundle.AFIFO_Data.bits := val_bundle.data_r(val_bundle.counter_byte_r)
		}
	}

	when(val_bundle.state_r === CRC && val_bundle.counter_r === 2.U && val_bundle.counter_byte_r === 7.U && val_bundle.AFIFO_Fire_w === true.B){
		output_bundle.DMA_Done := true.B	
	}.otherwise{
		output_bundle.DMA_Done := false.B
	}

	//default
	val_bundle.state_w 			:= val_bundle.state_r
	val_bundle.counter_w 		:= val_bundle.counter_r
	val_bundle.counter_byte_w	:= val_bundle.counter_byte_r
	val_bundle.data_w			:= val_bundle.data_r
	val_bundle.PDU_Length_r		:= val_bundle.PDU_Length_r//note: preserve value
	val_bundle.DMA_Data_Ready_r	:= val_bundle.DMA_Data_Ready_r
	val_bundle.AFIFO_Valid_r	:= val_bundle.AFIFO_Valid_r

	//StateTransition with counter updates
	when(val_bundle.state_r === IDLE){
		when(input_bundle.DMA_Trigger === true.B){
			val_bundle.state_w := PREAMBLE
			val_bundle.counter_w := 0.U
			val_bundle.counter_byte_w := 0.U
		}.otherwise{
			val_bundle.state_w := IDLE
		}
	}.elsewhen(val_bundle.state_r === PREAMBLE){
		when(val_bundle.counter_r === 0.U && val_bundle.counter_byte_r === 7.U && val_bundle.AFIFO_Fire_w === true.B){//note
			val_bundle.state_w := AA
			val_bundle.counter_w := 0.U
			val_bundle.counter_byte_w := 0.U
		}.otherwise{
			val_bundle.state_w := PREAMBLE
			when(val_bundle.AFIFO_Fire_w === true.B){
				when(val_bundle.counter_byte_r === 7.U){
					val_bundle.counter_w := val_bundle.counter_r+1.U
					val_bundle.counter_byte_w := 0.U
				}.otherwise{
					val_bundle.counter_byte_w := val_bundle.counter_byte_r+1.U					
				}
			}
		}		
	}.elsewhen(val_bundle.state_r === AA){
		when(val_bundle.counter_r === 3.U && val_bundle.counter_byte_r === 7.U && val_bundle.AFIFO_Fire_w === true.B){//note
			val_bundle.state_w := PDU_HEADER
			val_bundle.counter_w := 0.U
			val_bundle.counter_byte_w := 0.U
		}.otherwise{
			val_bundle.state_w := AA
			when(val_bundle.AFIFO_Fire_w === true.B){
				when(val_bundle.counter_byte_r === 7.U){
					val_bundle.counter_w := val_bundle.counter_r+1.U
					val_bundle.counter_byte_w := 0.U
				}.otherwise{
					val_bundle.counter_byte_w := val_bundle.counter_byte_r+1.U					
				}
			}				
		}			
	}.elsewhen(val_bundle.state_r === PDU_HEADER){
		when(val_bundle.counter_r === 1.U && val_bundle.counter_byte_r === 7.U && val_bundle.AFIFO_Fire_w === true.B){//note
			val_bundle.state_w := PDU_PAYLOAD
			val_bundle.counter_w := 0.U
			val_bundle.counter_byte_w := 0.U
		}.otherwise{
			val_bundle.state_w := PDU_HEADER
			when(val_bundle.AFIFO_Fire_w === true.B){
				when(val_bundle.counter_byte_r === 7.U){
					val_bundle.counter_w := val_bundle.counter_r+1.U
					val_bundle.counter_byte_w := 0.U
				}.otherwise{
					val_bundle.counter_byte_w := val_bundle.counter_byte_r+1.U					
				}
			}
		}			
	}.elsewhen(val_bundle.state_r === PDU_PAYLOAD){
		when(val_bundle.counter_r === val_bundle.PDU_Length_r-1.U && val_bundle.counter_byte_r === 7.U && val_bundle.AFIFO_Fire_w === true.B){//note
			val_bundle.state_w := CRC
			val_bundle.counter_w := 0.U
			val_bundle.counter_byte_w := 0.U
		}.otherwise{
			val_bundle.state_w := PDU_PAYLOAD
			when(val_bundle.AFIFO_Fire_w === true.B){
				when(val_bundle.counter_byte_r === 7.U){
					val_bundle.counter_w := val_bundle.counter_r+1.U
					val_bundle.counter_byte_w := 0.U
				}.otherwise{
					val_bundle.counter_byte_w := val_bundle.counter_byte_r+1.U					
				}
			}
		}			
	}.elsewhen(val_bundle.state_r === CRC){
		when(val_bundle.counter_r === 2.U && val_bundle.counter_byte_r === 7.U && val_bundle.AFIFO_Fire_w === true.B){//note
			val_bundle.state_w := IDLE
			val_bundle.counter_w := 0.U
			val_bundle.counter_byte_w := 0.U
		}.otherwise{
			val_bundle.state_w := CRC
			when(val_bundle.AFIFO_Fire_w === true.B){
				when(val_bundle.counter_byte_r === 7.U){
					val_bundle.counter_w := val_bundle.counter_r+1.U
					val_bundle.counter_byte_w := 0.U
				}.otherwise{
					val_bundle.counter_byte_w := val_bundle.counter_byte_r+1.U					
				}
			}
		}		
	}.otherwise{
		val_bundle.state_w := IDLE//error
	}


	//PDU_Length
	when(val_bundle.state_r === PDU_HEADER && val_bundle.counter_r === 1.U){
		val_bundle.PDU_Length_r := val_bundle.data_r
	}.otherwise{
		//do nothing: registers preserve value//note
	}

	//DMA_Data_Ready_r//note:check corner cases
	when(val_bundle.state_r === AA || val_bundle.state_r === PDU_HEADER || val_bundle.state_r === PDU_PAYLOAD){
		when(val_bundle.state_r === PDU_PAYLOAD && val_bundle.counter_r === val_bundle.PDU_Length_r-1.U && val_bundle.counter_byte_r === 7.U && val_bundle.AFIFO_Fire_w === true.B){
			val_bundle.DMA_Data_Ready_r := false.B//special case at the end of PAYLOAD		
		}.elsewhen(val_bundle.counter_byte_r === 7.U && val_bundle.AFIFO_Fire_w === true.B){
			val_bundle.DMA_Data_Ready_r := true.B
		}.elsewhen(val_bundle.DMA_Data_Fire_w === true.B){
			val_bundle.DMA_Data_Ready_r := false.B		
		}.otherwise{
			//do nothing
		}		
	}.otherwise{//IDLE, PREAMBLE, CRC
		when(val_bundle.state_r === PREAMBLE && val_bundle.counter_r === 0.U && val_bundle.counter_byte_r === 7.U && val_bundle.AFIFO_Fire_w === true.B){
			val_bundle.DMA_Data_Ready_r := true.B//special case at the end of PREAMBLE: AA starts with ready
		}.otherwise{
			val_bundle.DMA_Data_Ready_r := false.B
		}
	}

	//AFIFO_Valid_w//note:check corner cases
		//val_bundle.AFIFO_Valid_w := ~val_bundle.DMA_Data_Ready_r
	when(val_bundle.state_r === IDLE){
		val_bundle.AFIFO_Valid_r := false.B
	}.elsewhen(val_bundle.state_r === PREAMBLE){
		when(val_bundle.counter_r === 0.U && val_bundle.counter_byte_r === 7.U && val_bundle.AFIFO_Fire_w === true.B){
			val_bundle.AFIFO_Valid_r := false.B//special case at the end of PREAMBLE: AA starts with invalid
		}.otherwise{
			val_bundle.AFIFO_Valid_r := true.B
		}
	}.elsewhen(val_bundle.state_r === CRC){
		when(val_bundle.counter_r === 2.U && val_bundle.counter_byte_r === 7.U && val_bundle.AFIFO_Fire_w === true.B){
			val_bundle.AFIFO_Valid_r := false.B//special case at the end of CRC		
		}.otherwise{
			val_bundle.AFIFO_Valid_r := true.B			
		}
	}.otherwise{//AA, PDU_HEADER, PDU_PAYLOAD
		when(val_bundle.counter_byte_r === 7.U && val_bundle.AFIFO_Fire_w === true.B){
			val_bundle.AFIFO_Valid_r := false.B			
		}.elsewhen(val_bundle.DMA_Data_Fire_w === true.B){
			val_bundle.AFIFO_Valid_r := true.B				
		}
	}

	//data
	when(val_bundle.state_r === AA || val_bundle.state_r === PDU_HEADER || val_bundle.state_r === PDU_PAYLOAD){
		when(val_bundle.DMA_Data_Fire_w === true.B){
			val_bundle.data_w := input_bundle.DMA_Data.bits			
		}.otherwise{
			val_bundle.data_w := val_bundle.data_r
		}
	}.elsewhen(val_bundle.state_r === PREAMBLE){
		when(input_bundle.DMA_Data.bits(0) === 0.U){//note: problems when not firing
			val_bundle.data_w := val_bundle.Preamble0
		}.otherwise{
			val_bundle.data_w := val_bundle.Preamble1
		}
	}.elsewhen(val_bundle.state_r === CRC){
		val_bundle.data_w := val_bundle.CRC_Result_w((val_bundle.counter_w+1)*8-1,val_bundle.counter_w*8)//note
	}.otherwise{//IDLE
		val_bundle.data_w := 0.U//or preserve
	}

	//CRC
	val_bundle.CRC_Reset_w := input_bundle.DMA_Trigger
	when(val_bundle.state_r === PDU_HEADER || val_bundle.state_r === PDU_PAYLOAD){
		val_bundle.CRC_Data_w := val_bundle.data_r(val_bundle.counter_byte_r)
		val_bundle.CRC_Valid_w := val_bundle.AFIFO_Fire_w
	}.otherwise{
		val_bundle.CRC_Data_w := 0.U
		val_bundle.CRC_Valid_w := false.B
	}
	//val_bundle.CRC_Result_w wires to CRC module
	val_bundle.CRC_Seed_w := input_bundle.REG_CRC_Seed

	//whitening
	val_bundle.WHITE_Reset_w := input_bundle.DMA_Trigger
	when(val_bundle.state_r === PDU_HEADER || val_bundle.state_r === PDU_PAYLOAD || val_bundle.state_r === CRC){	
		val_bundle.WHITE_Data_w  := val_bundle.data_r(val_bundle.counter_byte_r)//note
		val_bundle.WHITE_Valid_w := val_bundle.AFIFO_Fire_w
	}.otherwise{
		val_bundle.WHITE_Data_w  := 0.U
		val_bundle.WHITE_Valid_w := false.B
	}
	//val_bundle.WHITE_Result_w wires to WHITE module
	val_bundle.WHITE_Seed_w := input_bundle.REG_WHITE_Seed		


//sequential logic
	val_bundle.state_r 			:= val_bundle.state_w
	val_bundle.counter_r 		:= val_bundle.counter_w
	val_bundle.counter_byte_r	:= val_bundle.counter_byte_w
	val_bundle.data_r			:= val_bundle.data_w



	/*def OutputFunction(input_bundle: Class_input_bundle, val_bundle: Class_val_bundle): Class_output_bundle ={//note
		//OutputFunction.AFIFO_Data.valid := val_bundle.AFIFO_Valid_w
	}

	def CombLogic(input_bundle: Class_input_bundle, val_bundle: Class_val_bundle): Class_val_bundle ={	

	}*/

}