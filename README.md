# JoinLeaveMessage

A Paper/Spigot plugin that lets you customize join, first-join, leave, and private messages from a YAML file.

## Features
- configurable join message
- configurable first-join message
- configurable leave message
- configurable private message sent to the player
- multiline messages in YAML lists
- supports LuckPerms placeholders: `%luckperms_prefix%`, `%luckperms_suffix%`, `%luckperms_group_name%`
- reload command: /joinleavemessage reload

## Configuration

Example in `config.yml`:

```yml
joinMessage:
  - "&8[%luckperms_prefix%&8] &e%player% &aest arrivé sur le serveur !"
  - "&7Bonne aventure sur le serveur."

firstJoinMessage:
  - "&6Bienvenue pour la première fois, %luckperms_prefix% &e%player%&6 !"
  - "&aNous sommes ravis de te voir."

privateMessage:
  - "&bBonjour %luckperms_prefix% &e%player%&b !"
  - "&7Tu as rejoint le serveur."

leaveMessage:
  - "&c%luckperms_prefix% %player% a quitté le serveur."
```

The `%player%` placeholder is replaced by the player's name automatically.
When LuckPerms is installed, `%luckperms_prefix%`, `%luckperms_suffix%`, and `%luckperms_group_name%` are also resolved automatically.

## Build

```bash
mvn clean package
```

The JAR is generated in the `target/` folder.

## Install

1. Build the plugin.
2. Copy the generated JAR to your server's `plugins/` folder.
3. Ensure LuckPerms is installed if you want rank prefixes in the messages.
4. Start the server or reload the plugin with `/joinleavemessage reload`.
