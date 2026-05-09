package dev.maxrave.pipepipe.extractor.services.bilibili.linkHandler;

import dev.maxrave.pipepipe.extractor.exceptions.ParsingException;
import dev.maxrave.pipepipe.extractor.linkhandler.ListLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.search.filter.FilterItem;

import java.util.List;
import java.util.regex.Pattern;

import static dev.maxrave.pipepipe.extractor.services.bilibili.BilibiliService.*;

public class BilibiliPlaylistLinkHandlerFactory extends ListLinkHandlerFactory {
    @Override
    public String getId(String url) throws ParsingException {
        return url;
    }

    @Override
    public boolean onAcceptUrl(String url) throws ParsingException {
        return url.contains(GET_SEASON_ARCHIVES_ARCHIVE_BASE_URL) ||
                url.contains(GET_SERIES_BASE_URL) ||
                url.contains(GET_PARTITION_URL);
    }

    @Override
    public String getUrl(String id, List<FilterItem> contentFilter, List<FilterItem> sortFilter) throws ParsingException {
        return id;
    }
}
