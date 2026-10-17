package dev.maxrave.pipepipe.extractor.services.peertube.extractors;

import dev.maxrave.pipepipe.extractor.InfoItem;
import dev.maxrave.pipepipe.extractor.Page;
import dev.maxrave.pipepipe.extractor.StreamingService;
import dev.maxrave.pipepipe.extractor.exceptions.ExtractionException;
import dev.maxrave.pipepipe.extractor.exceptions.ParsingException;
import dev.maxrave.pipepipe.extractor.linkhandler.ListLinkHandler;
import dev.maxrave.pipepipe.extractor.services.peertube.linkHandler.PeertubeChannelLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.peertube.linkHandler.PeertubeChannelTabLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.utils.Utils;

import javax.annotation.Nonnull;
import java.io.IOException;

import static dev.maxrave.pipepipe.extractor.services.peertube.PeertubeParsingHelper.COUNT_KEY;
import static dev.maxrave.pipepipe.extractor.services.peertube.PeertubeParsingHelper.ITEMS_PER_PAGE;
import static dev.maxrave.pipepipe.extractor.services.peertube.PeertubeParsingHelper.START_KEY;
import static dev.maxrave.pipepipe.extractor.utils.Utils.isNullOrEmpty;

/**
 * The {@link dev.maxrave.pipepipe.extractor.linkhandler.ChannelTabs#SEARCH SEARCH} tab of a PeerTube
 * channel or account.
 *
 * <p>
 * PeerTube has no dedicated search endpoint for a channel: the video list of a channel
 * ({@code /api/v1/video-channels/<handle>/videos}) and the one of an account
 * ({@code /api/v1/accounts/<name>/videos}) both accept a {@code search} parameter, so the tab is
 * the video list of {@link PeertubeChannelTabExtractor} with that parameter added.
 * </p>
 */
public class PeertubeChannelSearchExtractor extends PeertubeChannelTabExtractor {

    public PeertubeChannelSearchExtractor(final StreamingService service,
                                          final ListLinkHandler linkHandler)
            throws ParsingException {
        super(service, linkHandler);
    }

    @Nonnull
    @Override
    public InfoItemsPage<InfoItem> getInitialPage() throws IOException, ExtractionException {
        final String query = PeertubeChannelTabLinkHandlerFactory
                .getSearchQueryFromUrl(getOriginalUrl());
        if (isNullOrEmpty(query)) {
            return InfoItemsPage.emptyPage();
        }

        return getPage(new Page(getBaseUrl() + PeertubeChannelLinkHandlerFactory.API_ENDPOINT
                + getId() + PeertubeChannelTabLinkHandlerFactory.getUrlSuffix(getTab())
                + "?search=" + Utils.encodeUrlUtf8(query)
                + "&sort=-publishedAt"
                + "&" + START_KEY + "=0&" + COUNT_KEY + "=" + ITEMS_PER_PAGE));
    }
}
