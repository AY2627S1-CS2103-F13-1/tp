package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Comparator;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);
        showNoPerson(expectedModel);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(INDEX_FIRST_PERSON);
        DeleteCommand deleteSecondCommand = new DeleteCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(INDEX_FIRST_PERSON);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));

        // same ID -> returns true
        DeleteCommand deleteByIdCommand = new DeleteCommand(PersonTarget.ofId(new PersonId("C1")));
        assertTrue(deleteByIdCommand.equals(new DeleteCommand(PersonTarget.ofId(new PersonId("C1")))));

        // different ID -> returns false
        assertFalse(deleteByIdCommand.equals(new DeleteCommand(PersonTarget.ofId(new PersonId("C2")))));

        // selected by ID instead of index -> returns false
        assertFalse(deleteFirstCommand.equals(deleteByIdCommand));
    }

    @Test
    public void execute_validIdUnfilteredList_success() {
        DeleteCommand deleteCommand = new DeleteCommand(PersonTarget.ofId(BENSON.getId().orElseThrow()));

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(BENSON));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(BENSON);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validIdOfPersonHiddenByFilter_success() {
        // only ALICE is displayed, but BENSON can still be deleted by his ID
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        DeleteCommand deleteCommand = new DeleteCommand(PersonTarget.ofId(BENSON.getId().orElseThrow()));

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(BENSON));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(BENSON);
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validIdAfterListReordered_deletesSamePerson() {
        // reversing the list moves BENSON away from the second position, but his ID still selects him
        Comparator<Person> reverseByName = Comparator.comparing((Person person) -> person.getName().fullName)
                .reversed();
        model.sortFilteredPersonList(reverseByName);
        DeleteCommand deleteCommand = new DeleteCommand(PersonTarget.ofId(BENSON.getId().orElseThrow()));

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(BENSON));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.sortFilteredPersonList(reverseByName);
        expectedModel.deletePerson(BENSON);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_unknownId_throwsCommandException() {
        PersonId unknownId = new PersonId("C99");
        DeleteCommand deleteCommand = new DeleteCommand(PersonTarget.ofId(unknownId));

        assertCommandFailure(deleteCommand, model, String.format(Messages.MESSAGE_PERSON_ID_NOT_FOUND, unknownId));
    }

    @Test
    public void execute_idOfDeletedPerson_throwsCommandException() {
        PersonId aliceId = ALICE.getId().orElseThrow();
        model.deletePerson(ALICE);
        DeleteCommand deleteCommand = new DeleteCommand(PersonTarget.ofId(aliceId));

        assertCommandFailure(deleteCommand, model, String.format(Messages.MESSAGE_PERSON_ID_NOT_FOUND, aliceId));
    }

    @Test
    public void toStringMethod() {
        PersonTarget target = PersonTarget.ofIndex(Index.fromOneBased(1));
        DeleteCommand deleteCommand = new DeleteCommand(target);
        String expected = DeleteCommand.class.getCanonicalName() + "{target=" + target + "}";
        assertEquals(expected, deleteCommand.toString());
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoPerson(Model model) {
        model.updateFilteredPersonList(p -> false);

        assertTrue(model.getFilteredPersonList().isEmpty());
    }
}
