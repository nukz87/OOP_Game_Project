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
        // interactArea.x = positionX - 20f; e
        // interactArea.y = positionY - 20f;
        // interactArea.height = height + 40f;
        // interactArea.width = width + 40f;

        if (interactArea.overlaps(player.hitbox)) {
            isPlayerInRange = true;
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                if (!isWaiting) {
                    stealProgress += delta;
                    if (stealProgress >= TIME_TO_STEAL) {
                        stealProgress = 0;
                        // isWaiting = true;
                        player.updateMango();
                    }
                }
            } else {
                stealProgress = 0f;
                isWaiting = false;
            }
        } else {
            isPlayerInRange = false;
            stealProgress = 0f;
            isWaiting = false;
        }
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect(positionX, positionY, width, height);

        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.rect(positionX, positionY + height + 5f, width, 3f);

        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(positionX, positionY + height + 5f, width * stealProgress / TIME_TO_STEAL, 3f);

    }

}
