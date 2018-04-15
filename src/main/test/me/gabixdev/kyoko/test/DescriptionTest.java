package me.gabixdev.kyoko.test;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.Settings;
import me.gabixdev.kyoko.i18n.Language;

public class DescriptionTest {
    public static void main(String... args) {
        Settings s = new Settings() {
            @Override
            public boolean isWipFeaturesEnabled() {
                return true;
            }

            @Override
            public String getWeebshApiKey() {
                return "test";
            }

            @Override
            public String getSaucenaoApiKey() {
                return "test";
            }
        };

        Kyoko kyoko = new Kyoko(s) {
            @Override
            public void start() {
                registerCommands();
                getCommandManager().getCommands().forEach(command -> {
                    if (getI18n().get(Language.ENGLISH, command.getDescription()).equals(command.getDescription())) {
                        System.out.println("Missing translation: " + command.getDescription());
                    }
                });
            }
        };

        try {
            kyoko.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
