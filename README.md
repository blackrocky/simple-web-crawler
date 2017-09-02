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
- Same link cannot be processed twice for 1 crawl
- OWASP concerns?
- Store processed links in db/file
- Respect robots.txt
- Multiple machines?
- Add or update links after being processed? Crawling frequency
- Simultaneously crawl links  using executors
- Indexing
- Pages that can't be accessed / timed out should be skipped
- Changing links in a website
- Https?
- Depth of search
- Use db as storage, not cache
- Use ConcurrentHashMap for visited links?
- Use java.net.URL
- Ports adapters?

## Software Needed
- JDK 8
- Gradle (bundled)

## Usage
- ./gradlew bootRun