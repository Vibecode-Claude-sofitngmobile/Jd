package com.yourname.heishere;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import java.util.List;

public class HeIsHere implements ModInitializer {
    private static final List<String> BAD = List.of(
        "бог лох", "бог дурак", "бога нет", "ненавижу бога",
        "тупой бог", "чертов бог", "бог урод", "бог слабак"
    );

    @Override
    public void onInitialize() {
        ServerPlayConnectionEvents.JOIN.register((h, s, srv) -> {
            ServerPlayerEntity p = h.getPlayer();
            if (!p.getCommandTags().contains("heishere_book")) {
                GodBook.give(p);
                p.addCommandTag("heishere_book");
            }
        });

        ServerMessageEvents.CHAT_MESSAGE.register((msg, snd, prm) -> {
            String t = msg.getContent().getString().toLowerCase();
            for (String w : BAD) {
                if (t.contains(w)) {
                    snd.getServer().execute(() ->
                        GodWrath.punish(snd, "Ты оскорбил Меня..."));
                    break;
                }
            }
        });

        UseBlockCallback.EVENT.register((plr, w, h, hit) -> {
            if (!w.isClient && plr instanceof ServerPlayerEntity sp) {
                if (stolen((ServerWorld) w, hit.getBlockPos())) {
                    GodWrath.punish(sp, "НЕ КРАДИ У МОИХ ДЕТЕЙ.");
                }
            }
            return ActionResult.PASS;
        });

        PlayerBlockBreakEvents.BEFORE.register((w, plr, pos, st, be) -> {
            if (!w.isClient && plr instanceof ServerPlayerEntity sp) {
                if (stolen((ServerWorld) w, pos)) {
                    GodWrath.punish(sp, "Я ВИЖУ, ЧТО ТЫ ЛОМАЕШЬ.");
                }
            }
            return true;
        });
    }

    private boolean stolen(ServerWorld w, BlockPos pos) {
        Block b = w.getBlockState(pos).getBlock();
        if (!(b instanceof ChestBlock || b instanceof BarrelBlock)) {
            return false;
        }
        Box box = new Box(pos).expand(14.0);
        return !w.getEntitiesByClass(
            VillagerEntity.class, box, v -> true).isEmpty();
    }
}
