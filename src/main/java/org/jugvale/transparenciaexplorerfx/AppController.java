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
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class AppController implements Initializable {

    TransparenciaClient cliente;

    // Para a paginação
    final int NUM_RES = 20;

    final String ID_CARGO_GOVERNADOR = "3";
    final String ID_CARGO_SENADOR = "5";
    final String ID_CARGO_DEP_ESTADUAL = "7";
    final String ID_CARGO_DEP_FEDERAL = "6";
    final String ID_CARGO_DISTRITAL = "8";

    // TODO carregar de arquivo
    final String CHAVE = "lGkuSLiphXo7";
    final String PRIMEIRO_ESTADO = "DF";

    // Um cache bem simples para os dados dos candidatos... 
    // Pode crescer muito e dar estouro de memória na aplicação
    Map<String, List<Candidato>> cache;

    private StringProperty estadoSelecionado;
    private StringProperty cargoSelecionado;
    private BooleanProperty carregando;
    private BooleanProperty erro;

    @FXML
    private TableView<Candidato> tblCandidatos;
    @FXML
    private ProgressIndicator prgCarregando;
    @FXML
    Label lblErroBusca;
    @FXML
    private Pane pnlPaginador;
    // Ainda não tem no meu SceneBuild
    private Pagination paginador;
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
        inicializaPropriedades();
        inicializaColunas();
        configuraCargos();
        configuraEstados();
        estadoSelecionado.set(PRIMEIRO_ESTADO);
    }

    private void inicializaPropriedades() {
        cliente = new TransparenciaClient(CHAVE);
        cache = new HashMap<>();
        estadoSelecionado = new SimpleStringProperty();
        cargoSelecionado = new SimpleStringProperty();
        carregando = new SimpleBooleanProperty();
        erro = new SimpleBooleanProperty();
        // a minha versão do SceneBuilder ainda não tem um paginador...
        paginador = new Pagination(Pagination.INDETERMINATE);
        paginador.setMinWidth(600);
        pnlPaginador.getChildren().setAll(paginador);
        cargoSelecionado.addListener((chg, v, n) -> {
            paginador.setCurrentPageIndex(0);
            carregaCandidatos();
        });
        estadoSelecionado.addListener((chg, v, n) -> {
            paginador.setCurrentPageIndex(0);
            novoEstadoSelecionado();
        });
        paginador.currentPageIndexProperty().addListener((chg, v, n) -> {
            // BUG: Será chamado duas vezes a carga
            carregaCandidatos();
        });
        
        lblErroBusca.visibleProperty().bind(erro);
        prgCarregando.visibleProperty().bind(carregando);
        tblCandidatos.disableProperty().bind(carregando.or(erro));
        paginador.disableProperty().bind(carregando.or(erro));
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
        carregando.set(true);
        erro.set(false);
        new Thread(new Task<List<Candidato>>() {

            @Override
            protected List<Candidato> call() throws Exception {
                String estado = estadoSelecionado.get();
                String cargo = cargoSelecionado.get();
                estado = estado == null ? PRIMEIRO_ESTADO : estado;
                cargo = cargo == null ? ID_CARGO_GOVERNADOR : cargo;
                return busca(estado, cargo);
            }

            @Override
            protected void succeeded() {
                List<Candidato> dadosLidos = this.getValue();
                tblCandidatos.setItems(FXCollections.observableArrayList(dadosLidos));
                carregando.set(false);
                erro.set(false);
            }

            @Override
            protected void failed() {
                super.failed();
                Throwable ex = getException();
                Logger.getLogger(AppController.class.getName()).log(Level.SEVERE, null, ex);
                carregando.set(false);
                erro.set(true);
            }

        }).start();
    }

    private List<Candidato> busca(String estado, String cargo) throws RestException {
        // tenta ver se tem no cache, se não tiver busca novos e coloca no cache
        int paginaAtual = paginador.getCurrentPageIndex();
        String chaveCache = estado + cargo + paginaAtual;
        System.out.println(NUM_RES + " - " + NUM_RES * paginaAtual);
        List<Candidato> res = cache.get(chaveCache);
        if (res == null) {
            // INFELIZMENTE a API não deixa a gente saber quantos candidatos tem..
            // Então eu começo com páginas de 50 candidatos...
            //res = cliente.getCandidatosByCargo(estado, cargo);
            res = cliente.getCandidatos(estado, null, null, cargo, NUM_RES, NUM_RES * paginaAtual);
            cache.put(chaveCache, res);
        }
        return res;
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
        grpEstados.getChildren().forEach(c -> {
            Label l = (Label) c;
            l.setOnMouseClicked(e -> estadoSelecionado.set(l.getText()));
        });
    }

    public void novoEstadoSelecionado() {
        String siglaEstadoSelecionado = estadoSelecionado.get();
        String nomeEstado = EstadoUtil.nome(siglaEstadoSelecionado);
        lblNomeEstado.setText(nomeEstado);
        // TODO: Cache?
        imgEstado.setImage(new Image(EstadoUtil.urlBandeira(siglaEstadoSelecionado)));
        carregaCandidatos();
    }
}
