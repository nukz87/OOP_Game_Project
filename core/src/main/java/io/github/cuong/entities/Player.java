package io.github.cuong.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Player {
    public Vector2 position;
    public float width = 32f;
    public float height = 32f;

    // 2. Tốc độ và thể lực
    private final float WALK_SPEED = 150f;
    private final float RUN_SPEED = 280f;

    public float maxStamina = 100f;
    public float currentStamina = 100f;
    public float currentCooldown = 0f;
    private final float STAMINA_DRAIN = 30f;
    private final float STAMINA_RECOVER = 15f;
    private final float COOLDOWN_RUNNING = 2f;
    public boolean isRunning = false;

    public int mangoCount = 0;
    public int gold = 0;
    public int inventoryTemplateIndex = 0;
    public Rectangle hitbox;

    public Player(float startX, float startY) {
        this.position = new Vector2(startX, startY);
        this.hitbox = new Rectangle(position.x, position.y, width, height);
    }

    public void update(float delta) {
        handleMovement(delta);
        handleStamina(delta);
        hitbox.setPosition(position.x, position.y);
    }

    private void handleMovement(float delta) {
        float moveX = 0f;
        float moveY = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.W))
            moveY += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.S))
            moveY -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            moveX -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            moveX += 1f;

        boolean isMoving = (moveX != 0 || moveY != 0);

        boolean shiftPressed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
        isRunning = shiftPressed && isMoving && (currentStamina > 0) && (currentCooldown == 0);

        if (isMoving) {
            Vector2 direction = new Vector2(moveX, moveY).nor();
            float currentSpeed = isRunning ? RUN_SPEED : WALK_SPEED;

            position.x += direction.x * currentSpeed * delta;
            position.y += direction.y * currentSpeed * delta;
        }
    }

    private void handleStamina(float delta) {
        if (isRunning) {
            currentStamina -= STAMINA_DRAIN * delta;
            if (currentStamina < 0) {
                currentStamina = 0;
                currentCooldown = COOLDOWN_RUNNING;
            }
        } else {
            if (currentCooldown > 0) {
                currentCooldown -= delta;
                if (currentCooldown < 0)
                    currentCooldown = 0;
            } else {
                if (currentStamina < maxStamina) {
                    currentStamina += STAMINA_RECOVER * delta;
                    if (currentStamina > maxStamina)
                        currentStamina = maxStamina;
                }
            }
        }
    }

    public void updateMango() {
        this.mangoCount++;
    }

    public int getInventoryTemplateIndex() {
        return inventoryTemplateIndex;
    }

    public void setInventoryTemplateIndex(int inventoryTemplateIndex) {
        this.inventoryTemplateIndex = inventoryTemplateIndex;
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(0.2f, 0.6f, 1f, 1f);
        shapeRenderer.rect(position.x, position.y, width, height);

        float barWidth = 32f;
        float barHeight = 4f;
        float staminaPercent = currentStamina / maxStamina;

        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f);
        shapeRenderer.rect(position.x, position.y + height + 6, barWidth, barHeight);

        shapeRenderer.setColor(0.2f, 0.9f, 0.2f, 1f);
        shapeRenderer.rect(position.x, position.y + height + 6, barWidth * staminaPercent, barHeight);
    }
}
