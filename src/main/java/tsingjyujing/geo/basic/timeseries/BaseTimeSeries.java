package tsingjyujing.geo.basic.timeseries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @param <T> Type of time series
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */
public abstract class BaseTimeSeries<T> {

    List<TimeUnit<T>> data = new ArrayList<>();
    boolean isUnique = false;
    int sortedStatus = 0;

    /**
     * @param descend sort type
     */
    public void sort(boolean descend) {
        Collections.sort(data);
        if (descend) {
            Collections.reverse(data);
            sortedStatus = -1;
        } else {
            sortedStatus = 1;
        }
    }

    /**
     * delete dump element in time series
     */
    public void unique() {
        this.sort(false);
        List<TimeUnit<T>> uniqueTimeSeriesList = new ArrayList<TimeUnit<T>>();
        if (data.isEmpty()) {
            return;
        }
        uniqueTimeSeriesList.add(data.get(0));
        int res;
        TimeUnit<T> lastTimeUnit = data.get(0);
        data.remove(0);
        for (TimeUnit<T> tmp : data) {
            res = lastTimeUnit.compareTo(tmp);
            if (res != 0) {
                uniqueTimeSeriesList.add(tmp);
                lastTimeUnit = tmp;
            }
        }
        data = uniqueTimeSeriesList;
        isUnique = true;
    }

    /**
     * @param value number to add to every value
     */
    public void append(TimeUnit<T> value) {
        data.add(value);
        isUnique = false;
        sortedStatus = 0;
    }

    /**
     * @return size
     */
    public int size() {
        return data.size();
    }

    /**
     * @param index get element by index
     * @return element at index
     */
    public TimeUnit<T> get(int index) {
        return data.get(index);
    }

    /**
     * @return max(T)-min(T)
     */
    public double timeRange() {
        switch (sortedStatus) {
            case 1:
                return data.get(size() - 1).time - data.get(0).time;
            case -1:
                return data.get(0).time - data.get(size() - 1).time;
            default:
                sort(false);
                return data.get(size() - 1).time - data.get(0).time;
        }
    }

    /**
     * @return time series where remove elements which tagged
     */
    public boolean remove() {
        boolean remove_one = false;
        List<TimeUnit<T>> ts_list_new = new ArrayList<TimeUnit<T>>();
        for (TimeUnit<T> tmp : data) {
            if (!tmp.remove) {
                ts_list_new.add(tmp);
            } else {
                remove_one = true;
            }
        }
        data = ts_list_new;
        return remove_one;
    }

    /**
     * @param index tag element to remove
     */
    public void setRemove(int index) {
        TimeUnit<T> tmp = data.get(index);
        tmp.remove = true;
        this.data.set(index, tmp);
    }

    /**
     * @param timePoint
     * @param isLessThanValue
     * @return
     */
    public int searchInSorted(double timePoint, boolean isLessThanValue) {
        int[] ret = search(timePoint);
        if (isLessThanValue) {
            return ret[0];
        } else {
            return ret[1];
        }

    }

    private int[] search(double given_time) {
        if (sortedStatus != 1) {
            this.sort(false);
        }
        if (
                given_time > get(size() - 1).time ||
                        given_time < get(0).time) {
            int[] returnValue = {-1, -1};
            return returnValue;
        }
        int startIndex = 0;
        int endIndex = size() - 1;
        int mid;
        int[] returnValue = new int[2];
        for (int i = 0; i < size(); ++i) {

            mid = (startIndex + endIndex) / 2;

            double thist = get(mid).time;

            if (thist < given_time) {
                startIndex = mid;
            } else if (thist > given_time) {
                endIndex = mid;
            } else {
                returnValue[0] = mid;
                returnValue[1] = mid;
                break;
            }

            if ((endIndex - startIndex) == 1) {
                returnValue[0] = startIndex;
                returnValue[1] = endIndex;
                break;
            }
        }
        return returnValue;
    }
}
