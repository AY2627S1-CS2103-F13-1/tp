package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.storage.BackupStorage;

/** Exports every contact, including archived contacts, to a new JSON backup. */
public class ExportCommand extends Command {
    public static final String COMMAND_WORD = "export";
    public static final String MESSAGE_USAGE = "export PATH: Save all contacts to a new JSON file. "
            + "Example: export backups/contacts (.json is optional; quotes are optional for spaces)";

    private final Path path;

    /** Creates a snapshot export to the given destination. */
    public ExportCommand(Path path) {
        this.path = requireNonNull(path);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        try {
            new BackupStorage().exportAddressBook(model.getAddressBook(), path);
        } catch (IOException e) {
            throw new CommandException("Could not export contacts: " + e.getMessage(), e);
        }
        return new CommandResult("Exported all contacts to " + path);
    }
}
