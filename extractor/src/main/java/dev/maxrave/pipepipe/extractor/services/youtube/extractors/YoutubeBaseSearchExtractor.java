package dev.maxrave.pipepipe.extractor.services.youtube.extractors;

import dev.maxrave.pipepipe.extractor.search.filter.FilterItem;

import dev.maxrave.pipepipe.extractor.StreamingService;
import dev.maxrave.pipepipe.extractor.linkhandler.SearchQueryHandler;
import dev.maxrave.pipepipe.extractor.search.SearchExtractor;

public abstract class YoutubeBaseSearchExtractor extends SearchExtractor {
    public YoutubeBaseSearchExtractor(final StreamingService service,
                                      final SearchQueryHandler linkHandler) {
        super(service, linkHandler);
    }

    @SuppressWarnings("unchecked")
    protected  <T extends FilterItem> T getSelectedContentFilterItem() {
        final FilterItem filterItem = getLinkHandler().getContentFilters().get(0);

        if (filterItem != null) {
            return (T) filterItem;
        }
        throw new RuntimeException("no content filter set");
    }
}
