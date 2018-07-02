package cn.com.agree.evs.common;

import java.math.BigDecimal;
import java.util.Random;

public class MathUtils {

    public static String addBigDecimal(String decimalLiteral1, String decimalLiteral2) {
        return getDecimal(decimalLiteral1).add(getDecimal(decimalLiteral2)).toPlainString();
    }

    public static int compare(String decimalLiteral1, String decimalLiteral2) {
        return getDecimal(decimalLiteral1).compareTo(getDecimal(decimalLiteral2));
    }

    public static String divideBigDecimal(String decimalLiteral1, String decimalLiteral2) {
        return divideBigDecimal(decimalLiteral1, decimalLiteral2, 2);
    }

    public static String divideBigDecimal(String decimalLiteral1, String decimalLiteral2, int decimalLength) {
        return getDecimal(decimalLiteral1).divide(getDecimal(decimalLiteral2), decimalLength, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    private static BigDecimal getDecimal(String literal) {
        if (literal == null || literal.length() == 0) {
            return new BigDecimal("0");
        }
        if (literal.charAt(0) == '.') {
            return new BigDecimal("0" + literal);
        }
        return new BigDecimal(literal);
    }

    public static String multiplyBigDecimal(String decimalLiteral1, String decimalLiteral2) {
        return getDecimal(decimalLiteral1).multiply(getDecimal(decimalLiteral2)).toPlainString();
    }

    public static String multiplyBigDecimal(String decimalLiteral1, String decimalLiteral2, int decimalLength) {
        BigDecimal bd = getDecimal(decimalLiteral1).multiply(getDecimal(decimalLiteral2));
        bd = bd.setScale(decimalLength, BigDecimal.ROUND_HALF_UP);
        return bd.toPlainString();
    }

    public static String setScale(String decimalLiteral, int decimalLength) {
        BigDecimal d = getDecimal(decimalLiteral);
        d = d.setScale(decimalLength, BigDecimal.ROUND_HALF_UP);
        return d.toPlainString();
    }

    public static String subtractBigDecimal(String decimalLiteral1, String decimalLiteral2) {
        return getDecimal(decimalLiteral1).subtract(getDecimal(decimalLiteral2)).toPlainString();
    }

    public static String getRandomData(String originData, int length) {
        String code = "0123456789abcdefghijklmnopqrstuvwxyz";
        StringBuffer sb = new StringBuffer();
        Random rd = new Random();
        sb.append(originData);
        for (int i = 0; i < length - originData.length(); i++) {
            sb.append(code.charAt(rd.nextInt(36)));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(MathUtils.getRandomData("abs0120150401", 15));
    }

}
