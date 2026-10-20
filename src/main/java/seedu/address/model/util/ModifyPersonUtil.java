package seedu.address.model.util;

import seedu.address.model.person.Person;

public class ModifyPersonUtil {
    /**
     *
     */
    public static Person setIsArchived(Person person, boolean isArchived) {
        return new Person(person.getId().orElseThrow(), person.getName(),
                person.getPhone(), person.getEmail(), person.getAddress(),
                person.getTags(), isArchived);
    }
}
