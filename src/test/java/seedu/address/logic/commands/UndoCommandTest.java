package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class UndoCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_nothingToUndo_throwsCommandException() {
        assertCommandFailure(new UndoCommand(), model, UndoCommand.MESSAGE_NOTHING_TO_UNDO);
    }

    @Test
    public void execute_afterSavedChange_restoresPreviousState() {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        model.deletePerson(BENSON);
        model.commitAddressBook();

        assertCommandSuccess(new UndoCommand(), model, UndoCommand.MESSAGE_SUCCESS, expectedModel);
        assertTrue(model.hasPerson(BENSON));
        assertFalse(model.canUndoAddressBook());
    }

    @Test
    public void equals() {
        UndoCommand undoCommand = new UndoCommand();

        assertTrue(undoCommand.equals(undoCommand));
        assertTrue(undoCommand.equals(new UndoCommand()));
        assertFalse(undoCommand.equals(null));
        assertFalse(undoCommand.equals(new ClearCommand()));
    }
}
