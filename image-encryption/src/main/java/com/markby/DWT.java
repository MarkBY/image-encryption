package com.markby;

public class DWT {
    public static int[][] dwt(int[][] source) {
        int nrows = source.length;
        int ncols = source[0].length;
        int level = 1;
        int[][] result = new int[nrows][ncols];
        int i, j, i1, j1, i2, j2, k;
        int nr, nc, nr2, nc2;
        nr = nrows;
        nc = ncols;

        for (k = 1; k <= level; k++, nr /= 2, nc /= 2) {
            // Horizontal processing:
            nc2 = nc / 2;
            for (i = 0; i < nr; i++) {
                for (j = 0; j < nc; j += 2) {
                    j1 = j + 1;
                    j2 = j / 2;
                    result[i][j2] = source[i][j] + source[i][j1];
                    result[i][nc2 + j2] = source[i][j] - source[i][j1];
                }
            }
            // Copy to source:
            for (i = 0; i < nr; i++)
                for (j = 0; j < nc; j++)
                    source[i][j] = result[i][j];

            // Vertical processing:
            nr2 = nr / 2;
            for (i = 0; i < nr; i += 2) {
                for (j = 0; j < nc; j++) {
                    i1 = i + 1;
                    i2 = i / 2;
                    result[i2][j] = source[i][j] + source[i1][j];
                    result[nr2 + i2][j] = source[i][j] - source[i1][j];
                }
            }
            // Copy to source:
            for (i = 0; i < nr; i++)
                for (j = 0; j < nc; j++)
                    source[i][j] = result[i][j];
        }

        return result;
    }

    public static int[][] idwt(int[][] source) {
        int nrows = source.length;
        int ncols = source[0].length;

        int level = 1;
        int[][] img;
        int[][] result;
        int i, j, i2, j2, k, nr, nc, nr2, nc2, tmp;

        img = new int[nrows][ncols];

        result = new int[nrows][ncols];

        tmp = (int) Math.pow(2, level - 1);
        nr = nrows / tmp;
        nc = ncols / tmp;

        for (k = level; k >= 1; k--, nr *= 2, nc *= 2) {
            // Vertical processing:
            nr2 = nr / 2;
            for (i = 0; i < nr2; i++) {
                for (j = 0; j < nc; j++) {
                    i2 = i * 2;
                    result[i2][j] = (source[i][j] + source[nr2 + i][j]) / 2;
                    result[i2 + 1][j] = (source[i][j] - source[nr2 + i][j]) / 2;
                }
            }
            // Copy to source:
            for (i = 0; i < nr; i++)
                for (j = 0; j < nc; j++)
                    source[i][j] = result[i][j];
            // Horizontal processing:
            nc2 = nc / 2;
            for (i = 0; i < nr; i++) {
                for (j = 0; j < nc2; j++) {
                    j2 = j * 2;
                    result[i][j2] = (source[i][j] + source[i][nc2 + j]) / 2;
                    result[i][j2 + 1] = (source[i][j] - source[i][nc2 + j]) / 2;
                }
            }
            // Copy to source:
            for (i = 0; i < nr; i++)
                for (j = 0; j < nc; j++)
                    source[i][j] = result[i][j];
        } // End of "for k ..."

        for (i = 0; i < nrows; i++)
            for (j = 0; j < ncols; j++) {
                if (source[i][j] > 255)
                    img[i][j] = 255;
                else if (source[i][j] < 0)
                    img[i][j] = 0;
                else
                    img[i][j] = source[i][j];

            }

        return img;
    }
}
