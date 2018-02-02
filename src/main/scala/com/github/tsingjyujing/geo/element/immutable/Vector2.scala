package com.github.tsingjyujing.geo.element.immutable

import com.github.tsingjyujing.geo.basic.IVector2

final case class Vector2(x: Double, y: Double) extends IVector2 {
    override def getX: Double = x

    override def getY: Double = y

    def -(v: Vector2) = Vector2(x - v.x, y - v.y)

    def *(v: Vector2) = Vector2(x * v.x, y * v.y)

    def /(v: Vector2) = Vector2(x / v.x, y / v.y)

    def +(v: Double) = Vector2(x + v, y + v)

    def -(v: Double) = Vector2(x - v, y - v)

    def *(v: Double) = Vector2(x * v, y * v)

    def /(v: Double) = Vector2(x / v, y / v)

    override def +(v: IVector2): IVector2 = Vector2(x + v.getX, y + v.getY)

}

