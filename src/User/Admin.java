package User;
import exceptions.InvalidCredentialsException;
import appointments.Appointment;
import interfaces.ReportGenerator;
import exceptions.DuplicateEmailException;

public class Admin extends User implements ReportGenerator {

    public Admin(String name, int id, String email, String password) {
        super(name, id, email, password);
    }

    @Override
    public boolean login() throws InvalidCredentialsException {
        System.out.println("Successfully Login: " + getName());
        return true;
    }

    public void addDoctor(Doctor[] doctors, int doctorCount, Patient[] patients, int patientCount) throws DuplicateEmailException {
        System.out.println("✓ Doctor added successfully!");
    }

    public void viewAllAppointments(Appointment[] appointments, int appointmentCount) {
        System.out.println("\n=== All Appointments in System ===");
        if (appointmentCount == 0) {
            System.out.println("No appointments in the system.");
        }
    }

    @Override
    public void generateSystemReport(Patient[] patients, int patientCount, Doctor[] doctors, int doctorCount, Appointment[] appointments, int appointmentCount) {

    }
}