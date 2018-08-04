package moe.kyokobot.bot.module;

import lombok.Data;

import java.util.List;

@Data
public class ModuleDescription {
    private String main;
    private String name;
    private String description;
    private String version;
    private List<String> authors = null;
    private List<String> dependencies = null;
}
