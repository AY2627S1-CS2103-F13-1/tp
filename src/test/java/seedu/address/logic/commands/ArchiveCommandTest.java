package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showOnlyPerson;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;

/**
 * Tests ID lookup, command value semantics, and the null input contract.
 */
public class ArchiveCommandTest {
    private static final PersonId TARGET_FIRST = new PersonId(1);
    private static final PersonId TARGET_SECOND = new PersonId(2);

    @Test
    public void equals_comparesCommandTypeAndTarget() {
        ArchiveCommand command = new ArchiveCommand(TARGET_FIRST);
        assertTrue(command.equals(command));
        assertTrue(command.equals(new ArchiveCommand(new PersonId(1))));
        assertFalse(command.equals(new ArchiveCommand(TARGET_SECOND)));
        assertFalse(command.equals(new DeleteCommand(new PersonId(1))));
        assertFalse(command.equals(null));
    }

    @Test
    public void toString_includesTarget() {
        ArchiveCommand command = new ArchiveCommand(TARGET_FIRST);
        assertEquals(ArchiveCommand.class.getCanonicalName() + "{targetId=" + TARGET_FIRST + "}", command.toString());
    }

    @Test
    public void nullInputs_rejected() {
        assertThrows(NullPointerException.class, () -> new ArchiveCommand(null));
        assertThrows(NullPointerException.class, () -> new ArchiveCommand(TARGET_FIRST).execute(null));
    }

    @Test
    public void execute_idHiddenByFilter_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Person original = BENSON.withIsArchived(false);
        model.setPerson(BENSON, original);
        showOnlyPerson(model, ALICE);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(original, original.withIsArchived(true));
        showOnlyPerson(expectedModel, ALICE);

        assertCommandSuccess(new ArchiveCommand(original.getId().orElseThrow()), model,
                String.format(ArchiveCommand.MESSAGE_ARCHIVE_PERSON_SUCCESS, Messages.format(original)),
                expectedModel);
    }

    @Test
    public void execute_unknownId_throwsCommandException() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        assertCommandFailure(new ArchiveCommand(new PersonId(99)), model,
                String.format(Messages.MESSAGE_PERSON_ID_NOT_FOUND, 99));
    }

    @Test
    public void execute_idOfDeletedPerson_throwsCommandException() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        PersonId id = BENSON.getId().orElseThrow();
        model.deletePerson(BENSON);
        assertCommandFailure(new ArchiveCommand(id), model,
                String.format(Messages.MESSAGE_PERSON_ID_NOT_FOUND, id.getValue()));
    }
}
