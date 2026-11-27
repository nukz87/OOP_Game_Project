package io.github.cuong;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


import io.github.cuong.inventory.asset.AssetLoader;
import io.github.cuong.screens.GameScreen;

public class Main extends Game {
    public static final float V_WIDTH = 800f;
    public static final float V_HEIGHT = 480f;

    public ShapeRenderer shapeRenderer;
    public SpriteBatch batch;
    public BitmapFont font;

    @Override
    public void create() {
        // Initialize asset loader first (generates default textures)
        AssetLoader assetLoader = AssetLoader.getInstance();

        // Load custom textures from resources
        assetLoader.loadTexture("square2x2", "assets/textures/shapes/square2x2.png");

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/JetBrainsMono-Medium.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 14;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS
            + "àáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ"
            + "ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ";

        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;

        parameter.borderWidth = 0.5f;
        parameter.borderColor = new Color(0, 0, 0, 0.75f);
        
        font = generator.generateFont(parameter);
        generator.dispose();
        

        this.setScreen(new GameScreen(this));
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
        AssetLoader.getInstance().dispose();
    }
}
