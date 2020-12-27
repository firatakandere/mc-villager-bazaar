package github.fakandere.villagerBazaar;

import com.google.inject.Inject;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.type.ChestMenu;

import github.fakandere.villagerBazaar.models.Bazaar;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.stream.IntStream;

enum VillagerBazaarStage {
    SELL,
    EDIT,
    CUSTOMIZE,
    ITEMEDIT,
}

public class VillagerBazaar {

    @Inject
    VillagerBazaarPlugin villagerBazaarPlugin;

    public Player p;
    public Villager v;
    public PlayerInteractEntityEvent e;
    public boolean canEdit = true;

    private Bazaar bazaar;
    private JavaPlugin plugin;


    public VillagerBazaarStage stage = VillagerBazaarStage.SELL;

    public VillagerBazaar(Player p, Villager v, PlayerInteractEntityEvent e, Bazaar bazaar, JavaPlugin plugin) {
        this.p = p;
        this.v = v;
        this.e = e;
        this.bazaar = bazaar;
        this.plugin = plugin;
    }

    public Menu createMenu() {
        return ChestMenu.builder(5)
                .title(this.v.getCustomName())
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
        ItemStack glass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);

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

    public void show(Menu screen, Player p) {

        this.glassBorder(screen);

        //Customize Button
        if (this.canEdit) {

            if (this.stage != VillagerBazaarStage.EDIT) {
                //Open EditScreen
                screen.getSlot(43).setClickHandler((player, info) -> {
                    screen.close(p);
                    this.editScreen();
                });                ;
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
                    .plugin(plugin)
                    .open(p);
        });
        screen.getSlot(11).setItem(this.getIcon(Material.NAME_TAG, "Change Type"));
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

    public void stopBazaar(Menu screen, Player player) {
        this.stage = VillagerBazaarStage.SELL;
        screen.close(player);
    }

}
