package meldexun.better_diving.util;

import meldexun.better_diving.config.BetterDivingConfig;
import meldexun.better_diving.oxygenprovider.DivingGearManager;
import meldexun.better_diving.oxygenprovider.DivingMaskProviderItem;
import meldexun.better_diving.oxygenprovider.MiningspeedProviderItem;
import meldexun.better_diving.oxygenprovider.SwimspeedProviderItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class DivingGearHelper {

	public static int getMaxDivingDepth(PlayerEntity player) {
		ItemStack stack = player.getItemBySlot(EquipmentSlotType.HEAD);
		DivingMaskProviderItem divingMaskProviderItem = DivingGearManager.getDivingMaskProviderItem(stack);
		int i = BetterDivingConfig.SERVER_CONFIG.oxygen.oxygenBaseDivingDepth.get();
		return divingMaskProviderItem != null ? Math.max(i, divingMaskProviderItem.maxDivingDepth) : i;
	}

	public static boolean isWearingDivingMask(PlayerEntity player) {
		ItemStack stack = player.getItemBySlot(EquipmentSlotType.HEAD);
		DivingMaskProviderItem divingMaskProviderItem = DivingGearManager.getDivingMaskProviderItem(stack);
		return divingMaskProviderItem != null;
	}

	public static double getSwimspeedBonus(ItemStack stack) {
		SwimspeedProviderItem swimspeedProviderItem = DivingGearManager.getSwimspeedProviderItem(stack);
		return swimspeedProviderItem != null ? swimspeedProviderItem.swimspeed : 0.0D;
	}

	public static double getSwimspeedBonus(PlayerEntity player) {
		double d = 0.0;
		for (EquipmentSlotType slot : EquipmentSlotType.values()) {
			d += getSwimspeedBonus(player.getItemBySlot(slot));
		}
		return d;
	}

	public static double getMiningspeedBonus(ItemStack stack) {
		MiningspeedProviderItem miningspeedProviderItem = DivingGearManager.getMiningspeedProviderItem(stack);
		return miningspeedProviderItem != null ? miningspeedProviderItem.miningspeed : 0.0D;
	}

	public static double getMiningspeedBonus(PlayerEntity player) {
		double d = 0.0;
		for (EquipmentSlotType slot : EquipmentSlotType.values()) {
			d += getMiningspeedBonus(player.getItemBySlot(slot));
		}
		return d;
	}

}
