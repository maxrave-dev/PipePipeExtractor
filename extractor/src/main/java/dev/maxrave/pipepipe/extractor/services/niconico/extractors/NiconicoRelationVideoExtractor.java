package dev.maxrave.pipepipe.extractor.services.niconico.extractors;

import org.jsoup.nodes.Element;
import dev.maxrave.pipepipe.extractor.exceptions.ParsingException;
import dev.maxrave.pipepipe.extractor.localization.DateWrapper;
import dev.maxrave.pipepipe.extractor.stream.StreamInfoItemExtractor;
import dev.maxrave.pipepipe.extractor.stream.StreamType;

import javax.annotation.Nullable;

public class NiconicoRelationVideoExtractor implements StreamInfoItemExtractor {
    private final Element video;

    public NiconicoRelationVideoExtractor(final Element v) {
        video = v;
    }

    @Override
    public String getName() throws ParsingException {
        return video.select("title").text();
    }

    @Override
    public String getUrl() throws ParsingException {
        return video.select("url").text();
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        return video.select("thumbnail").text();
    }

    @Override
    public StreamType getStreamType() throws ParsingException {
        return StreamType.VIDEO_STREAM;
    }

    @Override
    public boolean isAd() throws ParsingException {
        return false;
    }

    @Override
    public long getDuration() throws ParsingException {
        return Long.parseLong(video.select("length").text());
    }

    @Override
    public long getViewCount() throws ParsingException {
        return Long.parseLong(video.select("view").text());
    }

    @Override
    public String getUploaderName() throws ParsingException {
        return "";
    }

    @Override
    public String getUploaderUrl() throws ParsingException {
        return "";
    }

    @Nullable
    @Override
    public String getUploaderAvatarUrl() throws ParsingException {
        return null;
    }

    @Override
    public boolean isUploaderVerified() throws ParsingException {
        return false;
    }

    @Nullable
    @Override
    public String getTextualUploadDate() throws ParsingException {
        return null;
    }

    @Nullable
    @Override
    public DateWrapper getUploadDate() throws ParsingException {
        return null;
    }
}
