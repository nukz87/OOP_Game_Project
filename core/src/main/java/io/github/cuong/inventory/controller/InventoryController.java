package io.github.cuong.inventory.controller;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.cuong.inventory.config.GameConfig;
import io.github.cuong.inventory.model.Blocks;
import io.github.cuong.inventory.model.BlocksManager;
import io.github.cuong.inventory.model.Board;
import io.github.cuong.inventory.model.InventoryTemplate;
import io.github.cuong.inventory.model.InventoryTemplates;
import io.github.cuong.inventory.model.PlacedBlock;
import io.github.cuong.inventory.view.BoardView;
import io.github.cuong.inventory.view.ButtonView;
import io.github.cuong.entities.Player;
import io.github.cuong.inventory.view.ShapeView;
import io.github.cuong.inventory.view.TrayView;

public class InventoryController {
    private OrthographicCamera camera;
    private Viewport viewport;

    private Board board;
    private BoardView boardView;
    private BlocksManager blocksManager;
    private TrayView trayView;
    private ButtonView nextPieceButton;

    // Biến quản lý template hiện tại
    private int currentTemplateIndex = 1; // Khởi đầu với Template Bo 4 góc để thấy rõ khối cố định

    // Biến quản lý hiển thị các phân vùng màn chơi
    private boolean showTray = false;
    private boolean showBoard = false;

    // Biến quản lý kéo-thả: dùng chung cho cả khối từ khay lẫn khối đã đặt
    private ShapeView selectedShapeView;
    private PlacedBlock draggedPlacedBlock; // Khối đã đặt đang được kéo
    private float dragOffsetX;
    private float dragOffsetY;
    private final Vector3 touchPos = new Vector3();

    private boolean isGameOver = false;
    private Player player;

    public InventoryController() {
        this(null);
    }

    public InventoryController(Player player) {
        this.player = player;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT, camera);

        this.viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        this.board = new Board(GameConfig.BOARD_COLS, GameConfig.BOARD_ROWS);
        board.loadTemplate(InventoryTemplates.getTemplate(currentTemplateIndex));
        this.blocksManager = new BlocksManager();
        blocksManager.initShapePool();

        this.boardView = new BoardView(board);
        this.trayView = new TrayView();
        this.nextPieceButton = new ButtonView(
                GameConfig.BUTTON_X, GameConfig.BUTTON_Y,
                GameConfig.BUTTON_WIDTH, GameConfig.BUTTON_HEIGHT,
                "NEXT PIECE");
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void openInventoryOnly() {
        cancelDrag();
        this.showBoard = true;
        this.showTray = false;
    }

    public void openHarvestMenu() {
        cancelDrag();
        this.showBoard = true;
        this.showTray = true;
    }

    public void close() {
        cancelDrag();
        this.showBoard = false;
        this.showTray = false;
    }

    public boolean isShowTray() {
        return showTray;
    }

    public boolean isShowBoard() {
        return showBoard;
    }

    private void spawnNextBlock() {
        blocksManager.generateCurBlock();
        trayView.updateTray(blocksManager.getCurBlock());
    }

    public void update(float delta) {
        handleInput();
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {

        camera.update();
        viewport.apply();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw dark overlay background
        shapeRenderer.setColor(0f, 0f, 0f, 0.6f);
        shapeRenderer.rect(0, 0, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);

        // 1. Vẽ bàn cờ / inventory (nếu được bật) - backgrounds and highlights with ShapeRenderer
        if (showBoard) {
            if (selectedShapeView != null) {
                int hoverRow = getHoverRow(selectedShapeView);
                int hoverCol = getHoverCol(selectedShapeView);
                boolean canDrop = board.canPlace(selectedShapeView.getShapeLogic(), hoverRow, hoverCol);
                boardView.drawWithHighlight(shapeRenderer, hoverRow, hoverCol,
                        selectedShapeView.getShapeLogic(), canDrop);
            } else {
                boardView.draw(shapeRenderer);
            }
        }

        // 2. Vẽ phần Tray và nút tạo block (nếu được bật) - backgrounds with ShapeRenderer
        if (showTray) {
            trayView.drawBackground(shapeRenderer);
            nextPieceButton.drawShape(shapeRenderer);
        }

        shapeRenderer.end();

        // Draw shapes with SpriteBatch (textures)
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // 1. Vẽ các khối trên bàn cờ (placed blocks)
        if (showBoard) {
            boardView.drawPlacedBlocks(batch);
        }

        // 2. Vẽ khối trong khay
        if (showTray) {
            trayView.drawShapes(batch);
        }

        // 3. Vẽ khối đang được kéo nằm trên cùng (lớp trên hết)
        if (selectedShapeView != null) {
            selectedShapeView.draw(batch);
        }

        // Vẽ chữ của nút bấm nếu tray & button hiển thị
        if (showTray) {
            nextPieceButton.drawText(batch, font);
        }

        // Vẽ tiêu đề INVENTORY và Template nếu board hiển thị
        if (showBoard) {
            font.setColor(Color.WHITE);
            font.draw(batch, "INVENTORY",
                    GameConfig.BOARD_START_X + 115f,
                    GameConfig.BOARD_START_Y + GameConfig.BOARD_ROWS * GameConfig.CELL_SIZE + 24f);

            InventoryTemplate tpl = board.getCurrentTemplate();
            if (tpl != null) {
                font.setColor(new Color(0.95f, 0.85f, 0.45f, 1f));
                font.draw(batch, tpl.getName(),
                        GameConfig.BOARD_START_X,
                        GameConfig.BOARD_START_Y - 10f);
            }
        }

        // Dòng hướng dẫn phím bấm
        font.setColor(new Color(0.7f, 0.7f, 0.8f, 1f));
        if (showTray) {
            font.draw(batch, "[T / 1-6] Templates   [R] Rotate   [E] Inventory   [ESC / SPACE] Close", 15f, 30f);
        } else {
            font.draw(batch, "[T / 1-6] Templates   [R] Rotate   [E / ESC] Close", 15f, 30f);
        }

        if (isGameOver) {
            font.setColor(Color.RED);
            font.draw(batch, "GAME OVER! Click to restart",
                    GameConfig.SCREEN_WIDTH / 2f - 110f, GameConfig.SCREEN_HEIGHT - 20);
        }
        batch.end();
    }

    private void handleInput() {
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(touchPos);
        float mouseX = touchPos.x;
        float mouseY = touchPos.y;



        // Phím đổi template kho đồ (T: chuyển vòng tròn, 1-6: chọn trực tiếp template)
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            switchTemplate((currentTemplateIndex + 1) % InventoryTemplates.getTemplateCount());
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            switchTemplate(0);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            switchTemplate(1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            switchTemplate(2);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            switchTemplate(3);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            switchTemplate(4);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {
            switchTemplate(5);
        }

        if (isGameOver) {
            if (Gdx.input.justTouched())
                restartGame();
            return;
        }

        // Xoay khối bằng phím R
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            if (selectedShapeView != null) {
                // 1. Xoay khối đang cầm trên tay
                Blocks rotated = selectedShapeView.getShapeLogic().rotateClockwise();

                // Giữ tâm khối cố định để tránh bị giật vị trí so với con trỏ chuột
                float centerX = selectedShapeView.getX() + selectedShapeView.getTotalWidth() / 2f;
                float centerY = selectedShapeView.getY() + selectedShapeView.getTotalHeight() / 2f;

                selectedShapeView = new ShapeView(rotated, 0, 0);
                float newX = centerX - selectedShapeView.getTotalWidth() / 2f;
                float newY = centerY - selectedShapeView.getTotalHeight() / 2f;
                selectedShapeView.setPosition(newX, newY);

                // Cập nhật lại khoảng cách lệch chuột
                dragOffsetX = mouseX - newX;
                dragOffsetY = mouseY - newY;

                // Nếu là khối nhấc từ khay thì cập nhật luôn vào blocksManager
                if (draggedPlacedBlock == null) {
                    blocksManager.setCurBlock(rotated);
                }
            } else if (showTray && blocksManager.getCurBlock() != null) {
                // 2. Xoay khối trong khay nếu khay đang hiển thị và chưa cầm lên
                Blocks rotated = blocksManager.getCurBlock().rotateClockwise();
                blocksManager.setCurBlock(rotated);
                trayView.updateTray(rotated);
            }
        }

        if (Gdx.input.justTouched()) {
            // 1. Ấn nút sinh khối mới (chỉ khi phần Tray & Button đang hiển thị)
            if (showTray && nextPieceButton.isTouched(mouseX, mouseY)) {
                spawnNextBlock();
                return;
            }

            // 2. Kéo khối từ khay bên trái (chỉ khi phần Tray & Button đang hiển thị)
            if (showTray) {
                ShapeView touched = trayView.getTouchedShape(mouseX, mouseY);
                if (touched != null) {
                    selectedShapeView = touched;
                    dragOffsetX = mouseX - selectedShapeView.getX();
                    dragOffsetY = mouseY - selectedShapeView.getY();
                    trayView.clear(); // Xóa khối khỏi khay ngay khi cầm lên tay
                    return;
                }
            }

            // 3. Kéo khối đã đặt trên bàn cờ / inventory (chỉ khi phần Board đang hiển thị)
            if (showBoard) {
                int clickRow = boardView.pixelToRow(mouseY, 1);
                int clickCol = boardView.pixelToCol(mouseX);
                if (clickRow >= 0 && clickRow < GameConfig.BOARD_ROWS &&
                        clickCol >= 0 && clickCol < GameConfig.BOARD_COLS) {

                    PlacedBlock pb = board.getBlockAt(clickRow, clickCol);
                    // Chỉ cho phép di chuyển nếu khối không phải là khối cố định (fixed)
                    if (pb != null && !pb.isFixed()) {
                        // Nhấc khối đã đặt ra khỏi bàn cờ
                        board.removeBlock(pb);
                        draggedPlacedBlock = pb;

                        // Tính pixel của góc trái-dưới khối
                        float blockPixelX = boardView.colToPixelX(pb.getCol());
                        float blockPixelY = boardView.rowToPixelY(pb.getRow(), pb.getBlock().getRows());
                        selectedShapeView = new ShapeView(pb.getBlock(), blockPixelX, blockPixelY);

                        dragOffsetX = mouseX - blockPixelX;
                        dragOffsetY = mouseY - blockPixelY;
                    }
                }
            }

        } else if (Gdx.input.isTouched()) {
            if (selectedShapeView != null) {
                selectedShapeView.setPosition(mouseX - dragOffsetX, mouseY - dragOffsetY);
            }

        } else {
            // Thả chuột
            if (selectedShapeView != null) {
                Blocks blockLogic = selectedShapeView.getShapeLogic();
                int targetCol = getHoverCol(selectedShapeView);
                int targetRow = getHoverRow(selectedShapeView);

                if (showBoard && board.canPlace(blockLogic, targetRow, targetCol)) {
                    board.placeBlock(blockLogic, targetRow, targetCol);

                    // Nếu đang kéo từ khay → xóa khay; nếu từ bàn cờ → khối đã bị nhấc rồi
                    if (draggedPlacedBlock == null) {
                        blocksManager.setCurBlock(null);
                        trayView.clear();
                        if (player != null) {
                            player.updateMango();
                        }
                    }
                } else {
                    // Không đặt được hoặc board không hiển thị → trả về vị trí cũ
                    if (draggedPlacedBlock != null) {
                        // Hoàn tác: đặt lại vào ô cũ
                        board.placeBlock(draggedPlacedBlock.getBlock(),
                                draggedPlacedBlock.getRow(),
                                draggedPlacedBlock.getCol());
                    } else {
                        trayView.updateTray(blocksManager.getCurBlock());
                    }
                }

                selectedShapeView = null;
                draggedPlacedBlock = null;
            }
        }
    }

    // Hoàn tác thao tác kéo khi chuyển đổi chế độ hiển thị
    private void cancelDrag() {
        if (selectedShapeView != null) {
            if (draggedPlacedBlock != null) {
                board.placeBlock(draggedPlacedBlock.getBlock(),
                        draggedPlacedBlock.getRow(),
                        draggedPlacedBlock.getCol());
            } else {
                trayView.updateTray(blocksManager.getCurBlock());
            }
            selectedShapeView = null;
            draggedPlacedBlock = null;
        }
    }

    // --- Hàm tiện ích tính vị trí hover ---
    private int getHoverCol(ShapeView sv) {
        return boardView.pixelToCol(sv.getX());
    }

    private int getHoverRow(ShapeView sv) {
        return boardView.pixelToRow(sv.getY(), sv.getShapeLogic().getRows());
    }

    private void switchTemplate(int index) {
        if (index >= 0 && index < InventoryTemplates.getTemplateCount()) {
            cancelDrag();
            currentTemplateIndex = index;
            board.loadTemplate(InventoryTemplates.getTemplate(currentTemplateIndex));
        }
    }

    private void restartGame() {
        board.reset();
        isGameOver = false;
        spawnNextBlock();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}
