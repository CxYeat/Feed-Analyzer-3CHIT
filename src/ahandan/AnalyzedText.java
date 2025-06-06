package ahandan;

import java.util.Objects;
/**
 * Die Klasse {@code AnalyzedText} repräsentiert einen Text, der hinsichtlich seiner Stimmung
 * (Sentiment) analysiert wurde. Das Sentiment wird als numerischer Score im Bereich [-1.0, 1.0] dargestellt:
 * <ul>
 *     <li><b>Negativ</b>: sentiment &lt; 0</li>
 *     <li><b>Neutral</b>: sentiment == 0</li>
 *     <li><b>Positiv</b>: sentiment &gt; 0</li>
 * </ul>
 * Die Klasse implementiert {@code Comparable}, um Objekte nach Sentiment-Wert und ggf. nach Text zu vergleichen.
 * <p>
 * Die Objekte eignen sich zur Verwendung in Collections wie {@code Map} oder {@code Set}.
 * </p>
 *
 * @author Aron
 * @version 2025-04-08
 */

public class AnalyzedText implements Comparable<AnalyzedText>{

    private String text;
    private double sentiment;

    /**
     * Konstruktor zum Erstellen eines neuen {@code AnalyzedText}-Objekts mit gegebenem Text.
     * Der Sentiment-Wert wird initial auf 0.0 gesetzt.
     *
     * @param text der zu analysierende Text
     */
    public AnalyzedText(String text) {
        setText(text);
        this.sentiment = 0.0; // Wird später durch Utility.analyzeText() gesetzt
    }

    /**
     * Gibt den ursprünglichen Text zurück.
     *
     * @return der Text
     */
    public String getText() {
        return text;
    }

    /**
     * Gibt den zugehörigen Sentiment-Wert zurück.
     *
     * @return Sentiment-Score im Bereich [-1.0, 1.0]
     */
    public double getSentiment() {
        return sentiment;
    }

    /**
     * Setzt den Text. Wenn {@code null} übergeben wird, wird ein leerer String verwendet.
     *
     * @param text der neue Text
     */
    public void setText(String text) {
        this.text = text != null ? text : "";
    }

    public void setSentiment(double sentiment) {
        // Sentiment sollte zwischen -1 und 1 liegen
        if (sentiment < -1.0) {
            this.sentiment = -1.0;
        } else if (sentiment > 1.0) {
            this.sentiment = 1.0;
        } else {
            this.sentiment = sentiment;
        }
    }

    /**
     * Setzt den Sentiment-Wert. Werte außerhalb des Bereichs [-1.0, 1.0] werden begrenzt.
     *
     * @param sentiment neuer Sentiment-Wert
     */
    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof AnalyzedText that)) return false;

        return Double.compare(getSentiment(), that.getSentiment()) == 0 && Objects.equals(getText(), that.getText());
    }

    /**
     * Vergleicht dieses Objekt mit einem anderen {@code AnalyzedText}-Objekt anhand
     * des Sentiment-Werts. Wenn die Sentiment-Werte gleich sind, erfolgt der Vergleich über den Textinhalt.
     *
     * @param other das andere Objekt zum Vergleichen
     * @return negativer, null oder positiver Wert entsprechend der Vergleichsreihenfolge
     * @throws NullPointerException wenn {@code other} null ist
     */
    @Override
    public int hashCode() {
        int result = Objects.hashCode(getText());
        result = 31 * result + Double.hashCode(getSentiment());
        return result;
    }

    /**
     * Gibt {@code true} zurück, wenn dieser Text ein positives Sentiment aufweist.
     *
     * @return {@code true} wenn Sentiment > 0
     */
    @Override
    public String toString() {
        return "AnalyzedText{" +
                "text='" + text + '\'' +
                ", sentiment=" + sentiment +
                '}';
    }

    /**
     * Vergleicht AnalyzedText-Objekte basierend auf ihrem Sentiment-Score.
     * Negative Werte bedeuten, dass dieses Objekt einen niedrigeren Sentiment-Score hat.
     * Positive Werte bedeuten, dass dieses Objekt einen höheren Sentiment-Score hat.
     *
     * @param other das andere AnalyzedText-Objekt
     * @return negativer Wert, 0, oder positiver Wert
     */
    @Override
    public int compareTo(AnalyzedText other) {
        if (other == null) {
            throw new NullPointerException("Cannot compare to null");
        }

        // Primär nach Sentiment-Score sortieren
        int sentimentComparison = Double.compare(this.sentiment, other.sentiment);

        // Falls Sentiment-Scores gleich sind, nach Text sortieren
        if (sentimentComparison == 0) {
            return this.text.compareTo(other.text);
        }

        return sentimentComparison;
    }

    /**
     * Gibt zurück, ob dieser Text ein positives Sentiment hat.
     *
     * @return true wenn Sentiment > 0
     */
    public boolean isPositive() {
        return sentiment > 0;
    }

    /**
     * Gibt zurück, ob dieser Text ein negatives Sentiment hat.
     *
     * @return true wenn Sentiment < 0
     */
    public boolean isNegative() {
        return sentiment < 0;
    }

    /**
     * Gibt zurück, ob dieser Text ein neutrales Sentiment hat.
     *
     * @return true wenn Sentiment == 0
     */
    public boolean isNeutral() {
        return Double.compare(sentiment, 0.0) == 0;
    }
}
