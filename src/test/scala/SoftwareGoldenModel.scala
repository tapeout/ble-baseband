package SoftwareGoldenModel


class SoftwareGoldenModel (){

	def Whitening_sw(reset: Boolean, bitIn: Int, lfsr: String): (Int, String) = {
	   var len = 7
	   var buf = Array.ofDim[Int](len)
	   var bitOut: Int = 0
	   if (reset)
	      for (i <- 0 to len-1)
	         buf(i) = lfsr(i).asDigit
	   else  {
	      for (i <- 0 to len-1)  
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
	   println("bitOut is ",bitOut)
	   return (bitOut, buf.mkString(""))
	}

}