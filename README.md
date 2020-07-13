# Algorithms

This is a repository written in my experiments for several algorithms.

## HGA

Obtain reliable and optimal mapping between networks concerning about protein sequences and topological similarity  

![test](README.assets/test2-1594604673886.png)

<img src="README.assets/image-20200608122144302.png" alt="image-20200608122144302" style="zoom: 67%;" />

Please refer to An Adaptive Hybrid Algorithm for Global Network Alignment[http://dx.doi.org/10.1109/TCBB.2015.2465957] for the design of the algorithm.

### case 1 : biomolecular network

Get the mapping subgraph across species. 

### case 2: drug repurposing by alignment

<img src="README.assets/image-20200608122510098.png" alt="image-20200608122510098" style="zoom: 50%;" />

### How to run 

First you have to build by maven, and in your server command line, input the follow command

```
nohup mvn clean test -Dtest=Algorithms.Graph.HGA.HGARunSpec#<test> &
```

'<test>' can be any junit test class method, for example:

```java
  @Test
    void run_yeast() throws IOException {
         // reader for reading undirected graphs and the similarity matrix
        GraphFileReader reader = new GraphFileReader(true, false, false);
        // read graphs with the file path
        undG1 = reader.readToUndirectedGraph("src/test/java/resources/TestModule/HGATestData/Human-YeastSub38N/net-38n.txt", false);
        undG2 = reader.readToUndirectedGraph("src/test/java/resources/TestModule/HGATestData/Human-YeastSub38N/HumanNet.txt", false);
        // undirected graphs don't have to record neighbors
        reader.setRecordNeighbors(false);
        // read the simMat 
        simMat = reader.readToSimMat("src/test/java/resources/TestModule/HGATestData/Human-YeastSub38N/fasta/yeastHumanSimList_EvalueLessThan1e-10.txt",
                undG1.getAllNodes(), undG2.getAllNodes(), true);
        // hga init
        hga = new HGA(simMat, undG1, undG2, 0.4,true,0.5,0.01);
        // specify where you want the log matrix, score, and mapping result.
        HGA.debugOutputPath = "src\\test\\java\\resources\\Jupiter\\data\\";
        hga.run();
    }
```

Notice: the similarity matrix is consist of the index graph's nodes as its rows, and the target graph's as its columns.

The matrix's size should be equal to or more than m*n as it has to cover all pairs. 



### Applications

* BNMatch

  A Cytoscape app to reach optimized global mapping in biological networks, it will visualize the result returned from HGA.

  ![panel](README.assets/panel.png)

  

