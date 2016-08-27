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

package co.omkar.utility.opermission.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import co.omkar.utility.opermission.bean.Permission;

/**
 * Created by Omya on 12/08/16.
 * <p>
 * Use this annotation on method where you can
 * write a logic on permission granted.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GrantedPermission {

    /**
     * {@link android.Manifest.permission} or {@link android.Manifest.permission_group}
     * which is requested through RequestPermission. Default value empty {@link String}.
     */
    String permission() default "";

    /**
     * Array of {@link android.Manifest.permission} or {@link android.Manifest.permission_group}
     * which is requested through RequestPermission. Default NULL.
     * <p>
     * Use it if some method depend on two or more permissions grant.
     */
    String[] permissions() default {};

    /**
     * {@link Permission} value.
     * default NULL
     */
    Permission value() default Permission.NULL;

    /**
     * Array of {@link Permission}.
     * Default NULL
     */
    Permission[] values() default {};
}
