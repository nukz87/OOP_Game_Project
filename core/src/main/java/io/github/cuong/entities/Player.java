package io.github.cuong.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Player {
    // 1. Tọa độ và kích thước
    public Vector2 position; // Vector2 chứa cả x và y
    public float width = 32f; // Chiều rộng nhân vật (pixel)
    public float height = 32f; // Chiều cao nhân vật (pixel)

    // 2. Tốc độ và thể lực
    private final float WALK_SPEED = 150f; // Đi bộ: 150 pixel / giây
    private final float RUN_SPEED = 280f; // Chạy nhanh: 280 pixel / giây

    public float maxStamina = 100f;
    public float currentStamina = 100f;
    public float currentCooldown = 0f;
    private final float STAMINA_DRAIN = 30f; // Tốn 30 thể lực / giây khi chạy
    private final float STAMINA_RECOVER = 15f; // Hồi 15 thể lực / giây khi nghỉ/đi bộ
    private final float COOLDOWN_RUNNING = 2f; //
    public boolean isRunning = false;

    // 3. Túi đồ đơn giản
    public int mangoCount = 0;
    public Rectangle hitbox;

    // Hàm khởi tạo (Constructor): chạy 1 lần khi nhân vật được sinh ra
    public Player(float startX, float startY) {
        this.position = new Vector2(startX, startY);
        this.hitbox = new Rectangle(position.x, position.y, width, height);
    }

    // Hàm cập nhật logic mỗi frame
    public void update(float delta) {
        handleMovement(delta);
        handleStamina(delta);
        hitbox.setPosition(position.x, position.y);
    }

    private void handleMovement(float delta) {
        float moveX = 0f;
        float moveY = 0f;

        // Đọc phím WASD
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            moveY += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.S))
            moveY -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            moveX -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            moveX += 1f;

        // Kiểm tra xem người chơi có đang di chuyển không
        boolean isMoving = (moveX != 0 || moveY != 0);

        // Kiểm tra phím Shift để chạy (chỉ chạy được khi còn stamina và đang di chuyển)
        boolean shiftPressed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
        isRunning = shiftPressed && isMoving && (currentStamina > 0) && (currentCooldown == 0);

        // Chuẩn hóa vector: Đi chéo (W + D) không bị chạy nhanh hơn đi thẳng
        if (isMoving) {
            Vector2 direction = new Vector2(moveX, moveY).nor();
            float currentSpeed = isRunning ? RUN_SPEED : WALK_SPEED;

            // Công thức: Vị trí mới = Vị trí cũ + Hướng * Tốc độ * Thời gian trôi qua
            // (delta)
            position.x += direction.x * currentSpeed * delta;
            position.y += direction.y * currentSpeed * delta;
        }
    }

    private void handleStamina(float delta) {
        if (isRunning) {
            // Đang chạy: Trừ thể lực
            currentStamina -= STAMINA_DRAIN * delta;
            if (currentStamina < 0) {
                currentStamina = 0;
                currentCooldown = COOLDOWN_RUNNING;
            }
        } else {
            // Không chạy: Hồi dần thể lực
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

    // Tạm thời vẽ nhân vật bằng hình khối (ShapeRenderer) để kiểm tra logic trước
    public void render(ShapeRenderer shapeRenderer) {
        // Vẽ thân nhân vật (hình chữ nhật màu xanh lam)
        shapeRenderer.setColor(0.2f, 0.6f, 1f, 1f);
        shapeRenderer.rect(position.x, position.y, width, height);

        // Vẽ thanh Stamina nhỏ ngay trên đầu nhân vật
        float barWidth = 32f;
        float barHeight = 4f;
        float staminaPercent = currentStamina / maxStamina;

        // Nền thanh stamina (màu xám)
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f);
        shapeRenderer.rect(position.x, position.y + height + 6, barWidth, barHeight);

        // Lượng stamina hiện tại (màu xanh lá)
        shapeRenderer.setColor(0.2f, 0.9f, 0.2f, 1f);
        shapeRenderer.rect(position.x, position.y + height + 6, barWidth * staminaPercent, barHeight);
    }
}
