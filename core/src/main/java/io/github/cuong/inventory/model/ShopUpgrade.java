package io.github.cuong.inventory.model;

import com.badlogic.gdx.graphics.Color;

public class ShopUpgrade {
    public enum Type {
        STAMINA,
        INVENTORY_LAYOUT
    }

    private final String id;
    private final String name;
    private final String description;
    private final int price;
    private final Type type;
    private final Color color;
    private final float staminaBonus;
    private final int templateIndex;
    private boolean purchased = false;

    public static ShopUpgrade createStaminaUpgrade(String id, String name, String description,
                                                   int price, Color color, float staminaBonus) {
        return new ShopUpgrade(id, name, description, price, Type.STAMINA, color, staminaBonus, -1);
    }

    public static ShopUpgrade createInventoryUpgrade(String id, String name, String description,
                                                     int price, Color color, int templateIndex) {
        return new ShopUpgrade(id, name, description, price, Type.INVENTORY_LAYOUT, color, 0f, templateIndex);
    }

    private ShopUpgrade(String id, String name, String description, int price,
                        Type type, Color color, float staminaBonus, int templateIndex) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
        this.color = color;
        this.staminaBonus = staminaBonus;
        this.templateIndex = templateIndex;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public Type getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }

    public float getStaminaBonus() {
        return staminaBonus;
    }

    public int getTemplateIndex() {
        return templateIndex;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }
}
