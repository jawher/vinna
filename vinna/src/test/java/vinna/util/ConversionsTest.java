package vinna.util;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ConversionsTest {

    @Test
    public void convertLong() {
        Assert.assertEquals(10L, Conversions.convertString("10", Long.class));
        Assert.assertEquals(10L, Conversions.convertString("10", Long.TYPE));
        Assert.assertEquals(10L, Conversions.convertNumeric(BigDecimal.TEN, Long.class));
        Assert.assertEquals(10L, Conversions.convertNumeric(BigDecimal.TEN, Long.TYPE));
    }

    @Test
    public void convertInteger() {
        Assert.assertEquals(10, Conversions.convertString("10", Integer.class));
        Assert.assertEquals(10, Conversions.convertString("10", Integer.TYPE));
        Assert.assertEquals(10, Conversions.convertNumeric(BigDecimal.TEN, Integer.class));
        Assert.assertEquals(10, Conversions.convertNumeric(BigDecimal.TEN, Integer.TYPE));
    }

    @Test
    public void convertShort() {
        Assert.assertEquals(Short.parseShort("10"), Conversions.convertString("10", Short.class));
        Assert.assertEquals(Short.parseShort("10"), Conversions.convertString("10", Short.TYPE));
        Assert.assertEquals(Short.parseShort("10"), Conversions.convertNumeric(BigDecimal.TEN, Short.class));
        Assert.assertEquals(Short.parseShort("10"), Conversions.convertNumeric(BigDecimal.TEN, Short.TYPE));
    }

    @Test
    public void convertByte() {
        Assert.assertEquals(Byte.parseByte("10"), Conversions.convertString("10", Byte.class));
        Assert.assertEquals(Byte.parseByte("10"), Conversions.convertString("10", Byte.TYPE));
        Assert.assertEquals(Byte.parseByte("10"), Conversions.convertNumeric(BigDecimal.TEN, Byte.class));
        Assert.assertEquals(Byte.parseByte("10"), Conversions.convertNumeric(BigDecimal.TEN, Byte.TYPE));
    }

    @Test
    public void convertDouble() {
        Assert.assertEquals(10.0d, Conversions.convertString("10", Double.class));
        Assert.assertEquals(10.0d, Conversions.convertString("10", Double.TYPE));
        Assert.assertEquals(10.0d, Conversions.convertNumeric(BigDecimal.TEN, Double.class));
        Assert.assertEquals(10.0d, Conversions.convertNumeric(BigDecimal.TEN, Double.TYPE));
    }

    @Test
    public void convertFloat() {
        Assert.assertEquals(10.0f, Conversions.convertString("10", Float.class));
        Assert.assertEquals(10.0f, Conversions.convertString("10", Float.TYPE));
        Assert.assertEquals(10.0f, Conversions.convertNumeric(BigDecimal.TEN, Float.class));
        Assert.assertEquals(10.0f, Conversions.convertNumeric(BigDecimal.TEN, Float.TYPE));
    }

    @Test
    public void convertBigDecimal() {
        Assert.assertEquals(BigDecimal.TEN, Conversions.convertString("10", BigDecimal.class));
        Assert.assertEquals(BigDecimal.TEN, Conversions.convertNumeric(BigDecimal.TEN, BigDecimal.class));
    }

    @Test
    public void convertBigInteger() {
        Assert.assertEquals(BigInteger.TEN, Conversions.convertString("10", BigInteger.class));
        Assert.assertEquals(BigInteger.TEN, Conversions.convertNumeric(BigDecimal.TEN, BigInteger.class));
    }

    @Test
    public void convertBoolean() {
        Assert.assertEquals(Boolean.TRUE, Conversions.convertString("true", Boolean.class));
        Assert.assertEquals(true, Conversions.convertString("true", Boolean.TYPE));
        Assert.assertEquals(Boolean.FALSE, Conversions.convertString("false", Boolean.class));
        Assert.assertEquals(false, Conversions.convertString("false", Boolean.TYPE));
        Assert.assertEquals(Boolean.FALSE, Conversions.convertString("not a boolean", Boolean.class));
    }

    @Test
    public void convertString() {
        Assert.assertEquals("a string", Conversions.convertString("a string", String.class));
    }

    // TODO change expected exception according to Conversions.convertString
    @Test(expected = NullPointerException.class)
    public void convertANullLongPrimitive() {
        Conversions.convertString(null, Long.TYPE);
    }

    // TODO change expected exception according to Conversions.convertString
    @Test(expected = NullPointerException.class)
    public void convertANullIntegerPrimitive() {
        Conversions.convertString(null, Integer.TYPE);
    }

    // TODO change expected exception according to Conversions.convertString
    @Test(expected = NullPointerException.class)
    public void convertANullShortPrimitive() {
        Conversions.convertString(null, Short.TYPE);
    }

    // TODO change expected exception according to Conversions.convertString
    @Test(expected = NullPointerException.class)
    public void convertANullBytePrimitive() {
        Conversions.convertString(null, Byte.TYPE);
    }

    // TODO change expected exception according to Conversions.convertString
    @Test(expected = NullPointerException.class)
    public void convertANullDoublePrimitive() {
        Conversions.convertString(null, Double.TYPE);
    }

    // TODO change expected exception according to Conversions.convertString
    @Test(expected = NullPointerException.class)
    public void convertANullFloatPrimitive() {
        Conversions.convertString(null, Float.TYPE);
    }

    // TODO change expected exception according to Conversions.convertString
    @Test(expected = NullPointerException.class)
    public void convertANullBooleanPrimitive() {
        Conversions.convertString(null, Boolean.TYPE);
    }

    // TODO test for Conversions.convertCollection
}
