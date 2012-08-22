package vinna.util;

import org.junit.Assert;
import org.junit.Test;

public class MultivaluedHashMapTest {

    @Test
    public void getAlwaysReturnAList() {
        MultivaluedHashMap<?, ?> map = new MultivaluedHashMap<>();

        Assert.assertNotNull(map.get("a key"));
        Assert.assertTrue(map.get("anotherKey").isEmpty());
    }

    @Test
    public void returnTheFirstValue() {
        MultivaluedHashMap<String, String> map = new MultivaluedHashMap<>();
        map.add("key", "first value");
        map.add("key", "second value");

        Assert.assertEquals("first value", map.getFirst("key"));
    }

    @Test
    public void putSingleRemovePreviousValue() {
        MultivaluedHashMap<String, String> map = new MultivaluedHashMap<>();
        map.add("key", "first value");
        map.add("key", "second value");
        map.putSingle("key", "third value");

        Assert.assertEquals("third value", map.getFirst("key"));
    }

    @Test
    public void removeTest() {
        MultivaluedHashMap<String, String> map = new MultivaluedHashMap<>();
        map.add("key", "first value");
        map.add("key", "second value");
        map.remove("key", "first value");

        Assert.assertEquals("second value", map.getFirst("key"));
    }

    @Test
    public void containsTest() {
        MultivaluedHashMap<String, String> map = new MultivaluedHashMap<>();
        Assert.assertFalse(map.containsKey("key"));

        map.add("key", "first value");
        Assert.assertTrue(map.containsKey("key"));
    }
}
