package github.fakandere.villagerBazaar;

import github.fakandere.villagerBazaar.exceptions.InvalidInputException;
import github.fakandere.villagerBazaar.exceptions.NotFoundException;
import github.fakandere.villagerBazaar.models.Bazaar;
import com.google.inject.Inject;
import github.fakandere.villagerBazaar.models.BazaarItem;
import github.fakandere.villagerBazaar.utils.AnvilGUIHelper;
import github.fakandere.villagerBazaar.utils.BazaarManager;
import github.fakandere.villagerBazaar.utils.IBazaarManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.slot.ClickOptions;
import org.ipvp.canvas.type.ChestMenu;


import java.rmi.UnexpectedException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

enum VillagerBazaarStage {
    SELL, EDIT, CUSTOMIZE, ITEMADD, ITEMREMOVE, ITEMEDIT
}

public class VillagerBazaar {

    public Player p;
    public Villager v;
    public PlayerInteractEntityEvent e;
    public boolean canEdit = true;
    private Bazaar bazaar;
    private IBazaarManager bazaarManager;


    public VillagerBazaarStage stage = VillagerBazaarStage.SELL;

    public VillagerBazaar(Player p, Villager v, PlayerInteractEntityEvent e, Bazaar bazaar, IBazaarManager bazaarManager) {
        this.p = p;
        this.v = v;
        this.e = e;
        this.bazaar = bazaar;
        this.bazaarManager = bazaarManager;
    }

    public Menu createMenu() {
        return ChestMenu.builder(5).title(this.v.getCustomName()).build();
    }

    public ItemStack getIcon(Material m, String text) {
        ItemStack iconItem = new ItemStack(m, 1);
        ItemMeta itemMeta = iconItem.getItemMeta();
        itemMeta.setDisplayName(text);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        iconItem.setItemMeta(itemMeta);
        return iconItem;
    }

    public void glassBorder(Menu screen) {
        //Glass Decorations
        ItemStack glass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);

        IntStream.range(0, 10).forEach(n -> {
            screen.getSlot(n).setItem(glass);
        });

        screen.getSlot(17).setItem(glass);
        screen.getSlot(18).setItem(glass);
        screen.getSlot(26).setItem(glass);
        screen.getSlot(27).setItem(glass);

        IntStream.range(35, 45).forEach(n -> {
            screen.getSlot(n).setItem(glass);
        });
    }

    public void distributeItems(Menu screen, List<BazaarItem> items) {
        int[] ignore = new int[]{17, 18, 26, 27};
        int index = 10;
        while (items.size() > 0) {

            int finalIndex = index;
            if (Arrays.stream(ignore).noneMatch(i -> i == finalIndex)) {
                BazaarItem item = items.get(0);
                screen.getSlot(index).setItem(new ItemStack(item.getMaterial()));
                items.remove(0);

            }
            index++;
        }
    }

    public void show(Menu screen, Player p) {

        this.glassBorder(screen);

        //Customize Button
        if (this.canEdit) {

            if (this.stage != VillagerBazaarStage.EDIT) {
                //Open EditScreen
                screen.getSlot(43).setClickHandler((player, info) -> {
                    screen.close(p);
                    this.editScreen();
                });
                ;
                screen.getSlot(43)
                      .setItem(this.getIcon(Material.LEGACY_BOOK_AND_QUILL, "Customize"));
            } else {
                //Return Start Screen
                screen.getSlot(43).setClickHandler((player, info) -> {
                    screen.close(p);
                    this.startBazaar();
                });
                screen.getSlot(43).setItem(this.getIcon(Material.TIPPED_ARROW, "Back"));
            }
        }

        //Close Handler
        screen.setCloseHandler((player, menu1) -> {
            this.stopBazaar(screen, player);
        });

        //Close Button
        screen.getSlot(44).setClickHandler((player, info) -> {
            this.stopBazaar(screen, player);
        });
        screen.getSlot(44).setItem(this.getIcon(Material.ACACIA_DOOR, "Exit"));

        //Draw
        screen.open(p);
    }

    public void startBazaar() {
        this.stage = VillagerBazaarStage.SELL;
        Menu screen = createMenu();

        List<BazaarItem> items = this.bazaar.getItems();

        this.distributeItems(screen, items);

        this.show(screen, this.p);
    }

    public void editScreen() {
        this.stage = VillagerBazaarStage.EDIT;
        Menu screen = createMenu();

        //#region Type Changer
        screen.getSlot(10).setClickHandler((player, info) -> {
            this.customizeScreen();
        });
        screen.getSlot(10).setItem(this.getIcon(Material.ARMOR_STAND, "Customize Type"));
        //#endregion

        //#region Name Changer
        screen.getSlot(11).setClickHandler((player, info) -> {
            AnvilGUIHelper.prompt(p, "Bazaar Name", v.getCustomName(),
                    (text) -> v.setCustomName(text.replaceAll("[^a-zA-Z0-9\\s]", "")));
        });
        screen.getSlot(11).setItem(this.getIcon(Material.NAME_TAG, "Change Name"));
        //#endregion,


        //#region Item Add Screen
        screen.getSlot(14).setClickHandler((player, info) -> {
            this.itemAddScreen();
        });
        screen.getSlot(14).setItem(this.getIcon(Material.BUCKET, "Add Item"));
        //#endregion

        this.show(screen, this.p);
    }

    public void customizeScreen() {
        this.stage = VillagerBazaarStage.CUSTOMIZE;
        Menu screen = createMenu();

        //#region Villager Type Customization
        //Plain Villager
        screen.getSlot(10).setClickHandler((player, info) -> {
            this.v.setVillagerType(Villager.Type.PLAINS);
        });
        screen.getSlot(10).setItem(this.getIcon(Material.GRASS_BLOCK, "PLAIN"));

        //Desert Villager
        screen.getSlot(11).setClickHandler((player, info) -> {
            this.v.setVillagerType(Villager.Type.DESERT);
        });
        screen.getSlot(11).setItem(this.getIcon(Material.SAND, "DESERT"));
        ;

        //Jungle Villager
        screen.getSlot(12).setClickHandler((player, info) -> {
            this.v.setVillagerType(Villager.Type.JUNGLE);
        });
        screen.getSlot(12).setItem(this.getIcon(Material.JUNGLE_WOOD, "JUNGLE"));

        //Snow Villager
        screen.getSlot(13).setClickHandler((player, info) -> {
            this.v.setVillagerType(Villager.Type.SNOW);
        });
        screen.getSlot(13).setItem(this.getIcon(Material.SNOW, "SNOW"));

        //SAVANNA Villager
        screen.getSlot(14).setClickHandler((player, info) -> {
            this.v.setVillagerType(Villager.Type.SAVANNA);
        });
        screen.getSlot(14).setItem(this.getIcon(Material.ACACIA_WOOD, "SAVANNA"));

        //SWAMP Villager
        screen.getSlot(15).setClickHandler((player, info) -> {
            this.v.setVillagerType(Villager.Type.SWAMP);
        });
        screen.getSlot(15).setItem(this.getIcon(Material.COARSE_DIRT, "SWAMP"));

        //TAIGA Villager
        screen.getSlot(16).setClickHandler((player, info) -> {
            this.v.setVillagerType(Villager.Type.TAIGA);
        });
        screen.getSlot(16).setItem(this.getIcon(Material.SPRUCE_WOOD, "TAIGA"));
        //#endregion

        this.show(screen, this.p);
    }

    public void itemAddScreen() {
        this.stage = VillagerBazaarStage.ITEMADD;
        Menu screen = createMenu();
        //FillScreen with Glasses
        ItemStack glass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        IntStream.range(0, 44).forEach(n -> {
            screen.getSlot(n).setItem(glass);
        });

        screen.getSlot(22).setItem(null);
        ClickOptions cli = ClickOptions.builder().allow(InventoryAction.PLACE_ONE)
                                       .allow(InventoryAction.PLACE_SOME)
                                       .allow(InventoryAction.PLACE_ALL)
                                       .allow(InventoryAction.MOVE_TO_OTHER_INVENTORY)
                                       .allow(ClickType.LEFT).allow(ClickType.DROP)
                                       .allow(ClickType.RIGHT).build();

        screen.getSlot(22).setClickOptions(cli);
        screen.getSlot(22).setClickHandler((player, click) -> {
            if (click.isAddingItem()) {

                //Player put item in empty box.
                ItemStack addingItem = click.getAddingItem();
                Material m = addingItem.getType();
                Integer amount = addingItem.getAmount();

                player.sendMessage(m.toString());

                boolean isExisting = this.bazaar.itemExists(m);

                if (isExisting) {
                    try {
                        this.bazaarManager.addStock(this.bazaar, m, amount);
                    } catch (InvalidInputException invalidInputException) {
                        player.sendMessage("invalidInputException");
                        screen.close(player);
                    } catch (UnexpectedException unexpectedException) {
                        player.sendMessage("unexpectedException");
                        screen.close(player);
                    } catch (NotFoundException notFoundException) {
                        player.sendMessage("notFoundException");
                        screen.close(player);
                    }
                } else {

                    AnvilGUIHelper.prompt(player, "Selling Price", "1.00", (sellPriceStr) -> {
                        double sellingPrice = Double.parseDouble(sellPriceStr);
                        double buyingPrice = Double.parseDouble("1.00");
                        try {
                            this.bazaarManager
                                    .addItem(this.bazaar, m, sellingPrice, buyingPrice);
                        } catch (InvalidInputException invalidInputException) {
                            player.sendMessage("invalidInputException");
                            screen.close(player);
                        } catch (UnexpectedException unexpectedException) {
                            player.sendMessage("unexpectedException");
                            screen.close(player);
                        } catch (NotFoundException notFoundException) {
                            player.sendMessage("notFoundException");
                            screen.close(player);
                        }


                    });
                }

            }
            player.sendMessage(click.getAction().toString());
        });


        this.show(screen, this.p);
    }

    public void stopBazaar(Menu screen, Player player) {
        this.stage = VillagerBazaarStage.SELL;
        screen.close(player);
    }

}
