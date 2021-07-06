package movieselect.app;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

@Controller
public class MovieSelectController {
	
	/*Jsonをパースする*/
	public JsonNode JsonPerser(String url) throws JsonMappingException, JsonProcessingException {
	String Json = getResult.getJsonResult(url);
	//mapperをインスタンス化することで、JSONデータの文字列をJSONデータにパースする準備ができる
	ObjectMapper mapper = new ObjectMapper();
	//responseに取得したJSONデータの文字列をJSONデータにパースしたデータを格納している（JsonNodeという型は固定らしい）
	JsonNode response = mapper.readTree(Json);
	return response;
	}
//	http://www.omdbapi.com/?s=tes*&y=2020&apikey=f4ac8509 ←これで複数件数取れた
//	http://www.omdbapi.com/?i=tt3896198&apikey=f4ac8509
    @GetMapping("/movie")
    public String moviename(Model model) throws JsonMappingException, JsonProcessingException, UnsupportedEncodingException {
    	/*複数件数取得する(複数件数取得すると、映画の詳細は取得されない)*/
    	String randWord = "call";
    	String year = "2020";
    	Random rand = new Random();
    	randWord = URLEncoder.encode(randWord, "UTF-8");
    	JsonNode APIData = JsonPerser("http://www.omdbapi.com/?type=movie&s="+randWord+"&y="+year+"&apikey=f4ac8509");
    	int jsonLength = APIData.get("Search").size();
    	int resultNum = rand.nextInt(jsonLength);
    	String title = APIData.get("Search").get(resultNum).get("Title").textValue();
    	title = URLEncoder.encode(title, "UTF-8");

        
        /* 映画の詳細を取得するため、取得したタイトル指定で再検索 */
        JsonNode APIDataDetails = JsonPerser("http://www.omdbapi.com/?t="+title+"&apikey=f4ac8509");
        model.addAttribute("title", APIDataDetails.get("Title").textValue());
        model.addAttribute("poster", APIDataDetails.get("Poster").textValue());
        model.addAttribute("genre", APIDataDetails.get("Genre").textValue());
        model.addAttribute("director", APIDataDetails.get("Director").textValue());
        
        return "movie";
    }
}
