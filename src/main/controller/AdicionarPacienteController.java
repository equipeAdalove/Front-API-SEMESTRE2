package com.adalove.api.controller;

import com.adalove.api.model.dao.PacienteDAO;
import com.adalove.api.model.dao.PatologiaDAO;
import com.adalove.api.model.entities.Paciente;
import com.adalove.api.model.entities.Patologia;
import com.adalove.api.model.services.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import lombok.Setter;

import java.util.List;

public class AdicionarPacienteController {

    @FXML
    private TextField nomeField;

    @FXML
    private TextField cpfField;

    @FXML
    private RadioButton masculinoRadio;

    @FXML
    private ComboBox<String> codigoCidComboBox;

    @FXML
    private RadioButton femininoRadio;

    @FXML
    private TextField idMedicoField;

    @FXML
    private ToggleGroup sexoToggleGroup;

    @Setter
    private Stage stage;

    @FXML
    public void initialize() {
        carregarPatologias();

        sexoToggleGroup = new ToggleGroup();
        masculinoRadio.setToggleGroup(sexoToggleGroup);
        femininoRadio.setToggleGroup(sexoToggleGroup);
    }

    private void carregarPatologias() {
        PatologiaDAO patologiaDAO = new PatologiaDAO();
        List<Patologia> patologias = patologiaDAO.read();
        for (Patologia patologia : patologias) {
            codigoCidComboBox.getItems().add(patologia.getCid());
        }
    }

    @FXML
    public void onAddPacienteClick(ActionEvent event) {
        String nome = nomeField.getText();
        String cpf = cpfField.getText();
        String sexo = masculinoRadio.isSelected() ? "M" : "F";
        String codigoCid = codigoCidComboBox.getValue();
        int idMedico= Integer.parseInt(idMedicoField.getText());

        Paciente paciente = new Paciente(nome, sexo, cpf, codigoCid, idMedico);
        PacienteDAO pacienteDAO = new PacienteDAO();
        pacienteDAO.create(paciente);


        nomeField.clear();
        cpfField.clear();
        masculinoRadio.setSelected(false);
        femininoRadio.setSelected(false);


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
