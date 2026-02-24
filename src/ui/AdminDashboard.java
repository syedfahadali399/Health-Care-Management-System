package ui;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import User.Admin;
import User.Patient;
import User.Doctor;
import appointments.Appointment;
import database.FileHandling;
import exceptions.DuplicateEmailException;

public class AdminDashboard extends Application {

    private BorderPane root;
    private final String PRIMARY_BLUE = "#2563eb";
    private final String BACKGROUND_COLOR = "#f8fafc";
    private Admin currentAdmin;
    private Patient[] patients;
    private int patientCount;
    private Doctor[] doctors;
    private int doctorCount;
    private Appointment[] appointments;
    private int appointmentCount;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        root.setLeft(createSidebar());
        showOverview();

        Scene scene = new Scene(root, 1100, 750);
        primaryStage.setTitle("SmartHealth | Admin Control Panel");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setAdminData(Admin admin, Patient[] patients, int patientCount,
                             Doctor[] doctors, int doctorCount,
                             Appointment[] appointments, int appointmentCount) {
        this.currentAdmin = admin;
        this.patients = patients;
        this.patientCount = patientCount;
        this.doctors = doctors;
        this.doctorCount = doctorCount;
        this.appointments = appointments;
        this.appointmentCount = appointmentCount;

        System.out.println("✓ Admin dashboard loaded: " + patientCount + " patients, " +
                doctorCount + " doctors, " + appointmentCount + " appointments");
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setPrefWidth(260);
        sidebar.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 0 1 0 0;");

        Label logo = new Label("SmartHealth");
        logo.setFont(Font.font("System", FontWeight.BOLD, 22));
        logo.setTextFill(Color.web(PRIMARY_BLUE));

        Label subtitle = new Label("SYSTEM ADMIN");
        subtitle.setFont(Font.font("System", FontWeight.BOLD, 10));
        subtitle.setTextFill(Color.web("#94a3b8"));
        subtitle.setPadding(new Insets(0, 0, 30, 0));

        Button btnOverview = createNavButton("Overview", e -> showOverview());
        Button btnAddDoc = createNavButton("Add New Doctor", e -> showAddDoctor());
        Button btnDoctors = createNavButton("View Doctors", e -> showAllDoctors());
        Button btnPatients = createNavButton("View Patients", e -> showAllPatients());
        Button btnRecords = createNavButton("Appointments", e -> showRecords());
        Button btnReport = createNavButton("Generate Report", e -> showReport());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnLogout = createNavButton("Logout", e -> handleLogout());
        btnLogout.setTextFill(Color.RED);

        sidebar.getChildren().addAll(logo, subtitle, btnOverview, btnAddDoc, btnDoctors, btnPatients, btnRecords, btnReport, spacer, btnLogout);
        return sidebar;
    }

    private Button createNavButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(12, 15, 12, 15));
        btn.setFont(Font.font("System", FontWeight.BOLD, 14));
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-cursor: hand; -fx-background-radius: 10;");
        btn.setOnAction(action);

        return btn;
    }

    private void showOverview() {
        VBox layout = new VBox(30);
        layout.setPadding(new Insets(40));

        Label header = new Label("Admin Dashboard");
        header.setFont(Font.font("System", FontWeight.BLACK, 32));

        Label welcomeLabel = new Label("Welcome, " + currentAdmin.getName() + "!");
        welcomeLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        welcomeLabel.setTextFill(Color.web("#64748b"));

        HBox statsRow = new HBox(20);
        statsRow.getChildren().addAll(
                createStatCard("Total Doctors", String.valueOf(doctorCount), "#3b82f6"),
                createStatCard("Total Patients", String.valueOf(patientCount), "#10b981"),
                createStatCard("Appointments", String.valueOf(appointmentCount), "#8b5cf6")
        );

        layout.getChildren().addAll(header, welcomeLabel, statsRow);
        root.setCenter(layout);
    }

    private VBox createStatCard(String title, String val, String color) {
        VBox card = new VBox(5);
        card.setPrefWidth(250);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);");

        Label lTitle = new Label(title);
        lTitle.setTextFill(Color.web("#94a3b8"));
        lTitle.setFont(Font.font("System", FontWeight.BOLD, 12));

        Label lVal = new Label(val);
        lVal.setFont(Font.font("System", FontWeight.BLACK, 28));
        lVal.setTextFill(Color.web(color));

        card.getChildren().addAll(lTitle, lVal);
        return card;
    }

    private void showAddDoctor() {
        VBox layout = new VBox(25);
        layout.setPadding(new Insets(40));
        layout.setAlignment(Pos.TOP_CENTER);

        VBox formCard = new VBox(20);
        formCard.setMaxWidth(500);
        formCard.setPadding(new Insets(40));
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 25; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 20, 0, 0, 10);");

        Label title = new Label("Register New Doctor");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        nameField.setPrefHeight(45);
        nameField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e2e8f0;");

        ComboBox<String> specBox = new ComboBox<>(FXCollections.observableArrayList(
                "General", "Dentist", "Dermatologist"
        ));
        specBox.setPromptText("Select Specialization");
        specBox.setMaxWidth(Double.MAX_VALUE);
        specBox.setPrefHeight(45);
        specBox.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email Address");
        emailField.setPrefHeight(45);
        emailField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e2e8f0;");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Assign Password");
        passField.setPrefHeight(45);
        passField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e2e8f0;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        errorLabel.setWrapText(true);

        Button submitBtn = new Button("Add Doctor to System");
        submitBtn.setMaxWidth(Double.MAX_VALUE);
        submitBtn.setPrefHeight(50);
        submitBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12;");

        submitBtn.setOnAction(e -> {
            handleAddDoctor(nameField, specBox, emailField, passField, errorLabel);
        });

        formCard.getChildren().addAll(title, new Separator(), nameField, specBox, emailField, passField, errorLabel, submitBtn);
        layout.getChildren().add(formCard);
        root.setCenter(layout);
    }

    private void handleAddDoctor(TextField nameField, ComboBox<String> specBox,
                                 TextField emailField, PasswordField passField, Label errorLabel) {
        errorLabel.setText("");

        String name = nameField.getText().trim();
        String specialization = specBox.getValue();
        String email = emailField.getText().trim();
        String password = passField.getText();

        try {
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || specialization == null) {
                throw new IllegalArgumentException("Please fill in all fields!");
            }

            if (name.length() < 3) {
                throw new IllegalArgumentException("Name must be at least 3 characters!");
            }

//            if (!email.contains("@")) {
//                throw new IllegalArgumentException("Please enter a valid email!");
//            }

            if (password.length() < 4) {
                throw new IllegalArgumentException("Password must be at least 4 characters!");
            }

            checkDuplicateEmail(email);

            int newId = 200 + doctorCount;
            Doctor newDoctor = new Doctor(name, newId, email, password, specialization);
            doctors[doctorCount] = newDoctor;
            doctorCount++;

            FileHandling.saveDoctors(doctors, doctorCount);
            LoginPage.setDoctorCount(doctorCount);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("✓ Doctor Added Successfully!");
            alert.setContentText(
                    "Name: Dr. " + name + "\n" +
                            "Specialization: " + specialization + "\n" +
                            "Email: " + email + "\n" +
                            "Doctor ID: " + newId + "\n\n" +
                            "Doctor can now login to the system."
            );
            alert.showAndWait();

            nameField.clear();
            specBox.setValue(null);
            emailField.clear();
            passField.clear();

            showOverview();

        } catch (DuplicateEmailException | IllegalArgumentException ex) {
            errorLabel.setText("✗ " + ex.getMessage());
        } catch (Exception ex) {
            errorLabel.setText("✗ Failed to add doctor: " + ex.getMessage());
            ex.printStackTrace();
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

    private void showAllDoctors() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(40));

        Label title = new Label("All Doctors in System");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));

        TableView<Doctor> table = new TableView<>();

        TableColumn<Doctor, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(80);

        TableColumn<Doctor, String> colName = new TableColumn<>("Doctor Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(200);

        TableColumn<Doctor, String> colSpec = new TableColumn<>("Specialization");
        colSpec.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        colSpec.setPrefWidth(180);

        TableColumn<Doctor, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(220);

        table.getColumns().addAll(colId, colName, colSpec, colEmail);

        ObservableList<Doctor> doctorList = FXCollections.observableArrayList();
        for (int i = 0; i < doctorCount; i++) {
            doctorList.add(doctors[i]);
        }
        table.setItems(doctorList);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-radius: 10; -fx-border-radius: 10;");

        if (doctorCount == 0) {
            Label noData = new Label("No doctors in the system yet.");
            noData.setFont(Font.font("System", 16));
            noData.setTextFill(Color.web("#64748b"));
            layout.getChildren().addAll(title, noData);
        } else {
            layout.getChildren().addAll(title, table);
        }

        root.setCenter(layout);
    }

    private void showAllPatients() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(40));

        Label title = new Label("All Patients in System");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));

        TableView<Patient> table = new TableView<>();

        TableColumn<Patient, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(80);

        TableColumn<Patient, String> colName = new TableColumn<>("Patient Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(220);

        TableColumn<Patient, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(250);

        table.getColumns().addAll(colId, colName, colEmail);

        ObservableList<Patient> patientList = FXCollections.observableArrayList();
        for (int i = 0; i < patientCount; i++) {
            patientList.add(patients[i]);
        }
        table.setItems(patientList);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-radius: 10; -fx-border-radius: 10;");

        if (patientCount == 0) {
            Label noData = new Label("No patients in the system yet.");
            noData.setFont(Font.font("System", 16));
            noData.setTextFill(Color.web("#64748b"));
            layout.getChildren().addAll(title, noData);
        } else {
            layout.getChildren().addAll(title, table);
        }

        root.setCenter(layout);
    }

    private void showRecords() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(40));

        Label title = new Label("Appointment Records");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));

        TableView<Appointment> table = new TableView<>();

        TableColumn<Appointment, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        colId.setPrefWidth(80);

        TableColumn<Appointment, String> colPatient = new TableColumn<>("Patient Name");
        colPatient.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colPatient.setPrefWidth(180);

        TableColumn<Appointment, String> colDoctor = new TableColumn<>("Doctor");
        colDoctor.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        colDoctor.setPrefWidth(180);

        TableColumn<Appointment, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDate.setPrefWidth(120);

        TableColumn<Appointment, String> colTime = new TableColumn<>("Time");
        colTime.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));
        colTime.setPrefWidth(120);

        TableColumn<Appointment, String> colDisease = new TableColumn<>("Concern");
        colDisease.setCellValueFactory(new PropertyValueFactory<>("disease"));
        colDisease.setPrefWidth(150);

        table.getColumns().addAll(colId, colPatient, colDoctor, colDate, colTime, colDisease);

        ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
        for (int i = 0; i < appointmentCount; i++) {
            appointmentList.add(appointments[i]);
        }
        table.setItems(appointmentList);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-radius: 10; -fx-border-radius: 10;");

        if (appointmentCount == 0) {
            Label noData = new Label("No appointments in the system yet.");
            noData.setFont(Font.font("System", 16));
            noData.setTextFill(Color.web("#64748b"));
            layout.getChildren().addAll(title, noData);
        } else {
            layout.getChildren().addAll(title, table);
        }

        root.setCenter(layout);
    }

    private void showReport() {
        VBox layout = new VBox(25);
        layout.setPadding(new Insets(40));

        Label title = new Label("System Report");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));

        VBox reportCard = new VBox(15);
        reportCard.setPadding(new Insets(30));
        reportCard.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-border-color: #e2e8f0;");

        StringBuilder report = new StringBuilder();
        report.append(" SYSTEM REPORT          \n");
        report.append("                                \n");
        report.append("Total Patients: ").append(patientCount).append("\n");
        report.append("Total Doctors: ").append(doctorCount).append("\n");
        report.append("Total Appointments: ").append(appointmentCount).append("\n\n");

        if (doctorCount > 0) {
            report.append("--- Doctor Specializations ---\n");
            for (int i = 0; i < doctorCount; i++) {
                report.append("  • Dr. ").append(doctors[i].getName())
                        .append(" (").append(doctors[i].getSpecialization()).append(")\n");
            }
            report.append("\n");
        }

        if (appointmentCount > 0) {
            report.append("--- Appointment Statistics ---\n");
            int[] specCounts = new int[3];
            String[] specs = {"General", "Dentist", "Dermatologist"};

            for (int i = 0; i < appointmentCount; i++) {
                String doctorName = appointments[i].getDoctorName();
                for (int j = 0; j < doctorCount; j++) {
                    if (doctors[j].getName().equals(doctorName)) {
                        String spec = doctors[j].getSpecialization();
                        for (int k = 0; k < specs.length; k++) {
                            if (spec != null && spec.equalsIgnoreCase(specs[k])) {
                                specCounts[k]++;
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < specs.length; i++) {
                if (specCounts[i] > 0) {
                    report.append("  • ").append(specs[i]).append(": ")
                            .append(specCounts[i]).append(" appointments\n");
                }
            }
        }

        TextArea reportArea = new TextArea(report.toString());
        reportArea.setEditable(false);
        reportArea.setPrefHeight(400);
        reportArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 13px;");

        Button generateBtn = new Button("Generate Daily Report");
        generateBtn.setPrefHeight(45);
        generateBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10;");
        generateBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Report Generated");
            alert.setHeaderText("✓ Daily Report Generated Successfully!");
            alert.setContentText("Report timestamp: " + java.time.LocalDateTime.now());
            alert.showAndWait();
        });

        reportCard.getChildren().addAll(reportArea, generateBtn);
        layout.getChildren().addAll(title, reportCard);
        root.setCenter(layout);
    }

    private void handleLogout() {

        try {
            FileHandling.savePatients(patients, patientCount);
            FileHandling.saveDoctors(doctors, doctorCount);
            FileHandling.saveAppointments(appointments, appointmentCount);

            LoginPage loginPage = new LoginPage();
            loginPage.start(primaryStage);

            System.out.println("✓ Data saved successfully on logout");

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Logout Error");
            alert.setContentText("Failed to save data: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}