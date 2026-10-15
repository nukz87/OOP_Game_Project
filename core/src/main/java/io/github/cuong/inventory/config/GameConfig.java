package io.github.cuong.inventory.config;

public final class GameConfig {
    // Kích thước màn hình logic (Virtual Resolution)
    public static final float SCREEN_WIDTH = 800f;
    public static final float SCREEN_HEIGHT = 480f;

    // Cấu hình Bàn cờ 8x8 chuyển sang bên phải màn hình
    public static final int BOARD_ROWS = 8;
    public static final int BOARD_COLS = 8;
    public static final float CELL_SIZE = 40f;
    public static final float CELL_GAP = 2f;

    public static final float BOARD_START_X = 280f; // Bàn cờ nằm bên phải (280 -> 600)
    public static final float BOARD_START_Y = 80f; // Căn giữa theo trục Y (80 -> 400)

    // Nút bấm sinh khối gạch nằm ở bên trái
    public static final float BUTTON_X = 40f;
    public static final float BUTTON_Y = 340f;
    public static final float BUTTON_WIDTH = 180f;
    public static final float BUTTON_HEIGHT = 50f;

    // Tọa độ trung tâm vùng khay chứa khối gạch (bên dưới nút bấm)
    public static final float TRAY_CENTER_X = 130f;
    public static final float TRAY_CENTER_Y = 180f;
    public static final float TRAY_BOX_SIZE = 170f;

    private GameConfig() {
    }
}
