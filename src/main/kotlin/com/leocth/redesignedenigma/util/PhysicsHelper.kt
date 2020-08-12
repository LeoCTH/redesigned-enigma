package com.leocth.redesignedenigma.util

import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
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
        ifHitAction: (hitPosition: Vec3d, entitiesHit: Set<Entity>) -> Unit
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
        val entities = mutableSetOf<Entity>()
        var i = 0
        top@ while (penetratedEntities < args.penetrateEntitiesNum && i < args.maxRange) {
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
            }
            x += dirX * .25
            y += dirY * .25
            z += dirZ * .25
            ++i
        }
        if (!success)
            ifHitAction(Vec3d(x, y, z), emptySet())
        else
            ifHitAction(Vec3d(x, y, z), entities)
        return success
    }
}