# GeneticAlgorithm For TSP (Java)

This is a project to solve [TSP](https://en.wikipedia.org/wiki/Travelling_salesman_problem) by [GeneticAlgorithm](https://en.wikipedia.org/wiki/Genetic_algorithm). If you look the source code, please click [here](https://github.com/onlylemi/genetictsp/blob/master/src%2Fcom%2Fonlylemi%2Fgenetictsp%2FGeneticAlgorithm.java).

## Demo

I use [processing](https://processing.org) to visual this algorithm and test it.  

![geneticdemo](https://raw.githubusercontent.com/onlylemi/GeneticTSP/master/geneticdemo.gif)

If you `processing ide`, download floder named `GeneticP5Visaul`. Then open `GeneticP5Visaul.pde` and run it, you will visual view. But you don't processing ide, you can load processing library or you run `src/GeneticTest.java`, you will watch it in `console`.  

[Processing Demo](https://github.com/onlylemi/GeneticTSP/tree/master/GeneticP5Visaul)
[Java Demo](https://github.com/onlylemi/GeneticTSP/blob/master/src%2Fcom%2Fonlylemi%2Fgenetictsp%2FGeneticTest.java)

## Example

### Method 1

If you use this method, you are better in a `thread`. Because it will return result untill MaxGeneration.
```java
// get a GeneticAlgorithm instance
GeneticAlgorithm ga = GeneticAlgorithm.getInstance();
ga.setMaxGeneration(1000);
ga.setAutoNextGeneration(true);
// points is a array of all point, the function of getdist() is get adjacency matrix
best = ga.tsp(getDist(points));
System.out.print("best path:");
for (int i = 0; i < best.length; i++) {
    System.out.print(best[i] + " ");
}
System.out.println();
```

### Method 2

If you use this method, you will control it by yourselt and using `nextGeneration()` function. 

```java
GeneticAlgorithm ga = new GeneticAlgorithm();
best = ga.tsp(getDist(points));

int n = 0;
while (n++ < 100) {
    best = ga.nextGeneration();

    System.out.println("best distance:" + ga.getBestDist() + " current generation:" + ga.getCurrentGeneration() + " mutation times:" + ga.getMutationTimes());
    System.out.print("best path:");
    for (int i = 0; i < best.length; i++) {
        System.out.print(best[i] + " ");
    }
    System.out.println();
}
```

## About me

Welcome to pull [requests](https://github.com/onlylemi/GeneticTSP/pulls).  

If you have any new idea about this project, feel free to [contact me](mailto:onlylemi.com@gmail.com). :smiley: