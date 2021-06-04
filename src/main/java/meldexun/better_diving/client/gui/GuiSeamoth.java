package meldexun.better_diving.client.gui;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import meldexun.better_diving.BetterDiving;
import meldexun.better_diving.client.util.BetterDivingGuiHelper;
import meldexun.better_diving.config.BetterDivingConfig;
import meldexun.better_diving.entity.EntitySeamoth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GuiSeamoth {

	@SuppressWarnings("deprecation")
	public static void render(MatrixStack matrixStack) {
		Minecraft mc = Minecraft.getInstance();
		TextureManager textureManager = mc.getTextureManager();
		FontRenderer fontRenderer = mc.font;
		EntitySeamoth seamoth = ((EntitySeamoth) mc.player.getVehicle());

		boolean blend = GL11.glGetBoolean(GL11.GL_BLEND);
		GlStateManager._enableBlend();
		GlStateManager._blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		GlStateManager._alphaFunc(GL11.GL_GREATER, 0.00390625F);

		int width = 81;
		int height = 37;
		int offsetX = BetterDivingGuiHelper.getAnchorX(width, BetterDivingConfig.CLIENT_CONFIG.seamothGuiAnchor.get(), BetterDivingConfig.CLIENT_CONFIG.seamothGuiOffsetX.get());
		int offsetY = BetterDivingGuiHelper.getAnchorY(height, BetterDivingConfig.CLIENT_CONFIG.seamothGuiAnchor.get(), BetterDivingConfig.CLIENT_CONFIG.seamothGuiOffsetY.get());

		textureManager.bind(new ResourceLocation(BetterDiving.MOD_ID, "textures/gui/seamoth_background.png"));
		BetterDivingGuiHelper.drawTexture(offsetX, offsetY, 0.0D, 0.0D, width, height, width / 128.0D, height / 64.0D);

		int energy = MathHelper.ceil((double) seamoth.getEnergy() / (double) seamoth.getEnergyCapacity() * 100.0D);
		int health = 100;
		int temperature = Math.round(20.0F * mc.level.getBiome(mc.player.blockPosition()).getTemperature(mc.player.blockPosition()));

		String s1 = Integer.toString(energy);
		fontRenderer.draw(matrixStack, s1, offsetX + 67 - fontRenderer.width(s1) / 2, offsetY + 5, 0xFFFFFF);
		String s2 = Integer.toString(health);
		fontRenderer.draw(matrixStack, s2, offsetX + 28 - fontRenderer.width(s2) / 2, offsetY + 13, 0xFFFFFF);

		String s3 = Integer.toString(temperature);
		fontRenderer.draw(matrixStack, s3, offsetX + 62 - fontRenderer.width(s3) / 2, offsetY + 24, 0xFFFFFF);
		String s4 = "\u00B0C";
		fontRenderer.draw(matrixStack, s4, offsetX + 73 - fontRenderer.width(s4) / 2, offsetY + 24, 0xF6DC47);

		GlStateManager._alphaFunc(GL11.GL_GREATER, 0.1F);
		if (!blend) {
			GL11.glDisable(GL11.GL_BLEND);
		}
	}

}
