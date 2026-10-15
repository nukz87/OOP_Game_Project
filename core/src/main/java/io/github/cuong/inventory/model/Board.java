package io.github.cuong.inventory.model;

import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;
import java.util.List;

public class Board {
    private final Color[][] grid;
    private final int width;
    private final int height;
    private final List<PlacedBlock> placedBlocks = new ArrayList<>();
    private InventoryTemplate currentTemplate;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Color[height][width];
    }

    public Color[][] getGrid() {
        return grid;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<PlacedBlock> getPlacedBlocks() {
        return placedBlocks;
    }

    // Tái tạo lại mảng grid màu từ danh sách các khối gạch đã đặt
    private void rebuildGrid() {
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                grid[r][c] = null;
            }
        }
        for (PlacedBlock pb : placedBlocks) {
            Blocks block = pb.getBlock();
            boolean[][] shape = block.getShape();
            Color color = block.getColor();
            int rows = block.getRows();
            int cols = block.getCols();

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (shape[i][j]) {
                        grid[pb.getRow() + i][pb.getCol() + j] = color;
                    }
                }
            }
        }
    }

    // Kiểm tra xem khối có thể đặt được vào vị trí (startRow, startCol) không
    public boolean canPlace(Blocks block, int startRow, int startCol) {
        if (block == null)
            return false;
        boolean[][] shape = block.getShape();
        int rows = block.getRows();
        int cols = block.getCols();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (shape[i][j]) {
                    int r = startRow + i;
                    int c = startCol + j;
                    if (r < 0 || r >= this.height || c < 0 || c >= this.width || this.grid[r][c] != null) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Đặt khối gạch vào bàn cờ (mặc định là khối người chơi di chuyển được)
    public boolean placeBlock(Blocks block, int startRow, int startCol) {
        return placeBlock(block, startRow, startCol, false);
    }

    // Đặt khối gạch vào bàn cờ với cờ isFixed (cố định hay không)
    public boolean placeBlock(Blocks block, int startRow, int startCol, boolean isFixed) {
        if (!canPlace(block, startRow, startCol)) {
            return false;
        }
        placedBlocks.add(new PlacedBlock(block, startRow, startCol, isFixed));
        rebuildGrid();
        return true;
    }

    // Tìm khối gạch đang chiếm ô (row, col) trên bàn cờ
    public PlacedBlock getBlockAt(int row, int col) {
        for (PlacedBlock pb : placedBlocks) {
            if (pb.occupies(row, col)) {
                return pb;
            }
        }
        return null;
    }

    // Nhấc khối gạch ra khỏi bàn cờ (để người chơi di chuyển/sắp xếp lại)
    // Khối cố định (isFixed = true) sẽ được bảo vệ tuyệt đối, không thể bị xóa
    public void removeBlock(PlacedBlock pb) {
        if (pb == null || pb.isFixed()) {
            return;
        }
        placedBlocks.remove(pb);
        rebuildGrid();
    }

    // Nạp một template kho đồ mới
    public void loadTemplate(InventoryTemplate template) {
        this.currentTemplate = template;
        placedBlocks.clear();
        if (template != null) {
            for (PlacedBlock fb : template.getFixedBlocks()) {
                placedBlocks.add(new PlacedBlock(fb.getBlock(), fb.getRow(), fb.getCol(), true));
            }
        }
        rebuildGrid();
    }

    public InventoryTemplate getCurrentTemplate() {
        return currentTemplate;
    }

    // Xóa các khối do người chơi đặt, giữ nguyên các khối cố định của template
    public void clearPlayerBlocks() {
        placedBlocks.removeIf(pb -> !pb.isFixed());
        rebuildGrid();
    }

    // Reset bàn cờ về trạng thái ban đầu của template hiện tại
    public void reset() {
        if (currentTemplate != null) {
            loadTemplate(currentTemplate);
        } else {
            placedBlocks.clear();
            rebuildGrid();
        }
    }
}
