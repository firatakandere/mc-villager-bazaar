package com.kirpideleri.villagerBazaar;

import com.kirpideleri.villagerBazaar.exceptions.NotFoundException;
import com.kirpideleri.villagerBazaar.models.Bazaar;
import com.kirpideleri.villagerBazaar.prompts.BNumericPrompt;
import com.kirpideleri.villagerBazaar.prompts.BStringPrompt;
import com.kirpideleri.villagerBazaar.prompts.PromptFactory;
import com.kirpideleri.villagerBazaar.utils.IBazaarManager;
import com.kirpideleri.villagerBazaar.exceptions.InvalidInputException;
import com.kirpideleri.villagerBazaar.models.BazaarItem;
import com.kirpideleri.villagerBazaar.models.BazaarType;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.slot.ClickOptions;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.type.ChestMenu;

import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class VillagerBazaar {
    final private Menu menu;

    private final Villager villager;
    private final Bazaar bazaar;
    private final IBazaarManager bazaarManager;
    private final JavaPlugin plugin;
    private final Permission perm;
    private final Player player;

    public VillagerBazaar(Player player, Villager villager, JavaPlugin plugin, IBazaarManager bazaarManager, Bazaar bazaar, Permission perm) {
        this.player = player;
        this.villager = villager;
        this.plugin = plugin;
        this.bazaarManager = bazaarManager;
        this.bazaar = bazaar;
        this.perm = perm;

        menu = ChestMenu.builder(5).title(villager.getCustomName()).build();
    }

    public void open() {
        displayMainScreen();
        menu.open(player);
    }

    private void displayMainScreen() {
        menu.clear();
        applyGlassBorder();

        fillItemsFromBazaar(false);

        if (canEdit()) {
            addMenuItem(31, Material.ARMOR_STAND, "Change Biome", this::changeVillagerBiome);
            addMenuItem(32, Material.BOOK, "Edit Items", this::displayEditItemsScreen);
            addMenuItem(33, Material.CHEST, "Bazaar Inventory", this::displayEditBazaarInventory);
            addMenuItem(34, Material.NAME_TAG, "Change Name", this::changeVillagerNamePrompt);
        }
    }

    private void displayEditBazaarInventory() {
        menu.clear();
        BinaryMask.builder(menu)
                .item(new ItemStack(Material.WHITE_STAINED_GLASS_PANE))
                .pattern("000000000")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("111111111")
                .pattern("000000000")
                .build()
                .apply(menu);
        addMenuItem(43, Material.OAK_DOOR, "Back", this::displayMainScreen);

        ClickOptions clickOptions = ClickOptions.builder()
                .allClickTypes()
                .allActions()
                .build();

        Slot.ClickHandler clickHandler = (player, clickInformation) -> {
            int slotIndex = clickInformation.getClickedSlot().getIndex();

            if (clickInformation.isAddingItem()) {
                try {
                    ItemStack itemStack;
                    if (clickInformation.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY &&
                        bazaar.getStocks().get(slotIndex) != null) {
                        // @todo fix this
                        itemStack = bazaar.getStocks().get(slotIndex);
                        itemStack.setAmount(itemStack.getAmount() + clickInformation.getItemAmount());
                    }
                    else {
                        itemStack = new ItemStack(clickInformation.getAddingItem());
                    }
                    bazaarManager.setStock(bazaar, slotIndex, itemStack);
                } catch (UnexpectedException e) {
                    e.printStackTrace();
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }
            else if (clickInformation.isTakingItem()) {
                int leftAmount = bazaar.getStocks().get(slotIndex).getAmount() - getItemAmount(clickInformation.getAction(), bazaar.getStocks().get(slotIndex).getAmount());
                if (leftAmount <= 0) {
                    try {
                        bazaarManager.setStock(bazaar, slotIndex, null);
                    } catch (UnexpectedException e) {
                        e.printStackTrace();
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    // @todo fix this
                    ItemStack itemStack = bazaar.getStocks().get(slotIndex);
                    itemStack.setAmount(leftAmount);
                    try {
                        bazaarManager.setStock(bazaar, slotIndex, itemStack);
                    } catch (UnexpectedException e) {
                        e.printStackTrace();
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

        };

        IntStream.range(0, 27).forEach(i -> {
            menu.getSlot(i).setItem(bazaar.getStocks().get(i));
            menu.getSlot(i).setClickOptions(clickOptions);
            menu.getSlot(i).setClickHandler(clickHandler);
        });
    }

    private void displayEditItemsScreen() {
        menu.clear();
        applyGlassBorder();
        fillItemsFromBazaar(true);
        addMenuItem(34, Material.OAK_DOOR, "Back", this::displayMainScreen);


        Slot.ClickHandler clickHandler = (player, clickInformation) -> {
            if (clickInformation.getClickedSlot().getItem(player) == null) {
                ItemStack itemStack = clickInformation.getAddingItem();
                clickInformation.getClickedSlot().setItem(itemStack);
                setItemPricePrompt(clickInformation.getClickedSlot().getIndex());
            }
            else if (clickInformation.getClickType() == ClickType.LEFT) { // Edit price
                setItemPricePrompt(clickInformation.getClickedSlot().getIndex());
            }
            else if (clickInformation.getClickType() == ClickType.RIGHT) { // Delete item
                try {
                    bazaarManager.deleteItem(bazaar, clickInformation.getClickedSlot().getIndex());
                } catch (UnexpectedException e) {
                    e.printStackTrace();
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }
        };

        menu.getSlot(10).setClickHandler(clickHandler);
        menu.getSlot(11).setClickHandler(clickHandler);
        menu.getSlot(12).setClickHandler(clickHandler);
        menu.getSlot(13).setClickHandler(clickHandler);
        menu.getSlot(14).setClickHandler(clickHandler);
        menu.getSlot(15).setClickHandler(clickHandler);
        menu.getSlot(16).setClickHandler(clickHandler);
        menu.getSlot(17).setClickHandler(clickHandler);
    }

    private void displayConfirmationScreen(Runnable onConfirm, Runnable onCancel) {
        menu.clear();
        applyGlassBorder();
        addMenuItem(11, Material.GREEN_WOOL, "Confirm", onConfirm);
        addMenuItem(16, Material.RED_WOOL, "Cancel", onCancel);
    }

    private void setItemPricePrompt(int itemSlotIndex) {
        menu.close(player);

        new PromptFactory(plugin)
                .player(player)
                .addPrompt(new BNumericPrompt("Enter selling price for the item stack, type `cancel` to cancel", false, true), "sellingprice")
                .addPrompt(new BNumericPrompt("Enter buying price for the item stack, type `cancel` to cancel", false, true), "buyingprice")
                .onComplete(map -> {
                    menu.open(player);
                    try {
                        bazaarManager.setItem(
                                bazaar,
                                convertItemSlotToBazaarIndex(itemSlotIndex),
                                menu.getSlot(itemSlotIndex).getItem(player),
                                (double)map.getOrDefault("sellingprice", 0),
                                (double)map.getOrDefault("buyingprice", 0)
                        );
                        fillItemsFromBazaar(true);
                    } catch (UnexpectedException e) {
                        e.printStackTrace();
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                    } catch (InvalidInputException e) {
                        e.printStackTrace();
                    }
                })
                .onCancel(() -> {
                    menu.clear(itemSlotIndex);
                    menu.open(player);
                })
                .withTimeout(30)
                .withCancellationToken("cancel")
                .build()
                .begin();
    }

    private void changeVillagerNamePrompt() {
        menu.close(player);

        new PromptFactory(plugin)
                .player(player)
                .addPrompt(new BStringPrompt("Please type your shop's name to the chat, type `cancel` to cancel"), "villagername")
                .onComplete(map -> {
                    villager.setCustomName(map.get("villagername").toString());
                })
                .withTimeout(30)
                .withCancellationToken("cancel")
                .build()
                .begin();
    }

    private void changeVillagerBiome() {
        menu.clear();
        applyGlassBorder();

        addMenuItem(10, Material.GRASS_BLOCK, "PLAIN", () -> villager.setVillagerType(Villager.Type.PLAINS));
        addMenuItem(11, Material.SAND, "SAND", () -> villager.setVillagerType(Villager.Type.DESERT));
        addMenuItem(12, Material.JUNGLE_WOOD, "JUNGLE", () -> villager.setVillagerType(Villager.Type.JUNGLE));
        addMenuItem(13, Material.SNOW, "SNOW", () -> villager.setVillagerType(Villager.Type.SNOW));
        addMenuItem(14, Material.ACACIA_WOOD, "SAVANNA", () -> villager.setVillagerType(Villager.Type.SAVANNA));
        addMenuItem(15, Material.COARSE_DIRT, "SWAMP", () -> villager.setVillagerType(Villager.Type.SWAMP));
        addMenuItem(16, Material.SPRUCE_WOOD, "TAIGA", () -> villager.setVillagerType(Villager.Type.TAIGA));

        addMenuItem(34, Material.OAK_DOOR, "Back", this::displayMainScreen);
    }

    private ItemStack getIcon(Material m, String text) {
        ItemStack iconItem = new ItemStack(m, 1);
        ItemMeta itemMeta = iconItem.getItemMeta();
        itemMeta.setDisplayName(text);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        iconItem.setItemMeta(itemMeta);
        return iconItem;
    }

    private void applyGlassBorder() {
        BinaryMask.builder(menu)
                .item(new ItemStack(Material.WHITE_STAINED_GLASS_PANE))
                .pattern("111111111")
                .pattern("100000001")
                .pattern("111111111")
                .pattern("100000001")
                .pattern("111111111")
                .build()
                .apply(menu);
    }

    private boolean canEdit() {
        if (bazaar.getBazaarType() == BazaarType.ADMIN) {
            return player.isOp() || perm.has(player, "villagerbazaar.admin.manage");
        }
        else {
            return player.isOp() || (bazaar.getPlayerUniqueId() == player.getUniqueId());
        }
    }

    private void fillItemsFromBazaar(boolean isEditMode) {
        int slotIndex = 10;
        for (BazaarItem bazaarItem : bazaar.getItems()) {
            if (bazaarItem != null) {
                ItemStack itemStack = bazaarItem.getItemStack();
                ItemMeta itemMeta = itemStack.getItemMeta();
                List<String> lore = new ArrayList<>();
                List<String> currentLore = itemMeta.getLore();
                if (currentLore != null) {
                    lore.addAll(currentLore);
                }

                if (isEditMode) {
                    lore.add("EDIT (Left-click)");
                    lore.add("DELETE (Right-click)");
                }
                else {
                    lore.add("BUY (Left-click): $" + bazaarItem.getBuyPrice());
                    if (bazaar.getBazaarType() == BazaarType.ADMIN) {
                        lore.add("SELL (Right-click): $" + bazaarItem.getSellPrice());
                    }
                }
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
                menu.getSlot(slotIndex).setItem(bazaarItem.getItemStack());
            } else {
                menu.clear(slotIndex);
            }
            slotIndex++;
        }
    }

    private int convertItemSlotToBazaarIndex(int itemSlotIndex) {
        return itemSlotIndex - 10;
    }

    public int getItemAmount(InventoryAction action, int currentAmount) {
        switch (action) {
            case PICKUP_ONE:
            case DROP_ONE_SLOT:
                return 1;
            case PICKUP_HALF:
                return (int) Math.ceil(currentAmount / 2D);
            case PICKUP_ALL:
            case DROP_ALL_SLOT:
            case MOVE_TO_OTHER_INVENTORY:
                return currentAmount;
                // @todo disallow these two
            case PICKUP_SOME: // Don't know how this is caused
            case SWAP_WITH_CURSOR:
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void addMenuItem(int slot, Material material, String title, Runnable action) {
        menu.getSlot(slot).setItem(getIcon(material, title));
        menu.getSlot(slot).setClickHandler((player, info) -> action.run());
    }
}
