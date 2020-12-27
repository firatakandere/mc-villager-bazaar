package github.fakandere.villagerBazaar;

import com.google.inject.Inject;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.type.ChestMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

enum VillagerBazaarStage {
    SELL,
    EDIT,
    CUSTOMIZE,
    ITEMEDIT,
}

class VillagerBazaarItem {

    private Random rand = new Random();

    public VillagerBazaarItem() {
        Material[] ms = new Material[]{
                Material.GOLD_BLOCK,
                Material.DIAMOND_BLOCK,
                Material.NETHERITE_BLOCK,
                Material.REDSTONE_BLOCK,
                Material.IRON_BLOCK
        };

        this.material = ms[rand.nextInt(ms.length)];

    }

    Material material = Material.GOLD_BLOCK;
    Double sellPrice = 10.0;
    Double buyPrice = 5.0;
    Integer amount = 1;
}

public class VillagerBazaar {

    @Inject
    VillagerBazaarPlugin villagerBazaarPlugin;

    public Player p;
    public Villager v;
    public PlayerInteractEntityEvent e;
    public boolean canEdit = true;

    public ArrayList<VillagerBazaarItem> items = new ArrayList<>();

    public VillagerBazaarStage stage = VillagerBazaarStage.SELL;

    public VillagerBazaar() {
    }

    public void setPlayer(Player p) {
        this.p = p;
    }

    public void setVillager(Villager v) {
        this.v = v;
    }

    public void setEvent(PlayerInteractEntityEvent e) {
        this.e = e;
    }

    public Menu createMenu() {
        return ChestMenu.builder(5)
                .title(this.v.getCustomName().toString())
                .build();
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
        ItemStack glass = this.getIcon(Material.WHITE_STAINED_GLASS_PANE, " ");

        IntStream.range(0, 10).forEach(
                n -> {
                    screen.getSlot(n).setItem(glass);
                }
        );

        screen.getSlot(17).setItem(glass);
        screen.getSlot(18).setItem(glass);
        screen.getSlot(26).setItem(glass);
        screen.getSlot(27).setItem(glass);

        IntStream.range(35, 45).forEach(
                n -> {
                    screen.getSlot(n).setItem(glass);
                }
        );
    }

    public void distributeItems(Menu screen, ArrayList<VillagerBazaarItem> items) {
        int[] ignore = new int[]{17, 18, 26, 27};
        int index = 10;
        while (items.size() > 0) {

            int finalIndex = index;
            if (Arrays.stream(ignore).noneMatch(i -> i == finalIndex)) {
                VillagerBazaarItem item = items.get(0);
                screen.getSlot(index).setItem(new ItemStack(item.material));
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
                screen.getSlot(43).setItem(this.getIcon(Material.LEGACY_BOOK_AND_QUILL, "Customize"));
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

        //create fake BAZAAR

        this.items.add(new VillagerBazaarItem());
        this.items.add(new VillagerBazaarItem());
        this.items.add(new VillagerBazaarItem());
        this.items.add(new VillagerBazaarItem());
        this.items.add(new VillagerBazaarItem());
        this.items.add(new VillagerBazaarItem());
        this.items.add(new VillagerBazaarItem());
        this.items.add(new VillagerBazaarItem());
        this.items.add(new VillagerBazaarItem());
        this.items.add(new VillagerBazaarItem());
        this.items.add(new VillagerBazaarItem());
        this.items.add(new VillagerBazaarItem());
        this.items.add(new VillagerBazaarItem());

        this.distributeItems(screen, this.items);


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
            new AnvilGUI.Builder()
                    .onComplete((pl, text) -> {
                        v.setCustomName(text.replaceAll("[^a-zA-Z0-9\\s]", ""));
                        return AnvilGUI.Response.close();
                    })
                    .preventClose()
                    .text(this.v.getCustomName())
                    .title("Shop Name")
                    .plugin(villagerBazaarPlugin)
                    .open(p);
        });
        screen.getSlot(11).setItem(this.getIcon(Material.NAME_TAG, "Change Name"));
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
        screen.getSlot(15).setClickHandler((player, info) -> {
            this.v.setVillagerType(Villager.Type.TAIGA);
        });
        screen.getSlot(15).setItem(this.getIcon(Material.SPRUCE_WOOD, "TAIGA"));
        //#endregion

        this.show(screen, this.p);
    }

    public void stopBazaar(Menu screen, Player player) {
        this.stage = VillagerBazaarStage.SELL;
        screen.close(player);
    }


    public void createBazaar(){
        //UUID bazaarId = this.createBazaarNPC()
    }

    public UUID createBazaarNPC(Player p) {
        Location targetLocation = p.getTargetBlock(null, 10).getLocation();
        targetLocation.add(0.5, 1, 0.5);
        //@todo: Is this block appropriate is it lava or water or anything sketchy ?
        //throw VillagerBazaarInvalidPlacementException
        Villager v = (Villager) p.getWorld().spawnEntity(targetLocation, EntityType.VILLAGER);
        //Set properties
        v.setCustomName("Villager of " + p.getName());
        v.setInvulnerable(true);
        v.setAI(false);
        v.setVillagerLevel(5);
        v.setVillagerType(Villager.Type.PLAINS);
        v.setProfession(Villager.Profession.NONE);
        new AnvilGUI.Builder()
                .onComplete((player, text) -> {
                    v.setCustomName(text.replaceAll("[^a-zA-Z0-9\\s]", ""));
                    return AnvilGUI.Response.close();
                })
                .preventClose()
                .text(v.getCustomName())
                .title("Bazaar Name")
                .plugin(villagerBazaarPlugin)
                .open(p);


        return v.getUniqueId();

    }
}
