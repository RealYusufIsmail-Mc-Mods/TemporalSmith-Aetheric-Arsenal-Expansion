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
package io.github.realyusufismail.armourandtoolsmod.worldgen

import io.github.realyusufismail.armourandtoolsmod.ArmourAndToolsMod.ArmorAndToolsMod.MOD_ID
import io.github.realyusufismail.armourandtoolsmod.core.init.BlockInit
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest

object ModConfiguredFeatures {

    val OVERWORLD_RUBY_ORE = registerKey("ruby_ore")
    val OVERWORLD_SAPPHIRE_ORE = registerKey("sapphire_ore")
    val OVERWORLD_GRAPHITE_ORE = registerKey("graphite_ore")
    val OVERWORLD_AQUMARINE_ORE = registerKey("aqumarine_ore")
    val OVERWORLD_RAINBOW_ORE = registerKey("rainbow_ore")
    val END_ENDERITE_ORE = registerKey("enderite_ore")

    fun bootstrap(context: BootstapContext<ConfiguredFeature<*, *>>) {
        val stoneReplaceables: RuleTest = TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES)
        val deepslateReplaceables: RuleTest = TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
        val netherrackReplaceables: RuleTest = BlockMatchTest(Blocks.NETHERRACK)
        val endstoneReplaceables: RuleTest = BlockMatchTest(Blocks.END_STONE)

        val ruby =
            listOf<OreConfiguration.TargetBlockState>(
                OreConfiguration.target(
                    stoneReplaceables, BlockInit.RUBY_ORE.get().defaultBlockState()),
                OreConfiguration.target(
                    deepslateReplaceables, BlockInit.DEEPSLATE_RUBY_ORE.get().defaultBlockState()))

        val sapphire =
            listOf<OreConfiguration.TargetBlockState>(
                OreConfiguration.target(
                    stoneReplaceables, BlockInit.SAPPHIRE_ORE.get().defaultBlockState()),
                OreConfiguration.target(
                    deepslateReplaceables,
                    BlockInit.DEEPSLATE_SAPPHIRE_ORE.get().defaultBlockState()))

        val graphite =
            listOf<OreConfiguration.TargetBlockState>(
                OreConfiguration.target(
                    stoneReplaceables, BlockInit.GRAPHITE_ORE.get().defaultBlockState()),
                OreConfiguration.target(
                    deepslateReplaceables,
                    BlockInit.DEEPSLATE_GRAPHITE_ORE.get().defaultBlockState()))

        val aqumarine =
            listOf<OreConfiguration.TargetBlockState>(
                OreConfiguration.target(
                    stoneReplaceables, BlockInit.AQUMARINE_ORE.get().defaultBlockState()),
                OreConfiguration.target(
                    deepslateReplaceables,
                    BlockInit.DEEPSLATE_AQUMARINE_ORE.get().defaultBlockState()))

        val rainbow =
            listOf<OreConfiguration.TargetBlockState>(
                OreConfiguration.target(
                    stoneReplaceables, BlockInit.RAINBOW_ORE.get().defaultBlockState()),
                OreConfiguration.target(
                    deepslateReplaceables,
                    BlockInit.DEEPSLATE_RAINBOW_ORE.get().defaultBlockState()))

        val enderite =
            listOf<OreConfiguration.TargetBlockState>(
                OreConfiguration.target(
                    endstoneReplaceables, BlockInit.ENDERITE_ORE.get().defaultBlockState()))

        register(context, OVERWORLD_RUBY_ORE, Feature.ORE, OreConfiguration(ruby, 4))
        register(context, OVERWORLD_SAPPHIRE_ORE, Feature.ORE, OreConfiguration(sapphire, 3))
        register(context, OVERWORLD_GRAPHITE_ORE, Feature.ORE, OreConfiguration(graphite, 4))
        register(context, OVERWORLD_AQUMARINE_ORE, Feature.ORE, OreConfiguration(aqumarine, 4))
        register(context, OVERWORLD_RAINBOW_ORE, Feature.ORE, OreConfiguration(rainbow, 3))
        register(context, END_ENDERITE_ORE, Feature.ORE, OreConfiguration(enderite, 4))
    }

    private fun registerKey(name: String): ResourceKey<ConfiguredFeature<*, *>> {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation(MOD_ID, name))
    }

    private fun <FC : FeatureConfiguration, F : Feature<FC>> register(
        context: BootstapContext<ConfiguredFeature<*, *>>,
        key: ResourceKey<ConfiguredFeature<*, *>>,
        feature: F,
        configuration: FC
    ) {
        context.register(key, ConfiguredFeature(feature, configuration))
    }
}
