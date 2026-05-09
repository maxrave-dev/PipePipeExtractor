package dev.maxrave.pipepipe.extractor.services.soundcloud.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;

import dev.maxrave.pipepipe.extractor.NewPipe;
import dev.maxrave.pipepipe.extractor.Page;
import dev.maxrave.pipepipe.extractor.StreamingService;
import dev.maxrave.pipepipe.extractor.comments.CommentsExtractor;
import dev.maxrave.pipepipe.extractor.comments.CommentsInfoItem;
import dev.maxrave.pipepipe.extractor.comments.CommentsInfoItemsCollector;
import dev.maxrave.pipepipe.extractor.downloader.Downloader;
import dev.maxrave.pipepipe.extractor.downloader.Response;
import dev.maxrave.pipepipe.extractor.exceptions.ExtractionException;
import dev.maxrave.pipepipe.extractor.exceptions.ParsingException;
import dev.maxrave.pipepipe.extractor.exceptions.ReCaptchaException;
import dev.maxrave.pipepipe.extractor.linkhandler.ListLinkHandler;

import java.io.IOException;

import javax.annotation.Nonnull;

import static dev.maxrave.pipepipe.extractor.utils.Utils.isNullOrEmpty;

public class SoundcloudCommentsExtractor extends CommentsExtractor {
    public SoundcloudCommentsExtractor(final StreamingService service,
                                       final ListLinkHandler uiHandler) {
        super(service, uiHandler);
    }

    @Nonnull
    @Override
    public InfoItemsPage<CommentsInfoItem> getInitialPage() throws ExtractionException,
            IOException {
        return getPage(getUrl());
    }

    @Override
    public InfoItemsPage<CommentsInfoItem> getPage(final Page page) throws ExtractionException,
            IOException {
        if (page == null || isNullOrEmpty(page.getUrl())) {
            throw new IllegalArgumentException("Page doesn't contain an URL");
        }
        return getPage(page.getUrl());
    }

    @Nonnull
    private InfoItemsPage<CommentsInfoItem> getPage(@Nonnull final String url)
            throws ParsingException, IOException, ReCaptchaException {

        final Downloader downloader = NewPipe.getDownloader();
        final Response response = downloader.get(url);

        final JsonObject json;
        try {
            json = JsonParser.object().from(response.responseBody());
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse json", e);
        }

        final CommentsInfoItemsCollector collector = new CommentsInfoItemsCollector(
                getServiceId());

        collectStreamsFrom(collector, json.getArray("collection"));

        return new InfoItemsPage<>(collector, new Page(json.getString("next_href", null)));
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader) { }

    private void collectStreamsFrom(final CommentsInfoItemsCollector collector,
                                    final JsonArray entries) throws ParsingException {
        final String url = getUrl();
        for (final Object comment : entries) {
            collector.commit(new SoundcloudCommentsInfoItemExtractor((JsonObject) comment, url));
        }
    }
}
