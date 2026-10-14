package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.LogicManager;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

/**
 * Verifies archived contacts survive disk storage and legacy records stay active.
 */
public class ArchiveStorageTest {
    @TempDir
    public Path testFolder;

    @Test
    public void saveAndLoad_mixedContacts_preservesAllFieldsAndCounter() throws Exception {
        AddressBook book = new AddressBook(10);
        book.addPerson(ALICE.withArchived(true));
        book.addPerson(BENSON);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(testFolder.resolve("contacts.json"));
        storage.saveAddressBook(book);
        assertEquals(book, new AddressBook(storage.readAddressBook().orElseThrow()));
    }

    @Test
    public void read_missingArchivedField_defaultsToActive() throws Exception {
        Path path = testFolder.resolve("legacy.json");
        Files.writeString(path, """
                {"nextPersonId": 2, "persons": [{"id": "C1", "name": "Alex", "phone": "91234567",
                  "email": "alex@example.com", "address": "Singapore", "tags": []}]}
                """);
        Person loaded = new JsonAddressBookStorage(path).readAddressBook().orElseThrow().getPersonList().get(0);
        assertFalse(loaded.isArchived());
        assertEquals("C1", loaded.getId().orElseThrow().toString());
    }

    @Test
    public void read_archivedWithoutId_keepsStatusDuringIdAssignment() throws Exception {
        Path path = testFolder.resolve("legacy.json");
        Files.writeString(path, """
                {"persons": [{"name": "Alex", "phone": "91234567", "email": "alex@example.com",
                  "address": "Singapore", "tags": [], "archived": true}]}
                """);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(path);
        Person loaded = storage.readAddressBook().orElseThrow().getPersonList().get(0);
        assertTrue(loaded.isArchived());
        assertEquals("C1", loaded.getId().orElseThrow().toString());
        assertEquals(loaded, storage.readAddressBook().orElseThrow().getPersonList().get(0));
    }

    @Test
    public void execute_archiveAndRestore_savedThroughLogicManager() throws Exception {
        AddressBook book = new AddressBook();
        book.addPerson(ALICE);
        ModelManager model = new ModelManager(book, new UserPrefs());
        JsonAddressBookStorage storage = new JsonAddressBookStorage(testFolder.resolve("contacts.json"));
        StorageManager storageManager = new StorageManager(storage,
                new JsonUserPrefsStorage(testFolder.resolve("preferences.json")));
        LogicManager logic = new LogicManager(model, storageManager);
        logic.execute("archive 1");
        assertEquals(ALICE.withArchived(true), storage.readAddressBook().orElseThrow().getPersonList().get(0));
        logic.execute("list archived");
        logic.execute("restore 1");
        assertEquals(ALICE, storage.readAddressBook().orElseThrow().getPersonList().get(0));
    }
}
