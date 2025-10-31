import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
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
    @FXML private Label pontuacaoLabel;
    @FXML private Button voltarBtn;

    private QuizEngine quiz;
    private Pergunta perguntaAtual;
    private String nomeUsuario;
    private String tema;

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
            
            opcaoA.setText(opcoes[0]);
            opcaoB.setText(opcoes[1]);
            opcaoC.setText(opcoes[2]);
            opcaoD.setText(opcoes[3]);
            
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
        //String letra = clicado.getText().substring(0, 1).toUpperCase();

        VBox vboxPai = (VBox) clicado.getParent();

        Label letraLabel = (Label) vboxPai.getChildren().get(0);
        String letra = letraLabel.getText().trim().toUpperCase();

        // Desabilitar todos os botões temporariamente
        desabilitarOpcoes();


        boolean correta = quiz.verificarResposta(perguntaAtual, letra);
        System.out.println("Letra selecionada: '" + letra + "' | Correta: " + correta);
        
        if (correta) {
            //  RESPOSTA CORRETA - Carregar próxima pergunta
            pontuacaoLabel.setText("Pontuação: " + quiz.getPontos());
            
            // Aguardar 1 segundo antes de carregar próxima pergunta
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> {
                carregarPergunta();
            });
            pause.play();
            
        } else {
            //  RESPOSTA ERRADA - Redirecionar para tela de erro
            redirecionarParaTelaErro();
        }
    }
    
    /**
     * Redireciona para a tela de erro quando o jogador erra
     */
    private void redirecionarParaTelaErro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/erro.fxml"));
            Parent root = loader.load();

            // Passar dados para o ErroController
            ErroController erroController = loader.getController();
            erroController.inicializarErro(quiz.getPontos(), nomeUsuario, tema);

            // Aplicar CSS
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            Stage stage = (Stage) perguntaLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Game Over");
            stage.setFullScreen(true);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao carregar tela de erro: " + e.getMessage());
            
            // Fallback: mostrar mensagem no próprio quiz
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