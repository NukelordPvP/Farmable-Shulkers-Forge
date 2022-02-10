This port was permitted by the MIT License.

Logo

Farmable Shulkers (Forge)
1.17 introduced us to a new mechanic that turns shulker shells into a renewable resource. The purpose of this mod is to port this behavior to older versions (1.14.x, 1.15.x and 1.16.x).

Let the farming begin!

Why not just update to 1.17?
Personally, I have 2 reasons for this:

Just like many others, I was waiting for the Caves & Cliffs update. However, it was decided to split it into several versions (and this isn't bad. It's better to delay a release of a large-scale update, than to provide players with something unfinished). I'll wait for the final release, which should happen at the end of 2021, in order to get the full impression of the update, and not to regenerate unused chunks several times. I can live without copper, candles, moss and glow squids, but mechanics of farmable shulkers is a killer feature for me.

Servers are more difficult to update than single worlds, so it makes even more sense for them to wait for the final part of the update.

Features
Duplication of shulkers
New shulkers now have a chance to spawn when one shulker hits another shulker with a shulker bullet.

Duplication feature

Shulkers can travel to the Nether just like other entities
You might be surprised, but until the latest snapshots of 1.17, this didn't really work; shulkers preserved their coords as they moved to another dimension ("800 ~ 800 -> 800 ~ 800" instead of "800 ~ 800 -> 100 ~ 100").

![image](https://raw.githubusercontent.com/Kir-Antipov/farmable-shulkers/1.14.x/stable/media/nether.gif)

Shulkers can no longer teleport to non-square surfaces
For some reason, the game made sure that only the top of a block was a non-empty square surface, even if shulker tried to teleport to its bottom or side.

This is most easily illustrated with slabs:

![image](https://raw.githubusercontent.com/Kir-Antipov/farmable-shulkers/1.14.x/stable/media/slabs.png)

Looks cool, but how in the world can I use it to build a farm?
When something seems impossible to you, know that SciCraft members have already done it. So I recommend you to watch these videos:

Fully Automatic Shulker Shell Farm 20w45a, ilmango (initial design, exploits a bug with incorrect teleportation of shulkers to the Nether. However, the video still has value as a brief explanation of the idea itself)
Building the Reliable Shulker Farm for 1.17, cubicmetre (good to go bug-free design)
Note: cubicmetre's overworld box containing replacement shulkers is within the range of the shulkers' teleportation abilities, so in some edge cases they can teleport to its walls or roof. Just remove this box or expand it, and you're good to go!

Installation
Requirements:

Minecraft 1.16.x
Forge 1.16.5 - 36.2.23
You can download the mod from:

GitHub Releases
Modrinth
Farmable Shulkers Fabric (these builds may be unstable, but they represent the actual state of the development)
GitHub Actions (these builds may be unstable, but they represent the actual state of the development)
Github Forge Release
 

Credits: Kir_Antipov for making the mod, mahan for porting Farmable Shulkers to forge.
