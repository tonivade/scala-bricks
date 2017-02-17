package tomby.scala.bricks

import scala.util.Random

class DefaultColorGenerator(colors:Array[String]) extends ColorGenerator {
  
  private val random = new Random
  
  override def nextColor: String = {
    colors(random.nextInt(colors.length))
  }

}