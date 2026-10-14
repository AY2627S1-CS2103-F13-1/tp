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
import seedu.address.testutil.PersonBuilder;

/**
 * Exercises archive commands through the parser with real filtered and sorted model lists.
 */
public class ArchiveWorkflowTest {
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void archiveThenRestore_preservesDetailsIdAndCounter() throws Exception {
        execute("list active");
        int nextId = model.getAddressBook().getNextPersonId();
        execute("archive 1");
        assertFalse(model.getFilteredPersonList().contains(ALICE));
        assertEquals(new PersonBuilder(ALICE).withArchiveStatus(true).build(),
                model.getAddressBook().getPersonList().get(0));
        assertEquals(getTypicalAddressBook().getPersonList().size(), model.getAddressBook().getPersonList().size());

        execute("list archived");
        assertEquals(List.of(new PersonBuilder(ALICE).withArchiveStatus(true).build()), model.getFilteredPersonList());
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
        assertEquals(List.of(new PersonBuilder(BENSON).withArchiveStatus(true).build()), model.getFilteredPersonList());
        assertEquals(ALICE, model.getAddressBook().getPersonList().get(0));
        assertEquals(new PersonBuilder(BENSON).withArchiveStatus(true).build(),
                model.getAddressBook().getPersonList().get(1));
    }

    @Test
    public void archiveAndRestore_sortedList_targetsDisplayedPerson() throws Exception {
        execute("list active");
        model.sortFilteredPersonList(Comparator.comparing((Person person) -> person.getName().fullName).reversed());
        Person target = model.getFilteredPersonList().get(0);
        execute("archive 1");
        execute("archive 1");
        execute("list archived");
        model.sortFilteredPersonList(Comparator.comparing((Person person) -> person.getName().fullName).reversed());
        assertEquals(new PersonBuilder(target).withArchiveStatus(true).build(), model.getFilteredPersonList().get(0));
        execute("restore 1");
        assertTrue(model.getAddressBook().getPersonList().contains(target));
        assertEquals(1, model.getFilteredPersonList().size());
    }

    @Test
    public void listViews_selectRequestedContactsAndPreserveDefault() throws Exception {
        execute("archive 1");
        assertEquals(7, model.getFilteredPersonList().size());
        assertTrue(model.getFilteredPersonList().get(0).isArchived());
        execute("list active");
        assertEquals(6, model.getFilteredPersonList().size());
        assertTrue(model.getFilteredPersonList().stream().noneMatch(Person::isArchived));
        execute("list archived");
        assertEquals(List.of(new PersonBuilder(ALICE).withArchiveStatus(true).build()), model.getFilteredPersonList());
        execute("list");
        assertEquals(model.getAddressBook().getPersonList(), model.getFilteredPersonList());
        execute("restore 1");
        assertEquals(ALICE, model.getFilteredPersonList().get(0));
        execute("list archived");
        assertTrue(model.getFilteredPersonList().isEmpty());
        execute("list 3"); // Existing list behavior ignores unrelated arguments.
        assertEquals(7, model.getFilteredPersonList().size());
    }

    @Test
    public void startup_allContactsVisible() {
        AddressBook book = new AddressBook();
        book.addPerson(new PersonBuilder(ALICE).withArchiveStatus(true).build());
        book.addPerson(BENSON);
        Model restarted = new ModelManager(book, new UserPrefs());
        assertEquals(List.of(new PersonBuilder(ALICE).withArchiveStatus(true).build(),
                BENSON), restarted.getFilteredPersonList());
        assertTrue(restarted.hasPerson(ALICE));
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
