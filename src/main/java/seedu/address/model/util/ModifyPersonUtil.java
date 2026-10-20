package seedu.address.model.util;

import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

import java.util.Set;

public class ModifyPersonUtil {
    /**
     *
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
