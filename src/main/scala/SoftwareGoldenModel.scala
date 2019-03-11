package SoftwareGoldenModel
import scala.util._


object SoftwareGoldenModel{

  val initSeed = new Random().nextInt()
  printf("Inital seed is %d\n", initSeed)
  var rand = new Random(initSeed)

  def setSeed(seed: Int) = {
    rand = new Random(seed)
    printf("Set seed to %d\n", seed)
  }

  def getRandomPackets() : (Array[String]) = {
    // a function of generating random bluetooth packets
    // warning: this packet is only valid regarding large sections: preamble, AA, payload and CRC
    // in reality there should be subsections inside PDU that also follow certain restrictions, but these are too hard to randomize
    val length = rand.nextInt(32) + 12 //length of the random packet; should be between 12 and 43 bytes
    val input = Array.tabulate(length)(_ => {
      rand.nextInt(256)
    }) //generate an array of random 8-bit binary numbers
    input(5) = length - 6 //set the byte corresponding to packet length
    return(input.map(x => String.format("%8s", x.toBinaryString).replace(" ", "0"))) //convert the binary number to 8-bit binary string
    }

  def Whitening_sw(reset: Boolean, bitIn: Int, lfsr: String): (Int, String) = {
    var len = 7
    var buf = Array.ofDim[Int](len)
    var bitOut: Int = 0
    if (reset)
      for (i <- 0 to len - 1)
        buf(i) = lfsr(i).asDigit
    else {
      for (i <- 0 to len - 1)
        buf(i) = lfsr(i).asDigit

      //for (i <- 0 to len-1) print(buf(i))
      //println()
      var temp = buf(6)
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

  def Crc_sw(reset: Boolean, bitIn: Int, lfsr: String): (String) = {
    var len = 24
    var buf = Array.ofDim[Int](len)
    var bitOut: Int = 0
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
      var temp = buf(23) ^ bitIn
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


  def pa_sw(packetIn: Array[String]): (Array[String]) = {
    // software model for packet assembler
    // takes in array of binary strings and outputs an array of binary strings 
    val len = packetIn.size
    val buf = new Array[String](len + 4)
    val preamble = if(packetIn(0).slice(7,8) == "0") "10101010" else "01010101" //preamble determined by first bit output of aa
    var crc_lfsr = "010101010101010101010101".reverse
    var white_lfsr = "1100101"

    
    buf(0) = preamble
    for(i <- 0 to len + 2){
      if(i < 4){
        buf(i + 1) = packetIn(i) // Access address; no whitening/crc, just output
      }else{
        var stringOut: String = ""
        var bitIn: Int = 0
        for(j <- 0 to 7){
          if(i < len){
            bitIn = packetIn(i).slice(7-j,8-j).toInt
            crc_lfsr = Crc_sw(false, bitIn, crc_lfsr) //for payload: calculate crc
          }else{
            bitIn = crc_lfsr.reverse.slice(j+(i-len)*8, j+1+(i-len)*8).toInt 
          }
          var init_res = Whitening_sw(false, bitIn, white_lfsr) //run whitening for payload & crc sections
          val bitOut = init_res._1
          white_lfsr = init_res._2
          stringOut = stringOut + bitOut.toString
        }
        buf(i + 1) = stringOut.reverse
      }
    }
    return buf
  }

  def main(args: Array[String]) = {
    val output = getRandomPackets()
    //output.foreach((element:String) => printf("%s\n", element))
    val pa = pa_sw(output)
    val pa_int = pa.map(x => Integer.parseInt(x, 2))
    println("length of the packet is "+pa.size+"")
    pa.foreach((element:String) => printf("%s\n", element))
    pa_int.foreach((element:Int) => printf("%h\n", element)) 
  }  

}
