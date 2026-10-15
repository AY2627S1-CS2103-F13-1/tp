package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class PersonIdTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PersonId(null));
    }

    @Test
    public void constructor_invalidPersonId_throwsIllegalArgumentException() {
        String invalidPersonId = "";
        assertThrows(IllegalArgumentException.class, () -> new PersonId(invalidPersonId));
    }

    @Test
    public void constructor_nonPositiveValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new PersonId(0));
        assertThrows(IllegalArgumentException.class, () -> new PersonId(-1));
    }

    @Test
    public void constructor_validValue_matchesStringForm() {
        assertEquals(new PersonId("42"), new PersonId(42));
        assertEquals(42, new PersonId("42").getValue());
    }

    @Test
    public void isValidPersonId() {
        // null person ID
        assertThrows(NullPointerException.class, () -> PersonId.isValidPersonId(null));

        // invalid person IDs
        assertFalse(PersonId.isValidPersonId("")); // empty string
        assertFalse(PersonId.isValidPersonId(" ")); // spaces only
        assertFalse(PersonId.isValidPersonId("0")); // zero
        assertFalse(PersonId.isValidPersonId("042")); // leading zero
        assertFalse(PersonId.isValidPersonId("-1")); // negative number
        assertFalse(PersonId.isValidPersonId("+1")); // plus sign
        assertFalse(PersonId.isValidPersonId("C42")); // old format with a C prefix
        assertFalse(PersonId.isValidPersonId("4 2")); // spaces within number
        assertFalse(PersonId.isValidPersonId(" 42")); // leading space
        assertFalse(PersonId.isValidPersonId("42 ")); // trailing space
        assertFalse(PersonId.isValidPersonId("4a")); // alphabets within number
        assertFalse(PersonId.isValidPersonId("2147483648")); // too large

        // valid person IDs
        assertTrue(PersonId.isValidPersonId("1")); // smallest ID
        assertTrue(PersonId.isValidPersonId("42"));
        assertTrue(PersonId.isValidPersonId("2147483647")); // largest ID
    }

    @Test
    public void toStringMethod() {
        assertEquals("42", new PersonId("42").toString());
        assertEquals("7", new PersonId(7).toString());
    }

    @Test
    public void compareTo() {
        PersonId smaller = new PersonId("2");
        PersonId larger = new PersonId("10");

        // numeric, not lexicographic, ordering
        assertTrue(smaller.compareTo(larger) < 0);
        assertTrue(larger.compareTo(smaller) > 0);

        // same value
        assertEquals(0, smaller.compareTo(new PersonId(2)));
    }

    @Test
    public void equals() {
        PersonId personId = new PersonId("42");

        // same values -> returns true
        assertTrue(personId.equals(new PersonId("42")));
        assertEquals(personId.hashCode(), new PersonId("42").hashCode());

        // same object -> returns true
        assertTrue(personId.equals(personId));

        // null -> returns false
        assertFalse(personId.equals(null));

        // different types -> returns false
        assertFalse(personId.equals(5.0f));

        // different values -> returns false
        assertFalse(personId.equals(new PersonId("43")));
    }
}
