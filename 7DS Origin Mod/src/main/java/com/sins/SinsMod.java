package com.sins;

import net.fabricmc.api.ModInitializer;

public class SinsMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ModItems.register();
        SinCommands.register();
    }
}
