package com.leocth.redesignedenigma.util

import com.leocth.redesignedenigma.networking.C2SPacketManager
import com.leocth.redesignedenigma.networking.S2CPacketManager
import io.netty.buffer.Unpooled
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.fabricmc.fabric.api.server.PlayerStream
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.PacketByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

object PhysicsHelper {

    /**
     * Calculate a hitscan.
     * @param world World where this is happening
     * @param player Player who initiated the hitscan
     * @param args Arguments of the hitscan, incl. select predicates, max range, penetration, etc.
     * @param ifHitAction Action to be run when some entities intersects with the ray.
     *                    Do damage calculation here.
     */
    fun calcHitscan(
        world: World,
        player: PlayerEntity,
        args: HitscanArgs,
        ifHitAction: (hitPosition: Vec3d, entitiesHit: List<Entity>) -> Unit
    ) : Boolean {
        // where the player is looking at
        val pitch = player.pitch
        val yaw = player.yaw
        val a = MathHelper.cos(-yaw * 0.017453292f - 3.1415927f)
        val b = MathHelper.sin(-yaw * 0.017453292f - 3.1415927f)
        val c = -MathHelper.cos(-pitch * 0.017453292f)
        val dirX = b * c
        val dirY = MathHelper.sin(-pitch * 0.017453292f)
        val dirZ = a * c
        var x = dirX + player.x
        var y = dirY + player.eyeY
        var z = dirZ + player.z
        var success = false
        var penetratedEntities = 0
        val entities = mutableListOf<Entity>()
        var i = 0
        top@ while (i < args.maxRange) {
            // if we hit a block
            val pos = BlockPos(x, y, z)
            val block = world.getBlockState(pos)
            // rough check
            if (!block.isAir) {
                // smooth check.
                // warning: it will get very complicated with complicated models.
                for (bb in block.getCollisionShape(world, pos).boundingBoxes) {
                    val p = bb.offset(pos)
                    if (p.contains(x, y, z)) {
                        break@top
                    }
                }
            }


            val ent =
                world.getEntities(
                    if (args.ignoreShooter) player else null,
                    // rough zone for selection
                    Box(x-1,y-1,z-1,x+1,y+1,z+1)
                ) {
                    // fine check: if the entity furfills the predicate & the ray actually collided w/ its hitbox
                    args.entitySelectPredicate.invoke(it) && it.boundingBox.contains(x, y, z)
                }
            if (ent.isNotEmpty()) {
                success = true
                ++penetratedEntities
                if (penetratedEntities <= args.penetrateEntitiesNum)
                    entities.addAll(ent)
                else
                    break@top
            }
            x += dirX * .5
            y += dirY * .5
            z += dirZ * .5
            ++i
        }
        if (!success)
            ifHitAction(Vec3d(x, y, z), emptyList())
        else
            ifHitAction(Vec3d(x, y, z), entities)
        return success
    }

    fun sendS2CUpdatePacket(canFire: Boolean, hitPos: Vec3d, user: PlayerEntity, needsReload: Boolean) {
        val watchingPlayers = PlayerStream.watching(user.world, user.blockPos)
        val packetData = PacketByteBuf(Unpooled.buffer())

        packetData.writeBoolean(canFire)
        packetData.writeFloat(hitPos.x.toFloat())
        packetData.writeFloat(hitPos.y.toFloat())
        packetData.writeFloat(hitPos.z.toFloat())
        packetData.writeBoolean(needsReload)

        watchingPlayers.forEach {
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(
                it, S2CPacketManager.PEWPEW_S2C_UPDATE_ID, packetData
            )
        }
    }

    @Environment(EnvType.CLIENT)
    fun sendC2SFirePacket() {
        val packetData = PacketByteBuf(Unpooled.buffer())

        ClientSidePacketRegistry.INSTANCE.sendToServer(C2SPacketManager.PEWPEW_C2S_FIRE_ID, packetData)
    }
}