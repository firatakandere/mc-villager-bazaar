package github.fakandere.villagerBazaar.prompts;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PromptFactory {

    private Player player;
    private Consumer<Map<Object, Object>> onComplete;
    private final ConversationFactory conversationFactory;

    LinkedList<BPrompt> promptList = new LinkedList<>();

    public PromptFactory(JavaPlugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin is needed.");
        }

        conversationFactory = new ConversationFactory(plugin);
    }

    public PromptFactory player(Player player) {
        this.player = player;
        return this;
    }

    public PromptFactory withTimeout(int timeoutSeconds) {
        conversationFactory.withTimeout(timeoutSeconds);
        return this;
    }

    public PromptFactory addPrompt(BPrompt prompt, Object objectKey) {
        if (promptList.isEmpty()) {
            conversationFactory.withFirstPrompt(prompt);
        } else {
            promptList.getLast().setNextPrompt(prompt);
        }

        prompt.setObjectKey(objectKey);
        promptList.add(prompt);
        return this;
    }

    public PromptFactory onComplete(Consumer<Map<Object, Object>> onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    public Conversation build() {
        if (promptList.isEmpty()) {
            throw new IllegalArgumentException("At least one prompt is needed.");
        }

        if (onComplete == null) {
            throw new IllegalArgumentException("Completion function is needed.");
        }

        if (player == null) {
            throw new IllegalArgumentException("Player is needed.");
        }

        promptList.getLast().setOnComplete(onComplete);
        return conversationFactory.buildConversation(player);
    }
}
