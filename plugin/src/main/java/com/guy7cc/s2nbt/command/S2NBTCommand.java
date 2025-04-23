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
package com.guy7cc.s2nbt.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.guy7cc.s2nbt.S2NBTPlugin;
import com.guy7cc.s2nbt.StructureToNBT;
import com.guy7cc.s2nbt.util.JsonUtil;
import com.guy7cc.s2nbt.util.WorldEditUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.math.BlockPosition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FastBufferedInputStream;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;

public class S2NBTCommand {
    public static final LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("s2nbt")
            .then(Commands.literal("save")
                    .then(Commands.argument("name", StringArgumentType.string())
                            .requires(stack -> stack.getExecutor() instanceof Player)
                            .executes(ctx -> {
                                final String name = ctx.getArgument("name", String.class);
                                final Player player = (Player) ctx.getSource().getExecutor();
                                return saveByWorldEditSelection(name, player);
                            }).then(Commands.argument("start", ArgumentTypes.blockPosition())
                                    .then(Commands.argument("end", ArgumentTypes.blockPosition())
                                            .executes(ctx -> {
                                                final String name = ctx.getArgument("name", String.class);
                                                final CommandSender sender = ctx.getSource().getSender();
                                                final BlockPosition start = ctx.getArgument("start", BlockPositionResolver.class).resolve(ctx.getSource());
                                                final BlockPosition end = ctx.getArgument("end", BlockPositionResolver.class).resolve(ctx.getSource());
                                                final World world = ctx.getSource().getLocation().getWorld();
                                                return save(name, sender, toBlockPos(start), toBlockPos(end), world);
                                            }).then(Commands.argument("world", ArgumentTypes.world())
                                                    .executes(ctx -> {
                                                        final String name = ctx.getArgument("name", String.class);
                                                        final CommandSender sender = ctx.getSource().getSender();
                                                        final BlockPosition start = ctx.getArgument("start", BlockPositionResolver.class).resolve(ctx.getSource());
                                                        final BlockPosition end = ctx.getArgument("end", BlockPositionResolver.class).resolve(ctx.getSource());
                                                        final World world = ctx.getArgument("world", World.class);
                                                        return save(name, sender, toBlockPos(start), toBlockPos(end), world);
                                                    })
                                            )
                                    )
                            )
                    )
            ).then(Commands.literal("place")
                    .then(Commands.argument("name", StringArgumentType.string())
                            .executes(ctx -> {
                                final String name = ctx.getArgument("name", String.class);
                                return place(ctx.getSource().getSender(), name);
                            }).then(Commands.argument("pos", ArgumentTypes.blockPosition())
                                    .executes(ctx -> {
                                        final String name = ctx.getArgument("name", String.class);
                                        final BlockPosition pos = ctx.getArgument("pos", BlockPositionResolver.class).resolve(ctx.getSource());
                                        final World world = ctx.getSource().getLocation().getWorld();
                                        return place(ctx.getSource().getSender(), name, toBlockPos(pos), world);
                                    })
                                    .then(Commands.argument("world", ArgumentTypes.world())
                                            .executes(ctx -> {
                                                final String name = ctx.getArgument("name", String.class);
                                                final BlockPosition pos = ctx.getArgument("pos", BlockPositionResolver.class).resolve(ctx.getSource());
                                                final World world = ctx.getArgument("world", World.class);
                                                return place(ctx.getSource().getSender(), name, toBlockPos(pos), world);
                                            }).then(Commands.argument("mirror", ArgumentTypes.templateMirror())
                                                    .then(Commands.argument("rotation", ArgumentTypes.templateRotation())
                                                            .executes(ctx -> {
                                                                final String name = ctx.getArgument("name", String.class);
                                                                final BlockPosition pos = ctx.getArgument("pos", BlockPositionResolver.class).resolve(ctx.getSource());
                                                                final World world = ctx.getArgument("world", World.class);
                                                                final Mirror mirror = ctx.getArgument("mirror", Mirror.class);
                                                                final StructureRotation rotation = ctx.getArgument("rotation", StructureRotation.class);
                                                                return place(ctx.getSource().getSender(), name, toBlockPos(pos), world, mirror, rotation);
                                                            })
                                                    )
                                            )
                                    )
                            )
                    )
            );

    private static int saveByWorldEditSelection(String name, Player player){
        if(!StructureToNBT.isWorldEditAvailable()){
            player.sendMessage("§cWorldEdit not found. Please specify start and end positions.");
            return 0;
        }

        BlockPos[] selection = WorldEditUtil.getSelection(player);

        if(selection == null){
            player.sendMessage("§cPlease select a region first.");
            return 0;
        }

        return save(name, player, selection[0], selection[1], player.getWorld());
    }

    private static int save(String name, CommandSender sender, BlockPos start, BlockPos end, World world){
        BlockPos min = new BlockPos(Math.min(start.getX(), end.getX()), Math.min(start.getY(), end.getY()), Math.min(start.getZ(), end.getZ()));
        BlockPos max = new BlockPos(Math.max(start.getX(), end.getX()), Math.max(start.getY(), end.getY()), Math.max(start.getZ(), end.getZ()));
        Vec3i structureSize = new Vec3i(max.getX() - min.getX() + 1, max.getY() - min.getY() + 1, max.getZ() - min.getZ() + 1);

        ServerLevel serverLevel = ((CraftWorld) world).getHandle();

        StructureTemplate structureTemplate = new StructureTemplate();
        structureTemplate.fillFromWorld(serverLevel, min, structureSize, false, null);
        structureTemplate.setAuthor(sender.getName());

        CompoundTag compoundTag = structureTemplate.save(new CompoundTag());
        Path dir = S2NBTPlugin.getInstance().getDataPath();
        if(!Files.exists(dir) && !dir.toFile().mkdirs()){
            sender.sendMessage("§cFailed to create directory for structure files.");
            return 0;
        }
        Path nbtPath = dir.resolve(name + ".nbt");

        try {
            try (OutputStream outputStream = new FileOutputStream(nbtPath.toFile())) {
                NbtIo.writeCompressed(compoundTag, outputStream);
            }
            sender.sendMessage("§aStructure file saved: " + nbtPath);
        } catch (Throwable throwable) {
            sender.sendMessage("§cFailed to save structure file.");
            return 0;
        }

        // Create JSON file
        JsonObject json = new JsonObject();
        json.addProperty("author", sender.getName());
        json.addProperty("date", new Date().toString());
        json.addProperty("world", world.getName());
        JsonArray minArray = new JsonArray();
        minArray.add(min.getX());
        minArray.add(min.getY());
        minArray.add(min.getZ());
        json.add("pos", minArray);
        Path jsonPath = dir.resolve(name + ".json");
        if(JsonUtil.save(json, jsonPath.toFile())){
            sender.sendMessage("§aStructure meta JSON file saved: " + jsonPath);
            return Command.SINGLE_SUCCESS;
        } else {
            sender.sendMessage("§cFailed to create structure meta JSON file.");
            return 0;
        }
    }

    private static int place(CommandSender sender, String name){
        JsonElement jsonElement = JsonUtil.load(S2NBTPlugin.getInstance().getDataPath().resolve(name + ".json").toFile());
        if(jsonElement == null){
            sender.sendMessage("§cStructure meta JSON file not found: " + name + ".json");
            return 0;
        }
        try{
            JsonObject json = jsonElement.getAsJsonObject();
            JsonArray array = json.get("pos").getAsJsonArray();
            String worldName = json.get("world").getAsString();
            int x = array.get(0).getAsInt();
            int y = array.get(1).getAsInt();
            int z = array.get(2).getAsInt();
            BlockPos pos = new BlockPos(x, y, z);
            World world = Bukkit.getWorld(worldName);
            if(world == null){
                sender.sendMessage("§cWorld not found: " + json.get("world").getAsString());
                return 0;
            }
            return place(sender, name, pos, world);
        } catch (Exception e){
            sender.sendMessage("§cFailed to load structure meta JSON file: " + name + ".json");
            return 0;
        }
    }

    private static int place(CommandSender sender, String name, BlockPos pos, World world){
        return place(sender, name, pos, world, Mirror.NONE, StructureRotation.NONE);
    }

    private static int place(CommandSender sender, String name, BlockPos pos, World world, Mirror mirror, StructureRotation rotation){
        Path nbtPath = S2NBTPlugin.getInstance().getDataPath().resolve(name + ".nbt");
        if(!Files.exists(nbtPath)){
            S2NBTPlugin.getInstance().getLogger().warning("Structure file not found: " + nbtPath);
            return 0;
        }

        ServerLevel serverLevel = ((CraftWorld) world).getHandle();

        try{
            try (
                    InputStream inputStream1 = new FileInputStream(nbtPath.toFile());
                    InputStream inputStream2 = new FastBufferedInputStream(inputStream1)
            ) {
                CompoundTag nbt = NbtIo.readCompressed(inputStream2, NbtAccounter.unlimitedHeap());
                StructureTemplate structureTemplate = serverLevel.getStructureManager().readStructure(nbt);

                if(!checkLoaded(serverLevel, new ChunkPos(pos), new ChunkPos(pos.offset(structureTemplate.getSize())))){
                    sender.sendMessage("§cThe chunks where the structure will be placed are not loaded.");
                    return 0;
                }

                StructurePlaceSettings structurePlaceSettings = new StructurePlaceSettings().setMirror(toMcMirror(mirror)).setRotation(toMcRotation(rotation));
                boolean flag = structureTemplate.placeInWorld(serverLevel, pos, pos, structurePlaceSettings, StructureBlockEntity.createRandom(0), 2);
                if(flag){
                    sender.sendMessage("§aPlaced structure: " + name);
                    return Command.SINGLE_SUCCESS;
                } else {
                    S2NBTPlugin.getInstance().getLogger().warning("§cFailed to place structure: " + name);
                    return 0;
                }
            }
        } catch (Throwable throwable) {
            S2NBTPlugin.getInstance().getLogger().warning("§cFailed to load structure file: " + nbtPath);
            return 0;
        }
    }

    private static BlockPos toBlockPos(BlockPosition blockPosition) {
        return new BlockPos(blockPosition.blockX(), blockPosition.blockY(), blockPosition.blockZ());
    }

    private static net.minecraft.world.level.block.Mirror toMcMirror(Mirror bukkitMirror){
        for (var mcMirror : net.minecraft.world.level.block.Mirror.values()){
            if(Objects.equals(mcMirror.name(), bukkitMirror.name())) return mcMirror;
        }
        return null;
    }

    private static Rotation toMcRotation(StructureRotation bukkitRotation){
        for(var mcRotation : Rotation.values()){
            if(Objects.equals(mcRotation.name(), bukkitRotation.name())) return mcRotation;
        }
        return null;
    }

    private static boolean checkLoaded(ServerLevel level, ChunkPos start, ChunkPos end) {
        return ChunkPos.rangeClosed(start, end).allMatch(chunkPos -> level.isLoaded(chunkPos.getWorldPosition()));
    }
}
