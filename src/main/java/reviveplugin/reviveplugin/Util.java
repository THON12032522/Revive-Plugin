package reviveplugin.reviveplugin;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {

    private static Map<Player, String> localeSettings = new HashMap<>();
    private static Map<String, Map<String, String>> messages = new HashMap<>();
    public static List<String> files = new ArrayList<>();

    public static String getMessage(String locale, String messName) {
        return messages.getOrDefault(locale, messages.get("en, de")).getOrDefault(messName, "Message " + messName + " not set!");
    }

    public static String getLocale(Player p) {
        return localeSettings.get(p);
    }

    public static void setLocale(Player p, String string) {
        localeSettings.remove(p);
        if (!files.contains(string)) {
            p.sendMessage(Util.getMessage(Util.getLocale(p), "LangNoExist"));
        } else {
            localeSettings.put(p, string);
            p.sendMessage(Util.getMessage(Util.getLocale(p), "LangSet"));
            Main.getInstance().getConfig().set(p.getUniqueId().toString(), string);
            Main.getInstance().saveConfig();
        }
    }

    public static void removePlayer(Player p) {
        localeSettings.remove(p);
    }

    public static void loadMessages() {
        File langFolder = new File(Main.getInstance().getDataFolder() + "/langs");
        if (!langFolder.exists()) {
            langFolder.mkdir();
        }
        File enFile = new File(langFolder, "en.yml");
        try {
            if (!enFile.exists()) {
                InputStream in = Main.getInstance().getResource("en.yml");
                Files.copy(in, enFile.toPath());
            }
            File deFile = new File(langFolder, "de.yml");
            try {
                if (!deFile.exists()) {
                    InputStream in = Main.getInstance().getResource("de.yml");
                    Files.copy(in, deFile.toPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (File file : langFolder.listFiles()) {
                Map<String, String> localeMessages = new HashMap<>();

                FileConfiguration lang = YamlConfiguration.loadConfiguration(file);
                for (String key : lang.getKeys(false)) {
                    for (String messName : lang.getConfigurationSection(key).getKeys(false)) {
                        String message = ChatColor.translateAlternateColorCodes('&', lang.getString(key + "." + messName));
                        localeMessages.put(messName, message);
                    }
                }
                String fileName = file.getName().split(".yml")[0];
                messages.put(fileName, localeMessages);
                files.add(fileName);
                System.out.println(file.getName() + " loaded");
                }
            } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
