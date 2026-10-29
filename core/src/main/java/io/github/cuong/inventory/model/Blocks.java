package io.github.cuong.inventory.model;

import com.badlogic.gdx.graphics.Color;

public class Blocks {
    private final boolean[][] blocks;
    private final Color color;
    private final String textureName;
    private final boolean drawAsWholeShape;
    private final int rows;
    private final int cols;

    public Blocks(boolean[][] blocks, Color color) {
        this(blocks, color, null, false);
    }

    public Blocks(boolean[][] blocks, Color color, String textureName) {
        this(blocks, color, textureName, false);
    }

    public Blocks(boolean[][] blocks, Color color, String textureName, boolean drawAsWholeShape) {
        this.blocks = blocks;
        this.color = color;
        this.textureName = textureName;
        this.drawAsWholeShape = drawAsWholeShape;
        this.rows = blocks != null ? blocks.length : 0;
        this.cols = (blocks != null && blocks.length > 0) ? blocks[0].length : 0;
    }

    public boolean[][] getShape() {
        return this.blocks;
    }

    public Color getColor() {
        return this.color;
    }

    public int getRows() {
        return this.rows;
    }

    public int getCols() {
        return this.cols;
    }

    public String getTextureName() {
        return this.textureName;
    }

    public boolean isDrawAsWholeShape() {
        return this.drawAsWholeShape;
    }

    public Blocks rotateClockwise() {
        if (this.blocks == null || this.rows == 0 || this.cols == 0) {
            return this;
        }
        boolean[][] rotated = new boolean[cols][rows];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                rotated[c][rows - 1 - r] = this.blocks[r][c];
            }
        }
        return new Blocks(rotated, this.color, this.textureName, this.drawAsWholeShape);
    }
}
