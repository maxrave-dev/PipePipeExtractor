package dev.maxrave.pipepipe.extractor.services.niconico.linkHandler;

import static dev.maxrave.pipepipe.extractor.services.niconico.NiconicoService.MYLIST_PAGE_URL;

import dev.maxrave.pipepipe.extractor.exceptions.ParsingException;
import dev.maxrave.pipepipe.extractor.linkhandler.ListLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.search.filter.FilterItem;

import java.util.List;
import java.util.regex.Pattern;

public class NiconicoPlaylistLinkHandlerFactory extends ListLinkHandlerFactory {
    @Override
    public String getId(String url) throws ParsingException {
        return url;
    }

    @Override
    public String getUrl(String id, List<FilterItem> contentFilter, List<FilterItem> sortFilter) throws ParsingException {
        return id;
    }

    @Override
    public boolean onAcceptUrl(String url) throws ParsingException {
        return url.contains("/mylist/") || url.contains("/series/");
    }
}
