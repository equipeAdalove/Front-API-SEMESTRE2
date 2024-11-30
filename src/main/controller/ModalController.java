package com.adalove.api.controller;



import com.adalove.api.model.entities.FichaPaciente;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.Setter;

public class ModalController {

    @FXML
    private Label idLabel;

    @FXML
    private Label cpfLabel;

    @FXML
    private Label idFuncionarioLabel;

    @FXML
    private Label observacoesLabel;

    @FXML
    private Label dataLabel;

    @FXML
    private Label titleLabel;

    @Setter
    private Stage modalStage;

    public void setFichaPaciente(FichaPaciente fichaPaciente,String nome) {
        titleLabel.setText("Paciente: " + nome);
        idLabel.setText("ID: " + fichaPaciente.getId());
        cpfLabel.setText("CPF Paciente: " + fichaPaciente.getCpfPaciente());
        idFuncionarioLabel.setText("ID Funcionário: " + fichaPaciente.getIdFuncionario());
        observacoesLabel.setText("Observações: " + fichaPaciente.getObservacoes());
        dataLabel.setText("Data e Hora: " + fichaPaciente.getDataHora().toString());
    }

}
