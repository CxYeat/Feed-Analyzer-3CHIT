package ahandan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * FeedAnalyzer - Hauptklasse für die Sentiment-Analyse von Social Media Posts.
 * Liest Nachrichten aus einer CSV-Datei, analysiert deren Sentiment und
 * berechnet einen Gesamt-Sentiment-Score.
 *
 * @author Aron Handan
 * @version 2025-04-08
 */
public class FeedAnalyzer {

    private static final String ACCOUNT_NAME = "Potus";
    private static final String CSV_FILE = "resources/potus_tweets_2017_webarchive_publicaccess.csv";

    /**
     * Hauptmethode des FeedAnalyzer-Programms.
     *
     * @param args Kommandozeilenargumente (nicht verwendet)
     */
    public static void main(String[] args) {
        System.out.println("=== Feed Analyzer gestartet ===");
        System.out.println("Lade und analysiere Nachrichten...\n");

        // Map für die Speicherung: AnalyzedText -> Account Name
        Map<AnalyzedText, String> messageMap = new HashMap<>();

        // 1. Messages aus CSV-Datei einlesen und analysieren
        loadAndAnalyzeMessages(messageMap);

        // 2. Gesamt-Sentiment-Score berechnen und ausgeben
        calculateAndDisplayOverallSentiment(messageMap);

        // 3. Zusätzliche Statistiken ausgeben
        displayStatistics(messageMap);

        System.out.println("\n=== Feed Analyzer beendet ===");
    }

    /**
     * Lädt Messages aus der CSV-Datei und führt Sentiment-Analyse durch.
     *
     * @param messageMap Map zum Speichern der analysierten Nachrichten
     */
    private static void loadAndAnalyzeMessages(Map<AnalyzedText, String> messageMap) {
        int messageCount = 0;
        int analyzedCount = 0;

        try (InputStream inputStream = FeedAnalyzer.class.getResourceAsStream(CSV_FILE);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            if (inputStream == null) {
                System.err.println("Fehler: CSV-Datei nicht gefunden: " + CSV_FILE);
                return;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                messageCount++;
                line = line.trim();

                // Leere Zeilen überspringen
                if (line.isEmpty()) {
                    continue;
                }

                try {
                    // 2. AnalyzedText-Objekt für jede Nachricht erstellen
                    AnalyzedText analyzedText = new AnalyzedText(line);

                    // 3. Sentiment-Score berechnen
                    double sentimentScore = Utility.analyzeText(line);
                    analyzedText.setSentiment(sentimentScore);

                    // 4. In Map speichern (Nachricht -> Account Name)
                    messageMap.put(analyzedText, ACCOUNT_NAME);
                    analyzedCount++;

                    // Fortschritt anzeigen (alle 100 Nachrichten)
                    if (analyzedCount % 100 == 0) {
                        System.out.printf("Analysiert: %d Nachrichten...%n", analyzedCount);
                    }

                } catch (Exception e) {
                    System.err.printf("Fehler bei Nachricht %d: %s%n", messageCount, e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Fehler beim Lesen der CSV-Datei: " + e.getMessage());
            return;
        }

        System.out.printf("Erfolgreich geladen: %d/%d Nachrichten%n%n", analyzedCount, messageCount);
    }

    /**
     * Berechnet den Gesamt-Sentiment-Score und gibt ihn auf der Konsole aus.
     *
     * @param messageMap Map mit den analysierten Nachrichten
     */
    private static void calculateAndDisplayOverallSentiment(Map<AnalyzedText, String> messageMap) {
        if (messageMap.isEmpty()) {
            System.out.println("Keine Nachrichten zur Analyse verfügbar.");
            return;
        }

        // 5. Gesamt-Sentiment-Score berechnen
        double totalSentiment = 0.0;
        int messageCount = messageMap.size();

        for (AnalyzedText analyzedText : messageMap.keySet()) {
            totalSentiment += analyzedText.getSentiment();
        }

        double averageSentiment = totalSentiment / messageCount;

        // Ergebnis auf der Konsole ausgeben
        System.out.println("=== ERGEBNISSE ===");
        System.out.printf("Anzahl analysierter Nachrichten: %d%n", messageCount);
        System.out.printf("Gesamt-Sentiment-Score: %.6f%n", totalSentiment);
        System.out.printf("Durchschnittlicher Sentiment-Score: %.6f%n", averageSentiment);

        // Interpretation des Sentiment-Scores
        String interpretation;
        if (averageSentiment > 0.1) {
            interpretation = "POSITIV";
        } else if (averageSentiment < -0.1) {
            interpretation = "NEGATIV";
        } else {
            interpretation = "NEUTRAL";
        }
        System.out.printf("Gesamtbewertung: %s%n", interpretation);
    }

    /**
     * Zeigt zusätzliche Statistiken über die analysierten Nachrichten.
     *
     * @param messageMap Map mit den analysierten Nachrichten
     */
    private static void displayStatistics(Map<AnalyzedText, String> messageMap) {
        if (messageMap.isEmpty()) {
            return;
        }

        int positiveCount = 0;
        int negativeCount = 0;
        int neutralCount = 0;
        double maxSentiment = Double.MIN_VALUE;
        double minSentiment = Double.MAX_VALUE;
        AnalyzedText mostPositive = null;
        AnalyzedText mostNegative = null;

        for (AnalyzedText analyzedText : messageMap.keySet()) {
            double sentiment = analyzedText.getSentiment();

            // Kategorien zählen
            if (analyzedText.isPositive()) {
                positiveCount++;
            } else if (analyzedText.isNegative()) {
                negativeCount++;
            } else {
                neutralCount++;
            }

            // Extreme finden
            if (sentiment > maxSentiment) {
                maxSentiment = sentiment;
                mostPositive = analyzedText;
            }
            if (sentiment < minSentiment) {
                minSentiment = sentiment;
                mostNegative = analyzedText;
            }
        }

        System.out.println("\n=== DETAILSTATISTIKEN ===");
        System.out.printf("Positive Nachrichten: %d (%.1f%%)%n",
                positiveCount, (positiveCount * 100.0) / messageMap.size());
        System.out.printf("Negative Nachrichten: %d (%.1f%%)%n",
                negativeCount, (negativeCount * 100.0) / messageMap.size());
        System.out.printf("Neutrale Nachrichten: %d (%.1f%%)%n",
                neutralCount, (neutralCount * 100.0) / messageMap.size());

        if (mostPositive != null) {
            System.out.printf("%nPositivste Nachricht (%.6f):%n\"%s\"%n",
                    maxSentiment, truncateText(mostPositive.getText(), 100));
        }

        if (mostNegative != null) {
            System.out.printf("%nNegativste Nachricht (%.6f):%n\"%s\"%n",
                    minSentiment, truncateText(mostNegative.getText(), 100));
        }
    }

    /**
     * Kürzt einen Text auf die angegebene Länge und fügt "..." hinzu.
     *
     * @param text der zu kürzende Text
     * @param maxLength maximale Länge
     * @return der gekürzte Text
     */
    private static String truncateText(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}