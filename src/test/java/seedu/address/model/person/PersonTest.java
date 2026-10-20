package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class PersonTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getTags().remove(0));
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same name, all other attributes different -> returns true
        Person editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB)
                .withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // different name, all other attributes same -> returns false
        editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // name differs in case, all other attributes same -> returns false
        Person editedBob = new PersonBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertFalse(BOB.isSamePerson(editedBob));

        // name has trailing spaces, all other attributes same -> returns false
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new PersonBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertFalse(BOB.isSamePerson(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));
        assertEquals(ALICE.hashCode(), aliceCopy.hashCode());

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different address -> returns false
        editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));

        // different ID -> returns false
        editedAlice = new PersonBuilder(ALICE).withId("99").build();
        assertFalse(ALICE.equals(editedAlice));

        // ID vs no ID -> returns false
        Person aliceWithoutId = new Person(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(), ALICE.getAddress(),
                ALICE.getTags());
        assertFalse(ALICE.equals(aliceWithoutId));
    }

    @Test
    public void archiveStatus_affectsEqualityButPreservesIdentityAndDetails() {
        Person archived = new PersonBuilder(ALICE).withArchiveStatus(true).build();
        assertFalse(ALICE.equals(archived));
        assertFalse(archived.equals(ALICE));
        assertTrue(ALICE.isSamePerson(archived));
        assertEquals(ALICE.getId(), archived.getId());
        assertEquals(ALICE.getTags(), archived.getTags());
        assertEquals(ALICE, new PersonBuilder(archived).withArchiveStatus(false).build());
        assertFalse(ALICE.isArchived());
        assertEquals(archived, new PersonBuilder(ALICE).withArchiveStatus(true).build());
        assertEquals(archived.hashCode(), new PersonBuilder(ALICE).withArchiveStatus(true).build().hashCode());
    }

    @Test
    public void constructors_archiveStatusAndDefaults_preserved() {
        Person withoutId = new Person(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(),
                ALICE.getAddress(), ALICE.getTags(), true);
        Person withId = new Person(ALICE.getId().orElseThrow(), ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(),
                ALICE.getAddress(), ALICE.getTags(), true);
        assertTrue(withoutId.isArchived());
        assertTrue(withoutId.getId().isEmpty());
        assertTrue(withId.isArchived());
        assertEquals(ALICE.getId(), withId.getId());
        assertFalse(new Person(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(),
                ALICE.getAddress(), ALICE.getTags()).isArchived());
        assertFalse(new Person(ALICE.getId().orElseThrow(), ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(),
                ALICE.getAddress(), ALICE.getTags()).isArchived());
    }

    @Test
    public void constructors_explicitArchiveStatus_copyTagsAndRejectNulls() {
        Set<Tag> tags = new HashSet<>(ALICE.getTags());
        Person withoutId = new Person(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(),
                ALICE.getAddress(), tags, true);
        Person withId = new Person(ALICE.getId().orElseThrow(), ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(),
                ALICE.getAddress(), tags, true);
        tags.clear();
        assertEquals(ALICE.getTags(), withoutId.getTags());
        assertEquals(ALICE.getTags(), withId.getTags());
        assertThrows(NullPointerException.class, () -> new Person(null, ALICE.getName(), ALICE.getPhone(),
                ALICE.getEmail(), ALICE.getAddress(), ALICE.getTags(), true));
        assertThrows(NullPointerException.class, () -> new Person(null, ALICE.getPhone(), ALICE.getEmail(),
                ALICE.getAddress(), ALICE.getTags(), true));
    }

    @Test
    public void getId() {
        assertEquals(Optional.of(new PersonId("1")), ALICE.getId());
        assertEquals(Optional.empty(), new PersonBuilder().build().getId());
    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName() + "{id=" + ALICE.getId().get()
                + ", name=" + ALICE.getName() + ", phone=" + ALICE.getPhone()
                + ", email=" + ALICE.getEmail() + ", address=" + ALICE.getAddress() + ", tags=" + ALICE.getTags()
                + ", isArchived=false}";
        assertEquals(expected, ALICE.toString());
    }
}
