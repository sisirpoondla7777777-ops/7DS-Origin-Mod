package com.sins;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TooltipContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class SinSigilItem extends Item {
    public SinSigilItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) {
            return new TypedActionResult<>(ActionResult.SUCCESS, user.getStackInHand(hand));
        }

        ServerPlayerEntity player = (ServerPlayerEntity) user;

        if (player.isSneaking()) {
            Optional<String> randomSin = SinOriginManager.assignRandomSin(player);
            if (randomSin.isPresent()) {
                String sin = randomSin.get();
                player.sendMessage(Text.literal("Sin Sigil binds you to " + sin.toUpperCase(Locale.ROOT)).formatted(Formatting.GOLD), false);
            } else {
                player.sendMessage(Text.literal("Could not assign a random sin.").formatted(Formatting.RED), false);
            }
            return new TypedActionResult<>(ActionResult.SUCCESS, user.getStackInHand(hand));
        }

        player.sendMessage(Text.literal("Choose a Sin:").formatted(Formatting.GRAY), false);
        player.sendMessage(buildChoiceLine(), false);

        return new TypedActionResult<>(ActionResult.SUCCESS, user.getStackInHand(hand));
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Right-click: choose a Sin").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Sneak + right-click: random Sin").formatted(Formatting.DARK_GRAY));
    }

    private static Text buildChoiceLine() {
        Text line = Text.empty();
        for (int i = 0; i < SinOriginManager.SINS.size(); i++) {
            String sin = SinOriginManager.SINS.get(i);
            Text button = Text.literal("[" + sin.toUpperCase(Locale.ROOT) + "]")
                    .setStyle(Style.EMPTY
                            .withColor(Formatting.GOLD)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sin choose " + sin)));

            line = line.append(button);
            if (i < SinOriginManager.SINS.size() - 1) {
                line = line.append(" ");
            }
        }
        return line;
    }
}
