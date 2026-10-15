package io.github.cuong.inventory.model;

import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class InventoryTemplates {
    // Màu đá kim loại cứng của các khối cố định
    public static final Color FIXED_COLOR = new Color(0.35f, 0.38f, 0.44f, 1f);

    private static final List<InventoryTemplate> ALL_TEMPLATES = new ArrayList<>();

    static {
        ALL_TEMPLATES.add(createDefaultTemplate());
        ALL_TEMPLATES.add(createFourCornersTemplate());
        ALL_TEMPLATES.add(createCenterPillarTemplate());
        ALL_TEMPLATES.add(createCompartmentsTemplate());
        ALL_TEMPLATES.add(createUShapedTemplate());
        ALL_TEMPLATES.add(createScatteredRuinsTemplate());
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

    // --- Helper tạo các khối cố định ---
    private static Blocks createDotBlock() {
        return new Blocks(new boolean[][] { { true } }, FIXED_COLOR);
    }

    private static Blocks createSquare2x2Block() {
        return new Blocks(new boolean[][] {
                { true, true },
                { true, true }
        }, FIXED_COLOR);
    }

    private static Blocks createBar1x2Block() {
        return new Blocks(new boolean[][] {
                { true, true }
        }, FIXED_COLOR);
    }

    private static Blocks createBar2x1Block() {
        return new Blocks(new boolean[][] {
                { true },
                { true }
        }, FIXED_COLOR);
    }

    private static Blocks createBar1x3Block() {
        return new Blocks(new boolean[][] {
                { true, true, true }
        }, FIXED_COLOR);
    }

    // =========================================================================
    // TEMPLATE 1: Mặc định (Trống hoàn toàn - 8x8 mở rộng)
    // =========================================================================
    private static InventoryTemplate createDefaultTemplate() {
        return new InventoryTemplate(
                "default",
                "1. Cơ Bản (Trống)",
                "Toàn bộ 64 ô mở rộng tự do, không có vật cản.",
                Collections.emptyList());
    }

    // =========================================================================
    // TEMPLATE 2: Bốn góc bị khóa (Four Corners)
    // =========================================================================
    private static InventoryTemplate createFourCornersTemplate() {
        List<PlacedBlock> blocks = new ArrayList<>();
        Blocks sq2 = createSquare2x2Block();

        // 4 khối 2x2 ở 4 góc hòm đồ
        blocks.add(new PlacedBlock(sq2, 0, 0, true)); // Góc trên - trái
        blocks.add(new PlacedBlock(sq2, 0, 6, true)); // Góc trên - phải
        blocks.add(new PlacedBlock(sq2, 6, 0, true)); // Góc dưới - trái
        blocks.add(new PlacedBlock(sq2, 6, 6, true)); // Góc dưới - phải

        return new InventoryTemplate(
                "four_corners",
                "2. Bo 4 Góc",
                "Khóa 4 góc bằng các khối đá 2x2, để lại không gian chữ thập rộng rãi.",
                blocks);
    }

    // =========================================================================
    // TEMPLATE 3: Trụ đá trung tâm (Center Pillar)
    // =========================================================================
    private static InventoryTemplate createCenterPillarTemplate() {
        List<PlacedBlock> blocks = new ArrayList<>();
        Blocks sq2 = createSquare2x2Block();

        // Khối 2x2 cố định ngay chính tâm (hàng 3-4, cột 3-4)
        blocks.add(new PlacedBlock(sq2, 3, 3, true));

        return new InventoryTemplate(
                "center_pillar",
                "3. Trụ Tâm Cố Định",
                "Khối đá lớn chiếm giữ trung tâm, buộc người chơi phải sắp xếp bao quanh.",
                blocks);
    }

    // =========================================================================
    // TEMPLATE 4: Chia 4 khoang (Compartments / Cross Divider)
    // =========================================================================
    private static InventoryTemplate createCompartmentsTemplate() {
        List<PlacedBlock> blocks = new ArrayList<>();
        Blocks barH = createBar1x2Block();
        Blocks barV = createBar2x1Block();
        Blocks dot = createDotBlock();

        // Khối trung tâm
        blocks.add(new PlacedBlock(dot, 3, 3, true));
        blocks.add(new PlacedBlock(dot, 3, 4, true));
        blocks.add(new PlacedBlock(dot, 4, 3, true));
        blocks.add(new PlacedBlock(dot, 4, 4, true));

        // Vách ngăn ngang
        blocks.add(new PlacedBlock(barH, 3, 0, true));
        blocks.add(new PlacedBlock(barH, 3, 6, true));

        // Vách ngăn dọc
        blocks.add(new PlacedBlock(barV, 0, 3, true));
        blocks.add(new PlacedBlock(barV, 6, 3, true));

        return new InventoryTemplate(
                "compartments",
                "4. Chia 4 Khoang",
                "Vách ngăn chữ thập tạo 4 ngăn chứa riêng biệt như balo phân vùng.",
                blocks);
    }

    // =========================================================================
    // TEMPLATE 5: Túi chữ U (U-Shaped Chamber)
    // =========================================================================
    private static InventoryTemplate createUShapedTemplate() {
        List<PlacedBlock> blocks = new ArrayList<>();
        Blocks bar3 = createBar1x3Block();
        Blocks barV = createBar2x1Block();

        // Khóa hai bên mép trên
        blocks.add(new PlacedBlock(bar3, 0, 0, true));
        blocks.add(new PlacedBlock(bar3, 0, 5, true));

        // Khóa viền hai bên sườn
        blocks.add(new PlacedBlock(barV, 2, 0, true));
        blocks.add(new PlacedBlock(barV, 2, 7, true));
        blocks.add(new PlacedBlock(barV, 5, 0, true));
        blocks.add(new PlacedBlock(barV, 5, 7, true));

        return new InventoryTemplate(
                "u_chamber",
                "5. Túi Chữ U",
                "Khoang chứa dạng hộc sâu với các vách chắn hai bên sườn.",
                blocks);
    }

    // =========================================================================
    // TEMPLATE 6: Bãi đá rải rác (Scattered Ruins - Puzzle)
    // =========================================================================
    private static InventoryTemplate createScatteredRuinsTemplate() {
        List<PlacedBlock> blocks = new ArrayList<>();
        Blocks dot = createDotBlock();

        // Các khối 1x1 rải rác đối xứng nhẹ nhàng tạo bài toán giải đố xếp hình
        blocks.add(new PlacedBlock(dot, 1, 2, true));
        blocks.add(new PlacedBlock(dot, 1, 5, true));
        blocks.add(new PlacedBlock(dot, 3, 1, true));
        blocks.add(new PlacedBlock(dot, 3, 6, true));
        blocks.add(new PlacedBlock(dot, 5, 3, true));
        blocks.add(new PlacedBlock(dot, 5, 4, true));
        blocks.add(new PlacedBlock(dot, 6, 1, true));
        blocks.add(new PlacedBlock(dot, 6, 6, true));

        return new InventoryTemplate(
                "scattered_ruins",
                "6. Bãi Đá Rải Rác",
                "Các chướng ngại vật 1x1 ngẫu nhiên đòi hỏi xoay khối [R] khéo léo.",
                blocks);
    }
}
