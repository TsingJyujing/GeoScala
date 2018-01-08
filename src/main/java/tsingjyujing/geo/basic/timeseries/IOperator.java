package tsingjyujing.geo.basic.timeseries;

/**
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */
public interface IOperator<Type> {
    /**
     * Operation to do with input value
     *
     * @param value value in to process
     * @return process result
     * @example new IOperator<Double></>(){
     * @Override operation(Type value)
     * This Class defined a pow(x,2) operation
     */
    Type operation(Type value);
}
