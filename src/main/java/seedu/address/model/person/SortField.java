package seedu.address.model.person;

import java.util.Arrays;
import java.util.Optional;

/**
 * Represents a contact field that can be used for sorting.
 */
public enum SortField {
    NAME("name"),
    PHONE("phone"),
    EMAIL("email"),
    ADDRESS("address"),
    TAG("tag");

    private final String commandValue;

    SortField(String commandValue) {
        this.commandValue = commandValue;
    }

    /**
     * Returns the sort field represented by {@code commandValue}, ignoring case.
     */
    public static Optional<SortField> fromCommandValue(String commandValue) {
        return Arrays.stream(values())
                .filter(field -> field.commandValue.equalsIgnoreCase(commandValue))
                .findFirst();
    }

    /**
     * Returns the value used to represent this field in a sort command.
     */
    public String getCommandValue() {
        return commandValue;
    }
}
