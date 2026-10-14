package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.person.PersonId;

/**
 * Tests the command value semantics and null input contract.
 */
public class RestoreCommandTest {
    @Test
    public void equals_comparesCommandTypeAndDisplayedIndex() {
        RestoreCommand command = new RestoreCommand(INDEX_FIRST_PERSON);
        assertTrue(command.equals(command));
        assertTrue(command.equals(new RestoreCommand(INDEX_FIRST_PERSON)));
        assertFalse(command.equals(new RestoreCommand(INDEX_SECOND_PERSON)));
        assertFalse(command.equals(new DeleteCommand(INDEX_FIRST_PERSON)));
        assertFalse(command.equals(null));

        RestoreCommand byId = new RestoreCommand(PersonTarget.ofId(new PersonId("C1")));
        assertTrue(byId.equals(new RestoreCommand(PersonTarget.ofId(new PersonId("C1")))));
        assertFalse(byId.equals(new RestoreCommand(PersonTarget.ofId(new PersonId("C2")))));
        assertFalse(command.equals(byId));
    }

    @Test
    public void toString_includesTargetIndex() {
        RestoreCommand command = new RestoreCommand(INDEX_FIRST_PERSON);
        assertEquals(RestoreCommand.class.getCanonicalName() + "{target=" + PersonTarget.ofIndex(INDEX_FIRST_PERSON)
                + "}", command.toString());
    }

    @Test
    public void nullInputs_rejected() {
        assertThrows(NullPointerException.class, () -> new RestoreCommand((Index) null));
        assertThrows(NullPointerException.class, () -> new RestoreCommand((PersonTarget) null));
        assertThrows(NullPointerException.class, () -> new RestoreCommand(INDEX_FIRST_PERSON).execute(null));
    }
}
