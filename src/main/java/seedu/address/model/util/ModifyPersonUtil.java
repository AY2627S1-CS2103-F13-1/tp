package seedu.address.model.util;

import java.util.NoSuchElementException;
import java.util.Set;

import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Provides utility methods for creating copies of a {@link Person} with a change in only one field.
 * Preserves all other fields without modifying the original person.
 */
public class ModifyPersonUtil {

    private ModifyPersonUtil() {
    }

    /**
     * Returns a specified Person with a changed isArchived value.
     *
     * @param person Person to modify.
     * @param isArchived New value of isArchived.
     * @return Person.
     * @throws NoSuchElementException If the id of the Person cannot be retrieved.
     */
    public static Person setIsArchived(Person person, boolean isArchived) {
        return new Person(
                person.getId().orElseThrow(),
                person.getName(),
                person.getPhone(),
                person.getEmail(),
                person.getAddress(),
                person.getTags(),
                isArchived);
    }

    /**
     * Returns a specified Person with changed tags.
     *
     * @param person Person to modify.
     * @param tags New tags that will replace the existing tags in the Person.
     * @return Person.
     * @throws NoSuchElementException If the id of the Person cannot be retrieved.
     */
    public static Person setTags(Person person, Set<Tag> tags) {
        return new Person(
                person.getId().orElseThrow(),
                person.getName(),
                person.getPhone(),
                person.getEmail(),
                person.getAddress(),
                tags,
                person.isArchived());
    }
}
