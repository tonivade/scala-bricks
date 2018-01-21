package tomby.scala.bricks

import cats.effect.IO
import cats.data.StateT
import cats.data.StateT.liftF
import scala.io.StdIn.readLine
import scala.util.Try

object Main extends App {
  
  val read: StateT[IO, Matrix, String] = liftF(IO(readLine()))
  val readInt: StateT[IO, Matrix, Try[Int]] = liftF(IO(Try(readLine().toInt)))
  def print(str: String): StateT[IO, Matrix, Unit] = liftF(IO(println(str)))
  val quit: StateT[IO, Matrix, Unit] = liftF(IO(Unit))

  def click(position: Position) = StateT[IO, Matrix, Unit] {
    matrix => IO(MatrixOps.click(matrix, position), ())
  }
  
  def matrixToString = StateT[IO, Matrix, String] {
    matrix => IO(matrix, matrix.mkString)
  }
  
  val gameover = StateT[IO, Matrix, Boolean] {
    matrix => IO(matrix, matrix.gameover())
  }
  
  val numberOfTiles = StateT[IO, Matrix, Int] {
    matrix => IO(matrix, matrix.tiles.size)
  }
    
  val shuffle = StateT[IO, Matrix, Unit] {
    	matrix => IO(matrix.shuffle(ColorGenerator.randomColor), ())
  }
  
  val printMatrix: StateT[IO, Matrix, Unit] = 
    for {
      str <- matrixToString
      _   <- print(str)
      n   <- numberOfTiles
      _   <- print(s"$n tiles left")
    } yield ()
  
  val exit = 
    for {
      _ <- printMatrix
      n <- numberOfTiles
      _ <- if (n > 0) print("Gameover!!!") else print("You win!!!")
    } yield ()

  def map2[A, B](x: Try[A], y: Try[A])(fa: (A, A) => B): Try[B] = 
    x flatMap { a => y map { b => fa(a, b) } }

  def toPosition(x: Try[Int], y: Try[Int]) = StateT[IO, Matrix, Try[Position]] {
    matrix => IO(matrix, map2(x, y)(Position(_, _)))
  }

  val readPosition: StateT[IO, Matrix, Try[Position]] = for {
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
      _   <- pos.fold(error(_), click(_))
      go  <- gameover
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
    } yield()
  
  val result = mainLoop.runS(Matrix(5, 5)).unsafeRunSync()
}
