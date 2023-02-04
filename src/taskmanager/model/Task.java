package taskmanager.model;

import taskmanager.enums.Status;
import taskmanager.enums.TaskType;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private Integer id;
    private static final TaskType type = TaskType.TASK;
    private String taskName;
    private Status status = Status.NEW;
    private String content;
    private long duration; // в минутах
    private LocalDateTime startTime;

    public Task(String taskName, String content) {
        this.taskName = taskName;
        this.content = content;
    }

    public Task(String taskName, String content, long duration, LocalDateTime startTime) {
        this.taskName = taskName;
        this.content = content;
        this.duration = duration;
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime == null ? null : startTime.plusMinutes(duration);
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }


    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskType getType() {
        return type;
    }

    public String toStringForFile() {
        return getId() + "," +
                getType() + "," +
                getTaskName() + "," +
                getStatus() + "," +
                getContent() + "," +
                duration + "," +
                startTime + "," +
                getEndTime() + "\n";
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", taskName='" + taskName + '\'' +
                ", content='" + content + '\'' +
                ", status='" + status + '\'' +
                ", duration='" + duration + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + getEndTime() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return duration == task.duration &&
                Objects.equals(id, task.id) &&
                Objects.equals(taskName, task.taskName) &&
                status == task.status && Objects.equals(content, task.content) &&
                Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, taskName, status, content, duration, startTime);
    }
}
