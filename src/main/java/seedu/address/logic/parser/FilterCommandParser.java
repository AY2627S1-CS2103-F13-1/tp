package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;
import java.util.Set;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.HasAllTagsPredicate;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new {@code FilterCommand} object.
 */
public class FilterCommandParser implements Parser<FilterCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the {@code FilterCommand}.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    @Override
    public FilterCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, PREFIX_TAG);
        List<String> tagNames = arguments.getAllValues(PREFIX_TAG);

        if (!arguments.getPreamble().isEmpty() || tagNames.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }

        Set<Tag> tags = ParserUtil.parseTags(tagNames);
        return new FilterCommand(new HasAllTagsPredicate(tags));
    }
}
