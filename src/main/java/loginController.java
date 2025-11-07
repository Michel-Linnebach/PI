import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class loginController {

    @FXML private TextField nomeField;
    @FXML private TextField temaField;
    @FXML private Label mensagemLabel;

    // Regex ajustada: letras, espaços e acentos
    private static final String REGEX_VALIDO = "^[A-Za-zÀ-ÿ ]+$";

    @FXML
    public void iniciarQuiz() {
        String nome = nomeField.getText().trim();
        String tema = temaField.getText().trim();

        // Reset estilos
        nomeField.setStyle("");
        temaField.setStyle("");
        mensagemLabel.setText("");

        // ✅ Validação do NOME
        if (nome.isEmpty()) {
            mensagemLabel.setText("Por favor, digite seu nome.");
            nomeField.setStyle("-fx-border-color: #ff4d4d; -fx-border-width: 2;");
            return;
        }
        if (nome.length() < 3) {
            mensagemLabel.setText("O nome deve ter pelo menos 3 caracteres.");
            nomeField.setStyle("-fx-border-color: #ff4d4d; -fx-border-width: 2;");
            return;
        }
        if (!nome.matches(REGEX_VALIDO)) {
            mensagemLabel.setText("Nome inválido: use apenas letras e espaços.");
            nomeField.setStyle("-fx-border-color: #ff4d4d; -fx-border-width: 2;");
            return;
        }

        // ✅ Validação do TEMA (correções importantes aqui)
        if (tema.isEmpty()) {
            mensagemLabel.setText("Por favor, digite o tema do quiz.");
            temaField.setStyle("-fx-border-color: #ff4d4d; -fx-border-width: 2;");
            return;
        }
        if (tema.length() < 3) {
            mensagemLabel.setText("O tema deve ter pelo menos 3 caracteres.");
            temaField.setStyle("-fx-border-color: #ff4d4d; -fx-border-width: 2;");
            return;
        }
        if (!tema.matches(REGEX_VALIDO)) {
            mensagemLabel.setText("Tema inválido: use apenas letras e espaços.");
            temaField.setStyle("-fx-border-color: #ff4d4d; -fx-border-width: 2;");
            return;
        }

        // ✅ Palavras proibidas
        if (ForbiddenWords.containsProibida(tema)) {
            mensagemLabel.setText("Tema não permitido.");
            temaField.setStyle("-fx-border-color: #ff4d4d; -fx-border-width: 2;");
            return;
        }

        // ✅ Se tudo OK → iniciar quiz
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/quiz.fxml"));
            Parent root = loader.load();

            Controller quizController = loader.getController();
            quizController.iniciarQuiz(nome, tema);

            Stage stage = (Stage) nomeField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Quiz - " + nome + " (" + tema + ")");
            stage.setFullScreen(true);

        } catch (Exception e) {
            e.printStackTrace();
            mensagemLabel.setText("Erro ao iniciar o quiz.");
        }
    }
}
