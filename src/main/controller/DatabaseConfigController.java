package com.adalove.api.controller;

import com.adalove.api.model.factory.DatabaseInitialize;
import com.adalove.api.model.dao.UsuarioDAO;
import com.adalove.api.model.services.AlertUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Objects;

public class DatabaseConfigController {

    @FXML
    private StackPane stackPane;

    @FXML
    private VBox introPane;

    @FXML
    private VBox dbUserPane;

    @FXML
    private VBox appUserPane;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField appUsernameField;

    @FXML
    private PasswordField appPasswordField;

    @FXML
    private TextField appVisiblePasswordField;

    @FXML
    private CheckBox showPasswordCheckBox;

    @FXML
    private PasswordField dbPasswordField;

    @FXML
    private TextField dbVisiblePasswordField;

    @FXML
    private CheckBox dbShowPasswordCheckBox;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private int currentStep = 1;

    @FXML
    private void nextStep() {
        switch (currentStep) {
            case 1:
                introPane.setVisible(false);
                dbUserPane.setVisible(true);
                break;
            case 2:
                dbUserPane.setVisible(false);
                appUserPane.setVisible(true);
                break;
        }
        currentStep++;
    }

    @FXML
    private void handleSave() {
        String dbUrl = "jdbc:mysql://localhost:3306/api";
        String dbUsername = usernameField.getText();
        String dbPassword = dbPasswordField.getText();
        String appUsername = appUsernameField.getText();
        String appPassword = appPasswordField.getText();
        boolean isAdmin = true;

        try {
            DatabaseInitialize.saveConfig(dbUrl, dbUsername, dbPassword);
            DatabaseInitialize.createIfNotExist(dbUrl, dbUsername, dbPassword);

            // Registra o usuário do aplicativo no banco de dados
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            boolean userRegistered = usuarioDAO.registerUser(appUsername, appPassword, isAdmin);
            if (!userRegistered) {
                throw new RuntimeException("Falha ao registrar o usuário inicial.");
            }

            // Abre a tela de login após a configuração
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            stage.setTitle("MindDoc Analyzer - Login");
            stage.setScene(scene);
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/imagens/icon.png"))));
            stage.show();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            AlertUtil.showErrorAlert("Erro ao configurar o banco de dados ou registrar o usuário.");
        } catch (RuntimeException e) {
            e.printStackTrace();
            AlertUtil.showErrorAlert(e.getMessage());
        }
    }

    @FXML
    private void togglePasswordVisibility() {
        if (showPasswordCheckBox.isSelected()) {
            // Copiar o texto do PasswordField para o TextField
            appVisiblePasswordField.setText(appPasswordField.getText());

            // Alternar visibilidade
            appPasswordField.setVisible(false);
            appPasswordField.setManaged(false);
            appVisiblePasswordField.setVisible(true);
            appVisiblePasswordField.setManaged(true);
        } else {
            // Copiar o texto do TextField para o PasswordField
            appPasswordField.setText(appVisiblePasswordField.getText());

            // Alternar visibilidade
            appVisiblePasswordField.setVisible(false);
            appVisiblePasswordField.setManaged(false);
            appPasswordField.setVisible(true);
            appPasswordField.setManaged(true);
        }

    }

    @FXML
    private void toggleDbPasswordVisibility() {
        if (dbShowPasswordCheckBox.isSelected()) {
            // Copiar o texto do PasswordField para o TextField
            dbVisiblePasswordField.setText(dbPasswordField.getText());

            // Alternar visibilidade
            dbPasswordField.setVisible(false);
            dbPasswordField.setManaged(false);
            dbVisiblePasswordField.setVisible(true);
            dbVisiblePasswordField.setManaged(true);
        } else {
            // Copiar o texto do TextField para o PasswordField
            dbPasswordField.setText(dbVisiblePasswordField.getText());

            // Alternar visibilidade
            dbVisiblePasswordField.setVisible(false);
            dbVisiblePasswordField.setManaged(false);
            dbPasswordField.setVisible(true);
            dbPasswordField.setManaged(true);
        }}



    @FXML
    private void openLink() {
        try {
            String url = "https://github.com/equipeAdalove/API-SEMESTRE2/wiki";
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
