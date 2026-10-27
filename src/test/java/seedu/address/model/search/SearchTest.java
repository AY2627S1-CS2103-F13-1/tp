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
    public void substringThenLevenshtein_exactSubstring_matchesWithZeroEdits() {
        assertTrue(Search.substringThenLevenshtein("wildcat", "CAT", 0));
        assertTrue(Search.substringThenLevenshtein("Alex Yeoh", "LEX YEO", 0));
        assertFalse(Search.substringThenLevenshtein("Alex Yeoh", "Alx", 0));
    }

    @Test
    public void substringThenLevenshtein_typoInSubstring_matchesWithinTolerance() {
        assertTrue(Search.substringThenLevenshtein("Alex Yeoh", "Alx", 1.0 / 3.0));
        assertTrue(Search.substringThenLevenshtein("Alex Yeoh", "Alez", 1.0 / 3.0));
        assertTrue(Search.substringThenLevenshtein("Alex Yeoh", "Aleex", 1.0 / 3.0));
        assertTrue(Search.substringThenLevenshtein("alex@example.com", "EXMPLE", 1.0 / 3.0));
        assertTrue(Search.substringThenLevenshtein("Mr Alex Yeoh Jr", "Alx Yeo", 1.0 / 3.0));
        assertTrue(Search.substringThenLevenshtein("prefix kitten suffix", "sittin", 1.0 / 3.0));
        assertFalse(Search.substringThenLevenshtein("prefix kitten suffix", "sittin", 0.16));
        assertFalse(Search.substringThenLevenshtein("Alex Yeoh", "Bob", 1.0 / 3.0));
    }

    @Test
    public void substringThenLevenshtein_emailWithTypos_matchesWithinTolerance() {
        assertTrue(Search.substringThenLevenshtein("alex@example.com", "ALZZ@EXZMPLE.COM", 1.0 / 3.0));
        assertFalse(Search.substringThenLevenshtein("alex@example.com", "zzzz@zzzzzzz.zzz", 1.0 / 3.0));
    }

    @Test
    public void substringThenLevenshtein_fractionalEditLimit_roundsDown() {
        assertTrue(Search.substringThenLevenshtein("abc", "abz", 1.0 / 3.0));
        assertFalse(Search.substringThenLevenshtein("abc", "azz", 1.0 / 3.0));
        assertTrue(Search.substringThenLevenshtein("abcd", "abcz", 1.0 / 3.0));
        assertFalse(Search.substringThenLevenshtein("abcd", "abzz", 1.0 / 3.0));
        assertFalse(Search.substringThenLevenshtein("abcd", "azzz", 1.0 / 3.0));
        assertTrue(Search.substringThenLevenshtein("abcdef", "abcdzz", 1.0 / 3.0));
        assertFalse(Search.substringThenLevenshtein("abcdef", "abczzz", 1.0 / 3.0));
        assertTrue(Search.substringThenLevenshtein("abcdefg", "abcdezz", 1.0 / 3.0));
        assertFalse(Search.substringThenLevenshtein("abcdefg", "abcdzzz", 1.0 / 3.0));
        assertFalse(Search.substringThenLevenshtein("abcdefg", "abczzzz", 1.0 / 3.0));
    }

    @Test
    public void substringThenLevenshtein_toleranceBoundary_changesAllowedEdits() {
        assertFalse(Search.substringThenLevenshtein("abcd", "abzz", 0.25));
        assertFalse(Search.substringThenLevenshtein("abcd", "abzz", 0.499999));
        assertTrue(Search.substringThenLevenshtein("abcd", "abzz", 0.5));
        assertFalse(Search.substringThenLevenshtein("a", "z", 0.0));
        assertFalse(Search.substringThenLevenshtein("a", "z", 0.01));
        assertFalse(Search.substringThenLevenshtein("ab", "az", 1.0 / 3.0));
        assertTrue(Search.substringThenLevenshtein("a", "z", 1.0));
    }

    @Test
    public void substringThenLevenshtein_alexQuery_rejectsSharedEmailDomain() {
        assertTrue(Search.substringThenLevenshtein("Alex Yeoh", "Alex", 1.0 / 3.0));
        assertFalse(Search.substringThenLevenshtein("berniceyu@example.com", "Alex", 1.0 / 3.0));
    }

    @Test
    public void substringThenLevenshtein_separatedLetters_requiresEditsForGaps() {
        assertFalse(Search.substringThenLevenshtein("a123b456c", "abc", 1.0 / 3.0));
        assertFalse(Search.substringThenLevenshtein("Alex Yeoh", "AlexYeoh", 0));
        assertTrue(Search.substringThenLevenshtein("Alex Yeoh", "AlexYeoh", 1.0 / 3.0));
    }

    @Test
    public void substringThenLevenshtein_emptyInputs_countsDeletedQueryCharacters() {
        assertTrue(Search.substringThenLevenshtein("Alex", "", 0));
        assertTrue(Search.substringThenLevenshtein("", "", 0));
        assertFalse(Search.substringThenLevenshtein("", "cat", 1.0 / 3.0));
        assertTrue(Search.substringThenLevenshtein("", "cat", 1.0));
        assertTrue(Search.substringThenLevenshtein("xyz", "cat", 1.0));
    }

    @Test
    public void substringThenLevenshtein_outOfRangeTolerance_throwsIllegalArgumentException() {
        for (double tolerance : new double[] {-0.01, 1.01, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () ->
                    Search.substringThenLevenshtein("cat", "cat", tolerance));
            assertThrows(IllegalArgumentException.class, () ->
                    Search.substringThenLevenshtein("cat", "dog", tolerance));
        }
    }

    @Test
    public void substringThenLevenshtein_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Search.substringThenLevenshtein(null, "cat", 1));
        assertThrows(NullPointerException.class, () -> Search.substringThenLevenshtein("cat", null, 1));
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
