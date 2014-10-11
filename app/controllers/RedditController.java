package controllers;

import java.util.List;
import java.util.ArrayList;

import play.*;
import play.mvc.*;
import play.libs.ws.*;
import play.libs.F.Function;
import play.libs.F.Promise;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;

import views.html.*;

public class RedditController extends Controller {
    
    static String REDDIT_BASE_URL = "http://reddit.com/";
    
    static WSRequestHolder FRONTPAGE_HOLDER = 
        WS.url(REDDIT_BASE_URL+".json");
        
    private class FrontPageFunctionProcessor implements Function<JsonNode, Result>{
        public Result apply(JsonNode inputJson) {
            List<String> titles = new ArrayList<String>();
            
            List<JsonNode> children = inputJson.findValue("children").findValues("data");
            for (JsonNode child: children){
                titles.add(child.findValue("title").asText());
            }
            
            return ok(Json.toJson(titles));
        }
    }
    
    public Promise<Result> fetchFrontPageSubs() {
        Promise<WSResponse> responsePromise = FRONTPAGE_HOLDER.setTimeout(1000).get();
        
        final Promise<JsonNode> frontPageResponseAsJSON = responsePromise.map(
            new Function<WSResponse, JsonNode>() {
                public JsonNode apply(WSResponse response) {
                    JsonNode json = response.asJson();
                    return json;
                }
            }
        );
        // en Java 8 
        /** Promise<JsonNode> jsonPromise = WS.url(url).get().map(response -> {
            return response.asJson();
        }); */
        
        final Promise<Result> resultPromise = frontPageResponseAsJSON.map(
            new FrontPageFunctionProcessor()
        );
        
        return resultPromise;
    }
    
}