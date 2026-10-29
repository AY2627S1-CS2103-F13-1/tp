---
layout: page
title: User Guide
---

ByLine is a **desktop contact manager for journalists**. It helps you keep track of sources, experts and interviewees, tag them by topic, and find the right person quickly when a story breaks. You use ByLine by typing commands (a Command Line Interface, or CLI), and the window shows your contacts and the results. If you type quickly, this is faster than clicking through menus.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

2. Download the latest `.jar` file from [here](https://github.com/AY2627S1-CS2103-F13-1/tp/releases).

3. Copy the file to the folder you want to use as the _home folder_ for your ByLine.

4. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar ByLine.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

5. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to show the command guide.<br>
   Some example commands you can try:

   * `list` : Lists all active (not archived) contacts.

   * `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01` : Adds a contact named `John Doe` to ByLine.

   * `delete 3` : Deletes the contact with ID 3. Each contact's ID is shown as `ID: 3` on its card.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

6. Refer to the [Features](#features) section below for details of each command.

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
  For example, `help 123` is interpreted as `help`. The exceptions are `undo` and `redo`, which show an error instead, so that `undo 3` is not mistaken for undoing three changes.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Contact IDs

Every contact has an ID, a whole number shown as `ID: 3` on the contact's card. Commands that act on one contact, such as `edit` and `delete`, use this ID to pick the contact.

* A new contact gets the next unused ID. The first contact is `1`, the next is `2`, and so on.
* A contact keeps its ID for good. The ID does not change when you use `find`, `filter`, `sort` or `list`, archive the contact, or restart ByLine.
* Commands find the contact by its ID even if it is not in the list currently shown, e.g. after a `find`.
* IDs are never reused. After you delete a contact or run `clear`, the next new contact still gets a new ID. The one exception is [undoing](#undoing-a-change-undo) an `add`, which frees that contact's ID for the next contact you add.
* Type the ID as a plain number, e.g. `delete 3`.
* IDs go up to 999999. Because IDs are never reused, no more contacts can be added once ID 999999 has been given out.

### Contact details

Field | Rules
------|------
Name `n/` | English letters (A–Z), numbers and spaces. Must not start with a space.
Phone `p/` | Digits only, at least 3 digits.
Email `e/` | In the form `local-part@domain`, e.g. `jane@example.com`.
Address `a/` | Any text, but not blank.
Tag `t/` | Letters in any language, numbers and spaces, e.g. `housing policy`. Saved in lowercase.

Two contacts can't have the same name **and** phone number. ByLine ignores upper/lower case and extra spaces when comparing names, so `John Doe` and `john doe` with the same phone number count as the same contact. Contacts with the same name but different phone numbers are allowed. Names are saved exactly as you typed them.

### Viewing a contact's details

Click a contact in the list to see all of its details, including whether it is archived, in the panel on the right. Until you select a contact, the panel shows "Select a contact to view details here."

### Viewing help: `help`

Shows the command guide in the panel on the right.

![help message](images/helpMessage.png)

Format: `help`

* You can also press `F1`, or choose **Help** from the menu.
* Click any contact to show its details in the panel again.

### Adding a contact: `add`

Adds a contact to ByLine.

Format: `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​`

* The new contact gets the next unused ID, shown in the result message and on its card. See [Contact IDs](#contact-ids).
* ByLine rejects a contact with the same name and phone number as an existing contact. See [Contact details](#contact-details).
* If every ID up to 999999 has been used, ByLine shows `Cannot add more contacts: every contact ID up to 999999 has already been used.`
* After the contact is added, ByLine shows your active contacts, as `list` does, so the new contact is in view.

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
A contact can have any number of tags, including zero.
</div>

Examples:
* `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01`
* `add n/Betsy Crowe t/source e/betsycrowe@example.com a/12 Kent Ridge Road p/91234567 t/housing policy`

### Editing a contact: `edit`

Edits an existing contact in ByLine.

Format: `edit ID [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`

* Edits the contact with the given `ID`, shown as `ID: 3` on the contact's card. See [Contact IDs](#contact-ids).
* If no contact has that ID, ByLine shows `No contact has the ID 3.` and nothing is changed.
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* If the new name and phone number match another contact's, ByLine rejects the edit. See [Contact details](#contact-details).
* When editing tags, all of the contact's existing tags are removed; adding tags is not cumulative.
* To remove all of a contact's tags, enter `t/` without a tag after it.
* To add or remove only some tags, use [`tag`](#adding-tags-to-a-contact-tag) or [`untag`](#removing-tags-from-a-contact-untag) instead.
* After the edit, ByLine shows the group the contact is in: your active contacts, or your archived contacts if the edited contact is archived. The edited contact stays in view.

Examples:
*  `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the contact with ID 1 to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the contact with ID 2 to be `Betsy Crower` and clears all existing tags.

### Deleting a contact: `delete`

Deletes the specified contact from ByLine.

Format: `delete ID`

* Deletes the contact with the given `ID`, shown as `ID: 3` on the contact's card. See [Contact IDs](#contact-ids).
* The contact is deleted even if it is not in the list currently shown.
* If no contact has that ID, ByLine shows `No contact has the ID 3.` and nothing is deleted.
* The deleted contact's ID is never given to another contact.

Examples:
* `delete 2` deletes the contact with ID 2.
* `find Betsy` shows the matching contacts with their IDs. If Betsy's card shows `ID: 5`, `delete 5` deletes that contact.

### Adding tags to a contact: `tag`

Adds one or more tags to a contact, keeping the tags it already has.

Format: `tag ID t/TAG [t/TAG]…​`

* Adds the given tags to the contact with the given `ID`. See [Contact IDs](#contact-ids).
* At least one tag must be given.
* See [Contact details](#contact-details) for what a tag can contain.
* Tags are saved in lowercase, so `t/Expert` is saved as `expert`. Tags that differ only in upper/lower case count as the same tag.
* If the contact already has every given tag, ByLine shows `No tags were added because the contact already has the specified tags.`

Examples:
* `tag 3 t/expert` adds the tag `expert` to the contact with ID 3.
* `tag 3 t/housing policy t/interviewee` adds the tags `housing policy` and `interviewee` to the contact with ID 3.

### Removing tags from a contact: `untag`

Removes one or more tags from a contact, keeping its other tags.

Format: `untag ID t/TAG [t/TAG]…​`

* Removes the given tags from the contact with the given `ID`. See [Contact IDs](#contact-ids).
* At least one tag must be given.
* Tags are matched ignoring upper/lower case, so `t/Expert` removes the tag `expert`.
* If the contact has none of the given tags, ByLine shows `No tags were removed because the contact does not have the specified tags.`

Examples:
* `untag 3 t/expert` removes the tag `expert` from the contact with ID 3.
* `untag 3 t/housing policy t/interviewee` removes both tags from the contact with ID 3.

### Listing contacts: `list`

Shows contacts in ByLine.

Formats:

* `list` shows only active contacts, i.e. contacts that are not archived. ByLine also shows these when it starts.
* `list all` shows all contacts, including archived ones.
* `list archive` shows only archived contacts.
* `all` and `archive` can be typed in upper or lower case, e.g. `list ARCHIVE`.
* Any other input, such as `list archived`, shows an error listing the accepted formats, and the displayed list stays the same.

Examples:
* `list` shows your active contacts.
* `list archive` shows only archived contacts.
* `list all` shows every contact.

### Finding contacts: `find`

Finds contacts using part of their details. Combine one general substring with any selection of field-specific substrings, or use either kind of search on its own.

Format: `find [SUBSTRING] [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]…`

* Searches are case-insensitive: `al` matches both `Alex` and `Balakrishnan`.
* The general substring searches the name, phone number, email, address and tags. It must appear within at least one of these fields or one tag.
* Each prefixed substring searches only its corresponding field. A contact must match the general substring, if supplied, and every field and tag condition.
* Search text is matched as one consecutive substring, including internal spaces. For example, `Alex Ye` matches `Alex Yeoh`, but `Ye Alex` does not.
* Small typing mistakes are allowed: about one wrong, missing or extra character for every three characters you type. For example, `find Alx` still finds `Alex`. Searches of one or two characters must match exactly.
* Put the general substring before the first prefix. Text after a prefix belongs to that prefix until the next recognised prefix or the end of the command. For example, `find example n/Alex` combines a general search for `example` with a name search for `Alex`; `find n/Alex example` searches the name for the single substring `Alex example`.
* Prefixes may appear in any order. If `n/`, `p/`, `e/` or `a/` is repeated, only its last value is used.
* For multiple `t/` prefixes, every tag substring must match at least one of the contact's tags.
* `find` without search text or prefixes shows no contacts.
* An empty value after `n/`, `p/`, `e/` or `a/` imposes no restriction on that field. An empty `t/` value matches any contact with at least one tag.
* Archived contacts are included in the results.

Examples:

* `find oak` finds contacts with `oak` anywhere in their details, such as the name `Prof Oak` or the address `Oak Street`.
* `find n/Al` finds contacts whose names contain `Al`.
  ![Results for 'find n/Al'](images/findNameAl.png)
* `find p/2345` finds contacts whose phone numbers contain `2345`.
* `find n/Al e/example.com` finds contacts whose names contain `Al` and whose email addresses contain `example.com`.
* `find example n/Al t/fri` finds contacts with `example` in any searchable field, `Al` in their name and `fri` in at least one tag.
* `find t/fri t/col` finds contacts whose tags match **both** substrings, such as someone tagged with both `friends` and `colleagues`. Contacts tagged only with `friends` or only with `colleagues` are excluded. Adding field or tag conditions narrows the search instead of broadening it.
  ![Results for 'find t/fri t/col', requiring both tag conditions to match](images/findTagsFriCol.png)

### Filtering contacts by tag: `filter`

Shows contacts that have all the given tags. Unlike `find`, which matches part of a tag, `filter` matches only complete tags.

Format: `filter t/TAG [t/TAG]…​`

* A contact must have **every** given tag. It may also have other tags.
* Tags must match completely: `filter t/policy` does not match `housing policy`. Use `find t/policy` to match part of a tag.
* Upper/lower case does not matter, so `t/EXPERT` matches `expert`.
* Nothing may come before the first `t/`, and other prefixes such as `n/` are not allowed.
* If any tag is invalid (see [Contact details](#contact-details)), the whole command fails and the list does not change.
* `filter` searches all your contacts, including archived ones, not just the ones shown. It replaces the result of any earlier `find` or `filter`.
* Filtering only changes which contacts are shown. Use `list` to go back to your active contacts.

If no contact matches, ByLine lists every tag in use, so you can pick the exact one:

```text
0 person(s) listed!
Available tags: economics, expert, housing policy
```

Examples:
* `filter t/housing policy` shows contacts tagged `housing policy`.
* `filter t/housing policy t/expert` shows only contacts with both tags.
* `filter t/housing` does not show contacts tagged only `housing policy`, because the tag must match completely.

### Sorting displayed contacts: `sort`

Sorts the contacts currently shown by one of their details. Only the order on screen changes; your contacts are not changed.

Format: `sort FIELD [ORDER]`

* `FIELD` is `name`, `phone`, `email`, `address` or `tag`.
* `ORDER` is `asc` (A to Z, smallest first) or `desc`. If you leave it out, `asc` is used.
* Upper/lower case does not matter, so `sort NAME DESC` works.
* Names, emails and addresses are sorted alphabetically, ignoring upper/lower case. Phone numbers are sorted as numbers, so `000123` and `123` count as equal.
* `sort tag` uses each contact's alphabetically first tag. Contacts without tags always come last.
* Contacts that are equal on the chosen field are sorted by name, A to Z.
* Only the contacts currently shown are sorted, e.g. the results of a `find`. Running `list`, `find`, `filter`, `add` or `edit` afterwards shows the list unsorted again.

On success, ByLine shows, for example, `Sorted contacts by name in descending order.`

![Result of sorting contacts by name in descending order](images/sortNameDescResult.png)

If the field or order is not one of the above, or there are extra words, ByLine shows an error and the order does not change:

![Result of entering an invalid sort order](images/sortInvalidOrderResult.png)

Examples:
* `sort name` sorts by name, A to Z.
* `sort phone desc` sorts by phone number, largest first.
* `sort tag` sorts by first tag, with untagged contacts last.

### Archiving a contact: `archive`

Archives a contact you are no longer working with, without deleting it.

Format: `archive ID`

* Archives the contact with the given `ID`. See [Contact IDs](#contact-ids).
* An archived contact keeps all its details and its ID. Its card shows `[Archived]` after the name.
* Archived contacts are hidden by `list` and when ByLine starts. Use `list archive` to see only archived contacts, or `list all` to see every contact. `find` still includes archived contacts in its results.
* If archived contacts are not being shown, e.g. after `list`, the contact disappears from the list once it is archived.
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
* `list archive` shows the archived contacts. If one of them shows `ID: 4`, `restore 4` restores it and it disappears from the archived list. Run `list` to see it among your active contacts.

### Clearing all contacts: `clear`

Clears all contacts from ByLine.

Format: `clear`

* Archived contacts are deleted too.
* You can reverse `clear` with `undo`.
* IDs are not reset: the next contact you add still gets a new ID.

Examples:
* `clear` deletes all contacts. Running `undo` straight after brings them all back.

### Undoing a change: `undo`

Undoes the last change to your contacts.

Format: `undo`

* These commands can be undone: `add`, `edit`, `delete`, `clear`, `archive`, `restore`, `tag`, `untag` and `load`.
* Commands that only change what is shown, such as `list`, `find` and `sort`, are not undone. `undo` skips over them to the last change.
* You can undo up to 20 changes in a row. Older changes can no longer be undone.
* `undo` keeps the current view. For example, after a `find`, ByLine still shows only the contacts that match the search.
* The history of changes is cleared when you close ByLine, so changes made before a restart cannot be undone.
* If there is nothing to undo, ByLine shows `There is nothing to undo.`
* `undo` takes no parameters. Extra words, such as `undo 3`, show an error. Run `undo` again to undo more than one change.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
Undoing an `add` also takes back the contact's ID, so the next contact you add gets that same ID. This is the only case in which an ID is used again.
</div>

Examples:
* `delete 3` followed by `undo` brings back the contact with ID 3.
* `tag 2 t/expert`, then `tag 2 t/source`, then `undo` twice removes both tags.

### Redoing an undone change: `redo`

Redoes the last change you undid.

Format: `redo`

* You can redo each change you undid, in order, until you make a new change.
* After an `undo`, making a new change, e.g. with `add` or `edit`, means the undone changes can no longer be redone. Commands that only change what is shown, such as `list` or `find`, do not affect this.
* Redoing an undone `add` brings the contact back with the same ID.
* Like `undo`, `redo` keeps the current view, and its history is cleared when you close ByLine.
* If there is nothing to redo, ByLine shows `There is nothing to redo.`
* `redo` takes no parameters. Extra words, such as `redo 2`, show an error.

Examples:
* `delete 3`, then `undo`, then `redo` deletes the contact with ID 3 again.

### Exporting a backup: `export`

Saves all your contacts, including archived contacts, to a backup file.

Format: `export PATH`

* The backup keeps every contact's details, tags and ID, so `load` can bring them back exactly.
* `.json` is added to the file name if you leave it out, so `C:/My Contacts/October backup` saves to `C:/My Contacts/October backup.json`.
* Paths may contain spaces, with or without double quotes around the whole path.
* The folder must already exist. ByLine never overwrites an existing file, so use a new file name for each backup.
* A path that does not start with a drive or `/`, such as `backups/october`, is saved inside the folder you started ByLine from.
* Every contact is saved, whatever the list currently shows. Settings such as the window size are not saved.

Examples:
* `export C:/My Contacts/October backup` saves all contacts to `C:/My Contacts/October backup.json`.
* `export backups/october` saves to `october.json` in the `backups` folder inside the folder you started ByLine from.

### Loading a backup: `load`

Replaces all your contacts with the contacts in a backup file made by `export`.

Format: `load PATH`

* As with `export`, `.json` is optional and paths may contain spaces.
* All current contacts, including archived ones, are replaced. Contacts keep the IDs they have in the backup.
* Before replacing anything, ByLine saves a copy of your current contacts in the same folder as the backup, in a file whose name starts with `before-load-`. The result message shows where it is, so you can `load` that file to go back.
* No copy is made if you have no contacts, or only the sample contacts ByLine starts with, unchanged.
* ByLine does not ask you to confirm, so check the path first. If the backup can't be read, or the copy can't be saved, nothing is replaced.
* The backup file itself is never changed.
* You can reverse a `load` with `undo`.

Examples:
* `load C:/My Contacts/October backup` replaces your contacts with those in `C:/My Contacts/October backup.json`.

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

ByLine automatically saves data after every command. You do not need to save manually.

### Editing the data file

ByLine data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

Each contact in the file has an `id`, and the file has a `nextPersonId`, which is the ID the next new contact will get. Do not change these. Changing them can make ByLine reuse old IDs, or make the data file invalid, for example if two contacts have the same ID or an ID is above 999999.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make the data file invalid, ByLine starts with no contacts at the next run. The invalid file remains on disk until you run a command (ByLine saves after every command). Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause ByLine to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: On the old computer, run `export` and copy the backup file to the new computer. Then run `load` with that file there. You can also copy `data/addressbook.json` from your old ByLine folder into the new one.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add** | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 t/expert t/economics`
**Archive** | `archive ID`<br> e.g., `archive 4`
**Clear** | `clear`
**Delete** | `delete ID`<br> e.g., `delete 3`
**Edit** | `edit ID [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`<br> e.g., `edit 2 n/James Lee e/jameslee@example.com`
**Exit** | `exit`
**Export** | `export PATH`<br> e.g., `export C:/My Contacts/October backup`
**Filter** | `filter t/TAG [t/TAG]…​`<br> e.g., `filter t/housing policy t/expert`
**Find** | `find [SUBSTRING] [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]…`<br> e.g., `find oak`, `find n/Al t/fri` or `find example n/Al t/fri`
**Help** | `help`
**List** | `list`, `list all` or `list archive`
**Load** | `load PATH`<br> e.g., `load C:/My Contacts/October backup`
**Redo** | `redo`
**Restore** | `restore ID`<br> e.g., `restore 4`
**Sort** | `sort FIELD [ORDER]`<br> e.g., `sort name desc`
**Tag** | `tag ID t/TAG [t/TAG]…​`<br> e.g., `tag 3 t/expert t/housing policy`
**Undo** | `undo`
**Untag** | `untag ID t/TAG [t/TAG]…​`<br> e.g., `untag 3 t/expert`
