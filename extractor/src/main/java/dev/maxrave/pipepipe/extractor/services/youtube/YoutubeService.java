package dev.maxrave.pipepipe.extractor.services.youtube;

import dev.maxrave.pipepipe.extractor.NewPipe;
import dev.maxrave.pipepipe.extractor.bulletComments.BulletCommentsExtractor;
import dev.maxrave.pipepipe.extractor.search.filter.FilterItem;
import dev.maxrave.pipepipe.extractor.services.youtube.extractors.YoutubeBulletCommentsExtractor;
import dev.maxrave.pipepipe.extractor.services.youtube.linkHandler.YoutubeBulletCommentsLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.youtube.search.filter.YoutubeFilters;

import static java.util.Arrays.asList;
import static dev.maxrave.pipepipe.extractor.StreamingService.ServiceInfo.MediaCapability.*;

import dev.maxrave.pipepipe.extractor.StreamingService;
import dev.maxrave.pipepipe.extractor.channel.ChannelExtractor;
import dev.maxrave.pipepipe.extractor.channel.ChannelTabExtractor;
import dev.maxrave.pipepipe.extractor.comments.CommentsExtractor;
import dev.maxrave.pipepipe.extractor.exceptions.ExtractionException;
import dev.maxrave.pipepipe.extractor.feed.FeedExtractor;
import dev.maxrave.pipepipe.extractor.kiosk.KioskList;
import dev.maxrave.pipepipe.extractor.linkhandler.LinkHandler;
import dev.maxrave.pipepipe.extractor.linkhandler.LinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.linkhandler.ListLinkHandler;
import dev.maxrave.pipepipe.extractor.linkhandler.ListLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.linkhandler.SearchQueryHandler;
import dev.maxrave.pipepipe.extractor.linkhandler.SearchQueryHandlerFactory;
import dev.maxrave.pipepipe.extractor.localization.ContentCountry;
import dev.maxrave.pipepipe.extractor.localization.Localization;
import dev.maxrave.pipepipe.extractor.playlist.PlaylistExtractor;
import dev.maxrave.pipepipe.extractor.search.SearchExtractor;
import dev.maxrave.pipepipe.extractor.services.youtube.extractors.YoutubeChannelExtractor;
import dev.maxrave.pipepipe.extractor.services.youtube.extractors.YoutubeChannelTabExtractor;
import dev.maxrave.pipepipe.extractor.services.youtube.extractors.YoutubeCommentsExtractor;
import dev.maxrave.pipepipe.extractor.services.youtube.extractors.YoutubeFeedExtractor;
import dev.maxrave.pipepipe.extractor.services.youtube.extractors.YoutubeMixPlaylistExtractor;
import dev.maxrave.pipepipe.extractor.services.youtube.extractors.YoutubeMusicSearchExtractor;
import dev.maxrave.pipepipe.extractor.services.youtube.extractors.YoutubePlaylistExtractor;
import dev.maxrave.pipepipe.extractor.services.youtube.extractors.YoutubeSearchExtractor;
import dev.maxrave.pipepipe.extractor.services.youtube.extractors.YoutubeStreamExtractor;
import dev.maxrave.pipepipe.extractor.services.youtube.extractors.YoutubeSubscriptionExtractor;
import dev.maxrave.pipepipe.extractor.services.youtube.extractors.YoutubeSuggestionExtractor;
import dev.maxrave.pipepipe.extractor.services.youtube.extractors.YoutubeTrendingExtractor;
import dev.maxrave.pipepipe.extractor.services.youtube.linkHandler.YoutubeChannelLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.youtube.linkHandler.YoutubeChannelTabLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.youtube.linkHandler.YoutubeCommentsLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.youtube.linkHandler.YoutubePlaylistLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.youtube.linkHandler.YoutubeSearchQueryHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.youtube.linkHandler.YoutubeStreamLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.youtube.linkHandler.YoutubeTrendingLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.stream.StreamExtractor;
import dev.maxrave.pipepipe.extractor.stream.StreamType;
import dev.maxrave.pipepipe.extractor.subscription.SubscriptionExtractor;
import dev.maxrave.pipepipe.extractor.suggestion.SuggestionExtractor;

import javax.annotation.Nonnull;
import java.util.List;

/*
 * Created by Christian Schabesberger on 23.08.15.
 *
 * Copyright (C) Christian Schabesberger 2018 <chris.schabesberger@mailbox.org>
 * YoutubeService.java is part of NewPipe.
 *
 * NewPipe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NewPipe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NewPipe.  If not, see <http://www.gnu.org/licenses/>.
 */

public class YoutubeService extends StreamingService {
    public WatchDataCache watchDataCache = new WatchDataCache();

    public YoutubeService(final int id) {
        super(id, "YouTube", asList(AUDIO, VIDEO, LIVE, COMMENTS, BULLET_COMMENTS, SPONSORBLOCK));
    }

    @Override
    public String getBaseUrl() {
        return "https://youtube.com";
    }

    @Override
    public LinkHandlerFactory getStreamLHFactory() {
        return YoutubeStreamLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getChannelLHFactory() {
        return YoutubeChannelLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getChannelTabLHFactory() {
        return YoutubeChannelTabLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getPlaylistLHFactory() {
        return YoutubePlaylistLinkHandlerFactory.getInstance();
    }

    @Override
    public SearchQueryHandlerFactory getSearchQHFactory() {
        return YoutubeSearchQueryHandlerFactory.getInstance();
    }

    @Override
    public StreamExtractor getStreamExtractor(final LinkHandler linkHandler) {
        return new YoutubeStreamExtractor(this, linkHandler, watchDataCache);
    }

    @Override
    public ChannelExtractor getChannelExtractor(final ListLinkHandler linkHandler) {
        return new YoutubeChannelExtractor(this, linkHandler);
    }

    @Override
    public ChannelTabExtractor getChannelTabExtractor(final ListLinkHandler linkHandler) {
        return new YoutubeChannelTabExtractor(this, linkHandler);
    }

    @Override
    public PlaylistExtractor getPlaylistExtractor(final ListLinkHandler linkHandler) {
        if (YoutubeParsingHelper.isYoutubeMixId(linkHandler.getId())) {
            return new YoutubeMixPlaylistExtractor(this, linkHandler);
        } else {
            return new YoutubePlaylistExtractor(this, linkHandler);
        }
    }

    @Override
    public SearchExtractor getSearchExtractor(final SearchQueryHandler query) {
        final List<FilterItem> contentFilters = query.getContentFilters();

        if (contentFilters.isEmpty()) {
            // something is odd
            throw new RuntimeException("contentFilters is empty. WHY?");
        }

        final FilterItem filterItem = contentFilters.get(0);
        if (filterItem instanceof YoutubeFilters.MusicYoutubeContentFilterItem) {
            return new YoutubeMusicSearchExtractor(this, query);
        } else {
            return new YoutubeSearchExtractor(this, query);
        }
    }

    @Override
    public SuggestionExtractor getSuggestionExtractor() {
        return new YoutubeSuggestionExtractor(this);
    }

    @Override
    public KioskList getKioskList() throws ExtractionException {
        final KioskList list = new KioskList(this);

        // add kiosks here e.g.:
        try {
            list.addKioskEntry(
                    (streamingService, url, id) -> new YoutubeTrendingExtractor(
                            YoutubeService.this,
                            new YoutubeTrendingLinkHandlerFactory().fromUrl(url),
                            id
                    ),
                    new YoutubeTrendingLinkHandlerFactory(),
                    "Recommended Lives"
            );
            list.setDefaultKiosk("Recommended Lives");
        } catch (final Exception e) {
            throw new ExtractionException(e);
        }

        return list;
    }

    @Override
    public SubscriptionExtractor getSubscriptionExtractor() {
        return new YoutubeSubscriptionExtractor(this);
    }

    @Nonnull
    @Override
    public FeedExtractor getFeedExtractor(final String channelUrl) throws ExtractionException {
        return new YoutubeFeedExtractor(this, getChannelLHFactory().fromUrl(channelUrl));
    }

    @Override
    public ListLinkHandlerFactory getCommentsLHFactory() {
        return YoutubeCommentsLinkHandlerFactory.getInstance();
    }

    @Override
    public CommentsExtractor getCommentsExtractor(final ListLinkHandler urlIdHandler)
            throws ExtractionException {
        return new YoutubeCommentsExtractor(this, urlIdHandler);
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Localization
    //////////////////////////////////////////////////////////////////////////*/

    // https://www.youtube.com/picker_ajax?action_language_json=1
    // Using "zu" (Zulu) to prevent YouTube from auto-translating video titles
    private static final List<Localization> SUPPORTED_LANGUAGES = Localization.listFrom(
            "zu"
    );

    // https://www.youtube.com/picker_ajax?action_country_json=1
    private static final List<ContentCountry> SUPPORTED_COUNTRIES = ContentCountry.listFrom(
            "DZ", "AR", "AU", "AT", "AZ", "BH", "BD", "BY", "BE", "BO", "BA", "BR", "BG", "CA",
            "CL", "CO", "CR", "HR", "CY", "CZ", "DK", "DO", "EC", "EG", "SV", "EE", "FI", "FR",
            "GE", "DE", "GH", "GR", "GT", "HN", "HK", "HU", "IS", "IN", "ID", "IQ", "IE", "IL",
            "IT", "JM", "JP", "JO", "KZ", "KE", "KW", "LV", "LB", "LY", "LI", "LT", "LU", "MY",
            "MT", "MX", "ME", "MA", "NP", "NL", "NZ", "NI", "NG", "MK", "NO", "OM", "PK", "PA",
            "PG", "PY", "PE", "PH", "PL", "PT", "PR", "QA", "RO", "RU", "SA", "SN", "RS", "SG",
            "SK", "SI", "ZA", "KR", "ES", "LK", "SE", "CH", "TW", "TZ", "TH", "TN", "TR", "UG",
            "UA", "AE", "GB", "US", "UY", "VE", "VN", "YE", "ZW"
    );

    @Override
    public List<Localization> getSupportedLocalizations() {
        return SUPPORTED_LANGUAGES;
    }

    @Override
    public Localization getLocalization() {
        return new Localization("zu");
    }

    @Override
    public List<ContentCountry> getSupportedCountries() {
        return SUPPORTED_COUNTRIES;
    }

    @Override
    public BulletCommentsExtractor getBulletCommentsExtractor(ListLinkHandler linkHandler) throws ExtractionException {
        return new YoutubeBulletCommentsExtractor(this, linkHandler, watchDataCache);
    }

    @Override
    public ListLinkHandlerFactory getBulletCommentsLHFactory() {
        return new YoutubeBulletCommentsLinkHandlerFactory();
    }
}
