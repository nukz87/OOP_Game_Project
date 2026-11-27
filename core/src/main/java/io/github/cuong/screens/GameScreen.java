package io.github.cuong.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import io.github.cuong.Main;
import io.github.cuong.entities.Player;
import io.github.cuong.entities.Guard;
import io.github.cuong.managers.TimeManager;
import io.github.cuong.objects.MangoTree;
import io.github.cuong.objects.Merchant;
import io.github.cuong.ui.HUD;
import io.github.cuong.inventory.controller.InventoryController;
import io.github.cuong.schedule.controller.ScheduleController;

public class GameScreen implements Screen {
    public static final float V_WIDTH = 800f;
    public static final float V_HEIGHT = 480f;

    private OrthographicCamera camera;
    private Viewport viewport;
    private Viewport uiViewport;

    private Player player;
    private TimeManager timeManager;
    private MangoTree mangoTree;
    private Merchant merchant;
    private Guard guard;
    private HUD HUD;
    private InventoryController inventoryController;
    private ScheduleController scheduleController;
    private BitmapFont vnFont;

    private boolean isInventoryOpen = false;

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    private final Main game;

    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();

        viewport = new FitViewport(V_WIDTH, V_HEIGHT, camera);
        viewport.apply();

        uiViewport = new FitViewport(V_WIDTH, V_HEIGHT);
        uiViewport.apply(true);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("arial.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 14;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS
            + "àáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ"
            + "ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ";
        vnFont = generator.generateFont(parameter);
        generator.dispose();

        player = new Player(V_WIDTH / 2f, V_HEIGHT / 2f);
        timeManager = new TimeManager();
        mangoTree = new MangoTree(V_WIDTH / 2f, V_HEIGHT / 2f + 40, 30f, 30f);
        merchant = new Merchant(V_WIDTH / 2f + 85f, V_HEIGHT / 2f + 40f, 30f, 30f);
        guard = new Guard(V_WIDTH / 2f - 200f, V_HEIGHT / 2f);
        HUD = new HUD(V_WIDTH, V_HEIGHT);

        inventoryController = new InventoryController(player);

        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.textureMinFilter = Texture.TextureFilter.Nearest;
        params.textureMagFilter = Texture.TextureFilter.Nearest;

        map = new TmxMapLoader().load("map/untitled.tmx", params);
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        scheduleController = new ScheduleController(V_WIDTH, V_HEIGHT);
    }

    @Override
    public void render(float delta) {
        if (!isInventoryOpen) {
            scheduleController.handleInput(uiViewport);
        }

        boolean scheduleBlocking = scheduleController.isBlockingGameplay();

        if (!scheduleBlocking) {
            mangoTree.update(delta, player);
            merchant.update(delta, player);

            if (!isInventoryOpen) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                    isInventoryOpen = true;
                    inventoryController.openInventoryOnly();
                } else if (mangoTree.isPlayerInRange && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                    isInventoryOpen = true;
                    inventoryController.openHarvestMenu();
                } else if (merchant.isPlayerInRange && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                    isInventoryOpen = true;
                    inventoryController.openShopMenu();
                }
            } else {
                if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    isInventoryOpen = false;
                    inventoryController.close();
                } else if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                    if (!inventoryController.isShowTray() && !inventoryController.isShowShop()) {
                        isInventoryOpen = false;
                        inventoryController.close();
                    } else {
                        inventoryController.openInventoryOnly();
                    }
                } else if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && (inventoryController.isShowTray() || inventoryController.isShowShop())) {
                    isInventoryOpen = false;
                    inventoryController.close();
                }
            }

            if (!isInventoryOpen) {
                player.update(delta);
            } else {
                inventoryController.update(delta);
            }

            timeManager.update(delta);
            guard.update(delta, player);

            if (isInventoryOpen && inventoryController.isShowTray() && !mangoTree.isPlayerInRange) {
                isInventoryOpen = false;
                inventoryController.close();
            }
            if (isInventoryOpen && inventoryController.isShowShop() && !merchant.isPlayerInRange) {
                isInventoryOpen = false;
                inventoryController.close();
            }
        }

        ScreenUtils.clear(0, 0, 0, 1);

        float lerp = 5.0f; // Tốc độ bám theo của camera
        camera.position.x += (player.position.x - camera.position.x) * lerp * delta;
        camera.position.y += (player.position.y - camera.position.y) * lerp * delta;
        camera.update();

        float pad = 64f;
        float viewWidth = camera.viewportWidth * camera.zoom + pad * 2;
        float viewHeight = camera.viewportHeight * camera.zoom + pad * 2;
        float viewX = camera.position.x - viewWidth / 2f;
        float viewY = camera.position.y - viewHeight / 2f;

        mapRenderer.setView(camera.combined, viewX, viewY, viewWidth, viewHeight);
        mapRenderer.render();

        //Gdx.gl.glClearColor(0.12f, 0.12f, 0.15f, 1f);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.shapeRenderer.setProjectionMatrix(camera.combined);

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        //drawDebugGrid();
        guard.renderDebug(game.shapeRenderer);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        player.render(game.shapeRenderer);
        mangoTree.render(game.shapeRenderer);
        merchant.render(game.shapeRenderer);
        guard.renderBody(game.shapeRenderer);
        game.shapeRenderer.end();

        if (isInventoryOpen) {
            inventoryController.render(game.shapeRenderer, game.batch, game.font);
        }

        HUD.renderShapes(game.shapeRenderer, player);
        HUD.renderText(game.batch, game.font, player, timeManager);

        if (!isInventoryOpen && !scheduleBlocking) {
            if (mangoTree.isPlayerInRange) {
                game.batch.getProjectionMatrix().setToOrtho2D(0, 0, V_WIDTH, V_HEIGHT);
                game.batch.begin();
                game.font.setColor(com.badlogic.gdx.graphics.Color.YELLOW);
                game.font.draw(game.batch, "[E] Hai xoai", V_WIDTH / 2f - 45f, 60f);
                game.batch.end();
            } else if (merchant.isPlayerInRange) {
                game.batch.getProjectionMatrix().setToOrtho2D(0, 0, V_WIDTH, V_HEIGHT);
                game.batch.begin();
                game.font.setColor(com.badlogic.gdx.graphics.Color.GOLD);
                game.font.draw(game.batch, "[E] Cua hang (Ban do)", V_WIDTH / 2f - 80f, 60f);
                game.batch.end();
            }
        }

        uiViewport.apply();
        game.shapeRenderer.setProjectionMatrix(uiViewport.getCamera().combined);
        game.batch.setProjectionMatrix(uiViewport.getCamera().combined);
        scheduleController.render(game.shapeRenderer, game.batch, vnFont);
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
        if (uiViewport != null) {
            uiViewport.update(width, height, true);
        }
        if (inventoryController != null) {
            inventoryController.resize(width, height);
        }
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
        map.dispose();
        mapRenderer.dispose();
        if (vnFont != null) {
            vnFont.dispose();
        }
    }
}