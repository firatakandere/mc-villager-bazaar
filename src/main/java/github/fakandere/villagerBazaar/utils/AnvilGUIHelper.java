package github.fakandere.villagerBazaar.utils;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.function.Consumer;

public class AnvilGUIHelper {

    static JavaPlugin plugin;

    public static void setUp(JavaPlugin plg) {
        plugin = plg;
    }

    public static void prompt(Player p, String title, String text, Consumer<String> onComplete) {
        AnvilGUI gui = new AnvilGUI.Builder()
                .onComplete((player, tx) -> {                             //called when the inventory output slot is clicked
                    onComplete.accept(tx);
                    return AnvilGUI.Response.close();
                })
                .preventClose()                                             //prevents the inventory from being closed
                .text(text)                      //sets the text the GUI should start with
                .title(title)                        //set the title of the GUI (only works in 1.14+)
                .plugin(plugin)
                .open(p);                                            //opens the GUI for the player provided
    }
}
