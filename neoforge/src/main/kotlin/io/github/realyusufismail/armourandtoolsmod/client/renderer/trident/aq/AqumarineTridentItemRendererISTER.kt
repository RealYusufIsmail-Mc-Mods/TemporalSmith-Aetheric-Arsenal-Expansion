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
package io.github.realyusufismail.armourandtoolsmod.client.renderer.trident.aq

import io.github.realyusufismail.armourandtoolsmod.ArmourAndToolsMod
import io.github.realyusufismail.armourandtoolsmod.client.renderer.trident.ArmourToolsModTridentRendererISTER
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.resources.ResourceLocation

class AqumarineTridentItemRendererISTER : ArmourToolsModTridentRendererISTER() {
    override fun getTextureLocation(): ResourceLocation {
        return ArmourAndToolsMod.getModIdAndName("textures/entity/trident/aqumarine.png")
    }

    override fun getModelResourceLocation(): ModelResourceLocation {
        return ModelResourceLocation(
            ArmourAndToolsMod.getModIdAndName("aqumarine_trident"), "inventory")
    }
}
