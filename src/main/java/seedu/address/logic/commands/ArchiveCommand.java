package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Archives a contact identified by its ID.
 */
public class ArchiveCommand extends Command {

    public static final String COMMAND_WORD = "archive";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Archives the contact with the given ID (shown as \"ID: 3\" on its card).\n"
            + "Parameters: ID (must be a positive whole number)\n"
            + "Example: " + COMMAND_WORD + " 3";

    public static final String MESSAGE_ARCHIVE_PERSON_SUCCESS = "Archived person: %1$s";

    public static final String MESSAGE_WRONG_STATUS = "This person is already archived.";

    private final PersonTarget target;

    /**
     * Creates an ArchiveCommand to archive the person selected by {@code target}.
     */
    public ArchiveCommand(PersonTarget target) {
        this.target = requireNonNull(target);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person personToChange = target.resolve(model);
        if (personToChange.isArchived()) {
            throw new CommandException(MESSAGE_WRONG_STATUS);
        }
        Person updatedPerson = new Person(personToChange.getId().orElseThrow(), personToChange.getName(),
                personToChange.getPhone(), personToChange.getEmail(), personToChange.getAddress(),
                personToChange.getTags(), true);
        model.setPerson(personToChange, updatedPerson);
        return new CommandResult(String.format(MESSAGE_ARCHIVE_PERSON_SUCCESS, Messages.format(personToChange)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ArchiveCommand otherArchiveCommand)) {
            return false;
        }

        return target.equals(otherArchiveCommand.target);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("target", target)
                .toString();
    }
}
