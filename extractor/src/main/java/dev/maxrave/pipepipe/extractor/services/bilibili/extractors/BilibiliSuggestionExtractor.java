package dev.maxrave.pipepipe.extractor.services.bilibili.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import dev.maxrave.pipepipe.extractor.NewPipe;
import dev.maxrave.pipepipe.extractor.StreamingService;
import dev.maxrave.pipepipe.extractor.exceptions.ExtractionException;
import dev.maxrave.pipepipe.extractor.suggestion.SuggestionExtractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static dev.maxrave.pipepipe.extractor.services.bilibili.BilibiliService.GET_SUGGESTION_URL;
import static dev.maxrave.pipepipe.extractor.services.bilibili.BilibiliService.WWW_REFERER;
import static dev.maxrave.pipepipe.extractor.services.bilibili.BilibiliService.getHeaders;

public class BilibiliSuggestionExtractor extends SuggestionExtractor {
    public BilibiliSuggestionExtractor(StreamingService service) {
        super(service);
    }

    @Override
    public List<String> suggestionList(String query) throws IOException, ExtractionException {
        final String response = NewPipe.getDownloader().get(GET_SUGGESTION_URL + query, getHeaders(WWW_REFERER)).responseBody();
        List<String> resultList = new ArrayList<>();
        try {
            JsonArray respObject = JsonParser.object().from(response).getObject("result").getArray("tag");
            for(int i = 0; i < respObject.size(); i++){
                resultList.add(respObject.getObject(i).getString("value"));
            }
        } catch (JsonParserException e) {
            e.printStackTrace();
        }
        return resultList;
    }
}
