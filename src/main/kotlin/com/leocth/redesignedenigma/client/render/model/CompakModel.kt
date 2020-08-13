package com.leocth.redesignedenigma.client.render.model

import com.mojang.datafixers.util.Pair as DFPair
import net.fabricmc.fabric.api.renderer.v1.RendererAccess
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.model.*
import net.minecraft.client.render.model.json.ModelOverrideList
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.texture.Sprite
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.client.util.math.Vector3f
import net.minecraft.item.ItemStack
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockRenderView
import java.nio.ByteBuffer
import java.util.*
import java.util.function.Function
import java.util.function.Supplier

class CompakModel(id: Identifier, resourceManager: ResourceManager) : UnbakedModel, BakedModel, FabricBakedModel {

    private val spriteIds: MutableList<SpriteIdentifier> = mutableListOf()
    private val sprites: MutableList<Sprite> = mutableListOf()
    private val vertices = mutableListOf<Vector3f>()
    private val quads = mutableListOf<Quad>()
    private val tris = mutableListOf<Tri>()
    private lateinit var mesh: Mesh

    init {
        val resource = resourceManager.getResource(
            Identifier(
                id.namespace,
                "models/${id.path}.compak"
            )
        )

        //TODO parsing and stuff
        val buffer = ByteBuffer.wrap(resource.inputStream.readBytes())
        while (buffer.hasRemaining()) {
            when (buffer.get().toChar()) {
                'v' -> {
                    val x = buffer.float
                    val y = buffer.float
                    val z = buffer.float
                    vertices.add(Vector3f(x, y, z))
                    println(vertices)
                }
                's' -> {
                    var textureId = ""
                    var t: Char
                    do {
                        t = buffer.get().toChar()
                        if (t == ';')
                            break
                        textureId += t
                    } while (true)
                    println(textureId)
                    spriteIds.add(SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, Identifier(textureId)))
                }
                'q' -> {
                    val v0index = buffer.int
                    val v1index = buffer.int
                    val v2index = buffer.int
                    val v3index = buffer.int
                    val spriteId = buffer.int
                    val bakeFlags = buffer.int
                    quads.add(Quad(spriteId, v0index, v1index, v2index, v3index, bakeFlags))
                    println(quads)
                }
                't' -> {
                    val v0index = buffer.int
                    val v1index = buffer.int
                    val v2index = buffer.int
                    val spriteId = buffer.int
                    val bakeFlags = buffer.int
                    tris.add(Tri(spriteId, v0index, v1index, v2index, bakeFlags))
                    println(tris)
                }
            }
        }
    }

    override fun getModelDependencies(): MutableCollection<Identifier> = mutableListOf()

    override fun bake(
        loader: ModelLoader,
        textureGetter: Function<SpriteIdentifier, Sprite>,
        rotationContainer: ModelBakeSettings,
        modelId: Identifier
    ): BakedModel? {
        spriteIds.forEach {
            sprites.add(textureGetter.apply(it))
        }
        val renderer = RendererAccess.INSTANCE.renderer
        val builder = renderer.meshBuilder()
        val emitter = builder.emitter
        quads.forEach { quad ->
            // interpolation features?
            println(quad)
            emitter.pos(0, vertices[quad.v0i])
            emitter.pos(1, vertices[quad.v1i])
            emitter.pos(2, vertices[quad.v2i])
            emitter.pos(3, vertices[quad.v3i])
            emitter.spriteBake(0, sprites[quad.spriteId], quad.bakeFlags)
            emitter.spriteColor(0, -1, -1, -1, -1)
            emitter.emit()
        }
        tris.forEach { tri ->
            println(tri)
            emitter.pos(0, vertices[tri.v0i])
            emitter.pos(1, vertices[tri.v1i])
            emitter.pos(2, vertices[tri.v2i])
            emitter.spriteBake(0, sprites[tri.spriteId], tri.bakeFlags)
            emitter.spriteColor(0, 0, -1)
            emitter.spriteColor(1, 0, -1)
            emitter.spriteColor(2, 0, -1)
            emitter.emit()
        }
        mesh = builder.build()
        return this
    }

    override fun getTextureDependencies(
        unbakedModelGetter: Function<Identifier, UnbakedModel>,
        unresolvedTextureReferences: MutableSet<DFPair<String, String>>
    ): MutableCollection<SpriteIdentifier> = spriteIds

    /* vanilla stuff. dont bother */
    override fun getQuads(state: BlockState?, face: Direction?, random: Random?): MutableList<BakedQuad>? = null
    override fun getSprite(): Sprite = sprites[0]
    override fun useAmbientOcclusion(): Boolean = false
    override fun hasDepth(): Boolean = false
    override fun getTransformation(): ModelTransformation? = null
    override fun isSideLit(): Boolean = false
    override fun isBuiltin(): Boolean = false
    override fun getOverrides(): ModelOverrideList? = null
    override fun isVanillaAdapter(): Boolean = false
    /*      end vanilla stuff     */

    /* rendering api stuff */
    override fun emitItemQuads(stack: ItemStack, random: Supplier<Random>, context: RenderContext) {
        context.meshConsumer().accept(mesh)
    }

    override fun emitBlockQuads(
        blockView: BlockRenderView,
        blockState: BlockState,
        pos: BlockPos,
        random: Supplier<Random>,
        context: RenderContext
    ) {
        context.meshConsumer().accept(mesh)
    }
}

data class Quad(
    // things we need:
    // sprite id
    // 4 vertices = 12 floats
    // color
    // uv settings
    val spriteId: Int,
    val v0i: Int,
    val v1i: Int,
    val v2i: Int,
    val v3i: Int,
    val bakeFlags: Int
)

// short for 'triangle'.
data class Tri(
    val spriteId: Int,
    val v0i: Int,
    val v1i: Int,
    val v2i: Int,
    val bakeFlags: Int
)