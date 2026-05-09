package dev.maxrave.pipepipe.extractor.services.youtube.linkHandler;

import dev.maxrave.pipepipe.extractor.exceptions.ParsingException;
import dev.maxrave.pipepipe.extractor.linkhandler.ChannelTabs;
import dev.maxrave.pipepipe.extractor.linkhandler.ListLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.search.filter.FilterItem;
import dev.maxrave.pipepipe.extractor.services.youtube.search.filter.YoutubeFilters;

import java.util.List;

import javax.annotation.Nonnull;

public final class YoutubeChannelTabLinkHandlerFactory extends ListLinkHandlerFactory {
    private static final YoutubeChannelTabLinkHandlerFactory INSTANCE =
            new YoutubeChannelTabLinkHandlerFactory();
    private final YoutubeFilters searchFilters = new YoutubeFilters();

    private YoutubeChannelTabLinkHandlerFactory() {
    }

    public static YoutubeChannelTabLinkHandlerFactory getInstance() {
        return INSTANCE;
    }

    public static String getUrlSuffix(final String tab) throws ParsingException {
        switch (tab) {
            case ChannelTabs.VIDEOS:
                return "/videos";
            case ChannelTabs.PLAYLISTS:
                return "/playlists";
            case ChannelTabs.PODCASTS:
                return "/podcasts";
            case ChannelTabs.LIVESTREAMS:
                return "/streams";
            case ChannelTabs.SHORTS:
                return "/shorts";
            case ChannelTabs.CHANNELS:
                return "/channels";
        }
        throw new ParsingException("tab " + tab + " not supported");
    }

    @Override
    public String getUrl(final String id,@Nonnull final List<FilterItem> selectedContentFilter,
                         final List<FilterItem> selectedSortFilter)
            throws ParsingException {
        return "https://www.youtube.com/" + id + getUrlSuffix(selectedContentFilter.get(0).getName());
    }

    @Override
    public String getId(final String url) throws ParsingException {
        return YoutubeChannelLinkHandlerFactory.getInstance().getId(url);
    }

    @Override
    public boolean onAcceptUrl(final String url) throws ParsingException {
        try {
            getId(url);
        } catch (final ParsingException e) {
            return false;
        }
        return true;
    }


}
