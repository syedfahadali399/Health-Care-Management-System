package ui;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import User.Doctor;
import appointments.Appointment;
import database.FileHandling;

public class DoctorDashboard extends Application {

    private BorderPane root;
    private final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    private Doctor currentDoctor;
    private String[][] schedule;
    private boolean[] isWorking = new boolean[7];
    private Appointment[] appointments;
    private int appointmentCount;

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Initialize schedule from doctor's data
        schedule = currentDoctor.getSchedule();

        for(int i = 0; i < 7; i++) {
            isWorking[i] = false;
            for(int j = 0; j < 5; j++) {
                if (schedule[i][j] != null && !schedule[i][j].equals("Off")) {
                    isWorking[i] = true;
                    break;
                }
            }
        }

        root = new BorderPane();
        root.setStyle("-fx-background-color: #f8fafc;");
        root.setLeft(createSidebar());
        root.setCenter(createSetScheduleView());

        Scene scene = new Scene(root, 1100, 750);
        primaryStage.setTitle("SmartHealth - Doctor Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setDoctorData(Doctor doctor, Appointment[] appointments, int appointmentCount) {
        this.currentDoctor = doctor;
        this.appointments = appointments;
        this.appointmentCount = appointmentCount;

        System.out.println("✓ Doctor dashboard loaded for Dr. " + doctor.getName() +
                " (" + doctor.getSpecialization() + ")");
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setPrefWidth(280);
        sidebar.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 0 1 0 0;");

        Label logo = new Label("SmartHealth");
        logo.setFont(Font.font("System", FontWeight.BOLD, 24));
        logo.setTextFill(Color.web("#2563eb"));

        Label subtitle = new Label("DOCTOR PORTAL");
        subtitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        subtitle.setTextFill(Color.web("#94a3b8"));

        Label doctorName = new Label("Dr. " + currentDoctor.getName());
        doctorName.setFont(Font.font("System", FontWeight.BOLD, 14));
        doctorName.setTextFill(Color.web("#475569"));

        Label specialization = new Label(currentDoctor.getSpecialization());
        specialization.setFont(Font.font("System", 12));
        specialization.setTextFill(Color.web("#94a3b8"));

        VBox header = new VBox(5, logo, subtitle, doctorName, specialization);
        header.setPadding(new Insets(0, 0, 30, 0));

        Button btnSet = createNavButton("Set My Schedule", e -> root.setCenter(createSetScheduleView()));
        Button btnView = createNavButton("View Schedule", e -> root.setCenter(createViewScheduleView()));
        Button btnApps = createNavButton("My Appointments", e -> root.setCenter(createAppointmentsView()));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnLogout = createNavButton("Logout", e -> handleLogout());
        btnLogout.setTextFill(Color.RED);

        sidebar.getChildren().addAll(header, btnSet, btnView, btnApps, spacer, btnLogout);
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

    private Node createSetScheduleView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(40));

        Label title = new Label("Set Weekly Schedule");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));

        Label subtitle = new Label("Configure your availability for the week");
        subtitle.setFont(Font.font("System", 14));
        subtitle.setTextFill(Color.web("#64748b"));

        ScrollPane scroll = new ScrollPane();
        VBox container = new VBox(20);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: transparent;");

        for (int i = 0; i < 7; i++) {
            container.getChildren().add(createDayEditor(i));
        }

        scroll.setContent(container);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        Button saveBtn = new Button("Save All Changes");
        saveBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16; -fx-background-radius: 10; -fx-padding: 15 30 15 30;");
        saveBtn.setOnAction(e -> handleSaveSchedule());

        layout.getChildren().addAll(title, subtitle, scroll, saveBtn);
        return layout;
    }

    private VBox createDayEditor(int dayIndex) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);");

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label dayLabel = new Label(days[dayIndex]);
        dayLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        dayLabel.setPrefWidth(120);

        ToggleGroup group = new ToggleGroup();
        RadioButton rbYes = new RadioButton("Working");
        RadioButton rbNo = new RadioButton("Off");
        rbYes.setToggleGroup(group);
        rbNo.setToggleGroup(group);

        if (isWorking[dayIndex]) rbYes.setSelected(true);
        else rbNo.setSelected(true);

        HBox toggleBox = new HBox(10, rbYes, rbNo);
        toggleBox.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().addAll(dayLabel, toggleBox);
        GridPane slotsGrid = new GridPane();
        slotsGrid.setHgap(15);
        slotsGrid.setVgap(10);

        for (int j = 0; j < 5; j++) {
            VBox slotBox = new VBox(5);
            Label slotLabel = new Label("Slot " + (j + 1));
            slotLabel.setStyle("-fx-font-size: 10; -fx-font-weight: bold; -fx-text-fill: #94a3b8;");

            String currentSlot = schedule[dayIndex][j];
            if (currentSlot == null || currentSlot.equals("Off")) {
                currentSlot = "";
            }

            TextField field = new TextField(currentSlot);
            field.setPromptText("e.g. 09:00-10:00");
            field.setPrefHeight(40);
            field.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");

            int finalJ = j;
            field.textProperty().addListener((obs, oldV, newV) -> {
                schedule[dayIndex][finalJ] = newV;
            });
            field.disableProperty().bind(rbNo.selectedProperty());
            slotBox.getChildren().addAll(slotLabel, field);
            slotsGrid.add(slotBox, j, 0);
        }

        rbYes.setOnAction(e -> {
            isWorking[dayIndex] = true;
        });
        rbNo.setOnAction(e -> {
            isWorking[dayIndex] = false;
            for(int k = 0; k < 5; k++) {
                schedule[dayIndex][k] = "Off";
            }
        });

        card.getChildren().addAll(header, slotsGrid);
        return card;
    }

    private void handleSaveSchedule() {
        try {
            currentDoctor.getSchedule(); // This returns reference to same array we've been modifying

            Doctor[] doctors = LoginPage.getDoctors();
            int doctorCount = LoginPage.getDoctorCount();
            FileHandling.saveDoctors(doctors, doctorCount);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("✓ Schedule Saved Successfully!");
            alert.setContentText("Your weekly availability has been updated.");
            alert.showAndWait();
            root.setCenter(createViewScheduleView());

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Save Schedule");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private Node createViewScheduleView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(40));

        Label title = new Label("My Weekly Availability");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));

        Label subtitle = new Label("Current schedule for Dr. " + currentDoctor.getName());
        subtitle.setFont(Font.font("System", 14));
        subtitle.setTextFill(Color.web("#64748b"));

        FlowPane flow = new FlowPane();
        flow.setHgap(20);
        flow.setVgap(20);

        boolean hasSchedule = false;
        for (int i = 0; i < 7; i++) {
            VBox dayCard = new VBox(10);
            dayCard.setPrefWidth(220);
            dayCard.setPadding(new Insets(15));
            dayCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #e2e8f0;");

            Label name = new Label(days[i]);
            name.setFont(Font.font("System", FontWeight.BOLD, 16));
            name.setPadding(new Insets(0, 0, 5, 0));

            dayCard.getChildren().add(name);

            boolean dayHasSlots = false;
            for (int j = 0; j < 5; j++) {
                if (schedule[i][j] != null && !schedule[i][j].equals("Off") && !schedule[i][j].isEmpty()) {
                    HBox row = new HBox();
                    row.setPadding(new Insets(5));
                    row.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 5;");
                    Label sLabel = new Label("Slot " + (j + 1) + ": " + schedule[i][j]);
                    sLabel.setFont(Font.font("System", 12));
                    row.getChildren().add(sLabel);
                    dayCard.getChildren().add(row);
                    dayHasSlots = true;
                    hasSchedule = true;
                }
            }

            if (!dayHasSlots) {
                Label off = new Label("NOT WORKING");
                off.setTextFill(Color.GRAY);
                off.setFont(Font.font("System", FontWeight.BOLD, 14));
                off.setPadding(new Insets(10, 0, 0, 0));
                dayCard.getChildren().add(off);
            }

            flow.getChildren().add(dayCard);
        }

        if (!hasSchedule) {
            VBox noSchedule = new VBox(10);
            noSchedule.setAlignment(Pos.CENTER);
            noSchedule.setPadding(new Insets(40));
            noSchedule.setStyle("-fx-background-color: white; -fx-background-radius: 15;");

            Label noScheduleLabel = new Label("No schedule set yet");
            noScheduleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
            noScheduleLabel.setTextFill(Color.web("#64748b"));

            noSchedule.getChildren().addAll(noScheduleLabel);
            layout.getChildren().addAll(title, subtitle, noSchedule);
        } else {
            layout.getChildren().addAll(title, subtitle, flow);
        }

        return layout;
    }

    private Node createAppointmentsView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(40));

        Label title = new Label("My Appointments");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));

        Label subtitle = new Label("Appointments scheduled with you");
        subtitle.setFont(Font.font("System", 14));
        subtitle.setTextFill(Color.web("#64748b"));

        ScrollPane scroll = new ScrollPane();
        VBox appointmentsList = new VBox(15);
        appointmentsList.setPadding(new Insets(10));
        appointmentsList.setStyle("-fx-background-color: transparent;");

        boolean hasAppointments = false;

        for (int i = 0; i < appointmentCount; i++) {
            if (appointments[i].getDoctorName().equals(currentDoctor.getName())) {
                appointmentsList.getChildren().add(createAppointmentCard(appointments[i]));
                hasAppointments = true;
            }
        }

        if (!hasAppointments) {
            VBox noAppointments = new VBox(10);
            noAppointments.setAlignment(Pos.CENTER);
            noAppointments.setPadding(new Insets(40));
            noAppointments.setStyle("-fx-background-color: white; -fx-background-radius: 15;");

            Label noAppLabel = new Label("No appointments scheduled yet");
            noAppLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
            noAppLabel.setTextFill(Color.web("#64748b"));

            Label infoLabel = new Label("Patients can book appointments with you once you set your schedule");
            infoLabel.setFont(Font.font("System", 12));
            infoLabel.setTextFill(Color.web("#94a3b8"));
            infoLabel.setWrapText(true);
            infoLabel.setMaxWidth(400);
            infoLabel.setAlignment(Pos.CENTER);

            noAppointments.getChildren().addAll(noAppLabel, infoLabel);
            layout.getChildren().addAll(title, subtitle, noAppointments);
        } else {
            scroll.setContent(appointmentsList);
            scroll.setFitToWidth(true);
            scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
            layout.getChildren().addAll(title, subtitle, scroll);
        }

        return layout;
    }

    private HBox createAppointmentCard(Appointment appointment) {
        HBox card = new HBox(20);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: #e2e8f0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);");

        Label icon = new Label(" ");
        icon.setStyle("-fx-font-size: 30; -fx-background-color: #dbeafe; -fx-padding: 10; -fx-background-radius: 50;");

        VBox details = new VBox(5);
        Label pName = new Label("Patient: " + appointment.getPatientName());
        pName.setFont(Font.font("System", FontWeight.BOLD, 16));

        Label pIssue = new Label("Concern: " + appointment.getDisease());
        pIssue.setTextFill(Color.web("#64748b"));
        pIssue.setFont(Font.font("System", 14));

        Label appointmentId = new Label("ID: " + appointment.getAppointmentId());
        appointmentId.setTextFill(Color.web("#94a3b8"));
        appointmentId.setFont(Font.font("System", 12));

        details.getChildren().addAll(pName, pIssue, appointmentId);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox timeInfo = new VBox(5);
        timeInfo.setAlignment(Pos.CENTER_RIGHT);

        Label date = new Label(appointment.getDate());
        date.setFont(Font.font("System", FontWeight.BOLD, 14));

        Label slot = new Label(appointment.getTimeSlot());
        slot.setTextFill(Color.web("#2563eb"));
        slot.setFont(Font.font("System", FontWeight.BOLD, 13));

        timeInfo.getChildren().addAll(date, slot);

        card.getChildren().addAll(icon, details, spacer, timeInfo);
        return card;
    }

    private void handleLogout() {
        try {
            Doctor[] doctors = LoginPage.getDoctors();
            int doctorCount = LoginPage.getDoctorCount();
            FileHandling.saveDoctors(doctors, doctorCount);

            Appointment[] allAppointments = LoginPage.getAppointments();
            int allAppointmentCount = LoginPage.getAppointmentCount();
            FileHandling.saveAppointments(allAppointments, allAppointmentCount);

            System.out.println("✓ Doctor data saved successfully on logout");
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