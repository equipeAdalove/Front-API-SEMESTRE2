package com.adalove.api.controller;

import com.adalove.api.model.dao.UsuarioDAO;
import com.adalove.api.model.entities.Usuario;
import com.adalove.api.model.services.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class AdicionarUsuarioController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox adminCheckBox;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void onAddUsuarioClick(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        boolean isAdmin = adminCheckBox.isSelected();

        if (username.isEmpty() || password.isEmpty()) {
            AlertUtil.showErrorAlert("Todos os campos são obrigatórios.");
            return;
        }

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        try {
            // Passar isAdmin para o método registerUser
            if (usuarioDAO.registerUser(username, password, isAdmin)) {
                AlertUtil.showConfirmationAlert("Usuário adicionado com sucesso.");
                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close(); // Fecha a janela
            } else {
                AlertUtil.showErrorAlert("Nome de usuário já existe. Escolha outro nome.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showErrorAlert("Erro ao adicionar usuário: " + e.getMessage());
        }
    }

    @FXML
    public void onCancelClick(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close(); // Fecha a janela
    }
}
