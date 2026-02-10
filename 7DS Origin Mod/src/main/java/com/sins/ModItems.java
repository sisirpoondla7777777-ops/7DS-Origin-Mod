package com.sins;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModItems {
    public static final Item SIN_SIGIL = new SinSigilItem(new Item.Settings().maxCount(1));

    private ModItems() {}

    public static void register() {
        Registry.register(Registries.ITEM, new Identifier("sins", "sin_sigil"), SIN_SIGIL);
    }
}
