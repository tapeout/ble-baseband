import chisel3._
import chisel3.experimental.dontTouch
import PacketAssembler.PacketAssembler
import PacketAssembler.PacketDisAssembler

class ble extends Module {
    val io = IO(new Bundle {})
    val a = Module(new PacketAssembler {
        override def desiredName = "ble_packetassembler"
    })
    dontTouch(a.io)
    val b = Module(new PacketDisAssembler {
        override def desiredName = "ble_packetdisassembler"
    })
    dontTouch(b.io)
}

object elaborate_ble extends App {
    chisel3.Driver.execute(args, () => new ble)
}
