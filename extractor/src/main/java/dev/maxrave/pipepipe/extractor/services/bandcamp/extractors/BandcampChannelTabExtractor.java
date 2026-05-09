package dev.maxrave.pipepipe.extractor.services.bandcamp.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import dev.maxrave.pipepipe.extractor.InfoItem;
import dev.maxrave.pipepipe.extractor.MultiInfoItemsCollector;
import dev.maxrave.pipepipe.extractor.Page;
import dev.maxrave.pipepipe.extractor.StreamingService;
import dev.maxrave.pipepipe.extractor.channel.ChannelTabExtractor;
import dev.maxrave.pipepipe.extractor.downloader.Downloader;
import dev.maxrave.pipepipe.extractor.exceptions.ExtractionException;
import dev.maxrave.pipepipe.extractor.exceptions.ParsingException;
import dev.maxrave.pipepipe.extractor.linkhandler.ChannelTabs;
import dev.maxrave.pipepipe.extractor.linkhandler.ListLinkHandler;
import dev.maxrave.pipepipe.extractor.services.bandcamp.extractors.streaminfoitem.BandcampDiscographStreamInfoItemExtractor;
import dev.maxrave.pipepipe.extractor.services.bandcamp.linkHandler.BandcampChannelTabHandler;

import javax.annotation.Nonnull;
import java.io.IOException;

public class BandcampChannelTabExtractor extends ChannelTabExtractor {
    private JsonArray discography;
    private final String filter;
    public BandcampChannelTabExtractor(final StreamingService service,
                                       final ListLinkHandler linkHandler) {
        super(service, linkHandler);

        final String tab = linkHandler.getContentFilters().get(0).getName();
        switch (tab) {
            case ChannelTabs.TRACKS:
                filter = "track";
                break;
            case ChannelTabs.ALBUMS:
                filter = "album";
                break;
            default:
                throw new IllegalArgumentException("Unsupported channel tab: " + tab);
        }
    }

    @Nonnull
    private JsonArray getDiscographs() throws ExtractionException {
        final ListLinkHandler tabHandler = getLinkHandler();
        if (tabHandler instanceof BandcampChannelTabHandler) {
            return ((BandcampChannelTabHandler) tabHandler).getDiscographs();
        } else {
            final JsonObject artistDetails = BandcampExtractorHelper.getArtistDetails(getId());
            return artistDetails.getArray("discography");
        }
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader) throws ParsingException {
        if (discography == null) {
            discography = BandcampExtractorHelper.getArtistDetails(getId())
                    .getArray("discography");
        }
    }

    @Nonnull
    @Override
    public InfoItemsPage<InfoItem> getInitialPage() throws IOException, ExtractionException {
        final MultiInfoItemsCollector collector = new MultiInfoItemsCollector(getServiceId());

        for (final Object discograph : discography) {
            // A discograph is as an item appears in a discography
            if (!(discograph instanceof JsonObject)) {
                continue;
            }

            final JsonObject discographJsonObject = (JsonObject) discograph;
            final String itemType = discographJsonObject.getString("item_type", "");

            if (!itemType.equals(filter)) {
                continue;
            }

            switch (itemType) {
                case "track":
                    collector.commit(new BandcampDiscographStreamInfoItemExtractor(
                            discographJsonObject, getUrl()));
                    break;
                case "album":
                    collector.commit(new BandcampAlbumInfoItemExtractor(
                            discographJsonObject, getUrl()));
                    break;
            }
        }

        return new InfoItemsPage<>(collector, null);
    }

    @Override
    public InfoItemsPage<InfoItem> getPage(final Page page) {
        return null;
    }
}
