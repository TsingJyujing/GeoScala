package com.github.tsingjyujing.geo.jni;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Java Native Interface of S2 index
 */
public class NativeS2PointIndex {

    private final static Logger LOGGER = LoggerFactory.getLogger(NativeS2PointIndex.class);

    private final static String LIB_NAME = "GeoScalaPointIndex";

    /*
     * Load naive library from system lib path / JRE library path
     */
    static {
        LOGGER.info("Loading library " + LIB_NAME);
        try {
            System.loadLibrary(LIB_NAME);
            // FIXME Loading library in jar
            LOGGER.info("Library loaded.");
        } catch (Throwable ex) {
            LOGGER.error(
                    "Error while loading native library: {} try to put library in {}",
                    LIB_NAME,
                    System.getProperty("java.library.path"),
                    ex
            );
            throw ex;
        }
    }

    /**
     * Create an index and return it's pointer
     *
     * @return
     */
    public static native long newS2Index();

    /**
     * Release and destroy an index via given pointer
     */
    public static native void deleteS2Index(long s2IndexPtr);


    /**
     * Add an point into index
     *
     * @param s2IndexPtr S2 index pointer
     * @param longitude  longitude
     * @param latitude   latitude
     * @param value      value/index info of point
     */
    public static native void insertPoint(long s2IndexPtr, double longitude, double latitude, long value);

    /**
     * 查询点的信息
     *
     * @param s2IndexPtr S2 index pointer
     * @param longitude  longitude
     * @param latitude   latitude
     * @param radius     radius (in unit of km)
     * @param pointLimit how much point to return
     * @return An array which size=N*3 and 3 means longitude, latitude and value/index info of point
     */
    public static native long[] queryPointsNative(long s2IndexPtr, double longitude, double latitude, double radius, int pointLimit);

}
