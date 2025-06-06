package ahandan;

import java.util.Objects;

public class AnalyzedText implements Comparable<AnalyzedText>{

    private String text;
    private double sentiment;

    public AnalyzedText(String text) {
        setText(text);
        this.sentiment = 0.0; // Wird später durch Utility.analyzeText() gesetzt
    }

    public String getText() {
        return text;
    }

    public double getSentiment() {
        return sentiment;
    }

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

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof AnalyzedText that)) return false;

        return Double.compare(getSentiment(), that.getSentiment()) == 0 && Objects.equals(getText(), that.getText());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getText());
        result = 31 * result + Double.hashCode(getSentiment());
        return result;
    }

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
