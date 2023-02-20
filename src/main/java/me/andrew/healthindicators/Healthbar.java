package me.andrew.healthindicators;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Healthbar {
	public static void drawTexture(MatrixStack matrixStack, int x, int y, int width, int height, Identifier identifier) {
		RenderSystem.setShaderTexture(0, identifier);
		MinecraftClient.getInstance().getTextureManager().bindTexture(identifier);
		DrawableHelper.drawTexture(matrixStack, x, y, 0, 0, width, height, width, height);
	}

	public static void drawTexture(MatrixStack matrixStack, int x, int y, int u, int v, int width, int height, Identifier identifier) {
		RenderSystem.setShaderTexture(0, identifier);
		DrawableHelper.drawTexture(matrixStack, x, y, u, v, width, height, width, height);
	}

}
