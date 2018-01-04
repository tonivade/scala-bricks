package tomby.scala.bricks

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class BoardSpec extends FlatSpec with Matchers {
  
  "A Board" should "be not empty at the beginning" in {
    val board = new Board(3, 3)(_ => "R")
    
    board.isEmpty should be (false)
    board.forall(_.color == "R") should be (true)
    board.matrix().size should be (board.size)
    board.rows().size should be (board.height)
    board.cols().size should be (board.width)
    board.gameover() should be (false)
  }
  
  "A Board" should "be empty when user win" in {
    val board = new Board(3, 3)(_ => "R")
    
    board.click(0, 0) should be (true)
    board.isEmpty should be (true)
    board.gameover() should be (true)
  }
  
  "A Board" should "fall down" in {
    val board = new Board(3, 3)(pos => if (pos.y == 0) "R" else "X")
    
    board.click(2, 0) should be (false)
    board.isEmpty should be (false)
    board.filter(_.position.y == 2).isEmpty should be(true)
    board.filter(_.position.y != 2).forall(_.color == "X") should be(true)
  }
  
  "A Board" should "fall down two rows" in {
    val board = new Board(3, 3)(pos => if (pos.y < 2) "R" else "X")
    
    board.click(2, 0) should be (false)
    board.isEmpty should be (false)
    board.filter(_.position.y > 0).isEmpty should be(true)
    board.filter(_.position.y == 0).forall(_.color == "X") should be(true)
  }
  
  "A Board" should "shift left" in {
    val board = new Board(3, 3)(pos => if (pos.x == 0) "R" else "X")
    
    board.click(0, 0) should be (false)
    board.isEmpty should be (false)
    board.filter(_.position.x == 2).isEmpty should be(true)
    board.filter(_.position.x != 2).forall(_.color == "X") should be(true)
  }
  
  "A Board" should "shift left two collums" in {
    val board = new Board(3, 3)(pos => if (pos.x < 2) "R" else "X")
    
    board.click(0, 0) should be (false)
    board.isEmpty should be (false)
    board.filter(_.position.x > 0).isEmpty should be(true)
    board.filter(_.position.x == 0).forall(_.color == "X") should be(true)
  }
}