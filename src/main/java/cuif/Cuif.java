package cuif;

import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class Cuif {
    public int signature;
    public int version;
    public int numberOfStudents;
    public int width;
    public int height;
    public int[] identifier;
    public int[][][] raster;
    // RGB, onde raster[i][j] é a posição do pixel (i,j)
    // raster[i][j][0] é o componente R em (i,j)
    // raster[i][j][1] é o componente G em (i,j)
    // raster[i][j][2] é o componente B em (i,j)
    byte[] cuiffile; // � o array de bytes com dados do arquivo em mem�ria

    Cuif(String filename) throws IOException {
        readFile(filename);
    }

    // Construtor Cuif1 a partir de uma imagem Bitmap
    Cuif(Bitmap bmp, int version, int[] idStudents) throws Exception {
        numberOfStudents = idStudents.length;
        identifier = idStudents;
        width = bmp.width;
        height = bmp.height;

        int offset = 0;
        cuiffile = new byte[width*height*3 + 12 + numberOfStudents*4];
        cuiffile[offset++] = 0x37; // assinatura 5321d = 1537h
        cuiffile[offset++] = 0x15;
        cuiffile[offset++] = 1; // version
        cuiffile[offset++] = (byte)numberOfStudents;

        cuiffile[offset++] = (byte)(width&0x000000ff);
        cuiffile[offset++] = (byte)((width&0x0000ff00) >> 8);
        cuiffile[offset++] = (byte)((width&0x00ff0000) >> 16);
        cuiffile[offset++] = (byte)((width&0xff000000) >> 24);

        cuiffile[offset++] = (byte)(height&0x000000ff);
        cuiffile[offset++] = (byte)((height&0x0000ff00) >> 8);
        cuiffile[offset++] = (byte)((height&0x00ff0000) >> 16);
        cuiffile[offset++] = (byte)((height&0xff000000) >> 24);

        for (int i = 0;i < numberOfStudents;i++) {
            cuiffile[offset++] = (byte)(identifier[i]&0x000000ff);
            cuiffile[offset++] = (byte)((identifier[i]&0x0000ff00) >> 8);
            cuiffile[offset++] = (byte)((identifier[i]&0x00ff0000) >> 16);
            cuiffile[offset++] = (byte)((identifier[i]&0xff000000) >> 24);
        }
        readRGB(bmp.raster, offset);
    }

    /**
     * Leitura do raster do arquivo bitmap para salvar no atributo raster
     * Durante a leitura, o método também salva no array de bytes cuiffile que
     * representa os dados do arquivo cuif
     */
    private void readRGB(int[][][] rasterbmp, int offset) {
        raster = new int[height][width][3];
        for (var i = 0; i < height; i++) {
            for (var j = 0; j < width; j++) {
                var r = rasterbmp[i][j][0];
                cuiffile[offset++] = (byte)(r & 0xff);
                raster[i][j][0] = r;
            }
        }
        
        for (var i = 0; i < height; i++) {
            for (var j = 0; j < width; j++) {
                var g = rasterbmp[i][j][1];
                cuiffile[offset++] = (byte)(g & 0xff);
                raster[i][j][1] = g;
            }
        }

        for (var i = 0; i < height; i++) {
            for (var j = 0; j < width; j++) {
                var b = rasterbmp[i][j][2];
                cuiffile[offset++] = (byte)(b & 0xff);
                raster[i][j][2] = b;
            }
        }
    }

    // Leitura de um arquivo Cuif1
    public void readFile(String filename) throws IOException {
        // Abre o arquivo BMP
        var path = Paths.get(filename);

        // Leitura dos bytes do arquivo
        cuiffile = Files.readAllBytes(path);

        signature = (cuiffile[0] & 0xff) | (cuiffile[1] & 0xff) << 8;
        if (signature != 5431) {
            throw new IOException("File format error");
        }

        version  = cuiffile[2] & 0xff;
        numberOfStudents = cuiffile[3] & 0xff;
        width = (cuiffile[4] & 0xff) | 
                (cuiffile[5] & 0xff) << 8 |
                (cuiffile[6] & 0xff) << 16 |
                (cuiffile[7] & 0xff) << 24;
        height = (cuiffile[8] & 0xff) |
                 (cuiffile[9] & 0xff) << 8 |
                 (cuiffile[10] & 0xff) << 16 |
                 (cuiffile[11] & 0xff) << 24;

        identifier = new int[numberOfStudents];

        var index = 12;
        for (var i = 0; i < numberOfStudents; i++) {
            identifier[i] =
                (cuiffile[index++] & 0xff) |
                (cuiffile[index++] & 0xff) << 8 |
                (cuiffile[index++] & 0xff) << 16 |
                (cuiffile[index++] & 0xff) << 24;
        }

        raster = new int[height][width][3];

        if (version == 1) {
            readPixels(index);
        } else {
            throw new IOException("Unsupported version");
        }
    }

    // Leitura dos pixels do arquivo cuif1
    private void readPixels(int index) throws IOException {
        for (var i = 0; i < height; i++) {
            for (var j = 0; j < width; j++) {
                raster[i][j][0] = cuiffile[index++] & 0xff;;
            }
        }
        for (var i = 0; i < height; i++) {
            for (var j = 0; j < width; j++) {
                raster[i][j][1] = cuiffile[index++] & 0xff;
            }
        }
        for (var i = 0; i < height; i++) {
            for (var j = 0; j < width; j++) {
                raster[i][j][2] = cuiffile[index++] & 0xff;
            }
        }
    }

    // Salva a imagem
    public void save(String filename) {
        try {
            var fileOuputStream = new FileOutputStream(filename);
            fileOuputStream.write(cuiffile);
            fileOuputStream.close();
        } catch (IOException ioex) {
            System.err.println("Failed to save CUIF Image: " + ioex.getMessage());
        }
    }
}
