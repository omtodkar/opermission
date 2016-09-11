package co.omkar.utility;

import android.Manifest;

import co.omkar.utility.opermission.annotation.DeniedPermission;
import co.omkar.utility.opermission.annotation.GrantedPermission;
import co.omkar.utility.opermission.annotation.OPermission;
import co.omkar.utility.opermission.annotation.Result;
import co.omkar.utility.opermission.bean.Permission;

/**
 * MVPExample Presenter class.
 */
public class Presenter {

    private ExampleView mView;

    public Presenter(ExampleView mView) {
        this.mView = mView;
    }

    /**
     * <p>Executes this method on permission result.
     * Get boolean result as method parameter.
     * Granted = True && Denied = False.</p>
     *
     * @param isGranted boolean on permission result.
     */
    @OPermission(value = Permission.WRITE_EXTERNAL_STORAGE)
    void onStoragePermission(@Result boolean isGranted) {
        if (isGranted) {
            mView.updateView("\n Write Storage Granted");
        } else {
            mView.updateView("\n Write Storage Denied");
        }
    }

    /**
     * <p>Executes this method on permission results.
     * Get boolean result as method parameter.</p>
     *
     * @param isGranted True only if all permissions are granted else False.
     */
    @OPermission(values = {Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION})
    void onLocationPermission(@Result boolean isGranted) {
        if (isGranted) {
            mView.updateView("\n Location status Granted");
        } else {
            mView.updateView("\n Location status Denied");
        }
    }

    /**
     * Executes method on READ_PHONE_STATE method is granted.
     */
    @GrantedPermission(permission = Manifest.permission.READ_PHONE_STATE)
    void onReadPhonePermissionGranted() {
        mView.updateView("\n Read Phone status Granted");
    }

    /**
     * Executes method on READ_PHONE_STATE method is denied.
     */
    @DeniedPermission(permission = Manifest.permission.READ_PHONE_STATE)
    void onReadPhonePermissionDenied() {
        mView.updateView("\n Read Phone status Denied");
    }
}
