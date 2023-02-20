package me.andrew.healthindicators;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;

public class MainScreen extends Screen {
    public static final Identifier box = new Identifier(HealthIndicatorsMod.MOD_ID, "textures/box.png");
    public static final Identifier box1 = new Identifier(HealthIndicatorsMod.MOD_ID, "textures/box1.png");
    public static final Identifier bar = new Identifier(HealthIndicatorsMod.MOD_ID, "textures/bar.png");
    public static final Identifier bebra = new Identifier(HealthIndicatorsMod.MOD_ID, "textures/bebra.png");
    public static MainScreen INSTANCE;
    public int value;
    public int value1;
    boolean dragged;
    boolean dragged1;
    int barX;
    int barY;
    int bar1x;
    int bar1y;
    int barWidth = 200;
    int barHeight = 24;

    protected MainScreen() {
        super(Text.of("bebra"));
        INSTANCE = this;
        value = 100;
        value1 = 100;
    }

    @Override
    protected void init() {
        barX = (width / 2) - 100;
        barY = (height / 2) + 15;
        bar1x = barX;
        bar1y = barY + 30;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        Healthbar.drawTexture(matrices, (width / 2) - 10, (height / 2) - 10, 20, 20, HealthIndicatorsMod.toggled ? box1 : box);
        Healthbar.drawTexture(matrices, barX, barY, barWidth, barHeight, bar);
        Healthbar.drawTexture(matrices, barX + value + 4, barY + 3, 8, 18, bebra);
        drawCenteredText(matrices, textRenderer, String.format("height: %.2f", (value / 5F)), width / 2, barY + 5, Color.white.getRGB());
        Healthbar.drawTexture(matrices, bar1x, bar1y, barWidth, barHeight, bar);
        Healthbar.drawTexture(matrices, bar1x + value1 + 4, bar1y + 3, 8, 18, bebra);
        drawCenteredText(matrices, textRenderer, String.format("distance: %.2f", (value1 / 3F)), width / 2, bar1y + 5, Color.white.getRGB());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (collision((width / 2) - 10, (height / 2) - 10, 20, 20, mouseX, mouseY)) {
            HealthIndicatorsMod.toggled = !HealthIndicatorsMod.toggled;
            if(MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.sendMessage(new LiteralText((HealthIndicatorsMod.toggled ? "Enabled" : "Disabled") + " Health Bar"), true);
            }
            Data.writeData();
        } else {
            dragged = collision(barX + 3, barY + 3, 194, 24, mouseX, mouseY);
            dragged1 = collision(bar1x + 3, bar1y + 3, 194, 24, mouseX, mouseY);
            if (collision(barX - 3 + value, barY + 3, 8, 24, mouseX, mouseY)) {
                dragged = true;
            } else if (dragged) {
                value = (int) (mouseX - barX) - 4;
                if (value > 186) value = 186;
                else if (value < 0) value = 0;
                Data.writeData();
            } else if (collision(bar1y - 3 + value1, bar1y + 3, 8, 24, mouseX, mouseY)) {
                dragged1 = true;
            } else if (dragged1) {
                value1 = (int) (mouseX - bar1x) - 4;
                if (value1 > 186) value1 = 186;
                else if (value1 < 0) value1 = 0;
                Data.writeData();
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    boolean collision(int x, int y, int width, int height, double mX, double mY) {
        return mX > x && mX < x + width && mY > y && mY < y + height;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragged) {
            value = (int) (mouseX - barX) - 4;
            if (value > 186) value = 186;
            else if (value < 0) value = 0;
            Data.writeData();
        } else if (dragged1) {
            value1 = (int) (mouseX - bar1x) - 4;
            if (value1 > 186) value1 = 186;
            else if (value1 < 0) value1 = 0;
            Data.writeData();
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
}
