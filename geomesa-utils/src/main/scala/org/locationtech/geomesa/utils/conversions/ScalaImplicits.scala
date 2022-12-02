/***********************************************************************
 * Copyright (c) 2013-2022 Commonwealth Computer Research, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 ***********************************************************************/

package org.locationtech.geomesa.utils.conversions

import scala.collection.{IterableOnce, IterableOps, Iterator}
import scala.reflect.ClassTag

object ScalaImplicits {

  implicit class RichIterableOnce[T](val seq: IterableOnce[T]) extends AnyVal {
    def minOption(implicit cmp: Ordering[T]): Option[T] = if (seq.iterator.isEmpty) { None } else { Some(seq.min) }
    def minOrElse(or: => T)(implicit cmp: Ordering[T]): T = if (seq.iterator.isEmpty) { or } else { seq.min }

    def maxOption(implicit cmp: Ordering[T]): Option[T] = if (seq.iterator.isEmpty) { None } else Some(seq.max)
    def maxOrElse(or: => T)(implicit cmp: Ordering[T]): T = if (seq.iterator.isEmpty) { or } else seq.max

    def sumOption(implicit num: Numeric[T]): Option[T] = if (seq.iterator.isEmpty) { None } else Some(seq.sum)
    def sumOrElse(or: => T)(implicit num: Numeric[T]): T = if (seq.iterator.isEmpty) { or } else seq.sum

    def foreachIndex[U](f: (T, Int) => U): Unit = {
      var i = 0
      seq.iterator.foreach { v => f(v, i); i += 1 }
    }
  }

  implicit class RichTraversableLike[+A, +CC[_], +C](val seq: IterableOps[A, CC, C]) extends AnyVal {
    def tailOption: C = if (seq.isEmpty) { seq.asInstanceOf[C] } else { seq.tail }
    def mapWithIndex[B](f: (A, Int) => B): CC[B] = {
      var i = -1
      seq.map { v => i += 1; f(v, i) }
    }
  }

  implicit class RichIterator[T](val iter: Iterator[T]) extends AnyVal {
    def head: T = iter.next()
    def headOption: Option[T] = iter.find(_ != null)
    def mapWithIndex[B](f: (T, Int) => B): Iterator[B] = {
      var i = -1
      iter.map { v => i += 1; f(v, i) }
    }
  }

  implicit class RichArray[T](val array: Array[T]) extends AnyVal {
    def foreachIndex[U](f: (T, Int) => U): Unit = {
      var i = 0
      while (i < array.length) {
        f(array(i), i)
        i += 1
      }
    }

    def mapWithIndex[B](f: (T, Int) => B)(implicit ct: ClassTag[B]): Array[B] = {
      var i = -1
      array.map { v => i += 1; f(v, i) }
    }

    def flatMapWithIndex[B](f: (T, Int) => IterableOnce[B])(implicit ct: ClassTag[B]): Array[B] = {
      var i = -1
      array.flatMap { v => i += 1; f(v, i) }
    }
  }
}
