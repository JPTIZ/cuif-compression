/**
 * @author Roberto Willrich
 *
 * Aplicação Java usada para gerar arquivo CUIF a partir de um arquivo BMP (Windows Bitmap)
 *
 * Exemplo de uso: java bmp2cuif --version 1 lena.bmp lena.cuif
 *
 */
package cuif;


public class BmpToCuif {
    public static void main(String[] args) {
        // Analisa argumentos
        if (args.length != 4) {
            System.out.printf("Número errado de argumentos (%d)\n", args.length);
            System.out.println(
                "Uso: \n" +
                "    java bmp2cuif --version|-v <version>" +
                " <arquivo entrada> <arquivo saida>"
            );
            return;
        }

        if (!(args[0].equals("-v") || args[0].equals("--version"))) {
            System.out.println("Argumento desconhecido: " + args[0]);
            System.out.println(
                "Sintaxe: java bmp2cuif -v <version> <arquivo entrada> <arquivo saida>"
            );
            return;
        }

        try {
            var version = Integer.parseInt(args[1]);
            if (version != 1) {
                System.out.println("Versão não suportada");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Versão não suportada");
        }

        var inputFile = args[2];
        var outputFile = args[3];

        try {
            // Leitura de arquivo bmp
            var bmpimg = new Bitmap(inputFile);

            // Criação de do arquivo Cuif1 a partir da leutura do arquivo
            // lena.bmp modifique numero_estudantes e id_estudante indicando o
            // número de membros da equipe e a matrícula dos membros
            var idStudents = new int[] {14200743};
            var filecuif = new Cuif(bmpimg, 1, idStudents);
            filecuif.save(outputFile);
        } catch (Exception ioex) {
            ioex.printStackTrace();
        }
    }
}
