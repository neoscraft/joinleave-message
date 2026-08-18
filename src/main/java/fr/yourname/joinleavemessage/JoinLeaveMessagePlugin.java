package fr.yourname.joinleavemessage;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class JoinLeaveMessagePlugin extends JavaPlugin implements Listener, CommandExecutor {

    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        registerLuckPerms();
        getServer().getPluginManager().registerEvents(this, this);
        if (getCommand("joinleavemessage") != null) {
            getCommand("joinleavemessage").setExecutor(this);
        }
        getLogger().info("JoinLeaveMessage enabled.");
    }

    private void registerLuckPerms() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
            getLogger().info("LuckPerms detected, prefix support enabled.");
            return;
        }

        luckPerms = null;
        getLogger().info("LuckPerms not detected, prefix support disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && "reload".equalsIgnoreCase(args[0])) {
            if (!sender.hasPermission("joinleavemessage.reload")) {
                sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission de recharger la configuration.");
                return true;
            }

            reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "Configuration JoinLeaveMessage rechargée.");
            return true;
        }

        sender.sendMessage(ChatColor.YELLOW + "Utilisation : /joinleavemessage reload");
        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        List<String> joinLines = getConfiguredLines("joinMessage");
        List<String> firstJoinLines = getConfiguredLines("firstJoinMessage");
        List<String> privateLines = getConfiguredLines("privateMessage");

        event.setJoinMessage(null);

        List<String> messageLines = player.hasPlayedBefore() ? joinLines : firstJoinLines.isEmpty() ? joinLines : firstJoinLines;
        if (!messageLines.isEmpty()) {
            Bukkit.broadcastMessage(buildMessage(messageLines, player));
        }

        if (!privateLines.isEmpty()) {
            player.sendMessage(buildMessage(privateLines, player));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        List<String> leaveLines = getConfiguredLines("leaveMessage");

        event.setQuitMessage(null);

        if (!leaveLines.isEmpty()) {
            Bukkit.broadcastMessage(buildMessage(leaveLines, player));
        }
    }

    List<String> getConfiguredLines(String path) {
        if (!getConfig().contains(path)) {
            return List.of();
        }

        return getConfig().getStringList(path);
    }

    private String buildMessage(List<String> lines, Player player) {
        String rawMessage = String.join("\n", lines)
                .replace("%player%", player.getName())
                .replace("%luckperms_prefix%", getLuckPermsPrefix(player))
                .replace("%luckperms_suffix%", getLuckPermsSuffix(player))
                .replace("%luckperms_group%", getLuckPermsGroup(player))
                .replace("%luckperms_group_name%", getLuckPermsGroup(player));

        return ChatColor.translateAlternateColorCodes('&', rawMessage);
    }

    private String getLuckPermsPrefix(Player player) {
        if (luckPerms == null) {
            return "";
        }

        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) {
            return "";
        }

        String prefix = user.getCachedData().getMetaData().getPrefix();
        return prefix == null ? "" : prefix;
    }

    private String getLuckPermsSuffix(Player player) {
        if (luckPerms == null) {
            return "";
        }

        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) {
            return "";
        }

        String suffix = user.getCachedData().getMetaData().getSuffix();
        return suffix == null ? "" : suffix;
    }

    private String getLuckPermsGroup(Player player) {
        if (luckPerms == null) {
            return "";
        }

        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) {
            return "";
        }

        return user.getPrimaryGroup();
    }
}
