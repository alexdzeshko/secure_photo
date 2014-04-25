package com.sckftr.android.securephoto.utils;

import java.util.Collection;

/**
 * @author Aliaksandr_Litskevic
 * @created 1/27/14.
 */
public class Numbers {

    public static Integer inRange(Integer value, Integer min, Integer max) {

        return value == null || value < min ? min : value > max ? max : value;

    }
    public static Float inRange(Float value, Float min, Float max) {

        return value == null || value < min ? min : value > max ? max : value;

    }

    public static <T extends Number> T valueOf(Object value, T def) {

        if (value==null){
            return def;
        }

        if (value instanceof Integer){
            return (T) value;
        }

        if (value instanceof Float){
            return (T) value;
        }

        final String s = value.toString();
        return s.indexOf(".")==-1?(T)Integer.valueOf(s):(T)Float.valueOf(s);
    }

    public static int round(Float value) {
        if (value==null){
            return 0;
        }
        return Math.round(value);
    }

    public static boolean isEmpty(Number number) {
        return number==null || number.longValue()==0;
    }

    public static Integer nonNull(Integer value, int def) {
        return value==null?def:value;
    }

    public static <D> int[] getMiniMax(Collection<D> items, Function<D, Integer> function) {
        int min = Integer.MAX_VALUE;
        int max = 0;
        if (items!=null) {

            for (D item : items){
                final int v = function.apply(item);
                min = Math.min(min, v);
                max = Math.max(max, v);
            }
        }
        return new int[]{min, max};
    }
}
