package dev.maxrave.pipepipe.extractor.services.media_ccc.linkHandler;

import dev.maxrave.pipepipe.extractor.search.filter.FilterItem;

import dev.maxrave.pipepipe.extractor.exceptions.ParsingException;
import dev.maxrave.pipepipe.extractor.linkhandler.ListLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.utils.Parser;

import java.util.List;

public class MediaCCCConferenceLinkHandlerFactory extends ListLinkHandlerFactory {
    public static final String CONFERENCE_API_ENDPOINT
            = "https://api.media.ccc.de/public/conferences/";
    public static final String CONFERENCE_PATH = "https://media.ccc.de/c/";
    private static final String ID_PATTERN
            = "(?:(?:(?:api\\.)?media\\.ccc\\.de/public/conferences/)"
            + "|(?:media\\.ccc\\.de/[bc]/))([^/?&#]*)";

    @Override
    public String getUrl(final String id,
                         final List<FilterItem> contentFilter,
                         final List<FilterItem> sortFilter) throws ParsingException {
        return CONFERENCE_PATH + id;
    }

    @Override
    public String getId(final String url) throws ParsingException {
        return Parser.matchGroup1(ID_PATTERN, url);
    }

    @Override
    public boolean onAcceptUrl(final String url) {
        try {
            return getId(url) != null;
        } catch (final ParsingException e) {
            return false;
        }
    }
}
