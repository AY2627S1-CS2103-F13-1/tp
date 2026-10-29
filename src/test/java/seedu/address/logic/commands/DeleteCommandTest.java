package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showOnlyPerson;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Comparator;

import org.junit.jupiter.api.Test;

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
    public void execute_validIdOfDisplayedPersonInFilteredList_success() {
        // only ALICE is displayed, and she is deleted by her ID
        showOnlyPerson(model, ALICE);
        DeleteCommand deleteCommand = new DeleteCommand(ALICE.getId().orElseThrow());

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(ALICE));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(ALICE);
        showNoPerson(expectedModel);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(new PersonId(1));
        DeleteCommand deleteSecondCommand = new DeleteCommand(new PersonId(2));

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(new PersonId(1));
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void execute_validIdUnfilteredList_success() {
        DeleteCommand deleteCommand = new DeleteCommand(BENSON.getId().orElseThrow());

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(BENSON));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(BENSON);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validIdOfPersonHiddenByFilter_success() {
        // only ALICE is displayed, but BENSON can still be deleted by his ID
        showOnlyPerson(model, ALICE);
        DeleteCommand deleteCommand = new DeleteCommand(BENSON.getId().orElseThrow());

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(BENSON));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(BENSON);
        showOnlyPerson(expectedModel, ALICE);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validIdAfterListReordered_deletesSamePerson() {
        // reversing the list moves BENSON away from the second position, but his ID still selects him
        Comparator<Person> reverseByName = Comparator.comparing((Person person) -> person.getName().fullName)
                .reversed();
        model.sortFilteredPersonList(reverseByName);
        DeleteCommand deleteCommand = new DeleteCommand(BENSON.getId().orElseThrow());

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(BENSON));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.sortFilteredPersonList(reverseByName);
        expectedModel.deletePerson(BENSON);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_unknownId_throwsCommandException() {
        DeleteCommand deleteCommand = new DeleteCommand(new PersonId(99));

        assertCommandFailure(deleteCommand, model, String.format(Messages.MESSAGE_PERSON_ID_NOT_FOUND, 99));
    }

    @Test
    public void execute_idOfDeletedPerson_throwsCommandException() {
        PersonId aliceId = ALICE.getId().orElseThrow();
        model.deletePerson(ALICE);
        DeleteCommand deleteCommand = new DeleteCommand(aliceId);

        assertCommandFailure(deleteCommand, model,
                String.format(Messages.MESSAGE_PERSON_ID_NOT_FOUND, aliceId.getValue()));
    }

    @Test
    public void toStringMethod() {
        PersonId targetId = new PersonId(1);
        DeleteCommand deleteCommand = new DeleteCommand(targetId);
        String expected = DeleteCommand.class.getCanonicalName() + "{targetId=" + targetId + "}";
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
