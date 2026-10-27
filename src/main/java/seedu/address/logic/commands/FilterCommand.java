package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.HasAllTagsPredicate;

/**
 * Filters and lists contacts that have every specified tag.
 */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Filters contacts that have every specified tag.\n"
            + "Parameters: t/TAG [t/TAG]...\n"
            + "Example: " + COMMAND_WORD + " t/housing policy t/expert";

    private static final Logger logger = LogsCenter.getLogger(FilterCommand.class);

    private final HasAllTagsPredicate predicate;

    /**
     * Creates a command that displays contacts matching {@code predicate}.
     */
    public FilterCommand(HasAllTagsPredicate predicate) {
        this.predicate = requireNonNull(predicate);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);

        int numberOfMatchingContacts = model.getFilteredPersonList().size();
        logger.fine("Filter command displayed " + numberOfMatchingContacts + " contact(s).");

        return new CommandResult(
                String.format(Messages.MESSAGE_CONTACTS_LISTED_OVERVIEW, numberOfMatchingContacts));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FilterCommand otherFilterCommand)) {
            return false;
        }

        return predicate.equals(otherFilterCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
