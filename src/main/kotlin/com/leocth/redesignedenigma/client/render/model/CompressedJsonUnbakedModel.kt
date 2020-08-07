package com.leocth.redesignedenigma.client.render.model

import com.google.common.collect.Lists
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import com.mojang.datafixers.util.Pair
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.ModelBakeSettings
import net.minecraft.client.render.model.ModelLoader
import net.minecraft.client.render.model.UnbakedModel
import net.minecraft.client.render.model.json.*
import net.minecraft.client.texture.Sprite
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.client.util.math.Vector3f
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.math.Direction
import java.nio.ByteBuffer
import java.util.*
import java.util.function.Function

class CompressedJsonUnbakedModel : UnbakedModel {
    override fun getModelDependencies(): MutableCollection<Identifier> {
        TODO("Not yet implemented")
    }

    override fun bake(
        loader: ModelLoader?,
        textureGetter: Function<SpriteIdentifier, Sprite>?,
        rotationContainer: ModelBakeSettings?,
        modelId: Identifier?
    ): BakedModel? {
        TODO("Not yet implemented")
    }

    override fun getTextureDependencies(
        unbakedModelGetter: Function<Identifier, UnbakedModel>?,
        unresolvedTextureReferences: MutableSet<Pair<String, String>>?
    ): MutableCollection<SpriteIdentifier> {
        TODO("Not yet implemented")
    }

    class Deserializer : JsonUnbakedModel.Deserializer() {

        override fun deserializeElements(
            context: JsonDeserializationContext,
            json: JsonObject
        ): List<ModelElement>? {
            val list: MutableList<ModelElement> = Lists.newArrayList()
            if (json.has("elements")) {

                val bytes = Base64.getDecoder().decode(JsonHelper.getString(json, "elements"))
                val buffer = ByteBuffer.wrap(bytes)


                var from: Vector3f? = null
                var to: Vector3f? = null
                val faces = mutableMapOf<Direction, ModelElementFace>()
                var rotation: ModelRotation? = null
                var shade = true
                val id2TextureId = mutableMapOf<Int, String>()

                loop@ while (buffer.hasRemaining()) {
                    when (buffer.char) {
                        'f' -> {
                            // from
                            if (from != null) {
                                throw IllegalStateException("from already set!")
                            }
                            val x = buffer.float
                            val y = buffer.float
                            val z = buffer.float
                            from = Vector3f(x, y, z)
                        }
                        't' -> {
                            // to
                            if (to != null) {
                                throw IllegalStateException("to already set!")
                            }
                            val x = buffer.float
                            val y = buffer.float
                            val z = buffer.float
                            to = Vector3f(x, y, z)
                        }
                        'x' -> {
                            // face
                            val dir = when (buffer.char) {
                                'n' -> Direction.NORTH
                                'e' -> Direction.EAST
                                's' -> Direction.SOUTH
                                'w' -> Direction.WEST
                                'u' -> Direction.UP
                                'd' -> Direction.DOWN
                                else -> throw IllegalStateException("faces[].name set to an illegal value!")
                            }
                            val cullFace = when (buffer.char) {
                                'n' -> Direction.NORTH
                                'e' -> Direction.EAST
                                's' -> Direction.SOUTH
                                'w' -> Direction.WEST
                                'u' -> Direction.UP
                                'd' -> Direction.DOWN
                                '@' -> null
                                else -> throw IllegalStateException("faces[].cullFace set to an illegal value!")
                            }
                            val tintIndex = buffer.int

                            val textureIdIndex = buffer.int

                            val uv = FloatArray(4)
                            uv[0] = buffer.float
                            uv[1] = buffer.float
                            uv[2] = buffer.float
                            uv[3] = buffer.float

                            // this theoretically allows values other than 0, 90, 180 and 360.
                            // im not sure whether it will actually work tho.
                            val rot = buffer.int

                            faces[dir] = ModelElementFace(
                                cullFace,
                                tintIndex,
                                id2TextureId[textureIdIndex],
                                ModelElementTexture(uv, rot)
                            )
                        }
                        'r' -> {
                            // rotation
                            val originX = buffer.float
                            val originY = buffer.float
                            val originZ = buffer.float
                            val origin = Vector3f(originX, originY, originZ)
                            val axis = when (buffer.char) {
                                'x' -> Direction.Axis.X
                                'y' -> Direction.Axis.Y
                                'z' -> Direction.Axis.Z
                                else -> throw IllegalStateException("rotation.axis set to an illegal value!")
                            }
                            val angle = buffer.float
                            val rescale = when (buffer.char) {
                                'y' -> true
                                'n' -> false
                                else -> throw IllegalStateException("rotation.rescale set to an illegal value!")
                            }
                            rotation = ModelRotation(origin, axis, angle, rescale)
                        }
                        'h' -> {
                            // disable shade
                            shade = false
                        }
                        '#' -> {
                            // texture
                            var textureId = ""
                            var t: Char
                            do {
                                t = buffer.char
                                if (t == '|')
                                    break
                                textureId += t
                            } while (true)
                            val index = buffer.int
                            id2TextureId[index] = textureId
                        }
                        'e' -> {
                            // end element
                            from ?: throw IllegalStateException("from not set!")
                            to ?: throw IllegalStateException("to not set!")
                            list.add(ModelElement(from, to, faces, rotation, shade))
                            from = null
                            to = null
                            faces.clear()
                            rotation = null
                            shade = true
                        }
                    }
                }

            }
            return list
        }

    }
}