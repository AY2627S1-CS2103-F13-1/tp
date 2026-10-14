package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.PersonTarget;
import seedu.address.logic.commands.TagCommand;
import seedu.address.model.person.PersonId;
import seedu.address.model.tag.Tag;

/**
 * Tests for TagCommandParser.
 */
public class TagCommandParserTest {

    private final TagCommandParser parser = new TagCommandParser();

    @Test
    public void parse_singleTag_success() {
        assertParseSuccess(parser, "1 t/expert",
                new TagCommand(
                        INDEX_FIRST_PERSON,
                        Set.of(new Tag("expert"))));
    }

    @Test
    public void parse_multipleTags_success() {
        assertParseSuccess(parser, "1 t/housing policy t/EXPERT",
                new TagCommand(
                        INDEX_FIRST_PERSON,
                        Set.of(
                                new Tag("housing policy"),
                                new Tag("expert"))));
    }

    @Test
    public void parse_missingIndex_failure() {
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
    public void parse_zeroIndex_failure() {
        assertParseFailure(parser, "0 t/expert",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        TagCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_nonNumericIndex_failure() {
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
                new TagCommand(INDEX_FIRST_PERSON,
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
    public void parse_validId_success() {
        assertParseSuccess(parser, " id/C2 t/expert",
                new TagCommand(
                        PersonTarget.ofId(new PersonId("C2")),
                        Set.of(new Tag("expert"))));
    }

    @Test
    public void parse_indexAndId_failure() {
        assertParseFailure(parser, "1 id/C2 t/expert",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        TagCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidId_failure() {
        assertParseFailure(parser, " id/hello t/expert",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        PersonId.MESSAGE_CONSTRAINTS));
    }

    @Test
    public void parse_duplicateId_failure() {
        assertParseFailure(parser, " id/C1 id/C2 t/expert",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ID));
    }

    @Test
    public void parse_idWithoutTags_failure() {
        assertParseFailure(parser, " id/C2",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        TagCommand.MESSAGE_USAGE));
    }
}
