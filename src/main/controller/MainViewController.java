package com.adalove.api.controller;


import com.adalove.api.model.entities.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;

import java.awt.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import java.util.Objects;

public class MainViewController {

    @FXML
    private VBox vboxRight;

    @Setter
    private Usuario usuario;

    @FXML
    public void loadCadastrarFicha() {
        if (usuario != null && usuario.isAdministrador()) {
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
        } else {
            // Exibir alerta informando que o usuário não tem permissão para acessar essa funcionalidade
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Acesso Restrito");
            alert.setHeaderText(null);
            alert.setContentText("Você não tem permissão para cadastrar novos documentos.");
            alert.showAndWait();
        }
    }


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


            if (fxmlPath.equals("/fxml/ConsultarDatabases.fxml")) {
                ConsultarDatabasesController controller = loader.getController();
                controller.setUsuario(usuario);
            }else if(fxmlPath.equals("/fxml/ConsultarRegistro.fxml")){
                ConsultarRegistroController controller = loader.getController();
                controller.setUsuario(usuario);
            }


            vboxRight.getChildren().clear();
            vboxRight.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void logout() {
        try {
            // Carregar a tela de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent loginView = loader.load();

            // Obter o palco (stage) atual e definir a cena para o LoginView
            Stage currentStage = (Stage) vboxRight.getScene().getWindow();
            currentStage.setScene(new Scene(loginView));
            currentStage.setTitle("MindDoc Analyzer - Login");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void handleHelpButtonClick() {
        try {
            URI pdfUri = Objects.requireNonNull(getClass().getResource("/documents/Manual do Usuário.pdf")).toURI();


            File pdfFile = new File(pdfUri);


            if (pdfFile.exists()) {
                Desktop.getDesktop().browse(pdfFile.toURI());
            } else {
                System.out.println("Arquivo não encontrado!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}