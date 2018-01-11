package tomby.scala.bricks

case class Tile(position: Position, color: Color) {

  def adjacent(other: Tile): Boolean = {
    position.adjacent(other.position) && color == other.color
  }

}