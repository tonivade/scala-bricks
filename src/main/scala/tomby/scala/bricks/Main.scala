package tomby.scala.bricks

import cats.effect.IO
import cats.data.StateT
import cats.data.StateT.liftF

import scala.io.StdIn.readLine
import scala.util.Try
import cats.Applicative
import cats.instances.try_._

object Main extends App {
  
  val read: StateT[IO, Matrix, String] = liftF(IO(readLine()))
  val readInt: StateT[IO, Matrix, Try[Int]] = liftF(IO(Try(readLine().toInt)))
  def print(str: String): StateT[IO, Matrix, Unit] = liftF(IO(println(str)))
  val quit: StateT[IO, Matrix, Unit] = liftF(IO(()))

  def click(position: Position): StateT[IO, Matrix, Unit] = StateT {
    matrix => IO(Matrix.click(matrix, position), ())
  }
  
  val matrixToString: StateT[IO, Matrix, String] = StateT {
    matrix => IO(matrix, matrix.mkString)
  }
  
  val gameOver: StateT[IO, Matrix, Boolean] = StateT {
    matrix => IO(matrix, matrix.gameover)
  }
  
  val numberOfTiles: StateT[IO, Matrix, Int] = StateT {
    matrix => IO(matrix, matrix.tiles.size)
  }
    
  val shuffle: StateT[IO, Matrix, Unit] = StateT {
    matrix => IO(matrix.shuffle(ColorGenerator.randomColor), ())
  }
  
  val printMatrix: StateT[IO, Matrix, Unit] = 
    for {
      str <- matrixToString
      _   <- print(str)
      n   <- numberOfTiles
      _   <- print(s"$n tiles left")
    } yield ()
  
  val exit: StateT[IO, Matrix, Unit] =
    for {
      _ <- printMatrix
      n <- numberOfTiles
      _ <- if (n > 0) print("Gameover!!!") else print("You win!!!")
    } yield ()

  def toPosition(x: Try[Int], y: Try[Int]): StateT[IO, Matrix, Try[Position]] = StateT {
    matrix => IO(matrix, Applicative[Try].map2(x, y)(Position))
  }

  val readPosition: StateT[IO, Matrix, Try[Position]] = 
    for {
      _   <- print("Please enter X")
      x   <- readInt
      _   <- print("Please enter Y")
      y   <- readInt
      pos <- toPosition(x, y)
    } yield pos
  
  def error(t: Throwable): StateT[IO, Matrix, Unit] = 
    for {
      _ <- print(s"Invalid position! $t")
      _ <- loop
    } yield ()  

  val loop: StateT[IO, Matrix, Unit] = 
    for {
      _   <- printMatrix
      pos <- readPosition
      _   <- pos.fold(error, click)
      go  <- gameOver
      _   <- if (go) exit else loop
    } yield ()
  
  val mainLoop: StateT[IO, Matrix, Unit] = 
    for {
      _ <- print("Let's play a game")
      _ <- shuffle
      _ <- loop
      _ <- print("Do you want to play again?")
      s <- read
      _ <- if (s == "y") mainLoop else quit
    } yield ()
  
  val result = mainLoop.runS(Matrix(5, 5)).unsafeRunSync()
}
