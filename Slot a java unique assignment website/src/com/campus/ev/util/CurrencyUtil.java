package com.campus.ev.util;

import java.text.DecimalFormat;

public class CurrencyUtil {
    private static final DecimalFormat DF_CURRENCY = new DecimalFormat("₹#,##0.00");
    private static final DecimalFormat DF_KWH = new DecimalFormat("#,##0.000 kWh");
    private static final DecimalFormat DF_KW = new DecimalFormat("#,##0.0 kW");
    private static final DecimalFormat DF_PERCENT = new DecimalFormat("#0.0'%'");

    public static String formatINR(double amount) {
        return DF_CURRENCY.format(amount);
    }

    public static String formatKwh(double kwh) {
        return DF_KWH.format(kwh);
    }

    public static String formatKw(double kw) {
        return DF_KW.format(kw);
    }

    public static String formatPercent(double percent) {
        return DF_PERCENT.format(percent);
    }
}
