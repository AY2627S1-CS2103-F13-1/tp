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
        assertEquals(new PersonId("C42"), new PersonId(42));
        assertEquals(42, new PersonId("C42").getValue());
    }

    @Test
    public void isValidPersonId() {
        // null person ID
        assertThrows(NullPointerException.class, () -> PersonId.isValidPersonId(null));

        // invalid person IDs
        assertFalse(PersonId.isValidPersonId("")); // empty string
        assertFalse(PersonId.isValidPersonId(" ")); // spaces only
        assertFalse(PersonId.isValidPersonId("C")); // missing number
        assertFalse(PersonId.isValidPersonId("c42")); // lowercase prefix
        assertFalse(PersonId.isValidPersonId("C0")); // zero
        assertFalse(PersonId.isValidPersonId("C042")); // leading zero
        assertFalse(PersonId.isValidPersonId("C-1")); // negative number
        assertFalse(PersonId.isValidPersonId("42")); // missing prefix
        assertFalse(PersonId.isValidPersonId("C4 2")); // spaces within number
        assertFalse(PersonId.isValidPersonId(" C42")); // leading space
        assertFalse(PersonId.isValidPersonId("C42 ")); // trailing space
        assertFalse(PersonId.isValidPersonId("ID42")); // unrelated prefix
        assertFalse(PersonId.isValidPersonId("C4a")); // alphabets within number
        assertFalse(PersonId.isValidPersonId("C99999999999")); // too large

        // valid person IDs
        assertTrue(PersonId.isValidPersonId("C1")); // smallest ID
        assertTrue(PersonId.isValidPersonId("C42"));
        assertTrue(PersonId.isValidPersonId("C2147483647")); // largest ID
    }

    @Test
    public void toStringMethod() {
        assertEquals("C42", new PersonId("C42").toString());
        assertEquals("C7", new PersonId(7).toString());
    }

    @Test
    public void compareTo() {
        PersonId smaller = new PersonId("C2");
        PersonId larger = new PersonId("C10");

        // numeric, not lexicographic, ordering
        assertTrue(smaller.compareTo(larger) < 0);
        assertTrue(larger.compareTo(smaller) > 0);

        // same value
        assertEquals(0, smaller.compareTo(new PersonId(2)));
    }

    @Test
    public void equals() {
        PersonId personId = new PersonId("C42");

        // same values -> returns true
        assertTrue(personId.equals(new PersonId("C42")));
        assertEquals(personId.hashCode(), new PersonId("C42").hashCode());

        // same object -> returns true
        assertTrue(personId.equals(personId));

        // null -> returns false
        assertFalse(personId.equals(null));

        // different types -> returns false
        assertFalse(personId.equals(5.0f));

        // different values -> returns false
        assertFalse(personId.equals(new PersonId("C43")));
    }
}
