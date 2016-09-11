/*
    Copyright (C) 2016 Omkar Todkar

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package co.omkar.utility.opermission;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import co.omkar.utility.opermission.annotation.DeniedPermission;
import co.omkar.utility.opermission.annotation.GrantedPermission;
import co.omkar.utility.opermission.annotation.OPermission;
import co.omkar.utility.opermission.bean.PermBean;
import co.omkar.utility.opermission.bean.Permission;
import co.omkar.utility.opermission.bean.Result;
import co.omkar.utility.opermission.dialog.PermissionDialogFragment;

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
     * A pair of permissions and its results to invoke respective methods.
     */
    private static HashMap<Permission, Result> resultMap;

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
     * @return modified instance of RequestPermission with debug ON/OFF.
     */
    public RequestPermission debug() {
        mLog.setDebugMode(true);
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
        mLog.i(TAG, "Requesting.........");
        resultMap = new LinkedHashMap<>(permBean.size());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && isPermissionRequired(permBean)) {

            // if result target is not set use activity as default.
            if (RequestPermission.mBase == null) {
                RequestPermission.mBase = mActivity;
            }
            mLog.i(TAG, "On permission result " + mBase.getClass().getSimpleName()
                    + " class methods will be executed.");

            PermBean bean = new PermBean();
            Map<Permission, String> map = permBean.getPermissions();
            for (Map.Entry<Permission, String> m : map.entrySet()) {
                if (mActivity.checkSelfPermission(m.getKey().toString()) != PackageManager.PERMISSION_GRANTED) {
                    bean.put(m.getKey(), m.getValue());
                    mLog.i(TAG, m.getKey().name() + " requires permission");
                } else {
                    resultMap.put(m.getKey(), Result.GRANTED);
                }
            }

            // ask permissions for granted methods.
            if (bean.size() > 0) {
                showDialog(bean);
            }
        } else {
            for (Map.Entry<Permission, String> m : permBean.getPermissions().entrySet()) {
                resultMap.put(m.getKey(), Result.GRANTED);
            }
            try {
                invokeAnnotatedMethods(resultMap);
            } catch (InvocationTargetException e) {
                mLog.e(TAG, e.getMessage(), e);
            } catch (IllegalAccessException e) {
                mLog.e(TAG, e.getMessage(), e);
            }
            mLog.i(TAG, "request: Redundant");
        }
    }

    /**
     * <p>Check weather single or multiple permissions requires grant.</p>
     * Use instead {@link RequestPermission#request()} directly.
     *
     * @param permBean {@link PermBean} wrapper class.
     * @return boolean value if any one of permission requires to be asked.
     */
    private static boolean isPermissionRequired(PermBean permBean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }
        if (permBean.size() > 0) {
            Map<Permission, String> map = permBean.getPermissions();
            for (Permission permission : map.keySet()) {
                int status = mActivity.checkSelfPermission(permission.toString());
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
                mLog.e(TAG, e.getMessage(), e);
            } catch (InvocationTargetException e) {
                mLog.e(TAG, e.getMessage(), e);
            }

            /* Send local broadcast on permissions result. */
            Intent intent = new Intent(PERMISSION_RESULT_BROADCAST);
            intent.putExtra(GRANTED, grantedArray);
            intent.putExtra(DENIED, deniedArray);
            LocalBroadcastManager.getInstance(mActivity).sendBroadcast(intent);
        }
    }

    /**
     * Invoke annotated methods in provided class.
     *
     * @param resultMap prepared Map of Permissions and respective results.
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private static void invokeAnnotatedMethods(HashMap<Permission, Result> resultMap)
            throws InvocationTargetException, IllegalAccessException {
        Method[] methods = getBase().getClass().getDeclaredMethods();

        // check all methods from provided class or by default provided activity.
        for (Method method : methods) {

            if (method != null && method.isAnnotationPresent(GrantedPermission.class)) {
                GrantedPermission granted = method.getAnnotation(GrantedPermission.class);

                if (granted != null) {

                    /* Check single value annotations */
                    if (!granted.permission().equals("") || granted.value() != Permission.NONE) {
                        for (Map.Entry<Permission, Result> permResult : resultMap.entrySet()) {

                            // invoke method if String permission is annotated.
                            if ((granted.permission().equals(permResult.getKey().toString()) ||
                                    granted.value() == permResult.getKey())
                                    && Result.GRANTED == permResult.getValue()) {
                                method.invoke(getBase());
                                mLog.i(TAG, "invoking grant method: " + method.getName());
                            }
                        }
                        continue;
                    }

                    /* Check array fields in annotations */
                    if (granted.values().length > 0) {
                        if (allValuesGranted(granted.values(), resultMap)) {
                            method.invoke(getBase());
                            mLog.i(TAG, "invoking grant method: " + method.getName());
                        }
                        continue;
                    }

                    if (granted.permissions().length > 0) {
                        if (allValuesGranted(granted.permissions(), resultMap)) {
                            method.invoke(getBase());
                            mLog.i(TAG, "invoking grant method: " + method.getName());
                        }
                    }
                }
            } else if (method != null && method.isAnnotationPresent(DeniedPermission.class)) {
                DeniedPermission denied = method.getAnnotation(DeniedPermission.class);

                if (denied != null) {

                    /* Check single value annotations */
                    if (!denied.permission().equals("") || denied.value() != Permission.NONE) {
                        for (Map.Entry<Permission, Result> permResult : resultMap.entrySet()) {

                            // invoke method if String permission is annotated.
                            if ((denied.permission().equals(permResult.getKey().toString())
                                    || denied.value() == permResult.getKey())
                                    && Result.DENIED == permResult.getValue()) {
                                method.invoke(getBase());
                                mLog.i(TAG, "invoking denied method: " + method.getName());
                            }
                        }
                        continue;
                    }

                    /* Check array fields in annotations */
                    if (denied.values().length > 0) {
                        if (anyValueDenied(denied.values(), resultMap)) {
                            method.invoke(getBase());
                            mLog.i(TAG, "invoking denied method: " + method.getName());
                        }
                        continue;
                    }

                    if (denied.permissions().length > 0) {
                        if (anyValueDenied(denied.permissions(), resultMap)) {
                            mLog.i(TAG, "invoking denied method: " + method.getName());
                            method.invoke(getBase());
                        }
                    }
                }
            } else if (method != null && method.isAnnotationPresent(OPermission.class)) {
                OPermission oPermission = method.getAnnotation(OPermission.class);
                final Annotation[][] paramAnnotations = method.getParameterAnnotations();
                final Class[] paramTypes = method.getParameterTypes();

                if (oPermission != null
                        && paramAnnotations[0][0] instanceof co.omkar.utility.opermission.annotation.Result
                        && (paramTypes[0] == boolean.class
                        || paramTypes[0] == Boolean.class)) {

                    /* Check single value annotations */
                    for (Map.Entry<Permission, Result> permResult : resultMap.entrySet()) {

                        // invoke method if String permission is annotated.
                        if (oPermission.permission().equals(permResult.getKey().toString())
                                || oPermission.value().equals(permResult.getKey())) {

                            switch (permResult.getValue()) {

                                // Permission is granted.
                                case GRANTED:
                                    method.invoke(getBase(), true);
                                    mLog.i(TAG, "invoking method: " + method.getName());
                                    break;

                                // Permission is denied.
                                case DENIED:
                                    method.invoke(getBase(), false);
                                    mLog.i(TAG, "invoking method: " + method.getName());
                                    break;
                            }
                        }
                    }

                        /* Check array fields in annotations */
                    if (oPermission.values().length > 0) {
                        if (allValuesGranted(oPermission.values(), resultMap)) {
                            method.invoke(getBase(), true);
                            mLog.i(TAG, "invoking as granted method: " + method.getName());
                        } else {
                            method.invoke(getBase(), false);
                            mLog.i(TAG, "invoking as denied method: " + method.getName());
                        }
                        continue;
                    }

                    if (oPermission.permissions().length > 0) {
                        if (allValuesGranted(oPermission.permissions(), resultMap)) {
                            method.invoke(getBase(), true);
                            mLog.i(TAG, "invoking as granted method: " + method.getName());
                        } else {
                            method.invoke(getBase(), false);
                            mLog.i(TAG, "invoking as denied method: " + method.getName());
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
    private static boolean allValuesGranted(Object[] values, HashMap<Permission, Result> resultMap) {
        if (values instanceof Permission[]) {
            Set<Permission> valueSet = new HashSet<>(Arrays.asList((Permission[]) values));
            if (resultMap.keySet().containsAll(valueSet)) {
                for (Object value : values) {
                    if (Result.GRANTED != resultMap.get((Permission) value)) {
                        mLog.i(TAG, "denied - " + value.toString());
                        return false;
                    }
                }
                return true;
            }
        } else if (values instanceof String[]) {
            Set<String> valueSet = new HashSet<>(Arrays.asList((String[]) values));
            Set<String> permission = new HashSet<>();
            for (Permission perm : resultMap.keySet()) {
                permission.add(perm.toString());
            }
            if (permission.containsAll(valueSet)) {
                for (Object value : values) {
                    if (Result.GRANTED != resultMap.get(Permission.get(String.valueOf(value)))) {
                        mLog.i(TAG, "denied - " + value);
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Check if annotated result permissions contains all values from annotated array.
     * Then it also check if any permission in annotated method value array is denied.
     *
     * @param values    ({@link Permission} or {@link String}) array.
     * @param resultMap Permission and respective result map.
     * @return false if first statement is not satisfied else true if both are satisfied.
     */
    private static boolean anyValueDenied(Object[] values, HashMap<Permission, Result> resultMap) {
        if (values instanceof Permission[]) {
            Set<Permission> valueSet = new LinkedHashSet<>(Arrays.asList((Permission[]) values));
            if (resultMap.keySet().containsAll(valueSet)) {
                for (Object value : values) {
                    if (Result.DENIED == resultMap.get((Permission) value)) {
                        mLog.i(TAG, "denied - " + value.toString());
                        return true;
                    }
                }
            }
        } else if (values instanceof String[]) {
            Set<String> valueSet = new HashSet<>(Arrays.asList((String[]) values));
            Set<String> permissionSet = new HashSet<>();
            for (Permission perm : resultMap.keySet()) {
                permissionSet.add(perm.toString());
            }
            if (permissionSet.containsAll(valueSet)) {
                for (Object value : values) {
                    if (Result.DENIED == resultMap.get(Permission.get((String) value))) {
                        mLog.i(TAG, "denied - " + value);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static Object getBase() {
        return RequestPermission.mBase;
    }

    /**
     * <p>Controller logger.</p>
     * Created on 19/08/16.
     *
     * @author Omkar Todkar.
     */
    private static final class mLog {

        private static boolean mode;

        private static boolean isMode() {
            return mode;
        }

        public static void setDebugMode(boolean mode) {
            mLog.mode = mode;
        }

        public static int v(String tag, String msg) {
            return isMode() ? Log.v(tag, msg) : -1;
        }

        public static int v(String tag, String msg, Throwable tr) {
            return isMode() ? Log.v(tag, msg, tr) : -1;
        }

        public static int d(String tag, String msg) {
            return isMode() ? Log.d(tag, msg) : -1;
        }

        public static int d(String tag, String msg, Throwable tr) {
            return isMode() ? Log.d(tag, msg, tr) : -1;
        }

        public static int i(String tag, String msg) {
            return isMode() ? Log.i(tag, msg) : -1;
        }

        public static int i(String tag, String msg, Throwable tr) {
            return isMode() ? Log.i(tag, msg, tr) : -1;
        }

        public static int w(String tag, String msg) {
            return isMode() ? Log.w(tag, msg) : -1;
        }

        public static int w(String tag, String msg, Throwable tr) {
            return isMode() ? Log.w(tag, msg, tr) : -1;
        }

        public static int w(String tag, Throwable tr) {
            return isMode() ? Log.w(tag, tr) : -1;
        }

        public static int e(String tag, String msg) {
            return isMode() ? Log.e(tag, msg) : -1;
        }

        public static int e(String tag, String msg, Throwable tr) {
            return isMode() ? Log.e(tag, msg, tr) : -1;
        }

        public static int wtf(String tag, String msg) {
            return isMode() ? Log.e(tag, msg) : -1;
        }

        public static int wtf(String tag, Throwable tr) {
            return isMode() ? Log.wtf(tag, tr) : -1;
        }

        public static int wtf(String tag, String msg, Throwable tr) {
            return isMode() ? Log.wtf(tag, msg, tr) : -1;
        }

        public static String getStackTraceString(Throwable tr) {
            return isMode() ? Log.getStackTraceString(tr) : null;
        }

        public static int println(int priority, String tag, String msg) {
            return isMode() ? Log.println(priority, tag, msg) : -1;
        }
    }
}