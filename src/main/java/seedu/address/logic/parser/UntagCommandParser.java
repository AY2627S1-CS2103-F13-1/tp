package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.UntagCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses arguments for the untag command.
 */
public class UntagCommandParser implements Parser<UntagCommand> {

    @Override
    public UntagCommand parse(String args) throws ParseException {
        requireNonNull(args);

        ArgumentMultimap arguments =
                ArgumentTokenizer.tokenize(args, PREFIX_TAG);

        Index index;

        try {
            index = ParserUtil.parseIndex(arguments.getPreamble());
        } catch (ParseException exception) {
            throw new ParseException(
                    String.format(
                            MESSAGE_INVALID_COMMAND_FORMAT,
                            UntagCommand.MESSAGE_USAGE),
                    exception);
        }

        List<String> tagNames = arguments.getAllValues(PREFIX_TAG);

        if (tagNames.isEmpty()) {
            throw new ParseException(
                    String.format(
                            MESSAGE_INVALID_COMMAND_FORMAT,
                            UntagCommand.MESSAGE_USAGE));
        }

        Set<Tag> tags = ParserUtil.parseTags(tagNames);

        return new UntagCommand(index, tags);
    }
}
