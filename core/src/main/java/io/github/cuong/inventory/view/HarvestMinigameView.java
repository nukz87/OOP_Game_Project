package io.github.cuong.inventory.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class HarvestMinigameView {
    public static final float CENTER_X = 140f;
    public static final float CENTER_Y = 270f;
    public static final float OUTER_RADIUS = 112f;
    public static final float INNER_RADIUS = 86f;

    public static final float BUTTON_X = 50f;
    public static final float BUTTON_Y = 85f;
    public static final float BUTTON_WIDTH = 180f;
    public static final float BUTTON_HEIGHT = 46f;

    private static final float POINTER_SPEED = 195f; // Do / giay
    private static final float ZONE_SWEEP = 42f;     // Do rong moi vung xanh

    private float pointerAngle = 90f;
    private float zone1Start = 95f;
    private float zone2Start = 275f;

    private String feedbackText = null;
    private Color feedbackColor = Color.GREEN;
    private float feedbackTimer = 0f;

    private final Color ringBorderColor = new Color(0.92f, 0.88f, 0.78f, 1f);
    private final Color ringBaseColor = new Color(0.36f, 0.13f, 0.13f, 1f);
    private final Color greenZoneColor = new Color(0.42f, 0.85f, 0.42f, 1f);
    private final Color centerBgColor = new Color(0.08f, 0.09f, 0.12f, 1f);
    private final Color pointerColor = new Color(0.96f, 0.94f, 0.86f, 1f);

    public void reset() {
        feedbackText = null;
        feedbackTimer = 0f;
        randomizeZones();
    }

    public void randomizeZones() {
        zone1Start = MathUtils.random(0f, 360f);
        // Dat vung thu 2 doi dien (lech khoang 140..220 do) de khong chong len nhau
        zone2Start = (zone1Start + MathUtils.random(140f, 220f)) % 360f;
    }

    public void update(float delta) {
        pointerAngle = (pointerAngle + POINTER_SPEED * delta) % 360f;
        if (feedbackTimer > 0f) {
            feedbackTimer -= delta;
        }
    }

    public boolean tryHarvest() {
        boolean hit = isAngleInArc(pointerAngle, zone1Start, ZONE_SWEEP)
                || isAngleInArc(pointerAngle, zone2Start, ZONE_SWEEP);
        if (hit) {
            feedbackText = "TRUNG ROI!";
            feedbackColor = Color.GREEN;
            feedbackTimer = 1.2f;
            randomizeZones();
        } else {
            feedbackText = "TRUOT ROI!";
            feedbackColor = Color.RED;
            feedbackTimer = 1.0f;
        }
        return hit;
    }

    private boolean isAngleInArc(float angle, float arcStart, float arcSweep) {
        float diff = ((angle - arcStart) % 360f + 360f) % 360f;
        return diff <= arcSweep;
    }

    public void drawShapes(ShapeRenderer shapeRenderer) {
        // 1. Vien ngoai cung cua vong
        shapeRenderer.setColor(ringBorderColor);
        shapeRenderer.circle(CENTER_X, CENTER_Y, OUTER_RADIUS + 2.5f, 48);

        // 2. Nen do sam cua vong
        shapeRenderer.setColor(ringBaseColor);
        shapeRenderer.circle(CENTER_X, CENTER_Y, OUTER_RADIUS, 48);

        // 3. Hai khu vuc muc tieu mau xanh
        shapeRenderer.setColor(greenZoneColor);
        shapeRenderer.arc(CENTER_X, CENTER_Y, OUTER_RADIUS, zone1Start, ZONE_SWEEP, 24);
        shapeRenderer.arc(CENTER_X, CENTER_Y, OUTER_RADIUS, zone2Start, ZONE_SWEEP, 24);

        // 4. Vien trong va vung khay den o giua (thay cho con ca)
        shapeRenderer.setColor(ringBorderColor);
        shapeRenderer.circle(CENTER_X, CENTER_Y, INNER_RADIUS + 2.5f, 48);

        shapeRenderer.setColor(centerBgColor);
        shapeRenderer.circle(CENTER_X, CENTER_Y, INNER_RADIUS, 48);

        // 5. Thanh kim quay quanh vong tron
        float rad = pointerAngle * MathUtils.degreesToRadians;
        float cos = MathUtils.cos(rad);
        float sin = MathUtils.sin(rad);

        float xInner = CENTER_X + (INNER_RADIUS - 8f) * cos;
        float yInner = CENTER_Y + (INNER_RADIUS - 8f) * sin;
        float xOuter = CENTER_X + (OUTER_RADIUS + 10f) * cos;
        float yOuter = CENTER_Y + (OUTER_RADIUS + 10f) * sin;

        shapeRenderer.setColor(pointerColor);
        shapeRenderer.rectLine(xInner, yInner, xOuter, yOuter, 6f);

        shapeRenderer.setColor(centerBgColor);
        shapeRenderer.rectLine(
                CENTER_X + (INNER_RADIUS - 2f) * cos,
                CENTER_Y + (INNER_RADIUS - 2f) * sin,
                CENTER_X + (OUTER_RADIUS + 3f) * cos,
                CENTER_Y + (OUTER_RADIUS + 3f) * sin,
                2f);
    }

    public void drawText(SpriteBatch batch, BitmapFont font) {
        font.setColor(Color.WHITE);
        font.draw(batch, "HAI XOAI", CENTER_X - 32f, CENTER_Y + OUTER_RADIUS + 26f);

        if (feedbackTimer > 0f && feedbackText != null) {
            font.setColor(feedbackColor);
            font.draw(batch, feedbackText, CENTER_X - 40f, BUTTON_Y - 14f);
        }
    }
}
