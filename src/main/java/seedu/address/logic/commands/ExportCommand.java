package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.storage.BackupStorage;

/** Exports every contact, including archived contacts, requesting confirmation for existing files. */
public class ExportCommand extends Command {
    public static final String COMMAND_WORD = "export";
    public static final String MESSAGE_USAGE = "export PATH: Save all contacts to a new JSON file. "
            + "Example: export backups/contacts (.json is optional; quotes are optional for spaces). "
            + "If the file exists, enter Y to overwrite it or N to cancel when prompted. "
            + "Exports always use JSON. Missing extensions get .json appended; "
            + "other extensions are replaced (contacts.csv becomes contacts.json).";

    private final Path path;

    /** Creates a snapshot export to the given destination. */
    public ExportCommand(Path path) {
        this.path = requireNonNull(path);
    }

    /** Returns the destination used when requesting overwrite confirmation. */
    public Path getPath() {
        return path;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        try {
            new BackupStorage().exportAddressBook(model.getAddressBook(), path);
        } catch (IOException e) {
            throw new CommandException("Could not export contacts: " + e.getMessage(), e);
        }
        return new CommandResult("Exported all contacts as JSON to " + path);
    }
}
