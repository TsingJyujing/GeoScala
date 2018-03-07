package com.github.tsingjyujing.geo.util

import scala.annotation.meta.{beanGetter, beanSetter, getter, setter}

@getter
@setter
@beanGetter
@beanSetter class BeCarefulWhileUsing(message: String = "") extends scala.annotation.StaticAnnotation
