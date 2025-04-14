package logic.visualaudio;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.WeightedSoundSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VisualAudioClient implements ClientModInitializer {
	MinecraftClient client;
	public static final String MOD_ID = "visual-audio";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	SoundInstanceListener soundInstanceListener = new SoundInstanceListener() {
		@Override
		public void onSoundPlayed(SoundInstance sound, WeightedSoundSet soundSet, float range) {
			LOGGER.info("sound played: " + sound.getId().toString());
		}
	};

	@Override
	public void onInitializeClient() {
		LOGGER.info("Visual Audio Client Initialized Attempt 4");
		client = MinecraftClient.getInstance();
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			client.getSoundManager().registerListener(soundInstanceListener);
			LOGGER.info("Listener Registered");
		});
	}
}