package tomby.scala.bricks

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class BoardSpec extends FlatSpec with Matchers {
  
  "A Board" should "be not empty at the beginning" in {
    val board = Board(3, 3).shuffle(_ => Red)
    
    board.tiles.isEmpty should be (false)
    board.tiles.forall(_.color == Red) should be (true)
    board.gameover() should be (false)
    board.win() should be (false)
  }
  
  "A Board" should "be empty when user win" in {
    val board = Board(3, 3).shuffle(_ => Red)

    val _board = board.click(0, 0)
    
    _board.tiles.isEmpty should be (true)
    _board.gameover() should be (true)
    _board.win() should be (true)
  }
  
  "A Board" should "fall down" in {
    val board = Board(3, 3).shuffle(pos => if (pos.y == 0) Red else Yellow)
    
    val _board = board.click(2, 0)

    _board.tiles.isEmpty should be (false)
    _board.gameover() should be (false)
    _board.tiles.filter(_.position.y == 2).isEmpty should be(true)
    _board.tiles.filter(_.position.y != 2).forall(_.color == Yellow) should be(true)
  }
  
  "A Board" should "fall down two rows" in {
    val board = Board(3, 3).shuffle(pos => if (pos.y < 2) Red else Yellow)
    
    val _board = board.click(2, 0)

    _board.tiles.isEmpty should be (false)
    _board.gameover() should be (false)
    _board.tiles.filter(_.position.y > 0).isEmpty should be(true)
    _board.tiles.filter(_.position.y == 0).forall(_.color == Yellow) should be(true)
  }
  
  "A Board" should "shift left" in {
    val board = Board(3, 3).shuffle(pos => if (pos.x == 0) Red else Yellow)
    
    val _board = board.click(0, 0)

    _board.tiles.isEmpty should be (false)
    _board.gameover() should be (false)
    _board.tiles.filter(_.position.x == 2).isEmpty should be(true)
    _board.tiles.filter(_.position.x != 2).forall(_.color == Yellow) should be(true)
  }
  
  "A Board" should "shift left two collums" in {
    val board = Board(3, 3).shuffle(pos => if (pos.x < 2) Red else Yellow)
    
    val _board = board.click(0, 0)

    _board.tiles.isEmpty should be (false)
    _board.gameover() should be (false)
    _board.tiles.filter(_.position.x > 0).isEmpty should be(true)
    _board.tiles.filter(_.position.x == 0).forall(_.color == Yellow) should be(true)
  }
}