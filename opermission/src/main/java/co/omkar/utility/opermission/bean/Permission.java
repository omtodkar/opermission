package co.omkar.utility.opermission.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Omya on 12/08/16.
 * <p/>
 * Dangerous permissions which requires
 * runtime permissions.
 */
public enum Permission {

    NULL(null),

    // location
    ACCESS_COARSE_LOCATION("android.permission.ACCESS_COARSE_LOCATION"),
    ACCESS_FINE_LOCATION("android.permission.ACCESS_FINE_LOCATION"),

    // contacts
    READ_CONTACTS("android.permission.READ_CONTACTS"),
    WRITE_CONTACTS("android.permission.WRITE_CONTACTS"),
    GET_ACCOUNTS("android.permission.GET_ACCOUNTS"),

    // calender
    READ_CALENDAR("android.permission.READ_CALENDAR"),
    WRITE_CALENDAR("android.permission.WRITE_CALENDAR"),

    // camera
    CAMERA("android.permission.CAMERA"),

    // microphone audio
    RECORD_AUDIO("android.permission.RECORD_AUDIO"),

    // phone
    READ_PHONE_STATE("android.permission.READ_PHONE_STATE"),
    CALL_PHONE("android.permission.CALL_PHONE"),
    READ_CALL_LOG("android.permission.READ_CALL_LOG"),
    WRITE_CALL_LOG("android.permission.WRITE_CALL_LOG"),
    ADD_VOICEMAIL("com.android.voicemail.permission.ADD_VOICEMAIL"),
    USE_SIP("android.permission.USE_SIP"),
    PROCESS_OUTGOING_CALLS("android.permission.PROCESS_OUTGOING_CALLS"),

    // sensors
    BODY_SENSORS("android.permission.BODY_SENSORS"),

    // sms
    SEND_SMS("android.permission.SEND_SMS"),
    RECEIVE_SMS("android.permission.RECEIVE_SMS"),
    READ_SMS("android.permission.READ_SMS"),
    RECEIVE_WAP_PUSH("android.permission.RECEIVE_WAP_PUSH"),
    RECEIVE_MMS("android.permission.RECEIVE_MMS"),

    // storage
    READ_EXTERNAL_STORAGE("android.permission.READ_EXTERNAL_STORAGE"),
    WRITE_EXTERNAL_STORAGE("android.permission.WRITE_EXTERNAL_STORAGE");

    private final String name;

    Permission(String name) {
        this.name = name;
    }

    private static Map<String, Permission> mPermissions = new HashMap<>();

    static {
        for (Permission permission : Permission.values()) {
            mPermissions.put(permission.toString(), permission);
        }
    }

    public static Permission get(String permission) {
        return mPermissions.get(permission);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
