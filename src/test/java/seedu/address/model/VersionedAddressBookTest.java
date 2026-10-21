package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class VersionedAddressBookTest {

    private final VersionedAddressBook versionedAddressBook = new VersionedAddressBook(new AddressBook());

    @Test
    public void constructor_copiesInitialState() {
        assertEquals(getTypicalAddressBook(), new VersionedAddressBook(getTypicalAddressBook()));
    }

    @Test
    public void undo_noEarlierState_throwsIllegalStateException() {
        assertFalse(versionedAddressBook.canUndo());
        assertThrows(IllegalStateException.class, VersionedAddressBook.MESSAGE_NO_EARLIER_STATE,
                versionedAddressBook::undo);
    }

    @Test
    public void redo_noUndoneState_throwsIllegalStateException() {
        assertFalse(versionedAddressBook.canRedo());
        assertThrows(IllegalStateException.class, VersionedAddressBook.MESSAGE_NO_LATER_STATE,
                versionedAddressBook::redo);
    }

    @Test
    public void commit_noChange_savesNothing() {
        versionedAddressBook.commit();

        assertFalse(versionedAddressBook.canUndo());
    }

    @Test
    public void undo_afterAdd_restoresPersonsAndIdCounter() {
        AddressBook before = new AddressBook(versionedAddressBook);
        versionedAddressBook.addPerson(personNamed("Amy Bee"));
        versionedAddressBook.commit();
        assertTrue(versionedAddressBook.canUndo());

        versionedAddressBook.undo();

        assertEquals(before, versionedAddressBook);
        // the ID given to the undone addition is given out again
        assertEquals(1, versionedAddressBook.getNextPersonId());
        assertFalse(versionedAddressBook.canUndo());
    }

    @Test
    public void undo_multipleChanges_restoresEachStateInTurn() {
        AddressBook empty = new AddressBook(versionedAddressBook);
        versionedAddressBook.addPerson(personNamed("Amy Bee"));
        versionedAddressBook.commit();
        AddressBook withAmy = new AddressBook(versionedAddressBook);
        versionedAddressBook.addPerson(personNamed("Bob Choo"));
        versionedAddressBook.commit();

        versionedAddressBook.undo();
        assertEquals(withAmy, versionedAddressBook);
        versionedAddressBook.undo();
        assertEquals(empty, versionedAddressBook);
    }

    @Test
    public void commit_afterUndo_discardsUndoneStates() {
        versionedAddressBook.addPerson(personNamed("Amy Bee"));
        versionedAddressBook.commit();
        AddressBook withAmy = new AddressBook(versionedAddressBook);
        versionedAddressBook.addPerson(personNamed("Bob Choo"));
        versionedAddressBook.commit();
        versionedAddressBook.undo();

        versionedAddressBook.addPerson(personNamed("Carl Kurz"));
        versionedAddressBook.commit();

        // undoing Carl's addition returns to the state before it, not to the undone state with Bob
        versionedAddressBook.undo();
        assertEquals(withAmy, versionedAddressBook);
        versionedAddressBook.undo();
        assertFalse(versionedAddressBook.canUndo());
    }

    @Test
    public void redo_afterUndo_restoresPersonsAndIdCounter() {
        versionedAddressBook.addPerson(personNamed("Amy Bee"));
        versionedAddressBook.commit();
        AddressBook withAmy = new AddressBook(versionedAddressBook);
        versionedAddressBook.undo();
        assertTrue(versionedAddressBook.canRedo());

        versionedAddressBook.redo();

        assertEquals(withAmy, versionedAddressBook);
        assertEquals(2, versionedAddressBook.getNextPersonId());
        assertFalse(versionedAddressBook.canRedo());
    }

    @Test
    public void redo_multipleUndos_redoesEachStateInTurn() {
        versionedAddressBook.addPerson(personNamed("Amy Bee"));
        versionedAddressBook.commit();
        AddressBook withAmy = new AddressBook(versionedAddressBook);
        versionedAddressBook.addPerson(personNamed("Bob Choo"));
        versionedAddressBook.commit();
        AddressBook withAmyAndBob = new AddressBook(versionedAddressBook);
        versionedAddressBook.undo();
        versionedAddressBook.undo();

        versionedAddressBook.redo();
        assertEquals(withAmy, versionedAddressBook);
        versionedAddressBook.redo();
        assertEquals(withAmyAndBob, versionedAddressBook);
        assertFalse(versionedAddressBook.canRedo());
    }

    @Test
    public void commit_noChangeAfterUndo_keepsRedo() {
        versionedAddressBook.addPerson(personNamed("Amy Bee"));
        versionedAddressBook.commit();
        versionedAddressBook.undo();

        versionedAddressBook.commit();

        assertTrue(versionedAddressBook.canRedo());
    }

    @Test
    public void commit_changeAfterUndo_removesRedo() {
        versionedAddressBook.addPerson(personNamed("Amy Bee"));
        versionedAddressBook.commit();
        versionedAddressBook.undo();

        versionedAddressBook.addPerson(personNamed("Bob Choo"));
        versionedAddressBook.commit();

        assertFalse(versionedAddressBook.canRedo());
    }

    @Test
    public void commit_moreChangesThanLimit_forgetsOldestChange() {
        for (int i = 1; i <= VersionedAddressBook.HISTORY_LIMIT + 1; i++) {
            versionedAddressBook.addPerson(personNamed("Person " + i));
            versionedAddressBook.commit();
        }

        for (int i = 0; i < VersionedAddressBook.HISTORY_LIMIT; i++) {
            assertTrue(versionedAddressBook.canUndo());
            versionedAddressBook.undo();
        }

        // the first addition can no longer be undone
        assertFalse(versionedAddressBook.canUndo());
        assertEquals(1, versionedAddressBook.getPersonList().size());
    }

    private static Person personNamed(String name) {
        return new PersonBuilder().withName(name).build();
    }
}
