package io.github.cuong.objects;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.cuong.entities.Player;

public class MangoTree {
    public float positionX;
    public float positionY;
    public float height;
    public float width;

    Rectangle interactArea;

    public float stealProgress = 0;
    public final float TIME_TO_STEAL = 2.5f;
    public boolean isPlayerInRange = false;
    private boolean isWaiting = false;

    public MangoTree(float positionX, float positionY, float width, float height) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
        this.interactArea = new Rectangle(positionX - 20f, positionY - 20f, width + 40f, height + 40f);
    }

    public void update(float delta, Player player) {
        if (interactArea.overlaps(player.hitbox)) {
            isPlayerInRange = true;
        } else {
            isPlayerInRange = false;
        }
        stealProgress = 0f;
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect(positionX, positionY, width, height);
    }

}
