package dev.maxrave.pipepipe.extractor.suggestion;

import dev.maxrave.pipepipe.extractor.StreamingService;
import dev.maxrave.pipepipe.extractor.exceptions.ExtractionException;
import dev.maxrave.pipepipe.extractor.localization.ContentCountry;
import dev.maxrave.pipepipe.extractor.localization.Localization;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

public abstract class SuggestionExtractor {
    private final StreamingService service;
    @Nullable private Localization forcedLocalization;
    @Nullable private ContentCountry forcedContentCountry;

    public SuggestionExtractor(final StreamingService service) {
        this.service = service;
    }

    public abstract List<String> suggestionList(String query)
            throws IOException, ExtractionException;

    public int getServiceId() {
        return service.getServiceId();
    }

    public StreamingService getService() {
        return service;
    }

    // TODO: Create a more general Extractor class

    public void forceLocalization(@Nullable final Localization localization) {
        this.forcedLocalization = localization;
    }

    public void forceContentCountry(@Nullable final ContentCountry contentCountry) {
        this.forcedContentCountry = contentCountry;
    }

    @Nonnull
    public Localization getExtractorLocalization() {
        return forcedLocalization == null ? getService().getLocalization() : forcedLocalization;
    }

    @Nonnull
    public ContentCountry getExtractorContentCountry() {
        return forcedContentCountry == null
                ? getService().getContentCountry() : forcedContentCountry;
    }
}
