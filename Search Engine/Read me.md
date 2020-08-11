# Read me

The project aims to create a simple search engine. It is part of the work in **CSCI 572: Information Retrieval and Web Search Engines**.

Here are the working flows:

1. Post files (all .html texts) to the Solr server and index these files. The searching result will be sorted by default order (Score descending).
2. Create an edge list. This part is based on Java, including libraries like Jsoup.
3. Compute page ranks for each file. This part is based on Python, including libraries like networkx. We also need the edge list in the above step to generate a directed graph. 
4. Import the page rank data into the Solr server and then we can customize the sorting order as PageRank descending.
5. To display the searching results better, use Solr client for PHP.

For additional features like suggester and corrector:

1. We use the FuzzyLookupFactory suggester built in the Solr server. All we need to do is setting it up manually.
2. To build a corrector, first use Apache Tika to read all files into a text file ("big.txt"). Then we run a spell corrector script to get the correct/closest word.

Finally, you can check [this](https://www.youtube.com/watch?v=k2yBkOmAtss&feature=youtu.be) to view a running demo. Thanks for your reading!

