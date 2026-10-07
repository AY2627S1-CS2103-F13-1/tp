package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.person.PersonId;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.TypicalPersons;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.json");
    private static final Path LEGACY_TYPICAL_PERSONS_FILE =
            TEST_DATA_FOLDER.resolve("legacyTypicalPersonsAddressBook.json");
    private static final Path DELETED_HIGHEST_ID_FILE = TEST_DATA_FOLDER.resolve("deletedHighestIdAddressBook.json");
    private static final Path COUNTER_BELOW_HIGHEST_ID_FILE =
            TEST_DATA_FOLDER.resolve("counterBelowHighestIdAddressBook.json");
    private static final Path MIXED_ID_FILE = TEST_DATA_FOLDER.resolve("mixedIdAddressBook.json");
    private static final Path DUPLICATE_PERSON_ID_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonIdAddressBook.json");
    private static final Path INVALID_NEXT_PERSON_ID_FILE =
            TEST_DATA_FOLDER.resolve("invalidNextPersonIdAddressBook.json");

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalPersonsAddressBook);
        assertFalse(dataFromFile.needsUpgrade());
    }

    @Test
    public void toModelType_legacyFileWithoutIds_assignsIdsInOrder() throws Exception {
        JsonSerializableAddressBook dataFromFile = readFile(LEGACY_TYPICAL_PERSONS_FILE);
        assertTrue(dataFromFile.needsUpgrade());
        // persons are assigned C1, C2, ... in file order, matching the typical address book
        assertEquals(TypicalPersons.getTypicalAddressBook(), dataFromFile.toModelType());
    }

    @Test
    public void toModelType_highestIdDeleted_keepsSavedNextPersonId() throws Exception {
        AddressBook addressBookFromFile = readFile(DELETED_HIGHEST_ID_FILE).toModelType();
        // not reconstructed from the highest remaining ID (C2), so C3 is not reused
        assertEquals(4, addressBookFromFile.getNextPersonId());
    }

    @Test
    public void toModelType_nextPersonIdNotAboveHighestId_raisesNextPersonId() throws Exception {
        AddressBook addressBookFromFile = readFile(COUNTER_BELOW_HIGHEST_ID_FILE).toModelType();
        assertEquals(6, addressBookFromFile.getNextPersonId());
    }

    @Test
    public void toModelType_someIdsMissing_assignsUnusedIds() throws Exception {
        JsonSerializableAddressBook dataFromFile = readFile(MIXED_ID_FILE);
        assertTrue(dataFromFile.needsUpgrade());

        AddressBook addressBookFromFile = dataFromFile.toModelType();
        assertEquals(new PersonId("C6"), addressBookFromFile.getPersonList().get(0).getId().get());
        assertEquals(new PersonId("C5"), addressBookFromFile.getPersonList().get(1).getId().get());
        assertEquals(7, addressBookFromFile.getNextPersonId());
    }

    @Test
    public void needsUpgrade_personWithoutIdAndNextPersonIdPresent_returnsTrue() {
        JsonAdaptedPerson personWithoutId = new JsonAdaptedPerson(new PersonBuilder().build());
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(2, List.of(personWithoutId));
        assertTrue(data.needsUpgrade());
    }

    @Test
    public void toModelType_duplicatePersonIds_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = readFile(DUPLICATE_PERSON_ID_FILE);
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON_ID,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_invalidNextPersonId_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = readFile(INVALID_NEXT_PERSON_ID_FILE);
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_INVALID_NEXT_PERSON_ID,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

    private static JsonSerializableAddressBook readFile(Path filePath) throws Exception {
        return JsonUtil.readJsonFile(filePath, JsonSerializableAddressBook.class).get();
    }

}
