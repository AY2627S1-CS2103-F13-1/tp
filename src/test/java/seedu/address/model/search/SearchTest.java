package seedu.address.model.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class SearchTest {

    @Test
    public void levenshteinDistance_knownExamples_returnsExpectedDistance() {
        assertEquals(4, Search.levenshteinDistance("cat", "wildcat", false));
        assertEquals(4, Search.levenshteinDistance("wildcat", "cat", false));
        assertEquals(3, Search.levenshteinDistance("kitten", "sitting", false));
        assertEquals(0, Search.levenshteinDistance("Alex", "Alex", false));
        assertEquals(1, Search.levenshteinDistance("cat", "cut", false));
        assertEquals(1, Search.levenshteinDistance("cat", "cart", false));
        assertEquals(1, Search.levenshteinDistance("cart", "cat", false));
        assertEquals(2, Search.levenshteinDistance("ab", "ba", false));
    }

    @Test
    public void levenshteinDistance_partialMatch_ignoresTextOutsideMatch() {
        assertEquals(0, Search.levenshteinDistance("wildcat", "cat", true));
        assertEquals(1, Search.levenshteinDistance("Alex Yeoh", "Alx", true));
        assertEquals(1, Search.levenshteinDistance("alex@example.com", "exmple", true));
        assertEquals(3, Search.levenshteinDistance("", "cat", true));
        assertEquals(0, Search.levenshteinDistance("cat", "", true));
    }

    @Test
    public void levenshteinDistance_caseAndSpaces_countsDifferences() {
        assertEquals(1, Search.levenshteinDistance("Alex", "alex", false));
        assertEquals(1, Search.levenshteinDistance("Alex Yeoh", "AlexYeoh", false));
    }

    @Test
    public void levenshteinDistance_emptyInputs_returnsOtherLength() {
        assertEquals(0, Search.levenshteinDistance("", "", false));
        assertEquals(3, Search.levenshteinDistance("", "cat", false));
        assertEquals(3, Search.levenshteinDistance("cat", "", false));
    }

    @Test
    public void levenshteinDistance_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Search.levenshteinDistance(null, "cat", false));
        assertThrows(NullPointerException.class, () -> Search.levenshteinDistance("cat", null, false));
    }

    @Test
    public void substringThenLevenshtein_exactSubstring_matchesIgnoringCase() {
        assertTrue(Search.substringThenLevenshtein("wildcat", "CAT"));
        assertTrue(Search.substringThenLevenshtein("Alex Yeoh", "LEX YEO"));
    }

    @Test
    public void substringThenLevenshtein_typoInSubstring_matchesWithinTolerance() {
        assertFalse(Search.substringThenLevenshtein("Alex Yeoh", "Alx"));
        assertTrue(Search.substringThenLevenshtein("Alex Yeoh", "Alez"));
        assertTrue(Search.substringThenLevenshtein("Alex Yeoh", "Aleex"));
        assertTrue(Search.substringThenLevenshtein("alex@example.com", "EXMPLE"));
        assertTrue(Search.substringThenLevenshtein("Mr Alex Yeoh Jr", "Alx Yeo"));
        assertFalse(Search.substringThenLevenshtein("prefix kitten suffix", "sittin"));
        assertFalse(Search.substringThenLevenshtein("prefix kitten suffix", "siztin"));
        assertFalse(Search.substringThenLevenshtein("Alex Yeoh", "Bob"));
    }

    @Test
    public void substringThenLevenshtein_emailWithTypos_matchesWithinTolerance() {
        assertTrue(Search.substringThenLevenshtein("alex@example.com", "ALZZ@EXZMPLE.COM"));
        assertFalse(Search.substringThenLevenshtein("alex@example.com", "zzzz@zzzzzzz.zzz"));
    }

    @Test
    public void substringThenLevenshtein_fractionalEditLimit_roundsDown() {
        assertFalse(Search.substringThenLevenshtein("abc", "abz"));
        assertFalse(Search.substringThenLevenshtein("abc", "azz"));
        assertTrue(Search.substringThenLevenshtein("abcd", "abcz"));
        assertFalse(Search.substringThenLevenshtein("abcd", "abzz"));
        assertFalse(Search.substringThenLevenshtein("abcd", "azzz"));
        assertFalse(Search.substringThenLevenshtein("abcdef", "abcdzz"));
        assertFalse(Search.substringThenLevenshtein("abcdef", "abczzz"));
        assertFalse(Search.substringThenLevenshtein("abcdefg", "abcdezz"));
        assertFalse(Search.substringThenLevenshtein("abcdefg", "abcdzzz"));
        assertFalse(Search.substringThenLevenshtein("abcdefg", "abczzzz"));
        assertTrue(Search.substringThenLevenshtein("abcdefgh", "abcdefzz"));
        assertFalse(Search.substringThenLevenshtein("abcdefgh", "abcdezzz"));
    }

    @Test
    public void substringThenLevenshtein_shortKeys_requiresExactMatch() {
        assertTrue(Search.substringThenLevenshtein("Alex Yeoh", "A"));
        assertTrue(Search.substringThenLevenshtein("Alex Yeoh", "AL"));
        assertFalse(Search.substringThenLevenshtein("Alex Yeoh", "z"));
        assertFalse(Search.substringThenLevenshtein("Alex Yeoh", "Az"));
        assertFalse(Search.substringThenLevenshtein("Alex Yeoh", "Alx"));
    }

    @Test
    public void substringThenLevenshtein_alexQuery_rejectsSharedEmailDomain() {
        assertTrue(Search.substringThenLevenshtein("Alex Yeoh", "Alex"));
        assertFalse(Search.substringThenLevenshtein("berniceyu@example.com", "Alex"));
    }

    @Test
    public void substringThenLevenshtein_separatedLetters_requiresEditsForGaps() {
        assertFalse(Search.substringThenLevenshtein("a123b456c", "abc"));
        assertTrue(Search.substringThenLevenshtein("Alex Yeoh", "AlexYeoh"));
    }

    @Test
    public void substringThenLevenshtein_emptyInputs_matchesOnlyEmptyKey() {
        assertTrue(Search.substringThenLevenshtein("Alex", ""));
        assertTrue(Search.substringThenLevenshtein("", ""));
        assertFalse(Search.substringThenLevenshtein("", "cat"));
        assertFalse(Search.substringThenLevenshtein("", "a"));
        assertFalse(Search.substringThenLevenshtein("xyz", "cat"));
    }

    @Test
    public void substringThenLevenshtein_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Search.substringThenLevenshtein(null, "cat"));
        assertThrows(NullPointerException.class, () -> Search.substringThenLevenshtein("cat", null));
    }

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
}
