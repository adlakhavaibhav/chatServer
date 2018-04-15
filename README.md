##Introduction

This is simple chat server, that uses nio.It allows multiple clients to connect to it, join one or more rooms, and send and receive messages from those rooms.

##Start the server 

1) mvn clean install
2) cd target/classes/
3) java com.demo.SimpleChatServer


## Protocol

Clients should interact with the server by opening a TCP connection and using a very simple text based protocol

All messages are ASCII strings

\n is a new line character

<room_name> and <message> are ASCII strings that cannot be empty or contain the newline character

The protocol is as follows:

### JOIN

To join a room

JOIN <room_name>\n

### SEND

To send a value to a room

SEND <room_name> <message>\<nav></nav>
