import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class ErroController {

    @FXML private Label pontuacaoFinalLabel;

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
}