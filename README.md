# HaikAI

An experiment with AI and Spring Boot AI.

## Requirements

* Java SDK 25
* Docker

## Article

Please, follow the link below to see the article on my blog:

https://javalotsofbeans.wordpress.com/2026/06/08/the-poet-and-the-ai/

## Intention

An introduction on how to work Spring Boot AI and using MongoDB as chat memory.

## How to run

You can run it directly, using maven command, or you can run it separately.

## Docker

In the `compose.yaml` file it uses Mongo and Mongo-Express. Mongo is the famous NOSQL MongoDB and Mongo-Express, a web UI for MongoDB.

## Ports

* Mongo will run on port: 27017
* Mongo-Express will run on port: 8081
* Project will run on port: 9010

Change the file `compose.yaml` to change Mongo and Mongo-Express ports.

```yaml
services:
  mongodb:
    # (...)
    ports:
      - '27017:27017' # <-- change the first port
    # (...)

  mongo-express:
    # (...)
    ports:
      - "8081:8081" # <-- change the first port 
    # (...)
```

Change the file `src/main/java/resources/application.yaml` to change the main project port:

```yaml
server:
  port: 9010 # <-- Change it here
```
