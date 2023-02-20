package me.andrew.healthindicators;

import me.andrew.healthindicators.HealthIndicatorsMod;
import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Data {
    static Path minecraftDir = MinecraftClient.getInstance().getResourcePackDir().toPath().getParent();
    static Path topka = Paths.get(minecraftDir.toString(), "TopkaProduct");
    static Path settings = Paths.get(topka.toString(), "HealthBar.txt");
    public static void readData() {
        try {
            if (!Files.exists(topka))
                Files.createDirectory(topka);
            if (!Files.exists(settings)) {
                Files.createFile(settings);
                writeData();
                return;
            }
            BufferedReader reader = new BufferedReader(new FileReader(settings.toFile()));
            ArrayList<String> lines = new ArrayList<>();
            while (reader.ready()) {
                lines.add(reader.readLine());
            }
            reader.close();
            for (String line : lines) {
                String name = line.split(":")[0];
                String value = line.split(":")[1];
                if (name.equals("height")) {
                    HealthIndicatorsMod.aboba.mainScreen.value = Integer.parseInt(value);
                } else if (name.equals("enabled")) {
                    HealthIndicatorsMod.toggled = Boolean.parseBoolean(value);
                } else if (name.equals("distance")) {
                    HealthIndicatorsMod.aboba.mainScreen.value1 = Integer.parseInt(value);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {}
    }

    public static void writeData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(settings.toFile()))) {
            writer.write("height:" + HealthIndicatorsMod.aboba.mainScreen.value + "\n");
            writer.write("distance:" + HealthIndicatorsMod.aboba.mainScreen.value1 + "\n");
            writer.write("enabled:" + HealthIndicatorsMod.toggled);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
