package benchmarks

import scala.quoted._
import scala.quoted.util._
import scala.quoted.staging._
import strymonas._

object TestPipelines {
   given Toolbox = Toolbox.make(getClass.getClassLoader)
   import Settings._

   // import strymonas.Code._
   import strymonas.CodePs._
   import scala.language.implicitConversions

   def sumPipeline(using QuoteContext) = '{ (array: Array[Long]) => 
      ${ Stream.of('{array})
         .fold(long(0), _+_) } 
   }

   def sumOfSquaresPipeline(using QuoteContext) = '{ (array: Array[Long]) =>
      ${ Stream.of('{array})
         .map((a) => a * a)
         .fold(long(0), _+_) }
   }

   def sumOfSquaresEvenPipeline(using QuoteContext) = '{ (array: Array[Long]) =>
      ${ Stream.of('{array})
      .filter(d => (d mod long(2)) === long(0))
      .map((a) => a * a )
      .fold(long(0), _+_) }
   }

   def cartPipeline(using QuoteContext) = '{ (vHi: Array[Long], vLo: Array[Long]) =>
      ${ Stream.of('{vHi})
      .flatMap((d) => Stream.of('{vLo}).map((dp) => d * dp))
      .fold(long(0), _+_) }
   }

   def mapsMegamorphicPipeline(using QuoteContext) = '{ (array: Array[Long]) =>
      ${ Stream.of('{array})
         .map((a) => a * long(1))
         .map((a) => a * long(2))
         .map((a) => a * long(3))
         .map((a) => a * long(4))
         .map((a) => a * long(5))
         .map((a) => a * long(6))
         .map((a) => a * long(7))
         .fold(long(0), _+_) }
   }

   def filtersMegamorphicPipeline(using QuoteContext) = '{ (array: Array[Long]) =>
      ${ Stream.of('{array})
         .filter((a) => a > long(1))
         .filter((a) => a > long(2))
         .filter((a) => a > long(3))
         .filter((a) => a > long(4))
         .filter((a) => a > long(5))
         .filter((a) => a > long(6))
         .filter((a) => a > long(7))
         .fold(long(0), _+_) }
   } 

   def filterPipeline(using QuoteContext) = '{ (array: Array[Long]) =>
      ${ Stream.of('{array})
      .filter(d => (d mod long(2)) === long(0))
      .fold(long(0), _+_) }
   }

   def takePipeline(using QuoteContext) = '{ (array: Array[Long]) =>
      ${ Stream.of('{array})
      .take(int(2))
      .fold(long(0), _+_) }
   }

   def flatMapTakePipeline(using QuoteContext) = '{ (array1: Array[Long], array2: Array[Long]) =>
      ${ Stream.of('{array1})
      .flatMap((d) => Stream.of('{array2}))
      .take(int(vLimit_s))
      .fold(long(0), _+_) }
   }

   def dotProductPipeline(using QuoteContext) = '{ (array1: Array[Long], array2: Array[Long])  =>
      ${ Stream.of('{array1})
      .zipWith[Long, Long](_+_, Stream.of('{array2}))
      .fold(long(0), _+_) }
   }

   def flatMapAfterZipPipeline(using QuoteContext) = '{ (array1: Array[Long], array2: Array[Long]) =>
      ${ Stream.of('{array1})
      .zipWith[Long, Long](_+_, Stream.of('{array1}))
      .flatMap((d) => Stream.of('{array2}).map((dp) => d + dp))
      .fold(long(0), _+_) }
   }

   def zipAfterFlatMapPipeline(using QuoteContext) = '{ (array1: Array[Long], array2: Array[Long]) =>
      ${ Stream.of('{array1})
      .flatMap((d) => Stream.of('{array2}).map((dp) => d + dp))
      .zipWith[Long, Long](_+_, Stream.of('{array1}) )
      .fold(long(0), _+_) }
   }

   def zipFlatMapFlatMapPipeline(using QuoteContext) = '{ (array1: Array[Long], array2: Array[Long]) =>
      ${ Stream.of('{array1})
      .flatMap((d) => Stream.of('{array2}).map((dp) => d + dp))
      .zipWith[Long, Long](_+_, Stream.of('{array2}).flatMap((d) => Stream.of('{array1}).map((dp) => d + dp)) )
      .take(int(vLimit_s))
      .fold(long(0), _+_) }
   }

   def zipFilterFilterPipeline(using QuoteContext) = '{ (array1: Array[Long], array2: Array[Long]) =>
      ${ Stream.of('{array1}).filter((d) => d > long(7))
      .zipWith[Long, Long](_+_,  Stream.of('{array2}).filter((d) => d > long(5)))
      .fold(long(0), _+_) } 
   }
}