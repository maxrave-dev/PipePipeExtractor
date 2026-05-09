package dev.maxrave.pipepipe.extractor.services.soundcloud.linkHandler;

import dev.maxrave.pipepipe.extractor.exceptions.ParsingException;
import dev.maxrave.pipepipe.extractor.linkhandler.ChannelTabs;
import dev.maxrave.pipepipe.extractor.linkhandler.ListLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.search.filter.FilterItem;

import java.util.List;

public final class SoundcloudChannelTabLinkHandlerFactory extends ListLinkHandlerFactory {
    private static final SoundcloudChannelTabLinkHandlerFactory INSTANCE
            = new SoundcloudChannelTabLinkHandlerFactory();

    private SoundcloudChannelTabLinkHandlerFactory() {
    }

    public static SoundcloudChannelTabLinkHandlerFactory getInstance() {
        return INSTANCE;
    }

    public static String getUrlSuffix(final String tab) throws ParsingException {
        switch (tab) {
            case ChannelTabs.TRACKS:
                return "/tracks";
            case ChannelTabs.PLAYLISTS:
                return "/sets";
            case ChannelTabs.ALBUMS:
                return "/albums";
        }
        throw new ParsingException("tab " + tab + " not supported");
    }

    @Override
    public String getId(final String url) throws ParsingException {
        return SoundcloudChannelLinkHandlerFactory.getInstance().getId(url);
    }

    @Override
    public String getUrl(final String id, final List<FilterItem> contentFilter, final List<FilterItem> sortFilter)
            throws ParsingException {
        return SoundcloudChannelLinkHandlerFactory.getInstance().getUrl(id)
                + getUrlSuffix(contentFilter.get(0).getName());
    }

    @Override
    public boolean onAcceptUrl(final String url) throws ParsingException {
        return SoundcloudChannelLinkHandlerFactory.getInstance().onAcceptUrl(url);
    }

}
