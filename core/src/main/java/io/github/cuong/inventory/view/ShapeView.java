package io.github.cuong.inventory.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.cuong.inventory.config.GameConfig;
import io.github.cuong.inventory.model.Blocks;

public class ShapeView {
    private final Blocks shapeLogic;
    private float x;
    private float y;
    private final float totalWidth;
    private final float totalHeight;

    public ShapeView(Blocks shapeLogic, float startX, float startY) {
        this.shapeLogic = shapeLogic;
        this.x = startX;
        this.y = startY;

        int cols = shapeLogic.getCols();
        int rows = shapeLogic.getRows();

        this.totalWidth = cols * GameConfig.CELL_SIZE;
        this.totalHeight = rows * GameConfig.CELL_SIZE;
    }

    // Vẽ hình khối
    public void draw(ShapeRenderer shapeRenderer) {
        boolean[][] layout = shapeLogic.getShape();
        int rows = shapeLogic.getRows();
        int cols = shapeLogic.getCols();

        Color color = shapeLogic.getColor();
        shapeRenderer.setColor(color != null ? color : Color.WHITE);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (layout[r][c]) {
                    float blockX = x + (c * GameConfig.CELL_SIZE);
                    // Lật trục Y vì mảng logic đi từ trên xuống, còn LibGDX đi từ dưới lên
                    float blockY = y + ((rows - 1 - r) * GameConfig.CELL_SIZE);

                    shapeRenderer.rect(
                            blockX + GameConfig.CELL_GAP / 2f,
                            blockY + GameConfig.CELL_GAP / 2f,
                            GameConfig.CELL_SIZE - GameConfig.CELL_GAP,
                            GameConfig.CELL_SIZE - GameConfig.CELL_GAP);
                }
            }
        }
    }

    public void setPosition(float newX, float newY) {
        this.x = newX;
        this.y = newY;
    }

    public boolean contains(float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x + totalWidth &&
                mouseY >= y && mouseY <= y + totalHeight;
    }

    public Blocks getShapeLogic() {
        return shapeLogic;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getTotalWidth() {
        return totalWidth;
    }

    public float getTotalHeight() {
        return totalHeight;
    }
}
