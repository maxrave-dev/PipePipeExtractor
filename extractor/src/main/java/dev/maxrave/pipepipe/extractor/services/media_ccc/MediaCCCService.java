package dev.maxrave.pipepipe.extractor.services.media_ccc;

import dev.maxrave.pipepipe.extractor.StreamingService;
import dev.maxrave.pipepipe.extractor.channel.ChannelExtractor;
import dev.maxrave.pipepipe.extractor.channel.ChannelTabExtractor;
import dev.maxrave.pipepipe.extractor.comments.CommentsExtractor;
import dev.maxrave.pipepipe.extractor.exceptions.ExtractionException;
import dev.maxrave.pipepipe.extractor.kiosk.KioskList;
import dev.maxrave.pipepipe.extractor.linkhandler.LinkHandler;
import dev.maxrave.pipepipe.extractor.linkhandler.LinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.linkhandler.ListLinkHandler;
import dev.maxrave.pipepipe.extractor.linkhandler.ListLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.linkhandler.SearchQueryHandler;
import dev.maxrave.pipepipe.extractor.linkhandler.SearchQueryHandlerFactory;
import dev.maxrave.pipepipe.extractor.playlist.PlaylistExtractor;
import dev.maxrave.pipepipe.extractor.search.SearchExtractor;
import dev.maxrave.pipepipe.extractor.services.media_ccc.extractors.MediaCCCConferenceExtractor;
import dev.maxrave.pipepipe.extractor.services.media_ccc.extractors.MediaCCCConferenceKiosk;
import dev.maxrave.pipepipe.extractor.services.media_ccc.extractors.MediaCCCLiveStreamExtractor;
import dev.maxrave.pipepipe.extractor.services.media_ccc.extractors.MediaCCCLiveStreamKiosk;
import dev.maxrave.pipepipe.extractor.services.media_ccc.extractors.MediaCCCParsingHelper;
import dev.maxrave.pipepipe.extractor.services.media_ccc.extractors.MediaCCCRecentKiosk;
import dev.maxrave.pipepipe.extractor.services.media_ccc.extractors.MediaCCCSearchExtractor;
import dev.maxrave.pipepipe.extractor.services.media_ccc.extractors.MediaCCCStreamExtractor;
import dev.maxrave.pipepipe.extractor.services.media_ccc.linkHandler.MediaCCCConferenceLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.media_ccc.linkHandler.MediaCCCConferencesListLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.media_ccc.linkHandler.MediaCCCLiveListLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.media_ccc.linkHandler.MediaCCCRecentListLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.media_ccc.linkHandler.MediaCCCSearchQueryHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.media_ccc.linkHandler.MediaCCCStreamLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.stream.StreamExtractor;
import dev.maxrave.pipepipe.extractor.subscription.SubscriptionExtractor;
import dev.maxrave.pipepipe.extractor.suggestion.SuggestionExtractor;

import static java.util.Arrays.asList;
import static dev.maxrave.pipepipe.extractor.StreamingService.ServiceInfo.MediaCapability.AUDIO;
import static dev.maxrave.pipepipe.extractor.StreamingService.ServiceInfo.MediaCapability.VIDEO;

public class MediaCCCService extends StreamingService {
    public MediaCCCService(final int id) {
        super(id, "media.ccc.de", asList(AUDIO, VIDEO));
    }

    @Override
    public SearchExtractor getSearchExtractor(final SearchQueryHandler query) {
        return new MediaCCCSearchExtractor(this, query);
    }

    @Override
    public LinkHandlerFactory getStreamLHFactory() {
        return new MediaCCCStreamLinkHandlerFactory();
    }

    @Override
    public ListLinkHandlerFactory getChannelLHFactory() {
        return new MediaCCCConferenceLinkHandlerFactory();
    }

    @Override
    public ListLinkHandlerFactory getChannelTabLHFactory() {
        return null;
    }

    @Override
    public ListLinkHandlerFactory getPlaylistLHFactory() {
        return null;
    }

    @Override
    public SearchQueryHandlerFactory getSearchQHFactory() {
        return new MediaCCCSearchQueryHandlerFactory();
    }

    @Override
    public StreamExtractor getStreamExtractor(final LinkHandler linkHandler) {
        if (MediaCCCParsingHelper.isLiveStreamId(linkHandler.getId())) {
            return new MediaCCCLiveStreamExtractor(this, linkHandler);
        }
        return new MediaCCCStreamExtractor(this, linkHandler);
    }

    @Override
    public ChannelExtractor getChannelExtractor(final ListLinkHandler linkHandler) {
        return new MediaCCCConferenceExtractor(this, linkHandler);
    }

    @Override
    public ChannelTabExtractor getChannelTabExtractor(final ListLinkHandler linkHandler)
            throws ExtractionException {
        return null;
    }

    @Override
    public PlaylistExtractor getPlaylistExtractor(final ListLinkHandler linkHandler) {
        return null;
    }

    @Override
    public SuggestionExtractor getSuggestionExtractor() {
        return null;
    }

    @Override
    public KioskList getKioskList() throws ExtractionException {
        final KioskList list = new KioskList(this);

        // add kiosks here e.g.:
        try {
            list.addKioskEntry(
                    (streamingService, url, kioskId) -> new MediaCCCConferenceKiosk(
                            MediaCCCService.this,
                            new MediaCCCConferencesListLinkHandlerFactory().fromUrl(url),
                            kioskId
                    ),
                    new MediaCCCConferencesListLinkHandlerFactory(),
                    "conferences"
            );

            list.addKioskEntry(
                    (streamingService, url, kioskId) -> new MediaCCCRecentKiosk(
                            MediaCCCService.this,
                            new MediaCCCRecentListLinkHandlerFactory().fromUrl(url),
                            kioskId
                    ),
                    new MediaCCCRecentListLinkHandlerFactory(),
                    "recent"
            );

            list.addKioskEntry(
                    (streamingService, url, kioskId) -> new MediaCCCLiveStreamKiosk(
                            MediaCCCService.this,
                            new MediaCCCLiveListLinkHandlerFactory().fromUrl(url),
                            kioskId
                    ),
                    new MediaCCCLiveListLinkHandlerFactory(),
                    "live"
            );

            list.setDefaultKiosk("recent");
        } catch (final Exception e) {
            throw new ExtractionException(e);
        }

        return list;
    }

    @Override
    public SubscriptionExtractor getSubscriptionExtractor() {
        return null;
    }

    @Override
    public ListLinkHandlerFactory getCommentsLHFactory() {
        return null;
    }

    @Override
    public CommentsExtractor getCommentsExtractor(final ListLinkHandler linkHandler) {
        return null;
    }

    @Override
    public String getBaseUrl() {
        return "https://media.ccc.de";
    }

}
