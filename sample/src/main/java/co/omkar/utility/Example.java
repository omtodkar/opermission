package co.omkar.utility;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import co.omkar.utility.opermission.RequestPermission;
import co.omkar.utility.opermission.annotation.DeniedPermission;
import co.omkar.utility.opermission.annotation.GrantedPermission;
import co.omkar.utility.opermission.bean.PermBean;
import co.omkar.utility.opermission.bean.Permission;

public class Example extends AppCompatActivity {
    private static final String TAG = "Example";

    private Permission[] permissions;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        message = (TextView) findViewById(R.id.text);

        /**
         * Multiple permission example.
         */
        permissions = new Permission[]{    // prepare array of permissions
                Permission.READ_PHONE_STATE, Permission.LOCATION,
                Permission.WRITE_EXTERNAL_STORAGE
        };

        String[] messages = new String[]{
                "To process some features application required permissions to access your phone state.",
                "To know your location application required permission to access your GPS",
                "To store some of your important data related application we required permission to access your External Storage."
        };


        PermBean permBean = new PermBean(permissions);  // wrapper of permissions.

        // prepare permission request.
        if (RequestPermission.isPermissionRequired(this, new PermBean(permissions, messages))) {
            RequestPermission.on(this).with(permBean).request();
        }

        message.setText("Asking permission...");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        RequestPermission.onResult(requestCode, permissions, grantResults);
    }

    @GrantedPermission(value = Permission.READ_PHONE_STATE)
    void onReadPhonePermissionGranted() {
        message.append("\n Read Phone status Granted");
    }

    @DeniedPermission(value = Permission.READ_PHONE_STATE)
    void onReadPhonePermissionDenied() {
        message.append("\n Read Phone status Denied");
    }

    @GrantedPermission(value = Permission.LOCATION)
    void onLocationPermissionGranted() {
        message.append("\n Location status Granted");
    }

    @DeniedPermission(value = Permission.LOCATION)
    void onLocationPermissionDenied() {
        message.append("\n Location status Denied");
    }

    @GrantedPermission(value = Permission.WRITE_EXTERNAL_STORAGE)
    void onWriteStorageGranted() {
        message.append("\n Write Storage Granted");
    }

    @DeniedPermission(value = Permission.WRITE_EXTERNAL_STORAGE)
    void onWriteStorageDenied() {
        message.append("\n Write Storage Denied");
    }
}
