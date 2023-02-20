package me.andrew.healthindicators;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class HealthIndicatorsMod implements ModInitializer {
    public static final String MOD_ID = "healthindicators";
    public static HealthIndicatorsMod aboba;
    public MainScreen mainScreen = new MainScreen();
    public static KeyBinding keyMenu;
    public static KeyBinding keyToggle;
    public static boolean toggled = true;

    @Override
    public void onInitialize() {
        aboba = this;
        keyToggle = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key." + MOD_ID + ".toggle",
                InputUtil.UNKNOWN_KEY.getCode(),
                "key.categories." + MOD_ID
        ));
        keyMenu = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key." + MOD_ID + ".menu",
                InputUtil.GLFW_KEY_K,
                "key.categories." + MOD_ID
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (keyToggle.wasPressed()) {
                toggled = !toggled;
                debug("Health Bar" + (toggled ? "Enabled" : "Disabled"));
            }
            if (keyMenu.wasPressed()) {
                if (client.player != null) {
                    client.setScreen(mainScreen);
                }
            }
        });
        Data.readData();
    }

    public static void debug(String message) {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.sendMessage(Text.of(message), false);
        }
    }
}
