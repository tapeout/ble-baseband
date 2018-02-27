class DisAssembler extends Module {
  val io = IO(new Bundle {
    //DMA, REG
    val DMA_Switch_i = Input(Bool())
    val REG_AA_i = Input(UInt(32.W))   
    val REG_CRC_Seed_i = Input(UInt(24.W))
    val REG_DeWhite_Seed_i = Input(UInt(7.W))

    val DMA_Data_o = Decoupled(UInt(8.W))//decouple(sink): data, puch, full
    val DMA_Length_o = Decoupled(UInt(8.W))
    val DMA_Flag_AA_o = Decoupled(Bool())
    val DMA_Flag_CRC_o = Decoupled(Bool())     

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
    val nextPacket_w = Wire(Bool())  
    val PDU_Length_r = RegInit(UInt(8.W), 0.U)
    val PDU_Length_Valid_r = RegInit(Bool(), false.B)
    val Flag_AA_r = RegInit(Bool(), false.B)
    val Flag_AA_Valid_r = RegInit(Bool(), false.B)
    val Flag_CRC_r = RegInit(Bool(), false.B)
    val Flag_CRC_Valid_r = RegInit(Bool(), false.B)

    //Preamble
    val Preamble0 = Wire(UInt(8.W))
    val Preamble1 = Wire(UInt(8.W))
    val Preamble01 = Wire(UInt(8.W))

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
    val CRC_Data_w = Wire(UInt(1.W))
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

  //assignments
    //Decouple firing
  val_bundle.DMA_Data_Fire_w := io.DMA_Data_o.ready & io.DMA_Data_o.valid 
  val_bundle.AFIFO_Fire_w := io.AFIFO_Data_i.ready & io.AFIFO_Data_i.valid
  val_bundle.PDU_Length_Fire_w := io.DMA_Length_o.ready & io.DMA_Length_o.valid
  val_bundle.Flag_AA_Fire_w := io.DMA_Flag_AA_o.ready & io.DMA_Flag_AA_o.valid
  val_bundle.Flag_CRC_Fire_w := io.DMA_Flag_CRC_o.ready & io.DMA_Flag_CRC_o.valid

    //preamble hard code
  val_bundle.Preamble0 := "b01010101".U
  val_bundle.Preamble1 := "b10101010".U
  when(io.REG_AA_i(0) === 0.U){
    val_bundle.Preamble01 := val_bundle.Preamble0    
  }.otherwise{
    val_bundle.Preamble01 := val_bundle.Preamble1     
  }


  //output function
  when(val_bundle.state_r === IDLE || val_bundle.state_r === PREAMBLE){
    io.DMA_Data_o.bits := 0.U
  }.otherwise{//AA, PDU_HEADER, PDU_PAYLOAD, CRC
    io.DMA_Data_o.bits := val_bundle.data_r 
  }

  io.DMA_Length_o.bits := val_bundle.PDU_Length_r
  io.DMA_Length_o.valid := val_bundle.PDU_Length_Valid_r
  io.DMA_Flag_AA_o.bits := val_bundle.Flag_AA_r
  io.DMA_Flag_AA_o.valid := val_bundle.Flag_AA_Valid_r
  io.DMA_Flag_CRC_o.bits := val_bundle.Flag_CRC_r
  io.DMA_Flag_CRC_o.valid := val_bundle.Flag_CRC_Valid_r

  io.DMA_Data_o.valid := val_bundle.DMA_Data_Valid_r
  io.AFIFO_Data_i.ready := val_bundle.AFIFO_Ready_r

  //default
  val_bundle.state_w      := val_bundle.state_r
  val_bundle.counter_w    := val_bundle.counter_r
  val_bundle.counter_byte_w := val_bundle.counter_byte_r
  val_bundle.data_w     := val_bundle.data_r

  val_bundle.PDU_Length_r   := val_bundle.PDU_Length_r//note: preserve value
  val_bundle.PDU_Length_Valid_r := val_bundle.PDU_Length_Valid_r 
  val_bundle.Flag_AA_r := val_bundle.Flag_AA_r
  val_bundle.Flag_AA_Valid_r := val_bundle.Flag_AA_Valid_r
  val_bundle.Flag_CRC_r := val_bundle.Flag_CRC_r
  val_bundle.Flag_CRC_Valid_r := val_bundle.Flag_CRC_Valid_r 

  val_bundle.DMA_Data_Valid_r := val_bundle.DMA_Data_Valid_r
  val_bundle.AFIFO_Ready_r  := val_bundle.AFIFO_Ready_r

  //StateTransition with counter updates
    //Note: counter_r updates when DMA_Data_Fire_w
    //Note: counter_byte_r updates when AFIFO_Fire_w
  when(val_bundle.state_r === IDLE){
    when(io.DMA_Switch_i === true.B && val_bundle.nextPacket_w === true.B){//note: DMA_Switch_i usage
      val_bundle.state_w := PREAMBLE
    }.otherwise{
      val_bundle.state_w := IDLE
    }
  }.elsewhen(val_bundle.state_r === PREAMBLE){
    when(val_bundle.data_w === val_bundle.Preamble01){//note: data_w or data_r
      val_bundle.state_w := AA
      val_bundle.counter_w := 0.U
      val_bundle.counter_byte_w := 0.U
    }.otherwise{
      val_bundle.state_w := PREAMBLE
    }   
  }.elsewhen(val_bundle.state_r === AA){
    when(val_bundle.counter_r === 3.U && val_bundle.DMA_Data_Fire_w === true.B){//note
      val_bundle.state_w := PDU_HEADER
      val_bundle.counter_w := 0.U
      val_bundle.counter_byte_w := 0.U
    }.otherwise{
      val_bundle.state_w := AA
      when(val_bundle.DMA_Data_Fire_w === true.B){
        val_bundle.counter_w := val_bundle.counter_r+1.U     
      }       
    }     
  }.elsewhen(val_bundle.state_r === PDU_HEADER){
    when(val_bundle.counter_r === 1.U && val_bundle.DMA_Data_Fire_w === true.B){//note
      val_bundle.state_w := PDU_PAYLOAD
      val_bundle.counter_w := 0.U
      val_bundle.counter_byte_w := 0.U
    }.otherwise{
      val_bundle.state_w := PDU_HEADER
      when(val_bundle.DMA_Data_Fire_w === true.B){
        val_bundle.counter_w := val_bundle.counter_r+1.U     
      }       
    }     
  }.elsewhen(val_bundle.state_r === PDU_PAYLOAD){
    when(val_bundle.counter_r === val_bundle.PDU_Length_r-1.U && val_bundle.DMA_Data_Fire_w === true.B){//note
      val_bundle.state_w := CRC
      val_bundle.counter_w := 0.U
      val_bundle.counter_byte_w := 0.U
    }.otherwise{
      val_bundle.state_w := PDU_PAYLOAD
      when(val_bundle.DMA_Data_Fire_w === true.B){
        val_bundle.counter_w := val_bundle.counter_r+1.U     
      }       
    }     
  }.elsewhen(val_bundle.state_r === CRC){
    when(val_bundle.counter_r === 2.U && val_bundle.DMA_Data_Fire_w === true.B){//note
      val_bundle.state_w := IDLE
      val_bundle.counter_w := 0.U
      val_bundle.counter_byte_w := 0.U
    }.otherwise{
      val_bundle.state_w := CRC
      when(val_bundle.DMA_Data_Fire_w === true.B){
        val_bundle.counter_w := val_bundle.counter_r+1.U     
      }       
    }   
  }.otherwise{
    val_bundle.state_w := IDLE//error
  }

    //counter_byte updates
  when(val_bundle.AFIFO_Fire_w === true.B){
    when(val_bundle.counter_byte_r === 7.U){
      val_bundle.counter_byte_w := 0.U
    }.otherwise{
      val_bundle.counter_byte_w := val_bundle.counter_byte_r+1.U
    }
  }.otherwise{
    //do nothing
  }

  //packet status
    //next packet
  when(val_bundle.state_r === IDLE && io.DMA_Length_o.ready === true.B && io.DMA_Flag_AA_o.ready === true.B && io.DMA_Flag_CRC_o.ready === true.B){
    val_bundle.nextPacket_w := true.B
  }.otherwise{
    val_bundle.nextPacket_w := false.B      
  }
    //PDU_Length
  when(val_bundle.state_r === PDU_PAYLOAD && val_bundle.counter_r === 0.U && val_bundle.counter_byte_r === 0.U){//note: can change to intuitive statement(add fire_w) with data_w
    val_bundle.PDU_Length_r := val_bundle.data_r
    val_bundle.PDU_Length_Valid_r := true.B
  }.otherwise{
    //do nothing: registers preserve value//note
  }

    //Flag_AA
  when(val_bundle.state_r === AA && val_bundle.counter_r === 1.U && val_bundle.counter_byte_r === 0.U){//note: same as above
    when(val_bundle.data_r != io.REG_AA_i(0,7)){
      val_bundle.Flag_AA_r := true.B
      val_bundle.Flag_AA_Valid_r := true.B      
    }
  }.elsewhen(val_bundle.state_r === AA && val_bundle.counter_r === 2.U && val_bundle.counter_byte_r === 0.U){
    when(val_bundle.data_r != io.REG_AA_i(8,15)){
      val_bundle.Flag_AA_r := true.B
      val_bundle.Flag_AA_Valid_r := true.B      
    }    
  }.elsewhen(val_bundle.state_r === AA && val_bundle.counter_r === 3.U && val_bundle.counter_byte_r === 0.U){
    when(val_bundle.data_r != io.REG_AA_i(16,23)){
      val_bundle.Flag_AA_r := true.B
      val_bundle.Flag_AA_Valid_r := true.B      
    }
  }.elsewhen(val_bundle.state_r === PDU_HEADER && val_bundle.counter_r === 0.U && val_bundle.counter_byte_r === 0.U){
    when(val_bundle.data_r != io.REG_AA_i(24,31)){
      val_bundle.Flag_AA_r := true.B
      val_bundle.Flag_AA_Valid_r := true.B      
    }.otherwise{
      val_bundle.Flag_AA_Valid_r := true.B        
    }
  }.otherwise{
    //do nothing: registers preserve value//note
  }

    //Flag_CRC
  when(val_bundle.state_r === CRC && val_bundle.counter_r === 1.U && val_bundle.counter_byte_r === 0.U){//note: same as above
    when(val_bundle.data_r != val_bundle.CRC_Result_w(0,7)){
      val_bundle.Flag_CRC_r := true.B
      val_bundle.Flag_CRC_Valid_r := true.B      
    }
  }.elsewhen(val_bundle.state_r === CRC && val_bundle.counter_r === 2.U && val_bundle.counter_byte_r === 0.U){
    when(val_bundle.data_r != val_bundle.CRC_Result_w(8,15)){
      val_bundle.Flag_CRC_r := true.B
      val_bundle.Flag_CRC_Valid_r := true.B      
    }   
  }.elsewhen(val_bundle.state_r === IDLE && val_bundle.counter_r === 0.U && val_bundle.counter_byte_r === 0.U){
    when(val_bundle.data_r != val_bundle.CRC_Result_w(16,23)){
      val_bundle.Flag_CRC_r := true.B
      val_bundle.Flag_CRC_Valid_r := true.B      
    }.otherwise{
      val_bundle.Flag_CRC_Valid_r := true.B        
    }
  }.otherwise{
    //do nothing: registers preserve value//note
  }

    //reset for every packet
  when(val_bundle.nextPacket_w === true.B){
    val_bundle.PDU_Length_r := 0.U
    val_bundle.PDU_Length_Valid_r := false.B
    val_bundle.Flag_AA_r := false.B
    val_bundle.Flag_AA_Valid_r := false.B
    val_bundle.Flag_CRC_r := false.B
    val_bundle.Flag_CRC_Valid_r := false.B
  }

  //DMA_Data_Valid_r//note:check corner cases
  when(val_bundle.state_r === IDLE || val_bundle.state_r === PREAMBLE){
    val_bundle.DMA_Data_Valid_r := false.B
  }.otherwise{//AA, PDU_HEADER, PDU_PAYLOAD, CRC
    when(val_bundle.counter_byte_r === 7.U && val_bundle.AFIFO_Fire_w === true.B){
      val_bundle.DMA_Data_Valid_r := true.B
    }.elsewhen(val_bundle.DMA_Data_Fire_w === true.B){
      val_bundle.DMA_Data_Valid_r := false.B
    }
  }

  //AFIFO_Ready_w//note:check corner cases
  when(val_bundle.state_r === IDLE){
    val_bundle.AFIFO_Ready_r := false.B
  }.elsewhen(val_bundle.state_r === PREAMBLE){
    val_bundle.AFIFO_Ready_r := true.B
  }.otherwise{//AA, PDU_HEADER, PDU_PAYLOAD, CRC
    when(val_bundle.counter_byte_r === 7.U && val_bundle.AFIFO_Fire_w === true.B){
      val_bundle.AFIFO_Ready_r := false.B     
    }.elsewhen(val_bundle.DMA_Data_Fire_w === true.B){
      val_bundle.AFIFO_Ready_r := true.B        
    }
  }

  //data
  when(val_bundle.state_r === PDU_HEADER || val_bundle.state_r === PDU_PAYLOAD || val_bundle.state_r === CRC){
    when(val_bundle.AFIFO_Fire_w === true.B){
      val_bundle.data_w(val_bundle.counter_byte_r) := val_bundle.DEWHITE_Result_w
    }
  }.elsewhen(val_bundle.state_r === PREAMBLE){
    when(val_bundle.AFIFO_Fire_w === true.B){
      val_bundle.data_w(7) := io.AFIFO_Data_i.bits //note: subword assignment
      for(i<-0 to 6){//value shifting
        val_bundle.data_w(i) := val_bundle.data_r(i+1)
      }      
    }
  }.elsewhen(val_bundle.state_r === AA){
    when(val_bundle.AFIFO_Fire_w === true.B){
      val_bundle.data_w(val_bundle.counter_byte_r) := io.AFIFO_Data_i.bits
    }
  }.otherwise{//IDLE
    //do nothing or := 0.U
  }

  //CRC
  val_bundle.CRC_Reset_w := val_bundle.nextPacket_w
  when(val_bundle.state_r === PDU_HEADER || val_bundle.state_r === PDU_PAYLOAD){//check corner cases
    val_bundle.CRC_Data_w := val_bundle.DEWHITE_Result_w
    val_bundle.CRC_Valid_w := val_bundle.AFIFO_Fire_w
  }.otherwise{
    val_bundle.CRC_Data_w := 0.U
    val_bundle.CRC_Valid_w := false.B
  }
  //val_bundle.CRC_Result_w wires to CRC module
  val_bundle.CRC_Seed_w := io.REG_CRC_Seed_i

  //dewhitening
  val_bundle.DEWHITE_Reset_w := val_bundle.nextPacket_w
  when(val_bundle.state_r === PDU_HEADER || val_bundle.state_r === PDU_PAYLOAD || val_bundle.state_r === CRC){//check corner cases  
    val_bundle.DEWHITE_Data_w  := io.AFIFO_Data_i.bits
    val_bundle.DEWHITE_Valid_w := val_bundle.AFIFO_Fire_w
  }.otherwise{
    val_bundle.DEWHITE_Data_w  := 0.U
    val_bundle.DEWHITE_Valid_w := false.B
  }
  //val_bundle.DEWHITE_Result_w wires to WHITE module
  val_bundle.DEWHITE_Seed_w := io.REG_DeWhite_Seed_i

  //sequential logic
  val_bundle.state_r      := val_bundle.state_w
  val_bundle.counter_r    := val_bundle.counter_w
  val_bundle.counter_byte_r := val_bundle.counter_byte_w
  val_bundle.data_r     := val_bundle.data_w

}