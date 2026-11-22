# Steam Controller Trackpad Integration — Minecraft Fabric 26.3

**Project status: design specification; implementation pending.** This replaces the abandoned Legacy4J crafting-screen prototype.

## Target
New-generation Steam Controller with two trackpads. Fabric client-side Minecraft 26.3. No new blocks, items, mechanics, or server requirement.

## Default control layout

| Context | Left trackpad | Right trackpad |
| --- | --- | --- |
| Gameplay (no GUI) | Radial hotbar selector (slots 1–9) | Radial action wheel |
| Inventory, crafting, containers, settings and other GUI screens | Scroll wheel | Mouse cursor / mousepad |

### Gameplay
- Left: touch to reveal nine hotbar sectors, slide to highlight, release to select; configurable deadzone and activation behavior.
- Right: touch to reveal action sectors (inventory, perspective, drop, swap offhand, screenshot, etc.); user-editable assignments.
- No unintended activation from resting a thumb; configurable press/release behavior and haptics where hardware APIs permit.

### Menus
- Left: circular or vertical scroll gestures (user-selectable), adjustable sensitivity.
- Right: cursor movement; trackpad press for left click, configurable right-click and drag behavior.
- Automatic context switching based on whether Minecraft has an active screen.

## Architecture decision (not yet implemented)

**Steam Input is the hardware input layer.** It is responsible for trackpad touch coordinates, clicks, gestures, and device-specific configuration. A Fabric mod handles Minecraft GUI detection, optional radial overlays, and gameplay actions.

**Important limitation:** a Fabric mod alone cannot reliably read all Steam Controller trackpad touch coordinates or dynamically change Steam Input action sets without an appropriate Steam Input/Steamworks integration. Steam Input configuration or a native bridge may be required. We will validate that integration before claiming automatic context switching works.

### Implementation milestones
1. Validate the new Steam Controller's Steam Input API and whether Minecraft launched through Steam can use action sets or action layers.
2. Choose an actual transport for trackpad data and GUI-context signaling (Steam Input action sets, virtual keyboard/mouse events, or a local bridge).
3. Implement context detection and test it against inventory, crafting table, chat, pause, and modded menus.
4. Implement radial hotbar and action wheels, with touch/release semantics.
5. Add remapping and sensitivity settings; test on actual controller hardware.
6. Compile and publish a tested Fabric 26.3 JAR and documented Steam Input layout.

## Repo note
This repository's name is still `crafting-lce-mod`. Rename it to `steam-controller-minecraft` in GitHub Settings if desired. Existing Gradle scaffolding is temporary and not a finished trackpad mod.

## Attribution
Independent community project; not affiliated with Valve, Mojang, or Fabric.
