package io.github.cuong.inventory.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.cuong.inventory.model.PlacedBlock;

public class ShopView {
    // O don gian ben trai de hien gia tien cua block dang chi toi
    public static final float PANEL_X = 40f;
    public static final float PANEL_Y = 190f;
    public static final float PANEL_WIDTH = 180f;
    public static final float PANEL_HEIGHT = 100f;

    private final Color boxBg = new Color(0.14f, 0.16f, 0.22f, 0.95f);
    private final Color boxBorder = new Color(0.35f, 0.40f, 0.55f, 1f);
    private final Color boxHighlight = new Color(0.2f, 0.8f, 0.4f, 1f);

    public boolean isInsideShopPanel(float mouseX, float mouseY) {
        return mouseX >= PANEL_X && mouseX <= (PANEL_X + PANEL_WIDTH) &&
                mouseY >= PANEL_Y && mouseY <= (PANEL_Y + PANEL_HEIGHT);
    }

    public void drawBackground(ShapeRenderer shapeRenderer, boolean isHoveringShop, PlacedBlock hoveredBlock) {
        Color border = (hoveredBlock != null || isHoveringShop) ? boxHighlight : boxBorder;
        shapeRenderer.setColor(border);
        shapeRenderer.rect(PANEL_X - 2, PANEL_Y - 2, PANEL_WIDTH + 4, PANEL_HEIGHT + 4);

        shapeRenderer.setColor(boxBg);
        shapeRenderer.rect(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT);
    }

    public void drawTextAndAvatar(SpriteBatch batch, BitmapFont font,
                                  PlacedBlock hoveredBlock,
                                  int playerGold,
                                  String notificationText,
                                  float notificationTimer) {
        font.setColor(Color.WHITE);
        font.draw(batch, "GIA BAN:", PANEL_X + 20f, PANEL_Y + 72f);

        if (hoveredBlock != null) {
            font.setColor(Color.YELLOW);
            font.draw(batch, hoveredBlock.getBlock().getPrice() + " G", PANEL_X + 20f, PANEL_Y + 42f);
        } else {
            font.setColor(Color.GRAY);
            font.draw(batch, "---", PANEL_X + 20f, PANEL_Y + 42f);
        }

        if (notificationTimer > 0f && notificationText != null) {
            font.setColor(Color.GREEN);
            font.draw(batch, notificationText, PANEL_X, PANEL_Y - 15f);
        }
    }
}
