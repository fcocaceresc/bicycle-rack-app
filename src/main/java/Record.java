import java.time.LocalDateTime;

public class Record {
    private final int id;
    private Student student;
    private String bicycleDescription;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;

    public Record(int id, Student student, String bicycleDescription, LocalDateTime checkIn, LocalDateTime checkOut) {
        this.id = id;
        this.student = student;
        this.bicycleDescription = bicycleDescription;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public Record(int id, Student student, String bicycleDescription, LocalDateTime checkIn) {
        this.id = id;
        this.student = student;
        this.bicycleDescription = bicycleDescription;
        this.checkIn = checkIn;
        this.checkOut = null;
    }

    public int getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getBicycleDescription() {
        return bicycleDescription;
    }

    public void setBicycleDescription(String bicycleDescription) {
        this.bicycleDescription = bicycleDescription;
    }

    public LocalDateTime getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDateTime checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDateTime getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDateTime checkOut) {
        this.checkOut = checkOut;
    }
}
