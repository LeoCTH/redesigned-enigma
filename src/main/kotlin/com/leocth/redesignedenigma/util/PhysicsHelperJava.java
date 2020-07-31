package com.leocth.redesignedenigma.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import javafx.geometry.BoundingBox;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Function;

/**
 * This is a Java file created by leoc200 on 2020/7/31 in project redesigned-enigma-
 * All sources that are released publicly on GitHub are licensed under the MIT license.
 * Please do not redistribute this file to other platforms before acknowledging the author.
 */
public class PhysicsHelperJava {
    public static boolean calcHitscan(
            World world,
            PlayerEntity player,
            HitscanArgs args,
            IfHitAction ifHitAction
    ) {
        Preconditions.checkNotNull(world);
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(args);
        Preconditions.checkNotNull(ifHitAction);

        float pitch = player.pitch;
        float yaw = player.yaw;
        float a = MathHelper.cos(-yaw * 0.017453292f - 3.1415927f);
        float b = MathHelper.sin(-yaw * 0.017453292f - 3.1415927f);
        float c = -MathHelper.cos(-pitch * 0.017453292f);
        float dirX = b * c;
        float dirY = MathHelper.sin(-pitch * 0.017453292f);
        float dirZ = a * c;
        double x = dirX + player.getX();
        double y = dirY + player.getEyeY();
        double z = dirZ + player.getZ();
        boolean success = false;
        int penetratedEntities = 0;
        List<Entity> entities = Lists.newArrayList();
        top:
        for (int i = 0; i < args.getMaxRange(); i++) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockState block = world.getBlockState(pos);
            if (!block.isAir()) {
                for (Box bb : block.getCollisionShape(world, pos).getBoundingBoxes()) {
                    Box p = bb.offset(pos);
                    if (p.contains(x, y, z)) break top;
                }
            }

            double finalX = x;
            double finalY = y;
            double finalZ = z;
            List<Entity> ent =
                world.getEntities(
                    args.getIgnoreShooter() ? player : null,
                    new Box(x-1,y-1,z-1,x+1,y+1,z+1),
                    (Entity entity) ->
                        args.getEntitySelectPredicate().invoke(entity) && entity.getBoundingBox().contains(finalX, finalY, finalZ)
                );

            if (!ent.isEmpty()) {
                success = true;
                ++penetratedEntities;
                if (penetratedEntities <= args.getPenetrateEntitiesNum())
                    entities.addAll(ent);
                else
                    break;
            }
            x += dirX * 0.5;
            y += dirY * 0.5;
            z += dirZ * 0.5;
        }
        if (!success)
            ifHitAction.run(new Vec3d(x, y, z), Lists.newArrayList());
        else
            ifHitAction.run(new Vec3d(x, y, z), entities);
        return success;
    }

    @FunctionalInterface
    interface IfHitAction {
        void run(Vec3d hitPos, List<Entity> entities);
    }

    public static void main(String[] args) {
        //PhysicsHelperJava.calcHitscan(null, null, HitscanArgs.DEFAULT, ((hitPos, entities) -> {}));
        System.out.println("This mod is written by GrieferPig, LeoC200 had stolen my credut lol");
    }
}
