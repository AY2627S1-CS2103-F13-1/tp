package seedu.address.logic.parser;

import seedu.address.logic.commands.RestoreCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new RestoreCommand object
 */
public class RestoreCommandParser implements Parser<RestoreCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the RestoreCommand
     * and returns a RestoreCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public RestoreCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args);
        return new RestoreCommand(ParserUtil.parsePersonTarget(argMultimap, RestoreCommand.MESSAGE_USAGE));
    }

}
