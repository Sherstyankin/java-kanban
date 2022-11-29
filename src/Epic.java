import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasksArrayList;

    public Epic(String taskName, String content, String status, ArrayList<Subtask> subtasksArrayList) {
        super(taskName, content, status);
        this.subtasksArrayList = subtasksArrayList;
    }

    public ArrayList<Subtask> getSubtasksArrayList() {
        return subtasksArrayList;
    }
    @Override
    public String toString() {
        return "Epic{" +
                "taskName='" + getTaskName() + '\'' +
                ", content='" + getContent() + '\'' +
                ", id=" + getId() +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}

