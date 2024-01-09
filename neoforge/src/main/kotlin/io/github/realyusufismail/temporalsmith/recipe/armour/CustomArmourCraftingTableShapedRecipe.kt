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
package io.github.realyusufismail.temporalsmith.recipe.armour

import com.google.common.annotations.VisibleForTesting
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.github.realyusufismail.realyusufismailcore.recipe.util.EnchantmentsAndLevels
import io.github.realyusufismail.temporalsmith.blocks.armour.CustomArmourCraftingTableContainer
import io.github.realyusufismail.temporalsmith.blocks.armour.book.CustomArmourCraftingBookCategory
import io.github.realyusufismail.temporalsmith.core.init.BlockInit
import io.github.realyusufismail.temporalsmith.core.init.RecipeSerializerInit
import io.github.realyusufismail.temporalsmith.recipe.pattern.CustomCraftingTableRecipePattern
import net.minecraft.core.NonNullList
import net.minecraft.core.RegistryAccess
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.Level
import net.neoforged.neoforge.common.CommonHooks
import net.neoforged.neoforge.common.crafting.IShapedRecipe
import kotlin.math.max
import kotlin.math.min

class CustomArmourCraftingTableShapedRecipe(
    val gr: String,
    val recipeCategory: CustomArmourCraftingBookCategory,
    val recipePattern: CustomCraftingTableRecipePattern,
    override val result: ItemStack,
    val showN: Boolean,
    val enchantmentsAndLevels: EnchantmentsAndLevels,
    val hideFlags: Int,
) : CustomArmourCraftingTableRecipe, IShapedRecipe<CustomArmourCraftingTableContainer> {
    val width: Int = recipePattern.width
    val height: Int = recipePattern.height

    override fun matches(p_44176_: CustomArmourCraftingTableContainer, p_44177_: Level): Boolean {
        return this.recipePattern.matchesArmour(p_44176_)
    }

    override fun assemble(
        p_44001_: CustomArmourCraftingTableContainer,
        p_267165_: RegistryAccess,
    ): ItemStack {
        return this.result.copy()
    }

    override fun canCraftInDimensions(pWidth: Int, pHeight: Int): Boolean {
        return true
    }

    /**
     * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your
     * recipe has more than one possible result (e.g. it's dynamic and depends on its inputs), then
     * return an empty stack.
     */
    override fun getResultItem(p_267052_: RegistryAccess): ItemStack {
        return this.result
    }

    /** Recipes with equal group are combined into one button in the recipe book */
    override fun getGroup(): String {
        return this.gr
    }

    override fun getIngredients(): NonNullList<Ingredient> {
        return this.recipePattern.ingredients
    }

    override fun getToastSymbol(): ItemStack {
        return ItemStack(BlockInit.CUSTOM_ARMOUR_CRAFTING_TABLE.get())
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return RecipeSerializerInit.CUSTOM_ARMOUR_CRAFTER.get()
    }

    override fun showNotification(): Boolean {
        return showN
    }

    override fun getRecipeWidth(): Int {
        return this.width
    }

    override fun getRecipeHeight(): Int {
        return this.height
    }

    override fun category(): CustomArmourCraftingBookCategory {
        return recipeCategory
    }

    override fun isIncomplete(): Boolean {
        val nonnulllist: NonNullList<Ingredient> = this.ingredients
        return nonnulllist.isEmpty() ||
            nonnulllist
                .stream()
                .filter { ingredient: Ingredient -> !ingredient.isEmpty }
                .anyMatch { ingredient: Ingredient? -> CommonHooks.hasNoElements(ingredient) }
    }

    companion object {
        @JvmField var MAX_WIDTH = 3

        @JvmField var MAX_HEIGHT = 3
    }

    class Serializer : RecipeSerializer<CustomArmourCraftingTableShapedRecipe> {
        companion object {
            val CODEC: Codec<CustomArmourCraftingTableShapedRecipe> =
                RecordCodecBuilder.create { instance ->
                        instance
                            .group(
                                ExtraCodecs.strictOptionalField(Codec.STRING, "group", "")
                                    .forGetter(CustomArmourCraftingTableShapedRecipe::getGroup),
                                CustomArmourCraftingBookCategory.CODEC.fieldOf("category")
                                    .orElse(CustomArmourCraftingBookCategory.MISC)
                                    .forGetter(CustomArmourCraftingTableShapedRecipe::category),
                                CustomCraftingTableRecipePattern.MAP_CODEC.forGetter(
                                    CustomArmourCraftingTableShapedRecipe::recipePattern),
                                ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result")
                                    .forGetter(CustomArmourCraftingTableShapedRecipe::result),
                                ExtraCodecs.strictOptionalField(
                                        Codec.BOOL, "show_notification", true)
                                    .forGetter(
                                        CustomArmourCraftingTableShapedRecipe::showNotification),
                                EnchantmentsAndLevels.getCodec().fieldOf("enchantments")
                                    .forGetter(CustomArmourCraftingTableShapedRecipe::enchantmentsAndLevels),
                                Codec.INT.fieldOf("hide_flags").orElse(0)
                                    .forGetter(CustomArmourCraftingTableShapedRecipe::hideFlags)
                            )
                            .apply(instance, ::CustomArmourCraftingTableShapedRecipe)
                    }
                    .apply { stable() }
        }

        override fun codec(): Codec<CustomArmourCraftingTableShapedRecipe> {
            return CODEC
        }

        override fun toNetwork(
            pBuffer: FriendlyByteBuf,
            pRecipe: CustomArmourCraftingTableShapedRecipe,
        ) {
            pBuffer.writeVarInt(pRecipe.width)
            pBuffer.writeVarInt(pRecipe.height)
            pBuffer.writeUtf(pRecipe.gr)
            pRecipe.recipePattern.toNetwork(pBuffer)
            pRecipe.enchantmentsAndLevels.toNetwork(pBuffer)
            pBuffer.writeItem(pRecipe.result)
            pBuffer.writeBoolean(pRecipe.showN)
        }

        override fun fromNetwork(buffer: FriendlyByteBuf): CustomArmourCraftingTableShapedRecipe {
            val s = buffer.readUtf()
            val craftingbookcategory =
                buffer.readEnum(CustomArmourCraftingBookCategory::class.java)
            val shapedrecipepattern = CustomCraftingTableRecipePattern.fromNetwork(buffer)
            val itemstack = buffer.readItem()
            val flag = buffer.readBoolean()
            val enchantmentsAndLevels = buffer.readJsonWithCodec(EnchantmentsAndLevels.getCodec())
            val hideFlags = buffer.readInt()
            return CustomArmourCraftingTableShapedRecipe(
                s,
                craftingbookcategory,
                shapedrecipepattern,
                itemstack,
                flag,
                enchantmentsAndLevels,
                hideFlags
            )
        }
    }
}
