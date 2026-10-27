package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.person.PersonComparator.getComparator;

import java.util.Locale;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.person.SortField;
import seedu.address.model.person.SortOrder;

/**
 * Sorts the currently displayed contacts by a selected contact field and direction.
 */
public class SortCommand extends Command {

    public static final String COMMAND_WORD = "sort";

    public static final String MESSAGE_USAGE = "Usage: " + COMMAND_WORD + " FIELD [ORDER]";

    public static final String MESSAGE_SORT_SUCCESS = "Sorted contacts by %1$s in %2$s order.";

    private final SortField sortField;
    private final SortOrder sortOrder;

    /**
     * Creates a {@code SortCommand} for the specified field and direction.
     */
    public SortCommand(SortField sortField, SortOrder sortOrder) {
        this.sortField = requireNonNull(sortField);
        this.sortOrder = requireNonNull(sortOrder);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.sortFilteredPersonList(getComparator(sortField, sortOrder));

        String normalizedField = sortField.name().toLowerCase(Locale.ROOT);
        String normalizedOrder = sortOrder == SortOrder.ASCENDING ? "ascending" : "descending";
        return new CommandResult(String.format(MESSAGE_SORT_SUCCESS, normalizedField, normalizedOrder));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof SortCommand otherSortCommand)) {
            return false;
        }

        return sortField == otherSortCommand.sortField
                && sortOrder == otherSortCommand.sortOrder;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("sortField", sortField)
                .add("sortOrder", sortOrder)
                .toString();
    }
}
