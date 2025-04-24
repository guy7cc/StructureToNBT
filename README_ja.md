# StructureToNBT

[[English](https://github.com/guy7cc/StructureToNBT/blob/main/README.md)] [[日本語](https://github.com/guy7cc/StructureToNBT/blob/main/README_ja.md)]

**StructureToNBT** は、Paper サーバー上で動作する Minecraft プラグインです。選択した範囲を NBT（.nbt） 形式で保存し、あとからワールド内に配置する機能を提供します。

## 主な機能

- **WorldEdit 連携**（オプション）
    - WorldEdit が導入されている場合、選択範囲を自動取得して保存できます。
- **手動範囲指定**
    - ワールド内の任意の開始位置・終了位置をコマンド引数で指定して保存。
- **構造の保存**
    - 選択範囲を `.nbt` ファイルとしてプラグインのデータフォルダに保存。
    - 範囲情報や作者名を含むメタデータを JSON 形式で同時出力。
- **構造の配置**
    - 保存した `.nbt` ファイルを指定位置に再配置。
    - ミラー・回転オプション対応。

## 要件

- Paper 1.21.4 (他のバージョンにも対応する予定です)
- Java: 21
- WorldEdit: 任意（連携機能を使用する場合は `worldedit-bukkit` 7.3.10 以上）

## インストール方法

1. リポジトリをクローンまたは ZIP をダウンロード。
2. プロジェクトディレクトリで Gradle ビルドを実行:
   ```bash
   ./gradlew clean build
   ```
3. 生成された `StructureToNBT.jar` をサーバーの `plugins/` フォルダに配置。
4. プラグインをリロード。

## コマンド一覧

### 保存コマンド: `/s2nbt save`

- **WorldEdit 選択範囲を利用** （WorldEdit が有効）
  ```
  /s2nbt save <名前>
  ```
- **手動範囲指定**
  ```
  /s2nbt save <名前> <開始位置> <終了位置> [ワールド]
  ```
    - 位置指定はブロック座標（例: `100 64 -200`）。
    - ワールドを省略すると実行者のいるワールド。

> 成功時に `/plugins/StructureToNBT/<名前>.nbt` と `<名前>.json` が出力されます。

### 配置コマンド: `/s2nbt place`

- **位置省略（JSON に保存された起点）**
  ```
  /s2nbt place <名前>
  ```
- **位置指定**
  ```
  /s2nbt place <名前> <配置位置> [ワールド] [ミラー] [回転]
  ```
    - ミラー: `NONE`, `LEFT_RIGHT`, `FRONT_BACK`
    - 回転: `NONE`, `CLOCKWISE_90`, `CLOCKWISE_180`, `COUNTERCLOCKWISE_90`

> 成功時に指定位置に構造が再現されます。

## 保存先ディレクトリ

プラグインデータフォルダ（デフォルトでは `plugins/StructureToNBT/`）に以下のファイルが作成されます:

- `<名前>.nbt` - 構造本体データ
- `<名前>.json` - メタデータ（作者・日付・初期位置・ワールド名）

## ライセンス

This project is licensed under the GNU GPLv3 License. 詳細は `LICENSE` ファイルを参照してください。

## サポート・貢献

- 不具合報告や機能要望は GitHub Issues へ。

## 備考

このプラグインは構造物と`.nbt`ファイルをコード上で相互に変換する方法のインストラクションです。このプロジェクトに含まれるコードを参考にすることは大歓迎です。

