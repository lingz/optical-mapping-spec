package edu.nyu.opticalMapping;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by ling on 2/10/14.
 */
public class Utils {
    private static NumberFormat doubleFormatter = new DecimalFormat("#0.00000");

    public static String joinDoubles(List<Double> input) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Double cut : input) {
            stringBuilder.append(doubleFormatter.format(cut));
            stringBuilder.append(" ");
        }
        // remove trailing whitespace
        stringBuilder.setLength(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }
}
