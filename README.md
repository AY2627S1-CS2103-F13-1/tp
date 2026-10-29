# ByLine

**ByLine** is a desktop contact manager for journalists. It helps journalists identify relevant contacts and keep track of interviewees for their reporting work.

[![CI Status](https://github.com/AY2627S1-CS2103-F13-1/tp/workflows/Java%20CI/badge.svg)](https://github.com/AY2627S1-CS2103-F13-1/tp/actions)

![Ui](docs/images/Ui.png)

## About ByLine

Journalists work with a large and constantly growing network of sources, experts and interviewees, often across several stories at once. These contacts usually end up scattered across notebooks, message threads and phone records, making it hard to find the right person when a story breaks.

ByLine brings all of these contacts into one place. Journalists can record each contact's details, categorise them with tags (for example by expertise), and quickly search through their contact list to find people they might need. ByLine is optimised for users who type fast and prefer typing over other means of input.

## Target Users

ByLine is designed for **journalists who manage numerous professional contacts** with varying relationship strengths and areas of expertise, and who:

* juggle multiple stories at the same time
* need to quickly find the right source or interviewee for a story
* prefer typing commands over clicking through menus

**Example user:** Kimberly is an experienced journalist working on several stories at once. Her contacts are spread across scattered notes, messages and phone records, and she needs a single place to organise them and find the right person quickly.

## Main Features

* **Add a contact**: Record a contact's name, phone number, email and address, with optional tags.<br>
  e.g. `add n/Jane Tan p/91234567 e/jane@example.com a/12 Kent Ridge Road t/economist`
* **List contacts**: View all the contacts stored in ByLine.<br>
  e.g. `list`
* **Delete a contact**: Remove a contact using the ID shown on its card.<br>
  e.g. `delete 2`
* **Tag contacts**: Add or remove tags without retyping the others (`tag`, `untag`).<br>
  e.g. `tag 1 t/politics t/source`
* **Search contacts**: Find contacts by name, phone, email, address or tag. Name, phone, email, address, and tags can match on part of the text. When you give several conditions, only contacts that match all of them are shown.<br>
  e.g. `find n/Tan t/econ` shows person with fields `n/Tan Meng Cheng` and `t/economics`
* **Filter contacts by exact tags**: Show contacts that have every specified complete tag, ignoring letter case.<br>
  e.g. `filter t/housing policy t/expert` shows only contacts tagged with both `housing policy` and `expert`
* **Sort contacts**: Arrange the displayed contacts by a supported field in ascending or descending order.<br>
  e.g. `sort name desc`
* **Remove tags**: Remove only the tags you name and keep the rest.<br>
  e.g. `untag 1 t/source`
* **Archive and restore contacts**: Hide contacts you are no longer working with, without deleting them, and bring them back later.<br>
  e.g. `archive 4`, then `restore 4`
* **Switch list views**: Show your active contacts, only archived contacts, or every contact.<br>
  e.g. `list archive`
* **Undo and redo**: Reverse your last change, or redo a change you undid.<br>
  e.g. `undo`, `redo`
* **Back up and load contacts**: Save all your contacts to a file and load them back later.<br>
  e.g. `export C:/backups/october`, then `load C:/backups/october`

See the [User Guide](https://ay2627s1-cs2103-f13-1.github.io/tp/UserGuide.html) for every command, and the [Developer Guide](https://ay2627s1-cs2103-f13-1.github.io/tp/DeveloperGuide.html) for how ByLine is built.

## Acknowledgement of AB3

This project is based on the AddressBook-Level3 project created by the [SE-EDU initiative](https://se-education.org).
