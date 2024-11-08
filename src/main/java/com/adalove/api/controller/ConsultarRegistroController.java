package com.adalove.api.controller;

import com.adalove.api.model.dao.FichaPacienteDAO;
import com.adalove.api.model.dao.PacienteDAO;
import com.adalove.api.model.dao.UsuarioDAO;
import com.adalove.api.model.entities.FichaPaciente;
import com.adalove.api.model.entities.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class ConsultarRegistroController {

    public ComboBox tipoComboBox;
    @FXML
    private TextField cpfTextField;

    @FXML
    private TableView<FichaPaciente> resultTableView;

    @FXML
    private TableColumn<FichaPaciente, Integer> idColumn;

    @FXML
    private TableColumn<FichaPaciente, String> cpfColumn;

    @FXML
    private TableColumn<FichaPaciente, Integer> idFuncionarioColumn;

    @FXML
    private TableColumn<FichaPaciente, Void> deleteColumn;



    @FXML
    private TableColumn<FichaPaciente, LocalDateTime> dataColumn; // Ajuste o tipo para LocalDateTime


    private FichaPacienteDAO fichaPacienteDAO;

    private Usuario usuario;


    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @FXML
    private void initialize() {
        ajustarLarguraColunas();

        fichaPacienteDAO = new FichaPacienteDAO();

        // Configurando as colunas da TableView
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        cpfColumn.setCellValueFactory(new PropertyValueFactory<>("cpfPaciente"));
        idFuncionarioColumn.setCellValueFactory(new PropertyValueFactory<>("idFuncionario"));
        dataColumn.setCellValueFactory(new PropertyValueFactory<>("dataHora"));

        // Formatar a exibição da data na coluna
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        dataColumn.setCellFactory(param -> new TableCell<FichaPaciente, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(formatter));
            }
        });

        // Configuração da coluna de exclusão com controle de permissões
        deleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Excluir");

            {
                deleteButton.getStyleClass().add("remove-button");
                deleteButton.setOnAction(event -> {
                    FichaPaciente fichaPaciente = getTableView().getItems().get(getIndex());
                    if (usuario.isAdministrador()) {
                        mostrarDialogoDeSenha(fichaPaciente);  // Chama o método para pedir senha
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Permissão Negada", "Você não tem permissão para excluir registros.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                    deleteButton.setDisable(!usuario.isAdministrador());  // Desabilita para usuários não administradores
                }
            }
        });

        resultTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                FichaPaciente selectedFicha = resultTableView.getSelectionModel().getSelectedItem();
                if (selectedFicha != null) {
                    showFichaDetails(selectedFicha);
                }
            }
        });
    }





    private void ajustarLarguraColunas() {
        ajustarLarguraColuna(idColumn);
        ajustarLarguraColuna(cpfColumn);
        ajustarLarguraColuna(idFuncionarioColumn);
        ajustarLarguraColuna(dataColumn);
    }

    private void ajustarLarguraColuna(TableColumn<?, ?> column) {
        Text text = new Text(column.getText());
        double textWidth = text.getLayoutBounds().getWidth()+10;

        // Adiciona um valor extra para evitar que o texto fique muito próximo da borda
        double padding = 20;

        // Define a largura mínima da coluna para o tamanho do texto mais o padding
        column.setPrefWidth(textWidth + padding);
    }

    @FXML
    private void onBuscarClick() {
        // Obter o CPF digitado no campo de texto
        String cpf = cpfTextField.getText();

        // Verificar se o CPF foi preenchido
        if (cpf == null || cpf.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Por favor, insira um CPF válido.");
            return;
        }

        // Buscar fichas pelo CPF
        List<FichaPaciente> fichas = fichaPacienteDAO.buscarPorCPF(cpf);

        // Verificar se foram encontradas fichas
        if (fichas.isEmpty()) {
            resultTableView.setVisible(false);
            showAlert( Alert.AlertType.INFORMATION,"Resultado", "Nenhum registro encontrado para o CPF: " + cpf);
        } else {
            resultTableView.setVisible(true);
            resultTableView.getItems().clear();
            resultTableView.getItems().addAll(fichas);
        }
    }

    private void showFichaDetails(FichaPaciente fichaPaciente) {
        try {
            PacienteDAO pacienteDAO = new PacienteDAO();
            String nomePaciente = pacienteDAO.buscarNomePorCPF(fichaPaciente.getCpfPaciente());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Modal.fxml"));
            VBox modalRoot = loader.load();


            ModalController modalController = loader.getController();
            modalController.setFichaPaciente(fichaPaciente, nomePaciente);

            // Criar o novo Stage (janela) para o modal
            Stage modalStage = new Stage();

            // Definir o título como o nome completo do paciente (se encontrado)
            if (nomePaciente != null && !nomePaciente.isEmpty()) {
                modalStage.setTitle("Ficha do Paciente"); // Define o título como o nome completo
            } else {
                modalStage.setTitle("Ficha do Paciente"); // Título padrão
            }


            modalStage.setScene(new Scene(modalRoot));
            modalStage.initModality(Modality.APPLICATION_MODAL);  // Define o modal como bloqueador da janela principal
            modalController.setModalStage(modalStage);

            // Exibe a janela modal e espera ela ser fechada
            modalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarDialogoDeSenha(FichaPaciente fichaPaciente) {
        // Criar o dialog para pedir a senha
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Confirmação de Exclusão");
        dialog.setHeaderText("Digite sua senha para confirmar a exclusão.");

        // Definir os botões no dialog (OK e Cancelar)
        ButtonType loginButtonType = new ButtonType("Confirmar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Criar o campo de senha
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Senha");

        // Layout do dialog
        VBox dialogContent = new VBox();
        dialogContent.setSpacing(10);
        dialogContent.getChildren().addAll(new Label("Senha:"), passwordField);
        dialog.getDialogPane().setContent(dialogContent);

        // Converter o resultado do dialog
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return passwordField.getText();
            }
            return null;
        });

        // Processar o resultado do dialog
        dialog.showAndWait().ifPresent(password -> {
            if (!password.isEmpty()) {
                verificarSenhaEDeletar(fichaPaciente, password);
            }
        });
    }

    private void verificarSenhaEDeletar(FichaPaciente fichaPaciente, String password) {
        String username = usuario.getUsername(); // Obtém o nome de usuário do objeto atual

        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario authenticatedUser = usuarioDAO.authenticateUser(username, password); // Usa o usuário logado
            if (authenticatedUser != null) { // Verifica se a autenticação foi bem-sucedida
                fichaPacienteDAO.delete(fichaPaciente.getId());
                resultTableView.getItems().remove(fichaPaciente);
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Ficha deletada com sucesso.");
            } else {
                // Mensagem personalizada informando que a senha está incorreta
                showAlert(Alert.AlertType.ERROR, "Erro", "Senha incorreta para o usuário " + username + ".");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", "Erro ao tentar deletar a ficha.");
        }
    }

    // Método auxiliar para exibir alertas
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




}
