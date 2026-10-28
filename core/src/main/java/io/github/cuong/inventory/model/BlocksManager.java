package io.github.cuong.inventory.model;

import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlocksManager {
    private final List<Blocks> shapePool = new ArrayList<>();
    private final Random random = new Random();
    private Blocks curBlock;

    public void initShapePool() {
        shapePool.clear();

        // 1x1 đơn
        boolean[][] singleDot = {
                { true }
        };

        // 2x2 hình vuông
        boolean[][] square2x2 = {
                { true, true },
                { true, true }
        };

        // 3x3 hình vuông lớn
        boolean[][] square3x3 = {
                { true, true, true },
                { true, true, true },
                { true, true, true }
        };

        // 1x4 thanh ngang
        boolean[][] bar4 = {
                { true, true, true, true }
        };

        // 4x1 thanh dọc
        boolean[][] col4 = {
                { true },
                { true },
                { true },
                { true }
        };

        // 1x3 thanh ngang
        boolean[][] bar3 = {
                { true, true, true }
        };

        // Khối chữ L
        boolean[][] shapeL = {
                { true, false, false },
                { true, true, true }
        };

        // Khối chữ T
        boolean[][] shapeT = {
                { true, true, true },
                { false, true, false }
        };

        // Khối chữ Z
        boolean[][] shapeZ = {
                { true, true, false },
                { false, true, true }
        };

        // Khối chữ S
        boolean[][] shapeS = {
                { false, true, true },
                { true, true, false }
        };

        shapePool.add(new Blocks(singleDot, Color.PINK, "single", false));
        shapePool.add(new Blocks(square2x2, Color.YELLOW, "square2x2", true));
        shapePool.add(new Blocks(square3x3, Color.SCARLET, "square3x3", false));
        shapePool.add(new Blocks(bar4, Color.CYAN, "bar4", false));
        shapePool.add(new Blocks(col4, Color.CYAN, "col4", false));
        shapePool.add(new Blocks(bar3, Color.ROYAL, "bar3", false));
        shapePool.add(new Blocks(shapeL, Color.ORANGE, "shapeL", false));
        shapePool.add(new Blocks(shapeT, Color.PURPLE, "shapeT", false));
        shapePool.add(new Blocks(shapeZ, Color.RED, "shapeZ", false));
        shapePool.add(new Blocks(shapeS, Color.GREEN, "shapeS", false));
    }

    public void generateCurBlock() {
        if (shapePool.isEmpty()) {
            initShapePool();
        }
        int index = random.nextInt(shapePool.size());
        this.curBlock = shapePool.get(index);
    }

    public Blocks getCurBlock() {
        return this.curBlock;
    }

    public void setCurBlock(Blocks curBlock) {
        this.curBlock = curBlock;
    }
}
