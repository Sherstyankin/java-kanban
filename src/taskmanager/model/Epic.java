package taskmanager.model;

import taskmanager.enums.TaskType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Subtask> subtasksList;
    private static final TaskType type = TaskType.EPIC;
    private long duration; // в минутах
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Epic(String taskName, String content, List<Subtask> subtasksList) {
        super(taskName, content);
        this.subtasksList = subtasksList;
    }

    @Override
    public long getDuration() {
        this.duration = 0;
        for (Subtask subtask : subtasksList) {
            this.duration += subtask.getDuration();
        }
        return this.duration;
    }

    @Override
    public LocalDateTime getStartTime() {
        if (!subtasksList.isEmpty()) {
            this.startTime = subtasksList.stream()
                    .map(Task::getStartTime)
                    .filter(Objects::nonNull)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);
        } else {
            this.startTime = null;
        }
        return this.startTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        if (!subtasksList.isEmpty()) {
            this.endTime = subtasksList.stream()
                    .map(Task::getEndTime)
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
        } else {
            this.endTime = null;
        }
        return this.endTime;
    }

    public List<Subtask> getSubtasksList() {
        return subtasksList;
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
                getEndTime() + "\n";
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id='" + getId() + '\'' +
                ", taskName='" + getTaskName() + '\'' +
                ", content='" + getContent() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", duration='" + getDuration() + '\'' +
                ", startTime='" + getStartTime() + '\'' +
                ", endTime='" + getEndTime() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return duration == epic.duration &&
                Objects.equals(subtasksList, epic.subtasksList) &&
                Objects.equals(startTime, epic.startTime) &&
                Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksList, type, duration, startTime, endTime);
    }
}

