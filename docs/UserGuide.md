---
layout: page
title: User Guide
---

ByLine is a **desktop application for managing contacts, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, ByLine can help you manage contacts faster than traditional GUI applications.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/AY2627S1-CS2103-F13-1/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your ByLine.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01` : Adds a contact named `John Doe` to the Address Book.

   * `delete 3` : Deletes the contact with ID 3. Each contact's ID is shown as `ID: 3` on its card.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items followed by `…`​ can appear zero or more times.<br>
  For example, `[t/TAG]…​` may be omitted, or written as `t/friend` or `t/friend t/family`.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Contact IDs

Every contact has an ID, a whole number shown as `ID: 3` on the contact's card. Commands that act on one contact, such as `edit` and `delete`, use this ID to pick the contact.

* A new contact gets the next unused ID. The first contact is `1`, the next is `2`, and so on.
* A contact keeps its ID for good. The ID does not change when you use `find`, `sort` or `list`, archive the contact, or restart ByLine.
* Commands find the contact by its ID even if it is not in the list currently shown, e.g. after a `find`.
* IDs are never reused. After you delete a contact or run `clear`, the next new contact still gets a new ID.
* Type the ID as a plain number, e.g. `delete 3`.
* IDs go up to 999999. Because IDs are never reused, no more contacts can be added once ID 999999 has been given out.

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a person: `add`

Adds a person to the address book.

Format: `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​`

* The new contact gets the next unused ID, shown in the result message and on its card. See [Contact IDs](#contact-ids).
* If every ID up to 999999 has been used, ByLine shows `Cannot add more contacts: every contact ID up to 999999 has already been used.`

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
A person can have any number of tags, including zero. Tags can contain letters, numbers and spaces, and are saved in lowercase.
</div>

Examples:
* `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01`
* `add n/Betsy Crowe t/friend e/betsycrowe@example.com a/Newgate Prison p/1234567 t/criminal`

### Listing contacts: `list`

Shows contacts in the address book.

Formats:

* `list` shows all contacts, including archived ones.
* `list active` shows only contacts that are not archived.
* `list archived` shows only archived contacts.

### Editing a person: `edit`

Edits an existing person in the address book.

Format: `edit ID [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`

* Edits the contact with the given `ID`, shown as `ID: 3` on the contact's card. See [Contact IDs](#contact-ids).
* If no contact has that ID, ByLine shows `No contact has the ID 3.` and nothing is changed.
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, all of the person's existing tags are removed; adding tags is not cumulative.
* To remove all of a person's tags, enter `t/` without a tag after it.
* To add or remove only some tags, use [`tag`](#adding-tags-to-a-contact-tag) or [`untag`](#removing-tags-from-a-contact-untag) instead.

Examples:
*  `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the contact with ID 1 to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the contact with ID 2 to be `Betsy Crower` and clears all existing tags.

### Adding tags to a contact: `tag`

Adds one or more tags to a contact, keeping the tags it already has.

Format: `tag ID t/TAG [t/TAG]…​`

* Adds the given tags to the contact with the given `ID`. See [Contact IDs](#contact-ids).
* At least one tag must be given.
* A tag can contain letters, numbers and spaces, e.g. `housing policy`.
* Tags are saved in lowercase, so `t/Expert` is saved as `expert`. Tags that differ only in upper/lower case count as the same tag.

Examples:
* `tag 3 t/expert` adds the tag `expert` to the contact with ID 3.
* `tag 3 t/housing policy t/interviewee` adds the tags `housing policy` and `interviewee` to the contact with ID 3.

### Removing tags from a contact: `untag`

Removes one or more tags from a contact, keeping its other tags.

Format: `untag ID t/TAG [t/TAG]…​`

* Removes the given tags from the contact with the given `ID`. See [Contact IDs](#contact-ids).
* At least one tag must be given.
* Tags are matched ignoring upper/lower case, so `t/Expert` removes the tag `expert`.

Examples:
* `untag 3 t/expert` removes the tag `expert` from the contact with ID 3.
* `untag 3 t/housing policy t/interviewee` removes both tags from the contact with ID 3.

### Finding persons: `find`

Finds persons using part of their details. Search across all fields, or use prefixes to search specific fields.

Formats:

* Search across all fields: `find SUBSTRING`
* Search specific fields: `find [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]…`

For a field-specific search, specify at least one prefix.

* Searches are case-insensitive: `al` matches both `Alex` and `Balakrishnan`.
* Without prefixes, a person matches if the search text appears in any field.
* With prefixes, each substring is searched only within its corresponding field. A person must match every condition, so adding conditions narrows the results.
* Search text is matched as one consecutive substring, including internal spaces. For example, `Alex Ye` matches `Alex Yeoh`, but `Ye Alex` does not.
* Prefixes may appear in any order. Use each prefix at most once, except `t/`, which may be repeated.
* For multiple `t/` prefixes, every tag substring must match at least one of the person's tags.
* Do not put unprefixed search text before a prefix. For example, `find Alex e/example.com` is invalid; use `find n/Alex e/example.com`.

Examples:

* `find oak` finds persons with `oak` anywhere in their details, such as the name `Prof Oak` or the address `Oak Street`.
* `find n/Al` finds persons whose names contain `Al`.
  ![Results for 'find n/Al'](images/findNameAl.png)
* `find p/2345` finds persons whose phone numbers contain `2345`.
* `find n/Al e/example.com` finds persons whose names contain `Al` and whose email addresses contain `example.com`.
* `find t/fri t/col` finds persons whose tags match **both** substrings, such as someone tagged with both `friends` and `colleagues`. Persons tagged only with `friends` or only with `colleagues` are excluded. Adding field or tag conditions narrows the search instead of broadening it.
  ![Results for 'find t/fri t/col', requiring both tag conditions to match](images/findTagsFriCol.png)

### Sorting displayed contacts: `sort`

Arranges the currently displayed contacts by a selected contact field. Sorting changes only the displayed order; it does not change contact details or the order stored in the address book.

Format: `sort FIELD [ORDER]`

* `FIELD` is mandatory. Supported fields are `name`, `phone`, `email`, `address`, and `tag`.
* `ORDER` is optional. Supported orders are `asc` and `desc`; omitting `ORDER` uses `asc`.
* `FIELD` and `ORDER` are case-insensitive. For example, `sort NAME DESC` is valid.
* Name, email, and address values are compared alphabetically without regard to letter case. The complete stored value is compared, and the address `NA` is treated as ordinary text.
* Phone numbers are compared numerically. Leading zeroes do not affect their numeric value, so `000123` and `123` tie on phone number.
* For tag sorting, a contact's primary tag is the alphabetically first tag, ignoring letter case. Only that tag is used. Contacts without tags remain after tagged contacts in both ascending and descending order.
* Contacts that tie on the selected field are ordered by name in ascending order, regardless of the selected `ORDER`. If both the selected value and name tie, their existing relative order is preserved.
* Only the contacts currently displayed are sorted. A search result remains the same subset, and the stored contact order remains unchanged.
* Running `list` or a new search replaces the sorted display. Run `sort` again to sort the new displayed list.
* Sorting an empty list or a list containing one contact succeeds even though its visible order does not change.

On success, ByLine shows the following message, substituting the selected field and the full order name:

`Sorted contacts by FIELD in ORDER order.`

For example, `sort name desc` shows `Sorted contacts by name in descending order.`

![Result of sorting contacts by name in descending order](images/sortNameDescResult.png)

Invalid commands leave the displayed contact list unchanged and show one of these exact messages:

* Missing `FIELD` or extra arguments:<br>
  `Invalid command format!`<br>
  `Usage: sort FIELD [ORDER]`
* Unsupported `FIELD`:<br>
  `Invalid sort field. Valid fields are name, phone, email, address, and tag.`
* Unsupported `ORDER`:<br>
  `Invalid sort order. ORDER must be asc or desc.`

For example, an unsupported order leaves the displayed contact list unchanged:

![Result of entering an invalid sort order](images/sortInvalidOrderResult.png)

Valid examples:

* `sort name` sorts names in ascending order.
* `sort phone asc` sorts phone numbers numerically in ascending order.
* `sort email desc` sorts complete email addresses in descending order.
* `sort tag` sorts by primary tag in ascending order and keeps untagged contacts last.
* `sort NAME DESC` sorts names in descending order because both parameters are case-insensitive.

Invalid examples:

* `sort` is missing the mandatory field.
* `sort date` uses an unsupported field.
* `sort name upward` uses an unsupported order.
* `sort name email` treats `email` as an invalid order.
* `sort name asc extra` contains an extra argument.

### Deleting a person: `delete`

Deletes the specified person from the address book.

Format: `delete ID`

* Deletes the contact with the given `ID`, shown as `ID: 3` on the contact's card. See [Contact IDs](#contact-ids).
* The contact is deleted even if it is not in the list currently shown.
* If no contact has that ID, ByLine shows `No contact has the ID 3.` and nothing is deleted.
* The deleted contact's ID is never given to another contact.

Examples:
* `delete 2` deletes the contact with ID 2.
* `find Betsy` shows the matching contacts with their IDs. If Betsy's card shows `ID: 5`, `delete 5` deletes that contact.

### Archiving a contact: `archive`

Archives a contact you are no longer working with, without deleting it.

Format: `archive ID`

* Archives the contact with the given `ID`. See [Contact IDs](#contact-ids).
* An archived contact keeps all its details and its ID. Its card shows `[Archived]` after the name.
* Archived contacts are still shown by `list` and `find`. Use `list active` to hide them, or `list archived` to see only them.
* An archived contact can still be edited, tagged or deleted.
* If the contact is already archived, ByLine shows `This person is already archived.`

Examples:
* `archive 4` archives the contact with ID 4.

### Restoring an archived contact: `restore`

Restores an archived contact, so that it is no longer archived.

Format: `restore ID`

* Restores the contact with the given `ID`. See [Contact IDs](#contact-ids).
* If the contact is not archived, ByLine shows `This person is not archived.`

Examples:
* `list archived` shows the archived contacts. If one of them shows `ID: 4`, `restore 4` restores it.

### Clearing all entries: `clear`

Clears all entries from the address book.

Format: `clear`

IDs are not reset: the next contact you add still gets a new ID.

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

ByLine automatically saves data after every command. You do not need to save manually.

### Editing the data file

ByLine data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

Each contact in the file has an `id`, and the file has a `nextPersonId`, which is the ID the next new contact will get. Do not change these. Changing them can make ByLine reuse old IDs, or make the data file invalid, for example if two contacts have the same ID or an ID is above 999999.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make the data file invalid, ByLine starts with an empty address book at the next run. The invalid file remains on disk until you run a command (ByLine saves after every command). Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause the ByLine to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous ByLine home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add** | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 t/friend t/colleague`
**Archive** | `archive ID`<br> e.g., `archive 4`
**Clear** | `clear`
**Delete** | `delete ID`<br> e.g., `delete 3`
**Edit** | `edit ID [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`<br> e.g., `edit 2 n/James Lee e/jameslee@example.com`
**Find** | `find SUBSTRING` or `find [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]…`<br> e.g., `find oak` or `find n/Al t/fri`
**List** | `list`, `list active` or `list archived`
**Help** | `help`
**Restore** | `restore ID`<br> e.g., `restore 4`
**Sort** | `sort FIELD [ORDER]`<br> e.g., `sort name desc`
**Tag** | `tag ID t/TAG [t/TAG]…​`<br> e.g., `tag 3 t/expert t/housing policy`
**Untag** | `untag ID t/TAG [t/TAG]…​`<br> e.g., `untag 3 t/expert`
