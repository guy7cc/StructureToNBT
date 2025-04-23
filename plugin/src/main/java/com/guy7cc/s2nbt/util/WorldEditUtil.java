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
package com.guy7cc.s2nbt.util;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import net.minecraft.core.BlockPos;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class WorldEditUtil {
    @Nullable
    public static BlockPos[] getSelection(Player player){
        com.sk89q.worldedit.entity.Player actor = BukkitAdapter.adapt(player);
        LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(actor);
        try {
            Region region = localSession.getSelection();
            BlockVector3 min = region.getMinimumPoint();
            BlockVector3 max = region.getMaximumPoint();
            return new BlockPos[]{
                    new BlockPos(min.x(), min.y(), min.z()),
                    new BlockPos(max.x(), max.y(), max.z())
            };
        } catch (IncompleteRegionException e) {
            return null;
        }
    }
}
