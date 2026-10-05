---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* _{List the sources of reused or adapted ideas, code, documentation, and third-party libraries here, with links to the originals.}_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams are in `docs/diagrams`. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</div>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Logic.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.

</div>

Similarly, how an undo operation goes through the `Model` component is shown below:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Model.png)

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

<img src="images/CommitActivityDiagram.png" width="250" />

#### Design considerations:

**Aspect: How undo & redo execute:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command is correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* has a need to manage a significant number of contacts
* prefers desktop apps over other types of applications
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: Manage contacts faster than with a typical mouse-driven GUI application.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​ | I want to …​ | So that I can…​ |
| -------- | -------- | ------------ | ---------------- |
| `* * *` | new user | see usage instructions | refer to instructions when I forget how to use ByLine |
| `* * *` | journalist | add a new contact | keep track of people who may be useful for my reporting |
| `* * *` | journalist | list my contacts | see the contacts I have saved |
| `* * *` | journalist | delete a contact | remove contacts that I no longer need |
| `* * *` | journalist | search for contacts by their details | quickly locate relevant contacts without going through the entire list |
| `* * *` | journalist | assign tags to contacts | organise contacts based on information relevant to my work |
| `* * *` | journalist | filter contacts by tag | quickly identify contacts relevant to a particular topic or need |
| `* *` | journalist | edit a contact's details | keep my contact information accurate and up to date |
| `* *` | journalist | assign multiple tags to a contact | categorise the same contact in multiple useful ways |

### Use cases

**System:** Byline<br>
**Use case:** UC01 - Delete a person<br>
**Actor:** Journalist

**MSS**

1.  Journalist requests to list persons
2.  Byline shows a list of persons
3.  Journalist requests to delete a specific person in the list
4.  Byline deletes the person

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

    * 3a1. Byline shows an error message.

      Use case resumes at step 2.

**System:** Byline<br>
**Use case:** UC02 - Add a contact<br>
**Actor:** Journalist

**MSS**

1.  Journalist requests to add a contact, providing the contact's name, phone number, email, and optionally an address and tags
2.  Byline adds the contact and shows the details of the added contact

    Use case ends.

**Extensions**

* 1a. A compulsory detail (name, phone number or email) is missing.

    * 1a1. Byline shows an error message stating the correct format.

      Use case resumes at step 1.

* 1b. One or more of the given details is invalid (e.g. the phone number contains non-digit characters).

    * 1b1. Byline shows an error message describing the valid format of that detail.

      Use case resumes at step 1.

* 1c. No address is given.

    * 1c1. Byline records the address as "NA" and shows a warning that no address was given.

      Use case resumes at step 2.

**Guarantees**

* The contact is added only if all given details are valid.

**System:** Byline<br>
**Use case:** UC03 - List all contacts<br>
**Actor:** Journalist

**MSS**

1.  Journalist requests to list all contacts
2.  Byline shows the list of all contacts

    Use case ends.

**Extensions**

* 2a. There are no contacts.

    * 2a1. Byline shows a message that there are no contacts.

      Use case ends.

**System:** Byline<br>
**Use case:** UC04 - Tag a contact<br>
**Actor:** Journalist

**MSS**

1.  Journalist requests to list contacts
2.  Byline shows a list of contacts
3.  Journalist requests to add one or more tags to a specific contact in the list
4.  Byline adds the tags to the contact and shows the updated contact

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

    * 3a1. Byline shows an error message.

      Use case resumes at step 2.

* 3b. A given tag contains non-alphanumeric characters.

    * 3b1. Byline shows an error message stating that tags must be alphanumeric.

      Use case resumes at step 2.

* 3c. The contact already has one of the given tags (ignoring upper/lower case).

    * 3c1. Byline does not add the duplicate tag and informs the journalist that the contact already has it.

      Use case resumes at step 4 for the remaining tags.

**Guarantees**

* A contact never has two tags that differ only in upper/lower case.

**System:** Byline<br>
**Use case:** UC05 - Search for contacts<br>
**Actor:** Journalist

**MSS**

1.  Journalist requests to search for contacts, giving one or more search criteria (name, phone number, email, address and/or tag)
2.  Byline shows the contacts that match all the given criteria

    Use case ends.

**Extensions**

* 1a. No search criteria are given.

    * 1a1. Byline shows an error message stating the correct format.

      Use case resumes at step 1.

* 2a. No contacts match all the given criteria.

    * 2a1. Byline shows an empty list and a message that no contacts were found.

      Use case ends.

*{More to be added}*

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `25` or above installed.
2.  Should be able to hold up to 1000 persons without noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.

*{More to be added}*

### Glossary

The terms below are used consistently throughout the requirements, design, implementation, and testing sections of this guide.

* **AB3**: AddressBook Level 3, the application on which ByLine is based. Some inherited class names and implementation details continue to use AB3 terminology.
* **Address**: The free-text field used to record a contact's address or general location.
* **Address book**: The internal collection that stores all contact records. This name is inherited from AB3 and is represented by the `AddressBook` class in the codebase.
* **AND search**: A search involving multiple criteria in which a contact must satisfy every specified criterion to be included in the results.
* **ByLine**: A desktop, CLI-based contact-management application that helps journalists organise and retrieve professional contacts relevant to their reporting. Users interact with ByLine primarily by typing commands, while a GUI displays their contacts and command results.
* **Case-insensitive matching**: Comparing text without treating uppercase and lowercase letters as different. For example, `Housing` and `housing` are considered equivalent.
* **CLI (Command Line Interface)**: A text-based interface through which users operate ByLine by typing commands.
* **Command**: A text instruction entered through the CLI to perform an operation, such as adding, listing, deleting, tagging, or searching for contacts.
* **Command prefix**: A marker that identifies the meaning of a command parameter, such as `n/` for a name or `t/` for a tag.
* **Complete contact list**: All contacts stored in ByLine, regardless of the contacts currently displayed in the UI.
* **Contact**: A person whose details a journalist stores in ByLine because the person may be relevant to current or future reporting.
* **Contact index**: The one-based number assigned to a contact in the currently displayed contact list. Commands that accept an index operate on the contact at that position.
* **Contact record**: The complete set of information stored about one contact, including their name, phone number, email address, address, and tags.
* **Displayed contact list**: The contacts currently visible in the UI. It may contain all stored contacts or only the contacts returned by a search.
* **Duplicate contact**: A contact whose normalised name is the same as that of an existing contact. Name comparison ignores letter case and leading, trailing, or repeated spaces.
* **Duplicate tag**: A tag that is already assigned to a contact or is repeated within the same command, ignoring letter case.
* **Exact tag match**: A match that occurs only when the complete search value is equal to a contact's complete tag, ignoring letter case.
* **Expert**: A contact with specialist knowledge relevant to a story or topic.
* **Filtered contact list**: The internal list of contacts that satisfy the current search criteria. It provides the data shown in the displayed contact list.
* **GUI (Graphical User Interface)**: The visual interface that displays the command box, contact list, command results, and other application information.
* **Interviewee**: A contact whom a journalist has interviewed or intends to interview.
* **Journalist**: The target user of ByLine, who manages professional contacts for reporting purposes and prefers a command-driven desktop application.
* **Mainstream OS**: Windows, Linux, Unix, or macOS.
* **MVP (Minimum Viable Product)**: The smallest set of features required for ByLine to provide its intended core value to journalists.
* **Normalised name**: A contact name converted to a standard form for comparison by ignoring letter case and leading, trailing, or repeated spaces.
* **Partial match**: A match that occurs when a search value appears within a stored field without being equal to the complete field value.
* **Person**: The internal model entity used to represent a contact. This term is inherited from AB3 and appears in class names such as `Person` and `UniquePersonList`; user-facing documentation should use **contact** instead.
* **Potential source**: A contact who may be able to provide information for a story but has not necessarily been approached or interviewed.
* **Private contact detail**: Contact information that is not intended to be shown or shared with others.
* **Search criterion**: A field and value used to restrict search results, such as a name, phone number, email address, address, or tag.
* **Source**: A contact who provides, or may provide, information, evidence, background, or commentary for a journalist's reporting.
* **Story**: A news article, investigation, or other reporting assignment on which a journalist is working.
* **Tag**: A journalist-defined label assigned to a contact to represent an area of expertise, topic, role, organisation, or another useful characteristic.
* **Topic**: A subject area used to describe the focus of a story or a contact's relevance, such as housing policy or public health.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy it into an empty folder.

   1. Double-click the JAR file.<br>
      Expected: The GUI opens with a set of sample contacts. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command, with multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: The first contact is deleted from the list. The status message shows the deleted contact's details.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases …​ }_
