import chisel3._
import chisel3.experimental.dontTouch
import scum3b.PDATop

object elaborate_pdatop extends App {
    chisel3.Driver.execute(args, () => new PDATop)
}
