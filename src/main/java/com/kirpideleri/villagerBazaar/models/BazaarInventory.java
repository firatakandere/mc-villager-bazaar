package com.kirpideleri.villagerBazaar.models;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.IntStream;

public class BazaarInventory implements ConfigurationSerializable, Iterable<ItemStack> {
    private List<ItemStack> itemStackList = new ArrayList<>(27);

    // Configuration Constants
    final static private String ITEM_STACKS = "ITEM_STACKS";

    public BazaarInventory() {
        IntStream.range(0, 27).forEach(i -> itemStackList.add(null));
    }

    public void set(int index, ItemStack itemStack) {
        itemStackList.set(index, itemStack);
    }

    public ItemStack get(int index) { return itemStackList.get(index); }

    public BazaarInventory(Map<String, Object> serialized) { // for deserialization
        IntStream.range(0, 27).forEach(i -> itemStackList.add(null));
        itemStackList = new ArrayList<>((List<ItemStack>)serialized.get(ITEM_STACKS));
    }

    public boolean containsAtLeast(ItemStack itemStack, int amount) {
        for (ItemStack is : itemStackList) {
            if (is.isSimilar(itemStack)) {
                amount -= is.getAmount();

                if (amount <= 0) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean tryRemoveWithAmount(ItemStack itemStack, int amount) {
        int counter = 0;
        boolean amountMet = false;

        Map<Integer, Integer> stackMap = new HashMap<>();

        synchronized (itemStackList) {
            // Run dry test
            for (int i = 0; i < itemStackList.size(); i++) {
                ItemStack currentItemStack = itemStackList.get(i);

                if (currentItemStack.isSimilar(itemStack)) {
                    counter += currentItemStack.getAmount();

                    if (counter < amount) {
                        stackMap.put(i, -1); // -1 means remove all from stack
                    }
                    else if (counter > amount) {
                        int amountToBeDecreased = currentItemStack.getAmount() - (counter - amount);
                        stackMap.put(i, amountToBeDecreased);
                        amountMet = true;
                        break;
                    }
                    else {
                        amountMet = true;
                        break;
                    }
                }
            }

            // Actually remove if amount is met
            if (amountMet) {
                for (Map.Entry<Integer, Integer> stackEntry : stackMap.entrySet()) {
                    if (stackEntry.getValue() == -1) {
                        itemStackList.remove((int)stackEntry.getKey());
                    }
                    else {
                        ItemStack is = itemStackList.get((int)stackEntry.getKey());
                        is.setAmount(is.getAmount() - stackEntry.getValue());
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put(ITEM_STACKS, itemStackList);
        return serialized;
    }

    @Override
    public Iterator<ItemStack> iterator() {
        return itemStackList.iterator();
    }
}
