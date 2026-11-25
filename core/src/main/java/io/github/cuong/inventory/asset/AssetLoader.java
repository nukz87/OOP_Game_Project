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
        String[] shapeNames = { "single", "square2x2", "square3x3", "bar4",
                "bar3", "shapeL", "shapeZ", };

        int[][] colors = {
                { 255, 192, 203 }, // PINK
                { 255, 255, 0 }, // YELLOW
                { 255, 36, 0 }, // SCARLET
                { 0, 255, 255 }, // CYAN
                { 65, 105, 225 }, // ROYAL BLUE
                { 255, 165, 0 }, // ORANGE
                { 255, 0, 0 }, // RED
        };

        for (int i = 0; i < shapeNames.length; i++) {
            TextureRegion region = createColoredTextureRegion(cellSize,
                    colors[i][0], colors[i][1], colors[i][2]);
            textureRegions.put(shapeNames[i], region);
        }

        // Also create a generic block texture for individual cells
        TextureRegion blockTexture = createColoredTextureRegion(cellSize, 100, 100, 100);
        textureRegions.put("block", blockTexture);
    }

    /**
     * Creates a colored texture region using Pixmap
     */
    private TextureRegion createColoredTextureRegion(int size, int r, int g, int b) {
        return createColoredTextureRegion(size, r, g, b, 255);
    }

    private TextureRegion createColoredTextureRegion(int size, int r, int g, int b, int a) {
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
     * Gets a texture region by name (default 0 degree rotation)
     */
    public TextureRegion getTextureRegion(String name) {
        return getTextureRegion(name, 0);
    }

    /**
     * Gets a texture region by name and rotation angle (0, 90, 180, 270)
     */
    public TextureRegion getTextureRegion(String name, int rotation) {
        if (name == null) {
            return textureRegions.get("block");
        }
        int norm = ((rotation % 360) + 360) % 360;
        int step = (Math.round(norm / 90f) * 90) % 360;

        TextureRegion region = textureRegions.get(name + "_" + step);
        if (region != null) {
            return region;
        }

        // Fallback to base name
        region = textureRegions.get(name);
        if (region != null) {
            return region;
        }

        return textureRegions.get("block");
    }

    /**
     * Loads a texture from internal resources and automatically generates
     * 4 rotation variants (0, 90, 180, 270 degrees clockwise).
     */
    public void loadTexture(String name, String filePath) {
        FileHandle file = Gdx.files.internal(filePath);
        if (!file.exists()) {
            System.out.println("[AssetLoader] Texture file not found (using procedural fallback): " + filePath);
            return;
        }
        System.out.println("[AssetLoader] Loading texture: " + name + " from " + filePath);
        try {
            Pixmap basePixmap = new Pixmap(file);
            registerRotatedTextures(name, basePixmap);
            basePixmap.dispose();
            System.out.println("[AssetLoader] Successfully loaded and generated rotations for: " + name);
        } catch (Exception e) {
            System.err.println("[AssetLoader] Failed to load texture: " + filePath);
            e.printStackTrace();
        }
    }

    /**
     * Creates and registers 4 rotation variants for a given base Pixmap.
     */
    private void registerRotatedTextures(String name, Pixmap base) {
        // 0 degrees
        Texture t0 = new Texture(base);
        t0.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion r0 = new TextureRegion(t0);
        textureRegions.put(name, r0);
        textureRegions.put(name + "_0", r0);

        // 90 degrees clockwise
        Pixmap p90 = rotatePixmapClockwise(base, 90);
        Texture t90 = new Texture(p90);
        t90.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion r90 = new TextureRegion(t90);
        textureRegions.put(name + "_90", r90);
        p90.dispose();

        // 180 degrees
        Pixmap p180 = rotatePixmapClockwise(base, 180);
        Texture t180 = new Texture(p180);
        t180.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion r180 = new TextureRegion(t180);
        textureRegions.put(name + "_180", r180);
        p180.dispose();

        // 270 degrees clockwise
        Pixmap p270 = rotatePixmapClockwise(base, 270);
        Texture t270 = new Texture(p270);
        t270.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion r270 = new TextureRegion(t270);
        textureRegions.put(name + "_270", r270);
        p270.dispose();
    }

    /**
     * Rotates a Pixmap clockwise by 90, 180, or 270 degrees.
     */
    public static Pixmap rotatePixmapClockwise(Pixmap src, int degrees) {
        int w = src.getWidth();
        int h = src.getHeight();
        int norm = ((degrees % 360) + 360) % 360;

        Pixmap dst;
        if (norm == 90) {
            dst = new Pixmap(h, w, src.getFormat());
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    dst.drawPixel(h - 1 - y, x, src.getPixel(x, y));
                }
            }
        } else if (norm == 180) {
            dst = new Pixmap(w, h, src.getFormat());
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    dst.drawPixel(w - 1 - x, h - 1 - y, src.getPixel(x, y));
                }
            }
        } else if (norm == 270) {
            dst = new Pixmap(h, w, src.getFormat());
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    dst.drawPixel(y, w - 1 - x, src.getPixel(x, y));
                }
            }
        } else {
            dst = new Pixmap(w, h, src.getFormat());
            dst.drawPixmap(src, 0, 0);
        }
        return dst;
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
        java.util.Set<Texture> disposed = new java.util.HashSet<>();
        for (TextureRegion region : textureRegions.values()) {
            if (region != null && region.getTexture() != null) {
                if (disposed.add(region.getTexture())) {
                    region.getTexture().dispose();
                }
            }
        }
        textureRegions.clear();
        instance = null;
    }
}