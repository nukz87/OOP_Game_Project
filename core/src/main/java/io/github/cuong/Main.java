package io.github.cuong;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.cuong.screens.GameScreen;

public class Main extends Game {
    public static final float V_WIDTH = 800f;
    public static final float V_HEIGHT = 480f;

    public ShapeRenderer shapeRenderer;
    public SpriteBatch batch;
    public BitmapFont font;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1.2f);

        this.setScreen(new GameScreen(this));
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }
}
