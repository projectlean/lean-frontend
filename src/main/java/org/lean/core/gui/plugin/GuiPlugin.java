package org.lean.core.gui.plugin;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GuiPlugin {
    String id() default "";
    String description() default "";
}
