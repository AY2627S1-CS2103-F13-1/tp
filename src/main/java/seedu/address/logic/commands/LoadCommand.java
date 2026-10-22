package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.storage.BackupStorage;

/** Prepares replacement data in the staging model; LogicManager backs up non-default data before saving it. */
public class LoadCommand extends Command {
    public static final String COMMAND_WORD = "load";
    public static final String MESSAGE_USAGE = "load PATH: Prepare a JSON backup for replacement. "
            + "Example: load backups/contacts (.json is optional; quotes are optional for spaces)";

    private final Path path;

    /** Creates a load request for the specified backup. */
    public LoadCommand(Path path) {
        this.path = requireNonNull(path);
    }

    /** Returns the source path whose folder will also hold the automatic backup. */
    public Path getPath() {
        return path;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        try {
            model.setAddressBook(new BackupStorage().readAddressBook(path));
        } catch (IOException | IllegalValueException e) {
            throw new CommandException("Could not load contacts: " + e.getMessage(), e);
        }
        return new CommandResult("Loaded contacts from " + path + ".");
    }
}
