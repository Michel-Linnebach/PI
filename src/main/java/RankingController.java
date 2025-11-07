import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.application.Platform;
import java.util.List;
import java.util.ArrayList;

public class RankingController {

    @FXML private VBox rankingList;
    @FXML private Button btnAnterior;
    @FXML private Button btnProximo;
    @FXML private Label lblPaginaInfo;

    private List<RankingData> todosRankings = new ArrayList<>();

    private static final int ITENS_POR_PAGINA = 5;
    private int paginaAtual = 0;
    private int totalPaginas = 0;

    @FXML
    public void initialize() {
        System.out.println("✅ RankingController inicializado!");
        btnAnterior.setDisable(true);
        btnProximo.setDisable(true);
        carregarTodosRankings();
    }

    public void carregarTodosRankings() {
        System.out.println("🔄 Carregando todos os rankings...");

        new Thread(() -> {
            try {
                RankingService service = RankingService.getInstance();
                List<RankingData> rankings = service.getAllRankings();

                System.out.println("✅ Ranking geral carregado: " + rankings.size() + " registros");

                Platform.runLater(() -> {
                    todosRankings = rankings;
                    calcularPaginacao();
                    exibirPaginaAtual();
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    Label error = new Label("❌ Erro ao carregar ranking.");
                    error.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
                    rankingList.getChildren().add(error);
                });
            }
        }).start();
    }

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

    private void atualizarBotoesPaginacao() {
        btnAnterior.setDisable(paginaAtual == 0);
        btnProximo.setDisable(paginaAtual >= totalPaginas - 1);

        if (totalPaginas > 0)
            lblPaginaInfo.setText("Página " + (paginaAtual + 1) + " de " + totalPaginas);
        else
            lblPaginaInfo.setText("Sem rankings");
    }

    private void exibirPaginaAtual() {
        rankingList.getChildren().clear();

        System.out.println("➡️ Exibindo página: " + paginaAtual);
        System.out.println("➡️ RankingList antes de adicionar: " + rankingList.getChildren().size());

        if (todosRankings.isEmpty()) {
            Label empty = new Label("📊 Nenhum ranking disponível ainda");
            empty.setStyle("-fx-font-size: 18px; -fx-font-family: 'QuicksandMedium'; -fx-text-fill: #718096; -fx-padding: 30;");
            rankingList.getChildren().add(empty);
            return;
        }

        int inicio = paginaAtual * ITENS_POR_PAGINA;
        int fim = Math.min(inicio + ITENS_POR_PAGINA, todosRankings.size());

        for (int i = inicio; i < fim; i++) {
            RankingData data = todosRankings.get(i);
            int posicao = i;

            HBox item = new HBox();
            item.setSpacing(20);
            item.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            item.setPrefWidth(780);

            if (posicao < 3) {
                item.setStyle("-fx-background-color: #ffeaa7; -fx-background-radius: 14; -fx-padding: 14 28;");
            } else {
                item.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 14; -fx-padding: 14 28;");
            }

            // posição visual
            String posText = (posicao == 0 ? "🥇" :
                             posicao == 1 ? "🥈" :
                             posicao == 2 ? "🥉" :
                             String.valueOf(posicao + 1));

            Label posLabel = new Label(posText);
            posLabel.setStyle("-fx-font-size: 20px; -fx-font-family: 'QuicksandBold'; -fx-min-width: 50; -fx-text-fill: #2d3748;");

            Label name = new Label(data.getNome());
            name.setStyle("-fx-font-size: 17px; -fx-font-family: 'QuicksandSemiBold'; -fx-text-fill: #2d3748;");

            Label tema = new Label("(" + data.getTema() + ")");
            tema.setStyle("-fx-font-size: 13px; -fx-font-family: 'QuicksandMedium'; -fx-text-fill: #718096;");

            javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            Label score = new Label(data.getPontuacao() + " pts");
            score.setStyle("-fx-font-size: 18px; -fx-font-family: 'QuicksandBold'; -fx-text-fill: #667eea;");

            item.getChildren().addAll(posLabel, name, tema, spacer, score);
            rankingList.getChildren().add(item);
        }

        System.out.println("✅ RankingList depois de adicionar: " + rankingList.getChildren().size());

        atualizarBotoesPaginacao();
    }

    @FXML
    private void paginaAnterior() {
        if (paginaAtual > 0) {
            paginaAtual--;
            exibirPaginaAtual();
        }
    }

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
    public void buttonHoverIn(MouseEvent e) {
        if (e.getSource() instanceof Button btn) {
            btn.setStyle(btn.getStyle()
                    + "; -fx-background-color: #7795f8; -fx-scale-x: 1.05; -fx-scale-y: 1.05;");
        }
    }

    @FXML
    public void buttonHoverOut(MouseEvent e) {
        if (e.getSource() instanceof Button btn) {
            btn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white;"
                    + "-fx-font-size: 15px; -fx-font-family: 'QuicksandBold';"
                    + "-fx-background-radius: 12; -fx-padding: 12 32;");
        }
    }
}
