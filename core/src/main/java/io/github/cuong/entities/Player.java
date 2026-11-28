package io.github.cuong.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Player {
    public Vector2 position;
    public float width = 64f;
    public float height = 64f;

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
    private static final float IDLE_TRIGGER_DELAY = 7f;

    public Rectangle hitbox;
    public PlayerState state = PlayerState.BASE;
    public Direction4 facingDirection = Direction4.DOWN;
    private float stationaryTimer = 0f;
    private final PlayerAnimator animator;

    public Player(float startX, float startY) {
        this.position = new Vector2(startX, startY);
        this.hitbox = new Rectangle(position.x, position.y, width, height);
        this.animator = new PlayerAnimator();
    }

    public void update(float delta) {
        handleMovement(delta);
        handleStamina(delta);
        hitbox.setPosition(position.x, position.y);
        animator.update(delta, state, facingDirection);
        if (state == PlayerState.IDLE && animator.isIdleFinished()) {
            state = PlayerState.BASE;
            stationaryTimer = 0f;
        }
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

            stationaryTimer = 0f;
            facingDirection = Direction4.fromVector(moveX, moveY);
            state = isRunning ? PlayerState.RUN : PlayerState.WALK;
        } else if (state == PlayerState.WALK || state == PlayerState.RUN) {
            state = PlayerState.BASE;
            stationaryTimer = 0f;
        } else if (state == PlayerState.BASE) {
            stationaryTimer += delta;
            if (stationaryTimer >= IDLE_TRIGGER_DELAY) {
                state = PlayerState.IDLE;
                stationaryTimer = 0f;
            }
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

    public void render(SpriteBatch batch) {
        TextureRegion frame = animator.getCurrentFrame();
        if (frame != null) {
            batch.draw(frame, position.x, position.y, width, height);
        }
    }

    public void render(ShapeRenderer shapeRenderer) {
        if (!animator.hasLoadedFrames() || animator.getCurrentFrame() == null) {
            shapeRenderer.setColor(0.2f, 0.6f, 1f, 1f);
            shapeRenderer.rect(position.x, position.y, width, height);
        }

        float barWidth = 32f;
        float barHeight = 4f;
        float staminaPercent = currentStamina / maxStamina;

        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f);
        shapeRenderer.rect(position.x, position.y + height + 6, barWidth, barHeight);

        shapeRenderer.setColor(0.2f, 0.9f, 0.2f, 1f);
        shapeRenderer.rect(position.x, position.y + height + 6, barWidth * staminaPercent, barHeight);
    }

    public void dispose() {
        animator.dispose();
    }
}
