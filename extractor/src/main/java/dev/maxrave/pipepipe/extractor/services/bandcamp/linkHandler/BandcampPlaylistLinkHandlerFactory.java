// Created by Fynn Godau 2019, licensed GNU GPL version 3 or later

package dev.maxrave.pipepipe.extractor.services.bandcamp.linkHandler;

import dev.maxrave.pipepipe.extractor.search.filter.FilterItem;

import dev.maxrave.pipepipe.extractor.exceptions.ParsingException;
import dev.maxrave.pipepipe.extractor.linkhandler.ListLinkHandlerFactory;
import dev.maxrave.pipepipe.extractor.services.bandcamp.extractors.BandcampExtractorHelper;

import java.util.List;

/**
 * Just as with streams, the album ids are essentially useless for us.
 */
public class BandcampPlaylistLinkHandlerFactory extends ListLinkHandlerFactory {
    @Override
    public String getId(final String url) throws ParsingException {
        return getUrl(url);
    }

    @Override
    public String getUrl(final String url,
                         final List<FilterItem> contentFilter,
                         final List<FilterItem> sortFilter) throws ParsingException {
        return url;
    }

    /**
     * Accepts all bandcamp URLs that contain /album/ behind their domain name.
     */
    @Override
    public boolean onAcceptUrl(final String url) throws ParsingException {

        // Exclude URLs which do not lead to an album
        if (!url.toLowerCase().matches("https?://.+\\..+/album/.+")) {
            return false;
        }

        // Test whether domain is supported
        return BandcampExtractorHelper.isSupportedDomain(url);
    }
}
