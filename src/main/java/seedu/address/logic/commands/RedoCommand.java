package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Redoes the last change to the address book that was undone.
 */
public class RedoCommand extends Command {

    public static final String COMMAND_WORD = "redo";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Redoes the last change you undid.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Redid the last undone change.";
    public static final String MESSAGE_NOTHING_TO_REDO = "There is nothing to redo.";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (!model.canRedoAddressBook()) {
            throw new CommandException(MESSAGE_NOTHING_TO_REDO);
        }

        model.redoAddressBook();
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof RedoCommand;
    }
}
