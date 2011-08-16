package controllers;

import java.util.Comparator;
import java.util.Map;

public class MyComparator implements Comparator<Object> {

    Map theMapToSort;

    public MyComparator(Map theMapToSort) {

        this.theMapToSort = theMapToSort;
    }

    public int compare(Object key1, Object key2) {

        Integer val1 = (Integer) theMapToSort.get(key1);
        Integer val2 = (Integer) theMapToSort.get(key2);
        if (val1 < val2) {
            return 1;
        } else {
            return -1;
        }
    }
}