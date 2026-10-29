# Adding Custom Assets to the Game

This guide explains how to add custom textures/images to replace the default colored shapes in the game.

## Asset Structure

Place your texture files in:
```
core/src/main/resources/assets/textures/shapes/
```

## Supported Texture Types

The game currently supports 10 different shape types. Each shape type can have its own texture:

| Shape Name | Description | Default Color |
|------------|-------------|---------------|
| `single` | 1x1 single block | Pink |
| `square2x2` | 2x2 square | Yellow |
| `square3x3` | 3x3 square | Scarlet |
| `bar4` | 1x4 horizontal bar | Cyan |
| `col4` | 4x1 vertical bar | Cyan |
| `bar3` | 1x3 horizontal bar | Royal Blue |
| `shapeL` | L-shaped block | Orange |
| `shapeT` | T-shaped block | Purple |
| `shapeZ` | Z-shaped block | Red |
| `shapeS` | S-shaped block | Green |

## Adding Custom Textures

### Option 1: Individual Image Files

1. Create PNG images for each shape (recommended size: 64x64 pixels)
2. Place them in `core/src/main/resources/assets/textures/shapes/`
3. Name them according to the shape names above (e.g., `shapeL.png`, `shapeT.png`)

### Option 2: Texture Atlas (Recommended for Performance)

1. Create a texture atlas using LibGDX's TexturePacker
2. Place the `.atlas` file and associated `.png` in the assets folder
3. Load it in the code (see below)

## Loading Custom Textures

### Method 1: Using AssetLoader.loadTexture()

In your initialization code (e.g., in `Main.create()`):

```java
AssetLoader.getInstance().loadTexture("shapeL", "assets/textures/shapes/shapeL.png");
AssetLoader.getInstance().loadTexture("shapeT", "assets/textures/shapes/shapeT.png");
// ... etc for other shapes
```

### Method 2: Using Texture Atlas

```java
AssetLoader.getInstance().loadTextureAtlas("assets/textures/shapes/shapes.atlas");
```

The atlas should contain regions named exactly as the shape names listed above.

## How It Works

1. **AssetLoader** - Singleton class that manages all textures
2. **Blocks** - Each shape now has a `textureName` field
3. **ShapeView** - Draws shapes using SpriteBatch with textures instead of ShapeRenderer
4. **BlocksManager** - Assigns texture names when creating shapes

## Recommended Texture Specifications

- **Format**: PNG (with transparency support)
- **Size**: 64x64 pixels (will be scaled to fit cell size)
- **Style**: Simple, clean designs work best at small sizes
- **Background**: Transparent (alpha channel)

## Example: Adding a Custom Texture Pack

1. Create your textures (64x64 PNG files)
2. Place in `core/src/main/resources/assets/textures/shapes/`
3. Modify `Main.java` to load them:

```java
@Override
public void create() {
    AssetLoader assetLoader = AssetLoader.getInstance();
    
    // Load custom textures
    assetLoader.loadTexture("single", "assets/textures/shapes/single.png");
    assetLoader.loadTexture("square2x2", "assets/textures/shapes/square2x2.png");
    // ... load all 10 shapes
    
    // ... rest of initialization
}
```

4. Rebuild and run!

## Fallback Behavior

If a texture is not found for a shape, the game will:
1. Try to load the texture by the shape's assigned name
2. Fall back to a generic "block" texture
3. If that also fails, it will render nothing (invisible)

## Tips

- Use power-of-two dimensions (64x64, 128x128) for better GPU compatibility
- Keep file sizes small for faster loading
- Test your textures at the actual game scale (40x40 pixels per cell)
- Consider creating a texture atlas for better performance with many textures