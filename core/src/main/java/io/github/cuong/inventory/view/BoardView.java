package io.github.cuong.inventory.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.cuong.inventory.asset.AssetLoader;
import io.github.cuong.inventory.config.GameConfig;
import io.github.cuong.inventory.model.Blocks;
import io.github.cuong.inventory.model.Board;
import io.github.cuong.inventory.model.PlacedBlock;

public class BoardView {
    private final Board boardLogic;
    private final Color emptyCellColor = new Color(0.25f, 0.25f, 0.35f, 1f);
    private final Color gridBorderColor = new Color(0.18f, 0.18f, 0.25f, 1f);
    private final Color highlightOk = new Color(0.2f, 0.9f, 0.2f, 0.55f);
    private final Color highlightFail = new Color(0.9f, 0.2f, 0.2f, 0.55f);
    private final Color fixedInnerColor = new Color(0.20f, 0.22f, 0.28f, 1f);
    private final Color fixedRivetColor = new Color(0.60f, 0.63f, 0.70f, 1f);

    public BoardView(Board boardLogic) {
        this.boardLogic = boardLogic;
    }

    public BoardView(Board boardLogic, ShapeRenderer ignored) {
        this.boardLogic = boardLogic;
    }

    // Vẽ bàn cờ bình thường (không highlight) - chỉ vẽ nền và lưới
    public void draw(ShapeRenderer shapeRenderer) {
        drawWithHighlight(shapeRenderer, -1, -1, null, false);
    }

    // Vẽ bàn cờ kèm highlight khi đang kéo khối - chỉ vẽ nền, lưới và highlight
    public void drawWithHighlight(ShapeRenderer shapeRenderer,
            int hoverRow, int hoverCol,
            Blocks hoverBlock, boolean canPlace) {
        Color[][] grid = boardLogic.getGrid();
        int rows = boardLogic.getHeight();
        int cols = boardLogic.getWidth();

        // 1. Viền nền bàn cờ
        shapeRenderer.setColor(gridBorderColor);
        shapeRenderer.rect(
                GameConfig.BOARD_START_X - 4,
                GameConfig.BOARD_START_Y - 4,
                cols * GameConfig.CELL_SIZE + 8,
                rows * GameConfig.CELL_SIZE + 8);

        // 2. Vẽ từng ô nền (không vẽ block cố định ở đây, sẽ vẽ với SpriteBatch)
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                float x = cellPixelX(c);
                float y = cellPixelY(r);

                Color cellColor = grid[r][c];
                PlacedBlock pb = boardLogic.getBlockAt(r, c);
                boolean isFixed = (pb != null && pb.isFixed());

                if (cellColor == null) {
                    shapeRenderer.setColor(emptyCellColor);
                    shapeRenderer.rect(
                            x + GameConfig.CELL_GAP / 2f,
                            y + GameConfig.CELL_GAP / 2f,
                            GameConfig.CELL_SIZE - GameConfig.CELL_GAP,
                            GameConfig.CELL_SIZE - GameConfig.CELL_GAP);
                } else {
                    // Vẽ nền ô
                    shapeRenderer.setColor(cellColor);
                    shapeRenderer.rect(
                            x + GameConfig.CELL_GAP / 2f,
                            y + GameConfig.CELL_GAP / 2f,
                            GameConfig.CELL_SIZE - GameConfig.CELL_GAP,
                            GameConfig.CELL_SIZE - GameConfig.CELL_GAP);

                    // Nếu là block cố định: vẽ hiệu ứng chìm (lõi và đinh) - vẫn dùng ShapeRenderer cho UI effect
                    if (isFixed) {
                        shapeRenderer.setColor(fixedInnerColor);
                        float innerGap = 6f;
                        shapeRenderer.rect(
                                x + GameConfig.CELL_GAP / 2f + innerGap,
                                y + GameConfig.CELL_GAP / 2f + innerGap,
                                GameConfig.CELL_SIZE - GameConfig.CELL_GAP - innerGap * 2,
                                GameConfig.CELL_SIZE - GameConfig.CELL_GAP - innerGap * 2);

                        shapeRenderer.setColor(fixedRivetColor);
                        float rSize = 3f;
                        float pad = 4f;
                        shapeRenderer.rect(x + pad, y + pad, rSize, rSize);
                        shapeRenderer.rect(x + GameConfig.CELL_SIZE - pad - rSize, y + pad, rSize, rSize);
                        shapeRenderer.rect(x + pad, y + GameConfig.CELL_SIZE - pad - rSize, rSize, rSize);
                        shapeRenderer.rect(x + GameConfig.CELL_SIZE - pad - rSize,
                                y + GameConfig.CELL_SIZE - pad - rSize, rSize, rSize);
                    }
                }
            }
        }

        // 3. Overlay highlight ô sẽ đặt khối (vẫn dùng ShapeRenderer cho highlight tạm thời)
        if (hoverBlock != null) {
            boolean[][] shape = hoverBlock.getShape();
            Color hlColor = canPlace ? highlightOk : highlightFail;
            shapeRenderer.setColor(hlColor);
            for (int i = 0; i < hoverBlock.getRows(); i++) {
                for (int j = 0; j < hoverBlock.getCols(); j++) {
                    if (shape[i][j]) {
                        int tr = hoverRow + i;
                        int tc = hoverCol + j;
                        if (tr >= 0 && tr < rows && tc >= 0 && tc < cols) {
                            shapeRenderer.rect(
                                    cellPixelX(tc) + GameConfig.CELL_GAP / 2f,
                                    cellPixelY(tr) + GameConfig.CELL_GAP / 2f,
                                    GameConfig.CELL_SIZE - GameConfig.CELL_GAP,
                                    GameConfig.CELL_SIZE - GameConfig.CELL_GAP);
                        }
                    }
                }
            }
        }
    }

    // Vẽ các khối đã đặt (placed blocks) với SpriteBatch và textures
    public void drawPlacedBlocks(SpriteBatch batch) {
        Color[][] grid = boardLogic.getGrid();
        int rows = boardLogic.getHeight();
        int cols = boardLogic.getWidth();

        float cellSize = GameConfig.CELL_SIZE;
        float gap = GameConfig.CELL_GAP;

        // Track which blocks we've already drawn to avoid drawing the same whole-shape multiple times
        boolean[][] drawn = new boolean[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (drawn[r][c]) continue;

                PlacedBlock pb = boardLogic.getBlockAt(r, c);
                if (pb != null && !pb.isFixed()) {
                    // Chỉ vẽ các block không cố định (có thể di chuyển) với texture
                    Blocks block = pb.getBlock();
                    String textureName = block.getTextureName();
                    TextureRegion region = textureName != null ? AssetLoader.getInstance().getTextureRegion(textureName) : null;

                    if (region == null) {
                        region = AssetLoader.getInstance().getTextureRegion("block");
                    }

                    // Check if this block should be drawn as a whole shape
                    if (block.isDrawAsWholeShape()) {
                        // Draw as single texture covering entire shape
                        int blockRows = block.getRows();
                        int blockCols = block.getCols();
                        float totalWidth = blockCols * cellSize;
                        float totalHeight = blockRows * cellSize;

                        // Get the top-left cell of this placed block
                        float x = cellPixelX(pb.getCol());
                        float y = cellPixelY(pb.getRow() + blockRows - 1);

                        batch.draw(region,
                                x + gap / 2f,
                                y + gap / 2f,
                                totalWidth - gap,
                                totalHeight - gap);

                        // Mark all cells of this block as drawn
                        for (int br = 0; br < blockRows; br++) {
                            for (int bc = 0; bc < blockCols; bc++) {
                                int gr = pb.getRow() + br;
                                int gc = pb.getCol() + bc;
                                if (gr >= 0 && gr < rows && gc >= 0 && gc < cols) {
                                    drawn[gr][gc] = true;
                                }
                            }
                        }
                    } else {
                        // Original per-cell drawing
                        float drawSize = cellSize - gap;
                        float x = cellPixelX(c);
                        float y = cellPixelY(r);

                        batch.draw(region,
                                x + gap / 2f,
                                y + gap / 2f,
                                drawSize,
                                drawSize);
                        drawn[r][c] = true;
                    }
                }
            }
        }
    }

    // --- Pixel của góc dưới-trái ô (col, row) ---
    public float cellPixelX(int col) {
        return GameConfig.BOARD_START_X + col * GameConfig.CELL_SIZE;
    }

    public float cellPixelY(int row) {
        return GameConfig.BOARD_START_Y + (boardLogic.getHeight() - 1 - row) * GameConfig.CELL_SIZE;
    }

    // --- Pixel tọa độ góc dưới-trái của cả khối (tính theo ô trên cùng bên trái) ---
    // ---
    public float colToPixelX(int col) {
        return cellPixelX(col);
    }

    public float rowToPixelY(int topRow, int shapeRows) {
        return cellPixelY(topRow + shapeRows - 1);
    }

    // --- Chuyển đổi pixel → hàng/cột logic ---
    public int pixelToCol(float pixelX) {
        return Math.round((pixelX - GameConfig.BOARD_START_X) / GameConfig.CELL_SIZE);
    }

    public int pixelToRow(float pixelY, int shapeRows) {
        return boardLogic.getHeight() - shapeRows
                - Math.round((pixelY - GameConfig.BOARD_START_Y) / GameConfig.CELL_SIZE);
    }
}