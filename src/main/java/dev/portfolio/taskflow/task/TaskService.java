package dev.portfolio.taskflow.task;

import dev.portfolio.taskflow.common.NotFoundException;
import dev.portfolio.taskflow.common.PageResponse;
import dev.portfolio.taskflow.user.UserAccount;
import dev.portfolio.taskflow.user.UserAccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TaskService {
    private final TaskRepository tasks;
    private final UserAccountRepository users;

    public TaskService(TaskRepository tasks, UserAccountRepository users) {
        this.tasks = tasks;
        this.users = users;
    }

    public PageResponse<TaskDtos.Response> findAll(String email, TaskStatus status, Pageable pageable) {
        UserAccount owner = requireUser(email);
        Page<Task> page = status == null
                ? tasks.findAllByOwnerId(owner.getId(), pageable)
                : tasks.findAllByOwnerIdAndStatus(owner.getId(), status, pageable);
        return PageResponse.from(page, TaskDtos.Response::from);
    }

    public TaskDtos.Response findOne(String email, Long id) {
        return TaskDtos.Response.from(requireOwnedTask(email, id));
    }

    @Transactional
    public TaskDtos.Response create(String email, TaskDtos.CreateRequest request) {
        Task task = new Task(request.title().trim(), clean(request.description()), request.dueDate(), requireUser(email));
        return TaskDtos.Response.from(tasks.save(task));
    }

    @Transactional
    public TaskDtos.Response update(String email, Long id, TaskDtos.UpdateRequest request) {
        Task task = requireOwnedTask(email, id);
        task.update(request.title().trim(), clean(request.description()), request.status(), request.dueDate());
        return TaskDtos.Response.from(task);
    }

    @Transactional
    public void delete(String email, Long id) {
        tasks.delete(requireOwnedTask(email, id));
    }

    private Task requireOwnedTask(String email, Long id) {
        UserAccount owner = requireUser(email);
        return tasks.findByIdAndOwnerId(id, owner.getId())
                .orElseThrow(() -> new NotFoundException("Task not found"));
    }

    private UserAccount requireUser(String email) {
        return users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
