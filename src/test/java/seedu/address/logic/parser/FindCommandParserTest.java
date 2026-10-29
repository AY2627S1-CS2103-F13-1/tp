package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

public class FindCommandParserTest {

    private final FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyInput_matchesNoPersons() throws Exception {
        assertMatches("");
        assertMatches("     ");
        assertMatches(" \n \t ");
    }

    @Test
    public void parse_generalSubstring_preservesWholePhrase() throws Exception {
        assertMatches(" Alice Pauline ", ALICE);
        assertMatches(" \n \t Alice Pauline \t \n ", ALICE);
        assertMatches(" Pauline Alice ");
        assertMatches(" Alice  Pauline ", ALICE);
        assertMatches(" Alice   Pauline ", ALICE);
    }

    @Test
    public void parse_eachPrefix_matchesItsField() throws Exception {
        assertMatches(" n/Mei", BENSON, DANIEL);
        assertMatches(" p/9435", ALICE, CARL);
        assertMatches(" e/johnd", BENSON);
        assertMatches(" a/Clementi", BENSON);
        assertMatches(" t/Money", BENSON);
    }

    @Test
    public void parse_generalAndPrefixes_requiresEveryCondition() throws Exception {
        assertMatches(" example n/Mei", BENSON, DANIEL);
        assertMatches(" Clementi n/Mei t/fri", BENSON);
        assertMatches(" nonexistent n/Mei");
        assertMatches(" Clementi n/Alice");
    }

    @Test
    public void parse_allPrefixCombinations_acceptsOptionalGeneralSubstring() throws Exception {
        String[] fields = {"n/Benson", "p/98765432", "e/johnd", "a/Clementi", "t/Money"};
        for (int mask = 1; mask < (1 << fields.length); mask++) {
            StringBuilder arguments = new StringBuilder();
            for (int index = 0; index < fields.length; index++) {
                if ((mask & (1 << index)) != 0) {
                    arguments.append(" ").append(fields[index]);
                }
            }
            assertMatches(arguments.toString(), BENSON);
            assertMatches("example" + arguments, BENSON);
        }
    }

    @Test
    public void parse_prefixesInDifferentOrders_matchesSamePersons() throws Exception {
        assertMatches(" example n/Ben p/987 e/johnd a/Clementi t/fri t/Money", BENSON);
        assertMatches(" example t/fri a/Clementi e/johnd t/Money p/987 n/Ben", BENSON);
    }

    @Test
    public void parse_textAfterPrefix_belongsToThatPrefix() throws Exception {
        assertMatches(" example n/Alice Pauline", ALICE);
        assertMatches(" n/Alice Pauline example");
        assertMatches(" n/Alice Pauline example e/example");
        assertMatches(" n/ Alice Pauline \t", ALICE);
        assertMatches(" n/Alice  Pauline", ALICE);
        assertMatches(" n/Alice   Pauline", ALICE);
    }

    @Test
    public void parse_repeatedTags_requiresEveryTagSubstring() throws Exception {
        assertMatches(" t/fri t/Money", BENSON);
        assertMatches(" t/Money t/Money", BENSON);
        assertMatches(" t/Money t/missing");
    }

    @Test
    public void parse_repeatedNonTagPrefixes_usesLastValue() throws Exception {
        assertMatches(" n/Alice n/Benson", BENSON);
        assertMatches(" p/9435 p/987", BENSON);
        assertMatches(" e/alice e/johnd", BENSON);
        assertMatches(" a/Jurong a/Clementi", BENSON);
    }

    @Test
    public void parse_emptyFieldSubstring_doesNotRestrictThatField() throws Exception {
        assertMatches(" Clementi n/ p/ e/ a/", BENSON);
    }

    private void assertMatches(String arguments, Person... expectedPersons) throws Exception {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        FindCommand command = parser.parse(arguments);
        assertEquals(String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, expectedPersons.length),
                command.execute(model).getFeedbackToUser(), arguments);
        assertEquals(List.of(expectedPersons), model.getFilteredPersonList(), arguments);
    }
}
