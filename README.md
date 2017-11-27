# Information Retrieval

## Details:
This was a part of my Information Retrieval Course that I have taken in the Spring 2018 Semester. In this course our main tasks were to build a Google look-alike XML document searcher. A User can just input a string or group of strings, the search engine gives back the relevant XML documents that have those strings.

## Technologies:
1. Java
2. Hadoop
3. Django (Web-Interface)

* __Phase One__: Intitially, created a basic search engine which goes through all the XML files parses the relevant content information. Here content is Indexed in 3 different ways: _Uniword Indexing_, _Bi-word Indexing_ and _Postional Indexing_.
  *	Uniword Indexing: In this, the files gives us the list of words/tokens that have occurred in the input data set and their corresponding                       frequencies. The output of the uniword indexing is a text document which is in the form of key-value pairs.
  * Bi-word Indexing: This indexing of the input files gives us the list of bigram words which occur side-by-side or consecutively and                           their frequencies. After the indexing, we will be having key-value pairs of bi-wordsand their frequencies.  
  * Postional Indexing: Positional indexing is almost similar to uniword indexing but it gives extra information about where the word/token                       appears with their position. After the indexing, we will have key-value pairs of words and frequency along with list
                      of occurances.  

* __Phase Two__: In this Phase, Implemented a rank-retrieval model using TF-IDF. For this stage, we have used the uniword indexing                          file(after the phase 1) to get the list of key-value pairs with words as keys and frequency as values. Removed all the                      unnecessary words using Stopwords code. Then calculated the dot-product(cosine similarity) between TF-IDF values of the                    given query and the content of the XML file. More the closer the dot-product value to 1, more the relevant are the                          documents to the given query.

* __Phase Three__: In this Phase, Implemented a rank-retrieval model using Okapi BM25(Best Match 25). BM25 is a family of widespread used                      scoring functions based on probabilistic term weighting models.
