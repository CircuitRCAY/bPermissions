package de.bananaco.bpermissions.imp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import de.bananaco.bpermissions.api.User;
import de.bananaco.bpermissions.api.World;
import de.bananaco.bpermissions.api.WorldManager;
import de.bananaco.permissions.interfaces.PromotionTrack;

public class MultiGroupPromotion extends BasePromotionTrack {

    @Override
    public void promote(String player, String track, String world) {
        List<String> groups = trackmap.get(track.toLowerCase());
        if (world == null) {
            for (World w : wm.getAllWorlds()) {
                User user = w.getUser(player);
                boolean promoted = false;
                // If they don't have the group, set it to their group
                for (int i = 0; i < groups.size() && !promoted; i++) {
                    if (!user.getGroupsAsString().contains(groups.get(i))) {
                        // Add the new group
                        user.addGroup(groups.get(i));
                        // We've promoted successfully
                        promoted = true;
                        w.save();
                    }
                }
            }
        } else {
            User user = wm.getWorld(world).getUser(player);
            boolean promoted = false;
            // If they don't have the group, set it to their group
            for (int i = 0; i < groups.size() && !promoted; i++) {
                if (!user.getGroupsAsString().contains(groups.get(i))) {
                    // Add the new group
                    user.addGroup(groups.get(i));
                    // We've promoted successfully
                    promoted = true;
                    wm.getWorld(world).save();
                }
            }
        }
    }

    @Override
    public void demote(String player, String track, String world) {
        List<String> groups = trackmap.get(track.toLowerCase());
        if (world == null) {
            for (World w : wm.getAllWorlds()) {
                User user = w.getUser(player);
                boolean demoted = false;
                // If they don't have the group, set it to their group
                for (int i = groups.size() - 1; i > 0 && !demoted; i--) {
                    if (user.getGroupsAsString().contains(groups.get(i))) {
                        // Remove the old group
                        user.removeGroup(groups.get(i));
                        // We've demoted successfully
                        demoted = true;
                        w.save();
                    }
                }
            }
        } else {
            User user = wm.getWorld(world).getUser(player);
            boolean demoted = false;
            // If they don't have the group, set it to their group
            for (int i = groups.size() - 1; i > 0 && !demoted; i--) {
                if (user.getGroupsAsString().contains(groups.get(i))) {
                    // Remove the old group
                    user.removeGroup(groups.get(i));
                    // We've demoted successfully
                    demoted = true;
                    wm.getWorld(world).save();
                }
            }
        }
    }

}
