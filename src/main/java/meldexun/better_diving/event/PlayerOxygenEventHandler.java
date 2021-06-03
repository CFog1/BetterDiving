package meldexun.better_diving.event;

import meldexun.better_diving.BetterDiving;
import meldexun.better_diving.api.event.PlayerSuffocateEvent;
import meldexun.better_diving.capability.oxygen.entity.CapabilityOxygenProvider;
import meldexun.better_diving.config.BetterDivingConfig;
import meldexun.better_diving.entity.EntitySeamoth;
import meldexun.better_diving.network.packet.server.SPacketSyncOxygen;
import meldexun.better_diving.util.BetterDivingHelper;
import meldexun.better_diving.util.DivingGearHelper;
import meldexun.better_diving.util.OxygenPlayerHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = BetterDiving.MOD_ID)
public class PlayerOxygenEventHandler {

	@SubscribeEvent
	public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
		if (event.phase == Phase.START) {
			return;
		}
		PlayerEntity player = event.player;
		if (!BetterDivingConfig.SERVER_CONFIG.oxygenChanges.get()) {
			if (!player.world.isRemote && player.getRidingEntity() instanceof EntitySeamoth) {
				player.setAir(Math.min(player.getAir() + 5, player.getMaxAir()));
			}
			return;
		}
		player.getCapability(CapabilityOxygenProvider.OXYGEN).ifPresent(cap -> {
			cap.setOxygen(cap.getOxygen());
			if (!player.world.isRemote) {
				if (!BetterDivingHelper.canBreath(player)) {
					int oxygenUsage = 1;

					if (BetterDivingConfig.SERVER_CONFIG.oxygen.oxygenEfficiency.get()) {
						int blocksUnderWater = BetterDivingHelper.blocksUnderWater(player);
						int maxDivingDepth = DivingGearHelper.getMaxDivingDepth(player);
						if (blocksUnderWater > maxDivingDepth) {
							oxygenUsage += 1 + (blocksUnderWater - maxDivingDepth) / BetterDivingConfig.SERVER_CONFIG.oxygen.oxygenEfficiencyRate.get();
						}
					}

					OxygenPlayerHelper.extractOxygenRespectEquipment(player, oxygenUsage);
				} else {
					OxygenPlayerHelper.receiveOxygenRespectEquipment(player, 25);
				}

				player.setAir((int) (OxygenPlayerHelper.getOxygenRespectEquipmentInPercent(player) * player.getMaxAir()));

				if (cap.getOxygen() <= -20) {
					cap.setOxygen(0);

					if (!player.world.isRemote && !MinecraftForge.EVENT_BUS.post(new PlayerSuffocateEvent(player))) {
						((ServerWorld) player.world).spawnParticle(ParticleTypes.BUBBLE, player.getPosX(), player.getPosY() + player.getHeight() * 0.5D, player.getPosZ(), 8, 0.25D, 0.25D, 0.25D, 0.0D);

						player.attackEntityFrom(DamageSource.DROWN, 2.0F);
					}
				}
			}

			if (!player.world.isRemote) {
				BetterDiving.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SPacketSyncOxygen(cap.getOxygen()));
			}
		});
	}

}
