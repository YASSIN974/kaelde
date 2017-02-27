package pl.themolka.commons.command;

import net.dv8tion.jda.core.entities.Message;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public abstract class Commands {
    private final Map<String, Command> commandMap = new HashMap<>();

    public Command getCommand(String command) {
        return this.commandMap.get(command.toLowerCase());
    }

    public Set<String> getCommandNames() {
        return this.commandMap.keySet();
    }

    public List<Command> getCommands() {
        List<Command> commands = new ArrayList<>();

        for (Command command : this.commandMap.values()) {
            if (!commands.contains(command)) {
                commands.add(command);
            }
        }

        return commands;
    }

    public abstract void handleCommand(Message message, CommandContext context);

    public void handleCommand(Message sender, Command command, String label, String[] args) {
        this.handleCommand(sender, command, label, args, new CommandContextParser());
    }

    public void handleCommand(Message sender, Command command, String label, String[] args, CommandContext.IContextParser parser) {
        this.handleCommand(sender, parser.parse(command, label, args));
    }

    public void registerCommand(Command command) {
        for (String name : command.getName()) {
            this.commandMap.put(name, command);
        }
    }

    public void registerCommandClass(Class clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            method.setAccessible(true);

            Annotation annotation = method.getDeclaredAnnotation(CommandInfo.class);
            if (annotation != null) {
                this.registerCommandMethod(method, null, (CommandInfo) annotation);
            }
        }
    }

    public void registerCommandClasses(Class... classes) {
        for (Class clazz : classes) {
            this.registerCommandClass(clazz);
        }
    }

    public void registerCommandObject(Object object) {
        for (Method method : object.getClass().getDeclaredMethods()) {
            method.setAccessible(true);

            Annotation annotation = method.getDeclaredAnnotation(CommandInfo.class);
            if (annotation != null) {
                this.registerCommandMethod(method, object, (CommandInfo) annotation);
            }
        }
    }

    public void registerCommandObjects(Object... objects) {
        for (Object object : objects) {
            this.registerCommandObject(object);
        }
    }

    public void registerCommandMethod(Method method, Object object, CommandInfo info) {
        this.registerCommand(new Command(info.name(), info.description(), info.min(), info.usage(), info.flags(), method, object));
    }
}
