package com.adalove.api.controller;

import com.adalove.api.model.dao.FichaPacienteDAO;
import com.adalove.api.model.dao.PacienteDAO;
import com.adalove.api.model.entities.FichaPaciente;
import com.adalove.api.model.entities.Paciente;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

public class ResultadoFichaController {
    @FXML
    private TextField nomeField;

    @FXML
    private TextField cpfField;

    @FXML TextField idFuncionarioField;

    @FXML
    private TextArea observacoesField;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private MainViewController mainViewController;

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @FXML
    private void voltarParaMainView() {
        if (mainViewController != null) {
            mainViewController.loadCadastrarFicha(); // Ou outro método que deseja chamar
        }
    }


    // Método para inicializar a tela com os dados
    public void setDados(String nome, String cpf, String observacoes) {
        nomeField.setText(nome);
        cpfField.setText(cpf);
        observacoesField.setText(observacoes);
    }

    @FXML
    private void salvar() {
        String nomeEditado = nomeField.getText();
        String cpfEditado = cpfField.getText();
        int idFuncionario = Integer.parseInt(idFuncionarioField.getText());
        String observacoesEditadas = observacoesField.getText();

        // Verifica se o paciente existe
        PacienteDAO pacienteDAO = new PacienteDAO();
        Paciente paciente = pacienteDAO.buscarPorCpf(cpfEditado);

        if (paciente == null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Paciente não encontrado");
            alert.setHeaderText(null);
            alert.setContentText("Paciente não encontrado. Gostaria de criá-lo?");

            ButtonType cancelarButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType criarButton = new ButtonType("Criar Paciente");

            alert.getButtonTypes().setAll(criarButton, cancelarButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == criarButton) {
                abrirModalCadastroPaciente(cpfEditado, nomeEditado);
                voltarParaMainView();
            } else {
                alert.close();
            }
        } else {
            // Paciente já existe, prosseguir com o salvamento da ficha
            FichaPaciente fichaPaciente = new FichaPaciente(cpfEditado, idFuncionario, observacoesEditadas, LocalDateTime.now());
            FichaPacienteDAO fichaPacienteDAO = new FichaPacienteDAO();
            fichaPacienteDAO.create(fichaPaciente);

            salvarFicha(cpfEditado, nomeEditado);
            voltarParaMainView();
        }
    }


    private void abrirModalCadastroPaciente(String cpf, String nome) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CadastrarPaciente.fxml"));
            Parent root = loader.load();

            // Obtém o controlador do modal e passa os dados do CPF e nome
            CadastrarPacienteController controller = loader.getController();
            controller.setDados(cpf, nome);

            Stage modalStage = new Stage();
            modalStage.setTitle("Cadastro de Paciente");
            modalStage.setScene(new Scene(root));
            modalStage.initOwner(stage); // Define a janela pai do modal
            modalStage.showAndWait(); // Espera o usuário finalizar o modal

            // Após o cadastro do paciente, salva a ficha
            Paciente novoPaciente = controller.getPaciente();

            if (novoPaciente != null) {
                salvarFicha(novoPaciente.getCpf(), novoPaciente.getNome());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void salvarFicha(String cpf, String nome) {
        int idFuncionario = Integer.parseInt(idFuncionarioField.getText());
        String observacoesEditadas = observacoesField.getText();

        FichaPaciente fichaPaciente = new FichaPaciente(cpf, idFuncionario, observacoesEditadas, LocalDateTime.now());
        FichaPacienteDAO fichaPacienteDAO = new FichaPacienteDAO();
        fichaPacienteDAO.create(fichaPaciente);

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText("Ficha salva com sucesso!");
        alert.showAndWait();


    }
}
