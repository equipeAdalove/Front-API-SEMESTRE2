package com.adalove.api.controller;

import com.adalove.api.model.dao.FuncionarioDAO;
import com.adalove.api.model.entities.Funcionario;
import com.adalove.api.model.services.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;

public class AdicionarFuncionarioController {
    @FXML
    private TextField nomeField;

    @FXML
    private TextField crmField;

    @FXML
    private TextField cargoField;

    @Setter
    private Stage stage;

    @FXML
    public void onAddFuncionarioClick(ActionEvent event) {

        String nome = nomeField.getText();
        String crm = crmField.getText();
        String cargo = cargoField.getText();

        Funcionario funcionario = new Funcionario(nome, cargo, crm);
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();

        funcionarioDAO.create(funcionario);

        AlertUtil.showConfirmationAlert("Registro adicionado ao banco de dados.");


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close(); // Fecha a janela
    }

    @FXML
    public void onCancelClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
