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
package io.github.realyusufismail.armourandtoolsmod.integration.tool

import io.github.realyusufismail.armourandtoolsmod.ArmourAndToolsMod
import io.github.realyusufismail.armourandtoolsmod.blocks.CustomToolCraftingTable
import io.github.realyusufismail.armourandtoolsmod.core.init.BlockInit
import io.github.realyusufismail.armourandtoolsmod.integration.ArmourAndToolsModJEIPlugin
import io.github.realyusufismail.armourandtoolsmod.integration.generic.GenericCraftingTableJEIRecipeCategory
import io.github.realyusufismail.armourandtoolsmod.recipe.tool.CustomToolCraftingTableRecipe
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.ingredient.ICraftingGridHelper
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeIngredientRole
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension
import net.minecraft.network.chat.Component

/**
 * @see ICraftingCategoryExtension
 * @see CraftingGridHelper
 * @see ICraftingGridHelper
 */
class CustomToolCraftingTableJEIRecipeCategory(private val guiHelper: IGuiHelper) :
    GenericCraftingTableJEIRecipeCategory<CustomToolCraftingTableRecipe>(
        guiHelper, BlockInit.CUSTOM_TOOL_CRAFTING_TABLE.get()) {

    override fun getRecipeType(): RecipeType<CustomToolCraftingTableRecipe> {
        return ArmourAndToolsModJEIPlugin.toolCraftingTableRecipeType
    }

    override fun getTitle(): Component {
        return CustomToolCraftingTable.containerTitle
    }

    override fun getBackground(): IDrawable {
        return this.bg
    }

    override fun getIcon(): IDrawable {
        return this.ic
    }

    /**
     * Y is up and down X is left and right
     *
     * Increase Y to down and decrease Y to up Increase X to right and decrease X to left
     */
    override fun setRecipe(
        builder: IRecipeLayoutBuilder,
        recipe: CustomToolCraftingTableRecipe,
        focuses: IFocusGroup
    ) {
        builder.addSlot(RecipeIngredientRole.INPUT, 30, 16).addIngredients(recipe.ingredients[0])
        builder.addSlot(RecipeIngredientRole.INPUT, 47, 16).addIngredients(recipe.ingredients[1])
        builder.addSlot(RecipeIngredientRole.INPUT, 65, 16).addIngredients(recipe.ingredients[2])
        builder.addSlot(RecipeIngredientRole.INPUT, 30, 34).addIngredients(recipe.ingredients[3])
        builder.addSlot(RecipeIngredientRole.INPUT, 48, 34).addIngredients(recipe.ingredients[4])
        builder.addSlot(RecipeIngredientRole.INPUT, 66, 34).addIngredients(recipe.ingredients[5])
        builder.addSlot(RecipeIngredientRole.INPUT, 30, 52).addIngredients(recipe.ingredients[6])
        builder.addSlot(RecipeIngredientRole.INPUT, 48, 52).addIngredients(recipe.ingredients[7])
        builder.addSlot(RecipeIngredientRole.INPUT, 66, 52).addIngredients(recipe.ingredients[8])

        builder.addSlot(RecipeIngredientRole.OUTPUT, 124, 34).addItemStack(recipe.result)
    }

    companion object {
        val UID = ArmourAndToolsMod.getModIdAndName("custom_tool_crafting_table")
    }
}
