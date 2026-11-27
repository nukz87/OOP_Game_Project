package io.github.cuong.schedule.model;

public class CourseSession {
    private int day;
    private String sessionTime;
    private String subjectName;
    private int startPeriod;
    private int endPeriod;
    private String location;
    private int room;

    public CourseSession(int day, String sessionTime, String subjectName,
                         int startPeriod, int endPeriod, String location, int room) {
        this.day = day;
        this.sessionTime = sessionTime;
        this.subjectName = subjectName;
        this.startPeriod = startPeriod;
        this.endPeriod = endPeriod;
        this.location = location;
        this.room = room;
    }

    public int getDay() { return day; }
    public String getSessionTime() { return sessionTime; }
    public String getSubjectName() { return subjectName; }
    public int getStartPeriod() { return startPeriod; }
    public int getEndPeriod() { return endPeriod; }
    public String getLocation() { return location; }
    public int getRoom() { return room; }

    public void setPeriods(int startPeriod, int endPeriod) {
        this.startPeriod = startPeriod;
        this.endPeriod = endPeriod;
    }

    public String getRoomText() {
        return location + "-" + room;
    }

    public String getPeriodText() {
        return "Tiết " + startPeriod + " - " + endPeriod + " (" + (endPeriod - startPeriod + 1) + " tiết)";
    }
}