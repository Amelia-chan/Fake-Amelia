> **WARNING**
> This repository contains the failed source code of a formerly-possible release candidate of Amelia. A newer Amelia will be created that will support a much better codebase will be created soon with a new dashboard and everything, please wait in the meantime!

# Amelia
An open-source Discord bot that brings story updates right to your server's doorsteps, and also brings trending notifications 
into your DMs to allow you to have a wake of surprise.

## 📦 Modules
Amelia has three child-modules under the "-chan" family prefix that all handle a specific area of tasks, 
all of these modules are dependent to each other with the exception of Alisa-chan which is what all the other modules 
depends on.

### Ame-chan
Ame-chan is the front-facing module of Amelia, akin to her physical body, she handles events from Discord such as 
commands and message sending but she also manages a major part of the database: feeds and servers. She is powered by 
Javacord with the Nexus framework.

### Akari-chan
Akari-chan is the back-facing module of Amelia, her brain and senses, she handles fetching of RSS feeds and trending 
notifications that are sent towards Ame-chan to be routed to the proper server, channels and users. She is powered by 
Javalin and many more.

### Alisa-chan
Alisa-chan is the main, shared, global module of Amelia which is among the most critical of all modules. She is responsible 
for fetching, parsing and mapping the RSS feeds into an order that Akari-chan can understand and she also holds all the 
knowledge for all the model types, database models, database methods and all other of those fancy stuff.

## 🏟 Installation
This section will be written once all the methods are ready to go.

## 🗡 Unit Testing
Alisa and Akari has major unit testing that ensures that an RSS feed doesn't produce looping issues that can be bothersome 
to fix and also nuking to many servers. You are required to run them after development by using the `mvn test` command.

## 🥞 Requirements
Amelia requires the following:
- [x] JDK 17 or above
- [x] MongoDB
- [x] 1.5G to 2.5G memory
- [x] 2 vCPUs or 1 dedicated EPYC or Ryzen core
