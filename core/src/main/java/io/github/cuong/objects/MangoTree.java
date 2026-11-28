package io.github.cuong.objects;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.cuong.entities.Player;

public class MangoTree {
    public float positionX;
    public float positionY;
    public float height;
    public float width;
    public float spriteWidth = 128f;
    public float spriteHeight = 128f;
    public float spriteScale = 2f;

    Rectangle interactArea;

    public float stealProgress = 0;
    public final float TIME_TO_STEAL = 2.5f;
    public boolean isPlayerInRange = false;
    private boolean isWaiting = false;
    private final MapObjectSprite sprite;

    public MangoTree(float positionX, float positionY, float width, float height) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
        this.interactArea = new Rectangle(positionX - 20f, positionY - 20f, width + 40f, height + 40f);
        this.sprite = new MapObjectSprite("objects/mango_tree/mango_tree");
    }

    public void update(float delta, Player player) {
        if (interactArea.overlaps(player.hitbox)) {
            isPlayerInRange = true;
        } else {
            isPlayerInRange = false;
        }
        stealProgress = 0f;
        sprite.update(delta);
    }

    public void render(ShapeRenderer shapeRenderer) {
        if (!sprite.hasTexture()) {
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.rect(positionX, positionY, width, height);
        }
    }

    public void render(SpriteBatch batch) {
        float scaledWidth = spriteWidth * spriteScale;
        float scaledHeight = spriteHeight * spriteScale;
        float drawX = positionX + (width - scaledWidth) / 2f;
        float drawY = positionY;
        sprite.render(batch, drawX, drawY, scaledWidth, scaledHeight);
    }

    public void dispose() {
        sprite.dispose();
    }

}
