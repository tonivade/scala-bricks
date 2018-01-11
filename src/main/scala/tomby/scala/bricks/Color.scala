package tomby.scala.bricks

import scala.util.Random

sealed trait Color
case object Red extends Color
case object Blue extends Color
case object Yellow extends Color
case object Green extends Color

object ColorGenerator {

  private val colors = Array(Red, Green, Blue, Yellow)
  
  private val random = new Random
  
  def randomColor(position: Position): Color = 
    colors(random.nextInt(colors.length))

}