package dev.maxrave.pipepipe.extractor.channel;

import dev.maxrave.pipepipe.extractor.InfoItem;
import dev.maxrave.pipepipe.extractor.ListExtractor;
import dev.maxrave.pipepipe.extractor.StreamingService;
import dev.maxrave.pipepipe.extractor.exceptions.ParsingException;
import dev.maxrave.pipepipe.extractor.linkhandler.ListLinkHandler;

import javax.annotation.Nonnull;

public abstract class ChannelTabExtractor extends ListExtractor<InfoItem> {

    public ChannelTabExtractor(final StreamingService service,
                               final ListLinkHandler linkHandler) {
        super(service, linkHandler);
    }

    @Nonnull
    public String getTab() {
        return getLinkHandler().getContentFilters().get(0).getName();
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        return getTab();
    }

}
