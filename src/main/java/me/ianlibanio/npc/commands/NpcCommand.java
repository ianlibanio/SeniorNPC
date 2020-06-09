package me.ianlibanio.npc.commands;

import me.ianlibanio.npc.SeniorNPC;
import me.ianlibanio.npc.initializer.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class NpcCommand implements CommandExecutor {

    private final FileConfiguration configuration = SeniorNPC.getConfiguration();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You can't use this command as the §lCONSOLE§c.");
            return true;
        }
        final Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "You must use: /npc <set, spawn> <name>.");
            return true;
        }
        if (args[0].equalsIgnoreCase("set")) {
            Location location = player.getLocation();

            configuration.set("location.x", location.getX());
            configuration.set("location.y", location.getY());
            configuration.set("location.z", location.getZ());
            configuration.set("location.yaw", location.getYaw());
            configuration.set("location.pitch", location.getPitch());
            configuration.set("location.world", location.getWorld().getName());

            SeniorNPC.getInstance().saveConfig();
            SeniorNPC.setNpcLocation(location);

            player.sendMessage(ChatColor.GREEN + "The location was set with sucess.");
        } else if (args[0].equalsIgnoreCase("spawn")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "You must use: /npc <spawn> <name>.");
                return true;
            }

            Location npcLocation = SeniorNPC.getNpcLocation();
            if (npcLocation == null) {
                player.sendMessage(ChatColor.RED + "The location value is not set, use /npc <set> to configure the location.");
                return true;
            }

            NPC npc = new NPC(args[1]);

            npc.spawn(npcLocation);
            npc.execute();

            player.sendMessage(ChatColor.GREEN + "NPC Spawned and animation is being executed.");
        } else {
            player.sendMessage(ChatColor.RED + "You must use: /npc <set, spawn> <name>.");
            return true;
        }

        return false;
    }
}
