package dev.maxrave.pipepipe.extractor.services.peertube;

import dev.maxrave.pipepipe.extractor.search.filter.FilterItem;

import static dev.maxrave.pipepipe.extractor.StreamingService.ServiceInfo.MediaCapability.COMMENTS;
import static dev.maxrave.pipepipe.extractor.StreamingService.ServiceInfo.MediaCapability.VIDEO;
import static java.util.Arrays.asList;

import dev.maxrave.pipepipe.extractor.StreamingService;
import dev.maxrave.pipepipe.extractor.channel.ChannelExtractor;
import dev.maxrave.pipepipe.extractor.channel.ChannelTabExtractor;
import dev.maxrave.pipepipe.extractor.comments.CommentsExtractor;
import dev.maxrave.pipepipe.extractor.exceptions.ExtractionException;
import dev.maxrave.pipepipe.extractor.exceptions.ParsingException;
import dev.maxrave.pipepipe.extractor.kiosk.KioskList;
import dev.maxrave.pipepipe.extractor.linkhandler.ChannelTabs;
import dev.maxrave.pipepipe.extractor.linkhandler.LinkHandler;
import dev.maxrave.pipepipe.extractor.linkhandler.LinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.linkhandler.ListLinkHandler;
import dev.maxrave.pipepipe.extractor.linkhandler.ListLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.linkhandler.SearchQueryHandler;
import dev.maxrave.pipepipe.extractor.linkhandler.SearchQueryHandlerFactory;
import dev.maxrave.pipepipe.extractor.playlist.PlaylistExtractor;
import dev.maxrave.pipepipe.extractor.search.SearchExtractor;
import dev.maxrave.pipepipe.extractor.services.peertube.extractors.PeertubeAccountExtractor;
import dev.maxrave.pipepipe.extractor.services.peertube.extractors.PeertubeAccountTabExtractor;
import dev.maxrave.pipepipe.extractor.services.peertube.extractors.PeertubeChannelExtractor;
import dev.maxrave.pipepipe.extractor.services.peertube.extractors.PeertubeChannelTabExtractor;
import dev.maxrave.pipepipe.extractor.services.peertube.extractors.PeertubeCommentsExtractor;
import dev.maxrave.pipepipe.extractor.services.peertube.extractors.PeertubePlaylistExtractor;
import dev.maxrave.pipepipe.extractor.services.peertube.extractors.PeertubeSearchExtractor;
import dev.maxrave.pipepipe.extractor.services.peertube.extractors.PeertubeStreamExtractor;
import dev.maxrave.pipepipe.extractor.services.peertube.extractors.PeertubeSuggestionExtractor;
import dev.maxrave.pipepipe.extractor.services.peertube.extractors.PeertubeTrendingExtractor;
import dev.maxrave.pipepipe.extractor.services.peertube.linkHandler.PeertubeChannelLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.peertube.linkHandler.PeertubeChannelTabLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.peertube.linkHandler.PeertubeCommentsLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.peertube.linkHandler.PeertubePlaylistLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.peertube.linkHandler.PeertubeSearchQueryHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.peertube.linkHandler.PeertubeStreamLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.peertube.linkHandler.PeertubeTrendingLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.stream.StreamExtractor;
import dev.maxrave.pipepipe.extractor.subscription.SubscriptionExtractor;
import dev.maxrave.pipepipe.extractor.suggestion.SuggestionExtractor;

import java.util.List;
import java.util.Optional;

public class PeertubeService extends StreamingService {

    private PeertubeInstance instance;

    public PeertubeService(final int id) {
        this(id, PeertubeInstance.DEFAULT_INSTANCE);
    }

    public PeertubeService(final int id, final PeertubeInstance instance) {
        super(id, "PeerTube", asList(VIDEO, COMMENTS));
        this.instance = instance;
    }

    @Override
    public LinkHandlerFactory getStreamLHFactory() {
        return PeertubeStreamLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getChannelLHFactory() {
        return PeertubeChannelLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getChannelTabLHFactory() {
        return PeertubeChannelTabLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getPlaylistLHFactory() {
        return PeertubePlaylistLinkHandlerFactory.getInstance();
    }

    @Override
    public SearchQueryHandlerFactory getSearchQHFactory() {
        return PeertubeSearchQueryHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getCommentsLHFactory() {
        return PeertubeCommentsLinkHandlerFactory.getInstance();
    }

    @Override
    public SearchExtractor getSearchExtractor(final SearchQueryHandler queryHandler) {
        final List<FilterItem> selectedSortFilter = queryHandler.getSortFilter();

        final Optional<FilterItem> sepiaFilter = PeertubeHelpers.getSepiaFilter(selectedSortFilter);
        return new PeertubeSearchExtractor(this, queryHandler, sepiaFilter.isPresent());
    }

    @Override
    public SuggestionExtractor getSuggestionExtractor() {
        return new PeertubeSuggestionExtractor(this);
    }

    @Override
    public SubscriptionExtractor getSubscriptionExtractor() {
        return null;
    }

    @Override
    public ChannelExtractor getChannelExtractor(final ListLinkHandler linkHandler)
            throws ExtractionException {

        if (linkHandler.getUrl().contains("/video-channels/")) {
            return new PeertubeChannelExtractor(this, linkHandler);
        } else {
            return new PeertubeAccountExtractor(this, linkHandler);
        }
    }

    @Override
    public ChannelTabExtractor getChannelTabExtractor(final ListLinkHandler linkHandler)
            throws ExtractionException {
        final String tab = linkHandler.getContentFilters().get(0).getName();
        switch (tab) {
            case ChannelTabs.CHANNELS:
                return new PeertubeAccountTabExtractor(this, linkHandler);
            case ChannelTabs.PLAYLISTS:
                return new PeertubeChannelTabExtractor(this, linkHandler);
        }
        throw new ParsingException("tab " + tab + " not supported");
    }

    @Override
    public PlaylistExtractor getPlaylistExtractor(final ListLinkHandler linkHandler)
            throws ExtractionException {
        return new PeertubePlaylistExtractor(this, linkHandler);
    }

    @Override
    public StreamExtractor getStreamExtractor(final LinkHandler linkHandler)
            throws ExtractionException {
        return new PeertubeStreamExtractor(this, linkHandler);
    }

    @Override
    public CommentsExtractor getCommentsExtractor(final ListLinkHandler linkHandler)
            throws ExtractionException {
        return new PeertubeCommentsExtractor(this, linkHandler);
    }

    @Override
    public String getBaseUrl() {
        return instance.getUrl();
    }

    public PeertubeInstance getInstance() {
        return this.instance;
    }

    public void setInstance(final PeertubeInstance instance) {
        this.instance = instance;
    }

    @Override
    public KioskList getKioskList() throws ExtractionException {
        final KioskList.KioskExtractorFactory kioskFactory = (streamingService, url, id) ->
                new PeertubeTrendingExtractor(
                        PeertubeService.this,
                        new PeertubeTrendingLinkHandlerFactory().fromId(id),
                        id
                );

        final KioskList list = new KioskList(this);

        // add kiosks here e.g.:
        final PeertubeTrendingLinkHandlerFactory h = new PeertubeTrendingLinkHandlerFactory();
        try {
            list.addKioskEntry(kioskFactory, h, PeertubeTrendingLinkHandlerFactory.KIOSK_TRENDING);
            list.addKioskEntry(kioskFactory, h,
                    PeertubeTrendingLinkHandlerFactory.KIOSK_MOST_LIKED);
            list.addKioskEntry(kioskFactory, h, PeertubeTrendingLinkHandlerFactory.KIOSK_RECENT);
            list.addKioskEntry(kioskFactory, h, PeertubeTrendingLinkHandlerFactory.KIOSK_LOCAL);
            list.setDefaultKiosk(PeertubeTrendingLinkHandlerFactory.KIOSK_TRENDING);
        } catch (final Exception e) {
            throw new ExtractionException(e);
        }

        return list;
    }


}
