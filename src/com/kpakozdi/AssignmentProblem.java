package com.kpakozdi;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.kpakozdi.ArrayUtils;

public class AssignmentProblem {
    private final int[][] c;
    private final int[][] original;
    private final int N;
    private final int[] markedRows;
    private final int[] markedColumns;
    private int[] staredZerosRow;
    private int[] staredZerosColumn;
    private int[][] commas;

    public AssignmentProblem(int[][] c) {
        this.c = c;
        original = ArrayUtils.copy(c);
        N = c.length;
        staredZerosColumn = ArrayUtils.nums(-1, N);
        staredZerosRow = ArrayUtils.nums(-1, N);
        commas = ArrayUtils.zeros(N);
        markedColumns = new int[N];
        markedRows = new int[N];
    }

    public void solve() {
        reduce();
        ArrayUtils.prettyPrint(c);

        freeZeroSystem();
        markColumnsAndRows();

        while (numberOfFreeZeros() < N) {
            int[] freeZero = findFreeZero();
            if (freeZero != null) {
                int row = freeZero[0];
                int column = freeZero[1];
                System.out.println("Van szabad 0: (" + row + ", " + column + ").");
                int assignedZeroColumn = findAssignedZeroColumnInRow(row);
                // Mindenképp el kell látni a szabad 0-t vesszővel
                commas[row][column] = 1;
                if (assignedZeroColumn > -1) {
                    System.out.println("\tVan hozzárendelt nulla, jelölés vesszővel...");
                    markedRows[row] = 1;
                    markedColumns[assignedZeroColumn] = 0;
                } else {
                    // ehh
                    System.out.println("\tNincs hozzárendelt nulla, láncképzés...");
                    Set<int[]> chain = new LinkedHashSet<>();
                    chain.add(freeZero);
                    boolean nextFound = true;
                    boolean lookingForRow = true;
                    do {
                        if (lookingForRow) {
                            int r0 = staredZerosColumn[column];
                            if (r0 > -1) {
                                chain.add(new int[]{r0, column});
                                row = r0;
                                lookingForRow = false;
                                continue;
                            }
                        } else {
                            int c0 = findCommaInRow(row);
                            if (c0 > -1) {
                                chain.add(new int[]{row, c0});
                                column = c0;
                                lookingForRow = true;
                                continue;
                            }
                        }
                        nextFound = false;
                    } while (nextFound);
                    System.out.print("\tA lánc: ");
                    System.out.println(chain.stream().map(Arrays::toString).collect(Collectors.joining(" <-> ")));
                    for (int[] link : chain) {
                        int r = link[0];
                        int c = link[1];
                        if (commas[r][c] == 0) {
                            staredZerosRow[r] = -1;
                            staredZerosColumn[c] = -1;
                        }
                        markedRows[r] = 0;
                    }
                    for (int[] link : chain) {
                        int r = link[0];
                        int c = link[1];
                        if (commas[r][c] == 1) {
                            staredZerosRow[r] = c;
                            staredZerosColumn[c] = r;
                            // ha az adott sor nincs lekötve, akkor kössük le az oszlopot (tökmindegy, hogy az le volt-e vagy sem korábban)
                            if (markedRows[r] == 0) {
                                markedColumns[c] = 1;
                            }
                        }
                    }
                    commas = ArrayUtils.zeros(N);
                }
            } else {
                System.out.println("Nincs újabb szabad 0, új 0-k képzése...");
                int min = minimumOfFreeElements();
                compensateElements(min);
            }
        }
        System.out.println("Elértük az optimumot.");
        int sum = 0;
        int[][] x = ArrayUtils.zeros(N);
        for (int i = 0; i < N; i++) {
            int j = staredZerosRow[i];
            sum += original[i][j];
            x[i][j] = 1;
        }
        System.out.println("Az X mátrix:");
        ArrayUtils.prettyPrint(x);
        System.out.println("Az optimális költség: " + sum);
    }

    private int findCommaInRow(int row) {
        for (int j = 0; j < N; j++) {
            if (commas[row][j] == 1) {
                return j;
            }
        }
        return -1;
    }

    private void compensateElements(int min) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (isElementFree(i, j)) {
                    c[i][j] -= min;
                }
                if (markedRows[i] == 1 && markedColumns[j] == 1) {
                    c[i][j] += min;
                }
            }
        }
    }

    private int minimumOfFreeElements() {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (isElementFree(i, j)) {
                    min = Math.min(min, c[i][j]);
                }
            }
        }
        return min;
    }

    private boolean isElementFree(int i, int j) {
        return markedRows[i] == 0 && markedColumns[j] == 0 && commas[i][j] == 0 && staredZerosColumn[j] != i && staredZerosRow[i] != j;
    }

    private int findAssignedZeroColumnInRow(int row) {
        for (int j = 0; j < N; j++) {
            if (c[row][j] == 0 && staredZerosRow[row] == j && staredZerosColumn[j] == row) {
                return j;
            }
        }
        return -1;
    }

    private int[] findFreeZero() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (c[i][j] == 0 && markedColumns[j] == 0 && markedRows[i] == 0 && commas[i][j] == 0 &&
                        staredZerosRow[i] != j && staredZerosColumn[j] != i) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    private void markColumnsAndRows() {
        for (int i = 0; i < N; i++) {
            if (staredZerosColumn[i] > -1) {
                markedColumns[i] = 1;
            }
        }
    }

    private int numberOfFreeZeros() {
        int count = 0;
        for (int i = 0; i < N; i++) {
            if (staredZerosColumn[i] > -1) {
                count++;
            }
        }
        return count;
    }

    private void freeZeroSystem() {
        staredZerosRow = ArrayUtils.nums(-1, N);
        staredZerosColumn = ArrayUtils.nums(-1, N);
        int[] fixedRows = new int[N];
        for (int j = 0; j < N; j++) {
            for (int i = 0; i < N; i++) {
                if (c[i][j] == 0 && fixedRows[i] == 0) {
                    staredZerosRow[i] = j;
                    staredZerosColumn[j] = i;
                    fixedRows[i] = 1;
                    break;
                }
            }
        }
    }

    private int[][] reduce() {
        for (int i = 0; i < N; i++) {
            int rowMin = Integer.MAX_VALUE;
            for (int j = 0; j < N; j++) {
                rowMin = Math.min(rowMin, c[i][j]);
            }
            if (rowMin > 0) {
                for (int j = 0; j < N; j++) {
                    c[i][j] -= rowMin;
                }
            }
        }
        for (int i = 0; i < N; i++) {
            int colMin = Integer.MAX_VALUE;
            for (int j = 0; j < N; j++) {
                colMin = Math.min(colMin, c[j][i]);
            }
            if (colMin > 0) {
                for (int j = 0; j < N; j++) {
                    c[j][i] -= colMin;
                }
            }
        }
        return c;
    }
}
