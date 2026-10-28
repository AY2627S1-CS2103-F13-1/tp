package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;

/** Reads and writes independent JSON snapshots without modifying an imported file. */
public class BackupStorage {
    /** Writes all contacts and the ID counter to a new file; existing files are never overwritten. */
    public void exportAddressBook(ReadOnlyAddressBook addressBook, Path path) throws IOException {
        String json = JsonUtil.toJsonString(new JsonSerializableAddressBook(addressBook));
        Files.writeString(path, json, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
    }

    /** Saves a unique pre-load snapshot beside the file being loaded, retaining all earlier backups. */
    public Path backupBeforeLoad(ReadOnlyAddressBook addressBook, Path sourcePath) throws IOException {
        Path directory = sourcePath.toAbsolutePath().normalize().getParent();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        Path destination = directory.resolve("before-load-" + timestamp + "-" + UUID.randomUUID() + ".json");
        exportAddressBook(addressBook, destination);
        return destination;
    }

    /** Validates the entire snapshot before returning data for recovery. */
    public AddressBook readAddressBook(Path path) throws IOException, IllegalValueException {
        JsonSerializableAddressBook snapshot = JsonUtil.fromJsonString(Files.readString(path),
                JsonSerializableAddressBook.class);
        if (snapshot == null) {
            throw new IOException("The backup must contain an address book, not null.");
        }
        try {
            return snapshot.toModelType();
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IOException("The backup contains invalid contact data.", e);
        }
    }
}
