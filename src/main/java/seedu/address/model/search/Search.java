package seedu.address.model.search;

import static java.util.Objects.requireNonNull;

import java.util.Locale;

/**
 * Contains methods for matching keywords in text
 */
public class Search {

    /**
     * Returns whether the entire key matches any contiguous substring of text within the edit limit.
     * Case is ignored, but spaces count as characters. Text outside the match costs no edits.
     * An empty key always matches. An empty substring can match by deleting all key characters.
     *
     * @param text The body of text to search.
     * @param key The query, which may span multiple words.
     * @param maxDistance The maximum number of insertions, deletions, and substitutions allowed.
     * @throws IllegalArgumentException If maxDistance is negative.
     * @throws NullPointerException If either string is null.
     */
    public static boolean substringThenLevenshtein(String text, String key, int maxDistance) {
        requireNonNull(text);
        requireNonNull(key);
        if (maxDistance < 0) {
            throw new IllegalArgumentException("Maximum distance must not be negative");
        }
        if (Search.substring(text, key)) {
            return true;
        }
        return levenshteinDistance(text.toLowerCase(Locale.ROOT), key.toLowerCase(Locale.ROOT), true)
                <= maxDistance;
    }

    /**
     * Returns a boolean of whether some text contains a given substring keyword, regardless of case, including spaces.
     *
     * @param text The body of text to scan.
     * @param key The substring to find from the body of text, if it exists. May contain spaces.
     * @return boolean
     */
    public static boolean substring(String text, String key) {
        return text.toLowerCase(Locale.ROOT).contains(key.toLowerCase(Locale.ROOT));
    }

    /**
     * Computes edit distance with two rows, optionally allowing unmatched text before and after the key.
     * Case and spaces are significant. Each insertion, deletion, or substitution costs one.
     *
     * @param text The body of text to compare.
     * @param key The complete query to match.
     * @param allowPartialMatch Whether to match any substring of text instead of the whole string.
     * @return The minimum number of edits required.
     * @throws NullPointerException If either string is null.
     */
    public static int levenshteinDistance(String text, String key, boolean allowPartialMatch) {
        requireNonNull(text);
        requireNonNull(key);
        int[] previousRow = new int[text.length() + 1];
        int[] currentRow = new int[text.length() + 1];

        // A zero first row lets a substring match start anywhere in the text.
        if (!allowPartialMatch) {
            for (int column = 0; column <= text.length(); column++) {
                previousRow[column] = column;
            }
        }

        for (int row = 1; row <= key.length(); row++) {
            currentRow[0] = row;
            for (int column = 1; column <= text.length(); column++) {
                int substitutionCost = key.charAt(row - 1) == text.charAt(column - 1) ? 0 : 1;
                int substitution = previousRow[column - 1] + substitutionCost;
                int insertion = currentRow[column - 1] + 1;
                int deletion = previousRow[column] + 1;
                currentRow[column] = Math.min(substitution, Math.min(insertion, deletion));
            }
            int[] temporaryRow = previousRow;
            previousRow = currentRow;
            currentRow = temporaryRow;
        }

        int distance = previousRow[text.length()];
        if (allowPartialMatch) {
            // Any endpoint is valid once the entire key has been consumed.
            for (int candidateDistance : previousRow) {
                distance = Math.min(distance, candidateDistance);
            }
        }
        return distance;
    }
}
