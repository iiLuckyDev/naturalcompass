# 🌿 Natural Compass — Plugin Recreation of Nature's Compass (by Chaosyr)


[![GitHub](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/github_vector.svg)](https://github.com/gh-Constant/naturalcompass)
[![Sponsor](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/donate/ghsponsors-singular_vector.svg)](https://github.com/sponsors/gh-Constant)

**Natural Compass** is a Spigot/Paper plugin that brings **biome-searching functionality** to Minecraft servers, recreating the popular Forge/Fabric mod [Nature's Compass](https://www.curseforge.com/minecraft/mc-mods/natures-compass) by Chaosyr.

Supports **vanilla, datapack, and modded biomes** (Terralith, Incendium), **all dimensions**, and **Bedrock players via Geyser**.

![image](https://cdn.modrinth.com/data/uTWZwCXZ/images/227ebe4ff8b6e728f327f90887b3cfd5d9a50669.png)

## 🚀 Quick Installation

[![Paper](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/paper_vector.svg)](https://modrinth.com/mod/natural-compass/versions?l=paper)
[![Purpur](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/purpur_vector.svg)](https://modrinth.com/mod/natural-compass/versions?l=purpur)
[![Bukkit](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/bukkit_vector.svg)](https://modrinth.com/mod/natural-compass/versions?l=bukkit)
[![Spigot](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/spigot_vector.svg)](https://modrinth.com/mod/natural-compass/versions?l=spigot)

1. 📥 Download the `.jar`
2. 📂 Place in `plugins/`
3. 🔁 Restart server
4. 🧭 Craft/give yourself a **Natural Compass** and explore!

<br>

### ⚠️⚠️⚠️ Server Admin Note — Bedrock Resource Pack ⚠️⚠️⚠️

The Bedrock resource pack was generated using **Rainbow** (client-side Fabric mod) to create Geyser **Custom Item API v2** mappings.

* **Rainbow:** [GitHub](https://github.com/GeyserMC/Rainbow) — used to build the pack, **not a plugin dependency**
* **Requirement:** Your server must use a **Geyser fork with Custom API v2** for textures/icons to display properly

> Without this setup, Bedrock players can still use the compass — textures and visual features will not appear.

<br>

### Java Edition
[![Java Resource Pack](https://i.imgur.com/nplIbYM.png)](https://www.dropbox.com/scl/fi/c02cldac4jz0tu8mk37ld/aNaturalCompass.zip?rlkey=xa0ayc71f12mtw8oo0salthn7&st=4ji35d8o&dl=0)  
— Animated compass needle, ready for Java Edition V1.1-SNAPSHOT.

### Bedrock Edition
[![Bedrock Resource Pack](https://i.imgur.com/sq8meoi.png)](https://www.dropbox.com/scl/fi/kc04ar4la5ku04adxcwzs/NaturalCompass-bedrock-V1.00SNAPSHOT.rar?rlkey=pzi2vqxdrhxf2y914m9za0w61&st=12uy5xt6&dl=0)  
— Requires **Geyser + Custom API v2** to work on Bedrock V1.0-SNAPSHOT.

<br>

## 🧭 Main Features

### 🔹 Biome Locator

* Search **any biome**: vanilla, datapack, or modded
* Works in **Overworld, Nether, End**
* Fully compatible with **Java & Bedrock (Geyser)**

<br>

### 🎨 Compass Texture

* Original Nature's Compass texture by Chaosyr
* Single texture for all compass tiers
* **Java resource pack** included (animated needle)
* **Bedrock pack** compatible via Geyser Custom API v2

<br>


### 📂 Customizable Compass Tiers

| Tier | Default Range | Recipe                         |
| ---- | ------------- | ------------------------------ |
| I    | 1,000 blocks  | Moss Block + Diamond + Compass |
| II   | 2,500 blocks  | Heart of the Sea + Tier I      |
| III  | 5,000 blocks  | Amethyst Cluster + Tier II     |
| IV   | 10,000 blocks | Echo Shard + Tier III          |
| V    | 25,000 blocks | Nether Star + Tier IV          |

> Recipes fully configurable in `config.yml`.

<br>

### 🖥 In-Game Interface & Visuals

* **Biome Selection GUI** by provider
* **Search messages**: "searching" & "biome found"
* **Visual Indicators**: Compass points, ActionBar/BossBar, optional coordinates
* **Biome & Provider Icons**: Grass Block, Snow Block, custom heads

<br>

### 🖌️ Customizing Biome & Provider Icons

#### Biome Icons (`biomes.yml`)

```yaml
minecraft:sunflower_plains:
  icon: SUNFLOWER
  dimension: overworld
```

* Replace `icon` with any valid Minecraft item/block ID
* Add new biomes by copying an entry and updating ID, name, icon, dimension

#### Provider Icons (`providers.yml`)

```yaml
terralith:
  icon: PLAYER_HEAD
  texture: eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGVhNjExOGI4ZjVlMTI5MjE0Mzc5MTI4Yzc4MmNmYWE0ZDM0ZTVjZmE3YjkyZmIwM2JiNjk2ZjRmZGJhMDBlZSJ9fX0=
```

* Replace Base64 string to change icon
* Add new providers with `display_name` + `icon`
* Reload plugin: `/naturalcompass reload`

<br>

### 🔐 Permissions

| Permission              | Description                         | Default |
| ----------------------- | ----------------------------------- | ------- |
| `naturalcompass.use`    | Use the compass                     | ✅ true  |
| `naturalcompass.craft`  | Craft/upgrade compasses             | ✅ true  |
| `naturalcompass.admin`  | Admin commands (reload, GUI access) | ❌ op    |
| `naturalcompass.reload` | Reload config                       | ❌ op    |

<br>

### 📌 Commands

* `/naturalcompass reload` — Reload configuration *(admin)*
* `/naturalcompass admin` — Open admin GUI *(admin)*
* Aliases: `nc`
