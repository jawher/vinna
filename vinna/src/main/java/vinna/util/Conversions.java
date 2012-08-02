package vinna.util;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Conversions {

    public static Object convertString(String value, Class<?> targetType) {
        if (Long.class.equals(targetType) || Long.TYPE.equals(targetType)) {
            return Long.parseLong(value);
        } else if (Integer.class.equals(targetType) || Integer.TYPE.equals(targetType)) {
            return Integer.parseInt(value);
        } else if (Short.class.equals(targetType) || Short.TYPE.equals(targetType)) {
            return Short.parseShort(value);
        } else if (Byte.class.equals(targetType) || Byte.TYPE.equals(targetType)) {
            return Byte.parseByte(value);
        } else if (Double.class.equals(targetType) || Double.TYPE.equals(targetType)) {
            return Double.parseDouble(value);
        } else if (Float.class.equals(targetType) || Float.TYPE.equals(targetType)) {
            return Float.parseFloat(value);
        } else if (BigDecimal.class.equals(targetType)) {
            return new BigDecimal(value);
        } else if (BigInteger.class.equals(targetType)) {
            return new BigInteger(value);
        } else if (Boolean.class.equals(targetType) || Boolean.TYPE.equals(targetType)) {
            return Boolean.parseBoolean(value);
        } else if (String.class.equals(targetType)) {
            return value;
        }

        //TODO: handle other types
        throw new IllegalArgumentException("Unsupported conversion of '" + value + "' to " + targetType);
    }

    public static Object convertNumeric(BigDecimal value, Class<?> targetType) {
        if (Long.class.equals(targetType) || Long.TYPE.equals(targetType)) {
            return value.longValue();
        } else if (Integer.class.equals(targetType) || Integer.TYPE.equals(targetType)) {
            return value.intValue();
        } else if (Short.class.equals(targetType) || Short.TYPE.equals(targetType)) {
            return value.intValue();
        } else if (Byte.class.equals(targetType) || Byte.TYPE.equals(targetType)) {
            return value.byteValue();
        } else if (Double.class.equals(targetType) || Double.TYPE.equals(targetType)) {
            return value.doubleValue();
        } else if (Float.class.equals(targetType) || Float.TYPE.equals(targetType)) {
            return value.floatValue();
        } else if (BigDecimal.class.equals(targetType)) {
            return value;
        } else if (BigInteger.class.equals(targetType)) {
            return value.toBigInteger();
        }
        throw new IllegalArgumentException("Cannot convert a numeric value to " + targetType);
    }
}
