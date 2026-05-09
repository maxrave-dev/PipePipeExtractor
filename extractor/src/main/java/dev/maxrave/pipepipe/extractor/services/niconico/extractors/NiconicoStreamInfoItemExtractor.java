package dev.maxrave.pipepipe.extractor.services.niconico.extractors;

import com.grack.nanojson.JsonObject;

import dev.maxrave.pipepipe.extractor.exceptions.ParsingException;
import dev.maxrave.pipepipe.extractor.localization.DateWrapper;
import dev.maxrave.pipepipe.extractor.services.niconico.NiconicoService;
import dev.maxrave.pipepipe.extractor.services.niconico.NiconicoServiceParsingHelper;
import dev.maxrave.pipepipe.extractor.stream.StreamInfoItemExtractor;
import dev.maxrave.pipepipe.extractor.stream.StreamType;

import javax.annotation.Nullable;

public class NiconicoStreamInfoItemExtractor implements StreamInfoItemExtractor {
    protected final JsonObject item;
    public NiconicoStreamInfoItemExtractor(final JsonObject json) {
        item = json;
    }

    @Override
    public String getName() throws ParsingException {
        return item.getString("title");
    }

    @Override
    public String getUrl() throws ParsingException {
        return NiconicoService.WATCH_URL + item.getString("contentId");
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        return item.getString("thumbnailUrl");
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
        return item.getLong("lengthSeconds");
    }

    @Override
    public long getViewCount() throws ParsingException {
        return item.getLong("viewCounter");
    }

    @Override
    public String getUploaderName() throws ParsingException {
        // Snapshot search API could not get uploader name.
        if (isChannel()) {
            return "ch" + item.getLong("channelId");
        }
        return "user/" + item.getLong("userId");
    }

    @Override
    public String getUploaderUrl() throws ParsingException {
        if (isChannel()) {
            return NiconicoService.CHANNEL_URL + "ch" + item.getLong("channelId");
        }
        return NiconicoService.USER_URL + item.getLong("userId");
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
        return item.getString("startTime");
    }

    @Nullable
    @Override
    public DateWrapper getUploadDate() throws ParsingException {
        return NiconicoServiceParsingHelper.parseSnapshotDateTime(item.getString("startTime"));
    }

    private boolean isChannel() {
        return item.has("channelId") && item.getLong("channelId") != 0;
    }
}
