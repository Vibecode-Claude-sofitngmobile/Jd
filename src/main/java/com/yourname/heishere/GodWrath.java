package com.yourname.heishere;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GodWrath {
    private static final Map<UUID, Long> CD = new HashMap<>();
    private static final Map<UUID, Integer> SINS = new HashMap<>();

    public static void punish(ServerPlayer p, String msg) {
        if (p.isCreative() || p.isSpectator()) return;
        long now = p.serverLevel().getGameTime();
        if (now - CD.getOrDefault(p.getUUID(), 0L) < 100) return;
        CD.put(p.getUUID(), now);

        int c = SINS.getOrDefault(p.getUUID(), 0) + 1;
        SINS.put(p.getUUID(), c);

        if (c == 1) step1(p, msg);
        else if (c == 2) step2(p, msg);
        else step3(p);
    }

    private static void step1(ServerPlayer p, String msg) {
        p.playNotifySound(SoundEvents.WARDEN_SONIC_BOOM,
            SoundSource.MASTER, 1f, 0.5f);
        p.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 120, 0));
        bolt(p.serverLevel(), p.getX() + 3, p.getY(), p.getZ() + 3);
        p.displayClientMessage(Component.literal("ГРЕХ [1/3]: " + msg)
            .withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
    }

    private static void step2(ServerPlayer p, String msg) {
        ServerLevel w = p.serverLevel();
        p.playNotifySound(SoundEvents.ENDERMAN_SCREAM,
            SoundSource.MASTER, 1.5f, 0.4f);
        p.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 220, 1));
        Vec3 look = p.getLookAngle();
        EnderMan sh = EntityType.ENDERMAN.create(w);
        if (sh != null) {
            sh.moveTo(p.getX() - look.x * 3, p.getY(),
                p.getZ() - look.z * 3, p.getYRot(), 0);
            sh.setTarget(p);
            sh.addEffect(new MobEffectInstance(
                MobEffects.INVISIBILITY, 400, 0));
            sh.addEffect(new MobEffectInstance(
                MobEffects.GLOWING, 400, 0));
            w.addFreshEntity(sh);
        }
        p.displayClientMessage(Component.literal("ГРЕХ [2/3]: ОН ЗА СПИНОЙ.")
            .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), true);
    }

    private static void step3(ServerPlayer p) {
        ServerLevel w = p.serverLevel();
        w.setDayTime(18000);
        w.setWeatherParameters(0, 6000, true, true);
        p.playNotifySound(SoundEvents.WITHER_SPAWN,
            SoundSource.MASTER, 1.5f, 0.3f);
        p.addEffect(new MobEffectInstance(MobEffects.WITHER, 160, 0));

        AABB box = new AABB(p.blockPosition()).inflate(40.0);
        for (Mob m : w.getEntitiesOfClass(Mob.class, box, Mob::isAlive)) {
            m.setTarget(p);
            m.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SPEED, 600, 1));
            m.addEffect(new MobEffectInstance(
                MobEffects.DAMAGE_BOOST, 600, 1));
        }
        p.displayClientMessage(Component.literal("ВЕСЬ МИР ТЕПЕРЬ ТВОЙ ВРАГ.")
            .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), true);
    }

    private static void bolt(ServerLevel w, double x, double y, double z) {
        LightningBolt b = EntityType.LIGHTNING_BOLT.create(w);
        if (b != null) {
            b.moveTo(x, y, z);
            b.setVisualOnly(true);
            w.addFreshEntity(b);
        }
    }
}
