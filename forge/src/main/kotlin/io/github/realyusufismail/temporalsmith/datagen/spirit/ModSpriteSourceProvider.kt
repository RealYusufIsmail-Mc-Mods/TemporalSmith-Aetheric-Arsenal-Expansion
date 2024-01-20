/*
 * Copyright 2024 RealYusufIsmail.
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
package io.github.realyusufismail.temporalsmith.datagen.spirit

import io.github.realyusufismail.temporalsmith.TemporalSmith
import io.github.realyusufismail.temporalsmith.TemporalSmith.TemporalSmith.MOD_ID
import java.util.*
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile
import net.minecraft.data.PackOutput
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.common.data.SpriteSourceProvider

/** Makes sure that the textures are loaded from the correct location. */
class ModSpriteSourceProvider(output: PackOutput, exFileHelper: ExistingFileHelper) :
    SpriteSourceProvider(output, exFileHelper, MOD_ID) {
    override fun addSources() {
        val blockAtlas = atlas(BLOCKS_ATLAS)
        blockAtlas.addSource(
            SingleFile(TemporalSmith.getModIdAndName("entity/shield/ruby"), Optional.empty()))

        blockAtlas.addSource(
            SingleFile(TemporalSmith.getModIdAndName("entity/shield/aqumarine"), Optional.empty()))

        blockAtlas.addSource(
            SingleFile(TemporalSmith.getModIdAndName("entity/shield/rainbow"), Optional.empty()))

        blockAtlas.addSource(
            SingleFile(TemporalSmith.getModIdAndName("entity/shield/sapphire"), Optional.empty()))

        blockAtlas.addSource(
            SingleFile(TemporalSmith.getModIdAndName("entity/shield/graphite"), Optional.empty()))
    }
}
