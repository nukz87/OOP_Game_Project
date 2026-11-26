package io.github.cuong.schedule.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class ScheduleManager {
    public static class SubjectInfo {
        public final String name;
        public final String shortName;
        public final int duration;
        public final Color badgeColor;

        public SubjectInfo(String name, String shortName, int duration, Color badgeColor) {
            this.name = name;
            this.shortName = shortName;
            this.duration = duration;
            this.badgeColor = badgeColor;
        }
    }

    public static final SubjectInfo[] SUBJECTS = {
        new SubjectInfo("Giải tích I", "Giải tích I", 3, new Color(0.55f, 0.72f, 0.25f, 1f)),
        new SubjectInfo("Đại số", "Đại số", 3, new Color(0.18f, 0.62f, 0.52f, 1f)),
        new SubjectInfo("OOP", "OOP", 3, new Color(0.24f, 0.56f, 0.84f, 1f)),
        new SubjectInfo("Xác suất Thống kê", "Xác suất TK", 2, new Color(0.32f, 0.68f, 0.42f, 1f)),
        new SubjectInfo("Triết học Mác - Lênin", "Triết Mác - Lênin", 2, new Color(0.54f, 0.36f, 0.76f, 1f)),
        new SubjectInfo("Vật lý Đại cương II", "Vật lý ĐC II", 2, new Color(0.84f, 0.44f, 0.22f, 1f))
    };

    private static final String[] LOCATIONS = {"D3", "D5", "D7", "D9", "D6", "D8"};
    private static final int[] ROOM_NUMBERS = {101, 201, 204, 302, 305, 401};

    private final Array<CourseSession> placedCourses;
    private boolean isConfirmed = false;

    public ScheduleManager() {
        placedCourses = new Array<>();
    }

    public void clearAll() {
        placedCourses.clear();
    }

    public boolean canPlace(int subjectIdx, int day, int startPeriod) {
        if (subjectIdx < 0 || subjectIdx >= SUBJECTS.length) return false;
        if (day < 1 || day > 3) return false;

        int duration = SUBJECTS[subjectIdx].duration;
        int endPeriod = startPeriod + duration - 1;

        boolean validMorning = (startPeriod >= 1 && endPeriod <= 6);
        boolean validAfternoon = (startPeriod >= 7 && endPeriod <= 12);
        if (!validMorning && !validAfternoon) return false;

        String targetName = SUBJECTS[subjectIdx].name;
        for (int i = 0; i < placedCourses.size; i++) {
            CourseSession cs = placedCourses.get(i);
            if (cs.getSubjectName().equals(targetName)) continue;
            if (cs.getDay() == day) {
                boolean overlap = !(endPeriod < cs.getStartPeriod() || cs.getEndPeriod() < startPeriod);
                if (overlap) return false;
            }
        }
        return true;
    }

    public boolean placeSubject(int subjectIdx, int day, int startPeriod) {
        if (!canPlace(subjectIdx, day, startPeriod)) return false;

        SubjectInfo info = SUBJECTS[subjectIdx];
        String loc = randomLoc();
        int room = randomRoom();

        for (int i = placedCourses.size - 1; i >= 0; i--) {
            if (placedCourses.get(i).getSubjectName().equals(info.name)) {
                loc = placedCourses.get(i).getLocation();
                room = placedCourses.get(i).getRoom();
                placedCourses.removeIndex(i);
            }
        }

        String sessionTime = (startPeriod <= 6) ? "Sáng" : "Chiều";
        int endPeriod = startPeriod + info.duration - 1;
        placedCourses.add(new CourseSession(day, sessionTime, info.name, startPeriod, endPeriod, loc, room));
        return true;
    }

    public void removeSubject(int subjectIdx) {
        if (subjectIdx < 0 || subjectIdx >= SUBJECTS.length) return;
        String targetName = SUBJECTS[subjectIdx].name;
        for (int i = placedCourses.size - 1; i >= 0; i--) {
            if (placedCourses.get(i).getSubjectName().equals(targetName)) {
                placedCourses.removeIndex(i);
            }
        }
    }

    public void autoGenerateValidSchedule() {
        clearAll();
        Array<Integer> order = new Array<>();
        for (int i = 0; i < SUBJECTS.length; i++) order.add(i);
        order.shuffle();

        for (int i = 0; i < order.size; i++) {
            int subIdx = order.get(i);
            for (int attempt = 0; attempt < 50; attempt++) {
                int day = MathUtils.random(1, 3);
                int startPeriod = MathUtils.random(1, 12);
                if (placeSubject(subIdx, day, startPeriod)) {
                    break;
                }
            }
        }
    }

    public int getSubjectIndexByName(String name) {
        for (int i = 0; i < SUBJECTS.length; i++) {
            if (SUBJECTS[i].name.equals(name)) return i;
        }
        return -1;
    }

    public boolean isSubjectPlaced(int subjectIdx) {
        String target = SUBJECTS[subjectIdx].name;
        for (int i = 0; i < placedCourses.size; i++) {
            if (placedCourses.get(i).getSubjectName().equals(target)) return true;
        }
        return false;
    }

    public String validateSchedule() {
        if (placedCourses.size == 0) {
            return "Kéo môn học vào lịch!";
        }
        return null;
    }

    private String randomLoc() { return LOCATIONS[MathUtils.random(LOCATIONS.length - 1)]; }
    private int randomRoom() { return ROOM_NUMBERS[MathUtils.random(ROOM_NUMBERS.length - 1)]; }

    public Array<CourseSession> getSessions() { return placedCourses; }
    public boolean isConfirmed() { return isConfirmed; }
    public void setConfirmed(boolean confirmed) { isConfirmed = confirmed; }
}