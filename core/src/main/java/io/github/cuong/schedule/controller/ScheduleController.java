package io.github.cuong.schedule.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.cuong.schedule.model.CourseSession;
import io.github.cuong.schedule.model.ScheduleManager;
import io.github.cuong.schedule.view.ScheduleOverlayView;
import io.github.cuong.schedule.view.ScheduleSetupView;

public class ScheduleController {
    private final ScheduleManager manager;
    private final ScheduleSetupView setupView;
    private final ScheduleOverlayView overlayView;

    private boolean isOverlayOpen;
    private final Vector2 touchPos;
    private int draggingSubjectIdx = -1;

    public ScheduleController(float worldWidth, float worldHeight) {
        manager = new ScheduleManager();
        setupView = new ScheduleSetupView(worldWidth, worldHeight);
        overlayView = new ScheduleOverlayView(worldWidth, worldHeight);
        isOverlayOpen = false;
        touchPos = new Vector2();
    }

    public void handleInput(Viewport viewport) {
        if (manager.isConfirmed() && Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            isOverlayOpen = !isOverlayOpen;
        }

        touchPos.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(touchPos);

        if (!manager.isConfirmed()) {
            if (Gdx.input.justTouched()) {
                // 1. Kiểm tra bấm nút Xóa nhỏ [x] trên khối môn đã xếp
                Array<CourseSession> sessions = manager.getSessions();
                for (int i = sessions.size - 1; i >= 0; i--) {
                    CourseSession cs = sessions.get(i);
                    Rectangle cr = setupView.getCourseRect(cs);
                    Rectangle xr = setupView.getRemoveBadgeRect(cr);
                    int subIdx = manager.getSubjectIndexByName(cs.getSubjectName());

                    if (xr.contains(touchPos.x, touchPos.y)) {
                        manager.removeSubject(subIdx);
                        return;
                    }
                    // 2. Bắt đầu kéo một khối môn đã nằm trên lưới để di chuyển vị trí
                    if (cr.contains(touchPos.x, touchPos.y)) {
                        draggingSubjectIdx = subIdx;
                        return;
                    }
                }

                // 3. Bắt đầu kéo thẻ môn từ danh sách bên trái
                int cardIdx = setupView.getCardSubjectAt(touchPos.x, touchPos.y);
                if (cardIdx != -1) {
                    draggingSubjectIdx = cardIdx;
                    return;
                }

                // 4. Các nút chức năng phía dưới
                if (setupView.isClearClicked(touchPos.x, touchPos.y)) {
                    manager.clearAll();
                } else if (setupView.isAutoFillClicked(touchPos.x, touchPos.y)) {
                    manager.autoGenerateValidSchedule();
                } else if (setupView.isConfirmClicked(touchPos.x, touchPos.y) && manager.validateSchedule() == null) {
                    manager.setConfirmed(true);
                }
            } else if (!Gdx.input.isTouched() && draggingSubjectIdx != -1) {
                // 5. Thả chuột -> đặt môn vào ô tiết trên lưới
                int[] cell = setupView.getGridCellAt(touchPos.x, touchPos.y);
                if (cell != null) {
                    manager.placeSubject(draggingSubjectIdx, cell[0], cell[1]);
                }
                draggingSubjectIdx = -1;
            }
            return;
        }

        if (!Gdx.input.justTouched()) return;

        if (overlayView.isToggleClicked(touchPos.x, touchPos.y)) {
            isOverlayOpen = !isOverlayOpen;
        } else if (isOverlayOpen && overlayView.isCloseClicked(touchPos.x, touchPos.y)) {
            isOverlayOpen = false;
        }
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        if (!manager.isConfirmed()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            setupView.drawShapes(shapeRenderer, manager, draggingSubjectIdx, touchPos.x, touchPos.y);
            shapeRenderer.end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            setupView.drawBorders(shapeRenderer, manager, draggingSubjectIdx, touchPos.x, touchPos.y);
            shapeRenderer.end();

            batch.begin();
            setupView.drawText(batch, font, manager, draggingSubjectIdx, touchPos.x, touchPos.y);
            batch.end();
        } else {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            overlayView.drawToggleButtonShape(shapeRenderer, isOverlayOpen);
            if (isOverlayOpen) {
                overlayView.drawPopupShapes(shapeRenderer, manager);
            }
            shapeRenderer.end();

            if (isOverlayOpen) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                overlayView.drawPopupBorders(shapeRenderer);
                shapeRenderer.end();
            }

            batch.begin();
            overlayView.drawToggleButtonText(batch, font, isOverlayOpen);
            if (isOverlayOpen) {
                overlayView.drawPopupText(batch, font, manager);
            }
            batch.end();
        }
    }

    public boolean isBlockingGameplay() {
        return !manager.isConfirmed() || isOverlayOpen;
    }

    public ScheduleManager getManager() {
        return manager;
    }
}