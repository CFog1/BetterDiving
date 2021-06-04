package meldexun.better_diving.item;

import java.util.List;

import meldexun.better_diving.capability.inventory.item.CapabilityItemHandlerItem;
import meldexun.better_diving.capability.inventory.item.CapabilityItemHandlerItemProvider;
import meldexun.better_diving.entity.EntitySeamoth;
import meldexun.better_diving.init.BetterDivingEntities;
import meldexun.better_diving.init.BetterDivingItemGroups;
import meldexun.better_diving.init.BetterDivingItems;
import meldexun.better_diving.inventory.container.ContainerSeamothItem;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class ItemSeamoth extends Item {

	public ItemSeamoth() {
		super(new Item.Properties().stacksTo(1).tab(BetterDivingItemGroups.BETTER_DIVING));
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
		return new CapabilityItemHandlerItemProvider(() -> new CapabilityItemHandlerItem(stack, 1));
	}

	@Override
	public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
		if (this.allowdedIn(group)) {
			items.add(new ItemStack(this));
			ItemStack stack = new ItemStack(this);
			stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(c -> {
				((ItemStackHandler) c).setStackInSlot(0, new ItemStack(BetterDivingItems.POWER_CELL.get()));
			});
			items.add(stack);
		}
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (!worldIn.isClientSide()) {
			if (playerIn.isShiftKeyDown()) {
				playerIn.openMenu(new SimpleNamedContainerProvider((id, playerInv, player) -> {
					return new ContainerSeamothItem(id, playerInv, playerIn.getItemInHand(handIn), handIn);
				}, new TranslationTextComponent("Seamoth")));
			} else {
				EntitySeamoth seamoth = new EntitySeamoth(BetterDivingEntities.SEAMOTH.get(), worldIn);

				playerIn.getItemInHand(handIn).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(c -> {
					seamoth.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(c1 -> {
						c1.insertItem(0, c.getStackInSlot(0).copy(), false);
					});
				});

				Vector3d start = playerIn.getEyePosition(1.0F);
				Vector3d look = playerIn.getLookAngle();
				Vector3d end = start.add(look.scale(5.0D));
				RayTraceResult result = worldIn.clip(new RayTraceContext(start, end, BlockMode.COLLIDER, FluidMode.NONE, null));
				Vector3d vec = (result != null ? result.getLocation() : end).subtract(look);

				seamoth.setPos(vec.x, vec.y, vec.z);
				worldIn.addFreshEntity(seamoth);

				if (worldIn.getBlockState(seamoth.blockPosition()).getMaterial() == Material.WATER) {
					seamoth.playSound(SoundEvents.AMBIENT_UNDERWATER_ENTER, 1.0F, 0.9F + worldIn.random.nextFloat() * 0.2F);
				}

				if (!playerIn.isCreative()) {
					playerIn.getItemInHand(handIn).shrink(1);
				}
			}
		}
		return ActionResult.success(playerIn.getItemInHand(handIn));
	}

	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		ItemStack powerCell = ItemStack.EMPTY;
		LazyOptional<IItemHandler> cap = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
		if (cap.isPresent()) {
			powerCell = cap.orElseThrow(NullPointerException::new).getStackInSlot(0);
		}
		if (!powerCell.isEmpty() && powerCell.getItem() instanceof ItemPowerCell) {
			int energy = MathHelper.ceil(ItemEnergyStorage.getEnergyPercent(powerCell) * 100.0D);
			if (flagIn.isAdvanced()) {
				tooltip.add(new StringTextComponent(TextFormatting.GRAY + String.format("Energy %d%% (%d/%d)", energy, ItemEnergyStorage.getEnergy(powerCell), ItemEnergyStorage.getEnergyCapacity(powerCell))));
			} else {
				tooltip.add(new StringTextComponent(TextFormatting.GRAY + String.format("Energy %d%%", energy)));
			}
		} else {
			tooltip.add(new StringTextComponent(TextFormatting.GRAY + "No power cell"));
		}
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public void onCraftedBy(ItemStack stack, World worldIn, PlayerEntity playerIn) {
		super.onCraftedBy(stack, worldIn, playerIn);
		stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(c -> {
			((ItemStackHandler) c).setStackInSlot(0, new ItemStack(BetterDivingItems.POWER_CELL.get()));
		});
	}

}
