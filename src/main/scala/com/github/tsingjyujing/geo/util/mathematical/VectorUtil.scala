package com.github.tsingjyujing.geo.util.mathematical

import com.github.tsingjyujing.geo.basic.IVector3
import com.github.tsingjyujing.geo.util.BeCarefulWhileUsing

/**
  * Common methods of vectors
  * @author tsingjyujing@163.com
  */
object VectorUtil {
    def outerProduct(v1: Array[Double], v2: Array[Double]): Array[Double] = if ((v1.length == 3) && (v2.length == 3)) {
        Array(
            0.0D + v1(1) * v2(2) - v1(2) * v2(1),
            0.0D - v1(0) * v2(2) + v1(2) * v2(0),
            0.0D + v1(0) * v2(1) - v1(1) * v2(0)
        )
    } else {
        throw new RuntimeException("Out product only allowed in 3D vector")
    }

    def norm2Vector(vec: Array[Double], normLen: Double = 1.0): Array[Double] = {
        val normFactor = normLen / math.sqrt(vec.map(x => x * x).sum)
        vec.map(x => x * normFactor)
    }

    /**
      * see visualize/solve_rotate_equation.m for implementation
      *
      * @param v1        Vector 1
      * @param v2        Vector 2
      * @param angleToV1 angle to Vector 1
      * @return
      */
    @BeCarefulWhileUsing("to ensure norm of vectors are all one.")
    def sphereInterpFast(v1: IVector3, v2: IVector3, angleToV1: Double): IVector3 = {
        val cosThetaMax = v1 innerProduct v2
        val sinThetaMax = math.sin(math.acos(cosThetaMax))
        val sinThetaToV1 = math.sin(angleToV1)
        val beta = sinThetaToV1 / sinThetaMax
        val alpha = math.cos(angleToV1) - beta * cosThetaMax
        (v1 * alpha) + (v2 * beta)
    }

    /**
      *
      * @param v1              Vector 1
      * @param v2              Vector 2
      * @param angleToV1Values angles to Vector 1
      * @return
      */
    @BeCarefulWhileUsing("to ensure norm of vectors are all one.")
    def sphereInterpFast(v1: IVector3, v2: IVector3, angleToV1Values: TraversableOnce[Double]): TraversableOnce[IVector3] = {
        val cosThetaMax = v1 innerProduct v2
        val sinThetaMax = math.sin(math.acos(cosThetaMax))
        angleToV1Values.map(angleToV1 => {
            val sinThetaToV1 = math.sin(angleToV1)
            val beta = sinThetaToV1 / sinThetaMax
            val alpha = math.cos(angleToV1) - beta * cosThetaMax
            (v1 * alpha) + (v2 * beta)
        })
    }

    /**
      * see visualize/solve_rotate_equation.m for implementation
      *
      * @param v1        Vector 1
      * @param v2        Vector 2
      * @param angleToV1 angle to Vector 1
      * @return
      */
    def sphereInterp(v1: IVector3, v2: IVector3, angleToV1: Double): IVector3 = {

        val cosThetaMax = v1 innerProduct v2
        val angleMax = math.acos(cosThetaMax)
        assert(angleToV1 < angleMax, "angle to \\vec{V_1} overflow")
        val sinThetaMax = math.sin(angleMax)
        val sinThetaToV1 = math.sin(angleToV1)
        val beta = sinThetaToV1 / sinThetaMax
        val alpha = math.cos(angleToV1) - beta * cosThetaMax
        (v1 * (alpha / v1.norm2)) + (v2 * (beta / v2.norm2))
    }

    /**
      *
      * @param v1              Vector 1
      * @param v2              Vector 2
      * @param angleToV1Values angles to Vector 1
      * @return
      */
    def sphereInterp(v1: IVector3, v2: IVector3, angleToV1Values: TraversableOnce[Double]): TraversableOnce[IVector3] = {
        val cosThetaMax = v1 innerProduct v2
        val angleMax = math.acos(cosThetaMax)
        val sinThetaMax = math.sin(angleMax)
        angleToV1Values.map(angleToV1 => {
            assert(angleToV1 < angleMax, "angle to \\vec{V_1} overflow")
            val sinThetaToV1 = math.sin(angleToV1)
            val beta = sinThetaToV1 / sinThetaMax
            val alpha = math.cos(angleToV1) - beta * cosThetaMax
            (v1 * (alpha / v1.norm2)) + (v2 * (beta / v2.norm2))
        })
    }
}
