package io.github.cuong.inventory.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.cuong.inventory.config.GameConfig;
import io.github.cuong.inventory.model.Blocks;
import java.util.ArrayList;
import java.util.List;

public class TrayView {
    private final List<ShapeView> shapeViews = new ArrayList<>();
    private final float centerX;
    private final float centerY;
    private final Color boxBgColor = new Color(0.18f, 0.18f, 0.25f, 1f);
    private final Color boxBorderColor = new Color(0.28f, 0.28f, 0.38f, 1f);

    public TrayView() {
        this(GameConfig.TRAY_CENTER_X, GameConfig.TRAY_CENTER_Y);
    }

    public TrayView(float centerX, float centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public void updateTray(Blocks shapeLogic) {
        shapeViews.clear();
        if (shapeLogic == null)
            return;

        ShapeView sv = new ShapeView(shapeLogic, 0, 0);
        // Căn giữa khối hình vào vùng khay
        float startX = centerX - (sv.getTotalWidth() / 2f);
        float startY = centerY - (sv.getTotalHeight() / 2f);
        sv.setPosition(startX, startY);

        shapeViews.add(sv);
    }

    public void clear() {
        shapeViews.clear();
    }

    public boolean hasShape() {
        return !shapeViews.isEmpty();
    }

    // Vẽ nền khay (background) với ShapeRenderer
    public void drawBackground(ShapeRenderer shapeRenderer) {
        float boxHalf = GameConfig.TRAY_BOX_SIZE / 2f;
        shapeRenderer.setColor(boxBorderColor);
        shapeRenderer.rect(
                centerX - boxHalf - 2,
                centerY - boxHalf - 2,
                GameConfig.TRAY_BOX_SIZE + 4,
                GameConfig.TRAY_BOX_SIZE + 4);
        shapeRenderer.setColor(boxBgColor);
        shapeRenderer.rect(
                centerX - boxHalf,
                centerY - boxHalf,
                GameConfig.TRAY_BOX_SIZE,
                GameConfig.TRAY_BOX_SIZE);
    }

    // Vẽ các khối hình (shapes) với SpriteBatch
    public void drawShapes(SpriteBatch batch) {
        for (ShapeView sv : shapeViews) {
            sv.draw(batch);
        }
    }

    // Legacy method for backward compatibility (if needed)
    public void draw(ShapeRenderer shapeRenderer) {
        drawBackground(shapeRenderer);
        // Note: shapes are now drawn with SpriteBatch, not here
    }

    public ShapeView getTouchedShape(float mouseX, float mouseY) {
        for (ShapeView sv : shapeViews) {
            if (sv.contains(mouseX, mouseY)) {
                return sv;
            }
        }
        return null;
    }
}
