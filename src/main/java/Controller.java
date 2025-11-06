import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class Controller {

    @FXML private Label perguntaLabel;
    @FXML private Button opcaoA;
    @FXML private Button opcaoB;
    @FXML private Button opcaoC;
    @FXML private Button opcaoD;
    @FXML private Label textoA;
    @FXML private Label textoB;
    @FXML private Label textoC;
    @FXML private Label textoD;
    @FXML private Label pontuacaoLabel;
    @FXML private Button voltarBtn;

    private QuizEngine quiz;
    private Pergunta perguntaAtual;
    private String nomeUsuario;
    private String tema;

    @FXML
    public void buttonHoverIn(MouseEvent event) {
        if (event.getSource() instanceof Button) {
            Button button = (Button) event.getSource();
            button.setStyle(button.getStyle().replace("#f8f9fa", "#ffffff")
                          .replace("dropshadow(gaussian, #adb5bd, 5, 0.12, 0, 2)", 
                                 "dropshadow(gaussian, #667eea, 12, 0.4, 0, 4)")
                          .replace("#dee2e6", "#667eea"));
        }
    }

    @FXML
    public void buttonHoverOut(MouseEvent event) {
        if (event.getSource() instanceof Button) {
            Button button = (Button) event.getSource();
            button.setStyle(button.getStyle().replace("#ffffff", "#f8f9fa")
                          .replace("dropshadow(gaussian, #667eea, 12, 0.4, 0, 4)", 
                                 "dropshadow(gaussian, #adb5bd, 5, 0.12, 0, 2)")
                          .replace("#667eea", "#dee2e6"));
        }
    }

    public void iniciarQuiz(String nomeUsuario, String tema) {
        this.nomeUsuario = nomeUsuario;
        this.tema = tema;
        
        quiz = new QuizEngine(tema);
        pontuacaoLabel.setText("Pontuação: 0");
        
        if (voltarBtn != null) {
            voltarBtn.setVisible(false);
            voltarBtn.setManaged(false);
        }
        
        carregarPergunta();
    }

    private void carregarPergunta() {
        try {
            perguntaAtual = quiz.gerarPergunta();
            
            if (perguntaAtual == null) {
                perguntaLabel.setText("Não foi possível gerar mais perguntas.");
                desabilitarOpcoes();
                return;
            }
            
            perguntaLabel.setText(perguntaAtual.getPergunta());
            String[] opcoes = perguntaAtual.getOpcoes();
            
            textoA.setText(opcoes[0]);
            textoB.setText(opcoes[1]);
            textoC.setText(opcoes[2]);
            textoD.setText(opcoes[3]);
            
            habilitarOpcoes();
        } catch (Exception e) {
            perguntaLabel.setText("Erro ao gerar pergunta.");
            e.printStackTrace();
            desabilitarOpcoes();
        }
    }

    @FXML
    private void responder(ActionEvent event) {
        Button clicado = (Button) event.getSource();
        String letra = "";
        if (clicado == opcaoA) letra = "A";
        else if (clicado == opcaoB) letra = "B";
        else if (clicado == opcaoC) letra = "C";
        else if (clicado == opcaoD) letra = "D";

        desabilitarOpcoes();

        boolean correta = quiz.verificarResposta(perguntaAtual, letra);
        System.out.println("Letra selecionada: '" + letra + "' | Correta: " + correta);
        
        if (correta) {
            pontuacaoLabel.setText("Pontuação: " + quiz.getPontos());
            
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> {
                carregarPergunta();
            });
            pause.play();
            
        } else {
            redirecionarParaTelaErro();
        }
    }
    
    private void redirecionarParaTelaErro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/erro.fxml"));
            Parent root = loader.load();

            ErroController erroController = loader.getController();
            erroController.inicializarErro(quiz.getPontos(), nomeUsuario, tema);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            Stage stage = (Stage) perguntaLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Game Over");
            stage.setFullScreen(true);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao carregar tela de erro: " + e.getMessage());
            
            perguntaLabel.setText(" Errou! Pontuação final: " + quiz.getPontos());
            if (voltarBtn != null) {
                voltarBtn.setVisible(true);
                voltarBtn.setManaged(true);
            }
        }
    }

    private void desabilitarOpcoes() {
        opcaoA.setDisable(true);
        opcaoB.setDisable(true);
        opcaoC.setDisable(true);
        opcaoD.setDisable(true);
    }

    private void habilitarOpcoes() {
        opcaoA.setDisable(false);
        opcaoB.setDisable(false);
        opcaoC.setDisable(false);
        opcaoD.setDisable(false);
    }

    @FXML
    private void voltarAoInicio() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            Stage stage = (Stage) perguntaLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Bem-vindo ao Quiz");
            stage.setFullScreen(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}