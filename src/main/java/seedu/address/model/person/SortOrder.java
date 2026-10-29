package seedu.address.model.person;

import java.util.Arrays;
import java.util.Optional;

/**
 * Represents the direction in which contacts can be sorted.
 */
public enum SortOrder {
    ASCENDING("asc", "ascending"),
    DESCENDING("desc", "descending");

    private final String commandValue;
    private final String description;

    SortOrder(String commandValue, String description) {
        this.commandValue = commandValue;
        this.description = description;
    }

    /**
     * Returns the sort order represented by {@code commandValue}, ignoring case.
     */
    public static Optional<SortOrder> fromCommandValue(String commandValue) {
        return Arrays.stream(values())
                .filter(order -> order.commandValue.equalsIgnoreCase(commandValue))
                .findFirst();
    }

    /**
     * Returns the value used to represent this order in a sort command.
     */
    public String getCommandValue() {
        return commandValue;
    }

    /**
     * Returns the user-facing description of this sort order.
     */
    public String getDescription() {
        return description;
    }
}
