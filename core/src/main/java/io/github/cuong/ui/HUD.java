package io.github.cuong.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.cuong.entities.Player;
import io.github.cuong.managers.TimeManager;

public class HUD {
    private final float virtualWidth;
    private final float virtualHeight;

    public HUD(float virtualWidth, float virtualHeight) {
        this.virtualWidth = virtualWidth;
        this.virtualHeight = virtualHeight;
    }

    public void renderShapes(ShapeRenderer shapeRenderer, Player player) {
        shapeRenderer.getProjectionMatrix().setToOrtho2D(0, 0, virtualWidth, virtualHeight);
    }

    public void renderText(SpriteBatch batch, BitmapFont font, Player player, TimeManager timeManager) {
        batch.getProjectionMatrix().setToOrtho2D(0, 0, virtualWidth, virtualHeight);
        batch.begin();

        font.setColor(Color.YELLOW);
        font.draw(batch, "XOAI: " + player.mangoCount, 20, virtualHeight - 65);

        // Hiển thị ngày và giờ
        font.setColor(Color.CYAN);
        font.draw(batch, "NGAY: " + timeManager.currentDay + " / 7", virtualWidth - 140, virtualHeight - 20);
        font.draw(batch, "GIO: " + timeManager.getFormattedTime(), virtualWidth - 140, virtualHeight - 45);

        batch.end();
    }
}
