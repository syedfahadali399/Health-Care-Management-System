package database;
import java.io.*;
import User.Patient;
import User.Doctor;
import appointments.Appointment;

public class FileHandling {

    public static void savePatients(Patient[] patients, int count) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("patients.txt"))) {
            for (int i = 0; i < count; i++) {
                writer.println(patients[i].toFileString());
            }
            System.out.println("✓ Patient data saved successfully!");
        } catch (IOException e) {
            System.out.println("✗ Error saving patient data: " + e.getMessage());
        }
    }

    public static int loadPatients(Patient[] patients) {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("patients.txt"))) {
            String line;
            while ((line = reader.readLine()) != null && count < patients.length) {
                String[] parts = line.split(",", 5);
                if (parts.length >= 4) {
                    String[] history = null;
                    if (parts.length == 5 && !parts[4].isEmpty()) {
                        history = parts[4].split("\\|");
                    }
                    patients[count] = new Patient(parts[0], Integer.parseInt(parts[1]),
                            parts[2], parts[3], history);
                    count++;
                }
            }
            System.out.println("✓ Loaded " + count + " patients from file.");
        } catch (FileNotFoundException e) {
            System.out.println("No previous patient data found. Starting fresh.");
        } catch (IOException e) {
            System.out.println("✗ Error loading patient data: " + e.getMessage());
        }
        return count;
    }

    public static void saveDoctors(Doctor[] doctors, int count) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("doctors.txt"))) {
            for (int i = 0; i < count; i++) {
                writer.println(doctors[i].toFileString());
            }
            System.out.println("✓ Doctor data saved successfully!");
        } catch (IOException e) {
            System.out.println("✗ Error saving doctor data: " + e.getMessage());
        }
    }

    public static int loadDoctors(Doctor[] doctors) {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("doctors.txt"))) {
            String line;
            while ((line = reader.readLine()) != null && count < doctors.length) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    doctors[count] = new Doctor(parts[0], Integer.parseInt(parts[1]),
                            parts[2], parts[3], parts[4]);
                    count++;
                }
            }
            System.out.println("✓ Loaded " + count + " doctors from file.");
        } catch (FileNotFoundException e) {
            System.out.println("No previous doctor data found. Starting fresh.");
        } catch (IOException e) {
            System.out.println("✗ Error loading doctor data: " + e.getMessage());
        }
        return count;
    }

    public static void saveAppointments(Appointment[] appointments, int count) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("appointments.txt"))) {
            for (int i = 0; i < count; i++) {
                writer.println(appointments[i].toFileString());
            }
            System.out.println("✓ Appointment data saved successfully!");
        } catch (IOException e) {
            System.out.println("✗ Error saving appointment data: " + e.getMessage());
        }
    }

    public static int loadAppointments(Appointment[] appointments) {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("appointments.txt"))) {
            String line;
            while ((line = reader.readLine()) != null && count < appointments.length) {
                String[] parts = line.split(",", 6);
                if (parts.length >= 6) {
                    appointments[count] = new Appointment(
                            Integer.parseInt(parts[0]), parts[1], parts[2],
                            parts[3], parts[4], parts[5]);
                    count++;
                }
            }
            System.out.println("✓ Loaded " + count + " appointments from file.");
        } catch (FileNotFoundException e) {
            System.out.println("No previous appointment data found. Starting fresh.");
        } catch (IOException e) {
            System.out.println("✗ Error loading appointment data: " + e.getMessage());
        }
        return count;
    }
}