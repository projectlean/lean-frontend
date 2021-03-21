package org.lean.ui.plugins.file;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LeanFileTypePlugin {

    String id();

    String name() default "";

    String description() default "";

    String image() default "";
}
