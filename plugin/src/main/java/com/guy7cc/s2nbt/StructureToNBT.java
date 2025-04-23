/*
 * Copyright (C) 2025 guy7cc
 *
 * This file is part of StructureToNBT.
 *
 * StructureToNBT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * StructureToNBT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with StructureToNBT. If not, see <https://www.gnu.org/licenses/>.
 */
package com.guy7cc.s2nbt;

import com.guy7cc.s2nbt.command.S2NBTCommand;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class StructureToNBT {
    public static final String NAMESPACE = "s2nbt";
    private static boolean worldEditAvailable = false;

    public static void init(S2NBTPlugin plugin){
        Plugin worldEdit = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

        if (worldEdit != null && worldEdit.isEnabled()) {
            plugin.getLogger().info("WorldEdit detected. Enabling integration features.");
            worldEditAvailable = true;
        } else {
            plugin.getLogger().warning("WorldEdit not found. Integration features will be disabled.");
        }

        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(S2NBTCommand.builder.build());
        });
    }

    public static boolean isWorldEditAvailable() {
        return worldEditAvailable;
    }
}
