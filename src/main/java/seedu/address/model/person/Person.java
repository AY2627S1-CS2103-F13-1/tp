package seedu.address.model.person;

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
    private final boolean archived;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Creates a person that has not been assigned an ID yet.
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        requireAllNonNull(name, phone, email, address, tags);
        this.id = null;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.archived = false;
        this.tags.addAll(tags);
    }

    /**
     * Creates a person with the given {@code id}.
     * Every field must be present and not null.
     */
    public Person(PersonId id, Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        requireAllNonNull(id, name, phone, email, address, tags);
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.archived = false;
        this.tags.addAll(tags);
    }

    /**
     * Copies a person while changing only archive status, including for persons awaiting an ID.
     */
    private Person(Person source, boolean archived) {
        this.id = source.id;
        this.name = source.name;
        this.phone = source.phone;
        this.email = source.email;
        this.address = source.address;
        this.tags.addAll(source.tags);
        this.archived = archived;
    }

    /**
     * Returns the ID of this person, or {@code Optional#empty()} if it has not been assigned one yet.
     */
    public Optional<PersonId> getId() {
        return Optional.ofNullable(id);
    }

    public boolean isArchived() {
        return archived;
    }

    /**
     * Returns an immutable copy with the requested archive status and the same ID and details.
     */
    public Person withArchived(boolean archived) {
        return new Person(this, archived);
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
                && archived == otherPerson.archived;
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(id, name, phone, email, address, tags, archived);
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
                .add("archived", archived)
                .toString();
    }

}
