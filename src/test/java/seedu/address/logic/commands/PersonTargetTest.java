package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.showOnlyPerson;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.PersonId;

public class PersonTargetTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void ofId_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> PersonTarget.ofId(null));
    }

    @Test
    public void resolve_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> PersonTarget.ofId(new PersonId(1)).resolve(null));
    }

    @Test
    public void resolve_existingId_returnsPerson() throws Exception {
        assertEquals(BENSON, PersonTarget.ofId(BENSON.getId().orElseThrow()).resolve(model));
    }

    @Test
    public void resolve_idOfPersonHiddenByFilter_returnsPerson() throws Exception {
        // only ALICE is displayed
        showOnlyPerson(model, ALICE);
        assertEquals(BENSON, PersonTarget.ofId(BENSON.getId().orElseThrow()).resolve(model));
    }

    @Test
    public void resolve_unknownId_throwsCommandException() {
        assertThrows(CommandException.class, String.format(Messages.MESSAGE_PERSON_ID_NOT_FOUND, 99), () ->
                PersonTarget.ofId(new PersonId(99)).resolve(model));
    }

    @Test
    public void resolve_idOfDeletedPerson_throwsCommandException() {
        // ID 2 must not fall back to whichever person is now second in the list
        model.deletePerson(BENSON);
        assertThrows(CommandException.class, String.format(Messages.MESSAGE_PERSON_ID_NOT_FOUND, 2), () ->
                PersonTarget.ofId(new PersonId(2)).resolve(model));
    }

    @Test
    public void equals() {
        PersonTarget firstId = PersonTarget.ofId(new PersonId(1));

        // same object -> returns true
        assertTrue(firstId.equals(firstId));

        // same ID -> returns true
        assertTrue(firstId.equals(PersonTarget.ofId(new PersonId(1))));

        // different types -> returns false
        assertFalse(firstId.equals(1));

        // null -> returns false
        assertFalse(firstId.equals(null));

        // different ID -> returns false
        assertFalse(firstId.equals(PersonTarget.ofId(new PersonId(2))));
    }

    @Test
    public void hashCodeMethod() {
        assertEquals(PersonTarget.ofId(new PersonId(1)).hashCode(), PersonTarget.ofId(new PersonId(1)).hashCode());
        assertNotEquals(PersonTarget.ofId(new PersonId(1)).hashCode(),
                PersonTarget.ofId(new PersonId(2)).hashCode());
    }

    @Test
    public void toStringMethod() {
        PersonId id = new PersonId(1);
        assertEquals(PersonTarget.class.getCanonicalName() + "{id=" + id + "}", PersonTarget.ofId(id).toString());
    }
}
