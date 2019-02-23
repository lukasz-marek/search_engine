# Simple search engine for text files

### Prerequisites:
- **openjdk** version "1.8.0_201" or compatible
- **Apache Maven** 3.5.4 (Red Hat 3.5.4-4) or compatible

### Building, testing and running
To build the project, please clone it, then `cd `to the root (*search_engine* directory) and execute: 
`mvn clean package`

The command above creates many *.class* files and two *.jar* files in the *search_engine/target* directory.

To run, please `cd` into `search_engine/target` and execute: 
`java -jar SearchEngine-1.0-SNAPSHOT-jar-with-dependencies.jar /path/to/directory`

**Note**: this *.jar* file contains all the dependencies inside and it can be freely moved between directories without losing
its functionality.

To run tests, please execute `mvn test` in the root directory.

### Usage notes:
- At launch, the program will read all the files in the provided directory **(non-recursively)**. It may take some time.
- When prompt `search> ` appears, you can type the sequence of words to be found, for example: `search> to be or not to be`
- To exit, type `:quit`

### Final comments:
- Due to ambiguity in task description, I wasn't entirely sure whether the file marked as 100% match should contain a phrase ("to be or not to be")
or just all the words in any order and with gaps ("not", "be", "to", "or"). Therefore, a "hybrid" approach was taken: 
the file will me marked as 100% match if it contains all the words in the given order, but up to 2 words (this can easily be adjusted) will be allowed in between,
for example the query "to be or not to be" will be 100% matched with "to be or maybe not to be". On the other hand, query "to be or not to be"
will only have some partial matches with "to be not to be": "to be" and "not to be".
- Search mechanism ignores newlines. Words are treated as if they were all present in a single line.
- What constitutes a word: current implementation, it is assumed that each line of the file contains words separated by whitespace characters.
For example, the sentence "I would like to eat." will be split into "I", "would", "like", "to", "eat.". Search is case sensitive.
- Search is parallel (each file is processed by a single thread, but the number of used threads is limited).
- Worst and best case scenario: best case scenario is when none of the queried words are present in a file. In this case,
the search is completed almost immediately. For the worst case scenario, one would have to prepare a special file. For example,
let's consider a query "to be or not to be". Since the algorithm allows for up to 2 words to be placed between queried words,
it is guaranteed that "to to to be be be or or or not not not be be be" will be recognized as a match. This prepared sequence
can be placed in a file and repeated a multitude of times, for example creating a file with the size of 300MB. Due to how the algorithm works,
it will take a considerable amount of time for it to find all the matches. For a *regular* file, the search should be close to the best case scenario.
- File representations consume a considerable amount of memory. Some kind of lazy (re-) loading or/and in-memory compression might be able to solve this problem.
- The code was tested on a **GNU/Linux** system, it might cause some encoding-related issues on a different OS.

**Author:** Łukasz Marek

