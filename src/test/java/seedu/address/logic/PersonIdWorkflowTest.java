package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BOB;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalPersons.getTypicalPersons;

import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.tag.Tag;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonUtil;

/**
 * Uses contact IDs the way a user would. Typed commands go through {@code LogicManager}, which saves the address
 * book to a real file after every command, and a restart reads that file into a new model.
 */
public class PersonIdWorkflowTest {

    @TempDir
    public Path temporaryFolder;

    private StorageManager storage;
    private Model model;
    private Logic logic;

    @BeforeEach
    public void setUp() throws Exception {
        storage = new StorageManager(new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json")));
        // the typical persons have the IDs 1 to 7
        storage.saveAddressBook(getTypicalAddressBook());
        restart();
    }

    @Test
    public void fullWorkflow_idsStayStableAndAreNeverReused() throws Exception {
        // a new contact gets the next unused ID
        assertTrue(execute(PersonUtil.getAddCommand(AMY)).contains("(ID: 8)"));

        // hidden by find, but still edited by its ID
        execute("find Alice");
        execute("edit 8 p/91234567");
        assertEquals("91234567", findById(8).getPhone().value);

        // moved by sort, but still tagged and untagged by its ID
        execute("sort name desc");
        execute("tag 8 t/expert");
        execute("untag 8 t/expert");
        execute("tag 8 t/source");

        // archived, then restored from the active view, where it is hidden
        execute("archive 8");
        assertTrue(findById(8).isArchived());
        execute("list active");
        execute("restore 8");

        // after a restart, the contact keeps its ID and every change
        restart();
        Person amy = findById(8);
        assertEquals(AMY.getName(), amy.getName());
        assertEquals("91234567", amy.getPhone().value);
        assertEquals(Set.of(new Tag(VALID_TAG_FRIEND), new Tag("source")), amy.getTags());
        assertFalse(amy.isArchived());
        for (Person person : getTypicalPersons()) {
            assertEquals(person, model.findPersonById(person.getId().orElseThrow()).orElseThrow());
        }

        // a deleted ID is never given to a new contact
        execute("delete 8");
        assertTrue(execute(PersonUtil.getAddCommand(BOB)).contains("(ID: 9)"));

        // not even after another restart
        restart();
        assertEquals(BOB.getName(), findById(9).getName());
        assertThrows(CommandException.class, String.format(Messages.MESSAGE_PERSON_ID_NOT_FOUND, 8), () ->
                execute("delete 8"));
    }

    @Test
    public void clearThenRestart_counterIsKept() throws Exception {
        execute("clear");
        restart();

        // IDs 1 to 7 were used before the clear, so they are not given out again
        assertTrue(execute(PersonUtil.getAddCommand(AMY)).contains("(ID: 8)"));
        assertEquals(AMY.getName(), findById(8).getName());
    }

    /**
     * Starts the app again from the saved file, as happens when the user closes and reopens it.
     */
    private void restart() throws Exception {
        ReadOnlyAddressBook savedData = storage.readAddressBook().orElseThrow();
        model = new ModelManager(savedData, new UserPrefs());
        logic = new LogicManager(model, storage);
    }

    private String execute(String commandText) throws Exception {
        return logic.execute(commandText).getFeedbackToUser();
    }

    private Person findById(int id) {
        return model.findPersonById(new PersonId(id)).orElseThrow();
    }
}
