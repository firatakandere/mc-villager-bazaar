package github.fakandere.villagerBazaar;

import github.fakandere.villagerBazaar.exceptions.InvalidInputException;
import github.fakandere.villagerBazaar.exceptions.NotFoundException;
import github.fakandere.villagerBazaar.models.Bazaar;
import github.fakandere.villagerBazaar.models.BazaarItem;
import github.fakandere.villagerBazaar.models.BazaarType;
import github.fakandere.villagerBazaar.prompts.BNumericPrompt;
import github.fakandere.villagerBazaar.prompts.BStringPrompt;
import github.fakandere.villagerBazaar.prompts.PromptFactory;
import github.fakandere.villagerBazaar.utils.IBazaarManager;
import org.bukkit.ChatColor;
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
import org.ipvp.canvas.slot.ClickOptions;
import org.ipvp.canvas.type.ChestMenu;


import java.rmi.UnexpectedException;
import java.util.*;
import java.util.stream.IntStream;

enum VillagerBazaarStage {
    SELL, EDIT, CUSTOMIZE, ITEMADD, ITEMREMOVE, ITEMEDIT
}

public class VillagerBazaar {

    public Player p;
    public Villager v;
    private Bazaar bazaar;
    private IBazaarManager bazaarManager;
    private JavaPlugin plugin;

    public VillagerBazaarStage stage = VillagerBazaarStage.SELL;

    public VillagerBazaar(Player p, Villager v, Bazaar bazaar,
                          IBazaarManager bazaarManager, JavaPlugin plugin) {
        this.p = p;
        this.v = v;
        this.bazaar = bazaar;
        this.bazaarManager = bazaarManager;
        this.plugin = plugin;
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
        Set<Integer> ignore = new HashSet<Integer>(){{
            add(17);
            add(18);
            add(26);
            add(27);
        }};
        int offset = 10;
        int index = 0;


        for (Iterator it = items.iterator(); it.hasNext(); ++index) {
            int itemIndex = offset + index;
            if (!ignore.contains(itemIndex)) {
                BazaarItem item = (BazaarItem) it.next();

                ItemStack displayItem = new ItemStack(item.getMaterial(), 1);
                ItemMeta itemMeta = displayItem.getItemMeta();

                int stocks = bazaar.getStocks().getOrDefault(item.getMaterial(),0);


                itemMeta.setLore(Arrays.asList(
                        ChatColor.RED + "Sell: " + item.getSellPrice(),
                        ChatColor.AQUA +"Buy: " + item.getBuyPrice(),
                        ChatColor.DARK_GREEN + "Stock: " + stocks)
                );

                displayItem.setItemMeta(itemMeta);
                screen.getSlot(itemIndex).setItem(displayItem);
                items.remove(0);
            }
        }
    }

    public void show(Menu screen, Player p) {

        this.glassBorder(screen);

        //Customize Button
        if (this.canEdit()) {

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
            new PromptFactory(plugin)
                .player(p)
                .addPrompt(new BStringPrompt("Please type your shop's name to the chat, type `cancel` to cancel"), "villagername")
                .onComplete(map -> {
                    v.setCustomName(map.get("villagername").toString());
                })
                .withTimeout(30)
                .withCancellationToken("cancel")
                .build()
                .begin();
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
                int amount = addingItem.getAmount();

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
                    new PromptFactory(plugin)
                        .player(p)
                        .addPrompt(new BNumericPrompt("Enter selling price for 1 item, type `cancel` to cancel", false, true), "sellingprice")
                        .addPrompt(new BNumericPrompt("Enter buying price for 1 item, type `cancel` to cancel", false, true), "buyingprice")
                        .onComplete(map -> {
                            try {
                                bazaarManager.addItem(bazaar, m, (double)map.get("sellingprice"), (double)map.get("buyingprice"), amount);
                                player.sendMessage("Your item is added");
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
                        })
                        .onCancel(() -> {
                            // @todo if player is offline all items are lost
                            if (player.isOnline()) {
                                p.getInventory().addItem(new ItemStack(m, amount));
                            }
                            p.sendMessage("The prompt has been cancelled");
                        })
                        .withTimeout(30)
                        .withCancellationToken("cancel")
                        .build()
                        .begin();
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

    private boolean canEdit() {
        if (bazaar.getBazaarType() == BazaarType.ADMIN) {
            return p.isOp(); // @todo add permission to edit admin bazaars
        }
        else {
            return p.isOp() || (bazaar.getPlayerUniqueId() == p.getUniqueId());
        }
    }

}
