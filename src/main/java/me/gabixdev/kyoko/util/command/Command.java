package me.gabixdev.kyoko.util.command;

import net.dv8tion.jda.core.entities.Message;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Command {
    private final String[] name;
    private String description;
    private int min;
    private String usage;
    private final String[] flags;
    private final Method method;
    private final Object classObject;

    public Command(String[] name, String description, int min, String usage, String[] flags,  Method method, Object classObject) {
        this.name = name;
        this.description = description;
        this.min = min;
        this.usage = usage;
        this.flags = flags;
        this.method = method;
        this.classObject = classObject;
    }

    public String getCommand() {
        return this.getName()[0];
    }

    public String[] getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public int getMin() {
        return this.min;
    }

    public String getUsage() {
        return ("!" + this.getCommand() + " " + this.usage).trim();
    }

    public String[] getFlags() {
        return this.flags;
    }

    public Method getMethod() {
        return this.method;
    }

    public Object getClassObject() {
        return this.classObject;
    }

    public void handleCommand(Message message, CommandContext context) throws Throwable {
        if (this.getMethod() == null) {
            return;
        }

        try {
            this.getMethod().setAccessible(true);
            this.getMethod().invoke(this.getClassObject(), message, context);
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }

    public boolean hasFlag(String flag) {
        for (String f : this.flags) {
            if (f.equals(flag)) {
                return true;
            }
        }
        return false;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }
}