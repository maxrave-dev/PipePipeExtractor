package dev.maxrave.pipepipe.extractor.services.niconico.linkHandler;

import dev.maxrave.pipepipe.extractor.exceptions.ParsingException;
import dev.maxrave.pipepipe.extractor.linkhandler.ListLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.search.filter.FilterItem;
import dev.maxrave.pipepipe.extractor.services.niconico.NiconicoService;

import java.util.List;

public class NiconicoTrendLinkHandlerFactory extends ListLinkHandlerFactory {
    @Override
    public String getId(final String url) throws ParsingException {
        switch (url){
            case NiconicoService.DAILY_TREND_URL:
            default:
                return "Trending";
            case NiconicoService.RECOMMEND_LIVES_URL:
                return "Recommended Lives";
            case NiconicoService.TOP_LIVES_URL:
                return "Top Lives";
        }
    }

    @Override
    public boolean onAcceptUrl(final String url) throws ParsingException {
        return NiconicoService.DAILY_TREND_URL.equals(url)
                || NiconicoService.RECOMMEND_LIVES_URL.equals(url)
                || NiconicoService.TOP_LIVES_URL.equals(url);
    }

    @Override
    public String getUrl(final String id, final List<FilterItem> contentFilter,
                         final List<FilterItem> sortFilter) throws ParsingException {
        switch (id){
            case "Trending":
            default:
                return NiconicoService.DAILY_TREND_URL;
            case "Recommended Lives":
                return NiconicoService.RECOMMEND_LIVES_URL;
            case "Top Lives":
                return NiconicoService.TOP_LIVES_URL;
        }
    }
}
