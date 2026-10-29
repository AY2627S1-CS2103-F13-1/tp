package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.ArgumentMultimap;
import seedu.address.logic.parser.Prefix;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.testutil.PersonBuilder;

public class MatchPersonPredicateTest {
    private final Person person = new PersonBuilder().withName("Alex Yeoh").withPhone("91234567")
            .withEmail("alex@example.com").withAddress("123 Main Street")
            .withTags("Colleagues", "Friends").build();

    @Test
    public void test_noCriteria_returnsFalse() {
        assertFalse(new MatchPersonPredicate(new ArgumentMultimap()).test(person));
    }

    @Test
    public void test_generalSubstring_matchesAnyFieldIgnoringCase() {
        for (String substring : new String[] {"LEX", "2345", "@EXAMPLE", "MAIN", "LEAGU"}) {
            assertTrue(createPredicate(substring).test(person), substring);
        }
        assertFalse(createPredicate("missing").test(person));
        assertFalse(createPredicate("Name:").test(person));
        assertFalse(createPredicate("Yeoh 9123").test(person));
    }

    @Test
    public void test_generalPhrase_allowsEditsWithinTolerance() {
        assertTrue(createPredicate("LEX YEO").test(person));
        assertFalse(createPredicate("Yeoh Alex").test(person));
        assertTrue(createPredicate("Alex  Yeoh").test(person));
        assertTrue(createPredicate("Alex   Yeoh").test(person));
        assertFalse(createPredicate("Alex" + " ".repeat(9) + "Yeoh").test(person));
    }

    @Test
    public void test_generalTypo_matchesWithinTolerance() {
        assertFalse(createPredicate("Alx").test(person));
        assertTrue(createPredicate("EXMPLE").test(person));
        assertFalse(createPredicate("Azzx").test(person));
        assertFalse(createPredicate("zzzx").test(person));
    }

    @Test
    public void test_alexQuery_matchesOnlyAlexInSampleData() {
        List<String> matchingNames = Arrays.stream(SampleDataUtil.getSamplePersons())
                .filter(createPredicate("Alex"))
                .map(samplePerson -> samplePerson.getName().fullName)
                .toList();

        // The shared example.com domain must not make unrelated contacts match Alex.
        assertEquals(List.of("Alex Yeoh"), matchingNames);
    }

    @Test
    public void test_threeCharacterTypo_matchesNoSamplePersons() {
        List<String> matchingNames = Arrays.stream(SampleDataUtil.getSamplePersons())
                .filter(createPredicate("Alx"))
                .map(samplePerson -> samplePerson.getName().fullName)
                .toList();
        assertEquals(List.of(), matchingNames);
    }

    @Test
    public void test_fieldTypos_matchesWithinTolerance() {
        Prefix[] prefixes = {PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG};
        String[] matchingValues = {"Alez", "2349", "@exmple", "Mzin", "Frends"};
        String[] nonMatchingValues = {"Azzx", "2399", "@zzzzple", "Mzzn", "zzzznds"};
        for (int index = 0; index < prefixes.length; index++) {
            assertTrue(createPredicate("", prefixes[index], matchingValues[index]).test(person),
                    matchingValues[index]);
            assertFalse(createPredicate("", prefixes[index], nonMatchingValues[index]).test(person),
                    nonMatchingValues[index]);
        }
    }

    @Test
    public void test_eachField_matchesOnlyItsOwnSubstring() {
        Prefix[] prefixes = {PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG};
        String[] matchingValues = {"YEO", "2345", "@EXAMPLE", "MAIN", "LEAGU"};
        String[] otherFieldValues = {"Main", "Alex", "Friends", "@example", "9123"};
        for (int index = 0; index < prefixes.length; index++) {
            assertTrue(createPredicate("", prefixes[index], matchingValues[index]).test(person));
            assertFalse(createPredicate("", prefixes[index], otherFieldValues[index]).test(person));
        }
    }

    @Test
    public void test_generalAndFieldCriteria_requiresBoth() {
        assertTrue(createPredicate("example", PREFIX_NAME, "Alex").test(person));
        assertFalse(createPredicate("missing", PREFIX_NAME, "Alex").test(person));
        assertFalse(createPredicate("example", PREFIX_NAME, "Bob").test(person));
    }

    @Test
    public void test_multipleFields_requiresEveryCondition() {
        ArgumentMultimap arguments = new ArgumentMultimap();
        arguments.put(new Prefix(""), "example");
        arguments.put(PREFIX_NAME, "Alex");
        arguments.put(PREFIX_PHONE, "2345");
        arguments.put(PREFIX_EMAIL, "@example");
        arguments.put(PREFIX_ADDRESS, "Main");
        arguments.put(PREFIX_TAG, "FRI");
        arguments.put(PREFIX_TAG, "LEAGU");
        assertTrue(new MatchPersonPredicate(arguments).test(person));
        arguments.put(PREFIX_PHONE, "000");
        assertFalse(new MatchPersonPredicate(arguments).test(person));
    }

    @Test
    public void test_multipleTags_requiresAllSubstringsButNotDistinctTags() {
        ArgumentMultimap arguments = new ArgumentMultimap();
        arguments.put(PREFIX_TAG, "FRI");
        arguments.put(PREFIX_TAG, "IEND");
        assertTrue(new MatchPersonPredicate(arguments).test(person));
        arguments.put(PREFIX_TAG, "LEAGU");
        assertTrue(new MatchPersonPredicate(arguments).test(person));
        arguments.put(PREFIX_TAG, "missing");
        assertFalse(new MatchPersonPredicate(arguments).test(person));
    }

    @Test
    public void test_personWithoutTags_matchesOnlyWhenTagsAreNotRequired() {
        Person withoutTags = new PersonBuilder(person).withTags().build();
        assertTrue(createPredicate("example", PREFIX_NAME, "Alex").test(withoutTags));
        assertFalse(createPredicate("friends").test(withoutTags));
        assertFalse(createPredicate("", PREFIX_TAG, "fri").test(withoutTags));
        assertFalse(createPredicate("", PREFIX_TAG, "").test(withoutTags));
    }

    @Test
    public void test_emptyFieldSubstring_matchesNonEmptyField() {
        assertTrue(createPredicate("", PREFIX_NAME, "").test(person));
        assertTrue(createPredicate("example", PREFIX_PHONE, "").test(person));
    }

    @Test
    public void toString_argumentMap_returnsExpectedString() {
        ArgumentMultimap arguments = new ArgumentMultimap();
        arguments.put(PREFIX_NAME, "Alex");
        assertEquals(MatchPersonPredicate.class.getCanonicalName() + "{argMultimap=" + arguments + "}",
                new MatchPersonPredicate(arguments).toString());
    }

    private MatchPersonPredicate createPredicate(String generalSubstring) {
        ArgumentMultimap arguments = new ArgumentMultimap();
        arguments.put(new Prefix(""), generalSubstring);
        return new MatchPersonPredicate(arguments);
    }

    private MatchPersonPredicate createPredicate(String generalSubstring, Prefix prefix, String substring) {
        ArgumentMultimap arguments = new ArgumentMultimap();
        arguments.put(new Prefix(""), generalSubstring);
        arguments.put(prefix, substring);
        return new MatchPersonPredicate(arguments);
    }
}
