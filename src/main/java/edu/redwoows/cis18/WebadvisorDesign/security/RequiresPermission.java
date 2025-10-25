package edu.redwoows.cis18.WebadvisorDesign.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {
    String value(); // The permission name required
    boolean allowAll() default true; // Whether 'ALL' permission grants access
}