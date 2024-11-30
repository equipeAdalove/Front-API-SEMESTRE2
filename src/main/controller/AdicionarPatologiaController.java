package com.adalove.api.controller;

import com.adalove.api.model.dao.PatologiaDAO;
import com.adalove.api.model.entities.Patologia;
import com.adalove.api.model.services.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;

public class AdicionarPatologiaController {
    @FXML
    public TextField grauField;
    @FXML
    private TextField nomeField;

    @FXML
    private TextField cidField;

    @Setter
    private Stage stage;

    @FXML
    public void onAddPatologiaClick(ActionEvent event) {
        String nome = nomeField.getText();
        String cid = cidField.getText();
        int grau = Integer.parseInt(grauField.getText());

        Patologia patologia = new Patologia(cid, grau,nome);
        PatologiaDAO patologiaDAO = new PatologiaDAO();

        patologiaDAO.create(patologia);

        AlertUtil.showConfirmationAlert("Registro adicionado ao banco de dados.");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onCancelClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
