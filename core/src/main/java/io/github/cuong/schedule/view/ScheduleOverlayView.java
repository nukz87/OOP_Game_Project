package io.github.cuong.schedule.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.cuong.schedule.model.CourseSession;
import io.github.cuong.schedule.model.ScheduleManager;

public class ScheduleOverlayView {
    private static final Color HUST_RED = new Color(0.78f, 0.06f, 0.18f, 1f);
    private static final Color HUST_DARK_RED = new Color(0.58f, 0.04f, 0.12f, 1f);
    private static final Color GRID_LINE = new Color(0.82f, 0.83f, 0.86f, 1f);

    private final Rectangle toggleButton;
    private final Rectangle popupBounds;
    private final Rectangle closeButton;
    private final Rectangle gridBounds;

    private final float timeColWidth = 54f;
    private final float headerHeight = 28f;
    private final float dayColWidth;
    private final float rowHeight;

    public ScheduleOverlayView(float worldWidth, float worldHeight) {
        toggleButton = new Rectangle(worldWidth - 275f, worldHeight - 44f, 120f, 32f);

        float w = worldWidth * 0.86f;
        float h = worldHeight * 0.88f;
        popupBounds = new Rectangle((worldWidth - w) / 2f, (worldHeight - h) / 2f, w, h);

        closeButton = new Rectangle(
            popupBounds.x + popupBounds.width - 142f,
            popupBounds.y + popupBounds.height - 32f,
            130f,
            24f
        );

        gridBounds = new Rectangle(popupBounds.x + 12f, popupBounds.y + 12f, popupBounds.width - 24f, popupBounds.height - 56f);
        dayColWidth = (gridBounds.width - timeColWidth) / 3f;
        rowHeight = (gridBounds.height - headerHeight) / 12f;
    }

    public void drawToggleButtonShape(ShapeRenderer sr, boolean isOpen) {
        sr.setColor(isOpen ? Color.DARK_GRAY : HUST_RED);
        sr.rect(toggleButton.x, toggleButton.y, toggleButton.width, toggleButton.height);
    }

    public void drawToggleButtonText(SpriteBatch batch, BitmapFont font, boolean isOpen) {
        font.setColor(Color.WHITE);
        font.draw(batch, isOpen ? "[T / ESC] ĐÓNG" : "[T] XEM TKB", toggleButton.x + 14f, toggleButton.y + 22f);
    }

    public void drawPopupShapes(ShapeRenderer sr, ScheduleManager manager) {
        sr.setColor(0.97f, 0.97f, 0.98f, 0.98f);
        sr.rect(popupBounds.x, popupBounds.y, popupBounds.width, popupBounds.height);

        sr.setColor(HUST_RED);
        sr.rect(popupBounds.x, popupBounds.y + popupBounds.height - 38f, popupBounds.width, 38f);

        sr.setColor(HUST_DARK_RED);
        sr.rect(closeButton.x, closeButton.y, closeButton.width, closeButton.height);

        sr.setColor(Color.WHITE);
        sr.rect(gridBounds.x, gridBounds.y, gridBounds.width, gridBounds.height);

        sr.setColor(HUST_DARK_RED);
        sr.rect(gridBounds.x, gridBounds.y + gridBounds.height - headerHeight, gridBounds.width, headerHeight);

        float cellsTop = gridBounds.y + gridBounds.height - headerHeight;
        sr.setColor(0.99f, 0.97f, 0.97f, 1f);
        sr.rect(gridBounds.x + timeColWidth, cellsTop - 6 * rowHeight, gridBounds.width - timeColWidth, 6 * rowHeight);
        sr.setColor(0.96f, 0.97f, 0.99f, 1f);
        sr.rect(gridBounds.x + timeColWidth, gridBounds.y, gridBounds.width - timeColWidth, 6 * rowHeight);

        sr.setColor(0.94f, 0.94f, 0.96f, 1f);
        sr.rect(gridBounds.x, gridBounds.y, timeColWidth, gridBounds.height - headerHeight);

        Array<CourseSession> sessions = manager.getSessions();
        for (int i = 0; i < sessions.size; i++) {
            CourseSession cs = sessions.get(i);
            int subIdx = manager.getSubjectIndexByName(cs.getSubjectName());
            int col = cs.getDay() - 1;
            int span = cs.getEndPeriod() - cs.getStartPeriod() + 1;

            float x = gridBounds.x + timeColWidth + col * dayColWidth + 4f;
            float topY = cellsTop - (cs.getStartPeriod() - 1) * rowHeight;
            float h = span * rowHeight - 2f;

            sr.setColor(subIdx != -1 ? ScheduleManager.SUBJECTS[subIdx].badgeColor : HUST_RED);
            sr.rect(x, topY - h - 1f, dayColWidth - 8f, h);
        }
    }

    public void drawPopupBorders(ShapeRenderer sr) {
        sr.setColor(HUST_RED);
        sr.rect(popupBounds.x, popupBounds.y, popupBounds.width, popupBounds.height);
        sr.rect(gridBounds.x, gridBounds.y, gridBounds.width, gridBounds.height);

        float cellsTop = gridBounds.y + gridBounds.height - headerHeight;
        for (int r = 1; r <= 12; r++) {
            float y = cellsTop - r * rowHeight;
            if (r == 6) {
                sr.setColor(HUST_RED);
                sr.line(gridBounds.x, y, gridBounds.x + gridBounds.width, y);
                sr.line(gridBounds.x, y + 1f, gridBounds.x + gridBounds.width, y + 1f);
            } else {
                sr.setColor(GRID_LINE);
                sr.line(gridBounds.x, y, gridBounds.x + gridBounds.width, y);
            }
        }

        sr.setColor(GRID_LINE);
        for (int c = 0; c <= 3; c++) {
            float x = gridBounds.x + timeColWidth + c * dayColWidth;
            sr.line(x, gridBounds.y, x, gridBounds.y + gridBounds.height);
        }
    }

    public void drawPopupText(SpriteBatch batch, BitmapFont font, ScheduleManager manager) {
        font.setColor(Color.WHITE);
        font.draw(batch, "HUST - THỜI KHÓA BIỂU CÁ NHÂN", popupBounds.x + 16f, popupBounds.y + popupBounds.height - 13f);
        font.draw(batch, "[T / ESC] ĐÓNG", closeButton.x + 11f, closeButton.y + 17f);

        font.draw(batch, "Tiết", gridBounds.x + 14f, gridBounds.y + gridBounds.height - 8f);
        for (int d = 0; d < 3; d++) {
            float hx = gridBounds.x + timeColWidth + d * dayColWidth + dayColWidth / 2f - 24f;
            font.draw(batch, "Ngày " + (d + 1), hx, gridBounds.y + gridBounds.height - 8f);
        }

        float cellsTop = gridBounds.y + gridBounds.height - headerHeight;
        for (int r = 0; r < 12; r++) {
            float ty = cellsTop - r * rowHeight - (rowHeight / 2f) + 5f;
            font.setColor(r < 6 ? HUST_DARK_RED : Color.DARK_GRAY);
            font.draw(batch, "Tiết " + (r + 1), gridBounds.x + 8f, ty);
        }

        Array<CourseSession> sessions = manager.getSessions();
        for (int i = 0; i < sessions.size; i++) {
            CourseSession cs = sessions.get(i);
            int subIdx = manager.getSubjectIndexByName(cs.getSubjectName());
            int col = cs.getDay() - 1;
            int span = cs.getEndPeriod() - cs.getStartPeriod() + 1;

            float x = gridBounds.x + timeColWidth + col * dayColWidth + 4f;
            float topY = cellsTop - (cs.getStartPeriod() - 1) * rowHeight;
            float h = span * rowHeight - 2f;
            float y = topY - h - 1f;

            String name = (subIdx != -1) ? ScheduleManager.SUBJECTS[subIdx].shortName : cs.getSubjectName();
            font.setColor(Color.WHITE);
            font.draw(batch, name + " - " + cs.getRoomText(), x + 8f, y + h - 7f);
            font.draw(batch, cs.getPeriodText(), x + 8f, y + 16f);
        }
    }

    public boolean isToggleClicked(float x, float y) { return toggleButton.contains(x, y); }
    public boolean isCloseClicked(float x, float y) { return closeButton.contains(x, y); }
}