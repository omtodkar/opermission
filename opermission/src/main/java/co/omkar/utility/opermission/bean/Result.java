package co.omkar.utility.opermission.bean;

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

    private static Map<Integer, Result> resultLookUp = new HashMap<>(2);

    /* populate map */
    static {
        for (Result type : Result.values()) {
            resultLookUp.put(type.value, type);
        }
    }

    /**
     * To find enum constant by its value.
     */
    public static Result get(int value) {
        return resultLookUp.get(value);
    }
}
