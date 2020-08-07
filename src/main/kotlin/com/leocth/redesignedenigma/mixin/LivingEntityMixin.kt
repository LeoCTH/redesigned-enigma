package com.leocth.redesignedenigma.mixin

import com.leocth.redesignedenigma.util.damage.IIgnoreRegenCooldown
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.passive.WolfEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundEvent
import net.minecraft.stat.Stats
import net.minecraft.world.World
import org.objectweb.asm.Opcodes
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.spongepowered.asm.mixin.injection.callback.LocalCapture
import kotlin.math.roundToInt

@Mixin(LivingEntity::class)
abstract class LivingEntityMixin(type: EntityType<out LivingEntity>, world: World) : Entity(type, world) {

    @Shadow
    var lastDamageTime = 0L
    @Shadow
    lateinit var lastDamageSource: DamageSource
    @Shadow
    var attackingPlayer: PlayerEntity? = null
    @Shadow
    var playerHitTimer = 0
    @Shadow
    var lastDamageTaken = 0f
    @Shadow
    var maxHurtTime = 0
    @Shadow
    var hurtTime = 0

    @Inject(
        at = [
            At(
                value = "FIELD",
                target = "limbDistance:F",
                opcode = Opcodes.PUTFIELD
            )
        ],
        method = ["damage"],
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    fun bypassTimeUntilRegen(
        source: DamageSource,
        amount: Float,
        ci: CallbackInfoReturnable<Boolean>,
        f: Float, bl: Boolean, g: Float
    ) {
        if (source is IIgnoreRegenCooldown) {
            this.lastDamageTaken = amount
            this.timeUntilRegen = 0
            this.applyDamage(source, amount)
            this.maxHurtTime = 10
            this.hurtTime = this.maxHurtTime

            val attacker = source.attacker
            if (attacker != null) {
                if (attacker is LivingEntity) {
                    this.setAttacker(attacker as LivingEntity?)
                }
                if (attacker is PlayerEntity) {
                    this.playerHitTimer = 100
                    this.attackingPlayer = attacker
                } else if (attacker is WolfEntity) {
                    if (attacker.isTamed) {
                        this.playerHitTimer = 100
                        val livingEntity = attacker.owner
                        if (livingEntity != null && livingEntity.type === EntityType.PLAYER) {
                            this.attackingPlayer = livingEntity as PlayerEntity
                        } else {
                            this.attackingPlayer = null
                        }
                    }
                }
            }

            world.sendEntityStatus(this, 2.toByte())
            scheduleVelocityUpdate()

            if (this.getHealth() <= 0.0f) {
                if (!this.tryUseTotem(source)) {
                    val soundEvent = this.getDeathSound()
                    if (soundEvent != null) {
                        playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch())
                    }
                    this.onDeath(source)
                }
            }
            this.playHurtSound(source)

            val bl3 = /*!bl || TODO*/ amount > 0.0f
            if (bl3) {
                this.lastDamageSource = source
                this.lastDamageTime = world.time
            }

            if (this is ServerPlayerEntity) {
                Criteria.ENTITY_HURT_PLAYER.trigger(this as ServerPlayerEntity, source, f, amount, bl)
                if (g > 0.0f && g < 3.4028235E37f) {
                    (this as ServerPlayerEntity).increaseStat(
                        Stats.DAMAGE_BLOCKED_BY_SHIELD,
                        (g * 10.0f).roundToInt()
                    )
                }
            }

            if (attacker is ServerPlayerEntity) {
                Criteria.PLAYER_HURT_ENTITY.trigger(attacker as ServerPlayerEntity?, this, source, f, amount, bl)
            }

            ci.returnValue = bl3
        }
    }

    @Shadow
    abstract fun setAttacker(livingEntity: LivingEntity?)

    @Shadow
    abstract fun playHurtSound(source: DamageSource)

    @Shadow
    abstract fun onDeath(source: DamageSource)

    @Shadow
    abstract fun getSoundPitch(): Float

    @Shadow
    abstract fun getSoundVolume(): Float

    @Shadow
    abstract fun getDeathSound(): SoundEvent?

    @Shadow
    abstract fun tryUseTotem(source: DamageSource): Boolean

    @Shadow
    abstract fun getHealth(): Float

    @Shadow
    abstract fun applyDamage(source: DamageSource, amount: Float)
}