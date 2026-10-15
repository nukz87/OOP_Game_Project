package io.github.cuong.inventory.model;

import com.badlogic.gdx.graphics.Color;

public class Blocks {
    private final boolean[][] blocks;
    private final Color color;
    private final int rows;
    private final int cols;

    public Blocks(boolean[][] blocks, Color color) {
        this.blocks = blocks;
        this.color = color;
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
        return new Blocks(rotated, this.color);
    }
}
