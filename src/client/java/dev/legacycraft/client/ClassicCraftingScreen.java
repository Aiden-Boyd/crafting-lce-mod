package dev.legacycraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ExtendedRecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Lightweight console-style categorized recipe browser at the crafting table.
 * Normal vanilla crafting slots and actions remain untouched.
 * This deliberately DOES NOT depend on Legacy4J, FactoryAPI or server-side code.
 *
 * v0.1: recipe browser beside the vanilla table (not a pixel-perfect Legacy4J port).
 */
public final class ClassicCraftingScreen extends CraftingScreen {
    private static final int PAGE_SIZE = 8;
    private static final String[] CATEGORY_NAMES = {"BUILD", "REDSTONE", "EQUIPMENT", "MISC"};
    private static final ExtendedRecipeBookCategory[] CATEGORIES = {
            RecipeBookCategories.CRAFTING_BUILDING_BLOCKS,
            RecipeBookCategories.CRAFTING_REDSTONE,
            RecipeBookCategories.CRAFTING_EQUIPMENT,
            RecipeBookCategories.CRAFTING_MISC
    };

    private final Inventory playerInventory;
    private int category = 0;
    private int page = 0;
    private boolean craftableOnly = false;
    private final List<RecipeDisplayEntry> available = new ArrayList<>();

    public ClassicCraftingScreen(CraftingMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.playerInventory = inventory;
    }

    @Override
    protected void init() {
        super.init();
        buildPanel();
    }

    private void buildPanel() {
        refreshRecipes();

        // Place the console-style recipe browser to the right of vanilla slots.
        int x = Math.min(this.width - 188, this.leftPos + this.imageWidth + 8);
        x = Math.max(4, x);
        int y = Math.max(5, this.topPos);
        int categoryWidth = 44;

        for (int i = 0; i < CATEGORY_NAMES.length; i++) {
            final int index = i;
            String caption = (category == i ? ">" : "") + CATEGORY_NAMES[i];
            this.addRenderableWidget(Button.builder(Component.literal(caption), b -> {
                category = index;
                page = 0;
                rebuildWidgets();
            }).bounds(x + i * 46, y, categoryWidth, 20).build());
        }

        this.addRenderableWidget(Button.builder(Component.literal(craftableOnly ? "CRAFTABLE ONLY" : "ALL RECIPES"), b -> {
            craftableOnly = !craftableOnly;
            page = 0;
            rebuildWidgets();
        }).bounds(x, y + 24, 184, 20).build());

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, available.size());
        var context = SlotDisplayContext.fromLevel(Minecraft.getInstance().level);
        for (int i = start; i < end; i++) {
            RecipeDisplayEntry recipe = available.get(i);
            List<ItemStack> results = recipe.resultItems(context);
            if (results.isEmpty()) continue;
            ItemStack output = results.get(0);
            String name = output.getHoverName().getString();
            if (name.length() > 21) name = name.substring(0, 20) + "...";
            String label = (i + 1) + ". " + name;
            final RecipeDisplayEntry selectedRecipe = recipe;
            this.addRenderableWidget(Button.builder(Component.literal(label), b -> {
                if (minecraft != null && minecraft.gameMode != null && minecraft.player != null) {
                    minecraft.gameMode.handlePlaceRecipe(minecraft.player.containerMenu.containerId,
                            selectedRecipe.id(), false);
                }
            }).bounds(x, y + 49 + (i - start) * 20, 184, 19).build());
        }
        int lastPage = Math.max(0, (available.size() - 1) / PAGE_SIZE);
        final int prev = Math.max(0, page - 1);
        final int next = Math.min(lastPage, page + 1);
        this.addRenderableWidget(Button.builder(Component.literal("<"), b -> {
            page = prev;
            rebuildWidgets();
        }).bounds(x, y + 214, 30, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal((page + 1) + " / " + (lastPage + 1)),
                b -> rebuildWidgets()).bounds(x + 34, y + 214, 76, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal(">"), b -> {
            page = next;
            rebuildWidgets();
        }).bounds(x + 114, y + 214, 30, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("REFRESH"), b -> rebuildWidgets())
                .bounds(x + 146, y + 214, 38, 20).build());
    }

    private void refreshRecipes() {
        available.clear();
        if (minecraft == null || minecraft.player == null) return;

        StackedItemContents items = new StackedItemContents();
        playerInventory.fillStackedContents(items);
        menu.fillCraftSlotsStackedContents(items);

        for (RecipeCollection collection : minecraft.player.getRecipeBook().getCollection(CATEGORIES[category])) {
            collection.selectRecipes(items, display -> switch (display) {
                case ShapedCraftingRecipeDisplay shaped ->
                        shaped.width() <= menu.getGridWidth() && shaped.height() <= menu.getGridHeight();
                case ShapelessCraftingRecipeDisplay shapeless ->
                        shapeless.ingredients().size() <= menu.getGridWidth() * menu.getGridHeight();
                default -> false;
            });
            if (!collection.hasAnySelected()) continue;
            List<RecipeDisplayEntry> entries = collection.getSelectedRecipes(
                    craftableOnly ? RecipeCollection.CraftableStatus.CRAFTABLE : RecipeCollection.CraftableStatus.ANY);
            available.addAll(entries);
        }
        page = Math.min(page, Math.max(0, (available.size() - 1) / PAGE_SIZE));
    }

    /** Clear only the panel widgets through a full vanilla screen reinit. */
    private void rebuildWidgets() {
        this.clearWidgets();
        this.init();
    }
}
