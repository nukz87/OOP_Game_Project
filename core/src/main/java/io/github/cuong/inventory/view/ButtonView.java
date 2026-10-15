package io.github.cuong.inventory.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ButtonView {
    private float x;
    private float y;
    private float width;
    private float height;
    private String text;
    private final Color bgColor = new Color(0.18f, 0.52f, 0.92f, 1f);
    private final Color borderColor = new Color(0.45f, 0.75f, 1f, 1f);
    private final GlyphLayout layout = new GlyphLayout();

    public ButtonView(float x, float y, float width, float height, String text) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
    }

    public boolean isTouched(float touchX, float touchY) {
        return touchX >= x && touchX <= x + width &&
                touchY >= y && touchY <= y + height;
    }

    public void drawShape(ShapeRenderer shapeRenderer) {
        // Viền nút
        shapeRenderer.setColor(borderColor);
        shapeRenderer.rect(x - 2, y - 2, width + 4, height + 4);

        // Nền nút
        shapeRenderer.setColor(bgColor);
        shapeRenderer.rect(x, y, width, height);
    }

    public void drawText(SpriteBatch spriteBatch, BitmapFont font) {
        layout.setText(font, text);
        float textX = x + (width - layout.width) / 2f;
        float textY = y + (height + layout.height) / 2f;
        font.setColor(Color.WHITE);
        font.draw(spriteBatch, text, textX, textY);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setText(String text) {
        this.text = text;
    }
}
