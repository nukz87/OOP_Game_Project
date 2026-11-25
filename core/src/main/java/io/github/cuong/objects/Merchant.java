package io.github.cuong.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import io.github.cuong.entities.Player;

/**
 * Merchant object in the game world.
 * Player can walk near this object and press SPACE to open the shop menu.
 */
public class Merchant {
    public float positionX;
    public float positionY;
    public float width;
    public float height;

    private final Rectangle interactArea;
    public boolean isPlayerInRange = false;

    public Merchant(float positionX, float positionY, float width, float height) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
        this.interactArea = new Rectangle(positionX - 20f, positionY - 20f, width + 40f, height + 40f);
    }

    public void update(float delta, Player player) {
        isPlayerInRange = interactArea.overlaps(player.hitbox);
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(positionX, positionY, width, height);
    }
}
