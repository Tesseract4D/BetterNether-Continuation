package paulevs.betternether.sounds;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SoundRegister
{
	public static final SoundEvent FLY_SOUND = new SoundEvent(new ResourceLocation("betternether", "mob.firefly.fly")).setRegistryName("bn_fly");
	public static final SoundEvent FLY_HURT = new SoundEvent(new ResourceLocation("betternether", "mob.firefly.hurt")).setRegistryName("bn_fly_hurt");
	public static final SoundEvent FLY_DEATH = new SoundEvent(new ResourceLocation("betternether", "mob.firefly.death")).setRegistryName("bn_fly_death");
	public static final SoundEvent FLY_SIT_AMBIENT = new SoundEvent(new ResourceLocation("betternether", "mob.firefly.sitambient")).setRegistryName("bn_fly_sitambient");
	
	public static void register()
	{
		ForgeRegistries.SOUND_EVENTS.register(FLY_SOUND);
		ForgeRegistries.SOUND_EVENTS.register(FLY_HURT);
		ForgeRegistries.SOUND_EVENTS.register(FLY_DEATH);
	}
}
