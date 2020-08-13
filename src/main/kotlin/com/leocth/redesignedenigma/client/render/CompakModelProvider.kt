package com.leocth.redesignedenigma.client.render

import com.leocth.redesignedenigma.client.render.model.CompakModel
import net.fabricmc.fabric.api.client.model.ModelProviderContext
import net.fabricmc.fabric.api.client.model.ModelResourceProvider
import net.minecraft.client.render.model.UnbakedModel
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier

class CompakModelProvider(
    private val effectiveNamespace: String,
    private val resourceManager: ResourceManager
): ModelResourceProvider {

    private val model2UnbakedModel = mutableMapOf<Identifier, CompakModel>()

    fun registerModel(path: String): CompakModelProvider {
        val id = Identifier(effectiveNamespace, path)
        model2UnbakedModel[id] = CompakModel(id, resourceManager)
        return this
    }

    fun registerBlockModel(name: String): CompakModelProvider = registerModel("block/$name")
    fun registerItemModel(name: String): CompakModelProvider = registerModel("item/$name")

    override fun loadModelResource(id: Identifier, context: ModelProviderContext): UnbakedModel? {
        return if (id.namespace == effectiveNamespace) {
            model2UnbakedModel[id]
        } else null
    }
}