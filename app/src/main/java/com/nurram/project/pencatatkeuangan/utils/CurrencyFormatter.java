package com.nurram.project.pencatatkeuangan.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class CurrencyFormatter {

    @SuppressWarnings("UnusedReturnValue")
    public static String convertAndFormat(long s) {
        DecimalFormat format = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');

        format.setDecimalFormatSymbols(formatRp);
        String result = format.format(s);
        return result.substring(0, result.length() - 3);
    }
}
