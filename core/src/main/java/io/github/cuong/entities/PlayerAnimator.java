package io.github.cuong.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.util.EnumMap;
import java.util.Map;

/**
 * Loads and manages player sprite animations:
 * - BASE: 1 static frame (player/base/base.png) or 4 frames (player/base/base_0.png .. base_3.png)
 * - IDLE: 4 frames (player/idle/idle_0.png .. idle_3.png)
 * - WALK: 4 directions x 4 frames (player/walk/walk_<dir>_0.png .. _3.png)
 * - RUN:  4 directions x 4 frames (player/run/run_<dir>_0.png .. _3.png)
 */
public class PlayerAnimator implements Disposable {
    public static final int FRAME_COUNT = 4;
    private static final float IDLE_FRAME_DURATION = 0.22f;
    private static final float WALK_FRAME_DURATION = 0.14f;
    private static final float RUN_FRAME_DURATION = 0.09f;

    private final Array<Texture> loadedTextures = new Array<>();
    private Animation<TextureRegion> baseAnimation;
    private Animation<TextureRegion> idleAnimation;
    private final Map<Direction4, Animation<TextureRegion>> walkAnimations = new EnumMap<>(Direction4.class);
    private final Map<Direction4, Animation<TextureRegion>> runAnimations = new EnumMap<>(Direction4.class);

    private PlayerState currentState = PlayerState.BASE;
    private Direction4 currentDirection = Direction4.DOWN;
    private float stateTime = 0f;

    public PlayerAnimator() {
        loadAnimations();
    }

    private void loadAnimations() {
        baseAnimation = loadSequence("player/base/base", IDLE_FRAME_DURATION, Animation.PlayMode.LOOP);
        if (baseAnimation == null) {
            baseAnimation = loadSingleFrame("player/base/base.png");
        }
        idleAnimation = loadSequence("player/idle/idle", IDLE_FRAME_DURATION, Animation.PlayMode.NORMAL);

        for (Direction4 dir : Direction4.values()) {
            Animation<TextureRegion> walkAnim = loadSequence("player/walk/walk_" + dir.getKey(), WALK_FRAME_DURATION, Animation.PlayMode.LOOP);
            if (walkAnim != null) {
                walkAnimations.put(dir, walkAnim);
            }

            Animation<TextureRegion> runAnim = loadSequence("player/run/run_" + dir.getKey(), RUN_FRAME_DURATION, Animation.PlayMode.LOOP);
            if (runAnim != null) {
                runAnimations.put(dir, runAnim);
            }
        }
    }

    private Animation<TextureRegion> loadSingleFrame(String path) {
        FileHandle file = Gdx.files.internal(path);
        if (!file.exists()) {
            return null;
        }
        Texture texture = new Texture(file);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        loadedTextures.add(texture);
        return new Animation<>(1f, new TextureRegion(texture));
    }

    private Animation<TextureRegion> loadSequence(String basePath, float frameDuration, Animation.PlayMode playMode) {
        Array<TextureRegion> frames = new Array<>(FRAME_COUNT);
        for (int i = 0; i < FRAME_COUNT; i++) {
            String path = basePath + "_" + i + ".png";
            FileHandle file = Gdx.files.internal(path);
            if (!file.exists()) {
                return null;
            }
            Texture texture = new Texture(file);
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            loadedTextures.add(texture);
            frames.add(new TextureRegion(texture));
        }
        return new Animation<>(frameDuration, frames, playMode);
    }

    public void update(float delta, PlayerState state, Direction4 direction) {
        if (state != currentState) {
            currentState = state;
            stateTime = 0f;
        } else {
            stateTime += delta;
        }
        if (direction != null) {
            currentDirection = direction;
        }
    }

    public TextureRegion getCurrentFrame() {
        Animation<TextureRegion> anim = resolveAnimation();
        if (anim == null) {
            return null;
        }
        boolean looping = anim.getPlayMode() == Animation.PlayMode.LOOP;
        return anim.getKeyFrame(stateTime, looping);
    }

    public boolean isIdleFinished() {
        if (currentState != PlayerState.IDLE) {
            return false;
        }
        if (idleAnimation == null) {
            return true;
        }
        return idleAnimation.isAnimationFinished(stateTime);
    }

    private Animation<TextureRegion> resolveAnimation() {
        switch (currentState) {
            case RUN:
                Animation<TextureRegion> runAnim = runAnimations.get(currentDirection);
                if (runAnim != null) return runAnim;
                Animation<TextureRegion> walkFallback = walkAnimations.get(currentDirection);
                if (walkFallback != null) return walkFallback;
                return baseAnimation != null ? baseAnimation : idleAnimation;
            case WALK:
                Animation<TextureRegion> walkAnim = walkAnimations.get(currentDirection);
                if (walkAnim != null) return walkAnim;
                return baseAnimation != null ? baseAnimation : idleAnimation;
            case IDLE:
                if (idleAnimation != null) return idleAnimation;
                return baseAnimation != null ? baseAnimation : walkAnimations.get(currentDirection);
            case BASE:
            default:
                if (baseAnimation != null) return baseAnimation;
                return idleAnimation != null ? idleAnimation : walkAnimations.get(currentDirection);
        }
    }

    public boolean hasLoadedFrames() {
        return loadedTextures.size > 0;
    }

    @Override
    public void dispose() {
        for (Texture texture : loadedTextures) {
            texture.dispose();
        }
        loadedTextures.clear();
    }
}
