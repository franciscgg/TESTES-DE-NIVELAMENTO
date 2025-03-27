import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {
    public static void main(String[] args) throws IOException {
        String url = "https://www.gov.br/ans/pt-br/acesso-a-informacao/participacao-da-sociedade/atualizacao-do-rol-de-procedimentos";
        String folder = "src/downloads/"; // CAMINHO DO DIRETÓRIO ONDE OS PDFs SERÃO ARMAZENADOS

        Elements links = buscarLinksDoSite(url);
        criarFolder(folder);
        baixarPDFs(links, folder);
    }

    private static Elements buscarLinksDoSite(String url) {
        Elements links = new Elements();
        try {
            // CONEXÃO COM O SITE
            Document document = Jsoup.connect(url).get();
            links = document.select("a[href$=.pdf]"); // BUSCANDO TODOS OS LINKS QUE TERMINAM COM .pdf PARA BAIXAR
            System.out.println("Conexão bem-sucedida! PDFs encontrados: "); // CONEXÃO BEM-SUCEDIDA E QUANTIDADE DE PDFs ENCONTRADOS
        } catch (IOException e) {
            System.out.println("Erro ao conectar ao site: " + e.getMessage()); // CASO O SITE ESTEJA OFF, RETORNA UMA MENSAGEM DE ERRO
        }
        return links;
    }

    private static void criarFolder(String folder) {
        try {
            // VERIFICAR SE O DIRETÓRIO JÁ EXISTE
            if (Files.exists(Paths.get(folder))) {
                System.out.println("Pasta já existente!"); // MENSAGEM CASO A PASTA JÁ EXISTA
            } else {
                Files.createDirectories(Paths.get(folder));  // CRIA O DIRETÓRIO UTILIZANDO O CAMINHO DEFINIDO NA VARIÁVEL "folder"
                System.out.println("Pasta Criada com Sucesso!"); // MENSAGEM DE SUCESSO NA CRIAÇÃO DA PASTA
            }
        } catch (IOException e) {
            System.out.println("Error ao criar folder: " + e.getMessage()); // MENSAGEM DE ERRO SE A PASTA NÃO FOR CRIADA CORRETAMENTE
        }
    }

    private static void baixarPDFs(Elements links, String folder) {
        for (Element link : links) {
            String fileUrl = link.absUrl("href");
            String nomeArquivo = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

            if (nomeArquivo.startsWith("Anexo_I") || nomeArquivo.startsWith("Anexo_II")) {  // FILTRO DOS PDFs "Anexo_I" OU "Anexo_II"
                Path filePath = Paths.get(folder + nomeArquivo); // VARIAVEL RECEBENDO O DESTINO DA PASTA ONDE O ARQUIVO ESTÁ SENDO BAIXADO
                try (InputStream in = new URL(fileUrl).openStream()) { //ABRE UM FLUXO DE ENTRADA PARA LER O CONTEUDO DO ARQUIVO
                    Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING); // COPIA O CONTEUDO PARA O CAMINHO E SUBSTITUI O ARQUIVO SE JÁ EXISTIR
                    System.out.println("Baixado: " + nomeArquivo);
                } catch (IOException e) {
                    System.out.println("Erro ao baixar " + nomeArquivo + ": " + e.getMessage());
                }
                criarZip(folder);
            }
        }
    }

    private static void criarZip(String folder) {
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(folder + "anexos.zip"))) {
            Files.list(Paths.get(folder)).forEach(file -> {
                if (file.toString().endsWith(".pdf")) { // VERIFICA SE O ARQUIVO É PDF
                    try (FileInputStream fis = new FileInputStream(file.toFile())) {
                        zipOut.putNextEntry(new ZipEntry(file.getFileName().toString()));
                        fis.transferTo(zipOut);
                        zipOut.closeEntry();
                    } catch (IOException e) {
                        System.err.println("Erro ao adicionar ao ZIP: " + file.getFileName());
                    }
                }
            });
        } catch (IOException e) {
            System.err.println("Erro ao criar ZIP: " + e.getMessage());
        }
        System.out.println("ZIP criado com sucesso!");
    }
}