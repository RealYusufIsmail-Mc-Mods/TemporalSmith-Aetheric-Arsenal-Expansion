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
package io.github.realyusufismail.armourandtoolsmod.core.init

import io.github.realyusufismail.armourandtoolsmod.ArmourAndToolsMod.ArmorAndToolsMod.MOD_ID
import io.github.realyusufismail.armourandtoolsmod.effect.WorthyEffect
import net.minecraft.world.effect.MobEffect
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.ForgeRegistries
import net.neoforged.neoforge.registries.RegistryObject

object MobEffectsInit {
    val MOB_EFFECTS: DeferredRegister<MobEffect> =
        DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MOD_ID)

    // WORTHY_EFFECT has no effect, it just an indication that the potion is worthy to use mjolnir
    val WORTHY_EFFECT: RegistryObject<WorthyEffect> =
        MOB_EFFECTS.register("worthy_effect") { WorthyEffect() }
}
