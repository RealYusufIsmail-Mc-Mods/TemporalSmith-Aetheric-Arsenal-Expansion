/*
 * Copyright 2023 RealYusufIsmail.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package io.github.realyusufismail.temporalsmith.entities;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class EnderiteGolem extends AbstractGolem implements NeutralMob {
  protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID =
      SynchedEntityData.defineId(EnderiteGolem.class, EntityDataSerializers.BYTE);
  private static final int ENDERITE_INGOT_HEAL_AMOUNT = 25;
  @Getter private int attackAnimationTick;
  @Getter private int offerFlowerTick;
  private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
  private int remainingPersistentAngerTime;
  @Nullable private UUID persistentAngerTarget;

  public EnderiteGolem(EntityType<? extends EnderiteGolem> p_28834_, Level p_28835_) {
    super(p_28834_, p_28835_);
    this.setMaxUpStep(1.0F);
  }

  @Override
  protected void registerGoals() {
    this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
    this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9, 32.0F));
    this.goalSelector.addGoal(2, new MoveBackToVillageGoal(this, 0.6, false));
    this.goalSelector.addGoal(4, new GolemRandomStrollInVillageGoal(this, 0.6));
    // this.goalSelector.addGoal(5, new OfferFlowerGoal(this));
    this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
    this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    // this.targetSelector.addGoal(1, new DefendVillageTargetGoal(this));
    this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(
        3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
    this.targetSelector.addGoal(
        3,
        new NearestAttackableTargetGoal<>(
            this,
            Mob.class,
            5,
            false,
            false,
            p_28879_ -> p_28879_ instanceof Enemy && !(p_28879_ instanceof Creeper)));
    this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.entityData.define(DATA_FLAGS_ID, (byte) 0);
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 100.0)
        .add(Attributes.MOVEMENT_SPEED, 0.25)
        .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
        .add(Attributes.ATTACK_DAMAGE, 15.0);
  }

  @Override
  protected int decreaseAirSupply(int p_28882_) {
    return p_28882_;
  }

  @Override
  protected void doPush(Entity p_28839_) {
    if (p_28839_ instanceof Enemy
        && !(p_28839_ instanceof Creeper)
        && this.getRandom().nextInt(20) == 0) {
      this.setTarget((LivingEntity) p_28839_);
    }

    super.doPush(p_28839_);
  }

  @Override
  public void aiStep() {
    super.aiStep();
    if (this.attackAnimationTick > 0) {
      --this.attackAnimationTick;
    }

    if (this.offerFlowerTick > 0) {
      --this.offerFlowerTick;
    }

    if (!this.level().isClientSide) {
      this.updatePersistentAnger((ServerLevel) this.level(), true);
    }
  }

  @Override
  public boolean canSpawnSprintParticle() {
    return this.getDeltaMovement().horizontalDistanceSqr() > 2.5000003E-7F
        && this.random.nextInt(5) == 0;
  }

  @Override
  public boolean canAttackType(EntityType<?> p_28851_) {
    if (this.isPlayerCreated() && p_28851_ == EntityType.PLAYER) {
      return false;
    } else {
      return p_28851_ != EntityType.CREEPER && super.canAttackType(p_28851_);
    }
  }

  @Override
  public void addAdditionalSaveData(CompoundTag p_28867_) {
    super.addAdditionalSaveData(p_28867_);
    p_28867_.putBoolean("PlayerCreated", this.isPlayerCreated());
    this.addPersistentAngerSaveData(p_28867_);
  }

  @Override
  public void readAdditionalSaveData(CompoundTag p_28857_) {
    super.readAdditionalSaveData(p_28857_);
    this.setPlayerCreated(p_28857_.getBoolean("PlayerCreated"));
    this.readPersistentAngerSaveData(this.level(), p_28857_);
  }

  @Override
  public void startPersistentAngerTimer() {
    this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
  }

  @Override
  public void setRemainingPersistentAngerTime(int p_28859_) {
    this.remainingPersistentAngerTime = p_28859_;
  }

  @Override
  public int getRemainingPersistentAngerTime() {
    return this.remainingPersistentAngerTime;
  }

  @Override
  public void setPersistentAngerTarget(@Nullable UUID p_28855_) {
    this.persistentAngerTarget = p_28855_;
  }

  @Nullable
  @Override
  public UUID getPersistentAngerTarget() {
    return this.persistentAngerTarget;
  }

  private float getAttackDamage() {
    return (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
  }

  @Override
  public boolean doHurtTarget(Entity p_28837_) {
    this.attackAnimationTick = 10;
    this.level().broadcastEntityEvent(this, (byte) 4);
    float f = this.getAttackDamage();
    float f1 = (int) f > 0 ? f / 2.0F + (float) this.random.nextInt((int) f) : f;
    boolean flag = p_28837_.hurt(this.damageSources().mobAttack(this), f1);
    if (flag) {
      double d0 =
          p_28837_ instanceof LivingEntity livingentity
              ? livingentity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)
              : 0.0;
      double d1 = Math.max(0.0, 1.0 - d0);
      p_28837_.setDeltaMovement(p_28837_.getDeltaMovement().add(0.0, 0.4F * d1, 0.0));
      this.doEnchantDamageEffects(this, p_28837_);
    }

    this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
    return flag;
  }

  @Override
  public boolean hurt(DamageSource p_28848_, float p_28849_) {
    EnderiteGolem.Crackiness irongolem$crackiness = this.getCrackiness();
    boolean flag = super.hurt(p_28848_, p_28849_);
    if (flag && this.getCrackiness() != irongolem$crackiness) {
      this.playSound(SoundEvents.IRON_GOLEM_DAMAGE, 1.0F, 1.0F);
    }

    return flag;
  }

  public EnderiteGolem.Crackiness getCrackiness() {
    return EnderiteGolem.Crackiness.byFraction(this.getHealth() / this.getMaxHealth());
  }

  @Override
  public void handleEntityEvent(byte p_28844_) {
    if (p_28844_ == 4) {
      this.attackAnimationTick = 10;
      this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
    } else if (p_28844_ == 11) {
      this.offerFlowerTick = 400;
    } else if (p_28844_ == 34) {
      this.offerFlowerTick = 0;
    } else {
      super.handleEntityEvent(p_28844_);
    }
  }

  public void offerFlower(boolean p_28886_) {
    if (p_28886_) {
      this.offerFlowerTick = 400;
      this.level().broadcastEntityEvent(this, (byte) 11);
    } else {
      this.offerFlowerTick = 0;
      this.level().broadcastEntityEvent(this, (byte) 34);
    }
  }

  @Override
  protected SoundEvent getHurtSound(DamageSource p_28872_) {
    return SoundEvents.IRON_GOLEM_HURT;
  }

  @Override
  protected SoundEvent getDeathSound() {
    return SoundEvents.IRON_GOLEM_DEATH;
  }

  @Override
  protected InteractionResult mobInteract(Player p_28861_, InteractionHand p_28862_) {
    ItemStack itemstack = p_28861_.getItemInHand(p_28862_);
    if (!itemstack.is(Items.IRON_INGOT)) {
      return InteractionResult.PASS;
    } else {
      float f = this.getHealth();
      this.heal(25.0F);
      if (this.getHealth() == f) {
        return InteractionResult.PASS;
      } else {
        float f1 = 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
        this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, f1);
        if (!p_28861_.getAbilities().instabuild) {
          itemstack.shrink(1);
        }

        return InteractionResult.sidedSuccess(this.level().isClientSide);
      }
    }
  }

  @Override
  protected void playStepSound(BlockPos p_28864_, BlockState p_28865_) {
    this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 1.0F);
  }

  public boolean isPlayerCreated() {
    return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
  }

  public void setPlayerCreated(boolean p_28888_) {
    byte b0 = this.entityData.get(DATA_FLAGS_ID);
    if (p_28888_) {
      this.entityData.set(DATA_FLAGS_ID, (byte) (b0 | 1));
    } else {
      this.entityData.set(DATA_FLAGS_ID, (byte) (b0 & -2));
    }
  }

  @Override
  public void die(DamageSource p_28846_) {
    super.die(p_28846_);
  }

  @Override
  public boolean checkSpawnObstruction(LevelReader p_28853_) {
    BlockPos blockpos = this.blockPosition();
    BlockPos blockpos1 = blockpos.below();
    BlockState blockstate = p_28853_.getBlockState(blockpos1);
    if (!blockstate.entityCanStandOn(p_28853_, blockpos1, this)) {
      return false;
    } else {
      for (int i = 1; i < 3; ++i) {
        BlockPos blockpos2 = blockpos.above(i);
        BlockState blockstate1 = p_28853_.getBlockState(blockpos2);
        if (!NaturalSpawner.isValidEmptySpawnBlock(
            p_28853_, blockpos2, blockstate1, blockstate1.getFluidState(), EntityType.IRON_GOLEM)) {
          return false;
        }
      }

      return NaturalSpawner.isValidEmptySpawnBlock(
              p_28853_,
              blockpos,
              p_28853_.getBlockState(blockpos),
              Fluids.EMPTY.defaultFluidState(),
              EntityType.IRON_GOLEM)
          && p_28853_.isUnobstructed(this);
    }
  }

  @Override
  public @NotNull Vec3 getLeashOffset() {
    return new Vec3(
        0.0, (double) (0.875F * this.getEyeHeight()), (double) (this.getBbWidth() * 0.4F));
  }

  public static enum Crackiness {
    NONE(1.0F),
    LOW(0.75F),
    MEDIUM(0.5F),
    HIGH(0.25F);

    private static final List<EnderiteGolem.Crackiness> BY_DAMAGE =
        Stream.of(values())
            .sorted(Comparator.comparingDouble(p_28904_ -> (double) p_28904_.fraction))
            .collect(ImmutableList.toImmutableList());
    private final float fraction;

    private Crackiness(float p_28900_) {
      this.fraction = p_28900_;
    }

    public static EnderiteGolem.Crackiness byFraction(float p_28902_) {
      for (EnderiteGolem.Crackiness irongolem$crackiness : BY_DAMAGE) {
        if (p_28902_ < irongolem$crackiness.fraction) {
          return irongolem$crackiness;
        }
      }

      return NONE;
    }
  }
}
