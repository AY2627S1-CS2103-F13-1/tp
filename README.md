# Byline

**Byline** is a desktop contact manager for journalists. It helps journalists identify relevant contacts and keep track of interviewees for their reporting work.

[![CI Status](https://github.com/AY2627S1-CS2103-F13-1/tp/workflows/Java%20CI/badge.svg)](https://github.com/AY2627S1-CS2103-F13-1/tp/actions)

![Ui](docs/images/Ui.png)

## About Byline

Journalists work with a large and constantly growing network of sources, experts and interviewees, often across several stories at once. These contacts usually end up scattered across notebooks, message threads and phone records, making it hard to find the right person when a story breaks.

Byline brings all of these contacts into one place. Journalists can record each contact's details, categorise them with tags (for example by expertise), and quickly search through their contact list to find people they might need. Byline is optimised for users who type fast and prefer typing over other means of input.

## Target Users

Byline is designed for **journalists who manage numerous professional contacts** with varying relationship strengths and areas of expertise, and who:

* juggle multiple stories at the same time
* need to quickly find the right source or interviewee for a story
* prefer typing commands over clicking through menus

**Example user:** Kimberly is an experienced journalist working on several stories at once. Her contacts are spread across scattered notes, messages and phone records, and she needs a single place to organise them and find the right person quickly.

## Main Features

* **Add a contact**: Record a contact's name, phone number, email and address, with optional tags.<br>
  e.g. `add n/Jane Tan p/91234567 e/jane@example.com a/12 Kent Ridge Road t/economist`
* **List contacts**: View all the contacts stored in Byline.<br>
  e.g. `list`
* **Delete a contact**: Remove a contact using its index in the displayed list.<br>
  e.g. `delete 2`
* **Tag contacts**: Categorise contacts with tags such as their area of expertise, so related contacts are easy to group.<br>
  e.g. `edit 1 t/politics t/source`
* **Search contacts**: Find contacts by name, phone, email, address or tag. Name, phone, email and address can match on part of the text, while tags must match exactly. When you give several conditions, only contacts that match all of them are shown.<br>
  e.g. `find n/Tan t/economist`

## Acknowledgement of AB3

This project is based on the AddressBook-Level3 project created by the [SE-EDU initiative](https://se-education.org).
