package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.BackupStorage;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/** Exercises backup commands through parsing, model changes and persistence. */
public class BackupWorkflowTest {
    @TempDir
    public Path temporaryFolder;

    private Model model;
    private Logic logic;
    private Path backup;
    private JsonAddressBookStorage storage;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        backup = temporaryFolder.resolve("backup with spaces.json");
        storage = new JsonAddressBookStorage(temporaryFolder.resolve("live.json"));
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
    }

    @Test
    public void exportThenReplace_preservesAllFieldsAndCounter() throws Exception {
        model.addPerson(new PersonBuilder().withName("Active Contact").build());
        model.addPerson(new PersonBuilder().withName("Archived Contact")
                .withTags("source").withArchiveStatus(true).build());
        AddressBook original = new AddressBook(model.getAddressBook());
        assertEquals(1, model.getFilteredPersonList().size());

        logic.execute("export \"" + backup + "\"");
        logic.execute("clear");
        logic.execute("load " + backup);

        assertEquals(original, model.getAddressBook());
        assertEquals(original, storage.readAddressBook().orElseThrow());
        assertEquals(original, new BackupStorage().readAddressBook(backup));
    }

    @Test
    public void load_invalidOrMissingFile_leavesContactsUnchanged() throws Exception {
        model.addPerson(new PersonBuilder().build());
        AddressBook before = new AddressBook(model.getAddressBook());
        assertThrows(CommandException.class, () -> logic.execute("load " + backup));
        for (String invalid : new String[] {"broken JSON", "null", "{}", "{\"persons\":[null]}",
            "{\"persons\":[],\"nextPersonId\":0}"}) {
            Files.writeString(backup, invalid);
            assertThrows(CommandException.class, () -> logic.execute("load " + backup));
            assertEquals(before, model.getAddressBook());
            assertEquals(invalid, Files.readString(backup));
        }
    }

    @Test
    public void export_existingFile_isNotOverwritten() throws Exception {
        Files.writeString(backup, "existing backup");
        assertThrows(CommandException.class, () -> logic.execute("export " + backup));
        assertEquals("existing backup", Files.readString(backup));
    }

    @Test
    public void load_emptySnapshot_replacesCurrentContacts() throws Exception {
        new BackupStorage().exportAddressBook(new AddressBook(), backup);
        model.addPerson(new PersonBuilder().build());
        logic.execute("load " + backup);
        assertTrue(model.getAddressBook().getPersonList().isEmpty());
    }

    @Test
    public void load_saveFailure_keepsCurrentModelAndBackup() throws Exception {
        model.addPerson(new PersonBuilder().withName("Original").build());
        AddressBook before = new AddressBook(model.getAddressBook());
        new BackupStorage().exportAddressBook(new AddressBook(), backup);
        String snapshotBefore = Files.readString(backup);
        JsonAddressBookStorage failingStorage = new JsonAddressBookStorage(temporaryFolder.resolve("failed.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw new IOException("Simulated save failure");
            }
        };
        logic = new LogicManager(model, new StorageManager(failingStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));

        CommandException failure = assertThrows(CommandException.class, () -> logic.execute("load " + backup));
        assertEquals(before, model.getAddressBook());
        assertEquals(snapshotBefore, Files.readString(backup));
        Path saved = backupFiles().getFirst();
        assertEquals(before, new BackupStorage().readAddressBook(saved));
        assertTrue(failure.getMessage().contains(saved.toString()));
    }

    @Test
    public void load_defaultStateSaveFailure_keepsOriginalAndDoesNotClaimBackup() throws Exception {
        AddressBook replacement = new AddressBook();
        replacement.addPerson(new PersonBuilder().build());
        new BackupStorage().exportAddressBook(replacement, backup);
        String sourceBefore = Files.readString(backup);
        JsonAddressBookStorage failingStorage = new JsonAddressBookStorage(temporaryFolder.resolve("failed.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw new IOException("Simulated save failure");
            }
        };
        logic = new LogicManager(model, new StorageManager(failingStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));

        for (ReadOnlyAddressBook original : new ReadOnlyAddressBook[] {
            new AddressBook(), SampleDataUtil.getSampleAddressBook()}) {
            model.setAddressBook(original);
            CommandException failure = assertThrows(CommandException.class, () -> logic.execute("load " + backup));
            assertEquals(String.format(LogicManager.FILE_OPS_ERROR_FORMAT, "Simulated save failure"),
                    failure.getMessage());
            assertEquals(original, model.getAddressBook());
            assertTrue(backupFiles().isEmpty());
            assertEquals(sourceBefore, Files.readString(backup));
        }
    }

    @Test
    public void load_success_isUndoableAndRedoable() throws Exception {
        model.addPerson(new PersonBuilder().withName("Original").build());
        model.commitAddressBook();
        AddressBook original = new AddressBook(model.getAddressBook());
        AddressBook replacement = new AddressBook(30);
        replacement.addPerson(new PersonBuilder().withName("Loaded").withArchiveStatus(true).build());
        new BackupStorage().exportAddressBook(replacement, backup);

        logic.execute("load " + backup);
        assertEquals(replacement, model.getAddressBook());
        logic.execute("undo");
        assertEquals(original, model.getAddressBook());
        assertEquals(original, storage.readAddressBook().orElseThrow());
        logic.execute("redo");
        assertEquals(replacement, model.getAddressBook());
        assertEquals(replacement, storage.readAddressBook().orElseThrow());
        assertEquals(1, backupFiles().size());
    }

    @Test
    public void exportAndFailedLoad_preserveRedoHistory() throws Exception {
        model.addPerson(new PersonBuilder().build());
        model.commitAddressBook();
        AddressBook original = new AddressBook(model.getAddressBook());
        logic.execute("archive 1");
        AddressBook archived = new AddressBook(model.getAddressBook());
        logic.execute("undo");

        logic.execute("export " + backup);
        assertTrue(model.canRedoAddressBook());
        assertThrows(CommandException.class, () -> logic.execute("load " + temporaryFolder.resolve("missing.json")));
        assertEquals(original, model.getAddressBook());
        assertTrue(model.canRedoAddressBook());
        logic.execute("redo");
        assertEquals(archived, model.getAddressBook());
    }

    @Test
    public void export_missingParent_reportsFailure() {
        Path missingParent = temporaryFolder.resolve("missing").resolve("backup.json");
        assertThrows(CommandException.class, () -> logic.execute("export " + missingParent));
        assertFalse(Files.exists(missingParent));
    }

    @Test
    public void load_customData_createsRecoverableBackup() throws Exception {
        model.addPerson(new PersonBuilder().withName("Current").withTags("source")
                .withArchiveStatus(true).build());
        AddressBook before = new AddressBook(model.getAddressBook());
        new BackupStorage().exportAddressBook(new AddressBook(), backup);

        String feedback = logic.execute("load " + backup).getFeedbackToUser();

        assertTrue(model.getAddressBook().getPersonList().isEmpty());
        List<Path> backups = backupFiles();
        assertEquals(1, backups.size());
        assertTrue(feedback.contains(backups.getFirst().toString()));
        assertEquals(before, new BackupStorage().readAddressBook(backups.getFirst()));
        logic.execute("load " + backups.getFirst());
        assertEquals(before, model.getAddressBook());
        assertEquals(1, backupFiles().size());
    }

    @Test
    public void load_emptyOrSampleData_skipsBackup() throws Exception {
        AddressBook replacement = new AddressBook();
        replacement.addPerson(new PersonBuilder().build());
        new BackupStorage().exportAddressBook(replacement, backup);
        for (ReadOnlyAddressBook initial : new ReadOnlyAddressBook[] {
            new AddressBook(), new AddressBook(50), SampleDataUtil.getSampleAddressBook()}) {
            model.setAddressBook(initial);
            String feedback = logic.execute("load " + backup).getFeedbackToUser();
            assertTrue(feedback.contains("No backup needed"));
            assertEquals(replacement, model.getAddressBook());
            assertTrue(backupFiles().isEmpty());
        }
    }

    @Test
    public void load_modifiedSampleData_createsBackup() throws Exception {
        new BackupStorage().exportAddressBook(new AddressBook(), backup);
        for (String change : new String[] {"archive 1", "tag 1 t/source", "edit 1 p/12345678"}) {
            model.setAddressBook(SampleDataUtil.getSampleAddressBook());
            logic.execute(change);
            AddressBook before = new AddressBook(model.getAddressBook());
            logic.execute("load " + backup);
            boolean found = false;
            for (Path file : backupFiles()) {
                found |= new BackupStorage().readAddressBook(file).equals(before);
            }
            assertTrue(found);
        }
        assertEquals(3, backupFiles().size());
    }

    @Test
    public void load_invalidFile_doesNotCreateBackup() throws Exception {
        model.addPerson(new PersonBuilder().build());
        Files.writeString(backup, "invalid JSON");
        assertThrows(CommandException.class, () -> logic.execute("load " + backup));
        assertTrue(backupFiles().isEmpty());
    }

    @Test
    public void load_backupFailure_keepsCurrentDataOnDiskAndInMemory() throws Exception {
        model.addPerson(new PersonBuilder().build());
        AddressBook before = new AddressBook(model.getAddressBook());
        storage.saveAddressBook(before);
        BackupStorage failingBackup = new BackupStorage() {
            @Override
            public Path backupBeforeLoad(ReadOnlyAddressBook addressBook, Path sourcePath) throws IOException {
                throw new IOException("Backup folder is not writable");
            }
        };
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))), failingBackup);
        new BackupStorage().exportAddressBook(new AddressBook(), backup);

        assertThrows(CommandException.class, () -> logic.execute("load " + backup));
        assertEquals(before, model.getAddressBook());
        assertEquals(before, storage.readAddressBook().orElseThrow());
    }

    @Test
    public void load_repeatedLoads_retainSeparateBackups() throws Exception {
        new BackupStorage().exportAddressBook(new AddressBook(), backup);
        for (int i = 0; i < 2; i++) {
            model.addPerson(new PersonBuilder().build());
            logic.execute("load " + backup);
        }
        assertEquals(2, backupFiles().size());
    }

    @Test
    public void load_sourceInDifferentFolder_savesBackupBesideSource() throws Exception {
        Path sourceFolder = Files.createDirectory(temporaryFolder.resolve("chosen-folder"));
        backup = sourceFolder.resolve("contacts.json");
        model.addPerson(new PersonBuilder().build());
        AddressBook before = new AddressBook(model.getAddressBook());
        new BackupStorage().exportAddressBook(new AddressBook(), backup);
        String sourceBefore = Files.readString(backup);

        String feedback = logic.execute("load " + backup).getFeedbackToUser();

        Path saved = backupFiles().getFirst();
        assertEquals(sourceFolder, saved.getParent());
        assertEquals(before, new BackupStorage().readAddressBook(saved));
        assertEquals(sourceBefore, Files.readString(backup));
        assertTrue(feedback.contains(saved.toString()));
    }

    /** Returns the pre-load snapshots without relying on generated filenames or directory ordering. */
    private List<Path> backupFiles() throws IOException {
        try (var files = Files.list(backup.toAbsolutePath().getParent())) {
            return files.filter(path -> path.getFileName().toString().startsWith("before-load-")).toList();
        }
    }

    @Test
    public void exportAndLoad_extensionOptionalWithSpaces() throws Exception {
        Path folder = Files.createDirectory(temporaryFolder.resolve("My Contacts"));
        for (String name : new String[] {"October backup", "November backup.json", "December backup.JSON"}) {
            for (boolean quoted : new boolean[] {false, true}) {
                Path supplied = folder.resolve((quoted ? "quoted " : "unquoted ") + name);
                String argument = quoted ? "\"" + supplied + "\"" : supplied.toString();
                Path expected = name.contains(".") ? supplied : Path.of(supplied + ".json");
                model.addPerson(new PersonBuilder().withArchiveStatus(true).withTags("source").build());
                AddressBook original = new AddressBook(model.getAddressBook());

                logic.execute("export " + argument);
                assertEquals(original, new BackupStorage().readAddressBook(expected));
                assertFalse(Files.exists(Path.of(expected + ".json")));
                logic.execute("clear");
                logic.execute("load " + argument);
                assertEquals(original, model.getAddressBook());
                logic.execute("clear");
            }
        }
    }

    @Test
    public void parser_invalidPath_rejected() {
        String invalidPath = "bad" + (char) 0 + "path";
        assertThrows(ParseException.class, () -> logic.execute("export " + invalidPath));
        assertThrows(ParseException.class, () -> logic.execute("load " + invalidPath));
    }

    @Test
    public void parser_requiresPath() {
        for (String command : new String[] {"export", "export \"\"", "load", "load \"\"",
            "import file.json"}) {
            assertThrows(ParseException.class, () -> logic.execute(command));
        }
    }
}
