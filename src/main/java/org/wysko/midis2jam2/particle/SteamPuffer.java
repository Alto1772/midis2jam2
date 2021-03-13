package org.wysko.midis2jam2.particle;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.wysko.midis2jam2.Midis2jam2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The red, blue, white, and brown substances that emanate from the shaft of an instrument.
 */
public class SteamPuffer implements ParticleGenerator {
	
	public final Node steamPuffNode = new Node();
	final List<Cloud> clouds = new ArrayList<>();
	private final Midis2jam2 context;
	private final SteamPuffType type;
	
	public SteamPuffer(Midis2jam2 context, SteamPuffType type) {
		this.context = context;
		this.type = type;
	}
	
	private void despawnCloud(Cloud cloud) {
		steamPuffNode.detachChild(cloud.cloud);
	}
	
	@Override
	public void tick(double time, float delta, boolean active) {
		if (active) {
			// Spawn clouds
			double numberOfCloudsToSpawn = (delta / (1f / 60f));
			if (numberOfCloudsToSpawn < 1) {
				if (new Random().nextDouble() < numberOfCloudsToSpawn) {
					numberOfCloudsToSpawn = 1;
				} else {
					numberOfCloudsToSpawn = 0;
				}
			}
			for (int i = 0; i < Math.ceil(numberOfCloudsToSpawn); i++) {
				Cloud cloud = new Cloud();
				clouds.add(cloud);
				steamPuffNode.attachChild(cloud.cloud);
			}
		}
		
		for (Cloud cloud : clouds) {
			if (cloud != null)
				cloud.tick(delta);
		}
	}
	
	@SuppressWarnings("unused")
	public enum SteamPuffType {
		NORMAL("SteamPuff.bmp"),
		HARMONICA("SteamPuff_Harmonica.bmp"),
		POP("SteamPuff_Pop.bmp"),
		WHISTLE("SteamPuff_Whistle.bmp");
		String filename;
		
		SteamPuffType(String filename) {
			this.filename = filename;
		}
	}
	
	class Cloud implements Particle {
		final Node cloud = new Node();
		final float randY;
		final float randZ;
		double life = 0;
		
		public Cloud() {
			Random random = new Random();
			randY = (random.nextFloat() - 0.5f) * 1.5f;
			randZ = (random.nextFloat() - 0.5f) * 1.5f;
			Spatial cube = SteamPuffer.this.context.loadModel("SteamCloud.obj", type.filename, Midis2jam2.MatType.UNSHADED);
			cube.setLocalRotation(new Quaternion().fromAngles(new float[] {
					random.nextFloat() * FastMath.TWO_PI,
					random.nextFloat() * FastMath.TWO_PI,
					random.nextFloat() * FastMath.TWO_PI,
			}));
			cloud.attachChild(cube);
		}
		
		@Override
		public void tick(float delta) {
			cloud.setLocalTranslation(locEase(life) * 6, locEase(life) * randY, locEase(life) * randZ);
			cloud.setLocalScale((float) (0.75 * life + 1.2));
			life += delta * 1.5;
			double END_OF_LIFE = 0.7;
			if (life > END_OF_LIFE) despawn();
		}
		
		@Override
		public void despawn() {
			SteamPuffer.this.despawnCloud(this);
		}
		
		private float locEase(double x) {
			return (float) (1 - Math.pow(1 - x, 4));
		}
		
	}
}