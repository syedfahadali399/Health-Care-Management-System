package User;
import exceptions.InvalidCredentialsException;
import exceptions.InvalidDateException;
import appointments.Appointment;

public class Doctor extends User {
    private String specialization;
    private String[][] schedule = new String[7][5];
    final String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };

    public Doctor(String name) {
        super(name, 0, "", "");
    }

    public Doctor(String name, String specialization) {
        super(name, 0, "", "");
        this.specialization = specialization;
    }

    public Doctor(String name, int id, String email, String password) {
        super(name, id, email, password);
    }

    public Doctor(String name, int id, String email, String password, String specialization) {
        super(name, id, email, password);
        this.specialization = specialization;
    }

    @Override
    public boolean login() throws InvalidCredentialsException {
            System.out.println("✓ Login successful! Welcome Dr. " + getName());
            return true;

    }

    public String getSpecialization() {
        return specialization;
    }

    public String[][] getSchedule() {
        return schedule;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void updateAvailability() {
        System.out.println("✓ Schedule updated successfully!");
    }

    public void viewSchedule() {
        System.out.println("\n=== Schedule for Dr. " + getName() + " ===");
    }

    public boolean isAvailable(String day, String timeSlot) throws InvalidDateException {
        for (int i = 0; i < days.length; i++) {
            if (days[i].equalsIgnoreCase(day)) {
                for (int j = 0; j < 5; j++) {
                    if (schedule[i][j] != null && schedule[i][j].equals(timeSlot)) {
                        return true;
                    }
                }
                return false;
            }
        }
        throw new InvalidDateException("Invalid day: " + day);
    }

    public void viewAppointments(Appointment[] appointments, int appointmentCount) {
        System.out.println("\n=== Appointments for Dr. " + getName() + " ===");
    }

    public String toFileString() {
        return getName() + "," + getId() + "," + getEmail() + "," + getPassword() + "," + specialization;
    }
}