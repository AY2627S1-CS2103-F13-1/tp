package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

/**
 * Exercises archive commands through the parser with real filtered and sorted model lists.
 */
public class ArchiveWorkflowTest {
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void archiveThenRestore_preservesDetailsIdAndCounter() throws Exception {
        int nextId = model.getAddressBook().getNextPersonId();
        execute("archive 1");
        assertFalse(model.getFilteredPersonList().contains(ALICE));
        assertEquals(ALICE.withArchived(true), model.getAddressBook().getPersonList().get(0));
        assertEquals(getTypicalAddressBook().getPersonList().size(), model.getAddressBook().getPersonList().size());

        execute("list archived");
        assertEquals(List.of(ALICE.withArchived(true)), model.getFilteredPersonList());
        execute("restore 1");
        assertTrue(model.getFilteredPersonList().isEmpty());
        execute("list");
        assertEquals(getTypicalAddressBook().getPersonList(), model.getFilteredPersonList());
        assertEquals(nextId, model.getAddressBook().getNextPersonId());
    }

    @Test
    public void archive_filteredList_targetsDisplayedPerson() throws Exception {
        execute("find Benson");
        assertEquals(List.of(BENSON), model.getFilteredPersonList());
        execute("archive 1");
        assertTrue(model.getFilteredPersonList().isEmpty());
        assertEquals(ALICE, model.getAddressBook().getPersonList().get(0));
        assertEquals(BENSON.withArchived(true), model.getAddressBook().getPersonList().get(1));
    }

    @Test
    public void archiveAndRestore_sortedList_targetsDisplayedPerson() throws Exception {
        model.sortFilteredPersonList(Comparator.comparing((Person person) -> person.getName().fullName).reversed());
        Person target = model.getFilteredPersonList().get(0);
        execute("archive 1");
        execute("archive 1");
        execute("list archived");
        model.sortFilteredPersonList(Comparator.comparing((Person person) -> person.getName().fullName).reversed());
        assertEquals(target.withArchived(true), model.getFilteredPersonList().get(0));
        execute("restore 1");
        assertTrue(model.getAddressBook().getPersonList().contains(target));
        assertEquals(1, model.getFilteredPersonList().size());
    }

    @Test
    public void findAndList_excludeArchivedContacts() throws Exception {
        execute("archive 1");
        execute("list archived");
        execute("find Alice");
        assertTrue(model.getFilteredPersonList().isEmpty());
        execute("list");
        assertEquals(6, model.getFilteredPersonList().size());
        assertTrue(model.getFilteredPersonList().stream().noneMatch(Person::isArchived));
    }

    @Test
    public void startup_archivedContactsHidden() {
        AddressBook book = new AddressBook();
        book.addPerson(ALICE.withArchived(true));
        book.addPerson(BENSON);
        Model restarted = new ModelManager(book, new UserPrefs());
        assertEquals(List.of(BENSON), restarted.getFilteredPersonList());
        assertTrue(restarted.hasPerson(ALICE));
    }

    @Test
    public void add_archivedDuplicate_rejectedWithRestoreInstructions() throws Exception {
        execute("archive 1");
        assertThrows(CommandException.class, AddCommand.MESSAGE_DUPLICATE_ARCHIVED_PERSON, () ->
                new AddCommand(ALICE).execute(model));
        assertEquals(ALICE.withArchived(true), model.getAddressBook().getPersonList().get(0));
    }

    @Test
    public void edit_archivedContact_preservesArchiveStatusAndId() throws Exception {
        execute("archive 1");
        execute("list archived");
        execute("edit 1 p/91234567");
        Person edited = model.getAddressBook().getPersonList().get(0);
        assertTrue(edited.isArchived());
        assertEquals(ALICE.getId(), edited.getId());
        assertEquals("91234567", edited.getPhone().value);
        assertFalse(model.getFilteredPersonList().contains(edited));
    }

    @Test
    public void edit_duplicateArchivedName_rejected() throws Exception {
        execute("archive 1");
        assertThrows(CommandException.class, EditCommand.MESSAGE_DUPLICATE_PERSON, () ->
                execute("edit 1 n/Alice Pauline"));
    }

    @Test
    public void archiveAndRestore_wrongStatus_doesNotChangeData() throws Exception {
        assertThrows(CommandException.class, RestoreCommand.MESSAGE_WRONG_STATUS, () -> execute("restore 1"));
        execute("archive 1");
        execute("list archived");
        AddressBook before = new AddressBook(model.getAddressBook());
        assertThrows(CommandException.class, ArchiveCommand.MESSAGE_WRONG_STATUS, () -> execute("archive 1"));
        assertEquals(before, model.getAddressBook());
    }

    @Test
    public void archiveAndRestore_outOfBounds_doesNotChangeData() throws Exception {
        AddressBook before = new AddressBook(model.getAddressBook());
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, () ->
                execute("archive 8"));
        execute("list archived");
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, () ->
                execute("restore 1"));
        assertEquals(before, model.getAddressBook());
    }

    @Test
    public void parser_invalidArchiveArguments_rejected() {
        for (String command : List.of("archive", "restore")) {
            for (String args : List.of("", " 0", " -1", " C1", " abc", " 1 2", " 2147483648")) {
                assertThrows(ParseException.class, () -> parser.parseCommand(command + args));
            }
        }
        assertThrows(ParseException.class, () -> parser.parseCommand("list archive"));
        assertThrows(ParseException.class, () -> parser.parseCommand("list archived extra"));
    }

    @Test
    public void delete_archiveView_deletesDisplayedArchivedContact() throws Exception {
        execute("archive 2");
        execute("list archived");
        execute("delete 1");
        assertFalse(model.hasPerson(BENSON));
        assertTrue(model.hasPerson(ALICE));
    }

    private void execute(String command) throws Exception {
        parser.parseCommand(command).execute(model);
    }
}
