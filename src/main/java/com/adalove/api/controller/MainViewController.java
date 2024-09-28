package com.adalove.api.controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

public class MainViewController {

    @FXML
    private VBox vboxRight;

    @FXML
    public void loadCadastrarFicha() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CadastrarFicha.fxml"));
            Parent view = loader.load();

            // Passar o VBoxRight para o CadastrarFichaController
            CadastrarFichaController controller = loader.getController();
            controller.setVBoxRight(vboxRight);

            vboxRight.getChildren().clear();
            vboxRight.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para carregar a tela de "Visualizar Registros"
    @FXML
    public void loadVisualizarRegistros() {
        loadView("/fxml/ConsultarRegistro.fxml");
    }

    // Método genérico para carregar um arquivo FXML e substituir a VBox
    private void loadView(String fxmlPath) {
        try {
            // Carregar a nova view
            Parent view = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            // Limpar a VBox atual e adicionar a nova view
            vboxRight.getChildren().clear();
            vboxRight.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}