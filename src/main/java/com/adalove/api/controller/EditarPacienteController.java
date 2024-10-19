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

import java.util.List;

public class EditarPacienteController {

    @FXML
    private TextField nomeField;
    @FXML
    private TextField cpfField;
    @FXML
    private RadioButton masculinoRadio;
    @FXML
    private RadioButton femininoRadio;
    @FXML
    private ComboBox<String> codigoCidComboBox;
    @FXML
    private TextField idMedicoField;
    @FXML
    private ToggleGroup sexoToggleGroup;


    private PacienteDAO pacienteDAO = new PacienteDAO();
    private Paciente paciente;
    private Stage stage;
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void carregarPatologias() {
        PatologiaDAO patologiaDAO = new PatologiaDAO();
        List<Patologia> patologias = patologiaDAO.read();
        for (Patologia patologia : patologias) {
            codigoCidComboBox.getItems().add(patologia.getCid());
        }
    }

    public void initialize() {
        carregarPatologias();

        sexoToggleGroup = new ToggleGroup();
        masculinoRadio.setToggleGroup(sexoToggleGroup);
        femininoRadio.setToggleGroup(sexoToggleGroup);
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
        nomeField.setText(paciente.getNome());
        cpfField.setText(paciente.getCpf());
        idMedicoField.setText(String.valueOf(paciente.getIdMedico()));
        if (paciente.getSexo().equals("Masculino")) {
            masculinoRadio.setSelected(true);
        } else {
            femininoRadio.setSelected(true);
        }
        codigoCidComboBox.setValue(paciente.getCid());
    }

    @FXML
    public void onSavePacienteClick(ActionEvent event) {
        paciente.setNome(nomeField.getText());
        paciente.setCpf(cpfField.getText());
        paciente.setSexo(masculinoRadio.isSelected() ? "M" : "F");
        paciente.setCid(codigoCidComboBox.getValue());
        paciente.setIdMedico(Integer.parseInt(idMedicoField.getText()));
        pacienteDAO.update(paciente);

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
