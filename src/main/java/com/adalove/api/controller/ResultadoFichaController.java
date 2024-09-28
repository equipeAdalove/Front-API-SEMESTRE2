package com.adalove.api.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ResultadoFichaController {
    @FXML
    private TextField nomeField;

    @FXML
    private TextField cpfField;

    @FXML
    private TextArea observacoesField;

    // Método para inicializar a tela com os dados
    public void setDados(String nome, String cpf, String observacoes) {
        nomeField.setText(nome);
        cpfField.setText(cpf);
        observacoesField.setText(observacoes);
    }

    // Método que será chamado ao clicar no botão salvar
    @FXML
    private void salvar() {
        String nomeEditado = nomeField.getText();
        String cpfEditado = cpfField.getText();
        String observacoesEditadas = observacoesField.getText();

        // Aqui você pode salvar as informações no banco de dados ou fazer outras operações
        System.out.println("Dados salvos: Nome = " + nomeEditado + ", CPF = " + cpfEditado + ", Observações = " + observacoesEditadas);
    }
}
