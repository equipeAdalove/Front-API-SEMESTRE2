package com.adalove.api.controller;

import com.adalove.api.model.dao.PacienteDAO;
import com.adalove.api.model.dao.PatologiaDAO;
import com.adalove.api.model.entities.Paciente;
import com.adalove.api.model.entities.Patologia;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.List;

public class CadastrarPacienteController {
    @FXML
    private TextField nomeField;

    @FXML
    private TextField cpfField;

    @FXML
    private CheckBox sexoMasculino;

    @FXML
    private CheckBox sexoFeminino;

    @FXML
    private ComboBox<String> codigoCidComboBox;

    @FXML
    private TextField idMedicoField;

    private Paciente paciente;

    public void setDados(String cpf, String nome) {
        cpfField.setText(cpf);
        nomeField.setText(nome);
    }

    @FXML
    public void initialize() {
        // Carregar patologias no ComboBox
        carregarPatologias();

        // Configurar CheckBox para que apenas um seja selecionado (sexo)
        sexoMasculino.setOnAction(event -> {
            if (sexoMasculino.isSelected()) {
                sexoFeminino.setSelected(false);
            }
        });

        sexoFeminino.setOnAction(event -> {
            if (sexoFeminino.isSelected()) {
                sexoMasculino.setSelected(false);
            }
        });
    }

    private void carregarPatologias() {
        PatologiaDAO patologiaDAO = new PatologiaDAO();
        List<Patologia> patologias = patologiaDAO.read();
        for (Patologia patologia : patologias) {
            codigoCidComboBox.getItems().add(patologia.getCid());
        }
    }

    // Método chamado ao salvar o novo paciente
    @FXML
    private void salvar() {
        String nome = nomeField.getText();
        String cpf = cpfField.getText();
        String sexo = sexoMasculino.isSelected() ? "M" : "F"; // M ou F dependendo da seleção
        String codigoCid = codigoCidComboBox.getValue(); // Código CID selecionado
        int idMedico;

        // Verifica se o campo ID do médico está preenchido
        try {
            idMedico = Integer.parseInt(idMedicoField.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atenção");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, insira um ID válido para o médico.");
            alert.showAndWait();
            return;
        }

        // Criar novo paciente e salvar no banco de dados
        Paciente paciente = new Paciente(nome, sexo, cpf, codigoCid, idMedico);
        PacienteDAO pacienteDAO = new PacienteDAO();
        pacienteDAO.create(paciente);

        this.paciente = paciente;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText("Paciente cadastrado com sucesso!");
        alert.showAndWait();

        // Fechar a janela após o cadastro
        cpfField.getScene().getWindow().hide();
    }

    public Paciente getPaciente() {
        return paciente;
    }
}
