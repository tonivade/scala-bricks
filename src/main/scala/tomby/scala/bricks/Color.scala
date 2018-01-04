package tomby.scala.bricks

import scala.util.Random

class ColorGenerator(val colors:Array[String]) {
  
  private val random = new Random
  
  def randomColor(position: Position): String = {
    colors(random.nextInt(colors.length))
  }

}