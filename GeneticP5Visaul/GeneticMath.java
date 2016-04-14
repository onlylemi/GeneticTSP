import java.util.Arrays;
import java.util.Random;

/**
 * GeneticAlgorithm
 *
 * @author: onlylemi
 */
public class GeneticMath {

    private static final float DEFAULT_CROSSOVER_PROBABILITY = 0.9f; // 默认交叉概率
    private static final float DEFAULT_MUTATION_PROBABILITY = 0.01f; // 默认突变概率
    private static final int DEFAULT_POPULATION_SIZE = 30; // 默认种群数量

    private float crossoverProbability = DEFAULT_CROSSOVER_PROBABILITY; // 交叉概率
    private float mutationProbability = DEFAULT_MUTATION_PROBABILITY; // 突变概率
    private int populationSize = DEFAULT_POPULATION_SIZE; // 种群数量

    private int mutationTimes = 0; // 变异次数
    private int currentGeneration = 0; // 当前的一代
    private int maxGeneration = 1000; // 最大代数

    private Point[] points; // 点集
    private int[][] population; // 种群集
    private float[][] dist; // 点集间的邻接矩阵

    private int[] bestIndivial; // 最短的结果集
    private float bestDist; // 最短的距离
    private int currentBestPosition; // 当前最好个体的位置
    private float currentBestDist; // 当前最好个体的距离

    private float[] values;
    private float[] fitnessValues; // 适应度集
    private float[] roulette;

    private boolean isAutoNextGeneration = false;

    public int[] tsp(Point[] points) {
        this.points = points;
        init();

        if (isAutoNextGeneration) {
            int i = 0;
            while (i++ < maxGeneration) {
                nextGeneration();
            }
        }
        return bestIndivial;
    }

    /**
     * 初始化
     */
    private void init() {
        mutationTimes = 0;
        currentGeneration = 0;
        bestIndivial = null;
        bestDist = 0;
        currentBestPosition = 0;
        currentBestDist = 0;

        values = new float[populationSize];
        fitnessValues = new float[populationSize];
        roulette = new float[populationSize];
        population = new int[populationSize][points.length];

        initDist(points);
        // 父代
        for (int i = 0; i < populationSize; i++) {
            population[i] = randomIndivial(points.length);
        }
        evaluateBestIndivial();
    }

    /**
     * 下一代
     */
    public int[] nextGeneration() {
        currentGeneration++;

        // 选择
        selection();
        // 交叉
        crossover();
        // 变异
        mutation();
        // 评价
        evaluateBestIndivial();

        return bestIndivial;
    }

    /**
     * 选择
     */
    private void selection() {
        int[][] parents = new int[populationSize][points.length];

        int initnum = 4;
        parents[0] = population[currentBestPosition];
        parents[1] = doMutate(bestIndivial.clone());
        parents[2] = pushMutate(bestIndivial.clone());
        parents[3] = bestIndivial.clone();

        setRoulette();
        for (int i = initnum; i < populationSize; i++) {
            parents[i] = population[wheelOut((int) Math.random())];
        }
        population = parents;
    }

    /**
     *
     */
    public void setRoulette() {
        //calculate all the fitness
        for (int i = 0; i < values.length; i++) {
            fitnessValues[i] = 1.0f / values[i];
        }

        //set the roulette
        float sum = 0;
        for (int i = 0; i < fitnessValues.length; i++) {
            sum += fitnessValues[i];
        }
        for (int i = 0; i < roulette.length; i++) {
            roulette[i] = fitnessValues[i] / sum;
        }
        for (int i = 1; i < roulette.length; i++) {
            roulette[i] += roulette[i - 1];
        }
    }

    /**
     * @param ran
     * @return
     */
    private int wheelOut(int ran) {
        for (int i = 0; i < roulette.length; i++) {
            if (ran <= roulette[i]) {
                return i;
            }
        }
        return 0;
    }


    /**
     * 交换变异
     *
     * @param seq
     * @return
     */
    private int[] doMutate(int[] seq) {
        mutationTimes++;
        int m, n;
        do {
            m = random(seq.length - 2);
            n = random(seq.length);
        } while (m >= n);

        int j = (n - m + 1) >> 1;
        for (int i = 0; i < j; i++) {
            int tmp = seq[m + i];
            seq[m + i] = seq[n - i];
            seq[n - i] = tmp;
        }
        return seq;
    }

    /**
     * @param seq
     * @return
     */
    private int[] pushMutate(int[] seq) {
        mutationTimes++;
        int m, n;
        do {
            m = random(seq.length >> 1);
            n = random(seq.length);
        } while (m >= n);

        int[] s1 = Arrays.copyOfRange(seq, 0, m);
        int[] s2 = Arrays.copyOfRange(seq, m, n);
        int[] s3 = Arrays.copyOfRange(seq, n, seq.length);

        return concatAllArray(s1, s2, s3);
    }

    /**
     * 交叉
     */
    private void crossover() {
        int[] queue = new int[populationSize];
        int num = 0;
        for (int i = 0; i < populationSize; i++) {
            if (Math.random() < crossoverProbability) {
                queue[num] = i;
                num++;
            }
        }
        queue = shuffle(queue);
        for (int i = 0; i < num - 1; i++) {
            doCrossover(queue[i], queue[i + 1]);
        }
    }

    private static final int PREVIOUS = 0;
    private static final int NEXT = 1;

    /**
     * @param x
     * @param y
     */
    private void doCrossover(int x, int y) {
        population[x] = getChild(x, y, PREVIOUS);
        population[y] = getChild(x, y, NEXT);
    }

    private int[] getChild(int x, int y, int pos) {
        int[] solution = new int[points.length];
        int[] px = population[x].clone();
        int[] py = population[y].clone();

        int dx = 0, dy = 0;
        int c = px[random(px.length)];
        solution[0] = c;

        int i = 1;
        while (px.length > 1) {
            int posX = indexOf(px, c);
            int posY = indexOf(py, c);

            if (pos == PREVIOUS) {
                dx = px[(posX + px.length - 1) % px.length];
                dy = py[(posY + py.length - 1) % py.length];
            } else if (pos == NEXT) {
                dx = px[(posX + px.length + 1) % px.length];
                dy = py[(posY + py.length + 1) % py.length];
            }
            px = concatAllArray(Arrays.copyOfRange(px, 0, posX), Arrays.copyOfRange(px, posX + 1, px.length));
            py = concatAllArray(Arrays.copyOfRange(py, 0, posY), Arrays.copyOfRange(py, posY + 1, py.length));
            c = dist[c][dx] < dist[c][dy] ? dx : dy;

            solution[i] = c;
            i++;
        }
        return solution;
    }

    /**
     * 变异
     */
    private void mutation() {
        for (int i = 0; i < populationSize; i++) {
            if (Math.random() < mutationProbability) {
                if (Math.random() > 0.5) {
                    population[i] = pushMutate(population[i]);
                } else {
                    population[i] = doMutate(population[i]);
                }
                i--;
            }
        }
    }

    /**
     * 评估最好的个体
     */
    private void evaluateBestIndivial() {
        for (int i = 0; i < population.length; i++) {
            values[i] = calculateIndivialDist(population[i]);
        }
        evaluateBestCurrentDist();
        if (bestDist == 0 || bestDist > currentBestDist) {
            bestDist = currentBestDist;
            bestIndivial = population[currentBestPosition].clone();
        }
    }

    /**
     * 计算个体的距离
     *
     * @return
     */
    private float calculateIndivialDist(int[] indivial) {
        float sum = dist[indivial[0]][indivial[indivial.length - 1]];
        for (int i = 1; i < indivial.length; i++) {
            sum += dist[indivial[i]][indivial[i - 1]];
        }
        return sum;
    }

    /**
     * 评估得到最短距离
     */
    public void evaluateBestCurrentDist() {
        currentBestDist = values[0];
        for (int i = 1; i < populationSize; i++) {
            if (values[i] < currentBestDist) {
                currentBestDist = values[i];
                currentBestPosition = i;
            }
        }
    }


    /**
     * 产生个体（乱序）
     *
     * @param n
     * @return
     */
    private int[] randomIndivial(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = i;
        }

        return shuffle(a);
    }

    /**
     * 乱序处理
     *
     * @param a
     * @return
     */
    private int[] shuffle(int[] a) {
        for (int i = 0; i < a.length; i++) {
            int p = random(a.length);
            int tmp = a[i];
            a[i] = a[p];
            a[p] = tmp;
        }
        return a;
    }

    /**
     * 构建邻接矩阵
     */
    private void initDist(Point[] points) {
        dist = new float[points.length][points.length];
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points.length; j++) {
                dist[i][j] = distance(points[i], points[j]);
            }
        }
    }

    private float distance(Point p1, Point p2) {
        return (float) Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }

    private static Random rd;

    private int random(int n) {
        Random ran = rd;
        if (ran == null) {
            ran = new Random();
        }
        return ran.nextInt(n);
    }

    private int[] concatAllArray(int[] first, int[]... rest) {
        int totalLength = first.length;
        for (int[] array : rest) {
            totalLength += array.length;
        }
        int[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (int[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    private int indexOf(int[] a, int index) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == index) {
                return i;
            }
        }
        return 0;
    }

    /*public class Point {
        public float x;
        public float y;

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }*/

    public int[] getBestIndivial() {
        return bestIndivial;
    }

    public float getBestDist() {
        return bestDist;
    }

    public void setMaxGeneration(int maxGeneration) {
        this.maxGeneration = maxGeneration;
    }

    public void setAutoNextGeneration(boolean autoNextGeneration) {
        isAutoNextGeneration = autoNextGeneration;
    }

    public int getMutationTimes() {
        return mutationTimes;
    }

    public int getCurrentGeneration() {
        return currentGeneration;
    }
}
