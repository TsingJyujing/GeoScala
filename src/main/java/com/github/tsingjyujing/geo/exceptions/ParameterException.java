package com.github.tsingjyujing.geo.exceptions;

/**
 * Throw it while input parameter is error
 *
 * @author tsingjyujing@163.com
 */
public class ParameterException extends RuntimeException {
    /**
     * Create exception with message only
     *
     * @param msg message
     */
    public ParameterException(String msg) {
        super(msg);
    }

    /**
     * Create exception with message and exception
     *
     * @param msg message
     * @param ex  exception info
     */
    public ParameterException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
