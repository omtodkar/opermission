package co.omkar.utility.opermission;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.omkar.utility.opermission.annotation.DeniedPermission;
import co.omkar.utility.opermission.annotation.GrantedPermission;
import co.omkar.utility.opermission.bean.PermBean;
import co.omkar.utility.opermission.bean.Permission;
import co.omkar.utility.opermission.dialog.PermissionDialogFragment;

/**
 * Created by Omya on 12/08/16.
 * <p/>
 * Request permission is builder class to
 * prepare a permission request.
 */
public final class RequestPermission {
    private static final String TAG = "RequestPermission";

    /**
     * Local broadcast on permission result.
     */
    public static final String PERMISSION_RESULT_BROADCAST = "co.omkar.utility.opermission.result";

    /**
     * Intent bundle key to get array of granted permissions
     * from broadcast intent.
     */
    public static final String GRANTED = "KeyGranted";

    /**
     * Intent bundle key to get array of denied permissions
     * from broadcast intent.
     */
    public static final String DENIED = "KeyDenied";

    /**
     * An activity instance.
     */
    private static FragmentActivity mActivity;

    /**
     * {@link Class} where annotated methods
     * {@link GrantedPermission} or {@link DeniedPermission}
     * are declared.
     */
    private static Class clazz;

    /**
     * A wrapper class with bundle of permissions
     * and respective messages.
     */
    private static PermBean permBean;

    /**
     * A permission request code.
     */
    private static int requestCode = 16989;

    /**
     * Private constructor of Request Permission.
     *
     * @param mActivity instance of activity in where
     *                  permissions are asked.
     */
    private RequestPermission(FragmentActivity mActivity) {
        RequestPermission.mActivity = mActivity;
        RequestPermission.clazz = mActivity.getClass();
    }

    /**
     * Initializing method.
     *
     * @param activity instance of activity where permissions
     *                 are asked.
     * @return prepared object of RequestPermission.
     */
    public static RequestPermission on(FragmentActivity activity) {
        return new RequestPermission(activity);
    }

    /**
     * Set preferred permission request code.
     *
     * @param code Any positive 16 bit {@link Integer} value.
     * @return prepared object of RequestPermission.
     */
    public RequestPermission code(int code) {
        if (code < 1)
            throw new NumberFormatException("Request code must be non-zero positive integer");
        else
            RequestPermission.requestCode = code;
        return this;
    }

    /**
     * Set a permission and message wrapper class {@link PermBean}
     * with desired permission/s and respective messages.
     *
     * @param permBean A wrapper class containing
     *                 permissions to be asked and
     *                 respective message to be shown.
     * @return modified object of RequestPermission containing
     * permissions to be asked and respective messages to be shown.
     */
    public RequestPermission with(PermBean permBean) {
        RequestPermission.permBean = permBean;
        return this;
    }

    /**
     * Set Presenter / Other class where
     * {@link GrantedPermission} or {@link DeniedPermission}
     * annotated methods are declared.
     *
     * @param clazz Class where annotated methods are set.
     * @return prepared object of {@link RequestPermission}.
     */
    public RequestPermission setClass(Class clazz) {
        if (clazz != null) {
            RequestPermission.clazz = clazz;
        } else {
            RequestPermission.clazz = RequestPermission.mActivity.getClass();
        }
        return this;
    }

    /**
     * Execute request for all provided permissions.
     * Only if only API > 23.
     */
    public void request() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;

        String[] permissions = permBean.getPermission();
        String[] messages = permBean.getRationaleMessage();

        if (permissions != null) {
            int count = permissions.length;
            List<String> temPerm = new ArrayList<>();
            List<String> tempMsg = new ArrayList<>();

            for (int k = 0; k < count; k++) {

                int status = ContextCompat.checkSelfPermission(mActivity, permissions[k]);

                if (status != PackageManager.PERMISSION_GRANTED) {
                    temPerm.add(permissions[k]);

                    if (messages != null) {
                        tempMsg.add(messages[k]);
                    }
                }
            }

            // pass everything to dialog fragment.
            showDialog(new PermBean(temPerm.toArray(new String[temPerm.size()]),
                    tempMsg.toArray(new String[tempMsg.size()])));
        }
    }

    /**
     * Check weather single or multiple permissions requires grant.
     *
     * @param mContext Android Application Context.
     * @param permBean {@link PermBean} wrapper class.
     * @return boolean value if any one of permission requires to be asked.
     */
    public static boolean isPermissionRequired(@NonNull Context mContext, @NonNull PermBean permBean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false;
        String[] permissions = permBean.getPermission();
        int count = permissions.length;
        int status;
        if (count > 1) {
            for (int k = 0; k < count; k++) {
                status = ContextCompat.checkSelfPermission(mContext, permissions[k]);
                if (status != PackageManager.PERMISSION_GRANTED) return true;
            }
        } else {
            status = ContextCompat.checkSelfPermission(mContext, permissions[0]);
            if (status != PackageManager.PERMISSION_GRANTED) return true;
        }
        return false;
    }

    /**
     * Show dialog fragment to show dialogs before asking permission.
     * Or you can set to show dialogs only when user denied permission earlier.
     *
     * @param permBean {@link PermBean} wrapper class
     *                 with Permission and respective messages.
     */
    private void showDialog(PermBean permBean) {
        PermissionDialogFragment fragment = PermissionDialogFragment.getInstance(permBean, requestCode);
        fragment.show(mActivity.getSupportFragmentManager(), TAG);
    }

    /**
     * Get on Request Permissions Results, invoke annotated methods and send a local broadcast of results.
     *
     * @param requestCode  Auto Generated request code.
     * @param permissions  Permissions asked.
     * @param grantResults Result of permissions asked.
     */
    public static void onResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (RequestPermission.requestCode == requestCode) {

            /* Sort granted and denied permissions in array list */
            int count = permissions.length;

            List<String> granted = new ArrayList<>(count);
            List<String> denied = new ArrayList<>(count);

            for (int k = 0; k < count; k++) {

                if (grantResults[k] == PackageManager.PERMISSION_GRANTED) {
                    granted.add(permissions[k]);

                } else if (grantResults[k] == PackageManager.PERMISSION_DENIED) {
                    denied.add(permissions[k]);

                }
            }

            String[] grantedArray = granted.toArray(new String[granted.size()]);
            String[] deniedArray = denied.toArray(new String[denied.size()]);

            // forward to invoke annotated methods.
            try {
                invokeGrantMethods(grantedArray);
                invokeDeniedMethods(deniedArray);
            } catch (IllegalAccessException e) {
                Log.e(TAG, "invokeGrantMethods: invoke method mechanism failed", e);
            } catch (InstantiationException e) {
                Log.e(TAG, "invokeGrantMethods: invoke method mechanism failed", e);
            } catch (InvocationTargetException e) {
                Log.e(TAG, "invokeGrantMethods: invoke method mechanism failed", e);
            }


            /* Send local broadcast on permissions result. */
            Intent intent = new Intent(PERMISSION_RESULT_BROADCAST);
            intent.putExtra(GRANTED, grantedArray);
            intent.putExtra(DENIED, deniedArray);
            LocalBroadcastManager.getInstance(mActivity).sendBroadcast(intent);
        }
    }

    /**
     * Invoke annotated methods on granted results.
     *
     * @param grantedPermissions Granted permissions.
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */
    private static void invokeGrantMethods(String[] grantedPermissions) throws IllegalAccessException,
            InstantiationException, InvocationTargetException {
        Method[] methods = RequestPermission.clazz.getMethods();
        Object obj = RequestPermission.clazz.newInstance();
        int permCount = grantedPermissions.length;
        int methodCount = methods.length;

        // check all methods from provided class
        // or by default provided activity.
        for (int k = 0; k < methodCount; k++) {
            Method method = methods[k];

            /* Check if annotation is present on method. */
            if (method != null && method.isAnnotationPresent(GrantedPermission.class)) {
                GrantedPermission granted = method.getAnnotation(GrantedPermission.class);

                if (granted != null) {
                    for (int j = 0; j < permCount; j++) {
                        String permission = grantedPermissions[j];

                        // invoke method if String permission is annotated.
                        if (!granted.permission().equals("")
                                && permission.equals(granted.permission())) {
                            method.invoke(obj);
                            continue;
                        }

                        // invoke method if Permission enum is annotated.
                        if (granted.value() != Permission.NULL
                                && permission.equals(granted.permission())) {
                            method.invoke(obj);
                        }
                    }

                    // invoke method if permission's String array is annotated.
                    if (granted.permissions() != null) {
                        if (isPermissionsValid(granted.permissions(), grantedPermissions)) {
                            method.invoke(obj);
                        }
                        continue;
                    }

                    // invoke method if Permission enum array is annotated.
                    if (granted.values() != null) {
                        if (isValueValid(granted.values(), grantedPermissions)) {
                            method.invoke(obj);
                        }
                    }
                }
            }
        }
    }

    /**
     * Invoke annotated methods on denied results.
     *
     * @param deniedPermission Denied permissions.
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */
    private static void invokeDeniedMethods(String[] deniedPermission) throws IllegalAccessException,
            InstantiationException, InvocationTargetException {
        Method[] methods = RequestPermission.clazz.getMethods();
        Object obj = RequestPermission.clazz.newInstance();
        int permCount = deniedPermission.length;
        int methodCount = methods.length;

        // check all methods from provided class
        // or by default provided activity.
        for (int k = 0; k < methodCount; k++) {
            Method method = methods[k];

            /* Check if annotation is present on method. */
            if (method != null && method.isAnnotationPresent(DeniedPermission.class)) {
                DeniedPermission denied = method.getAnnotation(DeniedPermission.class);

                if (denied != null) {
                    for (int j = 0; j < permCount; j++) {
                        String permission = deniedPermission[j];

                        // invoke method if String permission is annotated.
                        if (!denied.permission().equals("")
                                && permission.equals(denied.permission())) {
                            method.invoke(obj);
                            continue;
                        }

                        // invoke method if Permission enum is annotated.
                        if (denied.value() != Permission.NULL
                                && permission.equals(denied.permission())) {
                            method.invoke(obj);
                        }
                    }

                    // invoke method if permission's String array is annotated.
                    if (denied.permissions() != null) {
                        if (isPermissionsValid(denied.permissions(), deniedPermission)) {
                            method.invoke(obj);
                        }
                        continue;
                    }

                    // invoke method if Permission enum array is annotated.
                    if (denied.values() != null) {
                        if (isValueValid(denied.values(), deniedPermission)) {
                            method.invoke(obj);
                        }
                    }
                }
            } else {
                Log.e(TAG, "invokeDeniedMethods: No methods found !");
            }
        }
    }

    /**
     * Check if array of permission are in granted permissions.
     *
     * @param permEnum    Array of {@link Permission}
     * @param permissions Array of granted permissions.
     * @return true if all permissions are present.
     */
    private static boolean isValueValid(Permission[] permEnum, String[] permissions) {
        int count = permEnum.length;
        List<String> grantedPerm = Arrays.asList(permissions);
        for (int k = 0; k < count; k++) {
            if (!grantedPerm.contains(permEnum[k].toString())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if array of permission are in granted permissions.
     *
     * @param annotatedPerm Array of {@link Permission}
     * @param permissions   Array of granted permissions.
     * @return true if all permissions are present.
     */
    private static boolean isPermissionsValid(String[] annotatedPerm, String[] permissions) {
        int count = annotatedPerm.length;
        List<String> grantedPerm = Arrays.asList(permissions);
        for (int k = 0; k < count; k++) {
            if (!grantedPerm.contains(annotatedPerm[k])) {
                return false;
            }
        }
        return true;
    }

}