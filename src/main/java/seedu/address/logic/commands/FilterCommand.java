package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;
import java.util.stream.Collectors;

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
    public static final String MESSAGE_AVAILABLE_TAGS = "Available tags: %1$s";

    private static final String NO_AVAILABLE_TAGS = "none";

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

        int numberOfMatchingPersons = model.getFilteredPersonList().size();
        logger.fine("Filter command displayed " + numberOfMatchingPersons + " contact(s).");

        String feedback = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, numberOfMatchingPersons);
        if (numberOfMatchingPersons == 0) {
            feedback += "\n" + String.format(MESSAGE_AVAILABLE_TAGS, formatAvailableTags(model));
        }

        return new CommandResult(feedback);
    }

    /**
     * Returns the unique tags in the address book as an alphabetically sorted, comma-separated string.
     */
    private static String formatAvailableTags(Model model) {
        String availableTags = model.getAddressBook().getPersonList().stream()
                .flatMap(person -> person.getTags().stream())
                .map(tag -> tag.tagName)
                .distinct()
                .sorted()
                .collect(Collectors.joining(", "));

        return availableTags.isEmpty() ? NO_AVAILABLE_TAGS : availableTags;
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
