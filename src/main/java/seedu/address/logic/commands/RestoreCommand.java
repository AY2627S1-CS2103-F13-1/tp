package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Restores a contact identified by its ID.
 */
public class RestoreCommand extends Command {

    public static final String COMMAND_WORD = "restore";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Restores the contact with the given ID (shown as \"ID: 3\" on its card).\n"
            + "Parameters: ID (must be a positive whole number)\n"
            + "Example: " + COMMAND_WORD + " 3";

    public static final String MESSAGE_RESTORE_PERSON_SUCCESS = "Restored person: %1$s";

    public static final String MESSAGE_WRONG_STATUS = "This person is not archived.";

    private final PersonTarget target;

    /**
     * Creates a RestoreCommand to restore the person selected by {@code target}.
     */
    public RestoreCommand(PersonTarget target) {
        this.target = requireNonNull(target);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person personToChange = target.resolve(model);
        if (!personToChange.isArchived()) {
            throw new CommandException(MESSAGE_WRONG_STATUS);
        }
        Person updatedPerson = personToChange.withIsArchived(false);
        model.setPerson(personToChange, updatedPerson);
        return new CommandResult(String.format(MESSAGE_RESTORE_PERSON_SUCCESS, Messages.format(personToChange)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RestoreCommand otherRestoreCommand)) {
            return false;
        }

        return target.equals(otherRestoreCommand.target);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("target", target)
                .toString();
    }
}
