package com.adalove.api.controller;

import com.adalove.api.model.dao.FuncionarioDAO;
import com.adalove.api.model.dao.PacienteDAO;
import com.adalove.api.model.dao.PatologiaDAO;
import com.adalove.api.model.dao.UsuarioDAO;
import com.adalove.api.model.entities.Funcionario;
import com.adalove.api.model.entities.Paciente;
import com.adalove.api.model.entities.Patologia;
import com.adalove.api.model.entities.Usuario;
import com.adalove.api.model.services.AlertUtil;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
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
import javafx.scene.paint.Color;
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

    private Usuario usuario;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;

        if (recordsTableView != null && !recordsTableView.getItems().isEmpty()) {
            loadRecords(entityComboBox.getValue());
        }
    }

    @FXML
    public void initialize() {
        entityComboBox.getItems().addAll("Funcionário", "Paciente", "Patologia", "Usuario");
        entityComboBox.setOnAction(event -> onEntitySelected());

        if (!entityComboBox.getItems().isEmpty()) {
            entityComboBox.getSelectionModel().selectFirst();
            loadRecords(entityComboBox.getValue());
        }

        recordsTableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double totalWidth = newWidth.doubleValue();

            // Ajuste a largura de cada coluna de acordo com a proporção desejada
            idColumn.setPrefWidth(totalWidth * 0.25); // Exemplo: 30% da largura total
            nameColumn.setPrefWidth(totalWidth * 0.45); // Exemplo: 40% da largura total
            actionsColumn.setPrefWidth(totalWidth * 0.30); // Exemplo: 30% da largura total
        });

        // Configuração das colunas da TableView
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIdentifier()));
        actionsColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getActions()));

        // Desabilitar o botão de adicionar caso o usuário não seja administrador
        adicionarRegistro.setDisable(usuario != null && !usuario.isAdministrador());
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
            else if (selectedEntity.equals("Usuario")) {
                List<Usuario> usuarios = usuarioDAO.buscarPorNome(searchText);
                populateTable(usuarios);
            }
        } else {
            assert selectedEntity != null;
            loadRecords(selectedEntity);
        }
    }

    private void loadRecords(String entity) {
        recordsTableView.getItems().clear();

        if (entity.equals("Funcionário")) {
            // Configuração para Funcionário
            nameColumn.setText("Nome");
            idColumn.setText("CRM");
            List<Funcionario> funcionarios = funcionarioDAO.read();
            populateTable(funcionarios);
        } else if (entity.equals("Paciente")) {
            // Configuração para Paciente
            nameColumn.setText("Nome");
            idColumn.setText("CPF");
            List<Paciente> pacientes = pacienteDAO.read();
            populateTable(pacientes);
        } else if (entity.equals("Patologia")) {
            // Configuração para Patologia
            nameColumn.setText("Nome");
            idColumn.setText("Código CID");
            List<Patologia> patologias = patologiaDAO.read();
            populateTable(patologias);
        } else if (entity.equals("Usuario")) {
            // Configuração para Usuario
            nameColumn.setText("Nome de Usuário");
            idColumn.setText("Administrador");
            List<Usuario> usuarios = usuarioDAO.read();
            populateTable(usuarios);
        }

        // Atualiza a tabela após o carregamento
        recordsTableView.refresh();  // Atualiza a TableView para refletir as mudanças
    }



    private void populateTable(List<?> records) {
        for (Object record : records) {
            String name = "";
            String identifier = ""; // Usado para CRM, CPF, CID ou Administrador
            HBox actionButtons = createActionButtons(record);

            if (record instanceof Funcionario) {
                Funcionario funcionario = (Funcionario) record;
                name = funcionario.getNome();
                identifier = funcionario.getCrm();
            } else if (record instanceof Paciente) {
                Paciente paciente = (Paciente) record;
                name = paciente.getNome();
                identifier = paciente.getCpf();
            } else if (record instanceof Patologia) {
                Patologia patologia = (Patologia) record;
                name = patologia.getNome();
                identifier = patologia.getCid();
            } else if (record instanceof Usuario) {
                Usuario usuario = (Usuario) record;
                name = usuario.getUsername();
                identifier = usuario.isAdministrador() ? "Sim" : "Não";
            }

            recordsTableView.getItems().add(new RecordRow(name, identifier, actionButtons));
        }
    }


    private HBox createActionButtons(Object record) {
        // Botão de excluir
        Button deleteButton = new Button();
        FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TIMES); // Ícone "X"
        deleteIcon.setSize("20px");
        deleteIcon.setFill(Color.rgb(255, 255, 255));
        deleteButton.setGraphic(deleteIcon);
        deleteButton.setOnAction(event -> confirmDeletion(record));
        deleteButton.getStyleClass().add("remove-icon-button");

        // Botão de editar
        Button editButton = new Button();
        FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL); // Ícone de lápis
        editIcon.setSize("20px");
        editIcon.setFill(Color.rgb(255, 255, 255));
        editButton.setGraphic(editIcon);
        editButton.setOnAction(event -> editRecord(record));
        editButton.getStyleClass().add("edit-icon-button");

        // Verifica se o usuário é administrador para definir o estado dos botões
        boolean isAdmin = usuario != null && usuario.isAdministrador();
        deleteButton.setDisable(usuario == null || !isAdmin);

        // Verifica se o registro é de um usuário para desabilitar o botão de edição
        if (record instanceof Usuario) {
            editButton.setDisable(true);  // Desabilita o botão "Editar"
        } else {
            editButton.setDisable(!isAdmin);  // Habilita ou desabilita baseado na permissão
        }

        HBox hbox = new HBox(deleteButton);
        if (isAdmin && !(record instanceof Usuario)) {
            hbox.getChildren().add(editButton);
        }
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
                controller.setStage(new Stage());
            } else if (entity instanceof Paciente) {
                EditarPacienteController controller = loader.getController();
                controller.setPaciente((Paciente) entity);
                controller.setStage(new Stage());
            } else if (entity instanceof Patologia) {
                EditarPatologiaController controller = loader.getController();
                controller.setPatologia((Patologia) entity);
                controller.setStage(new Stage());
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
        if (usuario != null && usuario.isAdministrador()) { // Verifique se o usuário é admin
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmação de Exclusão");
            alert.setHeaderText("Você tem certeza que deseja excluir este registro?");
            alert.setContentText("Digite sua senha para confirmar:");

            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Senha do usuário");
            alert.getDialogPane().setContent(passwordField);

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    String password = passwordField.getText();
                    try {
                        if (usuarioDAO.authenticateUser(usuario.getUsername(), password) != null) {
                            deleteRecord(record);
                        } else {
                            showError("Senha incorreta!");
                        }
                    } catch (SQLException e) {
                        showError("Erro ao verificar a senha: " + e.getMessage());
                    }
                }
            });
        } else {
            showError("Somente administradores podem excluir registros.");
        }
    }



    private boolean isValidRootPassword(String password) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        try {
            Usuario usuario = usuarioDAO.authenticateUser("root", password);
            return usuario != null; // Retorna true se o usuário for autenticado (não nulo), false caso contrário
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void deleteRecord(Object record) {
        if (record instanceof Funcionario) {
            try {
                funcionarioDAO.delete(((Funcionario) record).getId());
                AlertUtil.showSuccessAlert("Registro excluído com sucesso!");
            } catch (Exception e) {
                AlertUtil.showErrorAlert(e.getMessage());
            }
        } else if (record instanceof Paciente) {
            try {
                pacienteDAO.delete(((Paciente) record).getCpf());
                AlertUtil.showSuccessAlert("Registro excluído com sucesso!");
            } catch (Exception e) {
                AlertUtil.showErrorAlert(e.getMessage());
            }
        } else if (record instanceof Patologia) {
            try {
                patologiaDAO.delete(((Patologia) record).getCid());
                AlertUtil.showSuccessAlert("Registro excluído com sucesso!");
            } catch (Exception e) {
                AlertUtil.showErrorAlert(e.getMessage());
            }
        } else if (record instanceof Usuario) {
            Usuario usuarioToDelete = (Usuario) record;

            // Verifica se o usuário a ser excluído é o root ou o usuário logado
            if ("root".equals(usuarioToDelete.getUsername()) || usuarioToDelete.getUsername().equals(usuario.getUsername())) {
                AlertUtil.showErrorAlert("Não é possível excluir o usuário root ou o usuário atualmente logado.");
                return; // Interrompe o método se for o root ou o usuário logado
            }

            try {
                usuarioDAO.delete(usuarioToDelete.getUsername());
                AlertUtil.showSuccessAlert("Usuário excluído com sucesso!");
            } catch (Exception e) {
                AlertUtil.showErrorAlert(e.getMessage());
            }
        }

        // Atualiza a tabela após a exclusão
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

            if (controller instanceof AdicionarPacienteController) {
                ((AdicionarPacienteController) controller).setStage(new Stage());
            } else if (controller instanceof AdicionarFuncionarioController) {
                ((AdicionarFuncionarioController) controller).setStage(new Stage());
            } else if (controller instanceof AdicionarPatologiaController) {
                ((AdicionarPatologiaController) controller).setStage(new Stage());
            }


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
        if (usuario == null || !usuario.isAdministrador()) {
            showError("Somente administradores podem adicionar registros.");
            return;
        }

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
                case "Usuario":
                    openWindow("/fxml/AdicionarUsuario.fxml", "Adicionar Usuario", new AdicionarUsuarioController());
                    break;
                default:
                    showError("Entidade não reconhecida!");
            }
        }
        loadRecords(entityComboBox.getValue());
    }


}
