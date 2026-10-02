KITTY PAY TRACKER  -  Fabric mod + built-in OBS overlay (pink Hello Kitty theme)
Only the mod is needed. No Python, no extra programs.

HOW IT WORKS
The mod reads the chat your own client receives ("Name paid you $17M" / "You paid Name $200k"),
and runs a tiny web server inside Minecraft (localhost only). OBS shows that page as a Browser Source.
It sends nothing to the server and uses no bot account.

FOR THE STREAMER (once the .jar exists)
1. Install Fabric Loader + Fabric API for Minecraft 1.21.11 (default in this project).
2. Put kittypay-1.0.0.jar in .minecraft/mods and start the game.
3. In OBS:  Sources + > Browser
       URL:    http://localhost:8765/overlay
       Width:  470     Height: 600
   Background is transparent. Open http://localhost:8765/ in a browser for a preview + "Reset session" button.
   Overlay URL options:  ?rows=10   ?scale=0.8   ?summary=0
   (Overlay shows this game session. Every payment is also saved to
    .minecraft/config/kittypay/payments_history.tsv)

GETTING THE .JAR  (a mod has to be compiled once)
Option A - no installs, uses GitHub's free build servers
  1. Make a free account at github.com, click New repository (any name), then "uploading an existing file".
  2. Drag in EVERYTHING from this folder, including the hidden .github folder, and commit.
  3. Open the Actions tab > "build-jar" > Run workflow. Wait ~3 minutes.
  4. Open the finished run, download the "kittypay-jar" artifact, unzip it. That is the mod.
Option B - on your own PC
  Install JDK 21 and Gradle 8.14+, then in this folder run:  gradle build
  The jar appears in build/libs/ (use the one without "-sources").

OTHER MINECRAFT VERSIONS
Edit gradle.properties (minecraft_version, fabric_api_version, loader_version, loom_version).
Current numbers: https://fabricmc.net/develop . The mod code uses no version-specific names.
Minecraft 26.x no longer uses mappings: use the template from fabricmc.net/develop and copy src/ into it.

IF PAYMENTS DON'T SHOW
Chat wording may differ: edit the IN / OUT patterns in Parser.java and rebuild.
Port busy: change PORT in KittyPayMod.java.

Check the server's rules before using any mod. This one only reads incoming chat.
