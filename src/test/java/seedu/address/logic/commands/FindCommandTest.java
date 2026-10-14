package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.GEORGE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.AnyFieldContainsSubstringPredicate;
import seedu.address.model.person.EachFieldContainsSubstringPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals_variousComparisons_returnsExpectedResult() {
        AnyFieldContainsSubstringPredicate firstPredicate =
                new AnyFieldContainsSubstringPredicate("first");
        AnyFieldContainsSubstringPredicate secondPredicate =
                new AnyFieldContainsSubstringPredicate("second");

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_nonMatchingSubstring_noPersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        AnyFieldContainsSubstringPredicate predicate = new AnyFieldContainsSubstringPredicate("nonexistent");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_addressSubstring_multiplePersonsFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        AnyFieldContainsSubstringPredicate predicate = new AnyFieldContainsSubstringPredicate("STREET");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(CARL, DANIEL, GEORGE), model.getFilteredPersonList());
    }

    @Test
    public void execute_additionalField_narrowsResults() {
        EachFieldContainsSubstringPredicate namePredicate = new EachFieldContainsSubstringPredicate(
                Optional.of("Mei"), Optional.empty(), Optional.empty(), Optional.empty(), List.of());
        expectedModel.updateFilteredPersonList(namePredicate);
        assertCommandSuccess(new FindCommand(namePredicate), model,
                String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 2), expectedModel);
        assertEquals(List.of(BENSON, DANIEL), model.getFilteredPersonList());

        EachFieldContainsSubstringPredicate combinedPredicate = new EachFieldContainsSubstringPredicate(
                Optional.of("Mei"), Optional.empty(), Optional.empty(), Optional.of("Clementi"), List.of());
        expectedModel.updateFilteredPersonList(combinedPredicate);
        assertCommandSuccess(new FindCommand(combinedPredicate), model,
                String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1), expectedModel);
        assertEquals(List.of(BENSON), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleTags_onlyPersonMatchingEveryTagFound() {
        EachFieldContainsSubstringPredicate predicate = new EachFieldContainsSubstringPredicate(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                List.of("FRI", "Money"));
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(new FindCommand(predicate), model,
                String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1), expectedModel);
        assertEquals(List.of(BENSON), model.getFilteredPersonList());
    }

    @Test
    public void execute_oneFieldDoesNotMatch_noPersonFound() {
        EachFieldContainsSubstringPredicate predicate = new EachFieldContainsSubstringPredicate(
                Optional.of("Mei"), Optional.empty(), Optional.empty(), Optional.of("tokyo"), List.of());
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(new FindCommand(predicate), model,
                String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0), expectedModel);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void toString_substringPredicate_returnsExpectedString() {
        AnyFieldContainsSubstringPredicate predicate = new AnyFieldContainsSubstringPredicate("keyword");
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }

}
