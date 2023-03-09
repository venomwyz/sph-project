package com.atguigu.es.test;

import java.util.Scanner;

public class TicTacToe {
    public static void main(String[] args) {
        char[][] board = new char[3][3];
        int turn = 0;
        char player = 'X';
        boolean gameOver = false;

        // 初始化游戏面板
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }

        // 开始游戏循环
        while (!gameOver) {
            // 显示游戏面板
            System.out.println("  1 2 3");
            for (int i = 0; i < 3; i++) {
                System.out.print(i + 1 + " ");
                for (int j = 0; j < 3; j++) {
                    System.out.print(board[i][j] + " ");
                }
                System.out.println();
            }

            // 获取玩家输入并更新游戏状态
            Scanner scanner = new Scanner(System.in);
            System.out.printf("轮到 %c 玩家，请输入要下棋的位置（行列）：", player);
            int row = scanner.nextInt() - 1;
            int col = scanner.nextInt() - 1;

            if (board[row][col] != ' ') {
                System.out.println("该位置已经有棋子，请重新输入！");
            } else {
                board[row][col] = player;
                turn++;

                // 判断游戏是否结束
                if (checkWin(board, player)) {
                    System.out.println(player + " 玩家获胜！");
                    gameOver = true;
                } else if (turn == 9) {
                    System.out.println("平局！");
                    gameOver = true;
                } else {
                    // 切换玩家
                    player = player == 'X' ? 'O' : 'X';
                }
            }
        }
    }

    // 检查是否有一方玩家获胜
    public static boolean checkWin(char[][] board, char player) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                return true;
            }
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
                return true;
            }
        }
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true;
        }
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            return true;
        }
        return false;
    }
}
//在这个示例中，我们使用了一个二维字符数组来表示游戏面板，'X'表示X玩家的棋子，'O'






