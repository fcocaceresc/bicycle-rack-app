import java.time.LocalDateTime;
import java.util.ArrayList;

public class BicycleRackService {
    private final ArrayList<Record> records = new ArrayList<>();

    /**
     * Creates a new record and stores it in the records list.
     * @param student the student who is checking in the bicycle
     * @param bicycleDescription the description of the bicycle
     * @return the created record
     * @throws IllegalArgumentException if the student id, name or bicycle description are empty
     * @throws IllegalStateException if the student has a not checked out record
     */
    public Record checkIn(Student student, String bicycleDescription) {
        validateCheckIn(student, bicycleDescription);
        Record record = new Record(records.size() + 1, student, bicycleDescription, LocalDateTime.now());
        records.add(record);
        return record;
    }

    /**
     * Validates the inputs for the check-in.
     * @param student the student to validate
     * @param bicycleDescription the description of the bicycle to validate
     * @throws IllegalArgumentException if the student id, name or bicycle description are empty
     * @throws IllegalStateException if the student has a not checked out record
     */
    private void validateCheckIn(Student student, String bicycleDescription) {
        if (student.getId() == null || student.getId().isEmpty()) {
            throw new IllegalArgumentException("Student ID can't be empty");
        }
        if (student.getName() == null || student.getName().isEmpty()) {
            throw new IllegalArgumentException("Student name can't be empty");
        }
        if (bicycleDescription == null || bicycleDescription.isEmpty()) {
            throw new IllegalArgumentException("Bicycle description can't be empty");
        }
        if (hasNotCheckedOutRecord(student.getId())) {
            throw new IllegalStateException("The student has a not checked out record");
        }
    }

    /**
     * Checks if the student has a not checked out record by iterating through the records list, searching for a record with the given student id and a null check-out time.
     * @param studentId the id of the student
     * @return true if the student has a not checked out record, false otherwise
     */
    private boolean hasNotCheckedOutRecord(String studentId) {
        for (Record record : records) {
            if (record.getStudent().getId().equals(studentId) && record.getCheckOut() == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks out a record by its id by setting the check-out time to the current time.
     * @param recordId the id of the record to check out
     * @throws IllegalArgumentException if the record is not found
     * @throws IllegalStateException if the record is already checked out
     */
    public void checkOutByRecordId(int recordId) {
        Record recordToCheckOut = getRecordById(recordId);
        validateCheckOutByRecordId(recordToCheckOut);
        recordToCheckOut.setCheckOut(LocalDateTime.now());
    }

    /**
     * Gets a record by its id by iterating through the records list, searching for a record with the given id.
     * @param recordId the id of the record to get
     * @return the record with the given id, or null if not found
     */
    private Record getRecordById(int recordId) {
        for (Record record : records) {
            if (record.getId() == recordId) {
                return record;
            }
        }
        return null;
    }

    /**
     * Validates if a record can be checked out.
     * @param recordToCheckOut the record to validate for check-out
     * @throws IllegalArgumentException if the record is not found
     * @throws IllegalStateException if the record is already checked out
     */
    private void validateCheckOutByRecordId(Record recordToCheckOut) {
        if (recordToCheckOut == null) {
            throw new IllegalArgumentException("Record not found");
        }
        if (recordToCheckOut.getCheckOut() != null) {
            throw new IllegalStateException("Record is already checked out");
        }
    }

    /**
     * Checks out a record by the student id by setting the check-out time to the current time.
     * @param studentId the id of the student
     * @throws IllegalStateException if the record is already checked out
     */
    public void checkOutByStudentId(String studentId) {
        Record recordToCheckOut = getNotCheckedOutRecordForStudent(studentId);
        validateCheckOutByStudentId(recordToCheckOut);
        recordToCheckOut.setCheckOut(LocalDateTime.now());
    }

    /**
     * Gets a not checked out record for a student by iterating through the records list, searching for a record with the given student id and a null check-out time.
     * @param studentId the id of the student
     * @return the not checked out record for the student, or null if there is no such record
     */
    private Record getNotCheckedOutRecordForStudent(String studentId) {
        for (Record record : records) {
            if (record.getStudent().getId().equals(studentId) && record.getCheckOut() == null) {
                return record;
            }
        }
        return null;
    }

    /**
     * Validates if a record can be checked out by student id.
     * @param recordToCheckOut the record to check out
     * @throws IllegalStateException if the record is already checked out
     */
    private void validateCheckOutByStudentId(Record recordToCheckOut) {
        if (recordToCheckOut == null) {
            throw new IllegalStateException("The student is already checked out");
        }
    }

    /**
     * Gets all records.
     * @return a list of all records
     */
    public ArrayList<Record> getRecords() {
        return new ArrayList<>(records);
    }

    /**
     * Updates the student id of a record by its id.
     * @param recordId the id of the record to update
     * @param newStudentId the new student id
     * @throws IllegalArgumentException if the record is not found, the new value is empty or if the new value is the same as the current one
     */
    public void updateStudentId(int recordId, String newStudentId) {
        Record record = getRecordById(recordId);
        validateUpdate(record, newStudentId, "student ID");
        record.getStudent().setId(newStudentId);
    }

    /**
     * Updates the student name of a record by its id.
     * @param recordId the id of the record to update
     * @param newStudentName the new student name
     * @throws IllegalArgumentException if the record is not found, the new value is empty or if the new value is the same as the current one
     */
    public void updateStudentName(int recordId, String newStudentName) {
        Record record = getRecordById(recordId);
        validateUpdate(record, newStudentName, "student name");
        record.getStudent().setName(newStudentName);
    }

    /**
     * Updates the bicycle description of a record by its id.
     * @param recordId the id of the record to update
     * @param newBicycleDescription the new bicycle description
     * @throws IllegalArgumentException if the record is not found, the new value is empty or if the new value is the same as the current one
     */
    public void updateBicycleDescription(int recordId, String newBicycleDescription) {
        Record record = getRecordById(recordId);
        validateUpdate(record, newBicycleDescription, "bicycle description");
        record.setBicycleDescription(newBicycleDescription);
    }

    /**
     * Validates the inputs for the update.
     * @param record the record to validate
     * @param updateValue the new value to validate
     * @param updateType the type of value to validate (student ID, student name or bicycle description)
     * @throws IllegalArgumentException if the record is not found, the new value is empty or if the new value is the same as the current one
     */
    private void validateUpdate(Record record, String updateValue, String updateType) {
        if (record == null) {
            throw new IllegalArgumentException("Record not found");
        }
        if (updateValue == null || updateValue.isEmpty()) {
            throw new IllegalArgumentException("New " + updateType + " can't be empty");
        }

        String currentValue = getCurrentValue(record, updateType);
        if (currentValue.equals(updateValue)) {
            throw new IllegalArgumentException("New " + updateType + " is the same as the current one");
        }
    }

    /**
     * Gets the current value of the specified field of the record.
     * @param record the record to get the current value from
     * @param updateType the type of value to get (student ID, student name or bicycle description)
     * @return the current value of the record based on the update type
     */
    private String getCurrentValue(Record record, String updateType) {
        switch (updateType) {
            case "student ID":
                return record.getStudent().getId();
            case "student name":
                return record.getStudent().getName();
            case "bicycle description":
                return record.getBicycleDescription();
        }
        return updateType;
    }

}
