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
 * Adds one or more tags to an existing contact.
 */
public class TagCommand extends Command {

    public static final String COMMAND_WORD = "tag";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds tags to a contact without removing existing tags.\n"
            + "Parameters: ID (must be a positive whole number) "
            + PREFIX_TAG + "TAG [" + PREFIX_TAG + "TAG]...\n"
            + "Example: tag 3 t/housing policy t/expert";

    public static final String MESSAGE_SUCCESS = "Tagged contact: %1$s";

    private final PersonId targetId;
    private final Set<Tag> tagsToAdd;

    /**
     * Creates a command that adds tags to the contact selected by {@code targetId}.
     */
    public TagCommand(PersonId targetId, Set<Tag> tagsToAdd) {
        requireNonNull(targetId);
        requireNonNull(tagsToAdd);
        this.targetId = targetId;
        this.tagsToAdd = new HashSet<>(tagsToAdd);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Person original = model.findPersonById(targetId).orElseThrow(() ->
                new CommandException(String.format(Messages.MESSAGE_PERSON_ID_NOT_FOUND, targetId.getValue())));

        Set<Tag> updatedTags = new HashSet<>(original.getTags());
        updatedTags.addAll(tagsToAdd);

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

        if (!(other instanceof TagCommand otherCommand)) {
            return false;
        }

        return targetId.equals(otherCommand.targetId)
                && tagsToAdd.equals(otherCommand.tagsToAdd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetId, tagsToAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetId", targetId)
                .add("tagsToAdd", tagsToAdd)
                .toString();
    }
}
