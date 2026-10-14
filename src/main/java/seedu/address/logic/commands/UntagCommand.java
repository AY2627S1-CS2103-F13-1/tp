package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.HashSet;
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
 * Removes one or more tags from an existing contact.
 */
public class UntagCommand extends Command {

    public static final String COMMAND_WORD = "untag";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes specified tags from a contact.\n"
            + "Parameters: INDEX (positive integer) or " + PREFIX_ID + "ID "
            + PREFIX_TAG + "TAG [" + PREFIX_TAG + "TAG]...\n"
            + "Example: untag 1 t/housing policy t/expert or untag " + PREFIX_ID + "C3 t/expert";

    public static final String MESSAGE_SUCCESS = "Removed tags from contact: %1$s";

    private final PersonTarget target;
    private final Set<Tag> tagsToRemove;

    /**
     * Creates a command that removes tags from the contact selected by {@code target}.
     */
    public UntagCommand(PersonTarget target, Set<Tag> tagsToRemove) {
        requireNonNull(target);
        requireNonNull(tagsToRemove);

        this.target = target;
        this.tagsToRemove = new HashSet<>(tagsToRemove);
    }

    /**
     * Creates a command that removes tags from the contact at {@code index} in the displayed list.
     */
    public UntagCommand(Index index, Set<Tag> tagsToRemove) {
        this(PersonTarget.ofIndex(index), tagsToRemove);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Person original = target.resolve(model);

        Set<Tag> updatedTags = new HashSet<>(original.getTags());
        updatedTags.removeAll(tagsToRemove);

        PersonId id = original.getId().orElseThrow();

        Person updated = new Person(
                id,
                original.getName(),
                original.getPhone(),
                original.getEmail(),
                original.getAddress(),
                updatedTags,
                original.isArchived());

        model.setPerson(original, updated);

        return new CommandResult(
                String.format(MESSAGE_SUCCESS, Messages.format(updated)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof UntagCommand otherCommand)) {
            return false;
        }

        return target.equals(otherCommand.target)
                && tagsToRemove.equals(otherCommand.tagsToRemove);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, tagsToRemove);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("target", target)
                .add("tagsToRemove", tagsToRemove)
                .toString();
    }
}
