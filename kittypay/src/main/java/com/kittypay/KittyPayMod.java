package com.kittypay;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

/** Client-only. Reads chat lines your own client receives and feeds the OBS overlay. Sends nothing. */
public class KittyPayMod implements ClientModInitializer {
    public static final int PORT = 8765;

    @Override
    public void onInitializeClient() {
        Store.init();
        Dashboard.start(PORT);
        // Payment notices arrive as system ("game") messages. No type names are used, so this
        // compiles the same under Yarn and Mojang mappings.
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!overlay) Parser.handle(message.getString());
        });
    }
}
