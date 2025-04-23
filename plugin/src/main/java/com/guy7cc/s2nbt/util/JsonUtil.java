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

import com.google.gson.*;
import com.guy7cc.s2nbt.S2NBTPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.logging.Level;

public class JsonUtil {
    private static final Gson gson = new GsonBuilder().create();

    public static boolean save(@NotNull JsonElement element, @NotNull File file) {
        try {
            file.getParentFile().mkdirs();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            writer.write(gson.toJson(element));
            writer.close();
            return true;
        } catch (IOException exception) {
            S2NBTPlugin.getInstance().getLogger().log(
                    Level.SEVERE,
                    String.format("Could not save %s due to I/O errors. ", file),
                    exception
            );
            return false;
        } catch (SecurityException exception) {
            S2NBTPlugin.getInstance().getLogger().log(
                    Level.SEVERE,
                    String.format("Could not save %s due to security problems. ", file),
                    exception
            );
            return false;
        }
    }

    /**
     * Loads a JsonElement from a file.
     *
     * @param file the file to load from
     * @return the loaded JsonElement, or null if the file could not be read
     */
    @Nullable
    public static JsonElement load(@NotNull File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return JsonParser.parseString(sb.toString());
        } catch (FileNotFoundException exception) {
            return null;
        } catch (IOException exception) {
            S2NBTPlugin.getInstance().getLogger().log(
                    Level.SEVERE,
                    String.format("Could not read %s due to I/O errors.", file),
                    exception
            );
        } catch (JsonParseException exception) {
            S2NBTPlugin.getInstance().getLogger().log(
                    Level.SEVERE,
                    String.format("Could not read %s because the file format was invalid.", file),
                    exception
            );
        } catch (ClassCastException exception) {
            S2NBTPlugin.getInstance().getLogger().log(
                    Level.SEVERE,
                    String.format("Could not read %s because the element was not JsonObject", file),
                    exception
            );
        }
        return null;
    }
}
