package com.adalove.api.controller;

import com.adalove.api.model.dao.PatologiaDAO;
import com.adalove.api.model.entities.Patologia;
import com.adalove.api.model.services.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;

public class EditarPatologiaController {

    @FXML
    private TextField cidField;
    @FXML
    private TextField grauField;
    @FXML
    private TextField nomeField;

    private final PatologiaDAO patologiaDAO = new PatologiaDAO();
    private Patologia patologia;
    @Setter
    private Stage stage;

    public void setPatologia(Patologia patologia) {
        this.patologia = patologia;
        cidField.setText(patologia.getCid());
        grauField.setText(String.valueOf(patologia.getGrau()));
        nomeField.setText(patologia.getNome());
    }

    @FXML
    public void onSavePatologiaClick(ActionEvent event) {
        patologia.setCid(cidField.getText());
        patologia.setGrau(Integer.parseInt(grauField.getText()));
        patologia.setNome(nomeField.getText());
        patologiaDAO.update(patologia);

        AlertUtil.showSuccessAlert("Registro editado com sucesso!");


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onCancelClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }


}
