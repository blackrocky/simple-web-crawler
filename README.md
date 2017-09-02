# simple-web-crawler
You are required to design and create a simple web crawler. It needs to take
a URL as a parameter and create a tree of child pages linked to the URL. It’s
expected that your application provides deep-crawling solution, meaning that
it goes through multiple levels in link hierarchy.
Create a simple API endpoint that would take URL as a parameter and
return json representing the tree described above. Each node should have at
least following fields: url, title, nodes. See example for more details.
You can use any frameworks or libraries suitable for the task. You might be
asked why you made certain choices during interview.
End result should be a zip file that contains maven or Gradle project. It
should also have a README file with instructions and potential assumptions
you had to make during development.

## Assumptions
- Identical link can only be process once in 1 crawl
- Max depth can be set in properties file
- Connection and read timeout can be set in properties file
- Only absolute links are considered
- Links that start with http are valid
- Ends with PDF?
- Use ConcurrentHashMap for visited links?
- Simultaneously crawl links  using executors?
- OWASP concerns?
- Store processed links in db/file
- Respect robots.txt
- Multiple machines?
- Add or update links after being processed? Crawling frequency
- Indexing
- Pages that can't be accessed / timed out should be skipped
- Changing links in a website
- Https?
- Use db as storage, not cache

## DONE
- Depth of search
- Use java.net.URL
- Ports adapters

## Software Needed
- JDK 8
- Gradle (bundled)

## Usage
- cd simple-web-crawler-jar-deployable
- ../gradlew bootRun