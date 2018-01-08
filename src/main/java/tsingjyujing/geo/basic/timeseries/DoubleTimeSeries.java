package tsingjyujing.geo.basic.timeseries;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */
public class DoubleTimeSeries extends BaseTimeSeries<Double> {

    public DoubleTimeSeries() {
        //pass;
    }

    /**
     * @param value           value to compare
     * @param removeCondition remove "more than points" or "less than points"
     */
    public void conditionalRemove(double value, boolean removeCondition) {
        List<TimeUnit<Double>> cleanedList = new ArrayList<>();
        if (removeCondition) {
            for (TimeUnit<Double> tmp : data) {
                if (tmp.value <= value) {
                    cleanedList.add(tmp);
                }
            }
        } else {
            for (TimeUnit<Double> tmp : data) {
                if (tmp.value >= value) {
                    cleanedList.add(tmp);
                }
            }
        }
        data = cleanedList;
    }

    /**
     * @return Time series differential
     */
    public DoubleTimeSeries differential() {
        if (!isUnique) {
            this.unique();
        }
        DoubleTimeSeries res = new DoubleTimeSeries();
        TimeUnit<Double> lastTimeUnit = data.get(0);
        TimeUnit<Double> thisTimeUnit;
        for (int i = 1; i < data.size(); ++i) {
            thisTimeUnit = data.get(i);
            double dv = (thisTimeUnit.value - lastTimeUnit.value);
            double dt = (thisTimeUnit.time - lastTimeUnit.time);
            res.append(new TimeUnit<>(
                    (thisTimeUnit.time + lastTimeUnit.time) / 2.0D,
                    dv / dt
            ));
            lastTimeUnit = thisTimeUnit;
        }
        return res;
    }

    /**
     * @return time series of difference
     */
    public DoubleTimeSeries difference() {
        if (!isUnique) {
            this.unique();
        }
        DoubleTimeSeries res = new DoubleTimeSeries();
        TimeUnit<Double> lastTimeUnit = data.get(0);
        TimeUnit<Double> thisTimeUnit;
        for (int i = 1; i < data.size(); ++i) {
            thisTimeUnit = data.get(i);
            double dv = (thisTimeUnit.value - lastTimeUnit.value);
            res.append(new TimeUnit<>(
                    (thisTimeUnit.time + lastTimeUnit.time) / 2.0D,
                    dv
            ));
            lastTimeUnit = thisTimeUnit;
        }
        return res;
    }

    /**
     * @return sum of value
     */
    public double sum() {
        double rtn = 0.0D;
        for (int i = 0; i < data.size(); ++i) {
            rtn += data.get(i).value;
        }
        return rtn;
    }

    /**
     * @return max(value)-min(value)
     */
    public double getValueRange() {
        double minval = data.get(0).value;
        double maxval = data.get(0).value;
        for (int i = 1; i < data.size(); ++i) {
            if (minval > data.get(i).value) {
                minval = data.get(i).value;
            }
            if (maxval < data.get(i).value) {
                maxval = data.get(i).value;
            }
        }
        return maxval - minval;
    }

    /**
     * @return integration of the area under the curve
     */
    public double integral() {
        if (!isUnique) {
            this.unique();
        }
        double res = 0.0D;
        TimeUnit<Double> last_Time_unit = data.get(0);
        TimeUnit<Double> this_Time_unit;
        for (int i = 1; i < data.size(); ++i) {
            this_Time_unit = data.get(i);
            double v = (this_Time_unit.value + last_Time_unit.value) / 2.0D;
            double dt = (this_Time_unit.time - last_Time_unit.time);
            res += v * dt;
            last_Time_unit = this_Time_unit;
        }
        return res;
    }

    /**
     * Show time series
     */
    public void display() {
        System.out.println("Time\t\tValue");
        for (TimeUnit<Double> tmp : data) {
            System.out.printf("%f\t\t%f\n", tmp.time, tmp.value);
        }
    }

    /**
     * @param value
     * @return DoubleTimeSeries which DoubleTimeSeries.value *=value
     */
    public DoubleTimeSeries mul(double value) {
        DoubleTimeSeries newTimeSeries = new DoubleTimeSeries();
        for (TimeUnit<Double> tmp : data) {
            tmp.value *= value;
            newTimeSeries.append(tmp);
        }
        return newTimeSeries;
    }

    /**
     * @param value
     * @return DoubleTimeSeries which DoubleTimeSeries.value +=value
     */
    public DoubleTimeSeries add(double value) {
        DoubleTimeSeries newTimeSeries = new DoubleTimeSeries();
        for (TimeUnit<Double> tmp : data) {
            tmp.value += value;
            newTimeSeries.append(tmp);
        }
        return newTimeSeries;
    }

    /**
     * @param value
     * @return DoubleTimeSeries which DoubleTimeSeries.value -=value
     */
    public DoubleTimeSeries dec(double value) {
        DoubleTimeSeries newTimeSeries = new DoubleTimeSeries();
        for (TimeUnit<Double> tmp : data) {
            tmp.value -= value;
            newTimeSeries.append(tmp);
        }
        return newTimeSeries;
    }

    /**
     * @param value
     * @return DoubleTimeSeries which DoubleTimeSeries.value right divided by value
     */
    public DoubleTimeSeries rdiv(double value) {
        DoubleTimeSeries newTimeSeries = new DoubleTimeSeries();
        for (TimeUnit<Double> tmp : data) {
            tmp.value /= value;
            newTimeSeries.append(tmp);
        }
        return newTimeSeries;
    }

    /**
     * @param value
     * @return DoubleTimeSeries which DoubleTimeSeries.value left divided by value
     */
    public DoubleTimeSeries ldiv(double value) {
        DoubleTimeSeries newTimeSeries = new DoubleTimeSeries();
        for (TimeUnit<Double> tmp : data) {
            tmp.value = value / tmp.value;
            newTimeSeries.append(tmp);
        }
        return newTimeSeries;
    }

    /**
     * @return DoubleTimeSeries which DoubleTimeSeries.value = sqrt(DoubleTimeSeries.value)
     */
    public DoubleTimeSeries sqrt() {
        DoubleTimeSeries newTimeSeries = new DoubleTimeSeries();
        for (TimeUnit<Double> tmp : data) {
            tmp.value = Math.sqrt(tmp.value);
            newTimeSeries.append(tmp);
        }
        return newTimeSeries;
    }

    /**
     * @return DoubleTimeSeries which DoubleTimeSeries.value *= DoubleTimeSeries.value
     */
    public DoubleTimeSeries pow2() {
        //user define function example
        return userDefinedOperation(new IOperator<Double>() {
            @Override
            public Double operation(Double value) {
                return value * value;
            }
        });
    }

    /**
     * @param func anonymous class with public double operation(double);
     * @return DoubleTimeSeries which DoubleTimeSeries.value = func(DoubleTimeSeries.value)
     */
    public DoubleTimeSeries userDefinedOperation(IOperator<Double> func) {
        DoubleTimeSeries newTimeSeries = new DoubleTimeSeries();
        for (TimeUnit<Double> tmp : data) {
            tmp.value = func.operation(tmp.value);
            newTimeSeries.append(tmp);
        }
        return newTimeSeries;
    }

}
