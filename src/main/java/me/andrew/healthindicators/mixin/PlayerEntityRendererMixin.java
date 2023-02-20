package me.andrew.healthindicators.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.andrew.healthindicators.HealthIndicatorsMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PlayerEntityRenderer.class})
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = {"render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"}, at = {@At("RETURN")})
    public void renderHealth(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
        if (HealthIndicatorsMod.toggled && !abstractClientPlayerEntity.isMainPlayer()) {
            matrixStack.push();
            double dist = abstractClientPlayerEntity.squaredDistanceTo(MinecraftClient.getInstance().player);
            boolean dis = (dist < (HealthIndicatorsMod.aboba.mainScreen.value1 / 3.0F * HealthIndicatorsMod.aboba.mainScreen.value1 / 3.0F));
            if (dis) {
                RenderSystem.disableDepthTest();
            } else {
                RenderSystem.enableDepthTest();
            }

            double d = this.dispatcher.getSquaredDistanceToCamera(abstractClientPlayerEntity);

            matrixStack.translate(0, abstractClientPlayerEntity.getHeight() + 0.5f, 0);
            if (this.hasLabel(abstractClientPlayerEntity) && d <= 4096.0) {
                matrixStack.translate(0.0D, 9.0F * 1.15F * 0.025F, 0.0D);
                if (d < 100.0 && abstractClientPlayerEntity.getScoreboard().getObjectiveForSlot(2) != null) {
                    matrixStack.translate(0.0D, 9.0F * 1.15F * 0.025F, 0.0D);
                }
            }

            matrixStack.multiply(this.dispatcher.getRotation());

            float pixelSize = 0.025F;
            matrixStack.scale(pixelSize, pixelSize, pixelSize);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder vertexConsumer = tessellator.getBuffer();

            vertexConsumer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
            RenderSystem.enableDepthTest();

            Matrix4f model = matrixStack.peek().getPositionMatrix();

            int healthRed = MathHelper.ceil(abstractClientPlayerEntity.getHealth());
            int maxHealth = MathHelper.ceil(abstractClientPlayerEntity.getMaxHealth());
            int healthYellow = MathHelper.ceil(abstractClientPlayerEntity.getAbsorptionAmount());

            int heartsRed = MathHelper.ceil(healthRed / 2.0f);
            boolean lastRedHalf = (healthRed & 1) == 1;
            int heartsNormal = MathHelper.ceil(maxHealth / 2.0f);
            int heartsYellow = MathHelper.ceil(healthYellow / 2.0f);
            boolean lastYellowHalf = (healthYellow & 1) == 1;
            int heartsTotal = heartsNormal + heartsYellow;

            int pixelsTotal = heartsTotal * 8 + 1;
            float maxX = pixelsTotal / 2.0f;
            for (int heart = 0; heart < heartsTotal; heart++){
                float x = maxX - heart * 8;
                drawHeart(model, vertexConsumer, x, 0);
                // Offset in the gui icons texture in hearts
                // 0 - empty, 2 - red, 8 - yellow, +1 for half
                int type;
                if (heart < heartsRed) {
                    type = 2 * 2;
                    if (heart == heartsRed - 1 && lastRedHalf) type += 1;
                } else if (heart < heartsNormal) {
                    type = 0;
                } else {
                    type = 8 * 2;
                    if(heart == heartsTotal - 1 && lastYellowHalf) type += 1;
                }
                if (type != 0) {
                    drawHeart(model, vertexConsumer, x, type);
                }
            }

            tessellator.draw();

            matrixStack.pop();
            if (dis)
                RenderSystem.enableDepthTest();
        }
    }

    private static void drawHeart(Matrix4f model, VertexConsumer vertexConsumer, float x, int type) {
        float minU = 0.0625F + type * 9.0F / 256.0F;
        float maxU = minU + 0.03515625F;
        float minV = 0.0F;
        float maxV = minV + 0.03515625F;
        int y = HealthIndicatorsMod.aboba.mainScreen.value / 5 - 10;
        float heartSize = 9.0F;
        drawVertex(model, vertexConsumer, x, y - heartSize, 0.0F, minU, maxV);
        drawVertex(model, vertexConsumer, x - heartSize, y - heartSize, 0.0F, maxU, maxV);
        drawVertex(model, vertexConsumer, x - heartSize, y, 0.0F, maxU, minV);
        drawVertex(model, vertexConsumer, x, y, 0.0F, minU, minV);
    }

    private static void drawVertex(Matrix4f model, VertexConsumer vertices, float x, float y, float z, float u, float v) {
        vertices.vertex(model, x, y, z).texture(u, v).next();
    }
}