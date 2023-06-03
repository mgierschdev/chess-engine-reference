package com.backend.util;

public class Util {
    public static String GetChessNotation(int row, int col){
        // a - h cols
        // 1 - 9 rows

        char colChar =(char)('a' + col);
        int rowNumber = row + 1;

        return colChar+String.valueOf(rowNumber);
    }

    public static int[] GetMatrixNotation(String chessNotation){
        char[] target = chessNotation.toCharArray();

        int colChar = target[0] - 'a';
        int rowNumber = target[1] - '0';

        return new int[]{rowNumber - 1, colChar};
    }
}
