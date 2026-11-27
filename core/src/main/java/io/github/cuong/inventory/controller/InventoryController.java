package io.github.cuong.inventory.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.cuong.inventory.config.GameConfig;
import io.github.cuong.inventory.model.Blocks;
import io.github.cuong.inventory.model.BlocksManager;
import io.github.cuong.inventory.model.Board;
import io.github.cuong.inventory.model.InventoryTemplate;
import io.github.cuong.inventory.model.InventoryTemplates;
import io.github.cuong.inventory.model.PlacedBlock;
import io.github.cuong.inventory.model.ShopUpgrade;
import io.github.cuong.inventory.view.BoardView;
import io.github.cuong.inventory.view.ButtonView;
import io.github.cuong.inventory.view.HarvestMinigameView;
import io.github.cuong.inventory.view.ShopView;
import io.github.cuong.entities.Player;
import io.github.cuong.inventory.view.ShapeView;
import io.github.cuong.inventory.view.TrayView;
import java.util.ArrayList;
import java.util.List;

public class InventoryController {
    private OrthographicCamera camera;
    private Viewport viewport;

    private Board board;
    private BoardView boardView;
    private BlocksManager blocksManager;
    private TrayView trayView;
    private ButtonView nextPieceButton;
    private HarvestMinigameView harvestMinigameView;
    private ShopView shopView;

    // Biến quản lý template hiện tại
    private int currentTemplateIndex = 1; // Khởi đầu với Template Bo 4 góc để thấy rõ khối cố định

    // Biến quản lý hiển thị các phân vùng màn chơi
    private boolean showTray = false;
    private boolean showBoard = false;
    private boolean showShop = false;

    // Trạng thái hover và thông báo bán/mua hàng
    private PlacedBlock hoveredBlock = null;
    private ShopUpgrade hoveredUpgrade = null;
    private ShopView.ShopTab currentShopTab = ShopView.ShopTab.SELL;
    private final List<ShopUpgrade> shopUpgrades = new ArrayList<>();
    private String notificationText = null;
    private Color notificationColor = Color.GREEN;
    private float notificationTimer = 0f;

    // Biến quản lý kéo-thả: dùng chung cho cả khối từ khay lẫn khối đã đặt
    private ShapeView selectedShapeView;
    private PlacedBlock draggedPlacedBlock; // Khối đã đặt đang được kéo
    private float dragOffsetX;
    private float dragOffsetY;
    private final Vector3 touchPos = new Vector3();
    private float mouseX;
    private float mouseY;

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

        if (player != null) {
            this.currentTemplateIndex = player.getInventoryTemplateIndex();
        }

        this.board = new Board(GameConfig.BOARD_COLS, GameConfig.BOARD_ROWS);
        board.loadTemplate(InventoryTemplates.getTemplate(currentTemplateIndex));
        this.blocksManager = new BlocksManager();
        blocksManager.initShapePool();

        this.boardView = new BoardView(board);
        this.trayView = new TrayView(HarvestMinigameView.CENTER_X, HarvestMinigameView.CENTER_Y);
        this.harvestMinigameView = new HarvestMinigameView();
        this.shopView = new ShopView();
        initShopUpgrades();
        this.nextPieceButton = new ButtonView(
                HarvestMinigameView.BUTTON_X, HarvestMinigameView.BUTTON_Y,
                HarvestMinigameView.BUTTON_WIDTH, HarvestMinigameView.BUTTON_HEIGHT,
                "HAI XOAI [SPACE]");
    }

    public void setPlayer(Player player) {
        this.player = player;
        syncTemplateWithPlayer();
    }

    private void syncTemplateWithPlayer() {
        if (player != null && player.getInventoryTemplateIndex() != currentTemplateIndex) {
            switchTemplate(player.getInventoryTemplateIndex());
        }
    }

    public void openInventoryOnly() {
        cancelDrag();
        syncTemplateWithPlayer();
        this.showBoard = true;
        this.showTray = false;
        this.showShop = false;
    }

    public void openHarvestMenu() {
        cancelDrag();
        syncTemplateWithPlayer();
        blocksManager.setCurBlock(null);
        trayView.clear();
        harvestMinigameView.reset();
        this.showBoard = true;
        this.showTray = true;
        this.showShop = false;
    }

    public void openShopMenu() {
        cancelDrag();
        syncTemplateWithPlayer();
        this.showBoard = true;
        this.showTray = false;
        this.showShop = true;
        this.currentShopTab = ShopView.ShopTab.SELL;
        this.hoveredBlock = null;
        this.hoveredUpgrade = null;
    }

    private void initShopUpgrades() {
        shopUpgrades.clear();
        shopUpgrades.add(ShopUpgrade.createStaminaUpgrade(
                "stam_1", "STAMINA +30", "Tang the luc toi da", 80, new Color(0.2f, 0.85f, 0.45f, 1f), 30f));
        shopUpgrades.add(ShopUpgrade.createStaminaUpgrade(
                "stam_2", "STAMINA +50", "Tang the luc toi da", 150, new Color(0.15f, 0.7f, 0.95f, 1f), 50f));

        if (currentTemplateIndex < 1) {
            shopUpgrades.add(ShopUpgrade.createInventoryUpgrade(
                    "inv_1", "TUI TRUNG (6x6)", "Mo rong 36 o o giua", 120, new Color(0.75f, 0.55f, 0.95f, 1f), 1));
        }
        if (currentTemplateIndex < 2) {
            shopUpgrades.add(ShopUpgrade.createInventoryUpgrade(
                    "inv_2", "TUI LON (8x8)", "Mo rong full 64 o", 250, new Color(0.95f, 0.75f, 0.2f, 1f), 2));
        }
    }

    public boolean buyUpgrade(ShopUpgrade upg) {
        if (upg == null || upg.isPurchased() || player == null) {
            return false;
        }
        if (player.gold < upg.getPrice()) {
            notificationText = "Khong du vang! (" + upg.getPrice() + " G)";
            notificationColor = Color.RED;
            notificationTimer = 2.0f;
            return false;
        }
        player.gold -= upg.getPrice();
        if (upg.getType() == ShopUpgrade.Type.STAMINA) {
            player.maxStamina += upg.getStaminaBonus();
            player.currentStamina = player.maxStamina;
            notificationText = "Da mua " + upg.getName() + "!";
        } else if (upg.getType() == ShopUpgrade.Type.INVENTORY_LAYOUT) {
            // Chi ap dung layout moi neu co cap cao hon layout hien tai cua player
            if (upg.getTemplateIndex() > player.getInventoryTemplateIndex()) {
                player.setInventoryTemplateIndex(upg.getTemplateIndex());
                syncTemplateWithPlayer();
                notificationText = "Da nang cap " + upg.getName() + "!";
            } else {
                notificationText = "Da mua (giu tui cap cao hon)!";
            }
        }
        upg.setPurchased(true);
        notificationColor = Color.GREEN;
        notificationTimer = 2.5f;
        return true;
    }

    public void close() {
        cancelDrag();
        this.showBoard = false;
        this.showTray = false;
        this.showShop = false;
    }

    public boolean isShowTray() {
        return showTray;
    }

    public boolean isShowBoard() {
        return showBoard;
    }

    public boolean isShowShop() {
        return showShop;
    }

    public boolean sellBlock(PlacedBlock pb) {
        if (pb == null || pb.isFixed()) {
            return false;
        }
        int price = pb.getBlock().getPrice();
        // Nếu đang kéo trên tay thì block đã được nhấc khỏi board từ lúc bắt đầu kéo
        boolean removed = (pb == draggedPlacedBlock) || board.deleteBlock(pb);
        if (removed) {
            if (player != null) {
                player.gold += price;
            }
            String itemName = pb.getBlock().getTextureName() != null ? pb.getBlock().getTextureName().toUpperCase()
                    : "VAT PHAM";
            notificationText = "Da ban " + itemName + ": +" + price + " G";
            notificationColor = Color.GREEN;
            notificationTimer = 2.5f;
            return true;
        }
        return false;
    }

    private void spawnNextBlock() {
        blocksManager.generateCurBlock();
        trayView.updateTray(blocksManager.getCurBlock());
    }

    private void attemptHarvest() {
        if (harvestMinigameView.tryHarvest()) {
            spawnNextBlock();
        }
    }

    public void update(float delta) {
        if (notificationTimer > 0f) {
            notificationTimer -= delta;
        }
        if (showTray) {
            harvestMinigameView.update(delta);
        }
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

        boolean boardVisible = showBoard && (!showShop || currentShopTab == ShopView.ShopTab.SELL);

        // 1. Vẽ bàn cờ / inventory (nếu được bật) - backgrounds and highlights with
        // ShapeRenderer
        if (boardVisible) {
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

        // 2. Vẽ phần ShopView hoặc Tray & Button (phía bên trái)
        if (showShop) {
            shopView.drawTabsShapes(shapeRenderer, currentShopTab);
            if (currentShopTab == ShopView.ShopTab.SELL) {
                boolean isHoveringShop = isDropOnShop(mouseX, mouseY, selectedShapeView);
                shopView.drawBackground(shapeRenderer, isHoveringShop, hoveredBlock);
            } else {
                shopView.drawUpgradeInfoBackground(shapeRenderer, hoveredUpgrade);
                shopView.drawShelvesShapes(shapeRenderer, shopUpgrades, hoveredUpgrade);
            }
        } else if (showTray) {
            harvestMinigameView.drawShapes(shapeRenderer);
            nextPieceButton.drawShape(shapeRenderer);
        }

        shapeRenderer.end();

        // Draw shapes with SpriteBatch (textures)
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // 1. Vẽ các khối trên bàn cờ (placed blocks)
        if (boardVisible) {
            boardView.drawPlacedBlocks(batch);
        }

        // 2. Vẽ nội dung bên trái (ShopView hoặc TrayView)
        if (showShop) {
            shopView.drawTabsText(batch, font, currentShopTab);
            if (currentShopTab == ShopView.ShopTab.SELL) {
                shopView.drawTextAndAvatar(batch, font, hoveredBlock, player != null ? player.gold : 0,
                        notificationText, notificationTimer, notificationColor);
            } else {
                shopView.drawUpgradeInfoText(batch, font, hoveredUpgrade,
                        notificationText, notificationTimer, notificationColor);
                shopView.drawShelvesText(batch, font, shopUpgrades);
            }
        } else if (showTray) {
            harvestMinigameView.drawText(batch, font);
            trayView.drawShapes(batch);
            nextPieceButton.drawText(batch, font);
        }

        // 3. Vẽ khối đang được kéo nằm trên cùng (lớp trên hết)
        if (selectedShapeView != null) {
            selectedShapeView.draw(batch);
        }

        // Vẽ tiêu đề INVENTORY và Template nếu board hiển thị
        if (boardVisible) {
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
        if (showShop) {
            if (currentShopTab == ShopView.ShopTab.SELL) {
                font.draw(batch, "[Chuot Phai / Keo vao Shop] Ban   [TAB] Doi tab   [E / ESC] Dong", 15f, 30f);
            } else {
                font.draw(batch, "[Chuot Phai] Mua nang cap   [TAB] Doi tab   [E / ESC] Dong", 15f, 30f);
            }
        } else if (showTray) {
            font.draw(batch, "[SPACE / Nut Hai] Hai xoai   [R] Xoay   [E / ESC] Dong", 15f, 30f);
        } else {
            font.draw(batch, "[R] Rotate   [E / ESC] Close", 15f, 30f);
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
        mouseX = touchPos.x;
        mouseY = touchPos.y;

        // Cap nhat khoi / nang cap duoc hover khi o che do Shop
        hoveredBlock = null;
        hoveredUpgrade = null;
        if (showShop) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
                cancelDrag();
                currentShopTab = (currentShopTab == ShopView.ShopTab.SELL)
                        ? ShopView.ShopTab.UPGRADE
                        : ShopView.ShopTab.SELL;
                return;
            }
            if (Gdx.input.justTouched()) {
                ShopView.ShopTab clickedTab = shopView.getTouchedTab(mouseX, mouseY);
                if (clickedTab != null) {
                    if (clickedTab != currentShopTab) {
                        cancelDrag();
                        currentShopTab = clickedTab;
                    }
                    return;
                }
            }

            if (currentShopTab == ShopView.ShopTab.UPGRADE) {
                hoveredUpgrade = shopView.getUpgradeAt(mouseX, mouseY, shopUpgrades);
                if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT) && hoveredUpgrade != null) {
                    buyUpgrade(hoveredUpgrade);
                    hoveredUpgrade = null;
                }
                return;
            }

            if (selectedShapeView != null && draggedPlacedBlock != null) {
                hoveredBlock = draggedPlacedBlock;
            } else {
                int hoverRow = boardView.pixelToRow(mouseY, 1);
                int hoverCol = boardView.pixelToCol(mouseX);
                if (hoverRow >= 0 && hoverRow < GameConfig.BOARD_ROWS &&
                        hoverCol >= 0 && hoverCol < GameConfig.BOARD_COLS) {
                    PlacedBlock pb = board.getBlockAt(hoverRow, hoverCol);
                    if (pb != null && !pb.isFixed()) {
                        hoveredBlock = pb;
                    }
                }
            }

            // Click chuot phai vao khoi de ban ngay
            if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
                if (hoveredBlock != null) {
                    sellBlock(hoveredBlock);
                    hoveredBlock = null;
                    return;
                }
            }
        }

        syncTemplateWithPlayer();

        if (isGameOver) {
            if (Gdx.input.justTouched())
                restartGame();
            return;
        }

        // An SPACE de thu hai xoai khi thanh quay qua vung xanh
        if (showTray && selectedShapeView == null && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            attemptHarvest();
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
            // 1. Ấn nút hái xoài (chỉ khi phần Tray & Button đang hiển thị)
            if (showTray && nextPieceButton.isTouched(mouseX, mouseY)) {
                attemptHarvest();
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
                // Neu dang o Shop va keo tha vao o thong bao gia -> BAN!
                if (showShop && draggedPlacedBlock != null && isDropOnShop(mouseX, mouseY, selectedShapeView)) {
                    sellBlock(draggedPlacedBlock);
                    selectedShapeView = null;
                    draggedPlacedBlock = null;
                    hoveredBlock = null;
                    return;
                }

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
    private boolean isDropOnShop(float mx, float my, ShapeView sv) {
        if (shopView.isInsideShopPanel(mx, my)) {
            return true;
        }
        if (sv != null) {
            float cx = sv.getX() + sv.getTotalWidth() / 2f;
            float cy = sv.getY() + sv.getTotalHeight() / 2f;
            return shopView.isInsideShopPanel(cx, cy);
        }
        return false;
    }

    private int getHoverCol(ShapeView sv) {
        return boardView.pixelToCol(sv.getX());
    }

    private int getHoverRow(ShapeView sv) {
        return boardView.pixelToRow(sv.getY(), sv.getShapeLogic().getRows());
    }

    private void switchTemplate(int index) {
        if (index >= 0 && index < InventoryTemplates.getTemplateCount()) {
            cancelDrag();
            // Giu lai cac block nguoi choi da dat neu con cho trong tren template moi
            List<PlacedBlock> savedBlocks = new ArrayList<>();
            for (PlacedBlock pb : board.getPlacedBlocks()) {
                if (!pb.isFixed()) {
                    savedBlocks.add(pb);
                }
            }
            currentTemplateIndex = index;
            board.loadTemplate(InventoryTemplates.getTemplate(currentTemplateIndex));
            for (PlacedBlock pb : savedBlocks) {
                if (!board.placeBlock(pb.getBlock(), pb.getRow(), pb.getCol())) {
                    placeAtFirstFit(pb.getBlock());
                }
            }
        }
    }

    private void placeAtFirstFit(Blocks block) {
        for (int r = 0; r < GameConfig.BOARD_ROWS; r++) {
            for (int c = 0; c < GameConfig.BOARD_COLS; c++) {
                if (board.placeBlock(block, r, c)) {
                    return;
                }
            }
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
