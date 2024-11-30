package com.adalove.api.controller;

import com.adalove.api.model.dao.UsuarioDAO;
import com.adalove.api.model.entities.Usuario;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class LoginViewController {
    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    private void initialize() {
        loginField.requestFocus();
    }

    @FXML
    private void handleLogin() {
        String username = loginField.getText();
        String password = passwordField.getText();

        try {
            Usuario usuario = usuarioDAO.authenticateUser(username, password);
            if (usuario != null) {
                showAlert(Alert.AlertType.INFORMATION, "Login feito com sucesso.", "Bem-vindo, " + usuario.getUsername() + "!");


                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
                Parent mainView = loader.load();


                MainViewController mainController = loader.getController();
                mainController.setUsuario(usuario);

                Scene mainScene = new Scene(mainView);
                mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/application.css")).toExternalForm());

                Stage stage = (Stage) passwordField.getScene().getWindow();
                stage.setScene(mainScene);
                stage.setTitle("MindDoc Analyzer © Adalove");
                stage.setResizable(false);
                stage.show();
            } else {
                showAlert(Alert.AlertType.ERROR, "Falha ao Entrar", "Senha ou usuário incorreto");
            }
        } catch (SQLException | IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Ocorreu um erro ao tentar fazer o login.");
            e.printStackTrace();

        }
    }


    // Tela que carrega a MainView
    private void loadDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent mainView = loader.load();


            Scene mainScene = new Scene(mainView);
            mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/application.css")).toExternalForm());


            Stage stage = (Stage) passwordField.getScene().getWindow();
            stage.setScene(mainScene);
            stage.setTitle("MindDoc Analyzer © Adalove");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);


        alert.showAndWait();
    }
}
