package fr.constantdevs.naturalcompass.command;

import fr.constantdevs.NaturalCompass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record NaturalCompassCommand(NaturalCompass plugin) implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("Usage: /naturalcompass <reload|admin|setup|settings>", NamedTextColor.YELLOW));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("naturalcompass.reload")) {
                plugin.reload();
                sender.sendMessage(Component.text("NaturalCompass configuration reloaded.", NamedTextColor.GREEN));
            } else {
                sender.sendMessage(Component.text("You do not have permission to use this command.", NamedTextColor.RED));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("admin") || args[0].equalsIgnoreCase("setup") || args[0].equalsIgnoreCase("settings")) {
            if (sender instanceof Player player) {
                if (sender.hasPermission("naturalcompass.admin")) {
                    plugin.getGuiManager().openAdminGUI(player);
                } else {
                    sender.sendMessage(Component.text("You do not have permission to use this command.", NamedTextColor.RED));
                }
            } else {
                sender.sendMessage(Component.text("This command can only be run by a player.", NamedTextColor.RED));
            }
            return true;
        }

        return false;
    }


    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("reload", "admin", "setup", "settings");
        }
        return List.of();
    }
}