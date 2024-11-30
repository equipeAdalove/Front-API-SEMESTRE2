package com.adalove.api.controller;

import com.adalove.api.model.dao.FuncionarioDAO;
import com.adalove.api.model.entities.Funcionario;
import com.adalove.api.model.services.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;

public class EditarFuncionarioController {

    @FXML
    private TextField nomeField;
    @FXML
    private TextField cargoField;
    @FXML
    private TextField crmField;

    @Setter
    private Stage stage;


    private final FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
    private Funcionario funcionario;


    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
        nomeField.setText(funcionario.getNome());
        cargoField.setText(funcionario.getCargo());
        crmField.setText(funcionario.getCrm());
    }

    @FXML
    public void onSaveFuncionarioClick(ActionEvent event) {
        funcionario.setNome(nomeField.getText());
        funcionario.setCargo(cargoField.getText());
        funcionario.setCrm(crmField.getText());
        funcionarioDAO.update(funcionario, funcionario.getId());

        System.out.println("Funcionario atualizado com sucesso!");

        AlertUtil.showSuccessAlert("Registro editado com sucesso!");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close(); // Fecha a janela
    }

    @FXML
    public void onCancelClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close(); // Fecha a janela
    }


}
