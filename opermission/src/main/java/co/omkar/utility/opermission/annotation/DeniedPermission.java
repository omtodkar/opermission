package co.omkar.utility.opermission.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import co.omkar.utility.opermission.bean.Permission;

/**
 * Created by Omya on 12/08/16.
 * <p>
 * Use this annotation on method to react on denial
 * of any permission you request.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DeniedPermission {

    /**
     * {@link android.Manifest.permission} or {@link android.Manifest.permission_group}
     * which is requested through RequestPermission.
     */
    String permission() default "";

    /**
     * Array of {@link android.Manifest.permission} or {@link android.Manifest.permission_group}
     * which is requested through RequestPermission.
     * <p>
     * Use it if any method depend on two or more permissions denial.
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
