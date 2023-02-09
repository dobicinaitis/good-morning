# [Good morning](https://www.monkeyuser.com) 👋

Spice up your daily "Good morning" greeting with a link to a random <a href="https://www.workchronicles.com">WorkChronicles.com</a> comic. \
This script will fetch a random comic URL, add it as a link to your greeting text and paste it.

## Requirements
* Java 11 or later

## Usage
Define your [Markdown](https://www.markdownguide.org/basic-syntax/#links) greeting message in [variable](GoodMorning.java):
```java
GREETING_TEMPLATE = "[Good morning]({0}) {1}"; // link, emoji
```
Configure a keyboard shortcut to execute:
```shell
java PATH_TO_SCRIPT_DIR/GoodMorning.java
```
Enter your chat app, hit the keyboard shortcut and get your template pasted with a link to a random comic image.

Example

**plain**
```text
[Good morning](https://workchronicles.com/wp-content/uploads/2023/02/nohello-01-2.png) 👋
```

**rendered**

[Good morning](https://workchronicles.com/wp-content/uploads/2023/02/nohello-01-2.png) 👋

### Configuring keyboard shortcuts
Ubuntu [guide](https://help.ubuntu.com/stable/ubuntu-help/keyboard-shortcuts-set.html.en), example: \
![Keyboard shortcut configuration](https://cdn.dobicinaitis.dev/git/good-morning-keyboard-shortcut.png)

## Past sources

| Comic source                                 |                                              Last commit                                              |
|----------------------------------------------|:-----------------------------------------------------------------------------------------------------:|
| [MonkeyUser.com](https://www.monkeyuser.com) | [91132cb](https://github.com/dobicinaitis/good-morning/tree/91132cb2971f1f5a80652440bec223b4ecb46419) | 