package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.GEORGE;
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
import seedu.address.testutil.PersonBuilder;

/**
 * Exercises archive commands through the parser with real filtered and sorted model lists.
 */
public class ArchiveWorkflowTest {
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void archiveThenRestore_preservesDetailsIdAndCounter() throws Exception {
        execute("list");
        int nextId = model.getAddressBook().getNextPersonId();
        execute("archive 1");
        assertFalse(model.getFilteredPersonList().contains(ALICE));
        assertEquals(new PersonBuilder(ALICE).withArchiveStatus(true).build(),
                model.getAddressBook().getPersonList().get(0));
        assertEquals(getTypicalAddressBook().getPersonList().size(), model.getAddressBook().getPersonList().size());

        execute("list archive");
        assertEquals(List.of(new PersonBuilder(ALICE).withArchiveStatus(true).build()), model.getFilteredPersonList());
        execute("restore 1");
        assertTrue(model.getFilteredPersonList().isEmpty());
        execute("list all");
        assertEquals(getTypicalAddressBook().getPersonList(), model.getFilteredPersonList());
        assertEquals(nextId, model.getAddressBook().getNextPersonId());
    }

    @Test
    public void archive_filteredList_targetsId() throws Exception {
        execute("find Benson");
        assertEquals(List.of(BENSON), model.getFilteredPersonList());
        execute("archive 2");
        assertEquals(List.of(new PersonBuilder(BENSON).withArchiveStatus(true).build()), model.getFilteredPersonList());
        assertEquals(ALICE, model.getAddressBook().getPersonList().get(0));
        assertEquals(new PersonBuilder(BENSON).withArchiveStatus(true).build(),
                model.getAddressBook().getPersonList().get(1));
    }

    @Test
    public void archiveAndRestore_sortedList_targetsId() throws Exception {
        execute("list");
        model.sortFilteredPersonList(Comparator.comparing((Person person) -> person.getName().fullName).reversed());
        // GEORGE (ID 7) is displayed first, but the IDs select ALICE (ID 1) and BENSON (ID 2)
        assertEquals(GEORGE, model.getFilteredPersonList().get(0));
        execute("archive 1");
        execute("archive 2");
        execute("list archive");
        model.sortFilteredPersonList(Comparator.comparing((Person person) -> person.getName().fullName).reversed());
        assertEquals(List.of(new PersonBuilder(BENSON).withArchiveStatus(true).build(),
                new PersonBuilder(ALICE).withArchiveStatus(true).build()), model.getFilteredPersonList());
        execute("restore 1");
        assertTrue(model.getAddressBook().getPersonList().contains(ALICE));
        assertEquals(1, model.getFilteredPersonList().size());
    }

    @Test
    public void listViews_selectRequestedContacts() throws Exception {
        execute("archive 1");
        execute("list");
        assertEquals(6, model.getFilteredPersonList().size());
        assertTrue(model.getFilteredPersonList().stream().noneMatch(Person::isArchived));
        execute("list archive");
        assertEquals(List.of(new PersonBuilder(ALICE).withArchiveStatus(true).build()), model.getFilteredPersonList());
        execute("list all");
        assertEquals(model.getAddressBook().getPersonList(), model.getFilteredPersonList());
        execute("restore 1");
        assertEquals(ALICE, model.getFilteredPersonList().get(0));
        execute("list archive");
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void list_unknownKeyword_rejectedAndKeepsView() throws Exception {
        execute("archive 1");
        execute("list archive");
        String expectedMessage = String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE);
        for (String args : List.of(" 3", " active", " archived", " archiv", " all archive")) {
            assertThrows(ParseException.class, expectedMessage, () -> execute("list" + args));
        }
        assertEquals(List.of(new PersonBuilder(ALICE).withArchiveStatus(true).build()), model.getFilteredPersonList());
    }

    @Test
    public void startup_onlyActiveContactsVisible() {
        AddressBook book = new AddressBook();
        book.addPerson(new PersonBuilder(ALICE).withArchiveStatus(true).build());
        book.addPerson(BENSON);
        Model restarted = new ModelManager(book, new UserPrefs());
        assertEquals(List.of(BENSON), restarted.getFilteredPersonList());
        assertTrue(restarted.hasPerson(ALICE));
    }

    @Test
    public void archiveAndRestore_wrongStatus_doesNotChangeData() throws Exception {
        assertThrows(CommandException.class, RestoreCommand.MESSAGE_WRONG_STATUS, () -> execute("restore 1"));
        execute("archive 1");
        execute("list archive");
        AddressBook before = new AddressBook(model.getAddressBook());
        assertThrows(CommandException.class, ArchiveCommand.MESSAGE_WRONG_STATUS, () -> execute("archive 1"));
        assertEquals(before, model.getAddressBook());
    }

    @Test
    public void archiveAndRestore_unknownId_doesNotChangeData() throws Exception {
        AddressBook before = new AddressBook(model.getAddressBook());
        String expectedMessage = String.format(Messages.MESSAGE_PERSON_ID_NOT_FOUND, 8);
        assertThrows(CommandException.class, expectedMessage, () -> execute("archive 8"));
        execute("list archive");
        assertThrows(CommandException.class, expectedMessage, () -> execute("restore 8"));
        assertEquals(before, model.getAddressBook());
    }

    @Test
    public void parser_invalidArchiveArguments_rejected() {
        for (String command : List.of("archive", "restore")) {
            for (String args : List.of("", " 0", " -1", " C1", " id/1", " abc", " 1 2", " 2147483648")) {
                assertThrows(ParseException.class, () -> parser.parseCommand(command + args));
            }
        }
    }

    @Test
    public void delete_archivedContact_deletedById() throws Exception {
        execute("archive 2");
        execute("list archive");
        execute("delete 2");
        assertFalse(model.hasPerson(BENSON));
        assertTrue(model.hasPerson(ALICE));
    }

    @Test
    public void archiveAndRestoreById_fromActiveView() throws Exception {
        execute("list");
        execute("archive 2");
        Person archivedBenson = new PersonBuilder(BENSON).withArchiveStatus(true).build();
        assertEquals(archivedBenson, model.getAddressBook().getPersonList().get(1));
        assertFalse(model.getFilteredPersonList().contains(archivedBenson));

        // BENSON is hidden in the active view, but his ID still selects him
        execute("restore 2");
        assertEquals(BENSON, model.getAddressBook().getPersonList().get(1));
        assertTrue(model.getFilteredPersonList().contains(BENSON));
    }

    @Test
    public void archiveById_personHiddenByFind() throws Exception {
        execute("find Alice");
        execute("archive 2");
        assertEquals(ALICE, model.getAddressBook().getPersonList().get(0));
        assertEquals(new PersonBuilder(BENSON).withArchiveStatus(true).build(),
                model.getAddressBook().getPersonList().get(1));
    }

    private void execute(String command) throws Exception {
        parser.parseCommand(command).execute(model);
    }
}
