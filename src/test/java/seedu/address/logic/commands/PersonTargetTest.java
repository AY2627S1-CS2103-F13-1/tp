package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.PersonId;

public class PersonTargetTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void of_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> PersonTarget.ofIndex(null));
        assertThrows(NullPointerException.class, () -> PersonTarget.ofId(null));
    }

    @Test
    public void resolve_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> PersonTarget.ofIndex(INDEX_FIRST_PERSON).resolve(null));
    }

    @Test
    public void resolve_indexInDisplayedList_returnsDisplayedPerson() throws Exception {
        assertEquals(BENSON, PersonTarget.ofIndex(INDEX_SECOND_PERSON).resolve(model));
    }

    @Test
    public void resolve_indexOutsideDisplayedList_throwsCommandException() {
        // only one person is displayed, so the second index is out of range
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, () ->
                PersonTarget.ofIndex(INDEX_SECOND_PERSON).resolve(model));
    }

    @Test
    public void resolve_idOfPersonHiddenByFilter_returnsPerson() throws Exception {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertEquals(BENSON, PersonTarget.ofId(BENSON.getId().orElseThrow()).resolve(model));
    }

    @Test
    public void resolve_unknownId_throwsCommandException() {
        PersonId unknownId = new PersonId("C99");
        assertThrows(CommandException.class, String.format(Messages.MESSAGE_PERSON_ID_NOT_FOUND, unknownId), () ->
                PersonTarget.ofId(unknownId).resolve(model));
    }

    @Test
    public void resolve_idNumberThatIsAValidIndex_doesNotFallBackToIndex() {
        // C2 must never be read as the second displayed person
        model.deletePerson(BENSON);
        PersonId bensonId = BENSON.getId().orElseThrow();
        assertThrows(CommandException.class, String.format(Messages.MESSAGE_PERSON_ID_NOT_FOUND, bensonId), () ->
                PersonTarget.ofId(bensonId).resolve(model));
    }

    @Test
    public void equals() {
        PersonTarget firstIndex = PersonTarget.ofIndex(INDEX_FIRST_PERSON);
        PersonTarget firstId = PersonTarget.ofId(new PersonId("C1"));

        // same object -> returns true
        assertTrue(firstIndex.equals(firstIndex));

        // same values -> returns true
        assertTrue(firstIndex.equals(PersonTarget.ofIndex(Index.fromOneBased(1))));
        assertTrue(firstId.equals(PersonTarget.ofId(new PersonId("C1"))));

        // different types -> returns false
        assertFalse(firstIndex.equals(1));

        // null -> returns false
        assertFalse(firstIndex.equals(null));

        // different index or ID -> returns false
        assertFalse(firstIndex.equals(PersonTarget.ofIndex(INDEX_SECOND_PERSON)));
        assertFalse(firstId.equals(PersonTarget.ofId(new PersonId("C2"))));

        // index 1 and ID C1 select different persons -> returns false
        assertFalse(firstIndex.equals(firstId));
    }

    @Test
    public void hashCodeMethod() {
        assertEquals(PersonTarget.ofIndex(Index.fromOneBased(1)).hashCode(),
                PersonTarget.ofIndex(Index.fromOneBased(1)).hashCode());
        assertEquals(PersonTarget.ofId(new PersonId("C1")).hashCode(),
                PersonTarget.ofId(new PersonId("C1")).hashCode());
        assertNotEquals(PersonTarget.ofIndex(INDEX_FIRST_PERSON).hashCode(),
                PersonTarget.ofIndex(INDEX_SECOND_PERSON).hashCode());
    }

    @Test
    public void toStringMethod() {
        assertEquals(PersonTarget.class.getCanonicalName() + "{index=" + INDEX_FIRST_PERSON + ", id=null}",
                PersonTarget.ofIndex(INDEX_FIRST_PERSON).toString());

        PersonId id = new PersonId("C1");
        assertEquals(PersonTarget.class.getCanonicalName() + "{index=null, id=" + id + "}",
                PersonTarget.ofId(id).toString());
    }
}
