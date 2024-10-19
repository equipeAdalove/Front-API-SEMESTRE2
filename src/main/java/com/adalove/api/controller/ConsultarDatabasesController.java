package com.adalove.api.controller;

import com.adalove.api.model.dao.FuncionarioDAO;
import com.adalove.api.model.dao.PacienteDAO;
import com.adalove.api.model.dao.PatologiaDAO;
import com.adalove.api.model.dao.UsuarioDAO;
import com.adalove.api.model.entities.Funcionario;
import com.adalove.api.model.entities.Paciente;
import com.adalove.api.model.entities.Patologia;
import com.adalove.api.model.services.AlertUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.control.Alert.AlertType;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ConsultarDatabasesController {

    @FXML
    private ComboBox<String> entityComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private Button adicionarRegistro;

    @FXML
    private TableView<RecordRow> recordsTableView; // Usamos RecordRow como tipo genérico
    @FXML
    private TableColumn<RecordRow, String> idColumn; // Ajuste o tipo conforme o que você espera
    @FXML
    private TableColumn<RecordRow, String> nameColumn; // Ajuste o tipo conforme o que você espera
    @FXML
    private TableColumn<RecordRow, HBox> actionsColumn;

    private FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
    private PacienteDAO pacienteDAO = new PacienteDAO();
    private PatologiaDAO patologiaDAO = new PatologiaDAO();

    @FXML
    public void initialize() {
        entityComboBox.getItems().addAll("Funcionário", "Paciente", "Patologia");
        entityComboBox.setOnAction(event -> onEntitySelected());


        if (!entityComboBox.getItems().isEmpty()) {
            entityComboBox.getSelectionModel().selectFirst();
            loadRecords(entityComboBox.getValue());
        }

        // Configurando as colunas da TableView
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIdentifier()));
        actionsColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getActions()));
    }

    @FXML
    public void onEntitySelected() {
        loadRecords(entityComboBox.getValue());
    }

    @FXML
    public void onSearchClick() {
        String searchText = searchField.getText().trim();
        String selectedEntity = entityComboBox.getValue();

        recordsTableView.getItems().clear();

        if (selectedEntity != null && !searchText.isEmpty()) {
            if (selectedEntity.equals("Funcionário")) {
                List<Funcionario> funcionarios = funcionarioDAO.buscarPorNome(searchText);
                populateTable(funcionarios);
            } else if (selectedEntity.equals("Paciente")) {
                List<Paciente> pacientes = pacienteDAO.buscarPorNome(searchText);
                populateTable(pacientes);
            } else if (selectedEntity.equals("Patologia")) {
                List<Patologia> patologias = patologiaDAO.buscarPorNome(searchText);
                populateTable(patologias);
            }
        } else {
            assert selectedEntity != null;
            loadRecords(selectedEntity);
        }
    }

    private void loadRecords(String entity) {
        recordsTableView.getItems().clear();

        if (entity.equals("Funcionário")) {
            // Atualiza os nomes das colunas
            nameColumn.setText("Nome");
            idColumn.setText("CRM");

            List<Funcionario> funcionarios = funcionarioDAO.read();
            populateTable(funcionarios);
        } else if (entity.equals("Paciente")) {
            // Atualiza os nomes das colunas
            nameColumn.setText("Nome");
            idColumn.setText("CPF");

            List<Paciente> pacientes = pacienteDAO.read();
            populateTable(pacientes);
        } else if (entity.equals("Patologia")) {
            // Atualiza os nomes das colunas
            nameColumn.setText("Nome");
            idColumn.setText("Código CID");

            List<Patologia> patologias = patologiaDAO.read();
            populateTable(patologias);
        }
    }

    private void populateTable(List<?> records) {
        for (Object record : records) {
            String name = "";
            String identifier = ""; // Usado para CPF ou CID
            HBox actionButtons = createActionButtons(record);

            if (record instanceof Funcionario) {
                Funcionario funcionario = (Funcionario) record;
                name = funcionario.getNome();
                identifier = funcionario.getCrm(); // Supondo que o CRM seja usado como identificador
            } else if (record instanceof Paciente) {
                Paciente paciente = (Paciente) record;
                name = paciente.getNome();
                identifier = paciente.getCpf(); // Usando CPF como identificador
            } else if (record instanceof Patologia) {
                Patologia patologia = (Patologia) record;
                name = patologia.getNome();
                identifier = patologia.getCid(); // Cid como identificador
            }

            recordsTableView.getItems().add(new RecordRow(name, identifier, actionButtons));
        }
    }

    private HBox createActionButtons(Object record) {
        Button editButton = new Button("Editar");
        editButton.setOnAction(event -> editRecord(record));
        editButton.getStyleClass().add("edit-button");

        Button deleteButton = new Button("Excluir");
        deleteButton.setOnAction(event -> confirmDeletion(record));
        deleteButton.getStyleClass().add("remove-button");

        HBox hbox = new HBox(editButton, deleteButton);
        hbox.setSpacing(15);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }

    private void editRecord(Object record) {
        if (record instanceof Funcionario) {
            Funcionario funcionario = (Funcionario) record;
            openEditWindow("/fxml/EditarFuncionario.fxml", "Editar Funcionário", funcionario);
        } else if (record instanceof Paciente) {
            Paciente paciente = (Paciente) record;
            openEditWindow("/fxml/EditarPaciente.fxml", "Editar Paciente", paciente);
        } else if (record instanceof Patologia) {
            Patologia patologia = (Patologia) record;
            openEditWindow("/fxml/EditarPatologia.fxml", "Editar Patologia", patologia);
        }
        loadRecords(entityComboBox.getValue());
    }

    private void openEditWindow(String fxmlPath, String title, Object entity) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Obtenha o controller da janela de edição
            if (entity instanceof Funcionario) {
                EditarFuncionarioController controller = loader.getController();
                controller.setFuncionario((Funcionario) entity);
                controller.setStage(new Stage());  // Definindo o stage
            } else if (entity instanceof Paciente) {
                EditarPacienteController controller = loader.getController();
                controller.setPaciente((Paciente) entity);
                controller.setStage(new Stage());  // Definindo o stage
            } else if (entity instanceof Patologia) {
                EditarPatologiaController controller = loader.getController();
                controller.setPatologia((Patologia) entity);
                controller.setStage(new Stage());  // Definindo o stage
            }

            // Cria um novo Stage para a janela de edição
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(recordsTableView.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();  // Abre a janela de edição como modal



        } catch (IOException e) {
            e.printStackTrace();
            showError("Erro ao abrir a janela de edição: " + title);
        }
    }

    private void confirmDeletion(Object record) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmação de Exclusão");
        alert.setHeaderText("Você tem certeza que deseja excluir este registro?");
        alert.setContentText("Digite a senha do root para confirmar:");


        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Senha do root");


        passwordField.setPrefWidth(200);

        // Adicionando padding ao DialogPane
        alert.getDialogPane().setContent(passwordField);
        alert.getDialogPane().setPadding(new Insets(10));

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String password = passwordField.getText();
                if (isValidRootPassword(password)) {
                    deleteRecord(record);
                } else {
                    showError("Senha incorreta!");
                }
            }
        });
    }


    private boolean isValidRootPassword(String password) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        try {
            return usuarioDAO.authenticateUser("root", password);
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Retorna false em caso de erro
        }
    }

    private void deleteRecord(Object record) {
        if (record instanceof Funcionario) {
            try{
                funcionarioDAO.delete(((Funcionario) record).getId());
                AlertUtil.showSuccessAlert("Registro excluido com sucesso!");
            }
            catch (Exception e){
                AlertUtil.showErrorAlert(e.getMessage());
            }

        } else if (record instanceof Paciente) {
            try{
                pacienteDAO.delete(((Paciente) record).getCpf());
                AlertUtil.showSuccessAlert("Registro excluido com sucesso!");
            }
            catch (Exception e){
                AlertUtil.showErrorAlert(e.getMessage());
            }

        } else if (record instanceof Patologia) {

            try{
                patologiaDAO.delete(((Patologia) record).getCid());
                AlertUtil.showSuccessAlert("Registro excluido com sucesso!");
            }
            catch (Exception e){
                AlertUtil.showErrorAlert(e.getMessage());
            }
        }
        loadRecords(entityComboBox.getValue());
    }

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Classe auxiliar para armazenar dados de cada linha na TableView
    public static class RecordRow {
        private String name;
        private String identifier;
        private HBox actions;

        public RecordRow(String name, String identifier, HBox actions) {
            this.name = name;
            this.identifier = identifier;
            this.actions = actions;
        }

        public String getName() {
            return name;
        }

        public String getIdentifier() {
            return identifier;
        }

        public HBox getActions() {
            return actions;
        }
    }

    private void openWindow(String fxmlPath, String title, Object controller) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Acessa o controlador e define o stage
            // Acessa o controlador e define o stagee
            // Acessa o controlador e define o stagee
            if (controller instanceof AdicionarPacienteController) {
                ((AdicionarPacienteController) controller).setStage(new Stage());
            } else if (controller instanceof AdicionarFuncionarioController) {
                ((AdicionarFuncionarioController) controller).setStage(new Stage());
            } else if (controller instanceof AdicionarPatologiaController) {
                ((AdicionarPatologiaController) controller).setStage(new Stage());
            }

            // Cria um novo Stage para a janela modal
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(adicionarRegistro.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Erro ao abrir a janela: " + title);
        }
    }



    @FXML
    public void onAddRecordClick() {
        String selectedEntity = entityComboBox.getValue();

        if (selectedEntity != null) {
            switch (selectedEntity) {
                case "Paciente":
                    openWindow("/fxml/AdicionarPaciente.fxml", "Adicionar Paciente", new AdicionarPacienteController());
                    break;
                case "Funcionário":
                    openWindow("/fxml/AdicionarFuncionario.fxml", "Adicionar Funcionário", new AdicionarFuncionarioController());
                    break;
                case "Patologia":
                    openWindow("/fxml/AdicionarPatologia.fxml", "Adicionar Patologia", new AdicionarPatologiaController());
                    break;
                default:
                    showError("Entidade não reconhecida!");
            }
        }
        loadRecords(entityComboBox.getValue());
    }






}
