package com.example.dataFunctions;

import java.io.File;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;

public class Utility {

    public static void deleteDB(String file) {
        File f = new File(file);
        f.delete();
    }

    public static HashMap<String, String> map(List<String> keys, List<String> values)
    {
        int keysSize = keys.size();
        int valuesSize = values.size();

        // if the size of both arrays is not equal, throw an
        // IllegalArgumentsException
        if (keysSize != valuesSize) {
            throw new IllegalArgumentException(
                    "The number of keys doesn't match the number of values.");
        }


        if (keysSize == 0) {
            return new HashMap<>();
        }

        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < keysSize; i++) {
            map.put(keys.get(i), values.get(i));
        }
        return map;
    }

    public static String parseTrailingZero(String expression){
        try{
            float f = Float.parseFloat(expression);
            return NumberFormat.getInstance().format(f);
        }
        catch (Exception e) {
            return expression;
        }
    }

}
