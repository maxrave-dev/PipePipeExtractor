package dev.maxrave.pipepipe.extractor.bulletComments;

import javax.annotation.Nonnull;

import dev.maxrave.pipepipe.extractor.ListExtractor;
import dev.maxrave.pipepipe.extractor.Page;
import dev.maxrave.pipepipe.extractor.StreamingService;
import dev.maxrave.pipepipe.extractor.exceptions.ExtractionException;
import dev.maxrave.pipepipe.extractor.exceptions.ParsingException;
import dev.maxrave.pipepipe.extractor.linkhandler.ListLinkHandler;

import java.io.IOException;
import java.util.List;

public abstract class BulletCommentsExtractor extends ListExtractor<BulletCommentsInfoItem> {
    public BulletCommentsExtractor(final StreamingService service, final ListLinkHandler uiHandler) {
        super(service, uiHandler);
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        return "BulletComments";
    }

    // No need to be overridden normally as it is enough for bullet comments to be handled in getInitialPage
    @Override
    public InfoItemsPage<BulletCommentsInfoItem> getPage(Page page) throws IOException, ExtractionException {
        return null;
    }

    public List<BulletCommentsInfoItem> getLiveMessages() throws ParsingException {
        return null;
    }

    // return false if all the bullet comments can be fetched in one go, true if they need to be fetched continuously
    public boolean isLive() {
        return false;
    }

    public boolean isDisabled() {
        return false;
    }

    // Must be overriden if your BC extractor needs to fetch bullet comments continuously
    public void disconnect() {

    }

    // Must be overriden if your BC extractor needs to fetch bullet comments continuously
    public void reconnect() {

    }

    // May be useful if your BC extractor needs to fetch bullet comments continuously and requires the current play position
    public void setCurrentPlayPosition(long currentPlayPosition) {
    }

    public void clearMappingState() {
    }
}
