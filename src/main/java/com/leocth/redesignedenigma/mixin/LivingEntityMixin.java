package com.leocth.redesignedenigma.mixin;

import com.leocth.redesignedenigma.item.weapon.IIgnoreEquipProgress;
import com.leocth.redesignedenigma.util.damage.IIgnoreRegenCooldown;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow protected float lastDamageTaken;
    @Shadow private long lastDamageTime;
    @Shadow public int maxHurtTime;
    @Shadow public int hurtTime;
    @Shadow protected int playerHitTimer;
    @Shadow protected PlayerEntity attackingPlayer;
    @Shadow private DamageSource lastDamageSource;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/LivingEntity;limbDistance:F",
            opcode = Opcodes.PUTFIELD
        ),
        method = "damage",
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    void bypassTimeUntilRegen(
        DamageSource source,
        float amount,
        CallbackInfoReturnable<Boolean> cir,
        float f, boolean bl, float g
    ) {

        if (source instanceof IIgnoreRegenCooldown) {
            this.lastDamageTaken = amount;
            this.timeUntilRegen = 0;
            this.applyDamage(source, amount);
            this.maxHurtTime = 10;
            this.hurtTime = this.maxHurtTime;

            Entity attacker = source.getAttacker();
            if (attacker != null) {
                if (attacker instanceof LivingEntity) {
                    this.setAttacker((LivingEntity) attacker);
                }
                if (attacker instanceof PlayerEntity) {
                    this.playerHitTimer = 100;
                    this.attackingPlayer = (PlayerEntity) attacker;
                }
                else if (attacker instanceof WolfEntity) {
                    // FUUU I WANNA MY SMART CASTS
                    if (((WolfEntity) attacker).isTamed()) {
                        this.playerHitTimer = 100;
                        LivingEntity owner = ((WolfEntity) attacker).getOwner();
                        if (owner != null && owner.getType() == EntityType.PLAYER) {
                            this.attackingPlayer = (PlayerEntity) owner;
                        }
                        else {
                            this.attackingPlayer = null;
                        }
                    }
                }
            }

            world.sendEntityStatus(this, (byte) 2);
            this.scheduleVelocityUpdate();

            if (this.getHealth() <= 0.0f) {
                if (!this.tryUseTotem(source)) {
                    SoundEvent soundEvent = this.getDeathSound();
                    if (soundEvent != null) {
                        playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
                    }
                    this.onDeath(source);
                }
            }
            this.playHurtSound(source);

            boolean bl3 = bl || amount > 0.0f;
            if (bl3) {
                this.lastDamageSource = source;
                this.lastDamageTime = world.getTime();
            }

            /* FIXME: casting failed! try fix later...
            if (this instanceof ServerPlayerEntity) {
                Criteria.ENTITY_HURT_PLAYER.trigger((ServerPlayerEntity)this, source, f, amount, bl);
                if (g > 0.0F && g < 3.4028235E37F) {
                    ((ServerPlayerEntity)this).increaseStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(g * 10.0F));
                }
            }

            if (attacker instanceof ServerPlayerEntity) {
                Criteria.PLAYER_HURT_ENTITY.trigger((ServerPlayerEntity)entity2, this, source, f, amount, bl);
            }
             */
            cir.setReturnValue(bl3);
        }
    }

    @Shadow public abstract float getHealth();
    @Shadow protected abstract boolean tryUseTotem(DamageSource source);
    @Shadow protected abstract SoundEvent getDeathSound();
    @Shadow protected abstract float getSoundVolume();
    @Shadow protected abstract float getSoundPitch();
    @Shadow public abstract void onDeath(DamageSource source);
    @Shadow protected abstract void playHurtSound(DamageSource source);
    @Shadow public abstract void setAttacker(LivingEntity attacker);
    @Shadow protected abstract void applyDamage(DamageSource source, float amount);
}
