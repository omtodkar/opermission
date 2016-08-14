package co.omkar.utility;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;

import org.testng.annotations.Test;

import co.omkar.utility.opermission.bean.PermBean;
import co.omkar.utility.opermission.bean.Permission;
import co.omkar.utility.opermission.RequestPermission;

/**
 * Created by Omya on 12/08/16.
 */
public class ExampleTest extends AppCompatActivity {

    @Test
    public void testOnCreate() throws Exception {
        RequestPermission.on(this)
                .with(new PermBean(
                                Permission.valueOf(Manifest.permission.BATTERY_STATS).toString()
                        )
                );
    }
}