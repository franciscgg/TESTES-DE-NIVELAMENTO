import com.opencsv.CSVWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PDFExtrair {

    public static void extrairPDFParaCSV(String caminhoPDF, String caminhoCSV) {
        if (!isArquivoValido(caminhoPDF)) {
            System.out.println("Arquivo inválido: " + caminhoPDF);
            return;
        }

        try (PDDocument document = PDDocument.load(new File(caminhoPDF))) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String texto = pdfStripper.getText(document);

            try (CSVWriter writer = new CSVWriter(new FileWriter(caminhoCSV))) {
                String[] cabecalho = {"Código", "Procedimento", "Evento", "Valor"};
                writer.writeNext(cabecalho);

                String[] linhas = texto.split("\n");

                for (String linha : linhas) {
                    String[] colunas = linha.split("\\s{2,}");
                    if (colunas.length > 1) {
                        writer.writeNext(colunas);
                    }
                }
            }

            System.out.println("CSV gerado com sucesso: " + caminhoCSV);
        } catch (IOException e) {
            System.err.println("Erro ao processar o PDF: " + e.getMessage());
        }
    }

    private static boolean isArquivoValido(String caminhoPDF) {
        String nomeArquivo = new File(caminhoPDF).getName();
        return nomeArquivo.startsWith("Anexo_I") || nomeArquivo.startsWith("Anexo_II");
    }

    public static void compactarEmZip(String caminhoCSV, String caminhoZip) {
        try (FileInputStream fis = new FileInputStream(caminhoCSV);
             FileOutputStream fos = new FileOutputStream(caminhoZip);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            File fileToZip = new File(caminhoCSV);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            zipOut.closeEntry();

            System.out.println("Arquivo compactado com sucesso: " + caminhoZip);

        } catch (IOException e) {
            System.err.println("Erro ao compactar arquivo: " + e.getMessage());
        }
    }
}
