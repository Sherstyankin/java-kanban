package taskmanager.model;

import taskmanager.enums.TaskType;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private final Integer epicId;
    private static final TaskType type = TaskType.SUBTASK;

    public Subtask(String taskName, String content, long duration, LocalDateTime startTime, Integer epicId) {
        super(taskName, content, duration, startTime);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return type;
    }

    @Override
    public String toStringForFile() {
        return getId() + "," +
                getType() + "," +
                getTaskName() + "," +
                getStatus() + "," +
                getContent() + "," +
                getDuration() + "," +
                getStartTime() + "," +
                getEndTime() + "," +
                getEpicId() + "\n";
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id='" + getId() + '\'' +
                ", taskName='" + getTaskName() + '\'' +
                ", content='" + getContent() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", duration='" + getDuration() + '\'' +
                ", startTime='" + getStartTime() + '\'' +
                ", endTime='" + getEndTime() + '\'' +
                ", epicId='" + getEpicId() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(epicId, subtask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
