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

package co.omkar.utility.opermission.bean;

import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Permissions result value enum</p>
 * Created on 19/08/16.
 *
 * @author Omkar Todkar.
 */
public enum Result {

    GRANTED(0), DENIED(-1);

    private int value;

    Result(int value) {
        this.value = value;
    }

    private static SparseArray<Result> resultLookup = new SparseArray<>(2);

    /* populate map */
    static {
        for (Result type : Result.values()) {
            resultLookup.append(type.value, type);
        }
    }

    /**
     * To find enum constant by its value.
     */
    public static Result get(int value) {
        return resultLookup.get(value);
    }
}
