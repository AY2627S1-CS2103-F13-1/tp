package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.PersonId;

/**
 * Tests the command value semantics and null input contract.
 */
public class ArchiveCommandTest {
    private static final PersonTarget TARGET_FIRST = PersonTarget.ofId(new PersonId(1));
    private static final PersonTarget TARGET_SECOND = PersonTarget.ofId(new PersonId(2));

    @Test
    public void equals_comparesCommandTypeAndTarget() {
        ArchiveCommand command = new ArchiveCommand(TARGET_FIRST);
        assertTrue(command.equals(command));
        assertTrue(command.equals(new ArchiveCommand(PersonTarget.ofId(new PersonId(1)))));
        assertFalse(command.equals(new ArchiveCommand(TARGET_SECOND)));
        assertFalse(command.equals(new DeleteCommand(TARGET_FIRST)));
        assertFalse(command.equals(null));
    }

    @Test
    public void toString_includesTarget() {
        ArchiveCommand command = new ArchiveCommand(TARGET_FIRST);
        assertEquals(ArchiveCommand.class.getCanonicalName() + "{target=" + TARGET_FIRST + "}", command.toString());
    }

    @Test
    public void nullInputs_rejected() {
        assertThrows(NullPointerException.class, () -> new ArchiveCommand(null));
        assertThrows(NullPointerException.class, () -> new ArchiveCommand(TARGET_FIRST).execute(null));
    }
}
