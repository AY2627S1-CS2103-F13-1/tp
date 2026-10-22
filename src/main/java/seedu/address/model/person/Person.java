package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final PersonId id; // null until the person is added to an address book
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final boolean isArchived;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Creates an active person that has not been assigned an ID yet.
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        this(name, phone, email, address, tags, false);
    }

    /**
     * Creates a person without an ID and with the given archive status.
     * Used when loading records whose IDs have not yet been assigned.
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags, boolean isArchived) {
        this(name, phone, email, address, tags, isArchived, null);
    }

    /**
     * Creates a person with the given ID and archive status.
     * Every field must be present and not null.
     */
    public Person(PersonId id, Name name, Phone phone, Email email, Address address, Set<Tag> tags,
            boolean isArchived) {
        this(name, phone, email, address, tags, isArchived, requireNonNull(id));
    }

    /**
     * Initialises every field. {@code id} is null for a person who has not been assigned an ID yet.
     */
    private Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags, boolean isArchived,
            PersonId id) {
        requireAllNonNull(name, phone, email, address, tags);
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
        this.isArchived = isArchived;
    }

    /**
     * Returns the ID of this person, or {@code Optional#empty()} if it has not been assigned one yet.
     */
    public Optional<PersonId> getId() {
        return Optional.ofNullable(id);
    }

    public boolean isArchived() {
        return isArchived;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both persons have the same name.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getName().equals(getName());
    }

    /**
     * Returns a copy of this Person with updated isArchived value.
     *
     * @param newIsArchived New value of isArchived.
     * @return Person.
     */
    public Person withIsArchived(boolean newIsArchived) {
        return new Person(name, phone, email, address, tags, newIsArchived, id);
    }

    /**
     * Returns a copy of this Person with updated tags.
     *
     * @param newTags New tags.
     * @return Person.
     */
    public Person withTags(Set<Tag> newTags) {
        return new Person(name, phone, email, address, newTags, isArchived, id);
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return Objects.equals(id, otherPerson.id)
                && name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && tags.equals(otherPerson.tags)
                && isArchived == otherPerson.isArchived;
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(id, name, phone, email, address, tags, isArchived);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("id", id)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tags", tags)
                .add("isArchived", isArchived)
                .toString();
    }

}
