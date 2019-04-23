package SoftwareGoldenModel
import scala.util._

object SoftwareGoldenModel {

  val initSeed = new Random().nextInt()
  printf("Inital seed is %d\n", initSeed)
  var rand = new Random(initSeed)

  /**
    * setSeed
    * set the seed for random value in the object for debug purposes
    * @param seed: random seed for random packet generator
    */
  def setSeed(seed: Int) = {
    rand = new Random(seed)
    printf("Set seed to %d\n", seed)
  }

  /**
    * getRandomPackets
    * A function of generating random bluetooth packets
    * Warning: this packet is only valid regarding large sections: preamble, AA, payload and CRC
    * In reality there should be subsections inside PDU that also follow certain restrictions, but these are too hard to randomize
    * Length of the packet should be between 12 and 43 bytes
    * The sixth byte of the packet should be the length of the packet, corresponding to the BLE spec
    * @return The function returns an array of 8-bit bit string
    */
  def getRandomPackets(): (Array[String]) = {
    val length = rand.nextInt(32) + 12
    val input = Array.tabulate(length)(_ => {
      rand.nextInt(256)
    })
    input(5) = length - 6
    return (input.map(
      x => String.format("%8s", x.toBinaryString).replace(" ", "0")
    ))
  }

  /**
    * Whitening_sw
    * A software model that simulates the whitening procedure in packet assembler/disassembler
    * @param reset reset the whitening block to initial state and set the lfsr to initial seed value
    * @param bitIn one bit input
    * @param lfsr the 7-bit value of lfsr register in bit string format
    * @return the function returns the one bit output and the updated value of lfsr after input
    */
  def Whitening_sw(reset: Boolean, bitIn: Int, lfsr: String): (Int, String) = {
    val len = 7
    val buf = Array.ofDim[Int](len)
    var bitOut: Int = 0
    if (reset)
      for (i <- 0 to len - 1)
        buf(i) = lfsr(i).asDigit
    else {
      for (i <- 0 to len - 1)
        buf(i) = lfsr(i).asDigit

      //for (i <- 0 to len-1) print(buf(i))
      //println()
      val temp = buf(6)
      buf(6) = buf(5)
      buf(5) = buf(4)
      buf(4) = buf(3) ^ temp
      buf(3) = buf(2)
      buf(2) = buf(1)
      buf(1) = buf(0)
      buf(0) = temp

      bitOut = bitIn ^ temp
    }
    //for (i <- 0 to len-1) println(buf(i))
    //println("	       Software Output = " + bitOut)
    return (bitOut, buf.mkString(""))
  }

  /**
    * Crc_sw
    * A software model that simulates the crc procedure in packet assembler/disassembler
    * @param reset reset the crc block to initial state and set the lfsr to initial seed value
    * @param bitIn one bit input
    * @param lfsr the 24-bit value of lfsr register in bit string format
    * @return the function returns the updated value of lfsr after input
    */
  def Crc_sw(reset: Boolean, bitIn: Int, lfsr: String): (String) = {
    val len = 24
    val buf = Array.ofDim[Int](len)
    if (reset)
      for (i <- 0 to len - 1)
        //buf(i) = lfsr(i).asDigit
        buf(i) = lfsr(len - 1 - i).asDigit
    // position 23 shall be set as the MSB
    else {
      for (i <- 0 to len - 1)
        buf(i) = lfsr(i).asDigit

      //for (i <- 0 to len-1) print(buf(i))
      //println()
      val temp = buf(23) ^ bitIn
      buf(23) = buf(22)
      buf(22) = buf(21)
      buf(21) = buf(20)
      buf(20) = buf(19)
      buf(19) = buf(18)
      buf(18) = buf(17)
      buf(17) = buf(16)
      buf(16) = buf(15)
      buf(15) = buf(14)
      buf(14) = buf(13)
      buf(13) = buf(12)
      buf(12) = buf(11)
      buf(11) = buf(10)
      buf(10) = buf(9) ^ temp
      buf(9) = buf(8) ^ temp
      buf(8) = buf(7)
      buf(7) = buf(6)
      buf(6) = buf(5) ^ temp
      buf(5) = buf(4)
      buf(4) = buf(3) ^ temp
      buf(3) = buf(2) ^ temp
      buf(2) = buf(1)
      buf(1) = buf(0) ^ temp
      buf(0) = temp
    }
    //for (i <- 0 to len-1) println(buf(i))
    //println(s"Software Output = $buf")
    // println(
    //   "	       Software Output = " + buf(0) + buf(1) + buf(2) + buf(3) + buf(4) + buf(
    //     5
    //   ) + buf(6) + buf(7) + buf(8) + buf(9) + buf(10) + buf(11) + buf(12) + buf(
    //     13
    //   ) + buf(14) + buf(15) + buf(16) + buf(17) + buf(18) + buf(19) + buf(20) + buf(
    //     21
    //   ) + buf(22) + buf(23)
    // )
    return (buf.mkString(""))
  }

  /**
    * pa_sw
    * A software model that follows the behavior of packet assembler
    * This model takes in an array of binary strings (packet) and outputs an array of binary strings (after preamble, crc and whitening)
    * The output packet is 4 bytes longer than the input packet because of the addition of preamble and crc
    * Preamble is determined based on the first bit of access address
    * Whitening is performed on payload and crc; CRC is calculated only on payload
    * @param packetIn An array of 8-bit strings
    * @return output packet processed by packet assembler
    */
  def pa_sw(packetIn: Array[String]): (Array[String]) = {

    val len = packetIn.size
    val buf = new Array[String](len + 5)
    val preamble =
      if (packetIn(0).slice(7, 8) == "0") "10101010" else "01010101"
    val prepreamble = 
      if (packetIn(0).slice(7, 8) == "0") "00001111" else "11110000"
    var crc_lfsr = "010101010101010101010101".reverse
    var white_lfsr = "1100101"

    buf(0) = prepreamble
    buf(1) = preamble
    for (i <- 0 to len + 2) {
      if (i < 4) {
        buf(i + 2) = packetIn(i)
      } else {
        var stringOut: String = ""
        var bitIn: Int = 0
        for (j <- 0 to 7) {
          if (i < len) {
            bitIn = packetIn(i).slice(7 - j, 8 - j).toInt
            crc_lfsr = Crc_sw(false, bitIn, crc_lfsr)
          } else {
            bitIn = crc_lfsr.reverse
              .slice(j + (i - len) * 8, j + 1 + (i - len) * 8)
              .toInt
          }
          val init_res = Whitening_sw(false, bitIn, white_lfsr)
          val bitOut = init_res._1
          white_lfsr = init_res._2
          stringOut = stringOut + bitOut.toString
        }
        buf(i + 2) = stringOut.reverse
      }
    }
    return buf
  }

}
