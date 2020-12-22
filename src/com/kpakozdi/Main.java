package com.kpakozdi;

import java.io.*;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        if(args.length != 1) {
            System.err.println("Használat: assignment_problem.jar PATH|DIM");
            System.err.println("Ahol PATH a költségmátrix állomány abszolút útvonala,");
            System.err.println("random költségmátrix generálása esetén DIM a mátrix dimenziója.");
            System.exit(1);
        }
        String firstParam = args[0];
        int[][] c = null;

        try {
            int N = Integer.parseInt(firstParam);
            System.out.printf("%d x %1$d random költségmátrix generálása...%n", N);
            Random r = new Random();
            c = new int[N][N];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    c[i][j] = r.nextInt(N);
                }
            }
            System.out.println("Mátrix kimentése további elemzéshez...");
            saveMatrix(c);
        } catch (NumberFormatException e) {
            try {
                System.out.println("Mátrix állomány beolvasása...");
                c = readMatrixFromFile(firstParam);
            } catch (IOException ioException) {
                System.err.println("Nem sikerült beolvasni a fájlt.");
                e.printStackTrace();
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("Ismeretlen hiba.");
            e.printStackTrace();
            System.exit(1);
        }

        AssignmentProblem assignmentProblem = new AssignmentProblem(c);

        System.out.println("Indulás...");
        long start = System.currentTimeMillis();
        assignmentProblem.solve();
        long stop = System.currentTimeMillis() - start;

        System.out.println("A futtatás " + stop + " ms-t vett igénybe.");
    }

    private static void saveMatrix(int[][] c) {
        File f = new File("costMatrix.txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            for (int i = 0; i < c.length; i++) {
                for (int j = 0; j < c[i].length; j++) {
                    bw.write(Integer.toString(c[i][j]));
                    if (j < c[i].length - 1) {
                        bw.write(" ");
                    }
                }
                if (i < c.length - 1) {
                    bw.write("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int[][] readMatrixFromFile(String path) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
        List<String> lines = bufferedReader.lines().collect(Collectors.toList());
        int[][] cost = ArrayUtils.zeros(lines.size());
        int N = lines.size();
        for (int i = 0; i < N; i++) {
            String[] splitted = lines.get(i).split(" ");
            int[] row = new int[N];
            for (int j = 0; j < N; j++) {
                row[j] = Integer.parseInt(splitted[j]);
            }
            cost[i] = row;
        }

        return cost;
    }
}
