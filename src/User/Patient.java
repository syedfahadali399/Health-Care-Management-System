package User;
import exceptions.InvalidCredentialsException;
import exceptions.SlotFullException;

public class Patient extends User {

    public String[] medicalHistory;

    public Patient(String name) {
        super(name, 0, "", "");
        this.medicalHistory = new String[10];
    }

    public Patient(String name, String email) {
        super(name, 0, email, "");
        this.medicalHistory = new String[10];
    }

    public  Patient(String name, int id, String email, String password) {
        super(name, id, email, password);
        this.medicalHistory = new String[10];
    }

    public  Patient(String name, int id, String email, String password, String[] medicalHistory) {
        super(name, id, email, password);
        this.medicalHistory = medicalHistory;
    }

    @Override
    public boolean login() throws InvalidCredentialsException {
            System.out.println("✓ Login successful! Welcome " + getName());
            return true;
    }

    public String[] getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String[] medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public void addMedicalHistory(String record) throws SlotFullException {
        System.out.println("\n=== Medical History for " + getName() + " ===");
    }

    public void viewMedicalHistory() {
        System.out.println("\n=== Medical History for " + getName() + " ===");
    }

    public String toFileString() {
        StringBuilder history = new StringBuilder();
        for (int i = 0; i < medicalHistory.length; i++) {
            if (medicalHistory[i] != null) {
                history.append(medicalHistory[i]);
                if (i < medicalHistory.length - 1)
                    history.append("|");
            }
        }
        return getName() + "," + getId() + "," + getEmail() + "," + getPassword() + "," + history.toString();
    }
}