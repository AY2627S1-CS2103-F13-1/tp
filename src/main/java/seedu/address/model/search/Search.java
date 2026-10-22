package seedu.address.model.search;

/**
 * Contains methods for matching keywords in text
 */
public class Search {

    /**
     * Returns a boolean of whether some text contains a given substring keyword, regardless of case, including spaces.
     *
     * @param text The body of text to scan.
     * @param key The substring to find from the body of text, if it exists. May contain spaces.
     * @return boolean
     */
    public static boolean substring(String text, String key) {
        return text.toLowerCase().contains(key.toLowerCase());
    }

    /**
     * Returns a boolean of whether some text contains the letters in the key in the same order, excluding spaces.
     *
     * @param text The body of text to scan.
     * @param key The letters to find from the body of text, in that order, if it exists. Spaces are ignored.
     * @return
     */
    public static boolean fuzzyLetters(String text, String key) {
        String textLowerCaseNoSpaces = text.toLowerCase().replace(" ", "");
        String keyLowerCaseNoSpaces = key.toLowerCase().replace(" ", "");

        char[] textArray = textLowerCaseNoSpaces.toCharArray();
        char[] keyArray = keyLowerCaseNoSpaces.toCharArray();
        int t = 0, k = 0; // indices for textArray and keyArray respectively
        int matchCount = 0; // number of letters from text that match the key

        while (t < textArray.length && k < keyArray.length) {
            if (textArray[t] == keyArray[k]) {
                matchCount++;
                k++;
            }
            t++;
        }

        IO.println("text: " + text);
        IO.println("key: " + key);
        IO.println("matchCount: " + matchCount);

        return matchCount == keyArray.length;
    }
}
