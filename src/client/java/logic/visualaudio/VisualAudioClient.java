package logic.visualaudio;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.WeightedSoundSet;
import org.jetbrains.annotations.NotNull;

public class VisualAudioClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		SoundInstanceListener soundInstanceListener = new SoundInstanceListener() {
			@Override
			public void onSoundPlayed(SoundInstance sound, WeightedSoundSet soundSet, float range) {
				VisualAudio.LOGGER.info("Sound played: " + sound.getId().toString());
			}
		};
	}
}