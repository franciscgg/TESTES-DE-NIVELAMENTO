import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.*;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) throws IOException {

        try {
            // CONEXÃO COM O SITE
            Document URL = Jsoup.connect("https://www.gov.br/ans/pt-br/acesso-a-informacao/participacao-da-sociedade/atualizacao-do-rol-de-procedimentos").get();
            Elements links = URL.select("a[href$=.pdf]"); // BUSCANDO TODOS OS LINKS QUE TERMINAM COM .pdf PARA BAIXAR
            System.out.println("Conexão bem-sucedida! PDFs encontrados: " + links.size()); // CONEXÃO BEM-SUCEDIDA E QUANTIDADE DE PDFs ENCONTRADOS
        } catch (IOException e) {
            System.out.println("Erro ao conectar ao site: " + e.getMessage()); // CASO O SITE ESTEJA OFF, RETORNA UMA MENSAGEM DE ERRO
        }

        // CRIAR PASTA
        String folder = "src/downloads/"; // CAMINHO DO DIRETÓRIO ONDE OS PDFs SERÃO ARMAZENADOS
        try {
            // VERIFICAR SE O DIRETÓRIO JÁ EXISTE
            if (Files.exists(Paths.get(folder))) {
                System.out.println("Pasta já existente!"); // MENSAGEM CASO A PASTA JÁ EXISTA
            }else {
                Files.createDirectories(Paths.get(folder));  // CRIA O DIRETÓRIO UTILIZANDO O CAMINHO DEFINIDO NA VARIÁVEL "folder"
                System.out.println("Pasta Criada com Sucesso!"); // MENSAGEM DE SUCESSO NA CRIAÇÃO DA PASTA
            }
        } catch (IOException e) {
            System.out.println("Error ao criar folder: " + e.getMessage()); // MENSAGEM DE ERRO SE A PASTA NÃO FOR CRIADA CORRETAMENTE
        }

            }
        }



