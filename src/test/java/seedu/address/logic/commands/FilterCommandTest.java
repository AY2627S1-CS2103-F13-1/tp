package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.HasAllTagsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.SortField;
import seedu.address.model.person.SortOrder;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.PersonBuilder;

public class FilterCommandTest {

    private static final Person ALEX = new PersonBuilder().withId("1").withName("Alex Tan")
            .withTags("housing policy", "expert").build();
    private static final Person BETH = new PersonBuilder().withId("2").withName("Beth Lim")
            .withTags("housing policy").build();
    private static final Person CARLOS = new PersonBuilder().withId("3").withName("Carlos Ong")
            .withTags("expert", "economics").build();
    private static final Person DINA = new PersonBuilder().withId("4").withName("Dina Lee")
            .withTags("expert").withArchiveStatus(true).build();

    private AddressBook addressBook;
    private Model model;

    @BeforeEach
    public void setUp() {
        addressBook = new AddressBookBuilder()
                .withPerson(ALEX)
                .withPerson(BETH)
                .withPerson(CARLOS)
                .withPerson(DINA)
                .build();
        model = new ModelManager(addressBook, new UserPrefs());
    }

    @Test
    public void constructor_nullPredicate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new FilterCommand(null));
    }

    @Test
    public void execute_noMatchingContacts_returnsZeroResult() {
        CommandResult result = createFilterCommand("marine biology").execute(model);

        assertEquals(String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0), result.getFeedbackToUser());
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_oneMatchingContact_returnsOneResult() {
        CommandResult result = createFilterCommand("economics").execute(model);

        assertEquals(String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1), result.getFeedbackToUser());
        assertEquals(List.of(CARLOS), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleMatchingContacts_returnsEveryMatch() {
        CommandResult result = createFilterCommand("housing policy").execute(model);

        assertEquals(String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 2), result.getFeedbackToUser());
        assertEquals(List.of(ALEX, BETH), model.getFilteredPersonList());
    }

    @Test
    public void execute_typoWithinTolerance_returnsWholeTagMatches() {
        CommandResult result = createFilterCommand("housng policy").execute(model);

        assertEquals(String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 2), result.getFeedbackToUser());
        assertEquals(List.of(ALEX, BETH), model.getFilteredPersonList());
    }

    @Test
    public void execute_partialTag_returnsNoMatches() {
        CommandResult result = createFilterCommand("policy").execute(model);

        assertEquals(String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0), result.getFeedbackToUser());
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleRequiredTags_returnsOnlyContactsWithEveryTag() {
        createFilterCommand("housing policy", "expert").execute(model);

        assertEquals(List.of(ALEX), model.getFilteredPersonList());
        assertFalse(model.getFilteredPersonList().contains(BETH));
        assertFalse(model.getFilteredPersonList().contains(CARLOS));
    }

    @Test
    public void execute_multipleFuzzyRequiredTags_returnsOnlyContactsMatchingEveryTag() {
        createFilterCommand("housng policy", "expet").execute(model);

        assertEquals(List.of(ALEX), model.getFilteredPersonList());
    }

    @Test
    public void execute_replacesEarlierFilterAndEvaluatesAllStoredContacts() {
        createFilterCommand("housing policy").execute(model);
        assertEquals(List.of(ALEX, BETH), model.getFilteredPersonList());

        createFilterCommand("economics").execute(model);

        assertEquals(List.of(CARLOS), model.getFilteredPersonList());
    }

    @Test
    public void execute_afterSort_clearsDisplaySort() {
        new SortCommand(SortField.NAME, SortOrder.DESCENDING).execute(model);

        createFilterCommand("expert").execute(model);

        assertEquals(List.of(ALEX, CARLOS, DINA), model.getFilteredPersonList());
    }

    @Test
    public void execute_doesNotModifyStoredContacts() {
        AddressBook expectedAddressBook = new AddressBook(addressBook);

        createFilterCommand("expert").execute(model);

        assertEquals(expectedAddressBook, model.getAddressBook());
    }

    @Test
    public void execute_archivedMatchingContact_includesArchivedContact() {
        createFilterCommand("expert").execute(model);

        assertTrue(model.getFilteredPersonList().contains(DINA));
    }

    @Test
    public void equals_variousComparisons_returnsExpectedResult() {
        FilterCommand expertFilter = createFilterCommand("expert");
        FilterCommand expertFilterCopy = createFilterCommand("EXPERT");
        FilterCommand housingFilter = createFilterCommand("housing policy");

        assertTrue(expertFilter.equals(expertFilter));
        assertTrue(expertFilter.equals(expertFilterCopy));
        assertFalse(expertFilter.equals(housingFilter));
        assertFalse(expertFilter.equals(null));
        assertFalse(expertFilter.equals(1));
    }

    @Test
    public void toStringMethod() {
        HasAllTagsPredicate predicate = createPredicate("expert");
        FilterCommand filterCommand = new FilterCommand(predicate);
        String expected = FilterCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";

        assertEquals(expected, filterCommand.toString());
    }

    private FilterCommand createFilterCommand(String... tagNames) {
        return new FilterCommand(createPredicate(tagNames));
    }

    private HasAllTagsPredicate createPredicate(String... tagNames) {
        Set<Tag> tags = Arrays.stream(tagNames)
                .map(Tag::new)
                .collect(Collectors.toSet());
        return new HasAllTagsPredicate(tags);
    }
}
