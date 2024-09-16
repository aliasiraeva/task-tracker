public class Employee {
    private final String fio;
    private long timeInHours;

    public Employee(String fio) {
        this.fio = fio;
        timeInHours = 0;
    }

    public String getFio() {
        return fio;
    }

    public long getTimeInHours() {
        return timeInHours;
    }

    public void setTimeInHours(long timeInHours) {
        this.timeInHours = timeInHours;
    }

    @Override
    public String toString() {
        return "ФИО сотрудника: " + fio;
    }
}
