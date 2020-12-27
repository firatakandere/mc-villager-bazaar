package github.fakandere.villagerBazaar.prompts;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

import java.util.Map;
import java.util.function.Consumer;

public class BStringPrompt extends BPrompt {

    public BStringPrompt(String promptText) {
        super(promptText);
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {
        conversationContext.setSessionData(objectKey, s);
        
        if (nextPrompt == null) {
            onComplete.accept(conversationContext.getAllSessionData());
        }
        
        return nextPrompt;
    }
}
