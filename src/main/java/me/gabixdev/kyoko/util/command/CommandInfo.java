package me.gabixdev.kyoko.util.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandInfo {
    String[] name();

    String description() default "No description provided!";

    String usage() default "No usage provided!";

    int min() default 0;

    String[] flags() default "";
}
