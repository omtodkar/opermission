/*
    Copyright (C) 2016 Omkar Todkar

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/
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