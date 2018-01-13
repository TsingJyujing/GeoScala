package tsingjyujing.geo.basic.timeseries;

/**
 * @author tsingjyujing
 */
public interface IOperator<T> {
    /**
     * Operation to do with input value
     *
     * @param value value in to process
     * @return process result
     * This Class defined a pow(x,2) operation
     */
    T operation(T value);
}
