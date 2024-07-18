package academy.mischok.modules.exception;

public class ProjectNotFoundException extends Exception {
    public ProjectNotFoundException(Long projectId) {
        super(String.format("Project with id %d not found", projectId));
    }
}