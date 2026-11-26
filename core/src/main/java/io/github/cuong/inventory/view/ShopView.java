package io.github.cuong.inventory.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.cuong.inventory.config.GameConfig;
import io.github.cuong.inventory.model.PlacedBlock;
import io.github.cuong.inventory.model.ShopUpgrade;
import java.util.List;

public class ShopView {
    public enum ShopTab {
        SELL,
        UPGRADE
    }

    // O ben trai de hien gia ban / thong tin nang cap
    public static final float PANEL_X = 40f;
    public static final float PANEL_Y = 175f;
    public static final float PANEL_WIDTH = 180f;
    public static final float PANEL_HEIGHT = 120f;

    // 2 Tab phia tren o ben trai
    public static final float TAB_Y = 310f;
    public static final float TAB_WIDTH = 86f;
    public static final float TAB_HEIGHT = 34f;
    public static final float TAB_SELL_X = 40f;
    public static final float TAB_UPGRADE_X = 134f;

    // Vung ke hang ben phai (cung vi tri voi Board)
    public static final float SHELF_AREA_X = GameConfig.BOARD_START_X;
    public static final float SHELF_AREA_Y = GameConfig.BOARD_START_Y;
    public static final float SHELF_AREA_W = GameConfig.BOARD_COLS * GameConfig.CELL_SIZE
            + (GameConfig.BOARD_COLS - 1) * GameConfig.CELL_GAP;
    public static final float SHELF_AREA_H = GameConfig.BOARD_ROWS * GameConfig.CELL_SIZE
            + (GameConfig.BOARD_ROWS - 1) * GameConfig.CELL_GAP;

    private static final int SHELF_COLS = 3;
    private static final int SHELF_ROWS = 3;
    private static final float ITEM_SIZE = 54f;

    private final Color boxBg = new Color(0.14f, 0.16f, 0.22f, 0.95f);
    private final Color boxBorder = new Color(0.35f, 0.40f, 0.55f, 1f);
    private final Color boxHighlight = new Color(0.2f, 0.8f, 0.4f, 1f);
    private final Color tabActiveBg = new Color(0.24f, 0.46f, 0.34f, 1f);
    private final Color tabInactiveBg = new Color(0.16f, 0.18f, 0.25f, 1f);
    private final Color shelfWood = new Color(0.42f, 0.28f, 0.16f, 1f);
    private final Color shelfShadow = new Color(0.25f, 0.16f, 0.09f, 1f);

    public boolean isInsideShopPanel(float mouseX, float mouseY) {
        return mouseX >= PANEL_X && mouseX <= (PANEL_X + PANEL_WIDTH) &&
                mouseY >= PANEL_Y && mouseY <= (PANEL_Y + PANEL_HEIGHT);
    }

    public ShopTab getTouchedTab(float mouseX, float mouseY) {
        if (mouseY >= TAB_Y && mouseY <= TAB_Y + TAB_HEIGHT) {
            if (mouseX >= TAB_SELL_X && mouseX <= TAB_SELL_X + TAB_WIDTH) {
                return ShopTab.SELL;
            }
            if (mouseX >= TAB_UPGRADE_X && mouseX <= TAB_UPGRADE_X + TAB_WIDTH) {
                return ShopTab.UPGRADE;
            }
        }
        return null;
    }

    private float getSlotX(int col) {
        float spacing = (SHELF_AREA_W - SHELF_COLS * ITEM_SIZE) / (SHELF_COLS + 1);
        return SHELF_AREA_X + spacing + col * (ITEM_SIZE + spacing);
    }

    private float getSlotY(int row) {
        // row 0 la ke tren cung, row 2 la ke duoi cung
        float rowHeight = SHELF_AREA_H / SHELF_ROWS;
        return SHELF_AREA_Y + SHELF_AREA_H - (row + 1) * rowHeight + 24f;
    }

    public ShopUpgrade getUpgradeAt(float mouseX, float mouseY, List<ShopUpgrade> upgrades) {
        if (upgrades == null) {
            return null;
        }
        for (int i = 0; i < upgrades.size() && i < SHELF_COLS * SHELF_ROWS; i++) {
            ShopUpgrade upg = upgrades.get(i);
            if (upg.isPurchased()) {
                continue;
            }
            int row = i / SHELF_COLS;
            int col = i % SHELF_COLS;
            float x = getSlotX(col);
            float y = getSlotY(row);
            if (mouseX >= x && mouseX <= x + ITEM_SIZE && mouseY >= y && mouseY <= y + ITEM_SIZE) {
                return upg;
            }
        }
        return null;
    }

    public void drawTabsShapes(ShapeRenderer shapeRenderer, ShopTab activeTab) {
        // Tab BAN DO
        shapeRenderer.setColor(activeTab == ShopTab.SELL ? boxHighlight : boxBorder);
        shapeRenderer.rect(TAB_SELL_X - 2, TAB_Y - 2, TAB_WIDTH + 4, TAB_HEIGHT + 4);
        shapeRenderer.setColor(activeTab == ShopTab.SELL ? tabActiveBg : tabInactiveBg);
        shapeRenderer.rect(TAB_SELL_X, TAB_Y, TAB_WIDTH, TAB_HEIGHT);

        // Tab NANG CAP
        shapeRenderer.setColor(activeTab == ShopTab.UPGRADE ? boxHighlight : boxBorder);
        shapeRenderer.rect(TAB_UPGRADE_X - 2, TAB_Y - 2, TAB_WIDTH + 4, TAB_HEIGHT + 4);
        shapeRenderer.setColor(activeTab == ShopTab.UPGRADE ? tabActiveBg : tabInactiveBg);
        shapeRenderer.rect(TAB_UPGRADE_X, TAB_Y, TAB_WIDTH, TAB_HEIGHT);
    }

    public void drawTabsText(SpriteBatch batch, BitmapFont font, ShopTab activeTab) {
        font.setColor(activeTab == ShopTab.SELL ? Color.WHITE : Color.LIGHT_GRAY);
        font.draw(batch, "BAN DO", TAB_SELL_X + 14f, TAB_Y + 22f);

        font.setColor(activeTab == ShopTab.UPGRADE ? Color.WHITE : Color.LIGHT_GRAY);
        font.draw(batch, "NANG CAP", TAB_UPGRADE_X + 7f, TAB_Y + 22f);
    }

    public void drawBackground(ShapeRenderer shapeRenderer, boolean isHoveringShop, PlacedBlock hoveredBlock) {
        Color border = (hoveredBlock != null || isHoveringShop) ? boxHighlight : boxBorder;
        shapeRenderer.setColor(border);
        shapeRenderer.rect(PANEL_X - 2, PANEL_Y - 2, PANEL_WIDTH + 4, PANEL_HEIGHT + 4);

        shapeRenderer.setColor(boxBg);
        shapeRenderer.rect(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT);
    }

    public void drawUpgradeInfoBackground(ShapeRenderer shapeRenderer, ShopUpgrade hoveredUpgrade) {
        Color border = (hoveredUpgrade != null) ? boxHighlight : boxBorder;
        shapeRenderer.setColor(border);
        shapeRenderer.rect(PANEL_X - 2, PANEL_Y - 2, PANEL_WIDTH + 4, PANEL_HEIGHT + 4);

        shapeRenderer.setColor(boxBg);
        shapeRenderer.rect(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT);
    }

    public void drawShelvesShapes(ShapeRenderer shapeRenderer, List<ShopUpgrade> upgrades, ShopUpgrade hoveredUpgrade) {
        // Nen ke hang
        shapeRenderer.setColor(boxBorder);
        shapeRenderer.rect(SHELF_AREA_X - 2, SHELF_AREA_Y - 2, SHELF_AREA_W + 4, SHELF_AREA_H + 4);
        shapeRenderer.setColor(boxBg);
        shapeRenderer.rect(SHELF_AREA_X, SHELF_AREA_Y, SHELF_AREA_W, SHELF_AREA_H);

        // Ve 3 tang ke go
        for (int r = 0; r < SHELF_ROWS; r++) {
            float shelfY = getSlotY(r) - 10f;
            shapeRenderer.setColor(shelfShadow);
            shapeRenderer.rect(SHELF_AREA_X + 12f, shelfY - 4f, SHELF_AREA_W - 24f, 4f);
            shapeRenderer.setColor(shelfWood);
            shapeRenderer.rect(SHELF_AREA_X + 12f, shelfY, SHELF_AREA_W - 24f, 8f);
        }

        // Ve cac vat pham nang cap (o vuong) tren ke
        if (upgrades == null) {
            return;
        }
        for (int i = 0; i < upgrades.size() && i < SHELF_COLS * SHELF_ROWS; i++) {
            ShopUpgrade upg = upgrades.get(i);
            if (upg.isPurchased()) {
                continue;
            }
            int row = i / SHELF_COLS;
            int col = i % SHELF_COLS;
            float x = getSlotX(col);
            float y = getSlotY(row);

            boolean isHovered = (upg == hoveredUpgrade);
            shapeRenderer.setColor(isHovered ? Color.YELLOW : Color.DARK_GRAY);
            shapeRenderer.rect(x - 3f, y - 3f, ITEM_SIZE + 6f, ITEM_SIZE + 6f);

            shapeRenderer.setColor(upg.getColor());
            shapeRenderer.rect(x, y, ITEM_SIZE, ITEM_SIZE);
        }
    }

    public void drawShelvesText(SpriteBatch batch, BitmapFont font, List<ShopUpgrade> upgrades) {
        font.setColor(Color.WHITE);
        font.draw(batch, "KE HANG NANG CAP", SHELF_AREA_X + 95f, SHELF_AREA_Y + SHELF_AREA_H + 24f);

        if (upgrades == null) {
            return;
        }
        for (int i = 0; i < upgrades.size() && i < SHELF_COLS * SHELF_ROWS; i++) {
            ShopUpgrade upg = upgrades.get(i);
            if (upg.isPurchased()) {
                continue;
            }
            int row = i / SHELF_COLS;
            int col = i % SHELF_COLS;
            float x = getSlotX(col);
            float y = getSlotY(row);

            // Hien gia nho ngay duoi thanh ke
            font.setColor(Color.GOLD);
            font.draw(batch, upg.getPrice() + "G", x + 10f, y - 13f);
        }
    }

    public void drawTextAndAvatar(SpriteBatch batch, BitmapFont font,
                                  PlacedBlock hoveredBlock,
                                  int playerGold,
                                  String notificationText,
                                  float notificationTimer,
                                  Color notificationColor) {
        font.setColor(Color.WHITE);
        font.draw(batch, "GIA BAN:", PANEL_X + 20f, PANEL_Y + 92f);

        if (hoveredBlock != null) {
            font.setColor(Color.YELLOW);
            font.draw(batch, hoveredBlock.getBlock().getPrice() + " G", PANEL_X + 20f, PANEL_Y + 62f);
        } else {
            font.setColor(Color.GRAY);
            font.draw(batch, "---", PANEL_X + 20f, PANEL_Y + 62f);
        }

        drawNotification(batch, font, notificationText, notificationTimer, notificationColor);
    }

    public void drawUpgradeInfoText(SpriteBatch batch, BitmapFont font,
                                    ShopUpgrade hoveredUpgrade,
                                    String notificationText,
                                    float notificationTimer,
                                    Color notificationColor) {
        font.setColor(Color.WHITE);
        font.draw(batch, "GIA NANG CAP:", PANEL_X + 15f, PANEL_Y + 96f);

        if (hoveredUpgrade != null) {
            font.setColor(Color.YELLOW);
            font.draw(batch, hoveredUpgrade.getPrice() + " G", PANEL_X + 15f, PANEL_Y + 72f);

            font.setColor(Color.CYAN);
            font.draw(batch, hoveredUpgrade.getName(), PANEL_X + 15f, PANEL_Y + 46f);

            font.setColor(Color.LIGHT_GRAY);
            font.draw(batch, hoveredUpgrade.getDescription(), PANEL_X + 15f, PANEL_Y + 24f);
        } else {
            font.setColor(Color.GRAY);
            font.draw(batch, "---", PANEL_X + 15f, PANEL_Y + 72f);
        }

        drawNotification(batch, font, notificationText, notificationTimer, notificationColor);
    }

    private void drawNotification(SpriteBatch batch, BitmapFont font,
                                  String notificationText, float notificationTimer, Color notificationColor) {
        if (notificationTimer > 0f && notificationText != null) {
            font.setColor(notificationColor != null ? notificationColor : Color.GREEN);
            font.draw(batch, notificationText, PANEL_X, PANEL_Y - 15f);
        }
    }
}
