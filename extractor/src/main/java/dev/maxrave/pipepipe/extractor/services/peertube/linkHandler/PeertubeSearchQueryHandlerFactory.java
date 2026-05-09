package dev.maxrave.pipepipe.extractor.services.peertube.linkHandler;

import dev.maxrave.pipepipe.extractor.search.filter.Filter;
import dev.maxrave.pipepipe.extractor.search.filter.FilterItem;
import dev.maxrave.pipepipe.extractor.services.peertube.search.filter.PeertubeFilters;

import dev.maxrave.pipepipe.extractor.ServiceList;
import dev.maxrave.pipepipe.extractor.exceptions.ParsingException;
import dev.maxrave.pipepipe.extractor.linkhandler.SearchQueryHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.peertube.PeertubeHelpers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;

import static dev.maxrave.pipepipe.extractor.utils.Utils.UTF_8;

public final class PeertubeSearchQueryHandlerFactory extends SearchQueryHandlerFactory {

    public static final String VIDEOS = "videos";
    public static final String SEPIA_VIDEOS = "sepia_videos"; // sepia is the global index
    public static final String SEPIA_BASE_URL = "https://sepiasearch.org";
    public static final String SEARCH_ENDPOINT = "/api/v1/search/videos";

    private static PeertubeSearchQueryHandlerFactory instance = null;
    private final PeertubeFilters searchFilters = new PeertubeFilters();

    private PeertubeSearchQueryHandlerFactory() { }

    /**
     * Singleton to get the same objects of filters during search.
     *
     * The sort filter holds a variable search parameter: (filter.getQueryData())
     * @return
     */
    public static synchronized PeertubeSearchQueryHandlerFactory getInstance() {
        if (instance == null) {
            instance = new PeertubeSearchQueryHandlerFactory();
        }
        return instance;
    }

    @Override
    public String getUrl(final String searchString,
                         final List<FilterItem> selectedContentFilter,
                         final List<FilterItem> selectedSortFilters) throws ParsingException {

        final String baseUrl;
        final Optional<FilterItem> sepiaFilter =
                PeertubeHelpers.getSepiaFilter(selectedSortFilters);
        if (sepiaFilter.isPresent()) {
            baseUrl = SEPIA_BASE_URL;
        } else {
            baseUrl = ServiceList.PeerTube.getBaseUrl();
        }

        return getUrl(searchString, selectedContentFilter, selectedSortFilters, baseUrl);
    }

    @Override
    public String getUrl(final String searchString,
                         final List<FilterItem> selectedContentFilter,
                         final List<FilterItem> selectedSortFilter,
                         final String baseUrl) throws ParsingException {
        try {
            searchFilters.setSelectedSortFilter(selectedSortFilter);
            searchFilters.setSelectedContentFilter(selectedContentFilter);

            final String filterQuery = searchFilters.evaluateSelectedFilters(null);

            return baseUrl + SEARCH_ENDPOINT + "?search=" + URLEncoder.encode(searchString, UTF_8)
                    + filterQuery;
        } catch (final UnsupportedEncodingException e) {
            throw new ParsingException("Could not encode query", e);
        }
    }

    @Override
    public Filter getAvailableContentFilter() {
        return searchFilters.getContentFilters();
    }

    @Override
    public Filter getAvailableSortFilter() {
        return searchFilters.getSortFilters();
    }

    @Override
    public Filter getContentFilterSortFilterVariant(final int contentFilterId) {
        return searchFilters.getContentFilterSortFilterVariant(contentFilterId);
    }

    @Override
    public FilterItem getFilterItem(final int filterId) {
        return searchFilters.getFilterItem(filterId);
    }
}
