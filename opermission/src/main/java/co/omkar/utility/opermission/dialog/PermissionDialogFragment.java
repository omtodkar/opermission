package co.omkar.utility.opermission.dialog;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import co.omkar.utility.opermission.R;
import co.omkar.utility.opermission.RequestPermission;
import co.omkar.utility.opermission.bean.PermBean;
import co.omkar.utility.opermission.bean.Permission;
import co.omkar.utility.opermission.utility.mLog;

import static android.view.View.OnClickListener;


/**
 * <p>Permission Message dialog box. This Fragment is shown
 * with rationale messages just before permissions are asked.</p>
 * Created on 10/08/16.
 *
 * @author Omkar Todkar
 */
public class PermissionDialogFragment extends DialogFragment implements OnClickListener, OnPageChangeListener {
    private static final String TAG = "RequestPermission";

    private static final String PERMISSION = "permission";
    private static final String REQUEST = "request";

    LinearLayout dialogView;

    ViewPager viewPager;

    TextView counter;

    ImageButton next;

    ImageButton previous;

    PagerAdapter adapter;

    private HashMap<Permission, String> mBean;

    private String[] messages;
    private String[] permissions;

    private int position;
    private int size;
    private int requestCode;

    public PermissionDialogFragment() {
    }

    /**
     * A static dialog fragment instance creator method.
     *
     * @param bean        {@link PermBean}
     * @param requestCode any 16 bit {@link Integer} value.
     * @return new prepared instance of PermissionDialogFragment.
     */
    public static PermissionDialogFragment getInstance(PermBean bean, int requestCode) {
        if (bean == null) throw new NullPointerException("Permission Beans cannot be null !");
        Bundle extras = new Bundle(3);

        // convert map to two arrays.
        HashMap<Permission, String> map = (HashMap<Permission, String>) bean.getPermissions();

        // put arrays in extras.
        extras.putSerializable(PERMISSION, map);
        extras.putInt(REQUEST, requestCode);

        // set extras in fragment and return.
        PermissionDialogFragment fragment = new PermissionDialogFragment();
        fragment.setArguments(extras);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        if (extras != null) {
            //noinspection unchecked
            mBean = (HashMap<Permission, String>) extras.getSerializable(PERMISSION);

            if (mBean != null && !mBean.isEmpty()) {
                /* To avoid duplicity of provided messages. */
                Set<String> msgs = new LinkedHashSet<String>();
                msgs.addAll(mBean.values());
                messages = msgs.toArray(new String[msgs.size()]);

                /* extract permissions value from enum */
                Set<String> perm = new LinkedHashSet<String>();
                for (Permission p : mBean.keySet()) {
                    perm.add(p.toString());
                }
                permissions = perm.toArray(new String[perm.size()]);
            }
            requestCode = extras.getInt(REQUEST);
            size = messages.length;
            mLog.i(TAG, "Dialog: rationale message size is " + size);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.permission_dialog_layout, container, false);
        dialogView = (LinearLayout) view.findViewById(R.id.dialog_view);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);

        counter = (TextView) view.findViewById(R.id.count);
        counter.setOnClickListener(this);

        next = (ImageButton) view.findViewById(R.id.button_next);
        next.setOnClickListener(this);

        previous = (ImageButton) view.findViewById(R.id.button_prev);
        previous.setOnClickListener(this);

        if (size > 0) {
            if (size == 1) {
                next.setVisibility(View.INVISIBLE);
                previous.setVisibility(View.INVISIBLE);
                counter.setText(getString(R.string.ok));
                counter.setTextColor(Color.parseColor("#018c7a"));

                if (messages[0] == null) {
                    messages[0] = "Please allow permissions to enjoy all features of application.";
                }
            } else {
                String count = 1 + "/" + size;
                counter.setText(count);
            }
        } else if (size == 0) {
            next.setVisibility(View.INVISIBLE);
            previous.setVisibility(View.INVISIBLE);

            size = 1;
            messages = new String[size];
            messages[0] = "Please allow permissions to enjoy all features of application.";

            counter.setText(getString(R.string.ok));
            counter.setTextColor(Color.parseColor("#018c7a"));
        }
        adapter = new PagerAdapter(getChildFragmentManager(), messages);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        return view;
    }

    /**
     * Work around for dialog not to dismiss on back button press.
     */
    @Override
    public void onResume() {
        super.onResume();
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent keyEvent) {
                return keyCode != KeyEvent.ACTION_DOWN;
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.button_next) {
            if (position < size) {
                viewPager.setCurrentItem(position + 1);
            }

        } else if (id == R.id.button_prev) {
            if (position > 0) {
                viewPager.setCurrentItem(position - 1);
            }

        } else if (id == R.id.count) {
            if ((position + 1) == size) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    dialogView.setVisibility(View.GONE);
                    requestPermissions(permissions, requestCode);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        RequestPermission.onResult(requestCode, permissions, grantResults);
        dismissAllowingStateLoss();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        this.position = position;
        if ((position + 1) == size) {
            counter.setText(getString(R.string.ok));
            counter.setTextColor(Color.parseColor("#018c7a"));
        } else {
            String count = (position + 1) + "/" + size;
            counter.setText(count);
            counter.setTextColor(Color.parseColor("#595959"));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
