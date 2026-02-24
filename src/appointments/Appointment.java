package appointments;

public class Appointment {
    private int appointmentId;
    private String patientName;
    private String doctorName;
    private String date;
    private String timeSlot;
    private String disease;

    public Appointment(int appointmentId, String patientName, String doctorName,
                String date, String timeSlot, String disease) {
        this.appointmentId = appointmentId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.date = date;
        this.timeSlot = timeSlot;
        this.disease = disease;
    }

    public int getAppointmentId() {
        return appointmentId;
    }
    public String getPatientName() {
        return patientName;
    }
    public String getDoctorName() {
        return doctorName;
    }
    public String getDate() {
        return date;
    }
    public String getTimeSlot() {
        return timeSlot;
    }
    public String getDisease() {
        return disease;
    }

    public String toFileString() {
        return appointmentId + "," + patientName + "," + doctorName + "," +
                date + "," + timeSlot + "," + disease;
    }
}