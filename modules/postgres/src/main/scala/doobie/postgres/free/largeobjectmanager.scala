// Copyright (c) 2013-2017 Rob Norris
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package doobie.postgres.free

import cats.~>
import cats.effect.Async
import cats.free.{ Free => FF } // alias because some algebras have an op called Free

import org.postgresql.largeobject.LargeObject
import org.postgresql.largeobject.LargeObjectManager

@SuppressWarnings(Array("org.wartremover.warts.Overloading"))
object largeobjectmanager { module =>

  // Algebra of operations for LargeObjectManager. Each accepts a visitor as an alternatie to pattern-matching.
  sealed trait LargeObjectManagerOp[A] {
    def visit[F[_]](v: LargeObjectManagerOp.Visitor[F]): F[A]
  }

  // Free monad over LargeObjectManagerOp.
  type LargeObjectManagerIO[A] = FF[LargeObjectManagerOp, A]

  // Module of instances and constructors of LargeObjectManagerOp.
  object LargeObjectManagerOp {

    // Given a LargeObjectManager we can embed a LargeObjectManagerIO program in any algebra that understands embedding.
    implicit val LargeObjectManagerOpEmbeddable: Embeddable[LargeObjectManagerOp, LargeObjectManager] =
      new Embeddable[LargeObjectManagerOp, LargeObjectManager] {
        def embed[A](j: LargeObjectManager, fa: FF[LargeObjectManagerOp, A]) = Embedded.LargeObjectManager(j, fa)
      }

    // Interface for a natural tansformation LargeObjectManagerOp ~> F encoded via the visitor pattern.
    // This approach is much more efficient than pattern-matching for large algebras.
    trait Visitor[F[_]] extends (LargeObjectManagerOp ~> F) {
      final def apply[A](fa: LargeObjectManagerOp[A]): F[A] = fa.visit(this)

      // Common
      def raw[A](f: LargeObjectManager => A): F[A]
      def embed[A](e: Embedded[A]): F[A]
      def delay[A](a: () => A): F[A]
      def handleErrorWith[A](fa: LargeObjectManagerIO[A], f: Throwable => LargeObjectManagerIO[A]): F[A]
      def async[A](k: (Either[Throwable, A] => Unit) => Unit): F[A]

      // LargeObjectManager
      def create: F[Int]
      def create(a: Int): F[Int]
      def createLO: F[Long]
      def createLO(a: Int): F[Long]
      def delete(a: Int): F[Unit]
      def delete(a: Long): F[Unit]
      def open(a: Int): F[LargeObject]
      def open(a: Int, b: Boolean): F[LargeObject]
      def open(a: Int, b: Int): F[LargeObject]
      def open(a: Int, b: Int, c: Boolean): F[LargeObject]
      def open(a: Long): F[LargeObject]
      def open(a: Long, b: Boolean): F[LargeObject]
      def open(a: Long, b: Int): F[LargeObject]
      def open(a: Long, b: Int, c: Boolean): F[LargeObject]
      def unlink(a: Int): F[Unit]
      def unlink(a: Long): F[Unit]

    }

    // Common operations for all algebras.
    final case class Raw[A](f: LargeObjectManager => A) extends LargeObjectManagerOp[A] {
      def visit[F[_]](v: Visitor[F]) = v.raw(f)
    }
    final case class Embed[A](e: Embedded[A]) extends LargeObjectManagerOp[A] {
      def visit[F[_]](v: Visitor[F]) = v.embed(e)
    }
    final case class Delay[A](a: () => A) extends LargeObjectManagerOp[A] {
      def visit[F[_]](v: Visitor[F]) = v.delay(a)
    }
    final case class HandleErrorWith[A](fa: LargeObjectManagerIO[A], f: Throwable => LargeObjectManagerIO[A]) extends LargeObjectManagerOp[A] {
      def visit[F[_]](v: Visitor[F]) = v.handleErrorWith(fa, f)
    }
    final case class Async1[A](k: (Either[Throwable, A] => Unit) => Unit) extends LargeObjectManagerOp[A] {
      def visit[F[_]](v: Visitor[F]) = v.async(k)
    }

    // LargeObjectManager-specific operations.
    final case object Create extends LargeObjectManagerOp[Int] {
      def visit[F[_]](v: Visitor[F]) = v.create
    }
    final case class  Create1(a: Int) extends LargeObjectManagerOp[Int] {
      def visit[F[_]](v: Visitor[F]) = v.create(a)
    }
    final case object CreateLO extends LargeObjectManagerOp[Long] {
      def visit[F[_]](v: Visitor[F]) = v.createLO
    }
    final case class  CreateLO1(a: Int) extends LargeObjectManagerOp[Long] {
      def visit[F[_]](v: Visitor[F]) = v.createLO(a)
    }
    final case class  Delete(a: Int) extends LargeObjectManagerOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.delete(a)
    }
    final case class  Delete1(a: Long) extends LargeObjectManagerOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.delete(a)
    }
    final case class  Open(a: Int) extends LargeObjectManagerOp[LargeObject] {
      def visit[F[_]](v: Visitor[F]) = v.open(a)
    }
    final case class  Open1(a: Int, b: Boolean) extends LargeObjectManagerOp[LargeObject] {
      def visit[F[_]](v: Visitor[F]) = v.open(a, b)
    }
    final case class  Open2(a: Int, b: Int) extends LargeObjectManagerOp[LargeObject] {
      def visit[F[_]](v: Visitor[F]) = v.open(a, b)
    }
    final case class  Open3(a: Int, b: Int, c: Boolean) extends LargeObjectManagerOp[LargeObject] {
      def visit[F[_]](v: Visitor[F]) = v.open(a, b, c)
    }
    final case class  Open4(a: Long) extends LargeObjectManagerOp[LargeObject] {
      def visit[F[_]](v: Visitor[F]) = v.open(a)
    }
    final case class  Open5(a: Long, b: Boolean) extends LargeObjectManagerOp[LargeObject] {
      def visit[F[_]](v: Visitor[F]) = v.open(a, b)
    }
    final case class  Open6(a: Long, b: Int) extends LargeObjectManagerOp[LargeObject] {
      def visit[F[_]](v: Visitor[F]) = v.open(a, b)
    }
    final case class  Open7(a: Long, b: Int, c: Boolean) extends LargeObjectManagerOp[LargeObject] {
      def visit[F[_]](v: Visitor[F]) = v.open(a, b, c)
    }
    final case class  Unlink(a: Int) extends LargeObjectManagerOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.unlink(a)
    }
    final case class  Unlink1(a: Long) extends LargeObjectManagerOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.unlink(a)
    }

  }
  import LargeObjectManagerOp._

  // Smart constructors for operations common to all algebras.
  val unit: LargeObjectManagerIO[Unit] = FF.pure[LargeObjectManagerOp, Unit](())
  def raw[A](f: LargeObjectManager => A): LargeObjectManagerIO[A] = FF.liftF(Raw(f))
  def embed[F[_], J, A](j: J, fa: FF[F, A])(implicit ev: Embeddable[F, J]): FF[LargeObjectManagerOp, A] = FF.liftF(Embed(ev.embed(j, fa)))
  def delay[A](a: => A): LargeObjectManagerIO[A] = FF.liftF(Delay(() => a))
  def handleErrorWith[A](fa: LargeObjectManagerIO[A], f: Throwable => LargeObjectManagerIO[A]): LargeObjectManagerIO[A] = FF.liftF[LargeObjectManagerOp, A](HandleErrorWith(fa, f))
  def raiseError[A](err: Throwable): LargeObjectManagerIO[A] = delay(throw err)
  def async[A](k: (Either[Throwable, A] => Unit) => Unit): LargeObjectManagerIO[A] = FF.liftF[LargeObjectManagerOp, A](Async1(k))

  // Smart constructors for LargeObjectManager-specific operations.
  val create: LargeObjectManagerIO[Int] = FF.liftF(Create)
  def create(a: Int): LargeObjectManagerIO[Int] = FF.liftF(Create1(a))
  val createLO: LargeObjectManagerIO[Long] = FF.liftF(CreateLO)
  def createLO(a: Int): LargeObjectManagerIO[Long] = FF.liftF(CreateLO1(a))
  def delete(a: Int): LargeObjectManagerIO[Unit] = FF.liftF(Delete(a))
  def delete(a: Long): LargeObjectManagerIO[Unit] = FF.liftF(Delete1(a))
  def open(a: Int): LargeObjectManagerIO[LargeObject] = FF.liftF(Open(a))
  def open(a: Int, b: Boolean): LargeObjectManagerIO[LargeObject] = FF.liftF(Open1(a, b))
  def open(a: Int, b: Int): LargeObjectManagerIO[LargeObject] = FF.liftF(Open2(a, b))
  def open(a: Int, b: Int, c: Boolean): LargeObjectManagerIO[LargeObject] = FF.liftF(Open3(a, b, c))
  def open(a: Long): LargeObjectManagerIO[LargeObject] = FF.liftF(Open4(a))
  def open(a: Long, b: Boolean): LargeObjectManagerIO[LargeObject] = FF.liftF(Open5(a, b))
  def open(a: Long, b: Int): LargeObjectManagerIO[LargeObject] = FF.liftF(Open6(a, b))
  def open(a: Long, b: Int, c: Boolean): LargeObjectManagerIO[LargeObject] = FF.liftF(Open7(a, b, c))
  def unlink(a: Int): LargeObjectManagerIO[Unit] = FF.liftF(Unlink(a))
  def unlink(a: Long): LargeObjectManagerIO[Unit] = FF.liftF(Unlink1(a))

  // LargeObjectManagerIO is an Async
  implicit val AsyncLargeObjectManagerIO: Async[LargeObjectManagerIO] =
    new Async[LargeObjectManagerIO] {
      val M = FF.catsFreeMonadForFree[LargeObjectManagerOp]
      def pure[A](x: A): LargeObjectManagerIO[A] = M.pure(x)
      def handleErrorWith[A](fa: LargeObjectManagerIO[A])(f: Throwable => LargeObjectManagerIO[A]): LargeObjectManagerIO[A] = module.handleErrorWith(fa, f)
      def raiseError[A](e: Throwable): LargeObjectManagerIO[A] = module.raiseError(e)
      def async[A](k: (Either[Throwable,A] => Unit) => Unit): LargeObjectManagerIO[A] = module.async(k)
      def flatMap[A, B](fa: LargeObjectManagerIO[A])(f: A => LargeObjectManagerIO[B]): LargeObjectManagerIO[B] = M.flatMap(fa)(f)
      def tailRecM[A, B](a: A)(f: A => LargeObjectManagerIO[Either[A, B]]): LargeObjectManagerIO[B] = M.tailRecM(a)(f)
      def suspend[A](thunk: => LargeObjectManagerIO[A]): LargeObjectManagerIO[A] = M.flatten(module.delay(thunk))
    }

}

