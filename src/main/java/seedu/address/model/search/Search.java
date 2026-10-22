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
}
