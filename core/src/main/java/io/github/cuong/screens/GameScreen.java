package io.github.cuong.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.cuong.Main;
import io.github.cuong.entities.Player;
import io.github.cuong.entities.Guard;
import io.github.cuong.managers.TimeManager;
import io.github.cuong.objects.MangoTree;
import io.github.cuong.ui.HUD;

public class GameScreen implements Screen {
    public static final float V_WIDTH = 800f;
    public static final float V_HEIGHT = 480f;

    private OrthographicCamera camera;
    private Viewport viewport;

    private Player player;
    private TimeManager timeManager;
    private MangoTree mangoTree;
    private Guard guard;
    private HUD HUD;

    private final Main game;

    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {

        camera = new OrthographicCamera();

        viewport = new FitViewport(V_WIDTH, V_HEIGHT, camera);
        viewport.apply();

        player = new Player(V_WIDTH / 2f, V_HEIGHT / 2f);
        timeManager = new TimeManager();
        mangoTree = new MangoTree(V_WIDTH / 2f, V_HEIGHT / 2f + 40, 30f, 30f);
        guard = new Guard(V_WIDTH / 2f - 200f, V_HEIGHT / 2f);
        HUD = new HUD(V_WIDTH, V_HEIGHT);
    }

    @Override
    public void render(float delta) {
        player.update(delta);
        mangoTree.update(delta, player);
        timeManager.update(delta);
        guard.update(delta, player);

        float lerp = 5.0f; // Tốc độ bám theo của camera
        camera.position.x += (player.position.x - camera.position.x) * lerp * delta;
        camera.position.y += (player.position.y - camera.position.y) * lerp * delta;
        camera.update();

        Gdx.gl.glClearColor(0.12f, 0.12f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.shapeRenderer.setProjectionMatrix(camera.combined);

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        drawDebugGrid();
        guard.renderDebug(game.shapeRenderer);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        player.render(game.shapeRenderer);
        mangoTree.render(game.shapeRenderer);
        guard.renderBody(game.shapeRenderer);
        game.shapeRenderer.end();

        HUD.renderShapes(game.shapeRenderer, player);
        HUD.renderText(game.batch, game.font, player, timeManager);
    }

    private void drawDebugGrid() {
        game.shapeRenderer.setColor(0.2f, 0.2f, 0.25f, 1f);
        int gridSize = 64;
        int worldMin = -1000;
        int worldMax = 2000;

        for (int x = worldMin; x <= worldMax; x += gridSize) {
            game.shapeRenderer.line(x, worldMin, x, worldMax);
        }
        for (int y = worldMin; y <= worldMax; y += gridSize) {
            game.shapeRenderer.line(worldMin, y, worldMax, y);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}
