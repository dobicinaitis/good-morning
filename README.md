# [Good morning](https://www.monkeyuser.com) 👋

Spice up your daily "Good morning" greeting with a link to a random <a href="https://www.monkeyuser.com">MonkeyUser.com</a> comic. \
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
[Good morning](https://www.monkeyuser.com/assets/images/2019/120-pivoting.png) 👋
```

**rendered**

[Good morning](https://www.monkeyuser.com/assets/images/2019/120-pivoting.png) 👋

### Configuring keyboard shortcuts
Ubuntu [guide](https://help.ubuntu.com/stable/ubuntu-help/keyboard-shortcuts-set.html.en), example: \
![Keyboard shortcut configuration](https://cdn.dobicinaitis.dev/git/good-morning-keyboard-shortcut.png)
