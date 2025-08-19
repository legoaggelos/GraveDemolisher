package org.legoaggelos.util;

import java.util.ArrayList;
import java.util.List;

public class ArrayListUtils {
    public static String toString(List<Integer> arrayList){
        StringBuilder string = new StringBuilder();
        arrayList.forEach(v-> string.append(v.toString()).append(","));
        return string.substring(0,string.toString().length()-1);
    }
}
