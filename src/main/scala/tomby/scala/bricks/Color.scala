package tomby.scala.bricks

import scala.util.Random

sealed trait Color
case class Red() extends Color
case class Blue() extends Color
case class Yellow() extends Color
case class Green() extends Color

object ColorGenerator {

  private val colors = Array(Red(), Green(), Blue(), Yellow())
  
  private val random = new Random
  
  def randomColor(position: Position): Color = 
    colors(random.nextInt(colors.length))

}