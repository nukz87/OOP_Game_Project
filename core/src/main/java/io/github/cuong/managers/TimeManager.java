package io.github.cuong.managers;

import java.util.ArrayList;
import java.util.List;

class Session {
    public int sessionId;
    public float start;
    public float end;

    public Session(int sessionId, float start, float end) {
        this.sessionId = sessionId;
        this.start = start;
        this.end = end;
    }

    public static final Session[] FIXED_SESSIONS = {
            new Session(1, 6.45f, 7.30f),
            new Session(2, 7.30f, 8.15f),
            new Session(3, 8.25f, 9.10f),
            new Session(4, 9.20f, 10.05f),
            new Session(5, 10.15f, 11.0f),
            new Session(6, 11.0f, 11.45f),
            new Session(7, 12.30f, 13.15f),
            new Session(8, 13.15f, 14.00f),
            new Session(9, 14.10f, 14.55f),
            new Session(10, 15.05f, 15.50f),
            new Session(11, 16.0f, 16.45f),
            new Session(12, 16.45f, 17.30f)
    };
}

class ClassRoom {
    public String name;
    public int day;
    public Session session;
    public String location;
    public int room;

    public void ClassRoom(int day, Session session, String location, int room) {
        this.day = day;
        this.session = session;
        this.location = location;
        this.room = room;
    }
}

public class TimeManager {
    public int currentDay = 1;
    public float currentHour = 7.0f;
    private final float DAY_END = 22.0f;

    private final float TIME_SPEED = 0.0333f;

    List<ClassRoom> classArray = new ArrayList<>();
    List<Session> sessionArray = new ArrayList<>();

    public boolean isInClass = false;
    public int missedClass = 0;

    public void update(float delta) {
        currentHour += TIME_SPEED * delta;

        checkClassAttendance();

        if (currentHour > DAY_END) {
            advanceToNextDay();
        }
    }

    private void checkClassAttendance() {
        for (ClassRoom cl : classArray) {
            if (cl.day == currentDay && cl.session.start <= currentHour && cl.session.end >= currentDay && !isInClass) {
                missedClass++;
            }
        }
    }

    private void advanceToNextDay() {
        currentDay++;
        currentHour = 7.0f;
        isInClass = false;
    }

    public String getFormattedTime() {
        int hours = (int) currentHour;
        int minutes = (int) ((currentHour - hours) * 60);
        return String.format("%02d:%02d", hours, minutes);
    }

}
