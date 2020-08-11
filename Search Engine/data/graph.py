import networkx as nx

G = nx.read_edgelist("C:\\Users\\Xinyuan\\Desktop\\hw4\\edgeList.txt", create_using=nx.DiGraph())
pr = nx.pagerank(G, alpha=0.85, personalization=None, max_iter=30, tol=1e-06, nstart=None, weight='weight', dangling=None)
output = open("C:\\Users\\Xinyuan\\Desktop\\hw4\\external_pageRankFile.txt", "w")
for k, v in pr.items():
    output.write("C:\\Users\\Xinyuan\\Desktop\\hw4\\nytimes\\nytimes\\" + k + "=" + str(v) + "\n")
output.close()
