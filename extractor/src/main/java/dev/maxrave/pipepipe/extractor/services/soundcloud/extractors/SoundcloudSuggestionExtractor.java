package dev.maxrave.pipepipe.extractor.services.soundcloud.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import dev.maxrave.pipepipe.extractor.NewPipe;
import dev.maxrave.pipepipe.extractor.StreamingService;
import dev.maxrave.pipepipe.extractor.downloader.Downloader;
import dev.maxrave.pipepipe.extractor.exceptions.ExtractionException;
import dev.maxrave.pipepipe.extractor.exceptions.ParsingException;
import dev.maxrave.pipepipe.extractor.services.soundcloud.SoundcloudParsingHelper;
import dev.maxrave.pipepipe.extractor.suggestion.SuggestionExtractor;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static dev.maxrave.pipepipe.extractor.services.soundcloud.SoundcloudParsingHelper.SOUNDCLOUD_API_V2_URL;
import static dev.maxrave.pipepipe.extractor.utils.Utils.UTF_8;

public class SoundcloudSuggestionExtractor extends SuggestionExtractor {

    public SoundcloudSuggestionExtractor(final StreamingService service) {
        super(service);
    }

    @Override
    public List<String> suggestionList(final String query) throws IOException,
            ExtractionException {
        final List<String> suggestions = new ArrayList<>();
        final Downloader dl = NewPipe.getDownloader();
        final String url = SOUNDCLOUD_API_V2_URL + "search/queries" + "?q="
                + URLEncoder.encode(query, UTF_8) + "&client_id="
                + SoundcloudParsingHelper.clientId() + "&limit=10";
        final String response = dl.get(url, getExtractorLocalization()).responseBody();

        try {
            final JsonArray collection = JsonParser.object().from(response).getArray("collection");
            for (final Object suggestion : collection) {
                if (suggestion instanceof JsonObject) {
                    suggestions.add(((JsonObject) suggestion).getString("query"));
                }
            }

            return suggestions;
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse json response", e);
        }
    }
}
