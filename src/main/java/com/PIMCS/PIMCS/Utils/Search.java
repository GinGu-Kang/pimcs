package com.PIMCS.PIMCS.Utils;

import com.PIMCS.PIMCS.noSqlDomain.DynamoMat;

import java.util.List;

public class Search {

    public static int  binarySearch(String key, List<String> list){
        int mid;
        int left = 0;
        int right = list.size() - 1;

        while (right >= left) {
            mid = (right + left) / 2;
            String findKey = list.get(mid);
            if (key.compareTo(findKey) == 0) {
                return mid;
            }

            if (key.compareTo(findKey) == -1) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }

        }

        return -1;
    }
}
