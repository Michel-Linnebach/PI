import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RankingService {
    private Firestore db;
    private static RankingService instance;

    // Construtor privado para padrão Singleton
    private RankingService() throws IOException {
        inicializarFirebase();
    }

    // Singleton: retorna a mesma instância
    public static RankingService getInstance() throws IOException {
        if (instance == null) {
            instance = new RankingService();
        }
        return instance;
    }

    // Inicializa a conexão com Firebase
    private void inicializarFirebase() throws IOException {
        // Verificar se o Firebase já foi inicializado
        if (FirebaseApp.getApps().isEmpty()) {
            // Carregar as credenciais do arquivo JSON
            InputStream serviceAccount = getClass().getClassLoader()
                    .getResourceAsStream("superquiz-b1a51-firebase-adminsdk-fbsvc-610ddb1c33.json");

            if (serviceAccount == null) {
                throw new IOException("Arquivo de credenciais não encontrado!");
            }

            // Criar credenciais a partir do arquivo JSON
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);

            // Configurar opções do Firebase
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(credentials)
                    .setProjectId("superquiz-b1a51")
                    .build();

            // Inicializar Firebase
            FirebaseApp.initializeApp(options);
        }

        // Obter instância do Firestore
        db = FirestoreClient.getFirestore();
    }

    // Salvar um novo ranking no Firestore
    public void salvarRanking(String nome, String tema, int pontuacao)
            throws ExecutionException, InterruptedException {
        RankingData ranking = new RankingData(nome, tema, pontuacao);

        // Salvar na coleção "Rank" com ID automático
        db.collection("Rank").document().set(ranking).get();

        System.out.println("✓ Ranking salvo com sucesso! Nome: " + nome + ", Tema: " + tema + ", Pontuação: " + pontuacao);
    }

    // Buscar rankings por tema
    public List<RankingData> getRanking(String tema) throws ExecutionException, InterruptedException {
        List<RankingData> rankings = new ArrayList<>();

        // Buscar documentos da coleção "Rank" filtrados por tema
        var query = db.collection("Rank").whereEqualTo("tema", tema).get().get();

        for (QueryDocumentSnapshot document : query.getDocuments()) {
            RankingData ranking = document.toObject(RankingData.class);
            rankings.add(ranking);
        }

        // Ordenar por pontuação (decrescente - maior pontuação primeiro)
        rankings.sort((a, b) -> Integer.compare(b.getPontuacao(), a.getPontuacao()));

        return rankings;
    }

    // Buscar todos os rankings e exibir em tabela
    public void exibirTabelaRanking() throws ExecutionException, InterruptedException {
        List<RankingData> rankings = new ArrayList<>();

        // Buscar todos os documentos da coleção "Rank"
        var query = db.collection("Rank").get().get();

        for (QueryDocumentSnapshot document : query.getDocuments()) {
            RankingData ranking = document.toObject(RankingData.class);
            rankings.add(ranking);
        }

        // Ordenar por pontuação (decrescente - maior pontuação primeiro)
        rankings.sort(Comparator.comparingInt(RankingData::getPontuacao).reversed());

        // Exibir a tabela formatada
        if (rankings.isEmpty()) {
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║           Nenhum ranking disponível no momento            ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");
            return;
        }

        // Cabeçalho da tabela
        System.out.println("\n╔════╦════════════════╦═════════════════╦════════════╗");
        System.out.println("║ #  ║      Nome      ║      Tema       ║  Pontos    ║");
        System.out.println("╠════╬════════════════╬═════════════════╬════════════╣");

        // Exibir cada ranking
        int posicao = 1;
        for (RankingData ranking : rankings) {
            String nome = ranking.getNome();
            String tema = ranking.getTema();
            int pontos = ranking.getPontuacao();

            // Truncar valores se forem muito longos
            if (nome.length() > 14) nome = nome.substring(0, 11) + "...";
            if (tema.length() > 15) tema = tema.substring(0, 12) + "...";

            System.out.printf("║ %2d ║ %-14s ║ %-15s ║ %10d ║%n", posicao, nome, tema, pontos);
            posicao++;
        }

        // Rodapé da tabela
        System.out.println("╚════╩════════════════╩═════════════════╩════════════╝\n");
    }
}