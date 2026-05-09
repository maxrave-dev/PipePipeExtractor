package dev.maxrave.pipepipe.extractor.services.media_ccc.linkHandler;

import dev.maxrave.pipepipe.extractor.search.filter.FilterItem;

import dev.maxrave.pipepipe.extractor.linkhandler.ListLinkHandlerFactory;

import java.util.List;
import java.util.regex.Pattern;

public class MediaCCCRecentListLinkHandlerFactory extends ListLinkHandlerFactory {
    private static final String PATTERN = "^(https?://)?media\\.ccc\\.de/recent/?$";

    @Override
    public String getId(final String url) {
        return "recent";
    }

    @Override
    public boolean onAcceptUrl(final String url) {
        return Pattern.matches(PATTERN, url);
    }

    @Override
    public String getUrl(final String id,
                         final List<FilterItem> contentFilter,
                         final List<FilterItem> sortFilter) {
        return "https://media.ccc.de/recent";
    }
}
