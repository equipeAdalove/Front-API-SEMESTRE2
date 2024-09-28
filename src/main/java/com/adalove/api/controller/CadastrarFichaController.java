package com.adalove.api.controller;

import com.adalove.api.model.services.ImagePicker;
import com.adalove.api.model.services.OllamaService;
import io.github.ollama4j.exceptions.OllamaBaseException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
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

    private String imagePath;

    // Adicione uma referência para o VBoxRight da MainView
    private VBox vboxRight;

    // Método para configurar o VBox
    public void setVBoxRight(VBox vboxRight) {
        this.vboxRight = vboxRight;
    }

    // Instanciando ImagePicker e OllamaService no nível da classe
    private final ImagePicker imagePicker = new ImagePicker();
    private final OllamaService service = new OllamaService();

    @FXML
    private void clickToPickImage() {
        imagePath = imagePicker.getImagePath();

        if (imagePath != null) {
            // Exibir o caminho no Label dentro da HBox
            filePathLabel.setText(imagePath);
            labelText.setText("Imagem carregada.");

            // Mostrar o botão "Enviar" e o botão "X"
            sendButton.setVisible(true);
            removeButton.setVisible(true); // Mostrar o botão de remover
        } else {
            // Nenhum arquivo selecionado
            labelText.setText("Nenhuma imagem selecionada.");
            filePathLabel.setText("Nenhum arquivo selecionado.");
            removeButton.setVisible(false); // Esconder o botão de remover
        }
    }

    @FXML
    private void clickToRemoveFile() {
        // Limpar o caminho do arquivo
        imagePath = null;

        // Atualizar o conteúdo do Label na HBox para o estado inicial
        filePathLabel.setText("Nenhum arquivo selecionado.");

        // Reverter o estado do Label superior
        labelText.setText("Selecione o documento.");

        // Ocultar o botão "Enviar" e o botão "X" já que não há arquivo selecionado
        sendButton.setVisible(false);
        removeButton.setVisible(false); // Esconder o botão de remover
    }

    @FXML
    private void clickToSend() {
        try {
            // Verificar se a imagem foi selecionada antes de tentar enviar
            if (imagePath == null || imagePath.isEmpty()) {
                labelText.setText("Por favor, selecione uma imagem antes de enviar.");
                return;
            }



            // Processar a resposta usando o OllamaService
            String resposta = service.getOllamaResponse(imagePath);

            resposta = resposta.replaceAll("\\[|\\]|\"", "").trim(); // Remove [ ] e "
            String[] dados = resposta.split(",\\s*"); // Divide por vírgula, permitindo espaços após a vírgula


            // Carregar a nova tela ResultadoFicha
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ResultadoFicha.fxml"));
            Parent root = loader.load();

            // Passar os dados para o novo controller
            ResultadoFichaController controller = loader.getController();

            controller.setDados(dados[0], dados[1], dados[2]); // Passar nome, cpf, observações

            // Substituir a tela atual pela nova
            vboxRight.getChildren().clear();
            vboxRight.getChildren().add(root);

        } catch (OllamaBaseException | IOException | InterruptedException e) {

            e.printStackTrace();
        }
    }
}