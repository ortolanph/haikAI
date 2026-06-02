# The poet and the AI

A haikai is a traditional Japanese poem with a 5-7-5 syllables format. Commonly it contains themes like nature,
capturing a moment or a landscape. Its message is simple, and has to be expressed with few words. No title, no rhyme,
only feelings of the moment.

> Old mountain
> Goes high up in the sky
> Getting old

Today they are used in Pop Culture for anything and even AI has the knowledge to create ones. Of course they will not
observe the nature or capture the moment and the landscape, but they have knowledge on what themes are more commonly
used.

This article's example is based upon [Baeldung](https://www.baeldung.com/spring-ai) article (thanks).

This article will show the basic use of Spring Boot AI with Haikus, an actor filmography and MongoDB memory.

---

## The project

The project is located at [Github](https://github.com/ortolanph/haikAI) and it's free to use. It contains a docker
compose file on which will prepare a MongoDB for AI memory.

But wait! Some warmup checks:

1. Get an OPEN AI Key
2. Put some money on it (at least 20 US$, who will pay the AI calls for your professional projects?)
3. The more call you do, more it will spend (be aware)

This project does not spend a lot of money as it does not extensive computations. Expect to spend less than US$ 3.

If you don't wish to spend anything at all, jump to the Ollama section. There I tell how to add Ollama to docker compose
configuration and how to configure on POM and add a configuration on Spring Boot.

To configure with OpenAI integration:

```yaml
spring:
  # (...)
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-5.4-nano
          temperature: 1.0
        embedding:
          options:
            model: text-embeddding-3-small
```

On which:

* `api-key`: your OpenAI key. Do not share.
* `chat.options.mode`: the model of the AI. For this article, gpt-5.4-nano will fit
* `chat.options.temperature`: how random will be the answer.
* `chat.embedding.options.model`: model to measure the relatedness of text string

On the Java side, I had to configure the ChatClient:

```java
private final ChatClient.Builder chatClientBuilder;

@Bean
public ChatClient chatClient() {
    return chatClientBuilder
            .defaultAdvisors(
                    new SimpleLoggerAdvisor(),
                    MessageChatMemoryAdvisor.builder(mongoChatMemory()).build()
            )
            .build();
}
```

Advisors are special classes that can enhance the chat. For now, I'm using two advisors:

- The `SimpleLoggerAdvisor` that will log every action from AI
- `MessageChatMemoryAdvisor` that will save the conversation between the user and the OpenAI, in this case, using Mongo
  DB. I will explain it later

Refer to [Advisors API](https://docs.spring.io/spring-ai/reference/api/advisors.html) for more information.

## Simple Haikai Service

You can go to the browser, access your favorite AI and time this:

> Write a playful haiku about mountains and the joy of programming with AI following the traditional 5-7-5 syllable
> structure.

But you are a programmer. You know how to develop software (I hope) and you don't bend to AI tools, you delegate to
them, you integrate them into your programs. You know Spring Boot, and them you browse the initilizr front and find
Spring Boot Open AI, which is the dependency to use AI in your project.

Knowing that let's move. Let's examine the diagram below:

```mermaid
sequenceDiagram
    External ->>+ HaikaiController: GET haikais/simple
    HaikaiController ->>+ HaikaiService: generateSimpleHaikai()
    HaikaiService ->>+ OpenAIImageModel: call()
    OpenAIImageModel ->>+ OpenAIIntegration: Prompt
    OpenAIIntegration ->>- OpenAIImageModel: result
    OpenAIImageModel ->>- HaikaiService: haiku generated
    HaikaiService ->>- HaikaiController: message
    HaikaiController ->>- External: image/png
```

This is a pretty straightforward example, nothing uncommon. The code of the service is:

```java
private static final PromptTemplate SIMPLE_TEMPLATE = new PromptTemplate("Write a playful haiku about mountains and the joy of programming with AI following the traditional 5-7-5 syllable structure.");

private final ChatClient chatClient;
private final UserService userService;

public String generateSimpleHaikai() {
    log.info("HaikaiService:generateSimpleHaikai())");

    return chatClient
            .prompt(SIMPLE_TEMPLATE.create())
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userService.getCustomerId()))
            .call()
            .content();
}

```

I got the response and created an image on ChatGPT. Later on this article, I explain how to generate images with Spring
Boot AI. Here it is the picture:

![Simple Hakai Picture](simple_kaikai_picture.png)

## Complex

But I wanted more! In the original article I saw that AI can respond within a determined object, so I wrote another
service that writes a parameterized haiku, and returns not only with the content, but withe the parameters and a title.

The prompt used was:

```
Write a {genre} haiku about {theme} following the traditional 5-7-5 syllable structure written {language}.
```

Let's stop for a moment and think: what is `{genre}`, `{theme}` or even `{language}`? They are parameters. The
difference now is that I call a `POST` method that will send me a payload with these three parameters and returns me a
Haikai object.

```mermaid
sequenceDiagram
    External ->>+ HaikaiController: POST haikais/complex
    HaikaiController ->>+ HaikaiService: generatePowerfulHaikai()
    HaikaiService ->>+ ChatClient: call()
    ChatClient ->>+ OpenAIIntegration: Prompt
    OpenAIIntegration ->>- ChatClient: result
    ChatClient ->>- HaikaiService: haiku generated
    HaikaiService ->>- HaikaiController: message
    HaikaiController ->>- External: application/json
```

The method code:

```java
private static final PromptTemplate COMPLEX_TEMPLATE = new PromptTemplate("Write a {genre} haiku about {theme} following the traditional 5-7-5 syllable structure written {language}.");
private final UserService userService;

public Haikai generatePowerfulHaikai(HaikaiRequest request) {
    log.info("HaikaiService:generatePowerfulHaikai(request={})", request);

    Prompt prompt = COMPLEX_TEMPLATE
            .create(
                    Map.of(
                            "genre", request.genre(),
                            "theme", request.theme(),
                            "language", request.language()));

    return chatClient
            .prompt(prompt)
            .advisors(new SimpleLoggerAdvisor())
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userService.getCustomerId()))
            .call().entity(Haikai.class);
}
```

An example input:

```json
{
  "genre": "Sci-Fi",
  "theme": "Artificial Intelligence",
  "language": "en-GB"
}
```

And an example output:

```json
{
  "content": "AI learns in silence\nfrom drifting data constellations—\nholds our futures steady",
  "genre": "Sci-Fi",
  "language": "en-GB",
  "theme": "Artificial intelligence",
  "title": "Constellation Quiet"
}
```

Again, I pasted the resulting Haikai in a ChatGPT image session and this is the result:

![Complex Hakai Picture](complex_hakai_picture.png)

## Image generation

The third example with Haikais is the image generation. Again, this is not a complex thing, but instead using the
ChatClient it will use the `OpenAiImageModel` class instance. This is specific to OpenAI, but implements the ImageModel
interface from Spring Boot AI, where there are implementations. For what I checked, Ollama Spring Boot AI API
does not support image generation.

While testing, a problem occurred and I have to run the application with the JVM Option below:

```
--enable-native-access=ALL-UNNAMED
```

The code for this service is:

```java
private static final String IMAGE_TEMPLATE = """
            Create an image with the following Haikai:
        
                {haikai_line1}
                {haikai_line2}
                {haikai_line3}
        
            Be creative and catch the essence of what has been asked.
        """;

private final ImageModel imageModel;

public byte[] generateImageHaikai(ImageHaikaiRequest imageHaikaiRequest) {
    log.info("HaikaiService:generateImageHaikai(imageHaikaiRequest={})", imageHaikaiRequest);

    String renderedPrompt = IMAGE_TEMPLATE
            .replace("{haikai_line1}", imageHaikaiRequest.line1())
            .replace("{haikai_line2}", imageHaikaiRequest.line2())
            .replace("{haikai_line3}", imageHaikaiRequest.line3());

    ImageResponse response = imageModel.call(
            new ImagePrompt(renderedPrompt,
                    OpenAiImageOptions.builder()
                            .model("gpt-image-1")
                            .build())
    );

    Image image = Objects.requireNonNull(response.getResult()).getOutput();

    return Base64.getDecoder().decode(image.getB64Json());
}
```

The process is almost the same, instead calling a chat client, I called the image model to make integration with Open
AI:

```mermaid
sequenceDiagram
    External ->>+ HaikaiController: POST haikais/image
    HaikaiController ->>+ HaikaiService: generateImageHaikai()
    HaikaiService ->>+ ImageModel: call()
    ImageModel ->>+ OpenAIIntegration: Prompt
    OpenAIIntegration ->>- ImageModel: result
    ImageModel ->>- HaikaiService: haiku generated
    HaikaiService ->>- HaikaiController: message
    HaikaiController ->>- External: image/png
```

From [Wikipedia Haiku article](https://en.wikipedia.org/wiki/Haiku), I got the haiku for this example, but I went
beyond,
not only to put the English version, but also the original in Japanese. The poem in English is:

> old pond
> frog leaps in
> water's sound

Then, the generated image is:

![Generated by IA - English](haikai.png)

The Japanese version is:

> 古池や蛙飛び込む水の音
> ふるいけやかわずとびこむみずのおと

As I am not familiar with Japanese, I just copy-pasted the original haiku. I assume that the AI will understand the
original and generate a similar image. The result is:

![Generated by IA - English](haikai0.png)

They are not the same image, but they have the same concept.

## Actors

Other example that was developed, getting a bit off the Haikai theme, was the filmography of an actor. I just want to
test the ability of the AI to return a list of movies with the role, director, year and TMDB Id.

The structure of the response is:

```mermaid
classDiagram
    ActorFilms "1" -- "many" Film

    class ActorFilms {
        -String name
        -List<Film> films
    }

    class Film {
        -String title
        -int year
        -String director
        -String role
        -int tmdbId
    }
```

And I thought about one actor on which is the most prolific in the world. Who is it? *Kevin Bacon*, of course. There is
even a game called "Six Degrees of Kevin Bacon" where the goal is to find the shortest path between an actor and Kevin
Bacon, based on the movies they have been in together.

The process is the same as the previous examples, but instead of returning a Haikai, it returns an `ActorFilms` object
with the name of the actor and a list of films with the title, year, director, role and TMDB Id.

```mermaid
sequenceDiagram
    External ->>+ ActorsController: GET actors/filmography/{actorName}
    ActorsController ->>+ ActorsService: getFilmography()
    ActorsService ->>+ ChatClient: call()
    ChatClient ->>+ OpenAIIntegration: Prompt
    OpenAIIntegration ->>- ChatClient: result
    ChatClient ->>- ActorsService: filmography generated
    ActorsService ->>- ActorsController: message
    ActorsController ->>- External: text/json
```

The code for this service is:

```java
    private static final PromptTemplate ACTOR_TEMPLATE = new PromptTemplate(
        """
                       Generate the filmography for a {name} with the name of played character.
                       I want an object with the top 20 movies.
                       I want a list with the movie title, the movie year, the movie director, the actor role, and the tmdbId.
                """);

private final ChatClient chatClient;

private final UserService userService;

public ActorFilms getFilmography(String name) {
    log.info("ActorsService:getFilmography(name={})", name);

    return chatClient
            .prompt(ACTOR_TEMPLATE.create(Map.of("name", name)))
            .advisors(new SimpleLoggerAdvisor())
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userService.getCustomerId()))
            .call()
            .entity(ActorFilms.class);
}
```

Note that the prompt is a bit more complex than the previous ones, as it has to specify the structure of the response
and the number of movies to return. The result is a JSON object with the name of the actor and a list of films with the
title, year, director, role and TMDB Id.

Example output:

```json
{
  "films": [
    {
      "director": "Nicole Kassell",
      "role": "Bob",
      "title": "The Woodsman",
      "tmdbId": 4175,
      "year": 2004
    },
    {
      "director": "Antonio Giménez Rico",
      "role": "Edward",
      "title": "The Disappearance of Garcia Lorca",
      "tmdbId": 5953,
      "year": 1997
    },
    {
      "director": "Alan Parker",
      "role": "Lt. R. Anderson",
      "title": "Mississippi Burning",
      "tmdbId": 12033,
      "year": 1988
    },
    {
      "director": "Rob Reiner",
      "role": "Lt. Daniel Kaffee",
      "title": "A Few Good Men",
      "tmdbId": 619,
      "year": 1992
    },
    {
      "director": "Kevin Smith",
      "role": "Steve Gold",
      "title": "Jersey Girl",
      "tmdbId": 12376,
      "year": 2004
    },
    {
      "director": "Cynthia Verges",
      "role": "Keith",
      "title": "Loverboy",
      "tmdbId": 10820,
      "year": 1989
    },
    {
      "director": "John Landis",
      "role": "Chip Diller",
      "title": "Animal House",
      "tmdbId": 9469,
      "year": 1978
    },
    {
      "director": "Lee Tamahori",
      "role": "Richard Attenborough",
      "title": "The Edge",
      "tmdbId": 4016,
      "year": 1997
    },
    {
      "director": "John McNaughton",
      "role": "Tommy",
      "title": "Wild Things",
      "tmdbId": 12109,
      "year": 1998
    },
    {
      "director": "Shane Black",
      "role": "Marv",
      "title": "Kiss Kiss Bang Bang",
      "tmdbId": 5081,
      "year": 2005
    },
    {
      "director": "Marc Sch?l?ndorff",
      "role": "John R. Scott",
      "title": "Murder in the First",
      "tmdbId": 5950,
      "year": 1995
    },
    {
      "director": "Paul Verhoeven",
      "role": "Sebastian Caine",
      "title": "Hollow Man",
      "tmdbId": 2910,
      "year": 2000
    },
    {
      "director": "Herbert Ross",
      "role": "William 'Bubba' Higgins",
      "title": "Footloose",
      "tmdbId": 1655,
      "year": 1984
    },
    {
      "director": "Justin Zackham",
      "role": "George",
      "title": "The Big Wedding",
      "tmdbId": 207643,
      "year": 2013
    },
    {
      "director": "Joel Schumacher",
      "role": "Jake Brigance",
      "title": "A Time to Kill",
      "tmdbId": 11268,
      "year": 1996
    },
    {
      "director": "Terry Green",
      "role": "Detective",
      "title": "Quicksand: No Escape",
      "tmdbId": 621362,
      "year": 2021
    },
    {
      "director": "Garry Marshall",
      "role": "Jack",
      "title": "The Letdown",
      "tmdbId": 206981,
      "year": 2013
    },
    {
      "director": "David Koepp",
      "role": "Himself",
      "title": "You Should Have Left",
      "tmdbId": 584074,
      "year": 2020
    },
    {
      "director": "Glenn Ficarra",
      "role": "Jacob",
      "title": "Crazy, Stupid, Love",
      "tmdbId": 594970,
      "year": 2011
    },
    {
      "director": "Kevin Williamson",
      "role": "Mark",
      "title": "The Following",
      "tmdbId": 254048,
      "year": 2013
    }
  ],
  "name": "Kevin Bacon"
}
```

Ok, but there's a catch? AI can commit mistakes, so I went through the list and checked if the information was correct.
I found mistakes. I created two tables, one with movies on which Kevin Bacon was, and another with movies on which he
was not. I also checked all the other data.

Checks:

| Check | Description             |
|:-----:|-------------------------|
|  `Y`  | The year is correct     |
|  `M`  | The movie is correct    |
|  `R`  | The role is correct     |
|  `D`  | The director is correct |
|  `T`  | The TMDB Id is correct  |

### Movies with Kevin Bacon

| Year | Title                | Role                    | Director          | TMDB ID | Checks |
|:----:|----------------------|-------------------------|-------------------|:-------:|:------:|
| 1978 | Animal House         | Chip Diller             | John Landis       |  9469   | `YMRD` |
| 1984 | Footloose            | William "Bubba" Higgins | Herbert Ross      |  1655   | `YMD`  |
| 1992 | A Few Good Men       | Lt. Daniel Kaffee       | Rob Reiner        |   619   | `YMD`  |
| 1995 | Murder in the First  | John R. Scott           | Marc Schölöndorff |  5950   |  `YM`  |
| 1998 | Wild Things          | Tommy                   | John McNaughton   |  12109  | `YMD`  |
| 2000 | Hollow Man           | Sebastian Caine         | Paul Verhoeven    |  2910   | `YMRD` |
| 2004 | The Woodsman         | Bob                     | Nicole Kassell    |  4175   | `YMD`  |
| 2011 | Crazy, Stupid, Love  | Jacob                   | Glenn Ficarra     | 594970  | `YMD`  |
| 2013 | The Following        | Mark                    | Kevin Williamson  | 254048  | `YMDT` |
| 2020 | You Should Have Left | Himself                 | David Koepp       | 584074  | `YMD`  |

Remarks:

* The Following is a TV Show

### Movies Without Kevin Bacon

| Year | Title                             | Role                 | Director             | TMDB ID | Checks |
|:----:|-----------------------------------|----------------------|----------------------|:-------:|:------:|
| 1988 | Mississippi Burning               | Lt. R. Anderson      | Alan Parker          |  12033  | `YMD`  |
| 1989 | Loverboy                          | Keith                | Cynthia Verges       |  10820  |  `YM`  |
| 1996 | A Time to Kill                    | Jake Brigance        | Joel Schumacher      |  11268  | `YMD`  |
| 1997 | The Disappearance of Garcia Lorca | Edward               | Antonio Giménez Rico |  5953   |        |
| 1997 | The Edge                          | Richard Attenborough | Lee Tamahori         |  4016   | `YMD`  |
| 2004 | Jersey Girl                       | Steve Gold           | Kevin Smith          |  12376  | `YMRD` |
| 2005 | Kiss Kiss Bang Bang               | Marv                 | Shane Black          |  5081   | `YMD`  |
| 2013 | The Big Wedding                   | George               | Justin Zackham       | 207643  | `YMD`  |
| 2013 | The Letdown                       | Jack                 | Garry Marshall       | 206981  |        |
| 2021 | Quicksand: No Escape              | Detective            | Terry Green          | 621362  |  `M`   |

Remarks:

* The Disappearance of Garcia Lorca possible match
  is [Death In Granada](https://www.themoviedb.org/movie/104106-death-in-granada)
* The Letdown appears to be a 2013 show
* Quicksand: No Escape is a 1992 movie

## Ollama

Instead of using OpenAI, you can use Ollama, which is a local LLM server. You can use this docker compose configuration
to run Ollama locally:

```yaml
services:
  ollama:
    image: ollama/ollama
    container_name: ollama
    ports:
      - "11434:11434"
    volumes:
      - ollama:/root/.ollama
    mem_limit: 6g
    restart: unless-stopped

volumes:
  ollama:
```

But, before doing anything, you have to install a model. For this article, I used the `gemma3:1b` model, which is a
small model that can run on a local machine. You can install it with the command:

```shell
docker exec -it ollama ollama pull gemma3:1b
```

Then you have to ignore the OpenAI configuration and add the Ollama dependency to your project:

```xml

<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-ollama</artifactId>
</dependency>
```

Finally, you have to configure the model in your application properties:

```yaml
ollama:
  chat:
    options:
      model: gemma3:1b
```

Note that you don't need to configure the API key neither the host and port, as the default configuration is to connect
to `http://localhost:11434`, which is the default configuration of the Ollama server.

For more information on what models are available and how to use them, refer to
the [Ollama documentation](https://ollama.com/docs/usage/models).

For more information on what AIs you can run locally, refer to the site Can I Run AI Locally? (https://cani.run/), which
is a great resource to check if a model can run on your machine and what are the requirements.

## Memory

MongoDB

The last, but not least, example is the memory. AI can remember the conversation with the user, and use it to improve
the answers. For this example, I used MongoDB as the memory repository, but there are other implementations available,
such as Redis or JDBC.

```yaml
ai:
  memory:
    max_messages: 100

spring:
  # (...)
  data:
    mongodb:
      host: localhost
      port: 27019
      database: ai_chat
      username: ai_user
      password: ai_pass
```

And the configuration of the memory repository:

```java
private final MongoChatMemoryRepository mongoChatMemoryRepository;

@Value("${ai.memory.max_messages}")
private int maxMessages;

public ChatMemory mongoChatMemory() {
    return MessageWindowChatMemory
            .builder()
            .chatMemoryRepository(mongoChatMemoryRepository)
            .maxMessages(maxMessages)
            .build();
}
```

At the configuration of the ChatClient, I added the `MessageChatMemoryAdvisor` that will save the conversation in the
memory repository:

```java

@Bean
public ChatClient chatClient() {
    return chatClientBuilder
            .defaultAdvisors(
                    new SimpleLoggerAdvisor(),
                    MessageChatMemoryAdvisor.builder(mongoChatMemory()).build()
            )
            .build();
}
```

This is an example of a conversation saved in MongoDB, where the AI has generated the filmography of Kevin Bacon:

```
{
    _id: ObjectId('6a183e208213c918f9860762'),
    conversationId: '788946ac-3c3e-498c-8e9e-01575beb7586',
    message: {
        content: '{\n  "name": "Kevin Bacon",\n  "films": [\n    {\n      "title": "The Woodsman",\n      "year": 2004,\n      "director": "Nicole Kassell",\n      "role": "Bob",\n      "tmdbId": 4175\n    },\n    {\n      "title": "The Disappearance of Garcia Lorca",\n      "year": 1997,\n      "director": "Antonio Giménez Rico",\n      "role": "Edward",\n      "tmdbId": 5953\n    },\n    {\n      "title": "Mississippi Burning",\n      "year": 1988,\n      "director": "Alan Parker",\n      "role": "Lt. R. Anderson",\n      "tmdbId": 12033\n    },\n    {\n      "title": "A Few Good Men",\n      "year": 1992,\n      "director": "Rob Reiner",\n      "role": "Lt. Daniel Kaffee",\n      "tmdbId": 619\n    },\n    {\n      "title": "Jersey Girl",\n      "year": 2004,\n      "director": "Kevin Smith",\n      "role": "Steve Gold",\n      "tmdbId": 12376\n    },\n    {\n      "title": "Loverboy",\n      "year": 1989,\n      "director": "Cynthia Verges",\n      "role": "Keith",\n      "tmdbId": 10820\n    },\n    {\n      "title": "Animal House",\n      "year": 1978,\n      "director": "John Landis",\n      "role": "Chip Diller",\n      "tmdbId": 9469\n    },\n    {\n      "title": "The Edge",\n      "year": 1997,\n      "director": "Lee Tamahori",\n      "role": "Richard Attenborough",\n      "tmdbId": 4016\n    },\n    {\n      "title": "Wild Things",\n      "year": 1998,\n      "director": "John McNaughton",\n      "role": "Tommy",\n      "tmdbId": 12109\n    },\n    {\n      "title": "Kiss Kiss Bang Bang",\n      "year": 2005,\n      "director": "Shane Black",\n      "role": "Marv",\n      "tmdbId": 5081\n    },\n    {\n      "title": "Murder in the First",\n      "year": 1995,\n      "director": "Marc Sch?l?ndorff",\n      "role": "John R. Scott",\n      "tmdbId": 5950\n    },\n    {\n      "title": "Hollow Man",\n      "year": 2000,\n      "director": "Paul Verhoeven",\n      "role": "Sebastian Caine",\n      "tmdbId": 2910\n    },\n    {\n      "title": "Footloose",\n      "year": 1984,\n      "director": "Herbert Ross",\n      "role": "William \'Bubba\' Higgins",\n      "tmdbId": 1655\n    },\n    {\n      "title": "The Big Wedding",\n      "year": 2013,\n      "director": "Justin Zackham",\n      "role": "George",\n      "tmdbId": 207643\n    },\n    {\n      "title": "A Time to Kill",\n      "year": 1996,\n      "director": "Joel Schumacher",\n      "role": "Jake Brigance",\n      "tmdbId": 11268\n    },\n    {\n      "title": "Quicksand: No Escape",\n      "year": 2021,\n      "director": "Terry Green",\n      "role": "Detective",\n      "tmdbId": 621362\n    },\n    {\n      "title": "The Letdown",\n      "year": 2013,\n      "director": "Garry Marshall",\n      "role": "Jack",\n      "tmdbId": 206981\n    },\n    {\n      "title": "You Should Have Left",\n      "year": 2020,\n      "director": "David Koepp",\n      "role": "Himself",\n      "tmdbId": 584074\n    },\n    {\n      "title": "Crazy, Stupid, Love",\n      "year": 2011,\n      "director": "Glenn Ficarra",\n      "role": "Jacob",\n      "tmdbId": 594970\n    },\n    {\n      "title": "The Following",\n      "year": 2013,\n      "director": "Kevin Williamson",\n      "role": "Mark",\n      "tmdbId": 254048\n    }\n  ]\n}',
        type: 'ASSISTANT',
        metadata: {
            role: 'ASSISTANT',
            messageType: 'ASSISTANT',
            refusal: '',
            finishReason: 'STOP',
            annotations: [],
            index: 0,
            id: 'chatcmpl-DkUkTEBzuSeDiyFdKELSgTiEJZ866'
        }
    },
    timestamp: ISODate('2026-05-28T13:07:44.424Z'),
    _class: 'org.springframework.ai.chat.memory.repository.mongo.Conversation'
}
```

This is a picture of the MongoDB collection with the conversations:

![mongo_express_screen_shot.png](mongo_express_screen_shot.png)

## Conclusions

You can use the integration with Spring Boot AI to create a lot of different applications. You only to have in mind that
every call to the AI will cost you money. You can use Ollama to run AI locally, but you have to check your machine
capabilities, or install on a server with the necessary configuration to run a stronger model. It's possible to use
memory to save the conversations with the user, then the context will be richer and the answers will be better. You can
also use the image generation to create images based on some prompt you want to use. The possibilities are endless, and
it's up to you to explore them.
