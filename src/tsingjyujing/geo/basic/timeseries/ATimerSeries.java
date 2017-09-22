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
public abstract class ATimerSeries<T> {

    List<TimeUnit<T>> ts_list = new ArrayList<TimeUnit<T>>();
    boolean uniqued = false;
    int sorted = 0;

    /**
     * @param descend sort type
     */
    public void sort(boolean descend) {
        Collections.sort(ts_list);
        if (descend) {
            Collections.reverse(ts_list);
            sorted = -1;
        } else {
            sorted = 1;
        }
    }

    /**
     * delete dump element in time series
     */
    public void unique() {
        this.sort(false);
        List<TimeUnit<T>> ts_list_uniqued = new ArrayList<TimeUnit<T>>();
        if (ts_list.isEmpty()) {
            return;
        }
        ts_list_uniqued.add(ts_list.get(0));
        int res;
        TimeUnit<T> last_Time_unit = ts_list.get(0);
        ts_list.remove(0);
        for (TimeUnit<T> tmp : ts_list) {
            res = last_Time_unit.compareTo(tmp);
            if (res != 0) {
                ts_list_uniqued.add(tmp);
                last_Time_unit = tmp;
            }
        }
        ts_list = ts_list_uniqued;
        uniqued = true;
    }

    /**
     * @param add_val number to add to every value
     */
    public void append(TimeUnit<T> add_val) {
        ts_list.add(add_val);
        uniqued = false;
        sorted = 0;
    }

    /**
     * @return size
     */
    public int size() {
        return ts_list.size();
    }

    /**
     * @param index get element by index
     * @return element at index
     */
    public TimeUnit<T> get(int index) {
        return ts_list.get(index);
    }

    /**
     * @return max(T)-min(T)
     */
    public double time_range() {
        switch (sorted) {
            case 1:
                return ts_list.get(size() - 1).time - ts_list.get(0).time;
            case -1:
                return ts_list.get(0).time - ts_list.get(size() - 1).time;
            default:
                sort(false);
                return ts_list.get(size() - 1).time - ts_list.get(0).time;
        }
    }

    /**
     * @return time series where remove elements which tagged
     */
    public boolean remove() {
        boolean remove_one = false;
        List<TimeUnit<T>> ts_list_new = new ArrayList<TimeUnit<T>>();
        for (TimeUnit<T> tmp : ts_list) {
            if (!tmp.remove) {
                ts_list_new.add(tmp);
            } else {
                remove_one = true;
            }
        }
        ts_list = ts_list_new;
        return remove_one;
    }

    /**
     * @param index tag element to remove
     */
    public void set_remove(int index) {
        TimeUnit<T> tmp = ts_list.get(index);
        tmp.remove = true;
        this.ts_list.set(index, tmp);
    }

    /**
     * @param time_point
     * @param min_than_value
     * @return
     */
    public int search_in_sorted(double time_point, boolean min_than_value) {
        int[] ret = search(time_point);
        if (min_than_value) {
            return ret[0];
        } else {
            return ret[1];
        }

    }

    private int[] search(double given_time) {
        if (sorted != 1) {
            this.sort(false);
        }
        if (
                given_time > get(size() - 1).time ||
                        given_time < get(0).time) {
            int[] return_value = {-1, -1};
            return return_value;
        }
        int st_index = 0;
        int ed_index = size() - 1;
        int mid;
        int[] return_value = new int[2];
        for (int i = 0; i < size(); ++i) {

            mid = (st_index + ed_index) / 2;

            double thist = get(mid).time;

            if (thist < given_time) {
                st_index = mid;
            } else if (thist > given_time) {
                ed_index = mid;
            } else {
                return_value[0] = mid;
                return_value[1] = mid;
                break;
            }

            if ((ed_index - st_index) == 1) {
                return_value[0] = st_index;
                return_value[1] = ed_index;
                break;
            }
        }
        return return_value;
    }
}
