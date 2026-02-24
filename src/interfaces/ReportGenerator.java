package interfaces;
import User.Patient;
import User.Doctor;
import appointments.Appointment;

public interface ReportGenerator {
    void generateSystemReport(Patient[] patients, int patientCount,
                              Doctor[] doctors, int doctorCount,
                              Appointment[] appointments, int appointmentCount);
}