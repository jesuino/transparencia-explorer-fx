package org.jugvale.transparenciaexplorerfx;

import com.sensedia.transparencia.client.core.TransparenciaClient;
import com.sensedia.transparencia.client.ex.RestException;
import com.sensedia.transparencia.client.resources.Candidato;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class AppController implements Initializable {

    TransparenciaClient cliente;

    final String ID_CARGO_GOVERNADOR = "3";
    final String ID_CARGO_SENADOR = "5";
    final String ID_CARGO_DEP_ESTADUAL = "7";
    final String ID_CARGO_DEP_FEDERAL = "6";
    final String ID_CARGO_DISTRITAL = "8";

    // TODO carregar de arquivo
    final String CHAVE = "lGkuSLiphXo7";
    final String PRIMEIRO_ESTADO = "DF";
    final String STR_GOVERNADORES = "%2d Governadores";
    final String STR_SENADORES = "%2d Senadores";
    final String STR_DEP_ESTADUAIS = "%2d Deputados Estaduais";
    final String STR_DEP_FEDERAIS = "%2d Deputados Federais";
    final String STR_DEP_DISTRITAIS = "%2d Deputados Distritais";

    @FXML
    private TableView<Candidato> tblCandidatos;
    
    @FXML
    private TitledPane pnlDadosEstado;
    @FXML
    private Label lblGovernadores;
    @FXML
    private Label lblSenadores;
    @FXML
    private Label lblDepEstaduais;
    @FXML
    private Label lblDepFederais;
    @FXML
    private Label lblDepDistritais;

    @FXML
    private TableColumn<Candidato, String> colApelido;
    @FXML
    private TableColumn<Candidato, String> colNome;
    @FXML
    private TableColumn<Candidato, String> colPartido;

    @FXML
    private ToggleGroup grpCargos;
    @FXML
    private ToggleButton btnGovernador;
    @FXML
    private ToggleButton btnSenador;
    @FXML
    private ToggleButton btnDepEstadual;
    @FXML
    private ToggleButton btnDepFederal;
    @FXML
    private ToggleButton btnDepDistrital;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inicializaVariveis();
        inicializaColunas();
        carregaCandidatosEstado(PRIMEIRO_ESTADO);
    }

    private void inicializaVariveis() {
        cliente = new TransparenciaClient(CHAVE);
    }

    private void inicializaColunas() {
        colApelido.setCellValueFactory(new PropertyValueFactory<>("apelido"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colPartido.setCellValueFactory(new PropertyValueFactory<>("partido"));
    }

    @FXML
    public void estadoSelecionado(MouseEvent e) {
        Labeled lblClicado = (Labeled) e.getTarget();
        System.out.println(lblClicado.getText());
    }

    // TODO: Fazer cache
    // TODO: adicionar uma tela que indica que os dados estão carregando para o usuário
    private void carregaCandidatosEstado(String estado) {
        try {
            //TODO Carregar todos cargos relevantes pra APP
            // carrega governadores
            List<Candidato> cand = cliente.getCandidatosByCargo(estado, ID_CARGO_GOVERNADOR);
            //TODO: Binding
            lblGovernadores.setText(String.format(STR_GOVERNADORES, cand.size()));
            
            tblCandidatos.setItems(FXCollections.observableArrayList(cand));
           
        } catch (RestException ex) {
            System.out.println(ex.getHttpMessage());
            Logger.getLogger(AppController.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }

    }

}
