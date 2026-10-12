package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.SortCommandParser.MESSAGE_INVALID_SORT_FIELD;
import static seedu.address.logic.parser.SortCommandParser.MESSAGE_INVALID_SORT_ORDER;
import static seedu.address.model.person.SortField.ADDRESS;
import static seedu.address.model.person.SortField.EMAIL;
import static seedu.address.model.person.SortField.NAME;
import static seedu.address.model.person.SortField.PHONE;
import static seedu.address.model.person.SortField.TAG;
import static seedu.address.model.person.SortOrder.ASCENDING;
import static seedu.address.model.person.SortOrder.DESCENDING;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SortCommand;

public class SortCommandParserTest {

    private static final String INVALID_COMMAND_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);

    private final SortCommandParser parser = new SortCommandParser();

    @Test
    public void parse_supportedFieldWithoutOrder_returnsAscendingSortCommand() {
        assertParseSuccess(parser, "name", new SortCommand(NAME, ASCENDING));
        assertParseSuccess(parser, "phone", new SortCommand(PHONE, ASCENDING));
        assertParseSuccess(parser, "email", new SortCommand(EMAIL, ASCENDING));
        assertParseSuccess(parser, "address", new SortCommand(ADDRESS, ASCENDING));
        assertParseSuccess(parser, "tag", new SortCommand(TAG, ASCENDING));
    }

    @Test
    public void parse_explicitOrder_returnsSortCommandWithSelectedOrder() {
        assertParseSuccess(parser, "name asc", new SortCommand(NAME, ASCENDING));
        assertParseSuccess(parser, "name desc", new SortCommand(NAME, DESCENDING));
    }

    @Test
    public void parse_mixedCaseFieldAndOrder_returnsNormalizedSortCommand() {
        assertParseSuccess(parser, "NAME DESC", new SortCommand(NAME, DESCENDING));
        assertParseSuccess(parser, "pHoNe aSc", new SortCommand(PHONE, ASCENDING));
    }

    @Test
    public void parse_leadingTrailingAndRepeatedWhitespace_returnsSortCommand() {
        assertParseSuccess(parser, "  \n\t email  \t  desc \r\n", new SortCommand(EMAIL, DESCENDING));
    }

    @Test
    public void parse_missingField_throwsInvalidCommandFormat() {
        assertParseFailure(parser, "", INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, " \t\r\n ", INVALID_COMMAND_FORMAT);
    }

    @Test
    public void parse_unsupportedField_throwsInvalidSortField() {
        assertParseFailure(parser, "date", MESSAGE_INVALID_SORT_FIELD);
        assertParseFailure(parser, "organisation", MESSAGE_INVALID_SORT_FIELD);
        assertParseFailure(parser, "name!", MESSAGE_INVALID_SORT_FIELD);
    }

    @Test
    public void parse_unsupportedOrder_throwsInvalidSortOrder() {
        assertParseFailure(parser, "name upward", MESSAGE_INVALID_SORT_ORDER);
        assertParseFailure(parser, "name email", MESSAGE_INVALID_SORT_ORDER);
        assertParseFailure(parser, "name ascending", MESSAGE_INVALID_SORT_ORDER);
        assertParseFailure(parser, "name descending", MESSAGE_INVALID_SORT_ORDER);
        assertParseFailure(parser, "name up", MESSAGE_INVALID_SORT_ORDER);
        assertParseFailure(parser, "name down", MESSAGE_INVALID_SORT_ORDER);
    }

    @Test
    public void parse_tooManyTokens_throwsInvalidCommandFormat() {
        assertParseFailure(parser, "name asc extra", INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, "name asc desc", INVALID_COMMAND_FORMAT);
    }
}
