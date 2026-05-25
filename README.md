# LOTR Pickup Filter

Standalone Forge 1.7.10 addon for the Lord of the Rings Mod.

## What It Does

- Adds a `Filter` button to the player inventory.
- Opens a small settings GUI for your personal pickup blocklist.
- The server cancels pickup for matching item stacks.
- The filter is saved per player.

## Build

```powershell
powershell -ExecutionPolicy Bypass -File .\gradle-local.ps1 reobfJar
```

Output:

```text
build/libs/LOTR-PickupFilter-Addon-dev-local.jar
```

## Install

Install the jar on both the client and server. The inventory GUI is client-side, but pickup prevention is server-side.
