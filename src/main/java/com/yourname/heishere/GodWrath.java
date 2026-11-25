package com.yourname.heishere;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GodWrath {
    private static final Map<UUID, Long> CD = new HashMap<>();
    private static final Map<UUID, Integer> SINS = new HashMap<>();

    public static void punish(ServerPlayerEntity p, String msg) {
        if (p.isCreative() || p.isSpectator()) return;
        long now = p.getServerWorld().getTime();
        if (now - CD.getOrDefault(p.getUuid(), 0L) < 100) return;
        CD.put(p.getUuid(), now);

        int c = SINS.getOrDefault(p.getUuid(), 0) + 1;
        SINS.put(p.getUuid(), c);

        if (c == 1) step1(p, msg);
        else if (c == 2) step2(p, msg);
        else step3(p);
    }

    private static void step1(ServerPlayerEntity p, String msg) {
        p.playSound(SoundEvents.ENTITY_WARDEN_SONIC_BOOM,
            SoundCategory.MASTER, 1f, 0.5f);
        p.addStatusEffect(
            new StatusEffectInstance(StatusEffects.DARKNESS, 120, 0));
        bolt(p.getServerWorld(), p.getX() + 3, p.getY(), p.getZ() + 3);
        p.sendMessage(Text.literal("ГРЕХ [1/3]: " + msg)
            .formatted(Formatting.RED, Formatting.BOLD), true);
    }

    private static void step2(ServerPlayerEntity p, String msg) {
        ServerWorld w = p.getServerWorld();
        p.playSound(SoundEvents.ENTITY_ENDERMAN_SCREAM,
            SoundCategory.MASTER, 1.5f, 0.4f);
        p.addStatusEffect(
            new StatusEffectInstance(StatusEffects.DARKNESS, 220, 1));
        Vec3d look = p.getRotationVector();
        EndermanEntity sh = EntityType.ENDERMAN.create(w);
        if (sh != null) {
            sh.refreshPositionAndAngles(p.getX() - look.x * 3,
                p.getY(), p.getZ() - look.z * 3, p.getYaw(), 0);
            sh.setTarget(p);
            sh.addStatusEffect(new StatusEffectInstance(
                StatusEffects.INVISIBILITY, 400, 0));
            sh.addStatusEffect(new StatusEffectInstance(
                StatusEffects.GLOWING, 400, 0));
            w.spawnEntity(sh);
        }
        p.sendMessage(Text.literal("ГРЕХ [2/3]: ОН ЗА СПИНОЙ.")
            .formatted(Formatting.DARK_RED, Formatting.BOLD), true);
    }

    private static void step3(ServerPlayerEntity p) {
        ServerWorld w = p.getServerWorld();
        w.setTimeOfDay(18000);
        w.setWeather(0, 6000, true, true);
        p.playSound(SoundEvents.ENTITY_WITHER_SPAWN,
            SoundCategory.MASTER, 1.5f, 0.3f);
        p.addStatusEffect(
            new StatusEffectInstance(StatusEffects.WITHER, 160, 0));

        Box box = new Box(p.getBlockPos()).expand(40.0);
        for (MobEntity m : w.getEntitiesByClass(
                MobEntity.class, box, MobEntity::isAlive)) {
            m.setTarget(p);
            m.addStatusEffect(
                new StatusEffectInstance(StatusEffects.SPEED, 600, 1));
            m.addStatusEffect(
                new StatusEffectInstance(StatusEffects.STRENGTH, 600, 1));
        }
        p.sendMessage(Text.literal("ВЕСЬ МИР ТЕПЕРЬ ТВОЙ ВРАГ.")
            .formatted(Formatting.DARK_RED, Formatting.BOLD), true);
    }

    private static void bolt(ServerWorld w, double x, double y, double z) {
        LightningEntity b = EntityType.LIGHTNING_BOLT.create(w);
        if (b != null) {
            b.refreshPositionAfterTeleport(x, y, z);
            b.setCosmetic(true);
            w.spawnEntity(b);
        }
    }
}
