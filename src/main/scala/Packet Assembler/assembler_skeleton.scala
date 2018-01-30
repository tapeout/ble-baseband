class Assembler extends Module {
  val io = IO(new Bundle {
	//dma(AES)
    val tx_trigger = Input(UInt(1.W))
    val tx_dma_data = Input(UInt(8.W))//AA, PDU, CRC's seed
    val tx_dma_valid = Input(UInt(1.W))	

    val tx_dma_ready = Output(UInt(1.W))
    val tx_done = Output(UInt(1.W))	
	
	//send(whitening)
    val tx_send_ready = Input(UInt(1.W))
      
    val tx_send_data = Output(UInt(8.W))
    val tx_send_valid = Output(UInt(1.W))
  })
  io.out := io.in
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