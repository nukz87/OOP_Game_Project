package io.github.cuong.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Reusable sprite/animation loader for map objects (e.g. MangoTree, Merchant/Shop).
 * Supports either a single static PNG (<basePath>.png) or a 4-frame loop (<basePath>_0.png .. _3.png).
 */
public class MapObjectSprite implements Disposable {
    private static final int DEFAULT_ANIM_FRAMES = 4;
    private static final float DEFAULT_FRAME_DURATION = 0.22f;

    private final Array<Texture> loadedTextures = new Array<>();
    private Animation<TextureRegion> animation;
    private float stateTime = 0f;

    public MapObjectSprite(String basePath) {
        load(basePath, DEFAULT_FRAME_DURATION);
    }

    public MapObjectSprite(String basePath, float frameDuration) {
        load(basePath, frameDuration);
    }

    private void load(String basePath, float frameDuration) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < DEFAULT_ANIM_FRAMES; i++) {
            FileHandle frameFile = Gdx.files.internal(basePath + "_" + i + ".png");
            if (!frameFile.exists()) {
                break;
            }
            Texture texture = new Texture(frameFile);
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            loadedTextures.add(texture);
            frames.add(new TextureRegion(texture));
        }

        if (frames.size > 0) {
            animation = new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
            return;
        }

        FileHandle singleFile = Gdx.files.internal(basePath + ".png");
        if (singleFile.exists()) {
            Texture texture = new Texture(singleFile);
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            loadedTextures.add(texture);
            animation = new Animation<>(1f, new TextureRegion(texture));
        }
    }

    public void update(float delta) {
        if (animation != null && animation.getKeyFrames().length > 1) {
            stateTime += delta;
        }
    }

    public void render(SpriteBatch batch, float x, float y, float width, float height) {
        if (animation == null) {
            return;
        }
        TextureRegion frame = animation.getKeyFrame(stateTime, true);
        if (frame != null) {
            batch.draw(frame, x, y, width, height);
        }
    }

    public boolean hasTexture() {
        return animation != null;
    }

    @Override
    public void dispose() {
        for (Texture texture : loadedTextures) {
            texture.dispose();
        }
        loadedTextures.clear();
        animation = null;
    }
}
