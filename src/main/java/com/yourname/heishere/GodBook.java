package com.yourname.heishere;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class GodBook {
    public static void give(ServerPlayerEntity p) {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        book.setSubNbt("title", NbtString.of("Заветы Того, Кто Здесь"));
        book.setSubNbt("author", NbtString.of("ОН"));

        NbtList pages = new NbtList();
        pages.add(NbtString.of(Text.Serializer.toJson(
            Text.literal("HE'S HERE.\n\n")
                .formatted(Formatting.DARK_RED, Formatting.BOLD)
                .append(Text.literal("Я вижу каждый твой шаг.")
                    .formatted(Formatting.BLACK))
        )));
        pages.add(NbtString.of(Text.Serializer.toJson(
            Text.literal("ПРАВИЛА:\n\n")
                .formatted(Formatting.DARK_RED, Formatting.BOLD)
                .append(Text.literal(
                    "1. Не кради у жителей.\n2. Не оскорбляй Меня.\n3. Три греха = смерть.")
                    .formatted(Formatting.DARK_GRAY))
        )));
        book.setSubNbt("pages", pages);
        p.getInventory().insertStack(book);
    }
}
