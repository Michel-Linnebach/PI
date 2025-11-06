import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.application.Platform;
import java.util.List;
import java.util.ArrayList;

public class RankingController {
    
    @FXML private VBox rankingList;
    @FXML private Button btnAnterior;
    @FXML private Button btnProximo;
    @FXML private Label lblPaginaInfo;
    
    private String currentTema;
    private List<RankingData> todosRankings = new ArrayList<>();
    
    // Configurações de paginação
    private static final int ITENS_POR_PAGINA = 5;
    private int paginaAtual = 0;
    private int totalPaginas = 0;
    
    /**
     * Método chamado automaticamente pelo JavaFX quando o FXML é carregado
     */
    @FXML
    public void initialize() {
        System.out.println("RankingController inicializado!");
        
        // Desabilitar botões até carregar dados
        btnAnterior.setDisable(true);
        btnProximo.setDisable(true);
        
        // Carregar TODOS os rankings inicialmente (sem filtro de tema)
        carregarTodosRankings();
    }
    
    /**
     * Método para definir o tema e recarregar ranking
     */
    public void setTema(String tema) {
        this.currentTema = tema;
        carregarRankingPorTema();
    }
    
    /**
     * Carrega TODOS os rankings (sem filtro)
     */
    private void carregarTodosRankings() {
        System.out.println("Carregando todos os rankings...");
        
        new Thread(() -> {
            try {
                RankingService service = RankingService.getInstance();
                List<RankingData> rankings = buscarTodosRankings(service);
                
                System.out.println("Rankings encontrados: " + rankings.size());
                
                Platform.runLater(() -> {
                    todosRankings = rankings;
                    calcularPaginacao();
                    exibirPaginaAtual();
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Erro ao carregar rankings: " + e.getMessage());
                
                Platform.runLater(() -> {
                    Label errorLabel = new Label("❌ Erro ao carregar ranking: " + e.getMessage());
                    errorLabel.setStyle("-fx-text-fill: #e53e3e; -fx-font-size: 16px; -fx-font-family: 'QuicksandMedium';");
                    rankingList.getChildren().add(errorLabel);
                });
            }
        }).start();
    }
    
    /**
     * Busca todos os rankings (sem filtro de tema)
     */
    private List<RankingData> buscarTodosRankings(RankingService service) throws Exception {
        var db = com.google.firebase.cloud.FirestoreClient.getFirestore();
        var query = db.collection("Rank").get().get();
        
        List<RankingData> rankings = new ArrayList<>();
        
        for (com.google.cloud.firestore.QueryDocumentSnapshot document : query.getDocuments()) {
            RankingData ranking = document.toObject(RankingData.class);
            rankings.add(ranking);
            System.out.println("Ranking lido: " + ranking.getNome() + " - " + ranking.getPontuacao() + " pontos");
        }
        
        // Ordenar por pontuação (maior primeiro)
        rankings.sort((a, b) -> Integer.compare(b.getPontuacao(), a.getPontuacao()));
        
        return rankings;
    }
    
    /**
     * Carrega ranking filtrado por tema
     */
    private void carregarRankingPorTema() {
        System.out.println("Carregando ranking do tema: " + currentTema);
        
        new Thread(() -> {
            try {
                List<RankingData> rankings = RankingService.getInstance().getRanking(currentTema);
                
                System.out.println("Rankings do tema '" + currentTema + "': " + rankings.size());
                
                Platform.runLater(() -> {
                    todosRankings = rankings;
                    calcularPaginacao();
                    exibirPaginaAtual();
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Erro ao carregar rankings por tema: " + e.getMessage());
                
                Platform.runLater(() -> {
                    Label errorLabel = new Label("❌ Erro ao carregar ranking");
                    errorLabel.setStyle("-fx-text-fill: #e53e3e; -fx-font-size: 16px;");
                    rankingList.getChildren().add(errorLabel);
                });
            }
        }).start();
    }
    
    /**
     * Calcula o número total de páginas
     */
    private void calcularPaginacao() {
        if (todosRankings.isEmpty()) {
            totalPaginas = 0;
            paginaAtual = 0;
        } else {
            totalPaginas = (int) Math.ceil((double) todosRankings.size() / ITENS_POR_PAGINA);
            paginaAtual = 0;
        }
        atualizarBotoesPaginacao();
    }
    
    /**
     * Atualiza o estado dos botões de paginação
     */
    private void atualizarBotoesPaginacao() {
        btnAnterior.setDisable(paginaAtual == 0);
        btnProximo.setDisable(paginaAtual >= totalPaginas - 1);
        
        if (totalPaginas > 0) {
            lblPaginaInfo.setText("Página " + (paginaAtual + 1) + " de " + totalPaginas);
        } else {
            lblPaginaInfo.setText("Sem rankings");
        }
    }
    
    /**
     * Exibe a página atual de rankings
     */
    private void exibirPaginaAtual() {
        rankingList.getChildren().clear();
        
        if (todosRankings == null || todosRankings.isEmpty()) {
            Label emptyLabel = new Label("📊 Nenhum ranking disponível ainda");
            emptyLabel.setStyle("-fx-font-size: 18px; -fx-font-family: 'QuicksandMedium'; " +
                              "-fx-text-fill: #718096; -fx-padding: 30;");
            rankingList.getChildren().add(emptyLabel);
            return;
        }
        
        // Calcular índices da página atual
        int inicio = paginaAtual * ITENS_POR_PAGINA;
        int fim = Math.min(inicio + ITENS_POR_PAGINA, todosRankings.size());
        
        // Exibir apenas os itens da página atual
        for (int i = inicio; i < fim; i++) {
            RankingData data = todosRankings.get(i);
            int posicaoGlobal = i; // Posição global no ranking
            
            HBox itemContainer = new HBox();
            itemContainer.setSpacing(20);
            itemContainer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            itemContainer.setPrefWidth(780);
            
            // Estilo diferenciado para top 3 (considerando posição global)
            if (posicaoGlobal < 3) {
                itemContainer.setStyle("-fx-background-color: linear-gradient(to right, #fff3cd 0%, #ffffff 100%); " +
                                     "-fx-background-radius: 16; -fx-padding: 16 28; " +
                                     "-fx-border-color: #ffc107; -fx-border-width: 3; -fx-border-radius: 16; " +
                                     "-fx-effect: dropshadow(gaussian, #ffc107, 8, 0.3, 0, 2);");
            } else {
                itemContainer.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 14; " +
                                     "-fx-padding: 14 28; -fx-border-color: #e2e8f0; " +
                                     "-fx-border-width: 2.5; -fx-border-radius: 14;");
            }
            
            // Posição (medalha ou número)
            String positionText;
            String positionColor;
            
            switch (posicaoGlobal) {
                case 0:
                    positionText = "🥇";
                    positionColor = "#ffd700";
                    break;
                case 1:
                    positionText = "🥈";
                    positionColor = "#c0c0c0";
                    break;
                case 2:
                    positionText = "🥉";
                    positionColor = "#cd7f32";
                    break;
                default:
                    positionText = String.format("%2d", posicaoGlobal + 1);
                    positionColor = "#667eea";
            }
            
            // Label da posição
            Label posLabel = new Label(positionText);
            posLabel.setStyle("-fx-font-size: 20px; -fx-font-family: 'QuicksandBold'; " +
                            "-fx-text-fill: " + positionColor + "; -fx-min-width: 50; -fx-alignment: center;");
            
            // Nome do jogador
            Label nameLabel = new Label(data.getNome());
            nameLabel.setStyle("-fx-font-size: 17px; -fx-font-family: 'QuicksandSemiBold'; " +
                             "-fx-text-fill: #2d3748; -fx-min-width: 200;");
            
            // Tema
            Label temaLabel = new Label("(" + data.getTema() + ")");
            temaLabel.setStyle("-fx-font-size: 13px; -fx-font-family: 'QuicksandMedium'; " +
                             "-fx-text-fill: #718096; -fx-min-width: 150;");
            
            // Espaçador
            javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
            
            // Pontuação
            Label scoreLabel = new Label(data.getPontuacao() + " pts");
            scoreLabel.setStyle("-fx-font-size: 18px; -fx-font-family: 'QuicksandBold'; " +
                              "-fx-text-fill: #667eea; -fx-background-color: #e8eaf6; " +
                              "-fx-padding: 8 20; -fx-background-radius: 12;");
            
            itemContainer.getChildren().addAll(posLabel, nameLabel, temaLabel, spacer, scoreLabel);
            rankingList.getChildren().add(itemContainer);
        }
        
        atualizarBotoesPaginacao();
    }
    
    /**
     * Ir para página anterior
     */
    @FXML
    private void paginaAnterior() {
        if (paginaAtual > 0) {
            paginaAtual--;
            exibirPaginaAtual();
        }
    }
    
    /**
     * Ir para próxima página
     */
    @FXML
    private void proximaPagina() {
        if (paginaAtual < totalPaginas - 1) {
            paginaAtual++;
            exibirPaginaAtual();
        }
    }
    
    @FXML
    private void voltarAoInicio() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            Stage stage = (Stage) rankingList.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Bem-vindo ao Quiz");
            stage.setFullScreen(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void buttonHoverIn(MouseEvent event) {
        if (event.getSource() instanceof Button) {
            Button button = (Button) event.getSource();
            button.setStyle(button.getStyle() + 
                          "; -fx-background-color: #7795f8;" +
                          " -fx-scale-x: 1.05; -fx-scale-y: 1.05;" +
                          " -fx-effect: dropshadow(gaussian, #667eea, 15, 0.5, 0, 6);");
        }
    }

    @FXML
    public void buttonHoverOut(MouseEvent event) {
        if (event.getSource() instanceof Button) {
            Button button = (Button) event.getSource();
            button.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; " +
                          "-fx-font-size: 15px; -fx-font-family: 'QuicksandBold'; " +
                          "-fx-background-radius: 12; -fx-padding: 12 32; " +
                          "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, #667eea, 12, 0.35, 0, 4); " +
                          "-fx-border-width: 0;");
        }
    }
}