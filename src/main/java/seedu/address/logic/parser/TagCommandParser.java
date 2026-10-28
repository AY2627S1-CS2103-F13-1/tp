package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;
import java.util.Set;

import seedu.address.logic.commands.PersonTarget;
import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses arguments for the tag command.
 */
public class TagCommandParser implements Parser<TagCommand> {

    @Override
    public TagCommand parse(String args) throws ParseException {
        requireNonNull(args);

        ArgumentMultimap arguments =
                ArgumentTokenizer.tokenize(args, PREFIX_TAG);

        PersonTarget target = ParserUtil.parsePersonTarget(arguments, TagCommand.MESSAGE_USAGE);

        List<String> tagNames = arguments.getAllValues(PREFIX_TAG);

        if (tagNames.isEmpty()) {
            throw new ParseException(
                    String.format(
                            MESSAGE_INVALID_COMMAND_FORMAT,
                            TagCommand.MESSAGE_USAGE));
        }

        Set<Tag> tags = ParserUtil.parseTags(tagNames);

        return new TagCommand(target, tags);
    }
}
