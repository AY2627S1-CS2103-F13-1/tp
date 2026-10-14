package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Restores a contact identified by its index in the currently displayed list or by its ID.
 */
public class RestoreCommand extends Command {

    public static final String COMMAND_WORD = "restore";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Restores the person identified by the index number used in the displayed person list, "
            + "or by its ID.\n"
            + "Parameters: INDEX (must be a positive integer) or " + PREFIX_ID + "ID\n"
            + "Example: " + COMMAND_WORD + " 1 or " + COMMAND_WORD + " " + PREFIX_ID + "C3";

    public static final String MESSAGE_RESTORE_PERSON_SUCCESS = "Restored person: %1$s";

    public static final String MESSAGE_WRONG_STATUS = "This person is not archived.";

    private final PersonTarget target;

    /**
     * Creates a RestoreCommand to restore the person selected by {@code target}.
     */
    public RestoreCommand(PersonTarget target) {
        this.target = requireNonNull(target);
    }

    /**
     * Creates a RestoreCommand to restore the person at {@code targetIndex} in the displayed person list.
     */
    public RestoreCommand(Index targetIndex) {
        this(PersonTarget.ofIndex(targetIndex));
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person personToChange = target.resolve(model);
        if (!personToChange.isArchived()) {
            throw new CommandException(MESSAGE_WRONG_STATUS);
        }
        Person updatedPerson = new Person(personToChange.getId().orElseThrow(), personToChange.getName(),
                personToChange.getPhone(), personToChange.getEmail(), personToChange.getAddress(),
                personToChange.getTags(), false);
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
