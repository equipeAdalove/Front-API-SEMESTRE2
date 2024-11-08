package com.adalove.api.controller;

import com.adalove.api.model.services.ImagePicker;
import com.adalove.api.model.services.OllamaService;
import io.github.ollama4j.exceptions.OllamaBaseException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class CadastrarFichaController {
    @FXML
    private Label filePathLabel;

    @FXML
    private Button filePicker;

    @FXML
    private Button sendButton;

    @FXML
    private Button removeButton;

    @FXML
    private HBox fileContainer;

    @FXML
    private Label labelText;

    @FXML
    private ComboBox<String> modelComboBox;

    @FXML
    private Button cadastrarSemIAButton;

    private String imagePath;
    private String selectedModel = "minicpm-v";

    private VBox vboxRight;

    private final ImagePicker imagePicker = new ImagePicker();
    private final OllamaService service = new OllamaService();

    @FXML
    private void initialize() {
        modelComboBox.setItems(FXCollections.observableArrayList("moondream", "minicpm-v", "llava:7b", "llava:13b", "llava:34b", "Cadastrar sem IA"));
        modelComboBox.setValue(selectedModel);
        cadastrarSemIAButton.setVisible(false);
    }

    @FXML
    private void handleModeloSelection() {
        selectedModel = modelComboBox.getValue();

        if ("Cadastrar sem IA".equals(selectedModel)) {
            fileContainer.setVisible(false); // Esconde o container de arquivos
            filePicker.setVisible(false);     // Esconde o botão "Selecionar Documento"
            cadastrarSemIAButton.setVisible(true); // Torna o botão "Cadastrar Sem IA" visível
        } else {
            fileContainer.setVisible(true); // Mostra o container de arquivos
            filePicker.setVisible(true);     // Mostra o botão "Selecionar Documento"
            cadastrarSemIAButton.setVisible(false); // Torna o botão "Cadastrar Sem IA" invisível
        }
    }

    @FXML
    private void clickToPickImage() {
        imagePath = imagePicker.getImagePath();

        if (imagePath != null) {
            filePathLabel.setText(imagePath);
            labelText.setText("Imagem carregada.");
            sendButton.setVisible(true);
            removeButton.setVisible(true);
        } else {
            labelText.setText("Nenhuma imagem selecionada.");
            filePathLabel.setText("Nenhum arquivo selecionado.");
            removeButton.setVisible(false);
        }
    }

    @FXML
    private void clickToRemoveFile() {
        imagePath = null;
        filePathLabel.setText("Nenhum arquivo selecionado.");
        labelText.setText("Selecione o documento.");
        sendButton.setVisible(false);
        removeButton.setVisible(false);
    }

    @FXML
    private void clickToSend() {
        try {
            if (imagePath == null || imagePath.isEmpty()) {
                labelText.setText("Por favor, selecione uma imagem antes de enviar.");
                return;
            }

            String resposta = service.getOllamaResponse(imagePath, selectedModel);
            resposta = resposta.replaceAll("\\[|\\]|\"", "").trim();
            String[] dados = resposta.split(",\\s*");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ResultadoFicha.fxml"));
            Parent root = loader.load();

            ResultadoFichaController controller = loader.getController();
            controller.setMainViewController((MainViewController) vboxRight.getScene().getUserData());
            controller.setDados(dados[0], dados[1], dados[2]);

            vboxRight.getChildren().clear();
            vboxRight.getChildren().add(root);

        } catch (OllamaBaseException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Método para configurar o VBox
    public void setVBoxRight(VBox vboxRight) {
        this.vboxRight = vboxRight;
    }

    @FXML
    private void handleCadastrarSemIA() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ResultadoFicha.fxml"));
            Parent root = loader.load();

            ResultadoFichaController controller = loader.getController();
            controller.setMainViewController((MainViewController) vboxRight.getScene().getUserData());

            vboxRight.getChildren().clear();
            vboxRight.getChildren().add(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
