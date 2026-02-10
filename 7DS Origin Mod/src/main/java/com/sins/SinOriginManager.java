package com.sins;

import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.origin.OriginRegistry;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public final class SinOriginManager {
    public static final List<String> SINS = List.of(
            "pride",
            "greed",
            "lust",
            "envy",
            "gluttony",
            "wrath",
            "sloth"
    );
    private static final Set<String> SIN_SET = Set.copyOf(SINS);
    private static final Identifier ORIGIN_LAYER_ID = new Identifier("origins", "origin");

    private SinOriginManager() {
    }

    public static boolean assignSin(ServerPlayerEntity player, String sinName) {
        String normalized = sinName.toLowerCase(Locale.ROOT);
        if (!SIN_SET.contains(normalized)) {
            return false;
        }

        if (!OriginLayers.contains(ORIGIN_LAYER_ID)) {
            return false;
        }

        Identifier originId = new Identifier("sins", normalized);
        if (!OriginRegistry.contains(originId)) {
            return false;
        }

        OriginLayer layer = OriginLayers.getLayer(ORIGIN_LAYER_ID);
        Origin origin = OriginRegistry.get(originId);
        if (!(origin.isChoosable() || layer.contains(origin, player))) {
            return false;
        }

        OriginComponent component = ModComponents.ORIGIN.get(player);
        boolean hadOriginBefore = component.hadOriginBefore();
        boolean hadAllOrigins = component.hasAllOrigins();

        component.setOrigin(layer, origin);
        component.checkAutoChoosingLayers(player, false);
        component.selectingOrigin(false);
        component.sync();

        if (component.hasAllOrigins() && !hadAllOrigins) {
            OriginComponent.onChosen(player, hadOriginBefore);
        }

        OriginComponent.sync(player);
        return true;
    }

    public static Optional<String> assignRandomSin(ServerPlayerEntity player) {
        if (SINS.isEmpty()) {
            return Optional.empty();
        }
        String picked = SINS.get(player.getRandom().nextInt(SINS.size()));
        if (!assignSin(player, picked)) {
            return Optional.empty();
        }
        return Optional.of(picked);
    }
}
