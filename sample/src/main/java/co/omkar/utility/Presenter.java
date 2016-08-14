package co.omkar.utility;

import android.util.Log;

import co.omkar.utility.opermission.annotation.DeniedPermission;
import co.omkar.utility.opermission.bean.Permission;

/**
 * Created by Omya on 13/08/16.
 * <p>
 * Example presenter class.
 */
public class Presenter {
    private static final String TAG = "Presenter";

    public static Presenter getInstance() {
        return new Presenter();
    }

    @DeniedPermission(Permission.READ_PHONE_STATE)
    void onReadPermissionDenied() {
        Log.e(TAG, "onReadPermissionDenied");
    }
}
