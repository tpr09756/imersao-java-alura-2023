import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// import org.fusesource.jansi; need to get the jar

public class Main {




    public static void main(String[] args) throws Exception {

        //String imdbApiKey = System.getenv("IMDB_API_KEY");
        // Get the API key
        String key = "";
        try {
            Scanner scanner = new Scanner(new File("resources/key.properties"));
            key = scanner.nextLine();
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("API key was not found");
            e.printStackTrace();
        }

        // conexão http aceder api IMDB https://imdb-api.com/en/API/Top250Movies
        String url = "https://imdb-api.com/en/API/Top250Movies/" + key;
        URI address = URI.create(url);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(address).GET().build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        String body = response.body();
        

        // extrair dados que nos interessam (título, imagem, rating)
        JsonParser parser = new JsonParser();
        List<Map<String,String>> moviesList = parser.parse(body);
        
        // exibir e manipular os dados

        StickerGenerator stickerGenerator = new StickerGenerator();
        for (Map<String,String> movie : moviesList) {

            String urlImage = movie.get("image");
            String title = movie.get("title");
            InputStream inputStream = new URL(urlImage)
                    .openStream();
            String filename = title + ".png";


            stickerGenerator.generate(inputStream, filename);


            System.out.println("\u001b[1mTítulo:\u001b[m " + movie.get("title"));
            System.out.println("\u001b[1mPoster:\u001b[m " + movie.get("image"));
            System.out.println(movie.get("imDbRating"));
            Double numberStarsDouble = Double.parseDouble(movie.get("imDbRating"));
            int starNumber = numberStarsDouble.intValue();
            StringBuilder ratStar = new StringBuilder();
            for(int i = 1; i < starNumber; i++){
                ratStar.append("⭐");
            }
            while (10 - starNumber > 0){
                ratStar.append("✩");
                starNumber++;
            }
            System.out.println(ratStar.toString());
        }
    }
}