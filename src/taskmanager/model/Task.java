package taskmanager.model;

import taskmanager.enums.Status;
import taskmanager.enums.TaskType;

import java.util.Objects;

public class Task {
    private Integer id;
    private final TaskType type = TaskType.TASK;
    private String taskName;
    private Status status;
    private String content;


    public Task(String taskName, String content) {
        this.taskName = taskName;
        this.content = content;
        this.status = Status.NEW;
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
        return getId() + "," + getType() + "," + getTaskName() + "," + getStatus() + "," + getContent() + "\n";
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
                ", content='" + content + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(taskName, task.taskName) && Objects.equals(content, task.content) && Objects.equals(id, task.id) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName, content, id, status);
    }
}
