package ui;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import User.Patient;
import User.Doctor;
import appointments.Appointment;
import database.FileHandling;
import exceptions.InvalidDateException;
import exceptions.SlotFullException;

public class PatientDashboard extends Application {

    private BorderPane root;
    private Stage primaryStage;
    private Patient currentPatient;
    private Doctor[] doctors;
    private int doctorCount;
    private Appointment[] appointments;
    private int appointmentCount;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        root = new BorderPane();
        root.setStyle("-fx-background-color: #f8fafc;"); // Light slate background
        VBox sidebar = createSidebar();
        root.setLeft(sidebar);
        root.setCenter(createDoctorsView());

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("SmartHealth - Patient Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setPatientData(Patient patient, Doctor[] doctors, int doctorCount,
                               Appointment[] appointments, int appointmentCount) {
        this.currentPatient = patient;
        this.doctors = doctors;
        this.doctorCount = doctorCount;
        this.appointments = appointments;
        this.appointmentCount = appointmentCount;

        System.out.println("✓ Patient dashboard loaded for " + patient.getName());
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setPrefWidth(260);
        sidebar.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 0 1 0 0;");

        Label logo = new Label("SmartHealth");
        logo.setFont(Font.font("System", FontWeight.BOLD, 24));
        logo.setTextFill(Color.web("#2563eb")); // Blue-600

        Label subtitle = new Label("PATIENT PORTAL");
        subtitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        subtitle.setTextFill(Color.web("#94a3b8"));

        VBox header = new VBox(5, logo, subtitle);
        header.setPadding(new Insets(0, 0, 30, 0));

        Button btnDoctors = createNavButton("View Doctors", e -> root.setCenter(createDoctorsView()));
        Button btnBook = createNavButton("Book Appointment", e -> root.setCenter(createBookingView()));
        Button btnApps = createNavButton("My Appointments", e -> root.setCenter(createAppointmentsView()));
        Button btnHistory = createNavButton("Medical History", e -> root.setCenter(createHistoryView()));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnLogout = createNavButton("Logout", e -> handleLogout());
        btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-alignment: CENTER_LEFT; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 14;");

        sidebar.getChildren().addAll(header, btnDoctors, btnBook, btnApps, btnHistory, spacer, btnLogout);
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

    private Node createDoctorsView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(40));

        Label title = new Label("Available Doctors");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#1e293b"));

        Label subtitle = new Label("Browse all doctors and their specializations");
        subtitle.setFont(Font.font("System", 14));
        subtitle.setTextFill(Color.web("#64748b"));

        if (doctorCount == 0) {
            VBox noDoctors = new VBox(10);
            noDoctors.setAlignment(Pos.CENTER);
            noDoctors.setPadding(new Insets(40));
            noDoctors.setStyle("-fx-background-color: white; -fx-background-radius: 15;");

            Label noDoctorsLabel = new Label("No doctors available yet");
            noDoctorsLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
            noDoctorsLabel.setTextFill(Color.web("#64748b"));

            noDoctors.getChildren().add(noDoctorsLabel);
            layout.getChildren().addAll(title, subtitle, noDoctors);
        } else {
            FlowPane cardsContainer = new FlowPane();
            cardsContainer.setHgap(20);
            cardsContainer.setVgap(20);

            for (int i = 0; i < doctorCount; i++) {
                cardsContainer.getChildren().add(createDoctorCard(doctors[i]));
            }

            layout.getChildren().addAll(title, subtitle, cardsContainer);
        }

        return layout;
    }

    private VBox createDoctorCard(Doctor doc) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setPrefWidth(280);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);");

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(2);
        Label name = new Label("Dr. " + doc.getName());
        name.setFont(Font.font("System", FontWeight.BOLD, 16));

        Label spec = new Label(doc.getSpecialization());
        spec.setStyle("-fx-text-fill: #2563eb; -fx-background-color: #eff6ff; -fx-padding: 3 8 3 8; -fx-background-radius: 5; -fx-font-size: 12; -fx-font-weight: bold;");

        info.getChildren().addAll(name, spec);
        header.getChildren().addAll(info);

        Label details = new Label("ID: " + doc.getId() + "\nEmail: " + doc.getEmail());
        details.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13;");

        card.getChildren().addAll(header, details);
        return card;
    }

    private Node createBookingView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(40));
        layout.setAlignment(Pos.TOP_CENTER);

        VBox formCard = new VBox(20);
        formCard.setMaxWidth(600);
        formCard.setPadding(new Insets(30));
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 20, 0, 0, 10);");

        Label formTitle = new Label("Book Appointment");
        formTitle.setFont(Font.font("System", FontWeight.BOLD, 22));

        Label formSubtitle = new Label("Schedule an appointment with your preferred doctor");
        formSubtitle.setFont(Font.font("System", 14));
        formSubtitle.setTextFill(Color.web("#64748b"));

        ComboBox<String> doctorCombo = new ComboBox<>();
        for(int i = 0; i < doctorCount; i++) {
            doctorCombo.getItems().add(doctors[i].getName() + " (" + doctors[i].getSpecialization() + ")");
        }
        doctorCombo.setPromptText("Select Doctor");
        doctorCombo.setPrefWidth(Double.MAX_VALUE);
        doctorCombo.setPrefHeight(45);
        doctorCombo.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-radius: 10; -fx-background-radius: 10;");

        ComboBox<String> dayCombo = new ComboBox<>();
        dayCombo.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        dayCombo.setPromptText("Select Day");
        dayCombo.setPrefWidth(Double.MAX_VALUE);
        dayCombo.setPrefHeight(45);
        dayCombo.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-radius: 10; -fx-background-radius: 10;");

        ComboBox<String> timeCombo = new ComboBox<>();
        timeCombo.setPromptText("Select Time Slot");
        timeCombo.setPrefWidth(Double.MAX_VALUE);
        timeCombo.setPrefHeight(45);
        timeCombo.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-radius: 10; -fx-background-radius: 10;");
        timeCombo.setDisable(true);

        dayCombo.setOnAction(e -> {
            if (doctorCombo.getValue() != null && dayCombo.getValue() != null) {
                int doctorIndex = doctorCombo.getSelectionModel().getSelectedIndex();
                String day = dayCombo.getValue();
                updateTimeSlots(doctors[doctorIndex], day, timeCombo);
            }
        });

        doctorCombo.setOnAction(e -> {
            if (doctorCombo.getValue() != null && dayCombo.getValue() != null) {
                int doctorIndex = doctorCombo.getSelectionModel().getSelectedIndex();
                String day = dayCombo.getValue();
                updateTimeSlots(doctors[doctorIndex], day, timeCombo);
            }
        });

        TextArea concernArea = new TextArea();
        concernArea.setPromptText("Describe your disease or concern...");
        concernArea.setPrefRowCount(3);
        concernArea.setStyle("-fx-control-inner-background: #f8fafc; -fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        errorLabel.setWrapText(true);

        Button confirmBtn = new Button("Confirm Booking");
        confirmBtn.setPrefWidth(Double.MAX_VALUE);
        confirmBtn.setPrefHeight(50);
        confirmBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16; -fx-background-radius: 12; -fx-cursor: hand;");
        confirmBtn.setOnAction(e -> {
            handleBookAppointment(doctorCombo, dayCombo, timeCombo, concernArea, errorLabel);
        });

        formCard.getChildren().addAll(formTitle, formSubtitle, doctorCombo, dayCombo, timeCombo, concernArea, errorLabel, confirmBtn);
        layout.getChildren().add(formCard);
        return layout;
    }

    private void updateTimeSlots(Doctor doctor, String day, ComboBox<String> timeCombo) {
        timeCombo.getItems().clear();

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        int dayIndex = -1;
        for (int i = 0; i < days.length; i++) {
            if (days[i].equals(day)) {
                dayIndex = i;
                break;
            }
        }

        if (dayIndex != -1) {
            String[][] schedule = doctor.getSchedule();
            for (int j = 0; j < 5; j++) {
                if (schedule[dayIndex][j] != null &&
                        !schedule[dayIndex][j].equals("Off") &&
                        !schedule[dayIndex][j].isEmpty()) {
                    timeCombo.getItems().add(schedule[dayIndex][j]);
                }
            }

            if (timeCombo.getItems().isEmpty()) {
                timeCombo.setDisable(true);
                timeCombo.setPromptText("Doctor not available on " + day);
            } else {
                timeCombo.setDisable(false);
                timeCombo.setPromptText("Select Time Slot");
            }
        }
    }

    private void handleBookAppointment(ComboBox<String> doctorCombo, ComboBox<String> dayCombo,
                                       ComboBox<String> timeCombo, TextArea concernArea, Label errorLabel) {
        errorLabel.setText("");

        try {
            if (doctorCombo.getValue() == null) {
                throw new IllegalArgumentException("Please select a doctor!");
            }
            if (dayCombo.getValue() == null) {
                throw new IllegalArgumentException("Please select a day!");
            }
            if (timeCombo.getValue() == null) {
                throw new IllegalArgumentException("Please select a time slot!");
            }
            if (concernArea.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Please describe your concern!");
            }

            int doctorIndex = doctorCombo.getSelectionModel().getSelectedIndex();
            Doctor selectedDoctor = doctors[doctorIndex];
            String day = dayCombo.getValue();
            String timeSlot = timeCombo.getValue();
            String disease = concernArea.getText().trim();

            if (!selectedDoctor.isAvailable(day, timeSlot)) {
                throw new InvalidDateException("Selected time slot is not available!");
            }

            for (int i = 0; i < appointmentCount; i++) {
                if (appointments[i].getDoctorName().equals(selectedDoctor.getName()) &&
                        appointments[i].getDate().equalsIgnoreCase(day) &&
                        appointments[i].getTimeSlot().equals(timeSlot)) {
                    throw new SlotFullException("This time slot is already booked! Please choose another.");
                }
            }

            int appointmentId = 1000 + appointmentCount;
            Appointment newAppointment = new Appointment(
                    appointmentId,
                    currentPatient.getName(),
                    selectedDoctor.getName(),
                    day,
                    timeSlot,
                    disease
            );

            appointments[appointmentCount] = newAppointment;
            appointmentCount++;

            LoginPage.setAppointmentCount(appointmentCount);
            FileHandling.saveAppointments(appointments, appointmentCount);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Booking Success");
            alert.setHeaderText("✓ Appointment Booked Successfully!");
            alert.setContentText(
                    "Appointment ID: " + appointmentId + "\n" +
                            "Doctor: Dr. " + selectedDoctor.getName() + "\n" +
                            "Day: " + day + "\n" +
                            "Time: " + timeSlot + "\n" +
                            "Concern: " + disease
            );
            alert.showAndWait();
            root.setCenter(createAppointmentsView());

        } catch (InvalidDateException | SlotFullException | IllegalArgumentException ex) {
            errorLabel.setText("✗ " + ex.getMessage());
        } catch (Exception ex) {
            errorLabel.setText("✗ Failed to book appointment: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private Node createAppointmentsView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(40));

        Label title = new Label("My Appointments");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));

        Label subtitle = new Label("View all your scheduled appointments");
        subtitle.setFont(Font.font("System", 14));
        subtitle.setTextFill(Color.web("#64748b"));

        ObservableList<Appointment> patientAppointments = FXCollections.observableArrayList();
        for (int i = 0; i < appointmentCount; i++) {
            if (appointments[i].getPatientName().equals(currentPatient.getName())) {
                patientAppointments.add(appointments[i]);
            }
        }

        if (patientAppointments.isEmpty()) {
            VBox noAppointments = new VBox(10);
            noAppointments.setAlignment(Pos.CENTER);
            noAppointments.setPadding(new Insets(40));
            noAppointments.setStyle("-fx-background-color: white; -fx-background-radius: 15;");

            Label noAppLabel = new Label("No appointments scheduled yet");
            noAppLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
            noAppLabel.setTextFill(Color.web("#64748b"));

            Button bookBtn = new Button("Book Your First Appointment");
            bookBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
            bookBtn.setOnAction(e -> root.setCenter(createBookingView()));

            noAppointments.getChildren().addAll(noAppLabel, bookBtn);
            layout.getChildren().addAll(title, subtitle, noAppointments);
        } else {
            TableView<Appointment> table = new TableView<>();
            table.setItems(patientAppointments);
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            table.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5); -fx-background-radius: 10;");

            TableColumn<Appointment, Integer> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));

            TableColumn<Appointment, String> docCol = new TableColumn<>("Doctor");
            docCol.setCellValueFactory(new PropertyValueFactory<>("doctorName"));

            TableColumn<Appointment, String> dateCol = new TableColumn<>("Date");
            dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

            TableColumn<Appointment, String> timeCol = new TableColumn<>("Time");
            timeCol.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));

            TableColumn<Appointment, String> diseaseCol = new TableColumn<>("Concern");
            diseaseCol.setCellValueFactory(new PropertyValueFactory<>("disease"));

            table.getColumns().addAll(idCol, docCol, dateCol, timeCol, diseaseCol);

            layout.getChildren().addAll(title, subtitle, table);
        }

        return layout;
    }

    private Node createHistoryView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(40));

        Label title = new Label("Medical History");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));

        Label subtitle = new Label("Your complete medical records");
        subtitle.setFont(Font.font("System", 14));
        subtitle.setTextFill(Color.web("#64748b"));

        String[] history = currentPatient.getMedicalHistory();
        ObservableList<String> historyList = FXCollections.observableArrayList();

        for (String record : history) {
            if (record != null && !record.isEmpty()) {
                historyList.add(record);
            }
        }

        ListView<String> listView = new ListView<>(historyList);
        listView.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5); -fx-control-inner-background: white;");
        listView.setPrefHeight(300);

        if (historyList.isEmpty()) {
            listView.setPlaceholder(new Label("No medical history recorded yet"));
        }

        layout.getChildren().addAll(title, subtitle, listView);
        return layout;
    }

    private void handleLogout() {
        try {
            Patient[] patients = LoginPage.getPatients();
            int patientCount = LoginPage.getPatientCount();
            FileHandling.savePatients(patients, patientCount);
            FileHandling.saveAppointments(appointments, appointmentCount);

            System.out.println("✓ Patient data saved successfully on logout");

            LoginPage loginPage = new LoginPage();
            loginPage.start(primaryStage);

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