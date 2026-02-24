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
import database.FileHandling;
import exceptions.DuplicateEmailException;

public class RegistrationPage extends Application {

    private static Patient[] patients;
    private static int patientCount;
    private static Doctor[] doctors;
    private static int doctorCount;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;

        if (patients == null) {
            patients = new Patient[1000];
            doctors = new Doctor[20];
            patientCount = FileHandling.loadPatients(patients);
            doctorCount = FileHandling.loadDoctors(doctors);
        }

        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setSpacing(20);
        root.setStyle("-fx-background-color: #f8fafc;"); // Light gray background

        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);

        Label title = new Label("Smart HealthCare");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#2563eb")); // Blue-600

        Label subtitle = new Label("Create your Patient account");
        subtitle.setTextFill(Color.web("#64748b"));

        header.getChildren().addAll(title, subtitle);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setMaxWidth(350);
        grid.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        Label nameLabel = new Label("Full Name");
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your full name");
        nameField.setPrefHeight(40);
        nameField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e2e8f0;");

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

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(330);

        Button registerBtn = new Button("Register Now");
        registerBtn.setPrefWidth(350);
        registerBtn.setPrefHeight(45);
        registerBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 10; -fx-cursor: hand;");

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 0, 1);
        grid.add(emailLabel, 0, 2);
        grid.add(emailField, 0, 3);
        grid.add(passLabel, 0, 4);
        grid.add(passField, 0, 5);
        grid.add(errorLabel, 0, 6);
        grid.add(registerBtn, 0, 7);

        registerBtn.setOnAction(e -> {
            handleRegistration(nameField, emailField, passField, errorLabel);
        });

        Hyperlink backToLogin = new Hyperlink("Already have an account? Sign In");
        backToLogin.setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold;");
        backToLogin.setOnAction(e -> goToLoginPage());

        root.getChildren().addAll(header, grid, backToLogin);

        Scene scene = new Scene(root, 450, 650);
        primaryStage.setTitle("Smart HealthCare - Patient Registration");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleRegistration(TextField nameField, TextField emailField,
                                    PasswordField passField,
                                    Label errorLabel) {
        errorLabel.setText("");

        String fullName = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passField.getText();

        try {
            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                throw new IllegalArgumentException("Please fill in all fields!");
            }

            if (fullName.length() < 3) {
                throw new IllegalArgumentException("Name must be at least 3 characters long!");
            }

//            if ((!email.contains("@")) || (!email.contains("."))) {
//                throw new IllegalArgumentException("Please enter a valid email address!");
//            }

            if (password.length() < 4) {
                throw new IllegalArgumentException("Password must be at least 4 characters long!");
            }

            checkDuplicateEmail(email);

            int newId = 100 + patientCount;
            Patient newPatient = new Patient(fullName, newId, email, password);
            patients[patientCount] = newPatient;
            patientCount++;

            FileHandling.savePatients(patients, patientCount);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration Successful");
            alert.setHeaderText("✓ Account Created Successfully!");
            alert.setContentText(
                    "Welcome to Smart HealthCare System!\n\n" +
                            "Name: " + fullName + "\n" +
                            "Email: " + email + "\n" +
                            "Patient ID: " + newId + "\n\n" +
                            "You can now login with your credentials."
            );
            alert.showAndWait();

            goToLoginPage();

        } catch (DuplicateEmailException e) {
            errorLabel.setText(" " + e.getMessage());
        } catch (IllegalArgumentException e) {
            errorLabel.setText(" " + e.getMessage());
        } catch (Exception e) {
            errorLabel.setText("Registration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void checkDuplicateEmail(String email) throws DuplicateEmailException {
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
    }

    public static void setPatientData(Patient[] pats, int count) {
        patients = pats;
        patientCount = count;
    }

    public static void setDoctorData(Doctor[] docs, int count) {
        doctors = docs;
        doctorCount = count;
    }

    public static Patient[] getPatients() {
        return patients;
    }

    public static int getPatientCount() {
        return patientCount;
    }

    private void goToLoginPage() {
        LoginPage loginPage = new LoginPage();
        loginPage.setPatientData(patients, patientCount);
        loginPage.setDoctorData(doctors, doctorCount);
        loginPage.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}