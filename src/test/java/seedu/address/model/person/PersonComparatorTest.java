package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.person.PersonComparator.getComparator;
import static seedu.address.model.person.SortField.ADDRESS;
import static seedu.address.model.person.SortField.EMAIL;
import static seedu.address.model.person.SortField.NAME;
import static seedu.address.model.person.SortField.PHONE;
import static seedu.address.model.person.SortField.TAG;
import static seedu.address.model.person.SortOrder.ASCENDING;
import static seedu.address.model.person.SortOrder.DESCENDING;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PersonComparatorTest {

    @Test
    public void getComparator_name_ordersIgnoringCaseInBothDirections() {
        Person alice = personWithName("alice");
        Person bob = personWithName("Bob");
        Person charlie = personWithName("CHARLIE");
        List<Person> persons = List.of(bob, charlie, alice);

        assertOrder(persons, getComparator(NAME, ASCENDING), alice, bob, charlie);
        assertOrder(persons, getComparator(NAME, DESCENDING), charlie, bob, alice);
    }

    @Test
    public void getComparator_phone_ordersNumericallyInBothDirections() {
        Person nine = personWithNameAndPhone("Nine", "009");
        Person twenty = personWithNameAndPhone("Twenty", "020");
        Person hundred = personWithNameAndPhone("Hundred", "100");
        List<Person> persons = List.of(hundred, nine, twenty);

        assertOrder(persons, getComparator(PHONE, ASCENDING), nine, twenty, hundred);
        assertOrder(persons, getComparator(PHONE, DESCENDING), hundred, twenty, nine);
    }

    @Test
    public void getComparator_email_ordersCompleteValueIgnoringCaseInBothDirections() {
        Person alpha = personWithNameAndEmail("Alpha", "A@example.com");
        Person middle = personWithNameAndEmail("Middle", "middle@example.com");
        Person zulu = personWithNameAndEmail("Zulu", "z@example.com");
        List<Person> persons = List.of(middle, zulu, alpha);

        assertOrder(persons, getComparator(EMAIL, ASCENDING), alpha, middle, zulu);
        assertOrder(persons, getComparator(EMAIL, DESCENDING), zulu, middle, alpha);
    }

    @Test
    public void getComparator_address_ordersCompleteValueIgnoringCaseInBothDirections() {
        Person alpha = personWithNameAndAddress("Alpha", "alpha street");
        Person notAvailable = personWithNameAndAddress("NotAvailable", "NA");
        Person zulu = personWithNameAndAddress("Zulu", "ZULU STREET");
        List<Person> persons = List.of(notAvailable, zulu, alpha);

        assertOrder(persons, getComparator(ADDRESS, ASCENDING), alpha, notAvailable, zulu);
        assertOrder(persons, getComparator(ADDRESS, DESCENDING), zulu, notAvailable, alpha);
    }

    @Test
    public void getComparator_tag_ordersPrimaryTagIgnoringCaseInBothDirections() {
        Person alpha = personWithNameAndTags("Alpha", "zulu", "ALPHA");
        Person middle = personWithNameAndTags("Middle", "Middle");
        Person zulu = personWithNameAndTags("Zulu", "zulu");
        List<Person> persons = List.of(middle, zulu, alpha);

        assertOrder(persons, getComparator(TAG, ASCENDING), alpha, middle, zulu);
        assertOrder(persons, getComparator(TAG, DESCENDING), zulu, middle, alpha);
    }

    @Test
    public void getComparator_phoneWithLeadingZeroes_comparesEqualPrimaryValueByName() {
        Person alpha = personWithNameAndPhone("Alpha", "000123");
        Person beta = personWithNameAndPhone("Beta", "123");

        assertOrder(List.of(beta, alpha), getComparator(PHONE, ASCENDING), alpha, beta);
        assertOrder(List.of(beta, alpha), getComparator(PHONE, DESCENDING), alpha, beta);
    }

    @Test
    public void getComparator_phoneLargerThanLong_ordersWithoutOverflow() {
        Person smaller = personWithNameAndPhone("Smaller", "999999999999999999999999999999");
        Person larger = personWithNameAndPhone("Larger", "1000000000000000000000000000000");

        assertOrder(List.of(larger, smaller), getComparator(PHONE, ASCENDING), smaller, larger);
        assertOrder(List.of(smaller, larger), getComparator(PHONE, DESCENDING), larger, smaller);
    }

    @Test
    public void getComparator_tagWithMultipleTags_usesAlphabeticallyFirstTag() {
        Person alphaPrimary = personWithNameAndTags("ZuluName", "zulu", "Alpha", "middle");
        Person betaPrimary = personWithNameAndTags("AlphaName", "Beta");

        assertOrder(List.of(betaPrimary, alphaPrimary), getComparator(TAG, ASCENDING),
                alphaPrimary, betaPrimary);
    }

    @Test
    public void getComparator_tagWithNoTags_placesUntaggedLastInBothDirections() {
        Person alpha = personWithNameAndTags("Alpha", "alpha");
        Person zulu = personWithNameAndTags("Zulu", "zulu");
        Person untagged = personWithName("Untagged");
        List<Person> persons = List.of(untagged, alpha, zulu);

        assertOrder(persons, getComparator(TAG, ASCENDING), alpha, zulu, untagged);
        assertOrder(persons, getComparator(TAG, DESCENDING), zulu, alpha, untagged);
    }

    @Test
    public void getComparator_firstPersonUntagged_placesUntaggedLastAndUsesNameTieBreaker() {
        Person alphaUntagged = personWithName("Alpha");
        Person zuluUntagged = personWithName("Zulu");
        Person tagged = personWithNameAndTags("Tagged", "friend");
        Comparator<Person> comparator = getComparator(TAG, ASCENDING);

        assertTrue(comparator.compare(alphaUntagged, tagged) > 0);
        assertTrue(comparator.compare(alphaUntagged, zuluUntagged) < 0);
    }

    @Test
    public void getComparator_primaryValueTie_usesNameAscending() {
        Person alpha = personWithNameAndEmail("alpha", "same@example.com");
        Person zulu = personWithNameAndEmail("Zulu", "SAME@example.com");

        assertOrder(List.of(zulu, alpha), getComparator(EMAIL, ASCENDING), alpha, zulu);
    }

    @Test
    public void getComparator_descendingPrimaryValueTie_keepsNameAscending() {
        Person alpha = personWithNameAndAddress("Alpha", "Same address");
        Person zulu = personWithNameAndAddress("Zulu", "same ADDRESS");

        assertOrder(List.of(zulu, alpha), getComparator(ADDRESS, DESCENDING), alpha, zulu);
    }

    private static Person personWithName(String name) {
        return new PersonBuilder().withName(name).build();
    }

    private static Person personWithNameAndPhone(String name, String phone) {
        return new PersonBuilder().withName(name).withPhone(phone).build();
    }

    private static Person personWithNameAndEmail(String name, String email) {
        return new PersonBuilder().withName(name).withEmail(email).build();
    }

    private static Person personWithNameAndAddress(String name, String address) {
        return new PersonBuilder().withName(name).withAddress(address).build();
    }

    private static Person personWithNameAndTags(String name, String... tags) {
        return new PersonBuilder().withName(name).withTags(tags).build();
    }

    private static void assertOrder(List<Person> persons, Comparator<Person> comparator,
            Person... expectedPersons) {
        List<Person> sortedPersons = new ArrayList<>(persons);
        sortedPersons.sort(comparator);
        assertEquals(Arrays.asList(expectedPersons), sortedPersons);
    }
}
