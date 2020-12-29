package github.fakandere.villagerBazaar.prompts;

import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.Map;
import java.util.function.Consumer;

public class PromptFactory {

    private Player player;
    private Consumer<Map<Object, Object>> onComplete;
    private Runnable onCancel;
    private Conversation conversation;
    private final ConversationFactory conversationFactory;

    LinkedList<BPrompt> promptList = new LinkedList<>();

    public PromptFactory(JavaPlugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin is needed.");
        }

        conversationFactory = new ConversationFactory(plugin);

        conversationFactory.addConversationAbandonedListener(conversationAbandonedEvent -> {
            if (conversationAbandonedEvent.gracefulExit()) {
                if (onComplete != null) {
                    onComplete.accept(conversationAbandonedEvent.getContext().getAllSessionData());
                }
            } else {
                if (onCancel != null) {
                    onCancel.run();
                }
            }
        });
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

    public PromptFactory onCancel(Runnable onCancel) {
        this.onCancel = onCancel;
        return this;
    }

    public PromptFactory withCancellationToken(String cancelInput) {
        conversationFactory.withConversationCanceller(new ExactMatchConversationCanceller(cancelInput));
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

        conversation = conversationFactory.buildConversation(player);
        return conversation;
    }
}
