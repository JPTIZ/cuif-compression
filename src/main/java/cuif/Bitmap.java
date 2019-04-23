package cuif;

import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

// Classe representando o conteúdo de um arquivo bitmap
public class Bitmap {
    // campos do cabeçalho bitmap
    public int sizeOfBitmapFile;
    public int reserved;
    public int offsetToStartImage;
    public int sizeOfHeader; //must be 40
    public int width;
    public int height;
    public int numberOfPlanes;
    public int numberOfBitsPerPixel;
    public int compressionType;
    public int sizeOfImageInBytes;
    public int horizontalPixelsPerMeter;
    public int verticalPixelsPerMeter;
    public int numberOfColors;
    public int numberOfImportantColors;

    // reticulado da imagem mantida no arquivo
    public int[][][] raster;
    // RGB, onde raster[i][j] é a posição do pixel (i,j)
    // raster[i][j][0] é o componente R em (i,j)
    // raster[i][j][1] é o componente G em (i,j)
    // raster[i][j][2] é o componente B em (i,j)

    // é o array de bytes com dados do arquivo BitMap em memória, que serão salvo
    byte[] bitmapfile;

    Bitmap() {
        width = height = 0;
    }

    // Construtor para gerar objeto Bitmap a partir do arquivo bitmap de nome "filename"
    Bitmap(String filename) throws Exception {

        // Abre o arquivo BMP
        Path path = Paths.get(filename);

        // Leitura dos bytes do arquivo
        byte[] bitmapfile = Files.readAllBytes(path);

        // Leitura dos campos do cabeçalho BMP
        int signature = (bitmapfile[0]&0xff) | (bitmapfile[1]&0xff) << 8;
        if (signature != 0x4D42) {
            throw new IOException("File format error :" + Integer.toHexString(signature));
        }

        sizeOfBitmapFile = (bitmapfile[2]&0xff)|(bitmapfile[3]&0xff) << 8|
            (bitmapfile[4]&0xff) << 16| (bitmapfile[5]&0xff) << 24;
        reserved = (bitmapfile[6]&0xff)|(bitmapfile[7]&0xff) << 8|(bitmapfile[8]&0xff) << 16|
            (bitmapfile[9]&0xff) << 24;

        if (reserved != 0) {
            throw new IOException("BMP Format Unsuported: Reserved");
        }

        offsetToStartImage = (bitmapfile[10]&0xff)|(bitmapfile[11]&0xff) << 8|
            (bitmapfile[12]&0xff) << 16| (bitmapfile[13]&0xff) << 24;
        sizeOfHeader = (bitmapfile[14]&0xff)|(bitmapfile[15]&0xff) << 8|
            (bitmapfile[16]&0xff) << 16| (bitmapfile[17]&0xff) << 24;
        width = (bitmapfile[18]&0xff)|(bitmapfile[19]&0xff) << 8|(bitmapfile[20]&0xff) << 16|
            (bitmapfile[21]&0xff) << 24;
        height = (bitmapfile[22]&0xff)|(bitmapfile[23]&0xff) << 8|(bitmapfile[24]&0xff) << 16|
            (bitmapfile[25]&0xff) << 24;
        numberOfPlanes = (bitmapfile[26]&0xff)|(bitmapfile[27]&0xff) << 8;
        numberOfBitsPerPixel = (bitmapfile[28]&0xff)|(bitmapfile[29]&0xff) << 8|(bitmapfile[30]&0xff) << 16|
            (bitmapfile[31]&0xff) << 24;
        compressionType = (bitmapfile[32]&0xff)|(bitmapfile[33]&0xff) << 8|(bitmapfile[34]&0xff) << 16|
            (bitmapfile[35]&0xff) << 24;
        sizeOfImageInBytes = (bitmapfile[36]&0xff)|(bitmapfile[37]&0xff) << 8|(bitmapfile[38]&0xff) << 16|
            (bitmapfile[39]&0xff) << 24;
        horizontalPixelsPerMeter = (bitmapfile[40]&0xff)|(bitmapfile[41]&0xff) << 8|(bitmapfile[42]&0xff) << 16|
            (bitmapfile[43]&0xff) << 24;
        verticalPixelsPerMeter = (bitmapfile[44]&0xff)|(bitmapfile[45]&0xff) << 8|(bitmapfile[46]&0xff) << 16|
            (bitmapfile[47]&0xff) << 24;
        numberOfColors = (bitmapfile[48]&0xff)|(bitmapfile[49]&0xff) << 8|(bitmapfile[50]&0xff) << 16|
            (bitmapfile[51]&0xff) << 24;
        numberOfImportantColors = (bitmapfile[52]&0xff)|(bitmapfile[53]&0xff) << 8|(bitmapfile[54]&0xff) << 16|
            (bitmapfile[55]&0xff) << 24;

        // Criação do raster (reticulado)
        raster = new int[height][width][3];

        byte r, g, b;
        int index = offsetToStartImage;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                b = (byte)(bitmapfile[index++]&0xff);
                g = (byte)(bitmapfile[index++]&0xff);
                r = (byte)(bitmapfile[index++]&0xff);
                raster[i][j][0] = r;
                raster[i][j][1] = g;
                raster[i][j][2] = b;
            }
        }
    }

    // Converte um arquivo Cuif para Bitmap.
    public void cuiftoBitmap(String filename) throws IOException {
        // abre arquivo cuif e lê bytes
        Path path = Paths.get(filename);
        byte[] cuiffile = Files.readAllBytes(path);

        int signature = (cuiffile[0]&0xff) | (cuiffile[1]&0xff) << 8;
        if (signature != 5431)
            throw new IOException("File format error");

        int version = cuiffile[2];

        int numberOfStudents = cuiffile[3]&0xff;
        width = (cuiffile[4]&0xff)|(cuiffile[5]&0xff) << 8|(cuiffile[6]&0xff) << 16|
            (cuiffile[7]&0xff) << 24;
        height = (cuiffile[8]&0xff)|(cuiffile[9]&0xff) << 8|(cuiffile[10]&0xff) << 16|
            (cuiffile[11]&0xff) << 24;

        System.out.print("Arquivo gerado pelo(s) estudante(s):");
        int j = 12;
        for (int i = 0;i < numberOfStudents;i++) {
            int matricula = (cuiffile[j++]&0xff)|(cuiffile[j++]&0xff) << 8|(cuiffile[j++]&0xff) << 16|
                (cuiffile[j++]&0xff) << 24;
            System.out.print(matricula + ", ");
        }

        /* preparando array bitmapfile */
        sizeOfBitmapFile = 54 + height*width*3; // Cabeçalho de 54 bytes
        int i = 0;
        bitmapfile = new byte [sizeOfBitmapFile];

        // Assinatura
        bitmapfile[i++] = 0x42;
        bitmapfile[i++] = 0x4d;
        //tamanho do arquivo
        bitmapfile[i++] = (byte)(sizeOfBitmapFile&0x000000ff);
        bitmapfile[i++] = (byte)((sizeOfBitmapFile&0x0000ff00) >> 8);
        bitmapfile[i++] = (byte)((sizeOfBitmapFile&0x00ff0000) >> 16);
        bitmapfile[i++] = (byte)((sizeOfBitmapFile&0xff000000) >> 24);

        // reserved
        bitmapfile[i++] = bitmapfile[i++] = bitmapfile[i++] = bitmapfile[i++] = 0;

        // offset do inicio da imagem
        offsetToStartImage = 54; // 54 bytes usado no cabeçalho
        bitmapfile[i++] = (byte)offsetToStartImage;
        bitmapfile[i++] = bitmapfile[i++] = bitmapfile[i++] = 0;

        // sizeOfHeader
        sizeOfHeader = 40;
        bitmapfile[i++] = (byte) sizeOfHeader;
        bitmapfile[i++] = bitmapfile[i++] = bitmapfile[i++] = 0;

        // Width
        bitmapfile[i++] = (byte)(width&0x000000ff);
        bitmapfile[i++] = (byte)((width&0x0000ff00) >> 8);
        bitmapfile[i++] = (byte)((width&0x00ff0000) >> 16);
        bitmapfile[i++] = (byte)((width&0xff000000) >> 24);

        // Height
        bitmapfile[i++] = (byte)(height&0x000000ff);
        bitmapfile[i++] = (byte)((height&0x0000ff00) >> 8);
        bitmapfile[i++] = (byte)((height&0x00ff0000) >> 16);
        bitmapfile[i++] = (byte)((height&0xff000000) >> 24);

        // number of planes
        numberOfPlanes = 1;
        bitmapfile[i++] = (byte)numberOfPlanes;
        bitmapfile[i++] = 0;

        // numberOfBitsPerPixel
        numberOfBitsPerPixel = 24;
        bitmapfile[i++] = (byte)numberOfBitsPerPixel;
        bitmapfile[i++] = bitmapfile[i++] = bitmapfile[i++] = 0;

        // Compression Type
        compressionType = 0;
        bitmapfile[i++] = (byte)compressionType;
        bitmapfile[i++] = bitmapfile[i++] = bitmapfile[i++] = 0;

        // Size of image
        sizeOfImageInBytes = width*height*3;
        bitmapfile[i++] = (byte)(sizeOfImageInBytes&0x000000ff);
        bitmapfile[i++] = (byte)((sizeOfImageInBytes&0x0000ff00) >> 8);
        bitmapfile[i++] = (byte)((sizeOfImageInBytes&0x00ff0000) >> 16);
        bitmapfile[i++] = (byte)((sizeOfImageInBytes&0xff000000) >> 24);

        // horizontal pixels per meter
        bitmapfile[i++] = (byte)(sizeOfImageInBytes&0x000000ff);
        bitmapfile[i++] = (byte)((sizeOfImageInBytes&0x0000ff00) >> 8);
        bitmapfile[i++] = (byte)((sizeOfImageInBytes&0x00ff0000) >> 16);
        bitmapfile[i++] = (byte)((sizeOfImageInBytes&0xff000000) >> 24);

        // vertical pixels per meter
        bitmapfile[i++] = (byte)(sizeOfImageInBytes&0x000000ff);
        bitmapfile[i++] = (byte)((sizeOfImageInBytes&0x0000ff00) >> 8);
        bitmapfile[i++] = (byte)((sizeOfImageInBytes&0x00ff0000) >> 16);
        bitmapfile[i++] = (byte)((sizeOfImageInBytes&0xff000000) >> 24);

        // number of colors
        numberOfColors = 0;
        bitmapfile[i++] = (byte)numberOfColors;
        bitmapfile[i++] = bitmapfile[i++] = bitmapfile[i++] = 0;

        // numberOfImportantColors
        numberOfImportantColors = 0;
        bitmapfile[i++] = (byte)numberOfImportantColors;
        bitmapfile[i++] = bitmapfile[i++] = bitmapfile[i++] = 0;

        // gera raster
        if (version == 1)
            cuif1toRaster(cuiffile, numberOfStudents*4 + 12);
        else
            throw new IOException("Versão não suportada");

    }

    // Método que lê os pixels do arquivo Cuif1 e gera raster, incluindo no array de bytes do arquivo

    private void cuif1toRaster(byte[] cuiffile, int index) {
        int r, g, b;
        raster = new int[height][width][3];

        // Leitura do raster (bitmap-reticulado) do arquivo CUIF.1
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                r = cuiffile[index++]&0xff;
                raster[i][j][0] = r;
            }
        }
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                g = cuiffile[index++]&0xff;
                raster[i][j][1] = g;
            }
        }
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                b = cuiffile[index++]&0xff;
                raster[i][j][2] = b;
            }
        }

        // Escrita no array de bytes (bitmapfile) que representa em memória o arquivo Bitmap
        int k = offsetToStartImage;
        for (int i = height-1; i >= 0; i--) {
            for (int j = width-1; j >= 0; j--) {
                bitmapfile[k++] = (byte)raster[i][j][0]; // escreve componente R
                bitmapfile[k++] = (byte)raster[i][j][1]; // escreve componente G
                bitmapfile[k++] = (byte)raster[i][j][2]; // escreve componente B

            }
        }
    }

    public int getHeigth() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    // Método para salvar o arquivo em "filename"
    public void save(String filename) {
        try {
            FileOutputStream fileOuputStream = new FileOutputStream(filename);
            fileOuputStream.write(bitmapfile);
            fileOuputStream.close();
        } catch (IOException ioex) {
        }
    }
}
