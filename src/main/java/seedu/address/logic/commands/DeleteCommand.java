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
 * Deletes a person identified using its displayed index or its ID from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person identified by the index number used in the displayed person list, "
            + "or by its ID.\n"
            + "Parameters: INDEX (must be a positive integer) or " + PREFIX_ID + "ID\n"
            + "Example: " + COMMAND_WORD + " 1 or " + COMMAND_WORD + " " + PREFIX_ID + "C3";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted person: %1$s";

    private final PersonTarget target;

    /**
     * Creates a DeleteCommand to delete the person selected by {@code target}.
     */
    public DeleteCommand(PersonTarget target) {
        requireNonNull(target);
        this.target = target;
    }

    /**
     * Creates a DeleteCommand to delete the person at {@code targetIndex} in the displayed person list.
     */
    public DeleteCommand(Index targetIndex) {
        this(PersonTarget.ofIndex(targetIndex));
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person personToDelete = target.resolve(model);
        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand otherDeleteCommand)) {
            return false;
        }

        return target.equals(otherDeleteCommand.target);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("target", target)
                .toString();
    }
}
