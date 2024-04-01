package cloud.lamarandom;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class LamaRandom extends JavaPlugin {

    private Random random = new Random();
    private FileConfiguration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();
        getCommand("randomdonator").setExecutor(new RandomDonatorCommand());
    }

    private class RandomDonatorCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Эта команда доступна только для игроков.");
                return true;
            }

            Player player = (Player) sender;
            List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
            onlinePlayers.remove(player);

            if (onlinePlayers.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Нет других игроков онлайн.");
                return true;
            }

            Player randomPlayer = onlinePlayers.get(random.nextInt(onlinePlayers.size()));

            String header = config.getString("messages.header");
            String footer = config.getString("messages.footer");
            String donatorMessage = config.getString("messages.donator-selected");

            String message = ChatColor.translateAlternateColorCodes('&', header) + "\n" +
                    ChatColor.translateAlternateColorCodes('&', donatorMessage
                            .replace("%player%", player.getName())
                            .replace("%donator%", randomPlayer.getName())) + "\n" +
                    ChatColor.translateAlternateColorCodes('&', footer);

            Bukkit.broadcastMessage(message);

            String giveDonateCommand = config.getString("commands.give-donate");
            giveDonateCommand = giveDonateCommand.replace("%player%", randomPlayer.getName());

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), giveDonateCommand);

            return true;
        }
    }
}
