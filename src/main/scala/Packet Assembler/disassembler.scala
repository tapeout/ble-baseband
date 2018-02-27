class DisAssembler extends Module {
  val io = IO(new Bundle {
    //DMA, REG
    val DMA_Switch_i = Input(Bool())
    val REG_AA_i = Input(UInt(32.W))   
    val REG_CRC_Seed_i = Input(UInt(24.W))
    val REG_White_Seed_i = Input(UInt(7.W))

    val DMA_Data_o = Decoupled(UInt(8.W))//decouple(sink): data, puch, full
    val DMA_Length_o = Output(UInt(8.W))
    val DMA_Flag_AA_o = Output(Bool())
    val DMA_Flag_CRC_o = Output(Bool())     

    //AFIFO
    val AFIFO_Data_i = Flipped(Decoupled(UInt(1.W)))//decouple(source): data, pop, empty
})

//scala declaration(note: can be a class)
  //state parameter
  val IDLE :: PREAMBLE :: AA :: PDU_HEADER :: PDU_PAYLOAD :: CRC :: Nil = Enum(6)
  val initial_state = IDLE
  val state_list = List(IDLE, PREAMBLE, AA, PDU_HEADER, PDU_PAYLOAD, CRC)

  val val_bundle = new Bundle{
  //reg, wire
    //FSM
    val state_w = Wire(UInt(3.W))
    val state_r = RegInit(UInt(3.W), initial_state.U)

    val counter_w = Wire(UInt(8.W))//at most 255 for PDU
    val counter_r = RegInit(UInt(8.W), 0.U)

    val counter_byte_w = Wire(UInt(3.W))//bit in byte out
    val counter_byte_r = RegInit(UInt(3.W), 0.U)  

    //packet status
    val PDU_Length_r = RegInit(UInt(8.W), 0.U)
    val Flag_AA_r = RegInit(Bool(), false)
    val Flag_CRC_r = RegInit(Bool(), false)

    //Preamble
    val Preamble0 = Wire(UInt(8.W))
    val Preamble1 = Wire(UInt(8.W))

    //DMA_Data
    val DMA_Data_Valid_r = RegInit(Bool(), false.B)
    val DMA_Data_Fire_w = Wire(Bool())

    //AFIFO
    val AFIFO_Ready_r = RegInit(Bool(), false.B)
    val AFIFO_Fire_w = Wire(Bool())

    //data registers
    val data_w = Wire(UInt(8.W))
    val data_r = RegInit(UInt(8.W), 0.U)

    //CRC
    val CRC_Reset_w = Wire(Bool())
    val CRC_Data_w = Wire(UInt(8.W))
    val CRC_Valid_w = Wire(Bool())
    val CRC_Result_w = Wire(UInt(24.W))
    val CRC_Seed_w = Wire(UInt(24.W))

    //whitening
    val DEWHITE_Reset_w = Wire(Bool())
    val DEWHITE_Data_w = Wire(UInt(1.W))
    val DEWHITE_Valid_w = Wire(Bool())  
    val DEWHITE_Result_w = Wire(UInt(1.W))
    val DEWHITE_Seed_w = Wire(UInt(7.W))      
  }

  //decouple assignments
  io.DMA_Data.valid := val_bundle.DMA_Data_Valid_r
  val_bundle.DMA_Data_Fire_w := io.DMA_Data_o.ready & io.DMA_Data_o.valid 
  io.AFIFO_Data.ready := val_bundle.AFIFO_Ready_r
  val_bundle.AFIFO_Fire_w := io.AFIFO_Data_i.ready & io.AFIFO_Data_i.valid

  //Preamble assignments
  val_bundle.Preamble0 := "b01010101".U
  val_bundle.Preamble1 := "b10101010".U

  //output function