package io.github.cuong.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

public class Guard {
    public Vector2 position;
    public Rectangle hitbox;
    public float width = 32f;
    public float height = 32f;

    public float currentSpeed;
    public float speedWalk = 100f;
    public float speedChase = 230f;
    public float walkRange = 300f;

    public Vector2 pointA, pointB;
    public List<Vector2> pointList;
    public Vector2 currentTargetX;
    public float radiusDetect = 150f;
    public float radiusView = 200f;
    public boolean playerCatched = false;
    public Circle detectRange;

    private enum State {
        WALK,
        CHASE
    };

    public State currentState = State.WALK;

    public Guard(float positionX, float positionY) {
        this.position = new Vector2(positionX, positionY);
        // this.pointA = new Vector2(positionX - 300f, positionY);
        // this.pointB = new Vector2(positionX + 300f, positionY);
        this.hitbox = new Rectangle(positionX, positionY, width, height);
        this.currentTargetX = new Vector2(positionX, positionY);
        this.detectRange = new Circle(positionX + width / 2f, positionY + height / 2f, radiusDetect);
        this.pointList = new ArrayList<Vector2>();
        this.pointList.add(new Vector2(positionX - walkRange, positionY + walkRange));
        this.pointList.add(new Vector2(positionX + walkRange, positionY + walkRange));
        this.pointList.add(new Vector2(positionX + walkRange, positionY - walkRange));
        this.pointList.add(new Vector2(positionX - walkRange, positionY - walkRange));
    }

    public void update(float delta, Player player) {
        hitbox.setPosition(position.x, position.y);
        detectRange.setPosition(position.x + width / 2f, position.y + height / 2f);

        System.out.println(currentState);
        System.out.println(position);
        System.out.println(currentTargetX);
        // System.out.println(pointA);
        // System.out.println(pointB);

        if (currentState == State.WALK) {

            handleWalkMovement(delta);

            if (Intersector.overlaps(detectRange, player.hitbox)) {
                currentState = State.CHASE;
                currentTargetX = player.position;
            }
        } else {
            handleChaseMovement(delta, player);

            if (!Intersector.overlaps(detectRange, player.hitbox)) {
                currentState = State.WALK;
            }

            if (hitbox.overlaps(player.hitbox)) {
                catchPlayer(player);
            }
        }

        Vector2 dir = new Vector2(currentTargetX).sub(this.position);
        dir.nor();
        position.x += dir.x * currentSpeed * delta;
        position.y += dir.y * currentSpeed * delta;

    }

    private void handleWalkMovement(float delta) {
        currentSpeed = speedWalk;
        // if (currentTargetX != pointA && currentTargetX != pointB) {
        // currentTargetX = (currentTargetX.dst(pointA) < currentTargetX.dst(pointB)) ?
        // pointA : pointB;
        // }
        // if (position.dst(currentTargetX) <= 5f) {
        // currentTargetX = (currentTargetX == pointA) ? pointB : pointA;
        // }

        int checkTargetPoint = -1;
        int minDist = 0;
        for (int i = 0; i < pointList.size(); i++) {
            Vector2 point = new Vector2(pointList.get(i));
            if (currentTargetX.equals(point)) {
                checkTargetPoint = i;
                break;
            }
            if (currentTargetX.dst(point) < currentTargetX.dst(pointList.get(minDist)))
                minDist = i;
        }

        if (checkTargetPoint == -1) {
            currentTargetX = pointList.get(minDist);
        } else {
            if (position.dst(currentTargetX) <= 5f) {
                if (checkTargetPoint == pointList.size() - 1)
                    currentTargetX = pointList.get(0);
                else
                    currentTargetX = pointList.get(checkTargetPoint + 1);
            }
        }

    }

    private void handleChaseMovement(float delta, Player player) {
        currentSpeed = speedChase;
        currentTargetX = player.position;
    }

    private void catchPlayer(Player player) {
        // playerCatched = true;
        player.mangoCount = 0;
        player.position.set(50f, 50f);
    }

    public void renderBody(ShapeRenderer shapeRenderer) {

        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(position.x, position.y, width, height);

    }

    public void renderDebug(ShapeRenderer shapeRenderer) {

        shapeRenderer.setColor(Color.CLEAR_WHITE);
        shapeRenderer.circle(position.x + width / 2f, position.y + height / 2f, radiusDetect);
    }
}
