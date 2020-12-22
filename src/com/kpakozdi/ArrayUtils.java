package com.kpakozdi;

public class ArrayUtils {
    public static int[][] copy(int[][] matrix) {
        int[][] copy = new int[matrix.length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            System.arraycopy(matrix[i], 0, copy[i], 0, matrix.length);
        }
        return copy;
    }

    public static void prettyPrint(int[] c) {
        System.out.print("Array = [");
        if (c != null) {
            for (int i = 0; i < c.length; i++) {
                System.out.print(c[i]);
                if (i < c.length - 1) {
                    System.out.print(", ");
                }
            }
        } else {
            System.out.print(" null ");
        }
        System.out.println("]");
    }


    public static void prettyPrint(int[][] c) {
        System.out.println("Matrix = [");
        for (int[] row : c) {
            for (int item : row) {
                System.out.printf("%4d", item);
            }
            System.out.println();
        }
        System.out.println("]");
    }


    public static int[][] zeros(int n) {
        int[][] zeros = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                zeros[i][j] = 0;
            }
        }
        return zeros;
    }

    public static int[] nums(int num, int n) {
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) {
            nums[i] = num;
        }
        return nums;
    }
}
