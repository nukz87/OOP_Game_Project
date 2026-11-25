package io.github.cuong.inventory.view;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.cuong.inventory.asset.AssetLoader;
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

    // Vẽ hình khối bằng texture
    public void draw(SpriteBatch batch) {
        boolean[][] layout = shapeLogic.getShape();
        int rows = shapeLogic.getRows();
        int cols = shapeLogic.getCols();

        // Lấy texture region cho khối này (kèm góc xoay)
        String textureName = shapeLogic.getTextureName();
        TextureRegion region = textureName != null ? AssetLoader.getInstance().getTextureRegion(textureName, shapeLogic.getRotation()) : null;

        // Fallback to generic block texture
        if (region == null) {
            region = AssetLoader.getInstance().getTextureRegion("block");
        }

        float cellSize = GameConfig.CELL_SIZE;
        float gap = GameConfig.CELL_GAP;

        // Check if we should draw as a single whole-shape texture
        if (shapeLogic.isDrawAsWholeShape()) {
            // Draw single texture covering the entire shape bounds
            batch.draw(region,
                    x + gap / 2f,
                    y + gap / 2f,
                    totalWidth - gap,
                    totalHeight - gap);
        } else {
            // Original per-cell drawing (tile the texture for each cell)
            float drawSize = cellSize - gap;

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (layout[r][c]) {
                        float blockX = x + (c * cellSize);
                        // Lật trục Y vì mảng logic đi từ trên xuống, còn LibGDX đi từ dưới lên
                        float blockY = y + ((rows - 1 - r) * cellSize);

                        batch.draw(region,
                                blockX + gap / 2f,
                                blockY + gap / 2f,
                                drawSize,
                                drawSize);
                    }
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
