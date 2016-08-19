package co.omkar.utility;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.Arrays;

import co.omkar.utility.opermission.RequestPermission;
import co.omkar.utility.opermission.annotation.DeniedPermission;
import co.omkar.utility.opermission.annotation.GrantedPermission;
import co.omkar.utility.opermission.bean.PermBean;
import co.omkar.utility.opermission.bean.Permission;

/**
 * <p>Example activity class for live testing library.</p>
 *
 * @author Omkar Todkar.
 */
public class Example extends AppCompatActivity {
    private static final String TAG = "Example";

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            String[] grantedPermissions = extras.getStringArray(RequestPermission.GRANTED);
            String[] deniedPermissions = extras.getStringArray(RequestPermission.DENIED);

            if (grantedPermissions != null) {
                Log.e(TAG, "onReceive: Granted Permissions - " + Arrays.toString(grantedPermissions));
            }
            if (deniedPermissions != null) {
                Log.e(TAG, "onReceive: Denied Permissions - " + Arrays.toString(deniedPermissions));
            }
        }
    };

    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        message = (TextView) findViewById(R.id.text);

        /* Multiple permission example. */
        PermBean permBean = new PermBean();

        // wrapper of permissions.
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

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(RequestPermission.PERMISSION_RESULT_BROADCAST));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @GrantedPermission(permission = Manifest.permission.READ_PHONE_STATE)
    void onReadPhonePermissionGranted() {
        message.append("\n Read Phone status Granted");
    }

    @DeniedPermission(permission = Manifest.permission.READ_PHONE_STATE)
    void onReadPhonePermissionDenied() {
        message.append("\n Read Phone status Denied");
    }

    @GrantedPermission(values = {Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION})
    void onLocationPermissionGranted() {
        message.append("\n Location status Granted");
    }

    @DeniedPermission(permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
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
