package com.example.ecommerceweb.util;

import java.util.ArrayList;
import java.util.List;

public class DivideList {
    public static <T> List<List<T>> divideList(List<T> list, int size) {
        List<List<T>> dividedList = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            dividedList.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return dividedList;
    }
}
