package org.legoaggelos.util;

import java.util.ArrayList;

public class ArrayListUtils {
    public static String toString(ArrayList<Integer> arrayList){
        StringBuilder string = new StringBuilder();
        arrayList.forEach(v-> string.append(v.toString()).append(","));
        return string.toString().substring(0,string.toString().length()-1);
    }
}
