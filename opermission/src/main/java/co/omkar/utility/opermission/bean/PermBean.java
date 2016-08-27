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

package co.omkar.utility.opermission.bean;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>A wrapper class for Permission and its rational message.</p>
 * Created on 12/08/16.
 *
 * @author Omkar Todkar.
 * @see Permission
 * @see co.omkar.utility.opermission.RequestPermission
 * @see android.Manifest.permission
 */
public class PermBean {

    /**
     * Permission Map with corresponding message.
     */
    private Map<Permission, String> mPermissions;

    /**
     * Create a permission bean for single permission without message to show.
     * Note: Only default message will be shown on launch.
     *
     * @param permission {@link android.Manifest.permission} to be asked.
     */
    public PermBean(@NonNull String permission) {
        this.mPermissions = new HashMap<>(1);
        mPermissions.put(Permission.get(permission), null);
    }

    /**
     * Set a permission and messages map.
     *
     * @param mPermissions A prepared Map&lt;Permission, String&gt; to be asked.
     */
    public PermBean(Map<Permission, String> mPermissions) {
        this.mPermissions = mPermissions;
    }

    /**
     * Initialize empty permission map and add permission later.
     */
    public PermBean() {
        mPermissions = new HashMap<>();
    }

    /**
     * Add permission and message one by one.
     *
     * @param permission {@link Permission} to be asked.
     * @param message    String rationale message.
     */
    public void put(Permission permission, String message) {
        if (permission == null) throw new IllegalArgumentException("Permission can't be null");
        mPermissions.put(permission, message);
    }

    /**
     * Permission to be removed from map.
     *
     * @param permission {@link Permission} to be removed.
     */
    public void remove(Permission permission) {
        if (permission == null) {
            throw new IllegalArgumentException("Cannot remove null permission out of bean.");
        }
        mPermissions.remove(permission);
    }

    /**
     * Size of bean containing map.
     *
     * @return size of map in {@link Integer} value.
     */
    public int size() {
        return mPermissions != null ? mPermissions.size() : 0;
    }

    /**
     * Get A prepared Map&lt;Permission, String&gt; to be asked.
     *
     * @return Map&lt;Permission, String&gt;
     */
    public Map<Permission, String> getPermissions() {
        return mPermissions;
    }
}
