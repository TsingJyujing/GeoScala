package com.github.tsingjyujing.geo.element.immutable

import com.github.tsingjyujing.geo.basic.IVector2

/**
  * Implementation of IVector2
  *
  * @param x
  * @param y
  */
case class Vector2(x: Double, y: Double) extends IVector2 {
    override def getX: Double = x

    override def getY: Double = y
}

