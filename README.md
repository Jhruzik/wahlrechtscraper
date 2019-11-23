# wahlrechtscraper
[wahlrecht.de](http://www.wahlrecht.de/) publishes up-to-date polls for the Bundestag and all state legislatures. These polls can be found under the subsection *Sonntagsfrage*. 

wahlrechtscraper is designed to extract this data and present it in a human readable table on a user's terminal. You can either get the polls for the Bundestag or all state legislatures
.

### Installation
Before you can use wahlrechtscraper, make sure to download [Scala](https://docs.scala-lang.org) since it was written and compiled in that language.
You can find the latest version of wahlrechtscraper in an executable .jar file under releases.

You can execute the .jar file either by entering `scala wahlrechtscraper` or `scala wahlrechtscraper states`. The first command  will yield recent polls for the German Bundestag while the second command would yield the most up-to-date poll for every state. You could also enter `scala wahlrechtscraper bund`. That would also yield polls for the German Bundestag.
