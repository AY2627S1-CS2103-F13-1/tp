package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.tag.Tag;

/**
 * Adds one or more tags to an existing contact.
 */
public class TagCommand extends Command {

    public static final String COMMAND_WORD = "tag";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds tags to a contact without removing existing tags.\n"
            + "Parameters: INDEX (positive integer) "
            + PREFIX_TAG + "TAG [" + PREFIX_TAG + "TAG]...\n"
            + "Example: tag 1 t/housing policy t/expert";

    public static final String MESSAGE_SUCCESS = "Tagged contact: %1$s";

    private final Index index;
    private final Set<Tag> tagsToAdd;

    /**
     * Creates a command that adds tags to a contact.
     */
    public TagCommand(Index index, Set<Tag> tagsToAdd) {
        requireNonNull(index);
        requireNonNull(tagsToAdd);
        this.index = index;
        this.tagsToAdd = new HashSet<>(tagsToAdd);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> displayedPersons = model.getFilteredPersonList();

        if (index.getZeroBased() >= displayedPersons.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person original = displayedPersons.get(index.getZeroBased());

        Set<Tag> updatedTags = new HashSet<>(original.getTags());
        updatedTags.addAll(tagsToAdd);

        PersonId id = original.getId().orElseThrow();

        Person updated = new Person(
                id,
                original.getName(),
                original.getPhone(),
                original.getEmail(),
                original.getAddress(),
                updatedTags).withArchived(original.isArchived());

        model.setPerson(original, updated);

        return new CommandResult(
                String.format(MESSAGE_SUCCESS, Messages.format(updated)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TagCommand otherCommand)) {
            return false;
        }

        return index.equals(otherCommand.index)
                && tagsToAdd.equals(otherCommand.tagsToAdd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, tagsToAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("tagsToAdd", tagsToAdd)
                .toString();
    }
}
