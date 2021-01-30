package org.lean.ui.plugins.perspective;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LeanPerspectivePlugin {

    String id();
    String name() default "";
    String description() default "";
    String image() default "";
    String route() default "";

}
