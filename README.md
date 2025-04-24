# StructureToNBT

[[English](https://github.com/guy7cc/StructureToNBT/README.md)] [[日本語](https://github.com/guy7cc/StructureToNBT/README_ja.md)]

**StructureToNBT** is a Minecraft plugin for Paper servers that enables you to save a selected region in NBT (.nbt) format and later place it back into the world.

## Features

- **WorldEdit Integration (Optional)**
    - Automatically uses your WorldEdit selection if WorldEdit is installed.
- **Manual Selection**
    - Specify any start and end coordinates via command arguments.
- **Save Structures**
    - Saves the selected region as a `.nbt` file in the plugin’s data folder.
    - Outputs metadata in JSON, including region bounds and author name.
- **Place Structures**
    - Places saved `.nbt` files at a specified location.
    - Supports mirror and rotate options.

## Requirements

- Paper 1.21.4 (other versions may be supported in the future)
- Java 21
- WorldEdit: Optional (requires `worldedit-bukkit` 7.3.10+ for integration)

## Installation

1. Clone this repository or download the ZIP.
2. In the project directory, run the Gradle build:
   ```bash
   ./gradlew clean build
   ```
3. Place the generated `StructureToNBT.jar` in your server’s `plugins/` folder.
4. Reload the plugin.

## Commands

### Save Command: `/s2nbt save`

- **Use WorldEdit Selection** (if WorldEdit is enabled)
  ```
  /s2nbt save <name>
  ```
- **Manual Selection**
  ```
  /s2nbt save <name> <start> <end> [world]
  ```
    - Coordinates are block positions (e.g., `100 64 -200`).
    - If `world` is omitted, uses the executor’s current world.

> On success, outputs `/plugins/StructureToNBT/<name>.nbt` and `<name>.json`.

### Place Command: `/s2nbt place`

- **Use JSON-Saved Origin** (omit position)
  ```
  /s2nbt place <name>
  ```
- **Specify Position**
  ```
  /s2nbt place <name> <position> [world] [mirror] [rotate]
  ```
    - Mirror options: `NONE`, `LEFT_RIGHT`, `FRONT_BACK`
    - Rotate options: `NONE`, `CLOCKWISE_90`, `CLOCKWISE_180`, `COUNTERCLOCKWISE_90`

> On success, the structure is placed at the specified location.

## Save Directory

In the plugin’s data folder (default `plugins/StructureToNBT/`), the following files are created:

- `<name>.nbt` – structure data
- `<name>.json` – metadata (author, date, origin position, world name)

## License

This project is licensed under the GNU GPLv3 License. See the `LICENSE` file for details.

## Support & Contribution

- Report issues or feature requests on GitHub Issues.

## Notes

This plugin provides instructions for converting between structures and `.nbt` files in code. Feel free to reference the code included in this project.

