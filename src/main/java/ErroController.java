import javafx.application.Platform;
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

    public void inicializarErro(int pontuacao, String nome, String temaQuiz) {
    this.pontuacaoFinal = pontuacao;
    this.nomeUsuario = nome;
    this.tema = temaQuiz;

    pontuacaoFinalLabel.setText(pontuacao + " pontos");

    try {
        RankingService.getInstance().salvarRanking(nomeUsuario, tema, pontuacaoFinal);
        System.out.println("✅ Ranking SALVO ao finalizar o quiz!");
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("❌ Falha ao salvar ranking ao finalizar o quiz.");
    }
}


    @FXML
    private void jogarNovamente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/quiz.fxml"));
            Parent root = loader.load();

            Controller quizController = loader.getController();
            quizController.iniciarQuiz(nomeUsuario, tema);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            Stage stage = (Stage) pontuacaoFinalLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Quiz - " + nomeUsuario + " (" + tema + ")");
            stage.setFullScreen(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        }
    }

    @FXML
    private void verRanking() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ranking.fxml"));
            Parent root = loader.load();

            // ✅ Ranking geral — não usa mais setTema()

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            Stage stage = (Stage) pontuacaoFinalLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Ranking Geral");
            stage.setFullScreen(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void buttonHoverIn(MouseEvent e) {
        if (e.getSource() instanceof Button button) {
            String style = button.getStyle();

            if (style.contains("#667eea")) {
                button.setStyle(style.replace("#667eea", "#7795f8"));
            } else if (style.contains("#f7fafc")) {
                button.setStyle(style.replace("#f7fafc", "#ffffff"));
            } else if (style.contains("#ffd93d")) {
                button.setStyle(style.replace("#ffd93d", "#ffeb3b"));
            }
        }
    }

    @FXML
    public void buttonHoverOut(MouseEvent e) {
        if (e.getSource() instanceof Button button) {
            String style = button.getStyle();

            if (style.contains("#7795f8")) {
                button.setStyle(style.replace("#7795f8", "#667eea"));
            } else if (style.contains("#ffffff")) {
                button.setStyle(style.replace("#ffffff", "#f7fafc"));
            } else if (style.contains("#ffeb3b")) {
                button.setStyle(style.replace("#ffeb3b", "#ffd93d"));
            }
        }
    }
}
