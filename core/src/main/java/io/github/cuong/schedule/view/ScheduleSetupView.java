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
import io.github.cuong.schedule.model.ScheduleManager.SubjectInfo;

public class ScheduleSetupView {
    private static final Color HUST_RED = new Color(0.78f, 0.06f, 0.18f, 1f);
    private static final Color HUST_DARK_RED = new Color(0.58f, 0.04f, 0.12f, 1f);
    private static final Color BG_WHITE = new Color(0.97f, 0.97f, 0.98f, 0.98f);
    private static final Color SIDEBAR_BG = new Color(0.93f, 0.93f, 0.95f, 1f);
    private static final Color GRID_LINE = new Color(0.82f, 0.83f, 0.86f, 1f);

    private final Rectangle panelBounds;
    private final Rectangle sidebarBounds;
    private final Rectangle gridBounds;

    private final Array<Rectangle> paletteCards;
    private final Rectangle clearButton;
    private final Rectangle autoFillButton;
    private final Rectangle confirmButton;

    private final float timeColWidth = 66f;
    private final float headerHeight = 28f;
    private final float dayColWidth;
    private final float rowHeight;

    private final GlyphLayout statusLayout = new GlyphLayout();

    public ScheduleSetupView(float worldWidth, float worldHeight) {
        float w = worldWidth * 0.96f;
        float h = worldHeight * 0.95f;
        panelBounds = new Rectangle((worldWidth - w) / 2f, (worldHeight - h) / 2f, w, h);

        float topBarH = 38f;
        float bottomBarH = 38f;
        float contentY = panelBounds.y + bottomBarH;
        float contentH = panelBounds.height - topBarH - bottomBarH;

        float sidebarW = 222f;
        sidebarBounds = new Rectangle(panelBounds.x + 8f, contentY + 4f, sidebarW, contentH - 8f);
        gridBounds = new Rectangle(sidebarBounds.x + sidebarW + 8f, contentY + 4f, panelBounds.width - sidebarW - 24f, contentH - 8f);

        dayColWidth = (gridBounds.width - timeColWidth) / 3f;
        rowHeight = (gridBounds.height - headerHeight) / 12f;

        paletteCards = new Array<>();
        float cardStartY = sidebarBounds.y + sidebarBounds.height - 68f;
        for (int i = 0; i < ScheduleManager.SUBJECTS.length; i++) {
            paletteCards.add(new Rectangle(
                sidebarBounds.x + 10f,
                cardStartY - i * 46f,
                sidebarBounds.width - 20f,
                38f
            ));
        }

        clearButton = new Rectangle(panelBounds.x + 12f, panelBounds.y + 6f, 95f, 26f);
        autoFillButton = new Rectangle(panelBounds.x + 115f, panelBounds.y + 6f, 130f, 26f);
        confirmButton = new Rectangle(panelBounds.x + panelBounds.width - 198f, panelBounds.y + 6f, 186f, 26f);
    }

    public int getCardSubjectAt(float x, float y) {
        for (int i = 0; i < paletteCards.size; i++) {
            if (paletteCards.get(i).contains(x, y)) return i;
        }
        return -1;
    }

    public Rectangle getCourseRect(CourseSession cs) {
        int col = cs.getDay() - 1;
        int span = cs.getEndPeriod() - cs.getStartPeriod() + 1;
        float x = gridBounds.x + timeColWidth + col * dayColWidth + 3f;
        float topY = gridBounds.y + gridBounds.height - headerHeight - (cs.getStartPeriod() - 1) * rowHeight;
        float h = span * rowHeight - 2f;
        return new Rectangle(x, topY - h - 1f, dayColWidth - 6f, h);
    }

    public Rectangle getRemoveBadgeRect(Rectangle courseRect) {
        return new Rectangle(courseRect.x + courseRect.width - 18f, courseRect.y + courseRect.height - 18f, 16f, 16f);
    }

    public int[] getGridCellAt(float x, float y) {
        float cellsLeft = gridBounds.x + timeColWidth;
        float cellsRight = gridBounds.x + gridBounds.width;
        float cellsTop = gridBounds.y + gridBounds.height - headerHeight;
        float cellsBottom = gridBounds.y;

        if (x < cellsLeft || x > cellsRight || y < cellsBottom || y > cellsTop) return null;

        int col = (int) ((x - cellsLeft) / dayColWidth);
        int row = (int) ((cellsTop - y) / rowHeight);
        col = Math.max(0, Math.min(2, col));
        row = Math.max(0, Math.min(11, row));
        return new int[]{col + 1, row + 1};
    }

    public void drawShapes(ShapeRenderer sr, ScheduleManager manager, int draggingSubIdx, float dragX, float dragY) {
        sr.setColor(BG_WHITE);
        sr.rect(panelBounds.x, panelBounds.y, panelBounds.width, panelBounds.height);

        sr.setColor(HUST_RED);
        sr.rect(panelBounds.x, panelBounds.y + panelBounds.height - 36f, panelBounds.width, 36f);

        sr.setColor(SIDEBAR_BG);
        sr.rect(sidebarBounds.x, sidebarBounds.y, sidebarBounds.width, sidebarBounds.height);

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

        if (draggingSubIdx != -1) {
            int[] cell = getGridCellAt(dragX, dragY);
            if (cell != null) {
                int day = cell[0];
                int startP = cell[1];
                int dur = ScheduleManager.SUBJECTS[draggingSubIdx].duration;
                int visibleSpan = Math.min(dur, 13 - startP);
                boolean valid = manager.canPlace(draggingSubIdx, day, startP);

                sr.setColor(valid ? new Color(0.20f, 0.78f, 0.35f, 0.35f) : new Color(0.88f, 0.15f, 0.20f, 0.35f));
                float px = gridBounds.x + timeColWidth + (day - 1) * dayColWidth + 2f;
                float pTop = cellsTop - (startP - 1) * rowHeight;
                float ph = visibleSpan * rowHeight;
                sr.rect(px, pTop - ph, dayColWidth - 4f, ph);
            }
        }

        for (int i = 0; i < paletteCards.size; i++) {
            Rectangle r = paletteCards.get(i);
            SubjectInfo info = ScheduleManager.SUBJECTS[i];
            boolean placed = manager.isSubjectPlaced(i);

            if (placed) {
                sr.setColor(0.82f, 0.83f, 0.85f, 1f);
            } else {
                sr.setColor(info.badgeColor);
            }
            sr.rect(r.x, r.y, r.width, r.height);

            sr.setColor(placed ? Color.GRAY : HUST_RED);
            sr.rect(r.x, r.y, 5f, r.height);
        }

        Array<CourseSession> sessions = manager.getSessions();
        for (int i = 0; i < sessions.size; i++) {
            CourseSession cs = sessions.get(i);
            int subIdx = manager.getSubjectIndexByName(cs.getSubjectName());
            if (subIdx == draggingSubIdx) continue;

            Rectangle cr = getCourseRect(cs);
            Color c = (subIdx != -1) ? ScheduleManager.SUBJECTS[subIdx].badgeColor : HUST_RED;
            sr.setColor(c);
            sr.rect(cr.x, cr.y, cr.width, cr.height);

            Rectangle xr = getRemoveBadgeRect(cr);
            sr.setColor(HUST_DARK_RED);
            sr.rect(xr.x, xr.y, xr.width, xr.height);
        }

        sr.setColor(0.45f, 0.47f, 0.52f, 1f);
        sr.rect(clearButton.x, clearButton.y, clearButton.width, clearButton.height);

        sr.setColor(HUST_RED);
        sr.rect(autoFillButton.x, autoFillButton.y, autoFillButton.width, autoFillButton.height);

        boolean valid = (manager.validateSchedule() == null);
        sr.setColor(valid ? new Color(0.14f, 0.62f, 0.28f, 1f) : new Color(0.75f, 0.76f, 0.78f, 1f));
        sr.rect(confirmButton.x, confirmButton.y, confirmButton.width, confirmButton.height);

        if (draggingSubIdx != -1) {
            SubjectInfo info = ScheduleManager.SUBJECTS[draggingSubIdx];
            float dw = dayColWidth - 8f;
            float dh = info.duration * rowHeight - 4f;
            sr.setColor(info.badgeColor);
            sr.rect(dragX - dw / 2f, dragY - dh / 2f, dw, dh);
        }
    }

    public void drawBorders(ShapeRenderer sr, ScheduleManager manager, int draggingSubIdx, float dragX, float dragY) {
        sr.setColor(HUST_RED);
        sr.rect(panelBounds.x, panelBounds.y, panelBounds.width, panelBounds.height);
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

        if (draggingSubIdx != -1) {
            SubjectInfo info = ScheduleManager.SUBJECTS[draggingSubIdx];
            float dw = dayColWidth - 8f;
            float dh = info.duration * rowHeight - 4f;
            sr.setColor(HUST_RED);
            sr.rect(dragX - dw / 2f, dragY - dh / 2f, dw, dh);
        }
    }

    public void drawText(SpriteBatch batch, BitmapFont font, ScheduleManager manager, int draggingSubIdx, float dragX, float dragY) {
        float prevScaleX = font.getData().scaleX;
        float prevScaleY = font.getData().scaleY;

        font.setColor(Color.WHITE);
        font.draw(batch, "HUST - XẾP THỜI KHÓA BIỂU", panelBounds.x + 14f, panelBounds.y + panelBounds.height - 12f);

        font.setColor(HUST_RED);
        font.draw(batch, "DANH SÁCH MÔN HỌC", sidebarBounds.x + 12f, sidebarBounds.y + sidebarBounds.height - 10f);

        for (int i = 0; i < paletteCards.size; i++) {
            Rectangle r = paletteCards.get(i);
            SubjectInfo info = ScheduleManager.SUBJECTS[i];
            boolean placed = manager.isSubjectPlaced(i);

            font.setColor(placed ? Color.DARK_GRAY : Color.WHITE);
            String label = placed ? info.shortName : (info.shortName + " (" + info.duration + "t)");
            font.draw(batch, label, r.x + 10f, r.y + 24f);
            if (placed) {
                statusLayout.setText(font, "[Đã xếp]");
                font.draw(batch, statusLayout, r.x + r.width - statusLayout.width - 8f, r.y + 24f);
            }
        }

        font.setColor(Color.WHITE);
        statusLayout.setText(font, "Tiết");
        font.draw(batch, statusLayout, gridBounds.x + (timeColWidth - statusLayout.width) / 2f, gridBounds.y + gridBounds.height - 8f);
        for (int d = 0; d < 3; d++) {
            String dayTitle = "Ngày " + (d + 1);
            font.setColor(Color.WHITE);
            statusLayout.setText(font, dayTitle);
            float hx = gridBounds.x + timeColWidth + d * dayColWidth + (dayColWidth - statusLayout.width) / 2f;
            font.draw(batch, statusLayout, hx, gridBounds.y + gridBounds.height - 8f);
        }

        float cellsTop = gridBounds.y + gridBounds.height - headerHeight;
        font.getData().setScale(prevScaleX * 0.85f, prevScaleY * 0.85f);
        for (int r = 0; r < 12; r++) {
            String periodLabel = "Tiết " + (r + 1);
            font.setColor(r < 6 ? HUST_DARK_RED : Color.DARK_GRAY);
            statusLayout.setText(font, periodLabel);
            float tx = gridBounds.x + (timeColWidth - statusLayout.width) / 2f;
            float ty = cellsTop - r * rowHeight - (rowHeight - statusLayout.height) / 2f;
            font.draw(batch, statusLayout, tx, ty);
        }
        font.getData().setScale(prevScaleX, prevScaleY);

        Array<CourseSession> sessions = manager.getSessions();
        for (int i = 0; i < sessions.size; i++) {
            CourseSession cs = sessions.get(i);
            int subIdx = manager.getSubjectIndexByName(cs.getSubjectName());
            if (subIdx == draggingSubIdx) continue;

            Rectangle cr = getCourseRect(cs);
            Rectangle xr = getRemoveBadgeRect(cr);
            SubjectInfo info = (subIdx != -1) ? ScheduleManager.SUBJECTS[subIdx] : null;
            String displayName = (info != null) ? info.shortName : cs.getSubjectName();

            font.setColor(Color.WHITE);
            font.draw(batch, displayName, cr.x + 6f, cr.y + cr.height - 6f);
            font.draw(batch, cs.getRoomText() + " (T" + cs.getStartPeriod() + "-" + cs.getEndPeriod() + ")", cr.x + 6f, cr.y + 16f);
            font.draw(batch, "x", xr.x + 5f, xr.y + 13f);
        }

        font.setColor(Color.WHITE);
        font.draw(batch, "Xóa hết", clearButton.x + 20f, clearButton.y + 18f);
        font.draw(batch, "Xếp ngẫu nhiên", autoFillButton.x + 14f, autoFillButton.y + 18f);
        font.draw(batch, "XÁC NHẬN & VÀO GAME", confirmButton.x + 10f, confirmButton.y + 18f);

        String err = manager.validateSchedule();
        if (err != null) {
            font.setColor(HUST_RED);
            font.draw(batch, err, autoFillButton.x + autoFillButton.width + 14f, confirmButton.y + 18f);
        } else {
            font.setColor(new Color(0.10f, 0.52f, 0.22f, 1f));
            font.draw(batch, "Đã xếp " + manager.getSessions().size + "/6 môn", autoFillButton.x + autoFillButton.width + 14f, confirmButton.y + 18f);
        }

        if (draggingSubIdx != -1) {
            SubjectInfo info = ScheduleManager.SUBJECTS[draggingSubIdx];
            float dw = dayColWidth - 8f;
            float dh = info.duration * rowHeight - 4f;
            font.setColor(Color.WHITE);
            font.draw(batch, info.shortName, dragX - dw / 2f + 8f, dragY + dh / 2f - 8f);
            font.draw(batch, "(" + info.duration + " tiết)", dragX - dw / 2f + 8f, dragY - dh / 2f + 18f);
        }
    }

    public boolean isClearClicked(float x, float y) { return clearButton.contains(x, y); }
    public boolean isAutoFillClicked(float x, float y) { return autoFillButton.contains(x, y); }
    public boolean isConfirmClicked(float x, float y) { return confirmButton.contains(x, y); }
}