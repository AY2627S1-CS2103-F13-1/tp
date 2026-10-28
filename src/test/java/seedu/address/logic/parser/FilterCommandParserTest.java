package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.model.person.HasAllTagsPredicate;
import seedu.address.model.tag.Tag;

public class FilterCommandParserTest {

    private final FilterCommandParser parser = new FilterCommandParser();

    @Test
    public void parse_oneValidTag_success() {
        assertParseSuccess(parser, " t/expert", createFilterCommand("expert"));
    }

    @Test
    public void parse_multipleValidTags_success() {
        assertParseSuccess(parser, " t/housing t/expert", createFilterCommand("housing", "expert"));
    }

    @Test
    public void parse_multiWordTag_success() {
        assertParseSuccess(parser, " t/housing policy", createFilterCommand("housing policy"));
    }

    @Test
    public void parse_differentCapitalisation_success() {
        assertParseSuccess(parser, " t/Housing Policy t/EXPERT",
                createFilterCommand("housing policy", "expert"));
    }

    @Test
    public void parse_repeatedEquivalentTags_success() {
        assertParseSuccess(parser, " t/expert t/EXPERT", createFilterCommand("expert"));
    }

    @Test
    public void parse_noArguments_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", expectedMessage);
        assertParseFailure(parser, "   ", expectedMessage);
    }

    @Test
    public void parse_emptyTag_failure() {
        assertParseFailure(parser, " t/", Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidPunctuation_failure() {
        assertParseFailure(parser, " t/housing-policy", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " t/housing_policy", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " t/@expert", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " t/policy/research", Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_unexpectedPreamble_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " housing t/expert", expectedMessage);
    }

    @Test
    public void parse_unsupportedPrefix_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " n/Alex", expectedMessage);
    }

    @Test
    public void parse_validAndInvalidTags_failure() {
        assertParseFailure(parser, " t/expert t/housing-policy", Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_supportedTagsInAnyOrder_success() {
        FilterCommand expectedCommand = createFilterCommand("interviewee", "singapore", "economics");
        assertParseSuccess(parser, " t/interviewee t/singapore t/economics", expectedCommand);
        assertParseSuccess(parser, " t/economics t/interviewee t/singapore", expectedCommand);
    }

    private FilterCommand createFilterCommand(String... tagNames) {
        Set<Tag> tags = Arrays.stream(tagNames)
                .map(Tag::new)
                .collect(Collectors.toSet());
        return new FilterCommand(new HasAllTagsPredicate(tags));
    }
}
