package ui;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import User.Patient;
import User.Doctor;
import User.Admin;
import appointments.Appointment;
import database.FileHandling;
import exceptions.InvalidCredentialsException;

public class LoginPage extends Application {

    private static Patient[] patients = new Patient[1000];
    private static Doctor[] doctors = new Doctor[20];
    private static Admin[] admins = new Admin[1];
    private static Appointment[] appointments = new Appointment[1000];
    private static int patientCount = 0;
    private static int doctorCount = 0;
    private static int adminCount = 0;
    private static int appointmentCount = 0;
    private static boolean dataLoaded = false;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        if (!dataLoaded) {
            loadAllData();
            dataLoaded = true;
        }

        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setSpacing(20);
        root.setStyle("-fx-background-color: #f8fafc;");

        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);

        Label title = new Label("Smart HealthCare");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#2563eb")); // Blue-600

        Label subtitle = new Label("Please sign in to your account");
        subtitle.setTextFill(Color.web("#64748b"));

        header.getChildren().addAll(title, subtitle);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setMaxWidth(350);
        grid.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        Label emailLabel = new Label("Email Address");
        emailLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        TextField emailField = new TextField();
        emailField.setPromptText("name@example.com");
        emailField.setPrefHeight(40);
        emailField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e2e8f0;");

        Label passLabel = new Label("Password");
        passLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        PasswordField passField = new PasswordField();
        passField.setPromptText("••••••••");
        passField.setPrefHeight(40);
        passField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e2e8f0;");

        Label roleLabel = new Label("Select Role");
        roleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        ComboBox<String> roleDropdown = new ComboBox<>();
        roleDropdown.getItems().addAll("Patient", "Doctor", "Admin");
        roleDropdown.setValue("Patient");
        roleDropdown.setPrefHeight(40);
        roleDropdown.setPrefWidth(350);
        roleDropdown.setStyle("-fx-background-radius: 8; -fx-background-color: white; -fx-border-radius: 8; -fx-border-color: #e2e8f0;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(330);

        Button loginBtn = new Button("Sign In");
        loginBtn.setPrefWidth(350);
        loginBtn.setPrefHeight(45);
        loginBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 10; -fx-cursor: hand;");

        grid.add(emailLabel, 0, 0);
        grid.add(emailField, 0, 1);
        grid.add(passLabel, 0, 2);
        grid.add(passField, 0, 3);
        grid.add(roleLabel, 0, 4);
        grid.add(roleDropdown, 0, 5);
        grid.add(errorLabel, 0, 6);
        grid.add(loginBtn, 0, 7);

        loginBtn.setOnAction(e -> {
            handleLogin(emailField, passField, roleDropdown, errorLabel);
        });

        passField.setOnAction(e -> {
            handleLogin(emailField, passField, roleDropdown, errorLabel);
        });

        HBox footer = new HBox(5);
        footer.setAlignment(Pos.CENTER);

        Label noAccountLabel = new Label("Don't have an account?");
        noAccountLabel.setStyle("-fx-text-fill: #64748b;");

        Hyperlink registerLink = new Hyperlink("Sign Up");
        registerLink.setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold;");
        registerLink.setOnAction(e -> goToRegistrationPage());

        footer.getChildren().addAll(noAccountLabel, registerLink);
        root.getChildren().addAll(header, grid, footer);

        Scene scene = new Scene(root, 450, 600);
        primaryStage.setTitle("Smart HealthCare - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleLogin(TextField emailField, PasswordField passField,
                             ComboBox<String> roleDropdown, Label errorLabel) {
        errorLabel.setText("");

        String email = emailField.getText().trim();
        String password = passField.getText();
        String selectedRole = roleDropdown.getValue();

        try {
            if (email.isEmpty() || password.isEmpty()) {
                throw new IllegalArgumentException("Please fill in all fields!");
            }

//            if (!email.contains("@")) {
//                throw new IllegalArgumentException("Please enter a valid email address!");
//            }

            // Authenticate based on role
            switch (selectedRole) {
                case "Patient":
                    Patient patient = authenticatePatient(email, password);
                    if (patient != null) {
                        openPatientDashboard(patient);
                    } else {
                        throw new InvalidCredentialsException("Invalid email or password!");
                    }
                    break;

                case "Doctor":
                    Doctor doctor = authenticateDoctor(email, password);
                    if (doctor != null) {
                        openDoctorDashboard(doctor);
                    } else {
                        throw new InvalidCredentialsException("Invalid email or password!");
                    }
                    break;

                case "Admin":
                    Admin admin = authenticateAdmin(email, password);
                    if (admin != null) {
                        openAdminDashboard(admin);
                    } else {
                        throw new InvalidCredentialsException("Invalid admin credentials!");
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Please select a role!");
            }

        } catch (InvalidCredentialsException | IllegalArgumentException e) {
            errorLabel.setText("✗ " + e.getMessage());
        } catch (Exception e) {
            errorLabel.setText("✗ Login failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Patient authenticatePatient(String email, String password) {
        for (int i = 0; i < patientCount; i++) {
            if (patients[i].getEmail().equalsIgnoreCase(email) &&
                    patients[i].getPassword().equals(password)) {
                return patients[i];
            }
        }
        return null;
    }

    private Doctor authenticateDoctor(String email, String password) {
        for (int i = 0; i < doctorCount; i++) {
            if (doctors[i].getEmail().equalsIgnoreCase(email) &&
                    doctors[i].getPassword().equals(password)) {
                return doctors[i];
            }
        }
        return null;
    }

    private Admin authenticateAdmin(String email, String password) {
        if (admins[0].getEmail().equals(email) &&
                admins[0].getPassword().equals(password)) {
            return admins[0];
        }
        return null;
    }

    private void openPatientDashboard(Patient patient) {
        System.out.println("✓ Patient login successful: " + patient.getName());

        PatientDashboard dashboard = new PatientDashboard();
        dashboard.setPatientData(patient, doctors, doctorCount, appointments, appointmentCount);
        dashboard.start(primaryStage);
    }

    private void openDoctorDashboard(Doctor doctor) {
        System.out.println("✓ Doctor login successful: Dr. " + doctor.getName());

        DoctorDashboard dashboard = new DoctorDashboard();
        dashboard.setDoctorData(doctor, appointments, appointmentCount);
        dashboard.start(primaryStage);
    }

    private void openAdminDashboard(Admin admin) {
        System.out.println("✓ Admin login successful: " + admin.getName());

        AdminDashboard dashboard = new AdminDashboard();
        dashboard.setAdminData(admin, patients, patientCount, doctors, doctorCount,
                appointments, appointmentCount);

        dashboard.start(primaryStage);
    }

    private void goToRegistrationPage() {
        RegistrationPage registrationPage = new RegistrationPage();
        RegistrationPage.setPatientData(patients, patientCount);
        RegistrationPage.setDoctorData(doctors, doctorCount);
        registrationPage.start(primaryStage);
    }

    public static Patient[] getPatients() { return patients; }
    public static Doctor[] getDoctors() { return doctors; }
    public static Admin[] getAdmins() { return admins; }
    public static Appointment[] getAppointments() { return appointments; }

    public static int getPatientCount() { return patientCount; }
    public static int getDoctorCount() { return doctorCount; }
    public static int getAdminCount() { return adminCount; }
    public static int getAppointmentCount() { return appointmentCount; }

    public static void setPatientCount(int count) { patientCount = count; }
    public static void setDoctorCount(int count) { doctorCount = count; }
    public static void setAppointmentCount(int count) { appointmentCount = count; }

    public void setPatientData(Patient[] pats, int count) {
        patients = pats;
        patientCount = count;
    }

    public void setDoctorData(Doctor[] docs, int count) {
        doctors = docs;
        doctorCount = count;
    }

    private void loadAllData() {
        System.out.println("Loading data from files...");

        patientCount = FileHandling.loadPatients(patients);
        doctorCount = FileHandling.loadDoctors(doctors);
        appointmentCount = FileHandling.loadAppointments(appointments);
        admins[0] = new Admin("Syed Fahad Ali", 101, "fahad", "fahad123");
        adminCount = 1;

        System.out.println("✓ Data loaded: " + patientCount + " patients, " +
                doctorCount + " doctors, " + appointmentCount + " appointments");
    }

    public static void main(String[] args) {
        launch(args);
    }
}