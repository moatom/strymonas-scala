package strymonas

import scala.quoted._
import scala.quoted.util._

import scala.language.implicitConversions

trait Cde {
   type Cde[A]
   type Var[A]

   implicit def toExpr[A](x: Cde[A]): Expr[A]
   implicit def ofExpr[A](x: Expr[A]): Cde[A]

   def inj[T: Liftable](c1: T)(using QuoteContext): Cde[T]

   def letl[A: Type, W: Type](x: Cde[A])(k: (Cde[A] => Cde[W]))(using QuoteContext): Cde[W]
   // Rename to newref?
   def letVar[A: Type, W: Type](x: Cde[A])(k: (Var[A] => Cde[W]))(using QuoteContext): Cde[W]

   def seq[A: Type](c1: Cde[Unit], c2: Cde[A])(using QuoteContext): Cde[A]
   def unit(using QuoteContext): Cde[Unit]

   // Booleans
   def bool(c1: Boolean)(using QuoteContext): Cde[Boolean]
   def not(c1: Cde[Boolean])(using QuoteContext): Cde[Boolean]
   def land(c1: Cde[Boolean], c2: Cde[Boolean])(using QuoteContext): Cde[Boolean]
   def  lor(c1: Cde[Boolean], c2: Cde[Boolean])(using QuoteContext): Cde[Boolean]
   implicit class BoolCde(val c1: Cde[Boolean]) {
      def &&(c2: Cde[Boolean])(using QuoteContext): Cde[Boolean] = land(c1, c2)
      def ||(c2: Cde[Boolean])(using QuoteContext): Cde[Boolean] =  lor(c1, c2)
   }

   // Numbers
   // def truncate(c1: Cde[Float])(using QuoteContext): Cde[Int]
   def int(c1: Int)(using QuoteContext): Cde[Int]
   def imin(c1: Cde[Int], c2: Cde[Int])(using QuoteContext): Cde[Int]
   def imax(c1: Cde[Int], c2: Cde[Int])(using QuoteContext): Cde[Int]

   def add(c1: Cde[Int], c2: Cde[Int])(using QuoteContext):  Cde[Int]
   def sub(c1: Cde[Int], c2: Cde[Int])(using QuoteContext):  Cde[Int]
   def mul(c1: Cde[Int], c2: Cde[Int])(using QuoteContext):  Cde[Int]
   def div(c1: Cde[Int], c2: Cde[Int])(using QuoteContext):  Cde[Int]
   def modf(c1: Cde[Int], c2: Cde[Int])(using QuoteContext): Cde[Int]

   def  lt(c1: Cde[Int], c2: Cde[Int])(using QuoteContext):     Cde[Boolean]
   def  gt(c1: Cde[Int], c2: Cde[Int])(using QuoteContext):     Cde[Boolean]
   def leq(c1: Cde[Int], c2: Cde[Int])(using QuoteContext):     Cde[Boolean]
   def geq(c1: Cde[Int], c2: Cde[Int])(using QuoteContext):     Cde[Boolean]
   def eq_temp(c1: Cde[Int], c2: Cde[Int])(using QuoteContext): Cde[Boolean]
   implicit class IntCde(val c1: Cde[Int]) {
      def +(c2: Cde[Int])(using QuoteContext):   Cde[Int] = add(c1, c2)
      def -(c2: Cde[Int])(using QuoteContext):   Cde[Int] = sub(c1, c2)
      def *(c2: Cde[Int])(using QuoteContext):   Cde[Int] = mul(c1, c2)
      def /(c2: Cde[Int])(using QuoteContext):   Cde[Int] = div(c1, c2)
      def mod(c2: Cde[Int])(using QuoteContext): Cde[Int] = modf(c1, c2)

      def <(c2: Cde[Int])(using QuoteContext):   Cde[Boolean] =  lt(c1 ,c2)
      def >(c2: Cde[Int])(using QuoteContext):   Cde[Boolean] =  gt(c1 ,c2)
      def <=(c2: Cde[Int])(using QuoteContext):  Cde[Boolean] = leq(c1, c2)
      def >=(c2: Cde[Int])(using QuoteContext):  Cde[Boolean] = geq(c1, c2)
      def ===(c2: Cde[Int])(using QuoteContext): Cde[Boolean] =  eq_temp(c1, c2)
   }


   // Control operators
   def cond[A: Type](cnd: Cde[Boolean], bt: Cde[A], bf: Cde[A])(using QuoteContext): Cde[A]
   def if_(cnd: Cde[Boolean], bt: Cde[Unit], bf: Cde[Unit])(using QuoteContext): Cde[Unit]
   def if1(cnd: Cde[Boolean], bt: Cde[Unit])(using QuoteContext): Cde[Unit]
   def for_(upb: Cde[Int], guard: Option[Cde[Boolean]], body: Cde[Int] => Cde[Unit])(using QuoteContext): Cde[Unit]
   def cloop[A: Type](k: A => Cde[Unit], bp: Option[Cde[Boolean]], body: ((A => Cde[Unit]) => Cde[Unit]))(using QuoteContext): Cde[Unit]
   def while_(goon: Cde[Boolean])(body: Cde[Unit])(using QuoteContext): Cde[Unit]

   //  Reference cells?
   def assign[A](c1: Var[A], c2: Cde[A])(using QuoteContext): Cde[Unit]
   def dref[A](x: Var[A])(using QuoteContext): Cde[A]
   def incr(i: Var[Int])(using QuoteContext): Cde[Unit]
   def decr(i: Var[Int])(using QuoteContext): Cde[Unit]

   implicit class VarCde[A](val c1: Var[A]) {
      def :=(c2: Cde[A])(using QuoteContext): Cde[Unit] = assign[A](c1, c2)
   }

   // Arrays
   def array_get[A: Type, W: Type](arr: Cde[Array[A]])(i: Cde[Int])(k: (Cde[A] => Cde[W]))(using QuoteContext): Cde[W]
   def array_len[A: Type](arr: Cde[Array[A]])(using QuoteContext): Cde[Int]
   def array_set[A: Type](arr: Cde[Array[A]])(i: Cde[Int])(v: Cde[A])(using QuoteContext): Cde[Unit]
   def int_array[A: Type](arr: Array[Int])(using QuoteContext): Cde[Array[Int]]

   // Others
   def pair[A: Type, B: Type](x: Cde[A], y: Cde[B])(using QuoteContext): Cde[Tuple2[A,B]]
   def uninit[A: Type](using QuoteContext): Cde[A]
   def blackhole[A: Type](using QuoteContext): Cde[A]
   def is_static[A: Type](c1: Cde[A])(using QuoteContext): Boolean
   def is_fully_dynamic[A: Type](c1: Cde[A])(using QuoteContext): Boolean

   // Lists
   def nil[A: Type]()(using QuoteContext): Cde[List[A]] 
   def cons[A: Type](x: Cde[A], xs: Cde[List[A]])(using QuoteContext): Cde[List[A]]
   def reverse[A: Type](xs: Cde[List[A]])(using QuoteContext): Cde[List[A]] 
}
