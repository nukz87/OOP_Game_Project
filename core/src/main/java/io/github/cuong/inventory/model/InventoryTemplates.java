package io.github.cuong.inventory.model;

import com.badlogic.gdx.graphics.Color;
import io.github.cuong.inventory.config.GameConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class InventoryTemplates {
    // Mau da kim loai cung cua cac khoi co dinh
    public static final Color FIXED_COLOR = new Color(0.35f, 0.38f, 0.44f, 1f);

    private static final List<InventoryTemplate> ALL_TEMPLATES = new ArrayList<>();

    static {
        ALL_TEMPLATES.add(createSmallTemplate());
        ALL_TEMPLATES.add(createMediumTemplate());
        ALL_TEMPLATES.add(createLargeTemplate());
    }

    private InventoryTemplates() {
    }

    public static List<InventoryTemplate> getAllTemplates() {
        return Collections.unmodifiableList(ALL_TEMPLATES);
    }

    public static InventoryTemplate getTemplate(int index) {
        if (index < 0 || index >= ALL_TEMPLATES.size()) {
            return ALL_TEMPLATES.get(0);
        }
        return ALL_TEMPLATES.get(index);
    }

    public static int getTemplateCount() {
        return ALL_TEMPLATES.size();
    }

    private static Blocks createDotBlock() {
        return new Blocks(new boolean[][] { { true } }, FIXED_COLOR);
    }

    // Khoa tat ca cac o nam ngoai vung mo [openRowStart..openRowEnd] x [openColStart..openColEnd]
    private static List<PlacedBlock> createLockedBorderBlocks(
            int openRowStart, int openRowEnd, int openColStart, int openColEnd) {
        List<PlacedBlock> blocks = new ArrayList<>();
        Blocks dot = createDotBlock();
        for (int r = 0; r < GameConfig.BOARD_ROWS; r++) {
            for (int c = 0; c < GameConfig.BOARD_COLS; c++) {
                boolean isOpen = (r >= openRowStart && r <= openRowEnd &&
                                  c >= openColStart && c <= openColEnd);
                if (!isOpen) {
                    blocks.add(new PlacedBlock(dot, r, c, true));
                }
            }
        }
        return blocks;
    }

    // =========================================================================
    // LAYOUT 0 (Mo dau): Khoi 6x6 o giua tru di 2 cot ben -> vung mo 6x4 o giua
    // =========================================================================
    private static InventoryTemplate createSmallTemplate() {
        List<PlacedBlock> blocks = createLockedBorderBlocks(1, 6, 2, 5);
        return new InventoryTemplate(
                "small_6x4",
                "1. Tui Nho (6x4)",
                "Tui khoi dau 24 o o trung tam.",
                blocks);
    }

    // =========================================================================
    // LAYOUT 1 (Trung binh): Khoi 6x6 day du o giua
    // =========================================================================
    private static InventoryTemplate createMediumTemplate() {
        List<PlacedBlock> blocks = createLockedBorderBlocks(1, 6, 1, 6);
        return new InventoryTemplate(
                "medium_6x6",
                "2. Tui Trung (6x6)",
                "Mo rong thanh khoang 6x6 o giua (36 o).",
                blocks);
    }

    // =========================================================================
    // LAYOUT 2 (Lon nhat): Toan bo 8x8 day du
    // =========================================================================
    private static InventoryTemplate createLargeTemplate() {
        return new InventoryTemplate(
                "large_8x8",
                "3. Tui Lon (8x8)",
                "Toan bo 64 o mo rong toi da.",
                Collections.emptyList());
    }
}
