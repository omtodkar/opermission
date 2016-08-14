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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.omkar.utility.opermission.R;
import co.omkar.utility.opermission.RequestPermission;
import co.omkar.utility.opermission.bean.PermBean;

import static android.view.View.OnClickListener;


/**
 * Created by Omya on 10/08/16.
 * <p/>
 * Permission Message dialog box. This Fragment is shown
 * with rationale messages just before permissions are asked.
 */
public class PermissionDialogFragment extends DialogFragment implements OnClickListener, OnPageChangeListener {
    private static final String TAG = "PermissionDialog";
    private static final String MESSAGE = "message";
    private static final String PERMISSION = "permission";
    private static final String REQUEST = "request";

    ViewPager viewPager;

    TextView counter;

    ImageButton next;

    ImageButton previous;

    PagerAdapter adapter;

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
        extras.putStringArray(MESSAGE, bean.getRationaleMessage());
        extras.putStringArray(PERMISSION, bean.getPermission());
        extras.putInt(REQUEST, requestCode);
        PermissionDialogFragment fragment = new PermissionDialogFragment();
        fragment.setArguments(extras);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getArguments();

        if (extras != null) {
            String[] msgs = extras.getStringArray(MESSAGE);

            /* Process to avoid duplicity of default messages. */
            if (msgs != null) {
                int count = msgs.length;    // calculate size.

                if (count > 1) {
                    List<String> temp = new ArrayList<>();

                    for (int k = 0; k < count; k++) {
                        if (k == 0) {       // add first message as it is.
                            temp.add(msgs[k]);
                        } else if (k > 0    // check on second message if it is similar to first one.
                                && !msgs[k].equals(msgs[k - 1])) {
                            temp.add(msgs[k]);
                        }
                    }

                    messages = temp.toArray(new String[temp.size()]);
                } else {

                    messages = msgs;
                }
            }

            permissions = extras.getStringArray(PERMISSION);
            requestCode = extras.getInt(REQUEST);
            size = messages.length;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.permission_dialog_layout, container, false);
        if (size > 0) {
            viewPager = (ViewPager) view.findViewById(R.id.view_pager);

            counter = (TextView) view.findViewById(R.id.count);
            counter.setOnClickListener(this);
            String count = 1 + "/" + size;
            counter.setText(count);

            next = (ImageButton) view.findViewById(R.id.button_next);
            next.setOnClickListener(this);

            previous = (ImageButton) view.findViewById(R.id.button_prev);
            previous.setOnClickListener(this);

            adapter = new PagerAdapter(getChildFragmentManager(), messages);
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(this);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getActivity().requestPermissions(permissions, requestCode);
                dismissAllowingStateLoss();
            }
        }
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
