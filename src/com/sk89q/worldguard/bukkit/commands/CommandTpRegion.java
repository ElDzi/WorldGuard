/**
 * 
 */
package com.sk89q.worldguard.bukkit.commands;

import com.sk89q.worldguard.bukkit.WorldGuardConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.commands.CommandHandler.CommandHandlingException;
import com.sk89q.worldguard.protection.regionmanager.RegionManager;
import com.sk89q.worldguard.protection.regions.AreaFlags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * @author wallnuss
 *
 */
public class CommandTpRegion extends WgCommand {

    public boolean handle(CommandSender sender, String senderName, String command, String[] args, WorldGuardConfiguration cfg) throws CommandHandlingException {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players may use this command");
            return true;
        }

        Player player = (Player) sender;
        cfg.checkPermission(sender, "tpregion");

        CommandHandler.checkArgs(args, 1, 2, "/tpregion <region name> {spawn}");

        String id = args[0];
        Boolean spawn = false;
        if (args.length == 2 && args[1].equals("spawn")) {
            cfg.checkPermission(player, "tpregion.spawn");
            spawn = true;
        }
        RegionManager mgr = cfg.getWorldGuardPlugin().getGlobalRegionManager().getRegionManager(player.getWorld().getName());
        ProtectedRegion region = mgr.getRegion(id);
        if (region != null) {
            AreaFlags flags = region.getFlags();
            Double x, y, z;
            World world;
            if (spawn) {
                x = flags.getDoubleFlag("spawn", "x");
                y = flags.getDoubleFlag("spawn", "y");
                z = flags.getDoubleFlag("spawn", "z");
                world = cfg.getWorldGuardPlugin().getServer().getWorld(flags.getFlag("teleport", "world"));
            } else {
                x = flags.getDoubleFlag("teleport", "x");
                y = flags.getDoubleFlag("teleport", "y");
                z = flags.getDoubleFlag("teleport", "z");
                world = cfg.getWorldGuardPlugin().getServer().getWorld(flags.getFlag("teleport", "world"));
            }
            if (x != null && y != null && z != null && world != null) {
                Location location = new Location(world, x, y, z);
                player.teleportTo(location);
                return true;
            } else {
                player.sendMessage(ChatColor.RED + "Region: " + id + " has no teleport/spawn location assign.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Region: " + id + " not defined");
        }

        return false;
    }
}