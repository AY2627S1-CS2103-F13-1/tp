package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
            + "Parameters: ID (must be a positive whole number) "
            + PREFIX_TAG + "TAG [" + PREFIX_TAG + "TAG]...\n"
            + "Example: untag 3 t/housing policy t/expert";

    public static final String MESSAGE_SUCCESS = "Removed tags from contact: %1$s";

    private final PersonId targetId;
    private final Set<Tag> tagsToRemove;

    /**
     * Creates a command that removes tags from the contact selected by {@code targetId}.
     */
    public UntagCommand(PersonId targetId, Set<Tag> tagsToRemove) {
        requireNonNull(targetId);
        requireNonNull(tagsToRemove);

        this.targetId = targetId;
        this.tagsToRemove = new HashSet<>(tagsToRemove);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Person original = model.findPersonById(targetId).orElseThrow(() ->
                new CommandException(String.format(Messages.MESSAGE_PERSON_ID_NOT_FOUND, targetId.getValue())));

        Set<Tag> updatedTags = new HashSet<>(original.getTags());
        updatedTags.removeAll(tagsToRemove);

        if (updatedTags.equals(original.getTags())) {
            throw new CommandException(
                    "No tags were removed because the contact does not have the specified tags.");
        }

        Person updated = original.withTags(updatedTags);

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

        return targetId.equals(otherCommand.targetId)
                && tagsToRemove.equals(otherCommand.tagsToRemove);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetId, tagsToRemove);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetId", targetId)
                .add("tagsToRemove", tagsToRemove)
                .toString();
    }
}
