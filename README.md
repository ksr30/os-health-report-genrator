Q2. Update the existing Akka program to do the following tasks:


Use different dispatchers (IO dispatchers with fixed thread pool) while reading from files. 

Limit the number of actors that should be configurable by using a router.

Use a mailbox having a capacity of 1000 files. More files should be discarded with an error log. 
