import chisel3._
import chisel3.experimental.dontTouch
import firrtl._
import firrtl.annotations.CircuitName
import firrtl.annotations.ModuleName
import firrtl.passes.{Pass, PassException}

import scala.io.Source

import scum3b.PDATop

/** Rename modules according to the given map. */
// Copied from https://github.com/ucb-bar/fcl-floorplan/blob/master/src/main/scala/fcl/transforms/ReplaceExtModule.scala
class RenameModulePass(nameMap: Map[String, String]) extends Pass {
  import firrtl.ir._

  override def run(c: Circuit): Circuit = {
    if (nameMap.isEmpty) {
      c
    } else {
      val newMain = nameMap.get(c.main) match {
        case Some(replacement) => replacement
        case _ => c.main
      }
      c.copy(modules=c.modules.map(onModule), main=newMain)
    }
  }

  private def onStatement(stmt: Statement): Statement = stmt match {
    case inst: DefInstance if nameMap.contains(inst.module) => inst.copy(module=nameMap(inst.module))
    case b: Block => b.mapStmt(onStatement)
    case x => x
  }

  private def onModule(dm: DefModule): DefModule = {
    // If this module is to be renamed, change this module's name first.
    val dmWithCorrectTop = if (nameMap.contains(dm.name)) {
      dm match {
        case m: Module => m.copy(name=nameMap(m.name))
        case _: ExtModule => throw new PassException("Cannot deal with renaming an ExtModule yet")
      }
    } else dm
    // Now map each of the statements in the module.
    dmWithCorrectTop.mapStmt(onStatement)
  }
}

object elaborate_pdatop extends App {
  chisel3.Driver.execute(args ++ Array("--no-run-firrtl"), () => new PDATop)

  val parsedInput = firrtl.Parser.parse(Source.fromFile("ble_pdatop.fir").getLines(), firrtl.Parser.UseInfo)
  val state = firrtl.CircuitState(parsedInput, firrtl.ChirrtlForm, None)

  // Get list of all modules' names.
  // Don't consider ExtModules.
  // Don't include modules already beginning with ble_.
  val modules: Seq[String] = state.circuit.modules
    .filterNot(_ match {
      case m: firrtl.ir.ExtModule => true
      case _ => false
    })
    .map(_.name)
    .filterNot(_.startsWith("ble_"))

  // Create a Map to add a "ble_pdatop_" prefix to these modules
  val renameMap = modules.map(m => (m, "ble_pdatop_" + m)).toMap

  val state2 = state.copy(circuit = (new RenameModulePass(renameMap)).run(state.circuit))

  // Compile to Verilog
  val res = (new VerilogCompiler()).compileAndEmit(state2)
  val verilog: String = res.getEmittedCircuit.value

  // Write output to file.
  import java.io.PrintWriter
  new PrintWriter("pdatop.v") { write(verilog); close }
}
