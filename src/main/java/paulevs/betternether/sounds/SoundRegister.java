package paulevs.betternether.sounds;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SoundRegister
{
	public static final SoundEvent FLY_SOUND = registerBNSound("mob.firefly.fly");
	public static final SoundEvent FLY_HURT1 = registerBNSound("mob.firefly.hurt_one");
	public static final SoundEvent FLY_HURT2 = registerBNSound("mob.firefly.hurt_two");
	public static final SoundEvent FLY_DEATH = registerBNSound("mob.firefly.death");
	public static final SoundEvent FLY_SIT_AMBIENT = registerBNSound("mob.firefly.sitambient");
	
	public static void register()
	{
		ForgeRegistries.SOUND_EVENTS.register(FLY_SOUND);
		ForgeRegistries.SOUND_EVENTS.register(FLY_HURT1);
		ForgeRegistries.SOUND_EVENTS.register(FLY_HURT2);
		ForgeRegistries.SOUND_EVENTS.register(FLY_DEATH);
		ForgeRegistries.SOUND_EVENTS.register(FLY_SIT_AMBIENT);
	}
	
	private static SoundEvent registerBNSound(String name) {
		return new SoundEvent(new ResourceLocation("betterneter", name)).setRegistryName(name);
	}
}
