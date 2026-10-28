package io.github.cuong.inventory.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages loading and accessing game assets (textures, atlases, etc.)
 */
public class AssetLoader {
    private static AssetLoader instance;
    private final AssetManager assetManager;
    private final Map<String, TextureRegion> textureRegions;

    private AssetLoader() {
        this.assetManager = new AssetManager();
        this.textureRegions = new HashMap<>();
        generateDefaultTextures();
    }

    public static AssetLoader getInstance() {
        if (instance == null) {
            instance = new AssetLoader();
        }
        return instance;
    }

    /**
     * Generates default colored textures programmatically for each shape type.
     * These can be replaced with actual image files later.
     */
    private void generateDefaultTextures() {
        int cellSize = 64; // Size of each cell in pixels

        // Define colors for each shape type (matching BlocksManager)
        String[] shapeNames = {"single", "square2x2", "square3x3", "bar4", "col4",
                               "bar3", "shapeL", "shapeT", "shapeZ", "shapeS"};

        int[][] colors = {
            {255, 192, 203}, // PINK
            {255, 255, 0},   // YELLOW
            {255, 36, 0},    // SCARLET
            {0, 255, 255},   // CYAN
            {0, 255, 255},   // CYAN
            {65, 105, 225},  // ROYAL BLUE
            {255, 165, 0},   // ORANGE
            {128, 0, 128},   // PURPLE
            {255, 0, 0},     // RED
            {0, 255, 0}      // GREEN
        };

        for (int i = 0; i < shapeNames.length; i++) {
            TextureRegion region = createColoredTextureRegion(shapeNames[i], cellSize,
                colors[i][0], colors[i][1], colors[i][2]);
            textureRegions.put(shapeNames[i], region);
        }

        // Also create a generic block texture for individual cells
        TextureRegion blockTexture = createColoredTextureRegion("block", cellSize, 100, 100, 100);
        textureRegions.put("block", blockTexture);

        // Create highlight textures
        TextureRegion highlightOk = createColoredTextureRegion("highlight_ok", cellSize, 0, 255, 0, 128);
        TextureRegion highlightFail = createColoredTextureRegion("highlight_fail", cellSize, 255, 0, 0, 128);
        textureRegions.put("highlight_ok", highlightOk);
        textureRegions.put("highlight_fail", highlightFail);
    }

    /**
     * Creates a colored texture region using Pixmap
     */
    private TextureRegion createColoredTextureRegion(String name, int size, int r, int g, int b) {
        return createColoredTextureRegion(name, size, r, g, b, 255);
    }

    private TextureRegion createColoredTextureRegion(String name, int size, int r, int g, int b, int a) {
        Pixmap pixmap = new Pixmap(size, size, Format.RGBA8888);
        pixmap.setColor(r / 255f, g / 255f, b / 255f, a / 255f);
        pixmap.fillRectangle(0, 0, size, size);

        // Add a subtle border for better visibility
        pixmap.setColor(0, 0, 0, 0.3f);
        pixmap.drawRectangle(0, 0, size, size);

        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();

        return new TextureRegion(texture);
    }

    /**
     * Gets a texture region by name
     */
    public TextureRegion getTextureRegion(String name) {
        return textureRegions.get(name);
    }

    /**
     * Loads a texture from internal resources (for custom assets in classpath)
     */
    public void loadTexture(String name, String filePath) {
        FileHandle file = Gdx.files.internal(filePath);
        if (!file.exists()) {
            System.err.println("[AssetLoader] Texture file not found: " + filePath);
            return;
        }
        System.out.println("[AssetLoader] Loading texture: " + name + " from " + filePath);
        try {
            assetManager.load(filePath, Texture.class);
            assetManager.finishLoading();
            Texture texture = assetManager.get(filePath, Texture.class);
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            textureRegions.put(name, new TextureRegion(texture));
            System.out.println("[AssetLoader] Successfully loaded: " + name + " (" + texture.getWidth() + "x" + texture.getHeight() + ")");
        } catch (Exception e) {
            System.err.println("[AssetLoader] Failed to load texture: " + filePath);
            e.printStackTrace();
        }
    }

    /**
     * Loads a texture atlas
     */
    public void loadTextureAtlas(String atlasPath) {
        FileHandle file = Gdx.files.internal(atlasPath);
        if (!file.exists()) {
            System.err.println("Texture atlas file not found: " + atlasPath);
            return;
        }
        assetManager.load(atlasPath, TextureAtlas.class);
        assetManager.finishLoading();
        TextureAtlas atlas = assetManager.get(atlasPath, TextureAtlas.class);
        for (TextureAtlas.AtlasRegion region : atlas.getRegions()) {
            textureRegions.put(region.name, region);
        }
    }

    /**
     * Gets the asset manager for advanced usage
     */
    public AssetManager getAssetManager() {
        return assetManager;
    }

    /**
     * Disposes all loaded assets
     */
    public void dispose() {
        assetManager.dispose();
        for (TextureRegion region : textureRegions.values()) {
            if (region.getTexture() != null) {
                region.getTexture().dispose();
            }
        }
        textureRegions.clear();
        instance = null;
    }
}