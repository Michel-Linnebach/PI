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

    private static final String REGEX_VALIDO = "^[A-Za-zÀ-ÿ ]+$";

    @FXML
    public void iniciarQuiz() {
        String nome = nomeField.getText().trim();
        String tema = temaField.getText().trim();

        // Reset estilos
        nomeField.setStyle("");
        temaField.setStyle("");
        mensagemLabel.setText("");

        if (nome.isEmpty()) {
            mensagemLabel.setText("Por favor, digite seu nome.");
            nomeField.setStyle("-fx-border-color: #ff4d4d; -fx-border-width: 2;");
            return;
        }
        if (!nome.matches(REGEX_VALIDO)) {
            mensagemLabel.setText("Nome inválido: apenas letras e espaços são permitidos.");
            nomeField.setStyle("-fx-border-color: #ff4d4d; -fx-border-width: 2;");
            return;
        }
        if (tema.isEmpty()) {
            mensagemLabel.setText("Por favor, digite o tema do quiz.");
            temaField.setStyle("-fx-border-color: #ff4d4d; -fx-border-width: 2;");
            return;
        }
        if (!tema.matches(REGEX_VALIDO)) {
            mensagemLabel.setText("Tema inválido: apenas letras e espaços são permitidos.");
            temaField.setStyle("-fx-border-color: #ff4d4d; -fx-border-width: 2;");
            return;
        }
        if (ForbiddenWords.containsProibida(tema)) {
            mensagemLabel.setText("Tema não permitido.");
            temaField.setStyle("-fx-border-color: #ff4d4d; -fx-border-width: 2;");
            return;
        }

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