package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Locale;

import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ListCommand.View;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ListCommand object
 */
public class ListCommandParser implements Parser<ListCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ListCommand
     * and returns a ListCommand object for execution.
     * No keyword lists active contacts, {@code all} lists every contact and {@code archive}
     * lists archived contacts. Keywords are case-insensitive.
     * @throws ParseException if the user input is not one of these keywords
     */
    @Override
    public ListCommand parse(String args) throws ParseException {
        return switch (args.trim().toLowerCase(Locale.ROOT)) {
            case "" -> new ListCommand(View.ACTIVE);
            case "all" -> new ListCommand(View.ALL);
            case "archive" -> new ListCommand(View.ARCHIVED);
            default -> throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        };
    }
}
