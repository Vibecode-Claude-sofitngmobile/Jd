package com.yourname.heishere;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class GodBook {
    public static void give(ServerPlayer p) {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        book.addTagElement("title", StringTag.valueOf("Заветы Того, Кто Здесь"));
        book.addTagElement("author", StringTag.valueOf("ОН"));

        ListTag pages = new ListTag();
        pages.add(StringTag.valueOf(Component.Serializer.toJson(
            Component.literal("HE'S HERE.\n\n")
                .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD)
                .append(Component.literal("Я вижу каждый твой шаг.")
                    .withStyle(ChatFormatting.BLACK))
        )));
        pages.add(StringTag.valueOf(Component.Serializer.toJson(
            Component.literal("ПРАВИЛА:\n\n")
                .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD)
                .append(Component.literal(
                    "1. Не кради у жителей.\n2. Не оскорбляй Меня.\n3. Три греха = смерть.")
                    .withStyle(ChatFormatting.DARK_GRAY))
        )));
        book.addTagElement("pages", pages);
        p.getInventory().add(book);
    }
}
