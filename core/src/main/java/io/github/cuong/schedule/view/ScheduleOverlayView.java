package io.github.cuong.schedule.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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

    private final float timeColWidth = 66f;
    private final float headerHeight = 28f;
    private final float dayColWidth;
    private final float rowHeight;

    private final GlyphLayout layout = new GlyphLayout();

    public ScheduleOverlayView(float worldWidth, float worldHeight) {
        toggleButton = new Rectangle(worldWidth - 275f, worldHeight - 44f, 125f, 32f);

        float w = worldWidth * 0.88f;
        float h = worldHeight * 0.94f;
        popupBounds = new Rectangle((worldWidth - w) / 2f, (worldHeight - h) / 2f, w, h);

        closeButton = new Rectangle(
            popupBounds.x + popupBounds.width - 148f,
            popupBounds.y + popupBounds.height - 31f,
            136f,
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
        String text = isOpen ? "[T/ESC] ĐÓNG" : "[T] XEM TKB";
        layout.setText(font, text);
        float tx = toggleButton.x + (toggleButton.width - layout.width) / 2f;
        float ty = toggleButton.y + (toggleButton.height + layout.height) / 2f;
        font.draw(batch, layout, tx, ty);
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
        float prevScaleX = font.getData().scaleX;
        float prevScaleY = font.getData().scaleY;

        font.setColor(Color.WHITE);
        font.draw(batch, "HUST - THỜI KHÓA BIỂU CÁ NHÂN", popupBounds.x + 16f, popupBounds.y + popupBounds.height - 13f);

        font.setColor(Color.WHITE);
        layout.setText(font, "[T/ESC] ĐÓNG");
        font.draw(
            batch,
            layout,
            closeButton.x + (closeButton.width - layout.width) / 2f,
            closeButton.y + (closeButton.height + layout.height) / 2f
        );

        font.setColor(Color.WHITE);
        layout.setText(font, "Tiết");
        font.draw(batch, layout, gridBounds.x + (timeColWidth - layout.width) / 2f, gridBounds.y + gridBounds.height - 8f);
        for (int d = 0; d < 3; d++) {
            String dayTitle = "Ngày " + (d + 1);
            font.setColor(Color.WHITE);
            layout.setText(font, dayTitle);
            float hx = gridBounds.x + timeColWidth + d * dayColWidth + (dayColWidth - layout.width) / 2f;
            font.draw(batch, layout, hx, gridBounds.y + gridBounds.height - 8f);
        }

        float cellsTop = gridBounds.y + gridBounds.height - headerHeight;
        font.getData().setScale(prevScaleX * 0.85f, prevScaleY * 0.85f);
        for (int r = 0; r < 12; r++) {
            String periodLabel = "Tiết " + (r + 1);
            font.setColor(r < 6 ? HUST_DARK_RED : Color.DARK_GRAY);
            layout.setText(font, periodLabel);
            float tx = gridBounds.x + (timeColWidth - layout.width) / 2f;
            float ty = cellsTop - r * rowHeight - (rowHeight - layout.height) / 2f;
            font.draw(batch, layout, tx, ty);
        }

        font.getData().setScale(prevScaleX * 0.90f, prevScaleY * 0.90f);
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
            font.draw(batch, name + " - " + cs.getRoomText(), x + 6f, y + h - 7f);
            font.draw(batch, cs.getPeriodText(), x + 6f, y + 15f);
        }

        font.getData().setScale(prevScaleX, prevScaleY);
    }

    public boolean isToggleClicked(float x, float y) { return toggleButton.contains(x, y); }
    public boolean isCloseClicked(float x, float y) { return closeButton.contains(x, y); }
}