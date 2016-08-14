package co.omkar.utility.opermission.bean;

import android.support.annotation.NonNull;

/**
 * Created by Omya on 12/08/16.
 */
// TODO: 12/08/16 documentation
public class PermBean {

    /**
     * Android Dangerous Permissions to be asked.
     *
     * @see android.Manifest.permission
     */
    private String[] permission;

    /**
     * Rationale messages to be shown on denied of at first with permissions.
     */
    private String[] rationaleMessage;

    /**
     * Rationale message must be shown from very first prompt.
     */
    private boolean showMessage;

    public PermBean(@NonNull String permission) {
        this.permission = new String[]{permission};
        this.rationaleMessage = null;
        this.showMessage = false;
    }

    // TODO: 12/08/16 distinguish between permissions.
    public PermBean(@NonNull String[] permission) {
        this.permission = permission;
        this.rationaleMessage = null;
        this.showMessage = false;
    }

    public PermBean(@NonNull Permission[] permission) {
        int count = permission.length;
        this.permission = new String[count];
        for (int k = 0; k < count; k++) {
            this.permission[k] = permission[k].toString();
        }
    }

    public PermBean(@NonNull String[] permission, String[] rationaleMessage) {
        this.permission = permission;
        this.rationaleMessage = rationaleMessage;
        this.showMessage = rationaleMessage != null;
    }

    public PermBean(@NonNull String permission, String rationaleMessage) {
        this.permission = new String[]{permission};
        this.rationaleMessage = new String[]{rationaleMessage};
        this.showMessage = rationaleMessage != null;
    }

    public PermBean(@NonNull Permission permission, String rationaleMessage) {
        this.permission = new String[]{permission.toString()};
        this.rationaleMessage = new String[]{rationaleMessage};
        this.showMessage = rationaleMessage != null;
    }

    public PermBean(@NonNull Permission[] permission, String[] rationaleMessage) {
        int count = permission.length;
        this.rationaleMessage = rationaleMessage;
        this.showMessage = rationaleMessage != null;
        this.permission = new String[count];
        for (int k = 0; k < count; k++) {
            this.permission[k] = permission[k].toString();
        }
    }

    public PermBean(@NonNull Permission[] permission, String rationaleMessage) {
        int count = permission.length;
        this.rationaleMessage = new String[]{rationaleMessage};
        this.showMessage = rationaleMessage != null;
        this.permission = new String[count];
        for (int k = 0; k < count; k++) {
            this.permission[k] = permission[k].toString();
        }
    }

    public String[] getPermission() {
        return permission;
    }

    public void setPermission(@NonNull String[] permission) {
        this.permission = permission;
    }

    public String[] getRationaleMessage() {
        return rationaleMessage;
    }

    public void setRationaleMessage(String[] rationaleMessage) {
        this.rationaleMessage = rationaleMessage;
    }

    public boolean isShowMessage() {
        return showMessage;
    }

    public void setShowMessage(boolean showMessage) {
        this.showMessage = showMessage;
    }
}
