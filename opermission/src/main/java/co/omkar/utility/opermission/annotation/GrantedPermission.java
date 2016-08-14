package co.omkar.utility.opermission.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import co.omkar.utility.opermission.bean.Permission;

/**
 * Created by Omya on 12/08/16.
 * <p/>
 * Use this annotation on method where you can
 * write a logic on permission granted.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GrantedPermission {

    /**
     * {@link android.Manifest.permission} or {@link android.Manifest.permission_group}
     * which is requested through RequestPermission.
     */
    String permission() default "";

    /**
     * Array of {@link android.Manifest.permission} or {@link android.Manifest.permission_group}
     * which is requested through RequestPermission.
     * <p/>
     * Use it if some method depend on two or more permissions grant.
     */
    String[] permissions() default {};

    /**
     * {@link Permission} value.
     */
    Permission value() default Permission.NULL;

    /**
     * Array of {@link Permission}.
     */
    Permission[] values() default {};
}
