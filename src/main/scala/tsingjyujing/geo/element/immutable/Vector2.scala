package tsingjyujing.geo.element.immutable

import tsingjyujing.geo.basic.IVector2

class Vector2(val x: Double, val y: Double) extends IVector2 {
    override def getX: Double = x

    override def getY: Double = y

    def +(v: Vector2) = new Vector2(x + v.x, y + v.y)

    def -(v: Vector2) = new Vector2(x - v.x, y - v.y)

    def *(v: Vector2) = new Vector2(x * v.x, y * v.y)

    def /(v: Vector2) = new Vector2(x / v.x, y / v.y)

    def +(v: Double) = new Vector2(x + v, y + v)

    def -(v: Double) = new Vector2(x - v, y - v)

    def *(v: Double) = new Vector2(x * v, y * v)

    def /(v: Double) = new Vector2(x / v, y / v)
}
