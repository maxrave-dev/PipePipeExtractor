package dev.maxrave.pipepipe.extractor.services.soundcloud.linkHandler;

import dev.maxrave.pipepipe.extractor.search.filter.FilterItem;

import dev.maxrave.pipepipe.extractor.linkhandler.ListLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.utils.Parser;

import java.util.List;

public class SoundcloudChartsLinkHandlerFactory extends ListLinkHandlerFactory {
    private static final String TOP_URL_PATTERN =
            "^https?://(www\\.|m\\.)?soundcloud.com/charts(/top)?/?([#?].*)?$";
    private static final String URL_PATTERN =
            "^https?://(www\\.|m\\.)?soundcloud.com/charts(/top|/new)?/?([#?].*)?$";

    @Override
    public String getId(final String url) {
        if (Parser.isMatch(TOP_URL_PATTERN, url.toLowerCase())) {
            return "Top 50";
        } else {
            return "New & hot";
        }
    }

    @Override
    public String getUrl(final String id,
                         final List<FilterItem> contentFilter,
                         final List<FilterItem> sortFilter) {
        if (id.equals("Top 50")) {
            return "https://soundcloud.com/charts/top";
        } else {
            return "https://soundcloud.com/charts/new";
        }
    }

    @Override
    public boolean onAcceptUrl(final String url) {
        return Parser.isMatch(URL_PATTERN, url.toLowerCase());
    }
}
