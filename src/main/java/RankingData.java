public class RankingData implements Comparable<RankingData> {
    private String nome;
    private String tema;
    private int pontuacao;
    private String docId; // Para referência do Firestore

    // Construtor padrão (necessário para Firestore)
    public RankingData() {
    }

    // Construtor com parâmetros
    public RankingData(String nome, String tema, int pontuacao) {
        this.nome = nome;
        this.tema = tema;
        this.pontuacao = pontuacao;
    }

    // Construtor completo incluindo docId
    public RankingData(String nome, String tema, int pontuacao, String docId) {
        this.nome = nome;
        this.tema = tema;
        this.pontuacao = pontuacao;
        this.docId = docId;
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public String getTema() {
        return tema;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public String getDocId() {
        return docId;
    }

    // Setters
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    @Override
    public int compareTo(RankingData other) {
        // Ordenar por pontuação em ordem decrescente
        return Integer.compare(other.getPontuacao(), this.pontuacao);
    }
}