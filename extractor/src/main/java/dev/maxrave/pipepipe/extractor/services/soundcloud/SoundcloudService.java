package dev.maxrave.pipepipe.extractor.services.soundcloud;

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
import dev.maxrave.pipepipe.extractor.localization.ContentCountry;
import dev.maxrave.pipepipe.extractor.playlist.PlaylistExtractor;
import dev.maxrave.pipepipe.extractor.search.SearchExtractor;
import dev.maxrave.pipepipe.extractor.services.soundcloud.extractors.SoundcloudChannelExtractor;
import dev.maxrave.pipepipe.extractor.services.soundcloud.extractors.SoundcloudChannelTabExtractor;
import dev.maxrave.pipepipe.extractor.services.soundcloud.extractors.SoundcloudChartsExtractor;
import dev.maxrave.pipepipe.extractor.services.soundcloud.extractors.SoundcloudCommentsExtractor;
import dev.maxrave.pipepipe.extractor.services.soundcloud.extractors.SoundcloudPlaylistExtractor;
import dev.maxrave.pipepipe.extractor.services.soundcloud.extractors.SoundcloudSearchExtractor;
import dev.maxrave.pipepipe.extractor.services.soundcloud.extractors.SoundcloudStreamExtractor;
import dev.maxrave.pipepipe.extractor.services.soundcloud.extractors.SoundcloudSubscriptionExtractor;
import dev.maxrave.pipepipe.extractor.services.soundcloud.extractors.SoundcloudSuggestionExtractor;
import dev.maxrave.pipepipe.extractor.services.soundcloud.linkHandler.SoundcloudChannelLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.soundcloud.linkHandler.SoundcloudChannelTabLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.soundcloud.linkHandler.SoundcloudChartsLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.soundcloud.linkHandler.SoundcloudCommentsLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.soundcloud.linkHandler.SoundcloudPlaylistLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.soundcloud.linkHandler.SoundcloudSearchQueryHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.soundcloud.linkHandler.SoundcloudStreamLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.stream.StreamExtractor;
import dev.maxrave.pipepipe.extractor.subscription.SubscriptionExtractor;

import java.util.List;

import static java.util.Arrays.asList;
import static dev.maxrave.pipepipe.extractor.StreamingService.ServiceInfo.MediaCapability.AUDIO;
import static dev.maxrave.pipepipe.extractor.StreamingService.ServiceInfo.MediaCapability.COMMENTS;

public class SoundcloudService extends StreamingService {

    public SoundcloudService(final int id) {
        super(id, "SoundCloud", asList(AUDIO, COMMENTS));
    }

    @Override
    public String getBaseUrl() {
        return "https://soundcloud.com";
    }

    @Override
    public SearchQueryHandlerFactory getSearchQHFactory() {
        return SoundcloudSearchQueryHandlerFactory.getInstance();
    }

    @Override
    public LinkHandlerFactory getStreamLHFactory() {
        return SoundcloudStreamLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getChannelLHFactory() {
        return SoundcloudChannelLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getChannelTabLHFactory() {
        return SoundcloudChannelTabLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getPlaylistLHFactory() {
        return SoundcloudPlaylistLinkHandlerFactory.getInstance();
    }

    @Override
    public List<ContentCountry> getSupportedCountries() {
        // Country selector here: https://soundcloud.com/charts/top?genre=all-music
        return ContentCountry.listFrom(
                "AU", "CA", "DE", "FR", "GB", "IE", "NL", "NZ", "US"
        );
    }

    @Override
    public StreamExtractor getStreamExtractor(final LinkHandler linkHandler) {
        return new SoundcloudStreamExtractor(this, linkHandler);
    }

    @Override
    public ChannelExtractor getChannelExtractor(final ListLinkHandler linkHandler) {
        return new SoundcloudChannelExtractor(this, linkHandler);
    }

    @Override
    public ChannelTabExtractor getChannelTabExtractor(final ListLinkHandler linkHandler) {
        return new SoundcloudChannelTabExtractor(this, linkHandler);
    }

    @Override
    public PlaylistExtractor getPlaylistExtractor(final ListLinkHandler linkHandler) {
        return new SoundcloudPlaylistExtractor(this, linkHandler);
    }

    @Override
    public SearchExtractor getSearchExtractor(final SearchQueryHandler queryHandler) {
        return new SoundcloudSearchExtractor(this, queryHandler);
    }

    @Override
    public SoundcloudSuggestionExtractor getSuggestionExtractor() {
        return new SoundcloudSuggestionExtractor(this);
    }
    @Override
    public KioskList getKioskList() throws ExtractionException {
        final KioskList.KioskExtractorFactory chartsFactory = (streamingService, url, id) ->
                new SoundcloudChartsExtractor(SoundcloudService.this,
                        new SoundcloudChartsLinkHandlerFactory().fromUrl(url), id);

        final KioskList list = new KioskList(this);

        // add kiosks here e.g.:
        final SoundcloudChartsLinkHandlerFactory h = new SoundcloudChartsLinkHandlerFactory();
        try {
            list.addKioskEntry(chartsFactory, h, "Top 50");
            list.addKioskEntry(chartsFactory, h, "New & hot");
            list.setDefaultKiosk("New & hot");
        } catch (final Exception e) {
            throw new ExtractionException(e);
        }

        return list;
    }

    @Override
    public SubscriptionExtractor getSubscriptionExtractor() {
        return new SoundcloudSubscriptionExtractor(this);
    }

    @Override
    public ListLinkHandlerFactory getCommentsLHFactory() {
        return SoundcloudCommentsLinkHandlerFactory.getInstance();
    }

    @Override
    public CommentsExtractor getCommentsExtractor(final ListLinkHandler linkHandler)
            throws ExtractionException {
        return new SoundcloudCommentsExtractor(this, linkHandler);
    }
}
