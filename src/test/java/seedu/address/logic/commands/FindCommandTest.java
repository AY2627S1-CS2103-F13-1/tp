package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_CONTACTS_LISTED_OVERVIEW;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.GEORGE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.ArgumentTokenizer;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.MatchPersonPredicate;
import seedu.address.model.person.Person;

/**
 * Contains integration tests for FindCommand with the combined search predicate.
 */
public class FindCommandTest {
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals_variousComparisons_returnsExpectedResult() {
        MatchPersonPredicate firstPredicate = createPredicate("first");
        FindCommand firstCommand = new FindCommand(firstPredicate);
        assertTrue(firstCommand.equals(firstCommand));
        assertTrue(firstCommand.equals(new FindCommand(firstPredicate)));
        assertFalse(firstCommand.equals(new FindCommand(createPredicate("second"))));
        assertFalse(firstCommand.equals(null));
        assertFalse(firstCommand.equals(1));
    }

    @Test
    public void execute_emptyOrNonMatchingSubstring_noPersonFound() {
        assertSearchResults("");
        assertSearchResults("nonexistent");
    }

    @Test
    public void execute_generalSubstring_searchesAcrossFields() {
        assertSearchResults("STREET", CARL, DANIEL, GEORGE);
    }

    @Test
    public void execute_generalAndFieldSubstrings_narrowsResults() {
        assertSearchResults(" n/Mei", BENSON, DANIEL, ELLE);
        assertSearchResults(" Clementi n/Mei", BENSON);
        assertSearchResults(" Clementi n/Mei e/johnd t/Money", BENSON);
        assertSearchResults(" missing n/Mei");
        assertSearchResults(" Clementi n/Alice");
    }

    @Test
    public void execute_typoInQuery_matchesWithinTolerance() {
        assertSearchResults("Bensn", BENSON, GEORGE);
        assertSearchResults(" n/Bensn e/johnd t/Mony", BENSON);
        assertSearchResults(" n/Bezzon", BENSON);
        assertSearchResults(" n/Bensn t/Mozzy", BENSON);
        assertSearchResults(" n/zzzzzz");
        assertSearchResults(" n/Bensn t/zzzzz");
    }

    @Test
    public void execute_multipleTags_requiresEveryTag() {
        assertSearchResults(" t/FRI t/Money", BENSON);
        assertSearchResults(" t/FRI t/nonexistent");
    }

    @Test
    public void execute_previousSearch_doesNotRestrictNextSearch() {
        assertSearchResults(" Clementi n/Mei", BENSON);
        assertSearchResults(" n/Mei", BENSON, DANIEL, ELLE);
    }

    @Test
    public void toString_matchPredicate_returnsExpectedString() {
        MatchPersonPredicate predicate = createPredicate("keyword n/Al");
        FindCommand command = new FindCommand(predicate);
        assertEquals(FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}", command.toString());
    }

    private void assertSearchResults(String arguments, Person... expectedPersons) {
        AddressBook originalAddressBook = new AddressBook(model.getAddressBook());
        FindCommand command = new FindCommand(createPredicate(arguments));
        assertEquals(String.format(MESSAGE_CONTACTS_LISTED_OVERVIEW, expectedPersons.length),
                command.execute(model).getFeedbackToUser());
        assertEquals(List.of(expectedPersons), model.getFilteredPersonList());
        assertEquals(originalAddressBook, model.getAddressBook());
    }

    private MatchPersonPredicate createPredicate(String arguments) {
        return new MatchPersonPredicate(ArgumentTokenizer.tokenize(arguments,
                PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG));
    }
}
