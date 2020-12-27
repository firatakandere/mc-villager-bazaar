package github.fakandere.villagerBazaar.prompts;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

public class BNumericPrompt extends BPrompt {

    private boolean mustBeInteger = false;
    private boolean mustBePositive = false;

    public BNumericPrompt(String promptText) {
        super(promptText);
    }

    public BNumericPrompt(String promptText, boolean mustBeInteger, boolean mustBePositive) {
        super(promptText);
        this.mustBeInteger = mustBeInteger;
        this.mustBePositive = mustBePositive;
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {

        if (mustBeInteger) {
            try {
                int value = Integer.parseInt(s);

                if (mustBePositive && value <= 0) {
                    conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "Please enter a positive integer.");
                    return this;
                }

                conversationContext.setSessionData(objectKey, value);
            } catch (NumberFormatException e) {
                conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "Unexpected input. Please provide a positive integer.");
                return this;
            }
        } else {
            try {
                double value = Double.parseDouble(s);

                if (mustBePositive && value <= 0) {
                    conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "Please enter a positive integer.");
                    return this;
                }

                conversationContext.setSessionData(objectKey, value);
            } catch (NumberFormatException e) {
                conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "Unexpected input. Please provide a positive integer.");
                return this;
            }
        }


        if (onComplete != null) {
            onComplete.accept(conversationContext.getAllSessionData());
        }
        return nextPrompt;
    }
}
