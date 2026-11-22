# Legacy Classic Crafting — Fabric 26.3

**Experimental source prototype — not yet verified to compile or work in-game.**

This is an independent, client-side Fabric mod intended to provide a lightweight console-style crafting recipe browser at the vanilla crafting table, without installing Legacy4J or FactoryAPI.

## Current prototype
- Four recipe categories: Building, Redstone, Equipment, Misc
- Craftable-only filter, pagination, and recipe placement via the vanilla client
- No added blocks, items, or game mechanics
- No server-side dependency

## Important limitations
This is **not yet** the authentic Classic Legacy4J crafting screen. It currently uses a simple recipe-name button list, not the original icon-based legacy console layout. Controller navigation and inventory crafting are not implemented. Compilation and runtime testing are still required.

## Build
Requires JDK 25 and Gradle 9.8.2. Run `gradle build`. GitHub Actions attempts a build on every push and uploads a JAR only if successful.

This project is inspired by Legacy4J by Wilyicaro, but is not affiliated with or endorsed by its maintainers. Any reused upstream source must retain its license and attribution.
