package logic.visualaudio;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.fabricmc.fabric.api.client.rendering.v1.LayeredDrawerWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class VisualAudioClient implements ClientModInitializer {
	MinecraftClient client;
	public static final String MOD_ID = "visual-audio";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	ArrayList<SoundEntry> sounds = new ArrayList<>();
	SoundInstanceListener soundInstanceListener = new SoundInstanceListener() {
		@Override
		public void onSoundPlayed(SoundInstance sound, WeightedSoundSet soundSet, float range) {
			if(client.world != null) {
				LOGGER.info("sound played: " + sound.getId().toString());
				sounds.add(new SoundEntry(sound, new Vec3d(sound.getX(), sound.getY(), sound.getZ()), Util.getMeasuringTimeMs()));
			}
		}
	};
	LayeredDrawerWrapper layeredDrawer;
	private static Identifier SOUND_LAYER = Identifier.of(MOD_ID, "sound_layer");
	SoundManager soundManager;
	@Override
	public void onInitializeClient() {
		LOGGER.info("Visual Audio Client Initialized Attempt 4");
		client = MinecraftClient.getInstance();
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			client.getSoundManager().registerListener(soundInstanceListener);
			soundManager = client.getSoundManager();
			LOGGER.info("Listener Registered");
		});
		HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> {
			layeredDrawer.attachLayerBefore(IdentifiedLayer.CHAT, SOUND_LAYER, this::render);
		});
	}

	private void render(DrawContext context, RenderTickCounter tickCounter) {
		if(client.world != null) {
			int x = client.getWindow().getScaledWidth() / 2;
			int y = 10;
			for(SoundEntry soundEntry : sounds) {
				SoundInstance sound = soundEntry.sound();
				long soundLife = Util.getMeasuringTimeMs() - soundEntry.time();
				if(soundLife < 5000) {
					context.drawText(client.textRenderer, sound.getId().toString(), x, y, 0xFFFFFFFF, false);
					y += 10;
				}
				else {
					int alpha = (255 - (int) ((soundLife - 5000) / 5000.0f * 255.0f));
					int color = ColorHelper.getArgb(alpha, 255, 255, 255);
					context.drawText(client.textRenderer, sound.getId().toString(), x, y, color, false);
					y += 10;
				}
			}
			cleanSoundEntries();
		}
	}

	private void cleanSoundEntries() {
		long l = Util.getMeasuringTimeMs();
		this.sounds.removeIf((sound) -> {
			return (double)(l - sound.time()) > 10000;
		});
	}

	@Environment(EnvType.CLIENT)
	static record SoundEntry(SoundInstance sound, Vec3d location, long time) {
		SoundEntry(SoundInstance sound, Vec3d location, long time) {
			this.location = location;
			this.time = time;
			this.sound = sound;
		}

		public Vec3d location() {
			return this.location;
		}

		public long time() {
			return this.time;
		}

		public SoundInstance sound() {
			return this.sound;
		}
	}
}