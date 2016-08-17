package co.omkar.utility;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import co.omkar.utility.opermission.RequestPermission;
import co.omkar.utility.opermission.annotation.DeniedPermission;
import co.omkar.utility.opermission.annotation.GrantedPermission;
import co.omkar.utility.opermission.bean.PermBean;
import co.omkar.utility.opermission.bean.Permission;

public class Example extends AppCompatActivity {
    private static final String TAG = "Example";

    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        message = (TextView) findViewById(R.id.text);

        /* Multiple permission example. */
        PermBean permBean = new PermBean();  // wrapper of permissions.
        permBean.put(Permission.READ_PHONE_STATE, "To process some features application required permissions to access your phone state.");
        permBean.put(Permission.ACCESS_FINE_LOCATION, "To know your location application required permission to access your GPS");
        permBean.put(Permission.ACCESS_COARSE_LOCATION, "To know your location application required permission to access your GPS");
        permBean.put(Permission.WRITE_EXTERNAL_STORAGE, "To store some of your important data related application we required permission to access your External Storage.");

        // prepare permission request.
        if (RequestPermission.isPermissionRequired(this, permBean)) {
            RequestPermission.on(this).with(permBean).request();
        }
        message.setText("Asking permission...");
    }

    @GrantedPermission(value = Permission.READ_PHONE_STATE)
    void onReadPhonePermissionGranted() {
        message.append("\n Read Phone status Granted");
    }

    @DeniedPermission(value = Permission.READ_PHONE_STATE)
    void onReadPhonePermissionDenied() {
        message.append("\n Read Phone status Denied");
    }

    @GrantedPermission(value = Permission.ACCESS_FINE_LOCATION)
    void onLocationFinePermissionGranted() {
        message.append("\n Location Fine status Granted");
    }

    @DeniedPermission(value = Permission.ACCESS_FINE_LOCATION)
    void onLocationFinePermissionDenied() {
        message.append("\n Location Fine status Denied");
    }


    @GrantedPermission(value = Permission.ACCESS_FINE_LOCATION)
    void onLocationCrossPermissionGranted() {
        message.append("\n Location Corss status Granted");
    }

    @DeniedPermission(value = Permission.ACCESS_FINE_LOCATION)
    void onLocationCrossPermissionDenied() {
        message.append("\n Location Cross status Denied");
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
