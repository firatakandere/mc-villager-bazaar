package com.kirpideleri.villagerBazaar.prompts;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

import java.util.Map;
import java.util.function.Consumer;

public abstract class BPrompt implements Prompt {
    protected Prompt nextPrompt = null;
    private String promptText;
    protected Object objectKey;

    public BPrompt(String promptText) {
        this.promptText = promptText;
    }

    public void setNextPrompt(Prompt prompt) {
        this.nextPrompt = prompt;
    }

    public void setObjectKey(Object objectKey) {
        this.objectKey = objectKey;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return promptText;
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {
        conversationContext.setSessionData(objectKey, s);

        return nextPrompt;
    }

    @Override
    public boolean blocksForInput(ConversationContext conversationContext) {
        return true;
    }
}
