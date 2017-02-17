package tomby.scala.bricks

class Position(val x: Int, val y: Int) {

  def neighbors = Array(up, down, right, left)
  
  def up: Position = {
    new Position(x, y + 1)
  }
  
  def down: Position = {
    new Position(x, y - 1)
  }
  
  def right: Position = {
    new Position(x + 1, y)
  }
  
  def left: Position = {
    new Position(x - 1, y)
  }

  def distance(other: Position): Double = {
    val diffx = x - other.x
    val diffy = y - other.y
    math.sqrt(math.pow(diffx, 2) + math.pow(diffy, 2))
  }
  
  def adjacent(other: Position): Boolean = {
    other != null && distance(other) == 1.0
  }

  override def toString(): String = {
    x + ":" + y
  }
  
  override def hashCode(): Int = {
    x.hashCode + 31 * y.hashCode
  }
  
  override def equals(that: Any): Boolean = {
    that match {
    	case other : Position => other != null && this.x == other.x && this.y == other.y
    	case _ => false
    }
  }

}