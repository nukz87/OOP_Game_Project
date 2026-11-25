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

        // 1x3 thanh ngang
        boolean[][] bar3 = {
                { true, true, true }
        };

        shapePool.add(new Blocks(singleDot, Color.PINK, "single", false, 0, 15));
        shapePool.add(new Blocks(square2x2, Color.YELLOW, "square2x2", true, 0, 50));
        shapePool.add(new Blocks(square3x3, Color.SCARLET, "square3x3", false, 0, 120));
        shapePool.add(new Blocks(bar4, Color.CYAN, "bar4", false, 0, 60));
        shapePool.add(new Blocks(bar3, Color.ROYAL, "bar3", false, 0, 40));
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
