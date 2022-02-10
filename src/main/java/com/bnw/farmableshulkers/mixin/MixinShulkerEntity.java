package com.bnw.farmableshulkers.mixin;

import com.bnw.farmableshulkers.entity.ColorableEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ShulkerEntity.class)
public abstract class MixinShulkerEntity extends GolemEntity implements ColorableEntity {
    @Final
    @Shadow
    protected static DataParameter<Byte> DATA_COLOR_ID;

    @Final
    @Shadow
    protected static DataParameter<Optional<BlockPos>> DATA_ATTACH_POS_ID;


    protected MixinShulkerEntity(EntityType<? extends GolemEntity> entityType, World level) {
        super(entityType, level);
    }


    /**
     * Attempts to teleport the shulker to a random location.
     *
     * @return true if the shulker was teleported; otherwise, false.
     */
    @Shadow
    protected abstract boolean teleportSomewhere();

    /**
     * Returns true if the shulker is closed; otherwise, false.
     * @return true if the shulker is closed; otherwise, false.
     */
    @Shadow
    protected abstract boolean isClosed();

    /**
     * {@inheritDoc}
     */
    public DyeColor getColor() {
        Byte colorByte = this.entityData.get(DATA_COLOR_ID);
        return colorByte != 16 && colorByte <= 15 ? DyeColor.byId(colorByte) : null;
    }

    /**
     * {@inheritDoc}
     */
    public void setColor(DyeColor color) {
        this.entityData.set(DATA_COLOR_ID, (byte)color.getId());
    }

    /**
     * Returns true if the block is empty; otherwise, false.
     *
     * @param pos The position to check.
     * @return true if the block is empty; otherwise, false.
     */
    private boolean isBlockEmpty(BlockPos pos) {
        BlockState blockState = this.level.getBlockState(pos);
        return blockState.isAir() || (blockState.is(Blocks.MOVING_PISTON) && pos.equals(this.blockPosition()));
    }

    /**
     * Creates intersection box of the shulker.
     *
     * @param direction The side to which the shulker opens.
     * @param prevOffset Shulker's opening progress on the previous step.
     * @param offset Shulker's opening progress.
     *
     * @return An intersection box of the shulker.
     */
    private static AxisAlignedBB createIntersectionBox(Direction direction, float prevOffset, float offset) {
        double max = Math.max(prevOffset, offset);
        double min = Math.min(prevOffset, offset);
        AxisAlignedBB testBox = new AxisAlignedBB(BlockPos.ZERO);
        return testBox.inflate(
                direction.getStepX() * max,
                direction.getStepY() * max,
                direction.getStepZ() * max
        ).contract(
                -direction.getStepX() * (1.0D + min),
                -direction.getStepY() * (1.0D + min),
                -direction.getStepZ() * (1.0D + min)
        );
    }

    /**
     * Returns true if the given block is attachable; otherwise, false.
     *
     * @param pos The position to check.
     * @param direction The direction to check.
     * @return true if the given block is attachable; otherwise, false.
     */
    private boolean isBlockAttachable(BlockPos pos, Direction direction) {
        if (this.isBlockEmpty(pos)) {
            Direction opposite = direction.getOpposite();
            if (this.level.loadedAndEntityCanStandOnFace(pos.relative(direction), this, opposite)) {
                AxisAlignedBB box = createIntersectionBox(opposite, -1.0F, 1.0F).move(pos).deflate(1.0E-6D);
                return this.level.noCollision(this, box);
            }
        }

        return false;
    }

    /**
     * Overrides the logic by which the shulker determines
     * whether it can use a block as an anchorage position.
     *
     * @param pos The position to check.
     * @param direction The direction to check.
     * @param cir The callback info.
     */
//    @Inject(method = "canAttachOnBlockFace", at = @At("HEAD"), cancellable = true)
//    private void isBlockAttachable(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
//        cir.setReturnValue(isBlockAttachable(pos, direction));
//    }

    /**
     * We already have a property that reflects a position of the entity.
     * Why not add another one and then forget to process it?
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate.
     * @param ci The callback info.
     */
    @Inject(method = "setPos(DDD)V", at = @At(value = "HEAD"))
    protected void setAttachedBlock(double x, double y, double z, CallbackInfo ci) {
        if (this.entityData != null && this.tickCount == 0) {
            Optional<BlockPos> pos = this.entityData.get(DATA_ATTACH_POS_ID);
            Optional<BlockPos> newPos = Optional.of(new BlockPos(x, y, z));
            if (!newPos.equals(pos)) {
                this.entityData.set(DATA_ATTACH_POS_ID, newPos);
            }
        }
    }

    /**
     * Implements shulker duplication logic.
     *
     * @param damageSource The damage source.
     * @param damageAmount The damage amount.
     * @param cir The callback info.
     */
    @Inject(method = "hurt", at = @At("RETURN"), cancellable = true)
    protected void onDamage(DamageSource damageSource, float damageAmount, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            Entity entitySource = damageSource.getDirectEntity();
            if (entitySource != null && entitySource.getType() == EntityType.SHULKER_BULLET) {
                this.spawnNewShulker();
            }
        }
    }

    /**
     * Attempts to teleport the shulker and spawn a new one at its original location.
     */
    private void spawnNewShulker() {
        Vector3d pos = this.position();
        AxisAlignedBB box = this.getBoundingBox();
        if (!this.isClosed() && this.teleportSomewhere()) {
            int i = this.level.getEntities(EntityType.SHULKER, box.inflate(8.0D), Entity::isAlive).size();
            float f = (float)(i - 1) / 5.0F;
            if (this.level.random.nextFloat() >= f) {
                ShulkerEntity shulkerEntity = EntityType.SHULKER.create(this.level);
                if (shulkerEntity == null) {
                    return;
                }

                DyeColor dyeColor = ((ColorableEntity)this).getColor();
                if (dyeColor != null) {
                    ((ColorableEntity)shulkerEntity).setColor(dyeColor);
                }

                shulkerEntity.setPos(pos.x, pos.y, pos.z);
                this.level.addFreshEntity(shulkerEntity);
            }
        }
    }


}