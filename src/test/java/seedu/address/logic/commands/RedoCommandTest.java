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

public class RedoCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_nothingToRedo_throwsCommandException() {
        assertCommandFailure(new RedoCommand(), model, RedoCommand.MESSAGE_NOTHING_TO_REDO);
    }

    @Test
    public void execute_afterUndo_restoresUndoneState() {
        model.deletePerson(BENSON);
        model.commitAddressBook();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        model.undoAddressBook();

        assertCommandSuccess(new RedoCommand(), model, RedoCommand.MESSAGE_SUCCESS, expectedModel);
        assertFalse(model.hasPerson(BENSON));
        assertFalse(model.canRedoAddressBook());
    }

    @Test
    public void equals() {
        RedoCommand redoCommand = new RedoCommand();

        assertTrue(redoCommand.equals(redoCommand));
        assertTrue(redoCommand.equals(new RedoCommand()));
        assertFalse(redoCommand.equals(null));
        assertFalse(redoCommand.equals(new UndoCommand()));
    }
}
