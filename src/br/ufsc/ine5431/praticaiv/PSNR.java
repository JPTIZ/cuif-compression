package br.ufsc.ine5431.praticaiv;

import java.lang.*;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.stream.IntStream;

public final class PSNR {

    /*
     *  Ferramenta que calcula o PSNR entre um arquivo BMP original e um arquivo BMP decodificado
     */
    public static void main(String[] args) {
        if (args.length!=2) {
            System.out.println("Número errado de argumentos:" + args.length);
            System.out.println("Sintaxe: java PSNR  <arquivo BMP original> <arquivo BMP decodificado>");
            return;
        }

        String original = args[0];
        String decodificado = args[1];

        try {
            Bitmap bmporiginal = new Bitmap(original);
            Bitmap bmpdecodificado = new Bitmap(decodificado);
            System.out.println("Relação de Sinal-Ruído de Pico (PSNR): "
                    + psnr(bmporiginal.raster,bmpdecodificado.raster,24) + " dB");

        } catch (Exception e) {
            e.getMessage();
            e.getStackTrace();
        }

    }

    private static double psnr(int[][][] original, int[][][] decodificado, int bpp) {
        /* TODO
         * Implemente o cálculo do PSNR
         */
        return mse(original, decodificado);
    }

    private static int[] flatten(int[][][] array) {
        return Stream.of(array)
            .flatMapToInt(x -> IntStream.of(flatten(x)))
            .toArray();
    }

    private static int[] flatten(int[][] array) {
        return Stream.of(array)
            .flatMapToInt(row -> IntStream.of(row))
            .toArray();
    }

    private static double mse(int[][][] original, int[][][] decodificado)  {
        /* TODO
         * Implemente aqui o cálculo do MSE. Dica: não esqueça de aplicar o
         * cast (double) e divisões de números inteiros
         */
        var flattenOri = flatten(original);
        var flattenDec = flatten(decodificado);
        var n = flattenOri.length;
        var sum = 0.0;

        for (int i = 0; i < n; i++) {  //percorre linhas
            sum += Math.sqrt(
                Math.pow(flattenOri[i] - flattenDec[i], 2)
            );
        }

        return sum / n;
    }
}
