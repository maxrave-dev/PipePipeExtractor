package dev.maxrave.pipepipe.extractor.channel;

import dev.maxrave.pipepipe.extractor.InfoItem;
import dev.maxrave.pipepipe.extractor.ListExtractor;
import dev.maxrave.pipepipe.extractor.ListInfo;
import dev.maxrave.pipepipe.extractor.Page;
import dev.maxrave.pipepipe.extractor.StreamingService;
import dev.maxrave.pipepipe.extractor.exceptions.ExtractionException;
import dev.maxrave.pipepipe.extractor.linkhandler.ListLinkHandler;
import dev.maxrave.pipepipe.extractor.utils.ExtractorHelper;

import java.io.IOException;

public class ChannelTabInfo extends ListInfo<InfoItem> {
    public ChannelTabInfo(final int serviceId, final ListLinkHandler linkHandler) {
        super(serviceId, linkHandler, linkHandler.getContentFilters().get(0).getName());
    }

    public static ChannelTabInfo getInfo(final StreamingService service,
                                         final ListLinkHandler linkHandler)
            throws ExtractionException, IOException {
        final ChannelTabExtractor extractor = service.getChannelTabExtractor(linkHandler);
        extractor.fetchPage();
        return getInfo(extractor);
    }

    public static ChannelTabInfo getInfo(final ChannelTabExtractor extractor) {
        final ChannelTabInfo info =
                new ChannelTabInfo(extractor.getServiceId(), extractor.getLinkHandler());

        try {
            info.setOriginalUrl(extractor.getOriginalUrl());
        } catch (final Exception e) {
            info.addError(e);
        }

        final ListExtractor.InfoItemsPage<InfoItem> page
                = ExtractorHelper.getItemsPageOrLogError(info, extractor);
        info.setRelatedItems(page.getItems());
        info.setNextPage(page.getNextPage());

        return info;
    }

    public static ListExtractor.InfoItemsPage<InfoItem> getMoreItems(
            final StreamingService service, final ListLinkHandler linkHandler, final Page page)
            throws ExtractionException, IOException {
        return service.getChannelTabExtractor(linkHandler).getPage(page);
    }
}
