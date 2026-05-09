package dev.maxrave.pipepipe.extractor.services.niconico.linkHandler;

import static dev.maxrave.pipepipe.extractor.services.niconico.NiconicoService.CHANNEL_URL;

import dev.maxrave.pipepipe.extractor.exceptions.ParsingException;
import dev.maxrave.pipepipe.extractor.linkhandler.ListLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.search.filter.FilterItem;
import dev.maxrave.pipepipe.extractor.services.niconico.NiconicoService;
import dev.maxrave.pipepipe.extractor.utils.Parser;

import java.util.List;

public class NiconicoUserLinkHandlerFactory extends ListLinkHandlerFactory {
    @Override
    public String getId(final String url) throws ParsingException {
        if(url.contains(CHANNEL_URL)){
            return url;
        }
        return NiconicoService.USER_URL + Parser.matchGroup1(NiconicoService.USER_UPLOAD_LIST, url);
    }

    @Override
    public boolean onAcceptUrl(final String url) throws ParsingException {
        return Parser.isMatch(NiconicoService.USER_UPLOAD_LIST, url) || url.contains(CHANNEL_URL);
    }

    @Override
    public String getUrl(final String id, final List<FilterItem> contentFilter,
                         final List<FilterItem> sortFilter) throws ParsingException {
        return id;
    }
}
