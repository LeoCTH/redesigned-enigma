package com.leocth.redesignedenigma.item

import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.World

class PewPewItem: Item(Settings().maxCount(1)) {
    companion object {
        fun calcHitScan(
            world: World,
            player: PlayerEntity,
            entitySelectPredicate: ((Entity) -> Boolean)? = { it is LivingEntity },
            maxRange: Int = 128,
            ignorePlayer: Boolean = true,
            ifHitAction: (entitiesHit: List<Entity>) -> Unit
        ) : Boolean {
            val dir = Vec3d.fromPolar(player.pitch, player.yaw)
            var x = dir.x + player.x
            var y = dir.y + player.eyeY
            var z = dir.z + player.z
            var hit = false
            var i = 0
            while (!hit && i < maxRange) {
                // if we hit a block
                val pos = BlockPos(x, y, z)
                val block = world.getBlockState(pos)
                if (!block.isAir) {
                    println("ain't air @ $pos, $block")
                    for ((ind, b) in block.getCollisionShape(world, pos).boundingBoxes.withIndex()) {
                        val p = b.offset(pos)
                        println("[$ind] => $p ($x, $y, $z)")
                        if (x in p.x1..p.x2 && y in p.y1..p.y2 && z in p.z1..p.z2) {
                            println("burp")
                            return false
                        }
                    }
                }

                val entities =
                    world.getEntities(
                        if (ignorePlayer) player else null,
                        // rough check
                        Box(x-1,y-1,z-1,x+1,y+1,z+1)
                    ) {
                        // fine check: if actually collided w/ hitbox
                        (entitySelectPredicate == null || entitySelectPredicate.invoke(it)) && it.boundingBox.contains(x, y, z)
                    }
                hit = entities.isNotEmpty()
                if (hit) {
                    println("hit, $entities")
                    ifHitAction(entities)
                    return true
                }
                x += dir.x * .5
                y += dir.y * .5
                z += dir.z * .5
                ++i
            }
            return false
        }
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        return if (world.isClient) {
            // render code?
            super.use(world, user, hand)
        } else {
            val stack = user.mainHandStack
            val k = calcHitScan(world, user) { entities ->
                entities.forEach {
                    it.damage(DamageSource.player(user), 2.0f)
                }
            }
            if (k) TypedActionResult.success(stack) else TypedActionResult.pass(stack)
        }
    }
}