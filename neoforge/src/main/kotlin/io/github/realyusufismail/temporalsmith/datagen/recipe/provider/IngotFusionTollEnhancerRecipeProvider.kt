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
package io.github.realyusufismail.temporalsmith.datagen.recipe.provider

import io.github.realyusufismail.temporalsmith.blocks.infusion.book.IngotFusionTollEnhancerRecipeBookCategory
import io.github.realyusufismail.temporalsmith.core.init.ItemInit
import io.github.realyusufismail.temporalsmith.datagen.recipe.MainModRecipeProvider
import io.github.realyusufismail.temporalsmith.recipe.ingot.builder.IngotFusionTollEnhancerRecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.world.item.ItemStack

class IngotFusionTollEnhancerRecipeProvider(
    private val mainModRecipeProvider: MainModRecipeProvider,
    private val pWriter: RecipeOutput
) : MainModRecipeProvider(mainModRecipeProvider) {

    fun build() {
        IngotFusionTollEnhancerRecipeBuilder.builder(
                IngotFusionTollEnhancerRecipeBookCategory.TOOL,
                ItemInit.IMPERIUM.get(),
                ItemInit.IMPERIUM_PICKAXE.get(),
                ItemInit.IMPERIUM.get(),
                ItemStack(ItemInit.MAGMA_STRIKE_PICKAXE.get()))
            .unlockedBy("has_imperium_ingot", has(ItemInit.IMPERIUM.get()))
            .save(pWriter, modId("magma_strike_pickaxe"))
    }
}
