package co.omkar.utility.opermission;

import org.junit.Assert;
import org.junit.Test;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    String[] arr = new String[]{};

    @Test
    public void array_isEmpty() throws Exception {
        Assert.assertEquals("Array has 0 size", 0, arr.length);
    }

    @Test
    public void array_isNull() throws Exception {
        Assert.assertArrayEquals("Array is null", new String[]{}, arr);
    }
}