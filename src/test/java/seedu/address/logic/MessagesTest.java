package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalPersons.CARL;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class MessagesTest {

    @Test
    public void format_personWithId_showsIdAfterName() {
        String expected = "Carl Kurz (ID: C3); Phone: 95352563; Email: heinz@example.com; Address: wall street; Tags: ";
        assertEquals(expected, Messages.format(CARL));
    }

    @Test
    public void format_personWithoutId_omitsId() {
        Person carlWithoutId = new Person(CARL.getName(), CARL.getPhone(), CARL.getEmail(), CARL.getAddress(),
                CARL.getTags());
        String expected = "Carl Kurz; Phone: 95352563; Email: heinz@example.com; Address: wall street; Tags: ";
        assertEquals(expected, Messages.format(carlWithoutId));
    }

    @Test
    public void format_personWithTags_listsTags() {
        Person person = new PersonBuilder(CARL).withTags("friends").build();
        assertEquals("Carl Kurz (ID: C3); Phone: 95352563; Email: heinz@example.com; Address: wall street; "
                + "Tags: [friends]", Messages.format(person));
    }
}
