package co.omkar.utility;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import co.omkar.utility.databinding.ActivityExampleBinding;
import co.omkar.utility.opermission.RequestPermission;
import co.omkar.utility.opermission.annotation.DeniedPermission;
import co.omkar.utility.opermission.annotation.GrantedPermission;
import co.omkar.utility.opermission.bean.PermBean;
import co.omkar.utility.opermission.bean.Permission;

public class Example extends AppCompatActivity {
    private static final String TAG = "Example";

    private Permission[] permissions;
    private ActivityExampleBinding mBinding;
    private StringBuilder message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_example);

        /**
         * Multiple permission example.
         */
        permissions = new Permission[]{    // prepare array of permissions
                Permission.ACCESS_COARSE_LOCATION, Permission.LOCATION,
                Permission.WRITE_EXTERNAL_STORAGE
        };

        PermBean permBean = new PermBean(permissions);  // wrapper of permissions.

        // prepare permission request.
        if (RequestPermission.isPermissionRequired(this, new PermBean(permissions))) {
            RequestPermission.on(this).with(permBean).request();
        }

        message = new StringBuilder("Asking permission...");
        mBinding.setMessage(message.toString());

    }

    @GrantedPermission(values = Permission.READ_PHONE_STATE)
    void onReadPhonePermissionGranted() {
        message.append("\n Read Phone status Granted");
        mBinding.setMessage(message.toString());
    }

    @DeniedPermission(value = Permission.READ_PHONE_STATE)
    void onReadPhonePermissionDenied() {
        message.append("\n Read Phone status Denied");
        mBinding.setMessage(message.toString());
    }

    @GrantedPermission(value = Permission.LOCATION)
    void onLocationPermissionGranted() {
        message.append("\n Location status Granted");
        mBinding.setMessage(message.toString());
    }

    @DeniedPermission(value = Permission.LOCATION)
    void onLocationPermissionDenied() {
        message.append("\n Location status Denied");
        mBinding.setMessage(message.toString());
    }
}
