package io.github.cuong.inventory.model;

public class PlacedBlock {
    private final Blocks block;
    private int row;
    private int col;
    private final boolean fixed;

    public PlacedBlock(Blocks block, int row, int col) {
        this(block, row, col, false);
    }

    public PlacedBlock(Blocks block, int row, int col, boolean fixed) {
        this.block = block;
        this.row = row;
        this.col = col;
        this.fixed = fixed;
    }

    public Blocks getBlock() {
        return block;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public boolean isFixed() {
        return fixed;
    }

    // Kiểm tra xem ô (r, c) có thuộc về khối gạch này không
    public boolean occupies(int r, int c) {
        if (r < row || r >= row + block.getRows() || c < col || c >= col + block.getCols()) {
            return false;
        }
        int shapeR = r - row;
        int shapeC = c - col;
        return block.getShape()[shapeR][shapeC];
    }
}
