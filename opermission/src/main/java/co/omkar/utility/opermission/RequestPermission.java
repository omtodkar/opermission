package co.omkar.utility.opermission;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import co.omkar.utility.opermission.annotation.DeniedPermission;
import co.omkar.utility.opermission.annotation.GrantedPermission;
import co.omkar.utility.opermission.bean.PermBean;
import co.omkar.utility.opermission.bean.Permission;
import co.omkar.utility.opermission.bean.Result;
import co.omkar.utility.opermission.dialog.PermissionDialogFragment;
import co.omkar.utility.opermission.utility.mLog;

/**
 * <p>Request permission is builder class to
 * prepare a permission request.</p>
 * Created on 12/08/16.
 *
 * @author Omkar Todkar
 */
public final class RequestPermission {
    private static final String TAG = "oPermission";

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
     * An Object where annotation classes are declared.
     */
    private static Object mBase;

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
        debug(true);
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
        if (permBean.getPermissions().isEmpty()) {
            throw new NullPointerException("Permission and Message collection cannot be null !");
        }
        RequestPermission.permBean = permBean;
        return this;
    }

    /**
     * Turn off oPermission logs while requesting permissions.
     *
     * @param mode boolean value.
     * @return modified instance of RequestPermission with debug ON/OFF.
     */
    public RequestPermission debug(boolean mode) {
        mLog.setDebugMode(mode);
        return this;
    }

    /**
     * Set a initialized class where you declared result annotated methods.
     * Example - instance of Presenter, ViewModel, etc.
     *
     * @param obj instance of Class containing {@link GrantedPermission}
     *            / {@link DeniedPermission} methods.
     * @return Modified instance of RequestPermission.
     */
    public RequestPermission setResultTarget(Object obj) {
        RequestPermission.mBase = obj;
        return this;
    }

    /**
     * <p>Execute request for all provided permissions. It checks if permissions
     * are already granted or required to grant.</p>
     * <p>Only if only API is greater than 23 (i.e. Marshmallows or later).</p>
     */
    public void request() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // if result target is not set use activity as default.
            if (RequestPermission.mBase == null) {
                RequestPermission.mBase = mActivity;
            }

            PermBean bean = new PermBean();
            Map<Permission, String> map = permBean.getPermissions();
            for (Map.Entry<Permission, String> m : map.entrySet()) {
                if (mActivity.checkSelfPermission(m.getKey().toString()) != PackageManager.PERMISSION_GRANTED) {
                    bean.put(m.getKey(), m.getValue());
                }
                // TODO: 19/08/16 if already granted invoke annotated respective methods.
            }

            // pass everything to dialog fragment.
            if (bean.size() > 0) {
                showDialog(bean);
            } else {
                mLog.i(TAG, "request: Redundant");
            }
        }
        // TODO: 19/08/16 invoke all methods considering permissions are granted.
    }

    /**
     * <p>Check weather single or multiple permissions requires grant.</p>
     * Use instead {@link RequestPermission#request()} directly.
     *
     * @param mContext Android Application Context.
     * @param permBean {@link PermBean} wrapper class.
     * @return boolean value if any one of permission requires to be asked.
     */
    @Deprecated
    public static boolean isPermissionRequired(@NonNull Context mContext, @NonNull PermBean permBean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }
        if (permBean.size() > 0) {
            Map<Permission, String> map = permBean.getPermissions();
            for (Permission permission : map.keySet()) {
                int status = mContext.checkSelfPermission(permission.toString());
                if (status != PackageManager.PERMISSION_GRANTED) return true;
            }
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
            HashMap<Permission, Result> resultMap = new HashMap<>(count);

            List<String> granted = new ArrayList<>(count);
            List<String> denied = new ArrayList<>(count);

            for (int k = 0; k < count; k++) {
                resultMap.put(Permission.get(permissions[k]), Result.get(grantResults[k]));

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
                invokeAnnotatedMethods(resultMap);
            } catch (IllegalAccessException e) {
                mLog.e(TAG, "invokeGrantMethods: invoke method mechanism failed", e);
            } catch (InvocationTargetException e) {
                mLog.e(TAG, "invokeGrantMethods: invoke method mechanism failed", e);
            }

            /* Send local broadcast on permissions result. */
            Intent intent = new Intent(PERMISSION_RESULT_BROADCAST);
            intent.putExtra(GRANTED, grantedArray);
            intent.putExtra(DENIED, deniedArray);
            LocalBroadcastManager.getInstance(mActivity).sendBroadcast(intent);
        }
    }

    /**
     * Invoke annotated methods in provided activity.
     *
     * @param resultMap prepared Map of Permissions and respective results.
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private static void invokeAnnotatedMethods(HashMap<Permission, Result> resultMap)
            throws InvocationTargetException, IllegalAccessException {
        Method[] methods = getBase().getClass().getMethods();

        // check all methods from provided class or by default provided activity.
        for (Method method : methods) {

            if (method != null && method.isAnnotationPresent(GrantedPermission.class)) {
                GrantedPermission granted = method.getAnnotation(GrantedPermission.class);

                if (granted != null) {

                    /* Check single value annotations */
                    for (Map.Entry<Permission, Result> permResult : resultMap.entrySet()) {

                        // invoke method if String permission is annotated.
                        if (granted.permission().equals(permResult.getKey().toString())
                                && Result.GRANTED == permResult.getValue()) {
                            method.invoke(getBase());
                            mLog.i(TAG, "invoking string annotated grant method: " + method.getName());
                            continue;
                        }

                        // invoke method if Permission enum is annotated.
                        if (granted.value() == permResult.getKey()
                                && Result.GRANTED == permResult.getValue()) {
                            method.invoke(getBase());
                            mLog.i(TAG, "invoking enum annotated grant method: " + method.getName());
                        }
                    }

                    /* Check array fields in annotations */

                    if (granted.values().length > 0) {
                        if (allValuesGranted(granted.values(), resultMap)) {
                            method.invoke(getBase());
                        }
                        continue;
                    }

                    if (granted.permissions().length > 0) {
                        if (allValuesGranted(granted.permissions(), resultMap)) {
                            method.invoke(getBase());
                        }
                    }
                }
            } else if (method != null && method.isAnnotationPresent(DeniedPermission.class)) {
                DeniedPermission denied = method.getAnnotation(DeniedPermission.class);

                if (denied != null) {

                    /* Check single value annotations */

                    for (Map.Entry<Permission, Result> permResult : resultMap.entrySet()) {

                        // invoke method if String permission is annotated.
                        if (denied.permission().equals(permResult.getKey().toString())
                                && Result.DENIED == permResult.getValue()) {
                            method.invoke(getBase());
                            mLog.i(TAG, "invoking string annotated denied method: " + method.getName());
                            continue;
                        }

                        // invoke method if Permission enum is annotated.
                        if (denied.value() == permResult.getKey()
                                && Result.DENIED == permResult.getValue()) {
                            method.invoke(getBase());
                            mLog.i(TAG, "invoking enum annotated denied method: " + method.getName());
                        }
                    }

                    /* Check array fields in annotations */

                    if (denied.values().length > 0) {
                        if (anyValueDenied(denied.values(), resultMap)) {
                            method.invoke(getBase());
                            mLog.i(TAG, "invoking string annotated denied method: " + method.getName());
                        }
                        continue;
                    }

                    if (denied.permissions().length > 0) {
                        if (anyValueDenied(denied.permissions(), resultMap)) {
                            mLog.i(TAG, "invoking enum annotated denied method: " + method.getName());
                            method.invoke(getBase());
                        }
                    }
                }
            }

        }
    }

    /**
     * Check if annotated result permissions contains all values from annotated array.
     * Then it also check if all permissions in annotated method value array are granted.
     *
     * @param values    Annotated value ({@link Permission}) array.
     * @param resultMap Permission and respective result map.
     * @return false if any of above condition fails.
     */
    private static boolean allValuesGranted(Permission[] values, HashMap<Permission, Result> resultMap) {
        Set<Permission> valueSet = new HashSet<>(Arrays.asList(values));
        if (resultMap.keySet().containsAll(valueSet)) {
            for (Permission value : values) {
                if (Result.GRANTED != resultMap.get(value)) {
                    mLog.i(TAG, "allValuesGranted: value denied - " + value.toString());
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Check if annotated result permissions contains all values from annotated array.
     * Then it also check if all permissions in annotated method value array are granted.
     *
     * @param strings   Annotated value ({@link android.Manifest.permission}) array.
     * @param resultMap Permission and respective result map.
     * @return false if any of above condition fails.
     */
    private static boolean allValuesGranted(String[] strings, HashMap<Permission, Result> resultMap) {
        Set<String> valueSet = new HashSet<>(Arrays.asList(strings));
        Set<String> permission = new HashSet<>();
        for (Permission perm : resultMap.keySet()) {
            permission.add(perm.toString());
        }
        if (permission.containsAll(valueSet)) {
            for (String value : strings) {
                if (Result.GRANTED != resultMap.get(Permission.get(value))) {
                    mLog.i(TAG, "allValuesGranted: value denied - " + value);
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Check if annotated result permissions contains all values from annotated array.
     * Then it also check if any permission in annotated method value array is denied.
     *
     * @param values    Annotated value ({@link Permission}) array.
     * @param resultMap Permission and respective result map.
     * @return false if first statement is not satisfied else true if both are satisfied.
     */
    private static boolean anyValueDenied(Permission[] values, HashMap<Permission, Result> resultMap) {
        Set<Permission> valueSet = new HashSet<>(Arrays.asList(values));
        if (resultMap.keySet().containsAll(valueSet)) {
            for (Permission value : values) {
                if (Result.DENIED == resultMap.get(value)) {
                    mLog.i(TAG, "anyValueDenied: value denied - " + value.toString());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if annotated result permissions contains all values from annotated array.
     * Then it also check if any permission in annotated method value array is denied.
     *
     * @param strings   Annotated value ({@link android.Manifest.permission}) array.
     * @param resultMap Permission and respective result map.
     * @return false if first statement is not satisfied else true if both are satisfied.
     */
    private static boolean anyValueDenied(String[] strings, HashMap<Permission, Result> resultMap) {
        Set<String> valueSet = new HashSet<>(Arrays.asList(strings));
        Set<String> permissionSet = new HashSet<>();
        for (Permission perm : resultMap.keySet()) {
            permissionSet.add(perm.toString());
        }
        if (permissionSet.containsAll(valueSet)) {
            for (String value : strings) {
                if (Result.DENIED == resultMap.get(Permission.get(value))) {
                    mLog.i(TAG, "anyValueDenied: value denied - " + value);
                    return true;
                }
            }
        }
        return false;
    }

    private static Object getBase() {
        return RequestPermission.mBase;
    }
}