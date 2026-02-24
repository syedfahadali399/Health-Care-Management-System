import java.util.Scanner;
import appointments.Appointment;
import exceptions.*;
import database.FileHandling;
import User.Patient;
import User.Admin;
import User.Doctor;

public class project4 {

    static Appointment[] appointments = new Appointment[1000];
    static int appointmentCount = 0;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        Patient[] patients = new Patient[1000];
        int patientCount = 0;

        Doctor[] doctors = new Doctor[20];
        int doctorCount = 0;

        Admin[] admin = new Admin[1];
        int adminCount = 0;

        // Load data from files
        patientCount = FileHandling.loadPatients(patients);
        doctorCount = FileHandling.loadDoctors(doctors);
        appointmentCount = FileHandling.loadAppointments(appointments);

        // Initialize admin
        admin[0] = new Admin("Syed Fahad Ali", 101, "fahad", "fahad");
        adminCount++;

        boolean running = true;

        while (running) {
            try {
                System.out.println("\n╔════════════════════════════════════════════╗");
                System.out.println("║  SMART HEALTH CARE MANAGEMENT SYSTEM      ║");
                System.out.println("╚════════════════════════════════════════════╝");
                System.out.println("1. Patient Portal");
                System.out.println("2. Doctor Portal");
                System.out.println("3. Admin Portal");
                System.out.println("4. Save & Exit");
                System.out.print("Choose option: ");

                int option = input.nextInt();
                input.nextLine();

                switch (option) {
                    case 1:
                        handlePatientPortal(input, patients, patientCount, doctors, doctorCount);
                        if (patientCount < patients.length - 1) {
                            patientCount = countPatients(patients);
                        }
                        break;

                    case 2:
                        handleDoctorPortal(input, doctors, doctorCount);
                        break;

                    case 3:
                        handleAdminPortal(input, admin, patients, patientCount, doctors, doctorCount);
                        if (doctorCount < doctors.length - 1) {
                            doctorCount = countDoctors(doctors);
                        }
                        break;

                    case 4:
                        // Save all data before exit
                        FileHandling.savePatients(patients, patientCount);
                        FileHandling.saveDoctors(doctors, doctorCount);
                        FileHandling.saveAppointments(appointments, appointmentCount);

                        running = false;
                        System.out.println("\n✓ Data saved successfully!");
                        System.out.println("✓ Thank you for using Smart Health Care System!");
                        break;

                    default:
                        System.out.println("✗ Invalid option! Please try again.");
                }
            } catch (Exception e) {
                System.out.println("✗ Error: " + e.getMessage());
                input.nextLine(); // Clear buffer
            }
        }
        input.close();
    }

    static void handlePatientPortal(Scanner input, Patient[] patients, int patientCount,
                                    Doctor[] doctors, int doctorCount) {
        try {
            System.out.println("\n=== PATIENT PORTAL ===");
            System.out.println("1. Sign up");
            System.out.println("2. Login");
            System.out.print("Choose: ");
            int choice = input.nextInt();
            input.nextLine();

            switch (choice) {
                case 1:
                    signupPatient(patients, patientCount, doctors, countDoctors(doctors));
                    break;

                case 2:
                    loginPatient(input, patients, patientCount, doctors, doctorCount);
                    break;

                default:
                    System.out.println("✗ Invalid choice!");
            }
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
            input.nextLine();
        }
    }

    static void signupPatient(Patient[] patients, int patientCount, Doctor[] doctors, int doctorCount) {
        Scanner input = new Scanner(System.in);

        try {
            System.out.println("\n=== Patient Registration ===");
            System.out.print("Enter your Name: ");
            String name = input.nextLine();

            System.out.print("Enter your email: ");
            String email = input.nextLine();

            // Check for duplicate email
            for (int i = 0; i < patientCount; i++) {
                if (patients[i].getEmail().equalsIgnoreCase(email)) {
                    throw new DuplicateEmailException("Email already registered!");
                }
            }
            for (int i = 0; i < doctorCount; i++) {
                if (doctors[i].getEmail().equalsIgnoreCase(email)) {
                    throw new DuplicateEmailException("Email already registered!");
                }
            }

            System.out.print("Enter your password: ");
            String password = input.nextLine();

            Patient newPatient = new Patient(name, patientCount + 100, email, password);
            patients[patientCount] = newPatient;

            System.out.println("✓ Successfully Registered!");

        } catch (DuplicateEmailException e) {
            System.out.println("✗ " + e.getMessage());
        }
    }

    static void loginPatient(Scanner input, Patient[] patients, int patientCount,
                             Doctor[] doctors, int doctorCount) {
        try {
            Patient loggedInPatient = null;

            for (int i = 0; i < patientCount; i++) {
                try {
                    if (patients[i].login()) {
                        loggedInPatient = patients[i];
                        break;
                    }
                } catch (InvalidCredentialsException e) {
                    System.out.println("✗ " + e.getMessage());
                }
            }

            if (loggedInPatient != null) {
                patientMenu(input, loggedInPatient, doctors, doctorCount);
            }
        } catch (Exception e) {
            System.out.println("✗ Error during login: " + e.getMessage());
        }
    }

    static void patientMenu(Scanner input, Patient patient, Doctor[] doctors, int doctorCount) {
        boolean patientMenu = true;

        while (patientMenu) {
            try {
                System.out.println("\n=== PATIENT MENU ===");
                System.out.println("1. View Doctors");
                System.out.println("2. Book Appointment");
                System.out.println("3. View My Appointments");
                System.out.println("4. View Medical History");
                System.out.println("5. Add Medical History");
                System.out.println("6. Logout");
                System.out.print("Choose: ");

                int option = input.nextInt();
                input.nextLine();

                switch (option) {
                    case 1:
                        viewDoctors(doctors, doctorCount);
                        break;

                    case 2:
                        bookAppointment(patient, doctors, doctorCount, input);
                        break;

                    case 3:
                        viewPatientAppointments(patient);
                        break;

                    case 4:
                        patient.viewMedicalHistory();
                        break;

                    case 5:
                        System.out.print("Enter medical history record: ");
                        String record = input.nextLine();
                        try {
                            patient.addMedicalHistory(record);
                        } catch (SlotFullException e) {
                            System.out.println("✗ " + e.getMessage());
                        }
                        break;

                    case 6:
                        patientMenu = false;
                        System.out.println("✓ Logged out successfully!");
                        break;

                    default:
                        System.out.println("✗ Invalid option!");
                }
            } catch (Exception e) {
                System.out.println("✗ Error: " + e.getMessage());
                input.nextLine();
            }
        }
    }

    static void handleDoctorPortal(Scanner input, Doctor[] doctors, int doctorCount) {
        try {
            System.out.println("\n=== DOCTOR PORTAL ===");
            System.out.println("1. Login");
            System.out.print("Choose: ");
            int choice = input.nextInt();
            input.nextLine();

            if (choice == 1) {
                Doctor loggedInDoctor = null;

                for (int i = 0; i < doctorCount; i++) {
                    try {
                        if (doctors[i].login()) {
                            loggedInDoctor = doctors[i];
                            break;
                        }
                    } catch (InvalidCredentialsException e) {
                        System.out.println("✗ " + e.getMessage());
                    }
                }

                if (loggedInDoctor != null) {
                    doctorMenu(input, loggedInDoctor);
                }
            }
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
            input.nextLine();
        }
    }

    static void doctorMenu(Scanner input, Doctor doctor) {
        boolean doctorMenu = true;

        while (doctorMenu) {
            try {
                System.out.println("\n=== DOCTOR MENU ===");
                System.out.println("1. Set Schedule");
                System.out.println("2. View My Schedule");
                System.out.println("3. View My Appointments");
                System.out.println("4. Logout");
                System.out.print("Choose: ");

                int option = input.nextInt();
                input.nextLine();

                switch (option) {
                    case 1:
                        doctor.updateAvailability();
                        break;

                    case 2:
                        doctor.viewSchedule();
                        break;

                    case 3:
                        doctor.viewAppointments(appointments, appointmentCount);
                        break;

                    case 4:
                        doctorMenu = false;
                        System.out.println("✓ Logged out successfully!");
                        break;

                    default:
                        System.out.println("✗ Invalid option!");
                }
            } catch (Exception e) {
                System.out.println("✗ Error: " + e.getMessage());
                input.nextLine();
            }
        }
    }

    static void handleAdminPortal(Scanner input, Admin[] admin, Patient[] patients,
                                  int patientCount, Doctor[] doctors, int doctorCount) {
        try {
            System.out.println("\n=== ADMIN PORTAL ===");

            try {
                if (admin[0].login()) {
                    adminMenu(input, admin[0], patients, patientCount, doctors, doctorCount);
                }
            } catch (InvalidCredentialsException e) {
                System.out.println("✗ " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
            input.nextLine();
        }
    }

    static void adminMenu(Scanner input, Admin admin, Patient[] patients, int patientCount,
                          Doctor[] doctors, int doctorCount) {
        boolean adminMenu = true;

        while (adminMenu) {
            try {
                System.out.println("\n=== ADMIN MENU ===");
                System.out.println("1. Add Doctor");
                System.out.println("2. View All Doctors");
                System.out.println("3. View All Patients");
                System.out.println("4. View All Appointments");
                System.out.println("5. Generate System Report");
                System.out.println("6. Generate Daily Report");
                System.out.println("7. Logout");
                System.out.print("Choose: ");

                int option = input.nextInt();
                input.nextLine();

                switch (option) {
                    case 1:
                        try {
                            admin.addDoctor(doctors, countDoctors(doctors), patients, patientCount);
                        } catch (DuplicateEmailException e) {
                            System.out.println("✗ " + e.getMessage());
                        }
                        break;

                    case 2:
                        viewAllDoctors(doctors, countDoctors(doctors));
                        break;

                    case 3:
                        viewAllPatients(patients, patientCount);
                        break;

                    case 4:
                        admin.viewAllAppointments(appointments, appointmentCount);
                        break;

                    case 5:
                        admin.generateSystemReport(patients, patientCount,
                                doctors, countDoctors(doctors),
                                appointments, appointmentCount);
                        break;
                    case 6:


                    case 7:
                        adminMenu = false;
                        System.out.println("✓ Logged out successfully!");
                        break;

                    default:
                        System.out.println("✗ Invalid option!");
                }
            } catch (Exception e) {
                System.out.println("✗ Error: " + e.getMessage());
                input.nextLine();
            }
        }
    }

    static void viewDoctors(Doctor[] doctors, int doctorCount) {
        if (doctorCount == 0) {
            System.out.println("\n✗ No doctors available yet.");
        } else {
            System.out.println("\n=== Available Doctors ===");
            for (int i = 0; i < doctorCount; i++) {
                System.out.println((i + 1) + ". Dr. " + doctors[i].getName() +
                        " - " + doctors[i].getSpecialization() +
                        " (ID: " + doctors[i].getId() + ")");
            }
        }
    }

    static void viewAllDoctors(Doctor[] doctors, int doctorCount) {
        if (doctorCount == 0) {
            System.out.println("\n✗ No doctors in system");
        } else {
            System.out.println("\n=== All Doctors ===");
            for (int i = 0; i < doctorCount; i++) {
                System.out.println((i + 1) + ". Dr. " + doctors[i].getName() +
                        " - " + doctors[i].getSpecialization() +
                        " (ID: " + doctors[i].getId() +
                        ", Email: " + doctors[i].getEmail() + ")");
            }
        }
    }

    static void viewAllPatients(Patient[] patients, int patientCount) {
        if (patientCount == 0) {
            System.out.println("\n✗ No patients in system");
        } else {
            System.out.println("\n=== All Patients ===");
            for (int i = 0; i < patientCount; i++) {
                System.out.println((i + 1) + ". " + patients[i].getName() +
                        " (ID: " + patients[i].getId() +
                        ", Email: " + patients[i].getEmail() + ")");
            }
        }
    }

    static void bookAppointment(Patient patient, Doctor[] doctors, int doctorCount, Scanner input) {
        try {
            if (doctorCount == 0) {
                System.out.println("\n✗ No doctors available for booking.");
                return;
            }

            System.out.println("\n=== Book Appointment ===");
            viewDoctors(doctors, doctorCount);

            System.out.print("\nEnter doctor number: ");
            int doctorIndex = input.nextInt() - 1;
            input.nextLine();

            if (doctorIndex < 0 || doctorIndex >= doctorCount) {
                throw new InvalidDateException("Invalid doctor selection!");
            }

            Doctor selectedDoctor = doctors[doctorIndex];

            System.out.println("\nDoctor Schedule:");
            selectedDoctor.viewSchedule();

            System.out.print("\nEnter appointment day (e.g., Monday): ");
            String date = input.nextLine();

            System.out.print("Enter time slot (e.g., 9:00-10:00): ");
            String timeSlot = input.nextLine();

            // Check if slot is available
            if (!selectedDoctor.isAvailable(date, timeSlot)) {
                throw new SlotFullException("Selected time slot is not available!");
            }

            // Check if slot is already booked
            for (int i = 0; i < appointmentCount; i++) {
                if (appointments[i].getDoctorName().equals(selectedDoctor.getName()) &&
                        appointments[i].getDate().equalsIgnoreCase(date) &&
                        appointments[i].getTimeSlot().equals(timeSlot)) {
                    throw new SlotFullException("This time slot is already booked!");
                }
            }

            System.out.print("Enter disease/concern: ");
            String disease = input.nextLine();

            int appointmentId = 1000 + appointmentCount;
            Appointment newAppointment = new Appointment(
                    appointmentId, patient.getName(), selectedDoctor.getName(),
                    date, timeSlot, disease);

            appointments[appointmentCount] = newAppointment;
            appointmentCount++;

            System.out.println("\n✓ Appointment booked successfully!");
            System.out.println("Appointment ID: " + appointmentId);
            System.out.println("Doctor: Dr. " + selectedDoctor.getName());
            System.out.println("Date: " + date);
            System.out.println("Time: " + timeSlot);

        } catch (InvalidDateException | SlotFullException e) {
            System.out.println("✗ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ Error booking appointment: " + e.getMessage());
            input.nextLine();
        }
    }

    static void viewPatientAppointments(Patient patient) {
        System.out.println("\n=== My Appointments ===");
        boolean found = false;
        for (int i = 0; i < appointmentCount; i++) {
            if (appointments[i].getPatientName().equals(patient.getName())) {
                System.out.println("\nAppointment ID: " + appointments[i].getAppointmentId());
                System.out.println("Doctor: Dr. " + appointments[i].getDoctorName());
                System.out.println("Date: " + appointments[i].getDate());
                System.out.println("Time: " + appointments[i].getTimeSlot());
                System.out.println("Disease/Concern: " + appointments[i].getDisease());
                System.out.println("------------------------");
                found = true;
            }
        }
        if (!found) {
            System.out.println("No appointments scheduled yet.");
        }
    }

    static int countPatients(Patient[] patients) {
        int count = 0;
        for (int i = 0; i < patients.length; i++) {
            if (patients[i] != null)
                count++;
        }
        return count;
    }

    static int countDoctors(Doctor[] doctors) {
        int count = 0;
        for (int i = 0; i < doctors.length; i++) {
            if (doctors[i] != null)
                count++;
        }
        return count;
    }
}