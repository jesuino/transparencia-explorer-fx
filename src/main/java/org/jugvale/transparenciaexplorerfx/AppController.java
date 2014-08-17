package org.jugvale.transparenciaexplorerfx;

import com.sensedia.transparencia.client.core.TransparenciaClient;
import com.sensedia.transparencia.client.ex.RestException;
import com.sensedia.transparencia.client.resources.Candidato;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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

    Map<String, List<Candidato>> cache;

    private StringProperty estadoSelecionado;
    private StringProperty cargoSelecionado;

    @FXML
    private TableView<Candidato> tblCandidatos;
    @FXML
    private Group grpEstados;
    @FXML
    private Label lblNomeEstado;

    @FXML
    private TableColumn<Candidato, String> colApelido;
    @FXML
    private TableColumn<Candidato, String> colNome;
    @FXML
    private TableColumn<Candidato, String> colPartido;
    @FXML
    private TableColumn<Candidato, String> colNum;

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
    @FXML
    private ImageView imgEstado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inicializaVariveis();
        inicializaColunas();
        configuraCargos();
        configuraEstados();
        estadoSelecionado.set(PRIMEIRO_ESTADO);    
    }

    private void inicializaVariveis() {
        cliente = new TransparenciaClient(CHAVE);
        cache = new HashMap<>();
        estadoSelecionado = new SimpleStringProperty();
        cargoSelecionado = new SimpleStringProperty();
        cargoSelecionado.addListener((chg, v, n) -> {
            carregaCandidatos();
        });
        estadoSelecionado.addListener((chg, v, n) -> {
            novoEstadoSelecionado();
        });
    }

    private void inicializaColunas() {
        colApelido.setCellValueFactory(new PropertyValueFactory<>("apelido"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colPartido.setCellValueFactory(new PropertyValueFactory<>("partido"));
        colNum.setCellValueFactory(new PropertyValueFactory<>("numero"));
    }

    // TODO: Fazer cache
    // TODO: adicionar uma tela que indica que os dados estão carregando para o usuário
    private void carregaCandidatos() {
        try {
            String estado = estadoSelecionado.get();
            String cargo = cargoSelecionado.get();
            estado = estado == null ? PRIMEIRO_ESTADO : estado;
            cargo = cargo == null ? ID_CARGO_GOVERNADOR : cargo;
            List<Candidato> dadosLidos = cliente.getCandidatosByCargo(estado, cargo);
            tblCandidatos.setItems(FXCollections.observableArrayList(dadosLidos));

        } catch (RestException ex) {
            System.out.println(ex.getHttpMessage());
            Logger.getLogger(AppController.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

    private void configuraCargos() {
        btnGovernador.setUserData(ID_CARGO_GOVERNADOR);
        btnSenador.setUserData(ID_CARGO_SENADOR);
        btnDepFederal.setUserData(ID_CARGO_DEP_FEDERAL);
        btnDepEstadual.setUserData(ID_CARGO_DEP_ESTADUAL);
        btnDepDistrital.setUserData(ID_CARGO_DISTRITAL);
        grpCargos.selectToggle(btnGovernador);
        grpCargos.selectedToggleProperty().addListener((obs, o, n) -> {
            if (n != null) {
                cargoSelecionado.set(n.getUserData().toString());
            }
        });
    }

    private void configuraEstados() {
        // quando o usuário clicar no Label, vamos configurar o estado selecionado com o valor do Label
        grpEstados.getChildren().forEach(c -> {
            Label l = (Label) c;
            l.setOnMouseClicked(e -> estadoSelecionado.set(l.getText()));
        });
    }

    public void novoEstadoSelecionado() {
        String siglaEstadoSelecionado = estadoSelecionado.get();
        String nomeEstado = EstadoUtil.nome(siglaEstadoSelecionado);
        lblNomeEstado.setText(nomeEstado);
        // TODO: Cache
        imgEstado.setImage(new Image(EstadoUtil.urlBandeira(siglaEstadoSelecionado)));
        carregaCandidatos();
    }
}
