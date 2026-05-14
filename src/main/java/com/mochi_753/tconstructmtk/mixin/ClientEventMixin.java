package com.mochi_753.tconstructmtk.mixin;

import com.mochi_753.tconstructmtk.common.registry.TConstructMTKModifiers;
import com.takoy3466.manaitamtk.KeyMapping.MTKKeyMappings;
import com.takoy3466.manaitamtk.ManaitaMTK;
import com.takoy3466.manaitamtk.capability.MTKCapabilities;
import com.takoy3466.manaitamtk.event.ForgeBusClientEvent;
import com.takoy3466.manaitamtk.event.MTKEventHelper;
import com.takoy3466.manaitamtk.screen.MTKSwitcherScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.awt.*;

@Mixin(value = ForgeBusClientEvent.class, remap = false)
public class ClientEventMixin {
    @Inject(
            at = @At("HEAD"),
            method = "onKeyInput"
    )
    private static void onKeyInputWithMTKTool(InputEvent.Key event, CallbackInfo ci){
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) return;
        Item item = minecraft.player.getMainHandItem().getItem();

        boolean hasItem = item instanceof IModifiable && ToolStack.from(minecraft.player.getMainHandItem()).getModifierLevel(TConstructMTKModifiers.MTK_MODIFIER.get()) > 0;
        if (hasItem){
            if (MTKKeyMappings.MTKSwitcherOpenKey.matches(event.getKey(), event.getScanCode())) {
                if (event.getAction() == GLFW.GLFW_PRESS) {
                    minecraft.setScreen(new MTKSwitcherScreen());
                } else if (event.getAction() == GLFW.GLFW_RELEASE) {
                    if (minecraft.screen instanceof MTKSwitcherScreen) {
                        minecraft.setScreen(null);
                    }
                }
            }
        }

    }

    @Inject(
            at = @At("HEAD"),
            method = "onRenderGuiOverlayEvent",
            cancellable = true
    )
    private static void renderGuiOverlayLoosely(RenderGuiOverlayEvent event, CallbackInfo ci) {
        ResourceLocation FLAME_TEXTURE = ResourceLocation.fromNamespaceAndPath(ManaitaMTK.MOD_ID, "textures/gui/switch_flame.png");
        Component SWORD_TEXT_ENEMY = Component.translatable("gui.overlay.sword.enemy_die");
        Component SWORD_TEXT_ALL = Component.translatable("gui.overlay.sword.all_die");

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        GuiGraphics graphics = event.getGuiGraphics();

        int FLAME_X = 10;
        int FLAME_Y = minecraft.getWindow().getGuiScaledHeight() - 10 - 24;

        if (player == null || minecraft.options.renderDebug) return;
        ItemStack stack = player.getMainHandItem();
        Item item = stack.getItem();

        if(item instanceof IModifiable && ToolStack.from(stack).getModifierLevel(TConstructMTKModifiers.MTK_MODIFIER.get()) > 0){
            MTKEventHelper.execute(MTKCapabilities.RANGE_BREAK, stack, (cap) -> {
                MTKSwitcherScreen.MTKIcon mtkIcon = MTKSwitcherScreen.MTKIcon.getFromRange(cap.getRange());
                graphics.blit(FLAME_TEXTURE , FLAME_X, FLAME_Y , 0, 0 , 24, 24 , 24, 24);
                graphics.renderItem(mtkIcon.getRenderStack(), FLAME_X + 4, FLAME_Y + 4);
            });

            MTKEventHelper.execute(MTKCapabilities.KILL_SWORD, stack, (cap) -> {
                boolean killTarget = cap.isKillAll();
                int xSword = minecraft.getWindow().getGuiScaledWidth() / 2 - minecraft.font.width(killTarget? SWORD_TEXT_ALL.getString() : SWORD_TEXT_ENEMY.getString()) / 2;
                int ySword = minecraft.getWindow().getGuiScaledHeight() - 49 - minecraft.font.lineHeight;
                graphics.drawString(minecraft.font, killTarget? SWORD_TEXT_ALL : SWORD_TEXT_ENEMY, xSword, ySword, killTarget? Color.RED.getRGB() : Color.GRAY.getRGB());
            });

            ci.cancel();
        }
    }
}
