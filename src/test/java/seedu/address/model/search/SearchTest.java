package seedu.address.model.search;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class SearchTest {

    @Test
    public void substring_matchingKey_returnsTrue() {
        assertTrue(Search.substring("Alex Yeoh", "Alex Yeoh"));
        assertTrue(Search.substring("Alex Yeoh", "Alex"));
        assertTrue(Search.substring("Alex Yeoh", "lex"));
        assertTrue(Search.substring("Alex Yeoh", "Yeoh"));
    }

    @Test
    public void substring_differentCase_returnsTrue() {
        assertTrue(Search.substring("Alex Yeoh", "aLEX yEOH"));
        assertTrue(Search.substring("ALEX YEOH", "lex"));
        assertTrue(Search.substring("alex yeoh", "YEO"));
    }

    @Test
    public void substring_nonMatchingKey_returnsFalse() {
        assertFalse(Search.substring("Alex Yeoh", "Bob"));
        assertFalse(Search.substring("Alex Yeoh", "Alex Yeoh Tan"));
        assertFalse(Search.substring("Alex Yeoh", "Yeoh Alex"));
        assertFalse(Search.substring("Alex Yeoh", "AlYe"));
    }

    @Test
    public void substring_keyWithSpaces_matchesSpacesExactly() {
        assertTrue(Search.substring("Alex Yeoh", "LEX YEO"));
        assertTrue(Search.substring("Alex Yeoh", " "));
        assertFalse(Search.substring("Alex Yeoh", "Alex  Yeoh"));
        assertFalse(Search.substring("Alex Yeoh", " Alex"));
        assertFalse(Search.substring("Alex Yeoh", "Yeoh "));
        assertFalse(Search.substring("Alex  Yeoh", "Alex Yeoh"));
    }

    @Test
    public void substring_numbersAndSymbols_matchesLiterally() {
        assertTrue(Search.substring("91234567", "2345"));
        assertTrue(Search.substring("alex@example.com", "@EXAMPLE."));
        assertFalse(Search.substring("alex@example.com", "example.*"));
        assertFalse(Search.substring("alex@example.com", "example+com"));
    }

    @Test
    public void substring_emptyKey_returnsTrue() {
        assertTrue(Search.substring("Alex Yeoh", ""));
        assertTrue(Search.substring("", ""));
    }

    @Test
    public void substring_emptyTextWithNonEmptyKey_returnsFalse() {
        assertFalse(Search.substring("", "Alex"));
        assertFalse(Search.substring("", " "));
    }

    @Test
    public void substring_nullText_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Search.substring(null, "Alex"));
    }

    @Test
    public void substring_nullKey_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Search.substring("Alex Yeoh", null));
    }

    @Test
    public void fuzzyLetters() {
        String textTest = "Many Want to rule the world!!";
        String keyTest = "rul th word";

        assertTrue(Search.fuzzyLetters(textTest, keyTest));
    }
}
