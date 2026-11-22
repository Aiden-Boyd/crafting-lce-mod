package dev.legacycraft.client.mixin;

import dev.legacycraft.client.ClassicCraftingScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Map;

/** Replace only the vanilla crafting table screen factory. */
@Mixin(MenuScreens.class)
public abstract class MenuScreensMixin {
    @Shadow @Final private static Map<MenuType<?>, MenuScreens.ScreenConstructor<?, ?>> SCREENS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void legacyclassiccrafting$register(CallbackInfo ci) {
        SCREENS.put(MenuType.CRAFTING,
                (MenuScreens.ScreenConstructor<CraftingMenu, AbstractContainerScreen<CraftingMenu>>) ClassicCraftingScreen::new);
    }
}
