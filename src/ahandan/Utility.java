package ahandan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Utility-Klasse für die Sentiment-Analyse von Texten.
 * Verwendet das Vader-Lexikon und filtert Stop-Wörter.
 *
 * @author Aron Handan
 * @version 2025-04-08
 */
public class Utility {

    private static Set<String> stopWords = null;
    private static Map<String, Double> sentimentLexicon = null;

    /**
     * Analysiert einen Text und berechnet den Sentiment-Score.
     *
     * @param text der zu analysierende Text
     * @return der Gesamt-Sentiment-Score des Textes
     */
    public static double analyzeText(String text) {
        // 1. Stop-Wörter laden (nur einmal)
        if (stopWords == null) {
            stopWords = loadStopWords();
        }

        // 2. Sentiment-Lexikon laden (nur einmal)
        if (sentimentLexicon == null) {
            sentimentLexicon = loadSentimentLexicon();
        }

        // 3. Text in Wörter aufteilen und analysieren
        return calculateSentimentScore(text, stopWords, sentimentLexicon);
    }

    /**
     * Lädt Stop-Wörter aus der SmartStoplist.txt Datei.
     *
     * @return Set mit Stop-Wörtern
     */
    private static Set<String> loadStopWords() {
        Set<String> stopWords = new HashSet<>();

        try (InputStream inputStream = Utility.class.getResourceAsStream("/SmartStoplist.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            if (inputStream == null) {
                System.err.println("SmartStoplist.txt nicht gefunden!");
                return stopWords;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (!line.isEmpty()) {
                    stopWords.add(line);
                }
            }

            System.out.println("Stop-Wörter geladen: " + stopWords.size());

        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Stop-Wörter: " + e.getMessage());
        }

        return stopWords;
    }

    /**
     * Lädt das Sentiment-Lexikon aus der vader_lexicon.txt Datei.
     * Format: Wort[TAB]Sentiment-Score[TAB]weitere_irrelevante_werte
     *
     * @return Map mit Wort -> Sentiment-Score
     */
    private static Map<String, Double> loadSentimentLexicon() {
        Map<String, Double> lexicon = new HashMap<>();

        try (InputStream inputStream = Utility.class.getResourceAsStream("/vader_lexicon.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            if (inputStream == null) {
                System.err.println("vader_lexicon.txt nicht gefunden!");
                return lexicon;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    String[] parts = line.split("\t");
                    if (parts.length >= 2) {
                        String word = parts[0].toLowerCase();
                        try {
                            double sentimentScore = Double.parseDouble(parts[1]);
                            lexicon.put(word, sentimentScore);
                        } catch (NumberFormatException e) {
                            System.err.println("Ungültiger Sentiment-Score für Wort: " + parts[0]);
                        }
                    }
                }
            }

            System.out.println("Sentiment-Lexikon geladen: " + lexicon.size() + " Wörter");

        } catch (IOException e) {
            System.err.println("Fehler beim Laden des Sentiment-Lexikons: " + e.getMessage());
        }

        return lexicon;
    }

    /**
     * Berechnet den Gesamt-Sentiment-Score für einen Text.
     *
     * @param text der Text
     * @param stopWords Set mit Stop-Wörtern
     * @param lexicon Map mit Wort -> Sentiment-Score
     * @return der durchschnittliche Sentiment-Score
     */
    private static double calculateSentimentScore(String text, Set<String> stopWords,
                                                  Map<String, Double> lexicon) {
        if (text == null || text.trim().isEmpty()) {
            return 0.0;
        }

        // Text zu Kleinbuchstaben und in Wörter aufteilen
        String[] words = text.toLowerCase()
                .replaceAll("[^a-zA-Z\\s]", "") // Satzzeichen entfernen
                .split("\\s+");

        double totalSentiment = 0.0;
        int validWordCount = 0;

        for (String word : words) {
            word = word.trim();

            // Leere Wörter überspringen
            if (word.isEmpty()) {
                continue;
            }

            // Stop-Wörter überspringen
            if (stopWords.contains(word)) {
                continue;
            }

            // Sentiment-Score für das Wort suchen
            if (lexicon.containsKey(word)) {
                totalSentiment += lexicon.get(word);
                validWordCount++;
            }
        }

        // Durchschnittlichen Sentiment-Score berechnen
        if (validWordCount == 0) {
            return 0.0; // Neutral, wenn keine relevanten Wörter gefunden
        }

        return totalSentiment / validWordCount;
    }

    /**
     * Hilfsmethode für Debugging - zeigt Analyse-Details eines Textes.
     *
     * @param text der zu analysierende Text
     */
    public static void debugAnalyzeText(String text) {
        System.out.println("=== Sentiment-Analyse Debug ===");
        System.out.println("Text: " + text);

        if (stopWords == null) stopWords = loadStopWords();
        if (sentimentLexicon == null) sentimentLexicon = loadSentimentLexicon();

        String[] words = text.toLowerCase()
                .replaceAll("[^a-zA-Z\\s]", "")
                .split("\\s+");

        System.out.println("Wörter gefunden:");
        for (String word : words) {
            word = word.trim();
            if (word.isEmpty()) continue;

            if (stopWords.contains(word)) {
                System.out.println("  " + word + " -> STOP-WORT (ignoriert)");
            } else if (sentimentLexicon.containsKey(word)) {
                System.out.println("  " + word + " -> " + sentimentLexicon.get(word));
            } else {
                System.out.println("  " + word + " -> nicht im Lexikon");
            }
        }

        double score = analyzeText(text);
        System.out.println("Gesamt-Sentiment: " + score);
        System.out.println("===============================\n");
    }
}