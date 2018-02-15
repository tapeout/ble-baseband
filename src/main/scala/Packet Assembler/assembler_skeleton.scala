class Assembler extends Module {
  val io = IO(new Bundle {
	//DMA or RegF
    val _Trigger = Input(Bool())//Input or Output
    val _AA = Input(UInt(32.W))
    val _Data = Input(UInt(8.W))
    val _CRC_Seed = Input(UInt(24.W))
    val _White_Seed = Input(UInt(7.W))

    val _Done = Output(Bool())	
	
	//Whitening
    val WHITE_Start_End = Output(Bool())
    val WHITE_Data = Output(UInt(8.W))
    val WHITE_DoWhite = Output(Bool())
    val WHITE_Seed = Output(UInt(7.W))
  })

	//scala declaration(note: can be a class)
	//state parameter
	val IDLE :: TRANS_PREAMBLE :: TRANS_AA :: LOAD_TRANS_PDU_CAL_CRC :: TRANS_CRC :: Nil = Enum(5)
	initial_state = IDLE
	state_list = List(IDLE, TRANS_PREAMBLE, TRANS_AA, LOAD_TRANS_PDU_CAL_CRC, TRANS_CRC)
	input_bundle
	output_bundle


	//reg, wire
	//for FSM
	val state_w = Wire(UInt(3.W))
	val state_r = RegInit(UInt(3.W), initial_state.U)

	val counter_w = Wire(UInt(8.W))//at most 255 for PDU
	val counter_r = RegInit(UInt(8.W), 0.U)

	val PDU_length_r = RegInit(UInt(8.W), 0.U)

	//data registers
	val data_w = Wire(UInt(8.W))
	val data_r = RegInit(UInt(3.W), 0.U)

	//CRC
	val CRC_Start_End_w = Wire(Bool())
		//val CRC_Start_End_r = RegInit(Bool(), 0.U)

	val CRC_Data_w = Wire(UInt(8.W))
	val CRC_Result_w = Wire(UInt(24.W))
	val CRC_Seed_w = Wire(UInt(24.W))

	val CRC_Done_w = Wire(Bool())
		//val CRC_Done_r = RegInit(Bool(), 0.U)

	//combinational logic
	for (states_num <- state_list){
		when (state_r === state_num.U){
			//state_w := StateTransition(input_bundle, val_bundle).U
			output_bundle := OutputFunction(input_bundle, val_bundle)
			val_bundle = CombLogic(input_bundle, val_bundle)
		}
	}

	//sequential logic
	state_r 	:= state_w
	counter_r 	:= counter_w
	data_r		:= data_w

	//OutputFunction
	def OutputFunction(input_bundle: Class_input_bundle, val_bundle: Class_val_bundle): Class_output_bundle ={
			if(val_bundle.state_r in state_list/IDLE){
				OutputFunction.WHITE_Data := val_bundle.data_r
			}
			else{
				OutputFunction.WHITE_Data := 0.U
			}

			if(val_bundle.state_r === LOAD_TRANS_PDU_CAL_CRC || val_bundle.state_r === TRANS_CRC){
				OutputFunction.WHITE_DoWhite := true.B					
			}
			else{
				OutputFunction.WHITE_DoWhite := false.B			
			}
			//SEED, START_END


		}



	}



}






























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