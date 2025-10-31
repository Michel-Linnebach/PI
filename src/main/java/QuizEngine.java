import java.util.HashSet;
import java.util.Set;

public class QuizEngine {
    private final Set<String> perguntasFeitas = new HashSet<>();
    private final Set<String> palavrasProibidas = ForbiddenWords.getDefault();
    private final String tema;
    private int pontos = 0;

    public QuizEngine(String tema) {
        this.tema = tema;
    }

    public Pergunta gerarPergunta() throws Exception {
        int tentativas = 0;
        int maxTentativas = 10;
        while (tentativas < maxTentativas) {
            Pergunta p = GeminiAPI.gerarPergunta(perguntasFeitas, tema, palavrasProibidas);
            if (!perguntasFeitas.contains(p.getPergunta())) {
                perguntasFeitas.add(p.getPergunta());
                return p;
            }
            tentativas++;
        }
        return null;
    }

    public boolean verificarResposta(Pergunta pergunta, String letraUsuario) {
        int indiceCorreto = -1;
        String[] opcoes = pergunta.getOpcoes();
        for (int i = 0; i < opcoes.length; i++) {
            if (opcoes[i].equalsIgnoreCase(pergunta.getResposta())) {
                indiceCorreto = i;
                break;
            }
        }
        String letraCorreta = String.valueOf((char) ('A' + indiceCorreto));
        boolean correta = letraCorreta.equalsIgnoreCase(letraUsuario);
        if (correta) pontos += 100;
        return correta;
    }

    public int getPontos() {
        return pontos;
    }
}
