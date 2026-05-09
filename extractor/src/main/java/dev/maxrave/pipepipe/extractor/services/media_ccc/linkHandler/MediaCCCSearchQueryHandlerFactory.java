package dev.maxrave.pipepipe.extractor.services.media_ccc.linkHandler;

import dev.maxrave.pipepipe.extractor.search.filter.Filter;
import dev.maxrave.pipepipe.extractor.search.filter.FilterItem;

import dev.maxrave.pipepipe.extractor.exceptions.ParsingException;
import dev.maxrave.pipepipe.extractor.linkhandler.SearchQueryHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.media_ccc.search.filter.MediaCCCFilters;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static dev.maxrave.pipepipe.extractor.utils.Utils.UTF_8;

public class MediaCCCSearchQueryHandlerFactory extends SearchQueryHandlerFactory {

    public final MediaCCCFilters searchFilters = new MediaCCCFilters();

    @Override
    public Filter getAvailableContentFilter() {
        return searchFilters.getContentFilters();
    }

    @Override
    public FilterItem getFilterItem(final int filterId) {
        return searchFilters.getFilterItem(filterId);
    }

    @Override
    public String getUrl(final String query, final List<FilterItem> contentFilter,
                         final List<FilterItem> sortFilter) throws ParsingException {
        try {
            return "https://media.ccc.de/public/events/search?q="
                    + URLEncoder.encode(query, UTF_8);
        } catch (final UnsupportedEncodingException e) {
            throw new ParsingException("Could not create search string with query: " + query, e);
        }
    }
}
