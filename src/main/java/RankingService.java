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

    private static volatile RankingService instance;
    private Firestore db;

    private RankingService() throws IOException {
        inicializarFirebase();
    }

    public static RankingService getInstance() throws IOException {
        if (instance == null) {
            synchronized (RankingService.class) {
                if (instance == null) {
                    instance = new RankingService();
                }
            }
        }
        return instance;
    }

    /**
     * Inicializa o Firebase apenas uma vez
     */
    private void inicializarFirebase() throws IOException {

        synchronized (RankingService.class) {

            if (FirebaseApp.getApps().isEmpty()) {

                InputStream serviceAccount = getClass().getClassLoader()
                        .getResourceAsStream("superquiz-b1a51-firebase-adminsdk-fbsvc-610ddb1c33.json");

                if (serviceAccount == null)
                    throw new IOException("Arquivo de credenciais não encontrado!");

                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setProjectId("superquiz-b1a51")
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase inicializado com sucesso!");

            } else {
                System.out.println("⚠️ Firebase já estava inicializado — ignorado.");
            }
        }

        db = FirestoreClient.getFirestore();
    }

    /**
     * SALVAR RANKING
     */
    public void salvarRanking(String nome, String tema, int pontuacao)
            throws ExecutionException, InterruptedException {

        RankingData ranking = new RankingData(nome, tema, pontuacao);
        db.collection("Rank").document().set(ranking).get();

        System.out.println("✅ Ranking salvo: " + nome + " | " + tema + " | " + pontuacao);
    }

    public List<RankingData> getRanking(String tema)
            throws ExecutionException, InterruptedException {

        var query = db.collection("Rank")
                .whereEqualTo("tema", tema)
                .get()
                .get();

        List<RankingData> rankings = new ArrayList<>();

        for (QueryDocumentSnapshot doc : query.getDocuments()) {
            rankings.add(doc.toObject(RankingData.class));
        }

        rankings.sort(Comparator.comparingInt(RankingData::getPontuacao).reversed());
        return rankings;
    }

    public List<RankingData> getAllRankings()
            throws ExecutionException, InterruptedException {

        var query = db.collection("Rank")
                .get()
                .get();

        List<RankingData> rankings = new ArrayList<>();

        for (QueryDocumentSnapshot doc : query.getDocuments()) {
            rankings.add(doc.toObject(RankingData.class));
        }

        rankings.sort(Comparator.comparingInt(RankingData::getPontuacao).reversed());

        System.out.println("✅ Ranking geral carregado: " + rankings.size() + " registros");
        return rankings;
    }
}
