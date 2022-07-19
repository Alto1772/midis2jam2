/*
 * Copyright (C) 2022 Jacob Wysko
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */
package org.wysko.midis2jam2.instrument.family.chromaticpercussion

import com.jme3.math.ColorRGBA
import com.jme3.math.FastMath.PI
import com.jme3.math.Quaternion
import com.jme3.math.Vector3f
import com.jme3.scene.Geometry
import com.jme3.scene.Node
import com.jme3.scene.Spatial
import com.jme3.scene.Spatial.CullHint.Always
import org.wysko.midis2jam2.Midis2jam2
import org.wysko.midis2jam2.instrument.DecayedInstrument
import org.wysko.midis2jam2.instrument.family.percussion.drumset.PercussionInstrument
import org.wysko.midis2jam2.instrument.family.percussive.Stick.STRIKE_SPEED
import org.wysko.midis2jam2.instrument.family.percussive.Stick.handleStick
import org.wysko.midis2jam2.midi.MidiChannelSpecificEvent
import org.wysko.midis2jam2.midi.MidiNoteOnEvent
import org.wysko.midis2jam2.util.Utils.rad
import org.wysko.midis2jam2.world.Axis.X
import org.wysko.midis2jam2.world.GlowController
import kotlin.math.pow
import kotlin.math.sin
import org.wysko.midis2jam2.instrument.family.percussive.Stick.MAX_ANGLE as StickMAX_ANGLE

/** The base amplitude of the strike. */
private const val BASE_AMPLITUDE = 0.5

/** The speed the bell will wobble at. */
private const val WOBBLE_SPEED = 3

/** How quickly the bell will return to rest. */
private const val DAMPENING = 0.3

private val OFFSET_DIRECTION_VECTOR = Vector3f(-10f, 0f, -10f)

/** The tubular bells. */
class TubularBells(context: Midis2jam2, events: List<MidiChannelSpecificEvent>) : DecayedInstrument(context, events) {

    /** Each of the twelve bells. */
    private val bells = Array(12) { i ->
        Bell(i).apply {
            instrumentNode.attachChild(highestLevel)
        }
    }

    /** Contains the list of strikes for each of the 12 bells. */
    private val bellStrikes: Array<MutableList<MidiNoteOnEvent>> = Array(12) { ArrayList() }

    override fun tick(time: Double, delta: Float) {
        super.tick(time, delta)

        bells.forEachIndexed { index, it ->
            it.tick(delta)
            val handleStick = handleStick(
                context,
                it.malletNode,
                time,
                delta,
                bellStrikes[index],
                STRIKE_SPEED,
                StickMAX_ANGLE,
                X
            )
            if (handleStick.justStruck()) {
                it.recoilBell((handleStick.strike ?: return@forEachIndexed).velocity)
            }
        }
    }

    override fun moveForMultiChannel(delta: Float) {
        offsetNode.localTranslation = OFFSET_DIRECTION_VECTOR.mult(updateInstrumentIndex(delta))
    }

    /** A single bell. */
    private inner class Bell(i: Int) {

        /** The highest level node. */
        val highestLevel = Node()

        /** Contains the tubular bell. */
        val bellNode = Node().apply {
            setLocalTranslation((i - 5) * 4f, 0f, 0f)
            setLocalScale((-0.04545 * i).toFloat() + 1)
            highestLevel.attachChild(this)
        }

        /** Contains the mallet. */
        val malletNode: Node = Node().apply {
            setLocalTranslation((i - 5) * 4f, -25f, 4f)
            highestLevel.attachChild(this)
            cullHint = Always
        }.also {
            context.loadModel("TubularBellMallet.obj", "Wood.bmp").apply {
                setLocalTranslation(0f, 5f, 0f)
                it.attachChild(this)
            }
        }

        val bell: Spatial = context.loadModel("TubularBell.obj", "ShinySilver.bmp", 0.9f).also {
            bellNode.attachChild(it)
        }

        /** The current amplitude of the recoil. */
        private var amplitude = 0.5

        /** The current time of animation, or -1 if the animation has never started yet. */
        private var animTime = -1.0

        /** True if this bell is recoiling, false if not. */
        private var bellIsRecoiling = false

        private val glowController = GlowController()

        /**
         * Updates animation.
         *
         * @param delta the amount of time since the last frame update
         */
        fun tick(delta: Float) {
            animTime += delta.toDouble()

            if (bellIsRecoiling) {
                bellNode.localRotation = Quaternion().fromAngles(rotationAmount(), 0f, 0f)
                (bell as Geometry).material.setColor("GlowColor", glowController.calculate(animTime))
            } else {
                (bell as Geometry).material.setColor("GlowColor", ColorRGBA.Black)
            }
        }

        /**
         * Calculates the rotation during the recoil.
         *
         * @return the rotation amount
         */
        fun rotationAmount(): Float {
            if (animTime < 0) return 0f

            return if (animTime in 0.0..2.0) {
                (amplitude * (sin(animTime * WOBBLE_SPEED * PI) / (3 + animTime.pow(3.0) * WOBBLE_SPEED * DAMPENING * PI))).toFloat()
            } else {
                bellIsRecoiling = false
                0f
            }
        }

        /**
         * Recoils the bell.
         *
         * @param velocity the velocity of the MIDI note
         */
        fun recoilBell(velocity: Int) {
            amplitude = PercussionInstrument.velocityRecoilDampening(velocity) * BASE_AMPLITUDE
            animTime = 0.0
            bellIsRecoiling = true
        }
    }

    init {
        hits.forEach { bellStrikes[(it.note + 3) % 12].add(it) }
        instrumentNode.run {
            setLocalTranslation(-65f, 100f, -130f)
            localRotation = Quaternion().fromAngles(0f, rad(25.0), 0f)
        }
    }
}