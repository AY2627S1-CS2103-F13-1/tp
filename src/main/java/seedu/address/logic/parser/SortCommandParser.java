package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.SortField;
import seedu.address.model.person.SortOrder;

/**
 * Parses input arguments and creates a new {@code SortCommand} object.
 */
public class SortCommandParser implements Parser<SortCommand> {

    public static final String MESSAGE_INVALID_SORT_FIELD = "Invalid sort field. Valid fields are "
            + getValidSortFieldsDescription() + ".";
    public static final String MESSAGE_INVALID_SORT_ORDER = "Invalid sort order. ORDER must be "
            + getValidSortOrdersDescription() + ".";

    /**
     * Parses the given {@code String} of arguments in the context of the {@code SortCommand}.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    @Override
    public SortCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw createInvalidCommandFormatException();
        }

        String[] arguments = trimmedArgs.split("\\s+");
        if (arguments.length > 2) {
            throw createInvalidCommandFormatException();
        }

        SortField sortField = parseSortField(arguments[0]);
        SortOrder sortOrder = arguments.length == 1
                ? SortOrder.ASCENDING
                : parseSortOrder(arguments[1]);
        return new SortCommand(sortField, sortOrder);
    }

    private static SortField parseSortField(String field) throws ParseException {
        return SortField.fromCommandValue(field)
                .orElseThrow(() -> new ParseException(MESSAGE_INVALID_SORT_FIELD));
    }

    private static SortOrder parseSortOrder(String order) throws ParseException {
        return SortOrder.fromCommandValue(order)
                .orElseThrow(() -> new ParseException(MESSAGE_INVALID_SORT_ORDER));
    }

    private static String getValidSortFieldsDescription() {
        List<String> fields = Arrays.stream(SortField.values())
                .map(SortField::getCommandValue)
                .collect(Collectors.toList());
        String finalField = fields.remove(fields.size() - 1);
        return String.join(", ", fields) + ", and " + finalField;
    }

    private static String getValidSortOrdersDescription() {
        return Arrays.stream(SortOrder.values())
                .map(SortOrder::getCommandValue)
                .collect(Collectors.joining(" or "));
    }

    private static ParseException createInvalidCommandFormatException() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
    }
}
