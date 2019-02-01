# Packet Assembler
 A packet assembler uses a finite state machine to collect data and form the packet for transmission. The FSM holds six states for each portion of the packet. Several modifications were done for proper state transitions.
```
//state parameter
	val idle :: preamble :: aa :: pdu_header :: pdu_payload :: crc :: Nil = Enum(6)
```
 In **assembler.scala**, the BLE baseband was updated to accommodate to new Chisel standard. In **PAChain.scala**, PA Block is wrapped as LazyModule:
 ```
 class PAThing
(...)(implicit p: Parameters) extends LazyModule {
  // instantiate lazy modules
  val writeQueue = LazyModule(new TLWriteQueue(depthWrite))
  val packet = LazyModule(new TLPABlock())
  val readQueue = LazyModule(new TLReadQueue(depthRead))

  // connect streamNodes of queues and cordic
  readQueue.streamNode := packet.streamNode := writeQueue.streamNode

  lazy val module = new LazyModuleImp(this)
}
 ```
 
 The Matlab golden model is given below to explain how `pdu` (protocol data unit) is constructed.
 ```
 pdu = [pdu_header AdvA payload1 payload2_header payload2_data]
 ```
 Then, it utilizes CRC and whitening modules. Finally, it passes the whole packet to the next module. The Matlab golden model is given below to explain how `packet` is constructed.
 ```
 packet01 = [pre_preamble bpreamble baccess_address pdu_crc_whitened]
 ```

 ## Improvement
  The biggest improvement of design is that: in the former implementation, each packet("PABundle") includes *trigger* (1 bit, denotes the beginning of a BLE packet), *data* (8 bits, the contents of BLE packets) *crc_seed* (24 bits), *white_seed* (7 bits), and *done* (1 bit, denotes the end of a BLE packet). Below is the structure of the former packet IO Bundle.
 ```
 class PacketAssembler extends Module {
  val io = IO(new Bundle {
    val DMA_Trigger_i = Input(Bool())
    val DMA_Data_i = Flipped(DecoupledIO(UInt(8.W)))//decouple(sink): data, pop, empty
    val REG_CRC_Seed_i = Input(UInt(24.W))
    val REG_White_Seed_i = Input(UInt(7.W))
    val DMA_Done_o = Output(Bool())	
    ...
  })

 ```
 Thus each time when transmitting, the effective payload takes up only 8 bits out of 41bits and most parts are repetitive for a single BLE packet. We improved the Bundle structure to include only *trigger* and *data*. 
 ```
 class PAInputBundle extends Bundle {
	val trigger = Output(Bool())
	val data = Output(UInt(8.W))

	override def cloneType: this.type = PAInputBundle().asInstanceOf[this.type]
}
 ```
 

 ## Input and Output Ports
 ```
 class PAInputBundle extends Bundle {
       val trigger = Output(Bool())
       val data = Output(UInt(8.W))
       
       override def cloneType: this.type = PAInputBundle().asInstanceOf[this.type]
}
 ```
Packet Assembler has two inputs. `trigger` notifies the beginning of the packet. `data` is an 8-bit sequence from BLE packet. 

```
class PAOutputBundle extends Bundle {
	val data = Output(UInt(1.W))
	val done = Output(Bool())

	override def cloneType: this.type = PAOutputBundle().asInstanceOf[this.type]
}
```
Packet Assembler has two outputs. `data` is a 1-bit result. `done` is a 1-bit output that notifies the end of the packet.

## Test

A simple scala test is provided for the Packet Assembler module. To perform the unit test, type `sbt` in the root directory. After that, type `testOnly PacketAssembler.test.PacketAssemblerTester` in the sbt terminal.
