package dev.maxrave.pipepipe.extractor.services.peertube.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import dev.maxrave.pipepipe.extractor.InfoItem;
import dev.maxrave.pipepipe.extractor.MultiInfoItemsCollector;
import dev.maxrave.pipepipe.extractor.Page;
import dev.maxrave.pipepipe.extractor.StreamingService;
import dev.maxrave.pipepipe.extractor.channel.ChannelTabExtractor;
import dev.maxrave.pipepipe.extractor.downloader.Downloader;
import dev.maxrave.pipepipe.extractor.downloader.Response;
import dev.maxrave.pipepipe.extractor.exceptions.ExtractionException;
import dev.maxrave.pipepipe.extractor.exceptions.ParsingException;
import dev.maxrave.pipepipe.extractor.linkhandler.ChannelTabs;
import dev.maxrave.pipepipe.extractor.linkhandler.ListLinkHandler;
import dev.maxrave.pipepipe.extractor.services.peertube.PeertubeParsingHelper;
import dev.maxrave.pipepipe.extractor.services.peertube.linkHandler.PeertubeChannelLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.utils.Utils;

import javax.annotation.Nonnull;
import java.io.IOException;

import static dev.maxrave.pipepipe.extractor.services.peertube.PeertubeParsingHelper.COUNT_KEY;
import static dev.maxrave.pipepipe.extractor.services.peertube.PeertubeParsingHelper.ITEMS_PER_PAGE;
import static dev.maxrave.pipepipe.extractor.services.peertube.PeertubeParsingHelper.START_KEY;
import static dev.maxrave.pipepipe.extractor.utils.Utils.isNullOrEmpty;

public class PeertubeChannelTabExtractor extends ChannelTabExtractor {
    private final String baseUrl;

    public PeertubeChannelTabExtractor(final StreamingService service,
                                       final ListLinkHandler linkHandler)
            throws ParsingException {
        super(service, linkHandler);
        baseUrl = getBaseUrl();
    }

    @Override
    public void onFetchPage(final @Nonnull Downloader downloader) throws ParsingException {
        if (!getTab().equals(ChannelTabs.PLAYLISTS)) {
            throw new ParsingException("tab " + getTab() + " not supported");
        }
    }

    @Nonnull
    @Override
    public InfoItemsPage<InfoItem> getInitialPage()
            throws IOException, ExtractionException {
        return getPage(new Page(baseUrl + PeertubeChannelLinkHandlerFactory.API_ENDPOINT + getId()
                + "/video-playlists?" + START_KEY + "=0&" + COUNT_KEY + "=" + ITEMS_PER_PAGE));
    }

    @Override
    public InfoItemsPage<InfoItem> getPage(final Page page)
            throws IOException, ExtractionException {
        if (page == null || isNullOrEmpty(page.getUrl())) {
            throw new IllegalArgumentException("Page doesn't contain an URL");
        }

        final Response response = getDownloader().get(page.getUrl());

        JsonObject pageJson = null;
        if (response != null && !Utils.isBlank(response.responseBody())) {
            try {
                pageJson = JsonParser.object().from(response.responseBody());
            } catch (final Exception e) {
                throw new ParsingException("Could not parse json data for account info", e);
            }
        }

        if (pageJson == null) {
            throw new ExtractionException("Unable to get channel playlist list");
        }

        PeertubeParsingHelper.validate(pageJson);

        final MultiInfoItemsCollector collector = new MultiInfoItemsCollector(getServiceId());
        final JsonArray contents = pageJson.getArray("data");
        if (contents == null) {
            throw new ParsingException("Unable to extract channel playlist list");
        }

        for (final Object c : contents) {
            if (c instanceof JsonObject) {
                collector.commit(new PeertubePlaylistInfoItemExtractor((JsonObject) c, baseUrl));
            }
        }

        return new InfoItemsPage<>(
                collector, PeertubeParsingHelper.getNextPage(page.getUrl(),
                pageJson.getLong("total")));
    }
}
