package co.omkar.utility;

import android.Manifest;

import co.omkar.utility.opermission.annotation.DeniedPermission;
import co.omkar.utility.opermission.annotation.GrantedPermission;
import co.omkar.utility.opermission.bean.Permission;

/**
 * Example Presenter class.
 */
public class Presenter {

    private ExampleView mView;

    public Presenter(ExampleView mView) {
        this.mView = mView;
    }

    @GrantedPermission(value = Permission.WRITE_EXTERNAL_STORAGE)
    void onWriteStorageGranted() {
        mView.UpdateView("\n Write Storage Granted");
    }

    @DeniedPermission(value = Permission.WRITE_EXTERNAL_STORAGE)
    void onWriteStorageDenied() {
        mView.UpdateView("\n Write Storage Denied");
    }


    @GrantedPermission(permission = Manifest.permission.READ_PHONE_STATE)
    void onReadPhonePermissionGranted() {
        mView.UpdateView("\n Read Phone status Granted");
    }

    @DeniedPermission(permission = Manifest.permission.READ_PHONE_STATE)
    void onReadPhonePermissionDenied() {
        mView.UpdateView("\n Read Phone status Denied");
    }

    @GrantedPermission(values = {Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION})
    void onLocationPermissionGranted() {
        mView.UpdateView("\n Location status Granted");
    }

    @DeniedPermission(permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void onLocationPermissionDenied() {
        mView.UpdateView("\n Location status Denied");
    }
}
