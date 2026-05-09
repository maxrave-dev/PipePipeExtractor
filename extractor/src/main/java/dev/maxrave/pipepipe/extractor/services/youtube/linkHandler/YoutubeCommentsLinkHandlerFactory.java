package dev.maxrave.pipepipe.extractor.services.youtube.linkHandler;

import dev.maxrave.pipepipe.extractor.search.filter.FilterItem;

import dev.maxrave.pipepipe.extractor.exceptions.FoundAdException;
import dev.maxrave.pipepipe.extractor.exceptions.ParsingException;
import dev.maxrave.pipepipe.extractor.linkhandler.ListLinkHandlerFactory;

import java.util.List;

public final class YoutubeCommentsLinkHandlerFactory extends ListLinkHandlerFactory {

    private static final YoutubeCommentsLinkHandlerFactory INSTANCE
            = new YoutubeCommentsLinkHandlerFactory();

    private YoutubeCommentsLinkHandlerFactory() {
    }

    public static YoutubeCommentsLinkHandlerFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public String getUrl(final String id) {
        return "https://www.youtube.com/watch?v=" + id;
    }

    @Override
    public String getId(final String urlString) throws ParsingException, IllegalArgumentException {
        // we need the same id, avoids duplicate code
        return YoutubeStreamLinkHandlerFactory.getInstance().getId(urlString);
    }

    @Override
    public boolean onAcceptUrl(final String url) throws FoundAdException {
        try {
            getId(url);
            return true;
        } catch (final FoundAdException fe) {
            throw fe;
        } catch (final ParsingException e) {
            return false;
        }
    }

    @Override
    public String getUrl(final String id,
                         final List<FilterItem> contentFilter,
                         final List<FilterItem> sortFilter) throws ParsingException {
        return getUrl(id);
    }
}
