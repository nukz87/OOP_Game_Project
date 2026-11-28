package io.github.cuong.entities;

/**
 * 4 movement directions mapped to asset file suffixes.
 */
public enum Direction4 {
    UP("up"),
    RIGHT("right"),
    DOWN("down"),
    LEFT("left");

    private final String key;

    Direction4(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    /**
     * Converts a 2D input/movement vector into one of the 4 cardinal directions.
     * Defaults to DOWN if both axes are zero.
     */
    public static Direction4 fromVector(float dx, float dy) {
        if (dx == 0f && dy == 0f) {
            return DOWN;
        }
        if (Math.abs(dx) >= Math.abs(dy)) {
            return dx > 0f ? RIGHT : LEFT;
        }
        return dy > 0f ? UP : DOWN;
    }
}
