package com.adalove.api.controller;

import com.adalove.api.model.dao.UsuarioDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class LoginViewController {

    @FXML
    private PasswordField passwordField;  // Alterado para PasswordField para maior segurança no campo de senha

    @FXML
    private Button loginButton;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Método atribuído ao botão de Login
    @FXML
    private void handleLogin() {
        String username = "root";  // Usuário fixo para login (nao utiliza o valor extraido do TextField)
        String password = passwordField.getText();  // Campo de senha

        try {
            if (usuarioDAO.authenticateUser(username, password)) {

                showAlert(Alert.AlertType.INFORMATION, "Login feito com sucesso.", "Bem-vindo!");
                loadDashboard(); // Carregar a tela do dashboard após login bem-sucedido

            } else {
                showAlert(Alert.AlertType.ERROR, "Falha ao Entrar", "Senha Incorreta");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while trying to login.");
        }
    }

    // Tela que carrega a MainView
    private void loadDashboard() {
        try {
            // Carrega a MainView.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent mainView = loader.load();

            // Cria uma nova cena para a MainView
            Scene mainScene = new Scene(mainView);
            mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/application.css")).toExternalForm());

            // Pega o Stage atual (tela de login) e define a MainView como a nova cena
            Stage stage = (Stage) passwordField.getScene().getWindow();  // Pega o Stage atual
            stage.setScene(mainScene);
            stage.setTitle("Clínica Bem Estar © Adalove");  // Define o título da nova janela
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
