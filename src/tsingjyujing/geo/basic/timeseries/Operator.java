package tsingjyujing.geo.basic.timeseries;

/**
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */
public abstract class Operator<Type> {
    /**
     * @param value_in value in to process
     * @return process result
     * @example new Operator<Double></>(){
     * @Override public Double op(Double value_in) {
     * return value_in*value_in;
     * }
     * This Class defined a pow(x,2) operation
     */
    abstract public Type op(Type value_in);
}
