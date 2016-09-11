package co.omkar.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;

import co.omkar.utility.opermission.RequestPermission;
import co.omkar.utility.opermission.bean.PermBean;
import co.omkar.utility.opermission.bean.Permission;

/**
 * <p>MVPExample activity class for live testing library.</p>
 *
 * @author Omkar Todkar.
 */
public class MVPExample extends AppCompatActivity implements ExampleView {
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
        message.setText("Asking permission...");

        Presenter mPresenter = new Presenter(this);

        /* Multiple permission example. */
        PermBean permBean = new PermBean();

        // wrapper of permissions.
        permBean.put(Permission.READ_PHONE_STATE, "To process some features application required permissions to access your phone state.");
        permBean.put(Permission.WRITE_EXTERNAL_STORAGE, "To store some of your important data related application we required permission to access your External Storage.");

        String locationMessage = "To know your location application required permission to access your GPS";
        permBean.put(Permission.ACCESS_FINE_LOCATION, locationMessage);
        permBean.put(Permission.ACCESS_COARSE_LOCATION, locationMessage);

        // prepare permission request.
        RequestPermission.on(this)
                .with(permBean)
                .setResultTarget(mPresenter)
                .request();
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

    @Override
    public void updateView(String data) {
        message.append(data);
    }

    public void switchActivity(View view) {
        startActivity(new Intent(this, ExampleActivity.class));
        finish();
    }
}
