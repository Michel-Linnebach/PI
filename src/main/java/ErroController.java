import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;

public class ErroController {

    @FXML
    private Label pontuacaoFinalLabel;

    private int pontuacaoFinal;
    private String nomeUsuario;
    private String tema;

    /**
     * Método para inicializar a tela de erro com os dados do jogo
     */
    public void inicializarErro(int pontuacao, String nome, String temaQuiz) {
        this.pontuacaoFinal = pontuacao;
        this.nomeUsuario = nome;
        this.tema = temaQuiz;

        // Atualizar label de pontuação
        pontuacaoFinalLabel.setText(pontuacao + " pontos");
        // Não há mais mensagemErroLabel, então não tente acessá-lo!
    }

    /**
     * Botão "Jogar Novamente" - Volta para o quiz com o mesmo tema
     */
    @FXML
    private void jogarNovamente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/quiz.fxml"));
            Parent root = loader.load();

            // Inicializar novo quiz com os mesmos dados
            Controller quizController = loader.getController();
            quizController.iniciarQuiz(nomeUsuario, tema);

            // Aplicar CSS
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            Stage stage = (Stage) pontuacaoFinalLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Quiz - " + nomeUsuario + " (" + tema + ")");
            stage.setFullScreen(true);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao reiniciar o quiz: " + e.getMessage());
        }
    }

    /**
     * Botão "Menu Principal" - Volta para a tela de login
     */
    @FXML
    private void voltarMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            Stage stage = (Stage) pontuacaoFinalLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Bem-vindo ao Quiz");
            stage.setFullScreen(true);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao voltar ao menu: " + e.getMessage());
        }
    }

    @FXML
    private void verRanking() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ranking.fxml"));
            Parent root = loader.load();

            // Obter controller e filtrar por tema
            RankingController controller = loader.getController();
            controller.setTema(tema); // ← Adicione esta linha

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            Stage stage = (Stage) pontuacaoFinalLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Ranking - " + tema);
            stage.setFullScreen(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void buttonHoverIn(MouseEvent event) {
        if (event.getSource() instanceof Button) {
            Button button = (Button) event.getSource();
            String style = button.getStyle();

            if (style.contains("#667eea")) { // Botão azul (Jogar Novamente)
                button.setStyle(style.replace("#667eea", "#7795f8"));
            } else if (style.contains("#f7fafc")) { // Botão branco (Menu Principal)
                button.setStyle(style.replace("#f7fafc", "#ffffff")
                        .replace("dropshadow(gaussian, #667eea, 14, 0.4, 0, 5)",
                                "dropshadow(gaussian, #667eea, 18, 0.5, 0, 8)"));
            } else if (style.contains("#ffd93d")) { // Botão amarelo (Ver Ranking)
                button.setStyle(style.replace("#ffd93d", "#ffeb3b"));
            }
        }
    }

    @FXML
    public void buttonHoverOut(MouseEvent event) {
        if (event.getSource() instanceof Button) {
            Button button = (Button) event.getSource();
            String style = button.getStyle();

            if (style.contains("#7795f8")) { // Botão azul (Jogar Novamente)
                button.setStyle(style.replace("#7795f8", "#667eea"));
            } else if (style.contains("#ffffff")) { // Botão branco (Menu Principal)
                button.setStyle(style.replace("#ffffff", "#f7fafc")
                        .replace("dropshadow(gaussian, #667eea, 18, 0.5, 0, 8)",
                                "dropshadow(gaussian, #667eea, 14, 0.4, 0, 5)"));
            } else if (style.contains("#ffeb3b")) { // Botão amarelo (Ver Ranking)
                button.setStyle(style.replace("#ffeb3b", "#ffd93d"));
            }
        }
    }
}