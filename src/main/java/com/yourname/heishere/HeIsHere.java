package com.yourname.heishere;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.phys.AABB;
import java.util.List;

public class HeIsHere implements ModInitializer {
    private static final List<String> BAD = List.of(
        "бог лох", "бог дурак", "бога нет", "ненавижу бога",
        "тупой бог", "чертов бог", "бог урод", "бог слабак"
    );

    @Override
    public void onInitialize() {
        ServerPlayConnectionEvents.JOIN.register((h, s, srv) -> {
            ServerPlayer p = h.getPlayer();
            if (!p.getTags().contains("heishere_book")) {
                GodBook.give(p);
                p.addTag("heishere_book");
            }
        });

        ServerMessageEvents.CHAT_MESSAGE.register((msg, snd, prm) -> {
            String t = msg.signedContent().toLowerCase();
            for (String w : BAD) {
                if (t.contains(w)) {
                    snd.getServer().execute(() ->
                        GodWrath.punish(snd, "Ты оскорбил Меня..."));
                    break;
                }
            }
        });

        UseBlockCallback.EVENT.register((plr, lvl, hand, hit) -> {
            if (!lvl.isClientSide && plr instanceof ServerPlayer sp) {
                if (stolen((ServerLevel) lvl, hit.getBlockPos())) {
                    GodWrath.punish(sp, "НЕ КРАДИ У МОИХ ДЕТЕЙ.");
                }
            }
            return InteractionResult.PASS;
        });

        PlayerBlockBreakEvents.BEFORE.register((lvl, plr, pos, st, be) -> {
            if (!lvl.isClientSide && plr instanceof ServerPlayer sp) {
                if (stolen((ServerLevel) lvl, pos)) {
                    GodWrath.punish(sp, "Я ВИЖУ, ЧТО ТЫ ЛОМАЕШЬ.");
                }
            }
            return true;
        });
    }

    private boolean stolen(ServerLevel lvl, BlockPos pos) {
        Block b = lvl.getBlockState(pos).getBlock();
        if (!(b instanceof ChestBlock || b instanceof BarrelBlock)) {
            return false;
        }
        AABB box = new AABB(pos).inflate(14.0);
        return !lvl.getEntitiesOfClass(Villager.class, box).isEmpty();
    }
}
