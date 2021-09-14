Hello,
This is multithreaded client server application using socket programming.
Features:
There is a chat room in which multiple clients(at most 10) can have a chat.
Each client can add into chat room by registering his name(he/she will be asked to enter name)
Each client will be handles by a new thread.
A client can send public message to all the clients(pattern:message).
A client can send private message to a paticular client(pattern:@clientName message).
A client can end chat whenever he/she wants.
Whenever a client enters or leaves the chat room, all the other clients get notification of that.

How to run:
Make a java project in your IDE.
Create two java classes Server and Client.
Copy and paste the code in both files.
First you need to run the server.
Now run client class (Whenever you will run the client class, a new client will be added to chat room)
In order to add two clients: Run client class for the first time and enter the name of client in console
                             Then run client class again and enter the name of second client.
Switch console windows in order to send messages from each client side.
In order to leave chat room, client have to send 'bye' or 'Bye'


Thanks.