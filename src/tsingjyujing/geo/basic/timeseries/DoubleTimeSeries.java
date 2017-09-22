package tsingjyujing.geo.basic.timeseries;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */
public class DoubleTimeSeries extends ATimerSeries<Double> {

    public DoubleTimeSeries() {
        //pass;
    }

    /**
     * @param value            value to compare
     * @param remove_more_than remove "more than points" or "less than points"
     */
    public void conditional_remove(double value, boolean remove_more_than) {
        List<TimeUnit<Double>> ts_list_cleaned = new ArrayList<TimeUnit<Double>>();
        if (remove_more_than) {
            for (TimeUnit<Double> tmp : ts_list) {
                if (tmp.value <= value) {
                    ts_list_cleaned.add(tmp);
                }
            }
        } else {
            for (TimeUnit<Double> tmp : ts_list) {
                if (tmp.value >= value) {
                    ts_list_cleaned.add(tmp);
                }
            }
        }
        ts_list = ts_list_cleaned;
    }

    /**
     * @return Time series differential
     */
    public DoubleTimeSeries differential() {
        if (!uniqued) {
            this.unique();
        }
        DoubleTimeSeries res = new DoubleTimeSeries();
        TimeUnit<Double> last_Time_unit = ts_list.get(0);
        TimeUnit<Double> this_Time_unit;
        for (int i = 1; i < ts_list.size(); ++i) {
            this_Time_unit = ts_list.get(i);
            double dv = (this_Time_unit.value - last_Time_unit.value);
            double dt = (this_Time_unit.time - last_Time_unit.time);
            res.append(new TimeUnit(
                    (this_Time_unit.time + last_Time_unit.time) / 2.0D,
                    dv / dt
            ));
            last_Time_unit = this_Time_unit;
        }
        return res;
    }

    /**
     * @return time series of difference
     */
    public DoubleTimeSeries difference() {
        if (!uniqued) {
            this.unique();
        }
        DoubleTimeSeries res = new DoubleTimeSeries();
        TimeUnit<Double> last_Time_unit = ts_list.get(0);
        TimeUnit<Double> this_Time_unit;
        for (int i = 1; i < ts_list.size(); ++i) {
            this_Time_unit = ts_list.get(i);
            double dv = (this_Time_unit.value - last_Time_unit.value);
            res.append(new TimeUnit(
                    (this_Time_unit.time + last_Time_unit.time) / 2.0D,
                    dv
            ));
            last_Time_unit = this_Time_unit;
        }
        return res;
    }

    /**
     * @return sum of value
     */
    public double sum() {
        double rtn = 0.0D;
        for (int i = 0; i < ts_list.size(); ++i) {
            rtn += ts_list.get(i).value;
        }
        return rtn;
    }

    /**
     * @return max(value)-min(value)
     */
    public double value_range() {
        double minval = ts_list.get(0).value;
        double maxval = ts_list.get(0).value;
        for (int i = 1; i < ts_list.size(); ++i) {
            if (minval > ts_list.get(i).value) {
                minval = ts_list.get(i).value;
            }
            if (maxval < ts_list.get(i).value) {
                maxval = ts_list.get(i).value;
            }
        }
        return maxval - minval;
    }

    /**
     * @return integration of the area under the curve
     */
    public double integral() {
        if (!uniqued) {
            this.unique();
        }
        double res = 0.0D;
        TimeUnit<Double> last_Time_unit = ts_list.get(0);
        TimeUnit<Double> this_Time_unit;
        for (int i = 1; i < ts_list.size(); ++i) {
            this_Time_unit = ts_list.get(i);
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
        for (TimeUnit<Double> tmp : ts_list) {
            System.out.printf("%f\t\t%f\n", tmp.time, tmp.value);
        }
    }

    /**
     * @param value
     * @return DoubleTimeSeries which DoubleTimeSeries.value *=value
     */
    public DoubleTimeSeries mul(double value) {
        DoubleTimeSeries ts_new = new DoubleTimeSeries();
        for (TimeUnit<Double> tmp : ts_list) {
            tmp.value *= value;
            ts_new.append(tmp);
        }
        return ts_new;
    }

    /**
     * @param value
     * @return DoubleTimeSeries which DoubleTimeSeries.value +=value
     */
    public DoubleTimeSeries add(double value) {
        DoubleTimeSeries ts_new = new DoubleTimeSeries();
        for (TimeUnit<Double> tmp : ts_list) {
            tmp.value += value;
            ts_new.append(tmp);
        }
        return ts_new;
    }

    /**
     * @param value
     * @return DoubleTimeSeries which DoubleTimeSeries.value -=value
     */
    public DoubleTimeSeries dec(double value) {
        DoubleTimeSeries ts_new = new DoubleTimeSeries();
        for (TimeUnit<Double> tmp : ts_list) {
            tmp.value -= value;
            ts_new.append(tmp);
        }
        return ts_new;
    }

    /**
     * @param value
     * @return DoubleTimeSeries which DoubleTimeSeries.value right divided by value
     */
    public DoubleTimeSeries rdiv(double value) {
        DoubleTimeSeries ts_new = new DoubleTimeSeries();
        for (TimeUnit<Double> tmp : ts_list) {
            tmp.value /= value;
            ts_new.append(tmp);
        }
        return ts_new;
    }

    /**
     * @param value
     * @return DoubleTimeSeries which DoubleTimeSeries.value left divided by value
     */
    public DoubleTimeSeries ldiv(double value) {
        DoubleTimeSeries ts_new = new DoubleTimeSeries();
        for (TimeUnit<Double> tmp : ts_list) {
            tmp.value = value / tmp.value;
            ts_new.append(tmp);
        }
        return ts_new;
    }

    /**
     * @return DoubleTimeSeries which DoubleTimeSeries.value = sqrt(DoubleTimeSeries.value)
     */
    public DoubleTimeSeries sqrt() {
        DoubleTimeSeries ts_new = new DoubleTimeSeries();
        for (TimeUnit<Double> tmp : ts_list) {
            tmp.value = Math.sqrt(tmp.value);
            ts_new.append(tmp);
        }
        return ts_new;
    }

    /**
     * @return DoubleTimeSeries which DoubleTimeSeries.value *= DoubleTimeSeries.value
     */
    public DoubleTimeSeries pow2() {
        //user define function example
        return udf_opr(new Operator<Double>() {
            @Override
            public Double op(Double value_in) {
                return value_in * value_in;
            }
        });
    }

    /**
     * @param func anonymous class with public double op(double);
     * @return DoubleTimeSeries which DoubleTimeSeries.value = func(DoubleTimeSeries.value)
     */
    public DoubleTimeSeries udf_opr(Operator<Double> func) {
        DoubleTimeSeries ts_new = new DoubleTimeSeries();
        for (TimeUnit<Double> tmp : ts_list) {
            tmp.value = func.op(tmp.value);
            ts_new.append(tmp);
        }
        return ts_new;
    }

}
