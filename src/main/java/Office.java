import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.random.RandomGenerator;

public class Office {

    private static final double COEFFICIENT = 0.6;
    private static final long SECONDS_IN_HOUR = 2;
    private final List<Employee> employees;

    public Office(List<Employee> employees) {
        this.employees = employees;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void workProcess() {
        int employeesCount = employees.size();
        List<ExecutorService> executors = new ArrayList<>();
        Map<Integer, List<Future<Long>>> tasks = new HashMap<>();
        for (int e = 0; e <= employeesCount; e++) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executors.add(executor);
        }
        for (int h = 0; h < 8; h++) {
            safePrintln(journal() + "| Начало " + (h + 1) + "-го часа");
            int tasksCount = (int) (RandomGenerator.getDefault().nextInt(employeesCount) * COEFFICIENT);
            for (int i = 0; i < tasksCount; i++) {
                int employeeIndex = RandomGenerator.getDefault().nextInt(employeesCount);
                Employee employee = employees.get(employeeIndex);
                long hours = RandomGenerator.getDefault().nextLong(1, 16);
                safePrintln(journal() + "| Сотрудник " + employee.getFio() + " получил задачу на " + hours + " часов");
                Future<Long> future = executors.get(employeeIndex).submit(() -> {
                    safePrintln(journal() + "| Сотрудник " + employee.getFio() + " начал выполнять задачу на " + hours + " часов");
                    long startTime = System.currentTimeMillis();
                    waitHours(hours);
                    if (!Thread.currentThread().isInterrupted()) {
                        safePrintln(journal() + "| Сотрудник " + employee.getFio() + " выполнил задачу на " + hours + " часов");
                    }
                    return (System.currentTimeMillis() - startTime) / 1000 / SECONDS_IN_HOUR;
                });
                if (tasks.get(employeeIndex) == null) {
                    tasks.put(employeeIndex, new ArrayList<>());
                }
                tasks.get(employeeIndex).add(future);
            }
            for (int e = 0; e <= employeesCount; e++) {
                List<Future<Long>> employeeTasks = tasks.get(e);
                if (employeeTasks != null) {
                    for (Future<Long> employeeTask : employeeTasks) {
                        if (employeeTask.isDone()) {
                            long hours = employeeTask.resultNow();
                            employees.get(e).setTimeInHours(employees.get(e).getTimeInHours() + hours);
                        }
                    }
                    employeeTasks.removeIf(Future::isDone);
                }
            }
            waitHours(1);
        }
        safePrintln(journal() + "| Конец рабочего дня");
        for (int e = 0; e <= employeesCount; e++) {
            executors.get(e).shutdownNow();
        }
        waitHours(2);
        for (int e = 0; e <= employeesCount; e++) {
            List<Future<Long>> employeeTasks = tasks.get(e);
            if (employeeTasks != null) {
                for (Future<Long> employeeTask : employeeTasks) {
                    if (employeeTask.isDone()) {
                        long hours = employeeTask.resultNow();
                        employees.get(e).setTimeInHours(employees.get(e).getTimeInHours() + hours);
                        safePrintln("Сотруднику " + employees.get(e).getFio() + " закрыто " + hours + " часов незавершенной задачи");
                    }
                }
            }
        }
    }

    private void waitHours(long hours) {
        try {
            Thread.sleep(Duration.ofSeconds(hours * SECONDS_IN_HOUR));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void safePrintln(String s) {
        synchronized (System.out) {
            System.out.println(s);
        }
    }

    private String journal() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss"));
    }
}
