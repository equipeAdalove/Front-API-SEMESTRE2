package com.adalove.api.controller;


import com.adalove.api.model.entities.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

public class MainViewController {

    @FXML
    private VBox vboxRight;

    private Usuario usuario;

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @FXML
    public void loadCadastrarFicha() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CadastrarFicha.fxml"));
            Parent view = loader.load();

            // Passar o VBoxRight para o CadastrarFichaController
            CadastrarFichaController controller = loader.getController();
            controller.setVBoxRight(vboxRight);

            view.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/application.css")).toExternalForm());

            vboxRight.getChildren().clear();
            vboxRight.getChildren().add(view);
            view.getScene().setUserData(this);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para carregar a tela de "Visualizar Registros"
    @FXML
    public void loadVisualizarRegistros() {
        loadView("/fxml/ConsultarRegistro.fxml");
    }

    @FXML
    public void loadVisualizarDatabases() {
        loadView("/fxml/ConsultarDatabases.fxml");
    }


    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            Parent view = loader.load();

            // Obter o controlador
            if (fxmlPath.equals("/fxml/ConsultarDatabases.fxml")) {
                ConsultarDatabasesController controller = loader.getController();
                controller.setUsuario(usuario); // Passar o usuario para o controlador
            }else if(fxmlPath.equals("/fxml/ConsultarRegistro.fxml")){
                ConsultarRegistroController controller = loader.getController();
                controller.setUsuario(usuario);
            }

            // Limpar a VBox atual e adicionar a nova view
            vboxRight.getChildren().clear();
            vboxRight.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}