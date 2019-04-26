package com.example.braintrainer;

import java.util.Random;

public class QuestionBank {
    private static final int EASY_MODE_MIN = 1;
    private static final int EASY_MODE_MAX = 49;
    private static final int MEDIUM_MODE_MIN = 50;
    private static final int MEDIUM_MODE_MAX = 499;
    private static final int HARD_MODE_MIN = 500;
    private static final int HARD_MODE_MAX = 1999;

    int[] generateTwoNumbers(String mode) {
        int[] arr = new int[2];

        Random rand = new Random();

        if (mode.equals("easy")) {
            arr[0] = rand.nextInt(EASY_MODE_MAX) + EASY_MODE_MIN;
            arr[1] = rand.nextInt(EASY_MODE_MAX) + EASY_MODE_MIN;
        } else if (mode.equals("medium")) {
            arr[0] = rand.nextInt(MEDIUM_MODE_MAX) + MEDIUM_MODE_MIN;
            arr[1] = rand.nextInt(MEDIUM_MODE_MAX) + MEDIUM_MODE_MIN;
        } else if (mode.equals("hard")) {
            arr[0] = rand.nextInt(HARD_MODE_MAX) + HARD_MODE_MIN;
            arr[1] = rand.nextInt(HARD_MODE_MAX) + HARD_MODE_MIN;
        }
        return arr;
    }

    int[] generateAnswers(int num1, int num2, String operation) {
        int[] answers = new int[4];

        Random rand = new Random();

        if (operation.equals("add"))
            answers[0] = num1 + num2;
        else if (operation.equals("subtract"))
            answers[0] = num1 - num2;
        else if (operation.equals("multiply")) {
            answers[0] = num1 * num2;
        }

        answers[1] = rand.nextInt(Math.max(num1, num2)) + Math.min(num1, num2);
        answers[2] = rand.nextInt(Math.max(num1, num2));
        answers[3] = rand.nextInt(num1 * num2);

        for (int i = 1; i < 4; i++) {
            while (answers[i] == answers[0])
                answers[i] = rand.nextInt(Math.max(num1, num2)) + Math.min(num1, num2);
        }

        shuffleArray(answers);

        return answers;
    }

    boolean checkAnswers(int num1, int num2, int ans, String operation) {
        if (operation.equals("add"))
            return num1 + num2 == ans;
        else if (operation.equals("subtract"))
            return num1 - num2 == ans;
        else
            return num1 * num2 == ans;
    }

    // Implementing Fisher–Yates shuffle
    private void shuffleArray(int[] numbers) {
        Random rnd = new Random();
        for (int i = numbers.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = numbers[index];
            numbers[index] = numbers[i];
            numbers[i] = a;
        }
    }
}
