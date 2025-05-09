import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class BicycleRackDao {
    String databaseUrl;

    public BicycleRackDao(String databaseUrl) {
        this.databaseUrl = databaseUrl;
        this.createRecordsTable();
    }

    public void createRecordsTable() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DriverManager.getConnection(this.databaseUrl);
            String query = "CREATE TABLE IF NOT EXISTS records(" +
                    "id INTEGER PRIMARY KEY," +
                    "studentId TEXT NOT NULL," +
                    "studentName TEXT NOT NULL," +
                    "bicycleDescription TEXT NOT NULL," +
                    "checkIn TEXT NOT NULL," +
                    "checkOut TEXT" +
                    ")";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void dropRecordsTable() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DriverManager.getConnection(this.databaseUrl);
            String query = "DROP TABLE IF EXISTS records";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void createRecord(String studentId, String studentName, String bicycleDescription) {
        validateCreateRecordInput(studentId, studentName, bicycleDescription);
        validateStudentHasANotCheckedOutRecord(studentId);
        validateBicycleRackCapacity();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DriverManager.getConnection(this.databaseUrl);
            String query = "INSERT INTO records (studentId, studentName, bicycleDescription, checkIn) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, studentId.trim());
            preparedStatement.setString(2, studentName.trim());
            preparedStatement.setString(3, bicycleDescription.trim());
            preparedStatement.setString(4, String.valueOf(LocalDateTime.now()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void validateCreateRecordInput(String studentId, String studentName, String bicycleDescription) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("The student id can't be null or empty");
        }
        if (studentName == null || studentName.trim().isEmpty()) {
            throw new IllegalArgumentException("The student name can't be null or empty");
        }
        if (bicycleDescription == null || bicycleDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("The bicycle description can't be null or empty");
        }
        int studentIdMaxLength = ConfigReader.getIntValue("record.student.id.maxLength");
        if (studentId.length() > studentIdMaxLength) {
            throw new IllegalArgumentException("The student id exceeds the maximum length of " + studentIdMaxLength + " characters");
        }
        int studentNameMaxLength = ConfigReader.getIntValue("record.student.name.maxLength");
        if (studentName.length() > studentNameMaxLength) {
            throw new IllegalArgumentException("The student name exceeds the maximum length of " + studentNameMaxLength + " characters");
        }
        int bicycleDescriptionMaxLength = ConfigReader.getIntValue("record.bicycleDescription.maxLength");
        if (bicycleDescription.length() > bicycleDescriptionMaxLength) {
            throw new IllegalArgumentException("The bicycle description exceeds the maximum length of " + bicycleDescriptionMaxLength + " characters");
        }
    }

    private void validateStudentHasANotCheckedOutRecord(String studentId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DriverManager.getConnection(this.databaseUrl);
            String query = "SELECT * FROM records WHERE studentId = ? AND checkOut IS NULL";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, studentId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                throw new IllegalStateException("The student has a not checked out record");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void validateBicycleRackCapacity() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DriverManager.getConnection(this.databaseUrl);
            String query = "SELECT COUNT(*) FROM records WHERE checkOut IS NULL";
            preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                int maxCapacity = ConfigReader.getIntValue("bicycleRack.capacity");
                if (count >= maxCapacity) {
                    throw new IllegalStateException("The bicycle rack is full");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ArrayList<Record> getRecords() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<Record> records = new ArrayList<>();
        try {
            connection = DriverManager.getConnection(this.databaseUrl);
            String query = "SELECT * FROM records";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Record record = mapResultSetToRecord(resultSet);
                records.add(record);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return records;
    }

    private Record mapResultSetToRecord(ResultSet resultSet) {
        Record record = null;
        try {
            Student student = new Student(
                    resultSet.getString("studentId"),
                    resultSet.getString("studentName")
            );
            record = new Record(
                    resultSet.getInt("id"),
                    student,
                    resultSet.getString("bicycleDescription"),
                    LocalDateTime.parse(resultSet.getString("checkIn"))
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return record;
    }
}
