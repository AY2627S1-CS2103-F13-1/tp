package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.PersonTarget;
import seedu.address.logic.commands.TagCommand;
import seedu.address.model.person.PersonId;
import seedu.address.model.tag.Tag;

/**
 * Tests for TagCommandParser.
 */
public class TagCommandParserTest {

    private static final PersonTarget TARGET_FIRST = PersonTarget.ofId(new PersonId(1));

    private final TagCommandParser parser = new TagCommandParser();

    @Test
    public void parse_singleTag_success() {
        assertParseSuccess(parser, "1 t/expert",
                new TagCommand(
                        TARGET_FIRST,
                        Set.of(new Tag("expert"))));
    }

    @Test
    public void parse_multipleTags_success() {
        assertParseSuccess(parser, "1 t/housing policy t/EXPERT",
                new TagCommand(
                        TARGET_FIRST,
                        Set.of(
                                new Tag("housing policy"),
                                new Tag("expert"))));
    }

    @Test
    public void parse_missingId_failure() {
        assertParseFailure(parser, "t/expert",
                String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT,
                        TagCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingTags_failure() {
        assertParseFailure(parser, "1",
                String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT,
                        TagCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_zeroId_failure() {
        assertParseFailure(parser, "0 t/expert",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        TagCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_nonNumericId_failure() {
        assertParseFailure(parser, "abc t/expert",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        TagCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_emptyTag_failure() {
        assertParseFailure(parser, "1 t/", Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicateTags_success() {
        assertParseSuccess(parser, "1 t/EXPERT t/expert",
                new TagCommand(TARGET_FIRST,
                        Set.of(new Tag("expert"))));
    }

    @Test
    public void parse_invalidTag_failure() {
        assertParseFailure(
                parser,
                "1 t/invalid!",
                Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_idWithCOrPrefix_failure() {
        // IDs are typed as plain numbers, without the C or id/
        assertParseFailure(parser, "C2 t/expert",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        TagCommand.MESSAGE_USAGE));
        assertParseFailure(parser, " id/2 t/expert",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        TagCommand.MESSAGE_USAGE));
    }
}
