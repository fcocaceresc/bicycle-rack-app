import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BicycleRackDaoTest {
    private BicycleRackDao dao;

    @BeforeEach
    void setUp() {
        dao = new BicycleRackDao("jdbc:sqlite:test.db");
    }

    @AfterEach
    void tearDown() {
        dao.dropRecordsTable();
    }

    @Test
    void createRecord() {
        dao.createRecord("4", "amadeus", "oxford");
        ArrayList<Record> records = dao.getRecords();
        assertEquals(1, records.size());
        Record record = records.getFirst();
        Student student = record.getStudent();
        assertEquals(1, record.getId());
        assertEquals("4", student.getId());
        assertEquals("amadeus", student.getName());
        assertEquals("oxford", record.getBicycleDescription());
        assertNotNull(record.getCheckIn());
        assertNull(record.getCheckOut());
    }

    @Test
    void createRecordNullStudentId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            dao.createRecord(null, "amadeus", "oxford");
        });
        assertEquals("The student id can't be null or empty", exception.getMessage());
    }

    @Test
    void createRecordEmptyStudentId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            dao.createRecord("", "amadeus", "oxford");
        });
        assertEquals("The student id can't be null or empty", exception.getMessage());
    }

    @Test
    void createRecordNullStudentName() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            dao.createRecord("4", null, "oxford");
        });
        assertEquals("The student name can't be null or empty", exception.getMessage());
    }

    @Test
    void createRecordEmptyStudentName() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            dao.createRecord("4", "", "oxford");
        });
        assertEquals("The student name can't be null or empty", exception.getMessage());
    }

    @Test
    void createRecordNullBicycleDescription() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            dao.createRecord("4", "amadeus", null);
        });
        assertEquals("The bicycle description can't be null or empty", exception.getMessage());
    }

    @Test
    void createRecordEmptyBicycleDescription() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            dao.createRecord("4", "amadeus", "");
        });
        assertEquals("The bicycle description can't be null or empty", exception.getMessage());
    }

    @Test
    void createRecordStudentIdExceedsMaxLength() {
        String string = "a";
        int studentIdMaxLength = ConfigReader.getIntValue("record.student.id.maxLength");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            dao.createRecord(string.repeat(studentIdMaxLength + 1), "amadeus", "oxford");
        });
        assertEquals("The student id exceeds the maximum length of " + studentIdMaxLength + " characters", exception.getMessage());
    }

    @Test
    void createRecordStudentNameExceedsMaxLength() {
        String string = "a";
        int studentNameMaxLength = ConfigReader.getIntValue("record.student.name.maxLength");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            dao.createRecord("4", string.repeat(studentNameMaxLength + 1), "oxford");
        });
        assertEquals("The student name exceeds the maximum length of " + studentNameMaxLength + " characters", exception.getMessage());
    }

    @Test
    void createRecordBicycleDescriptionExceedsMaxLength() {
        String string = "a";
        int bicycleDescriptionMaxLength = ConfigReader.getIntValue("record.bicycleDescription.maxLength");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            dao.createRecord("4", "amadeus", string.repeat(bicycleDescriptionMaxLength + 1));
        });
        assertEquals("The bicycle description exceeds the maximum length of " + bicycleDescriptionMaxLength + " characters", exception.getMessage());
    }

    @Test
    void createRecordStudentHasNotCheckedOutRecord() {
        dao.createRecord("4", "amadeus", "oxford");
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            dao.createRecord("4", "amadeus", "oxford");
        });
        assertEquals("The student has a not checked out record", exception.getMessage());
    }

    @Test
    void createRecordExceedsCapacity() {
        int bicycleRackCapacity = ConfigReader.getIntValue("bicycleRack.capacity");
        for (int i = 0; i < bicycleRackCapacity; i++) {
            dao.createRecord(String.valueOf(i), "amadeus", "oxford");
        }
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            dao.createRecord(String.valueOf(bicycleRackCapacity), "amadeus", "oxford");
        });
        assertEquals("The bicycle rack is full", exception.getMessage());
    }
}