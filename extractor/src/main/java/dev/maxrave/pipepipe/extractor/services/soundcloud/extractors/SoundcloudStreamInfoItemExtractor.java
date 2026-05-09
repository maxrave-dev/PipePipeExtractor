package dev.maxrave.pipepipe.extractor.services.soundcloud.extractors;

import com.grack.nanojson.JsonObject;
import dev.maxrave.pipepipe.extractor.exceptions.ParsingException;
import dev.maxrave.pipepipe.extractor.localization.DateWrapper;
import dev.maxrave.pipepipe.extractor.services.soundcloud.SoundcloudParsingHelper;
import dev.maxrave.pipepipe.extractor.stream.StreamInfoItemExtractor;
import dev.maxrave.pipepipe.extractor.stream.StreamType;

import javax.annotation.Nullable;

import static dev.maxrave.pipepipe.extractor.utils.Utils.EMPTY_STRING;
import static dev.maxrave.pipepipe.extractor.utils.Utils.replaceHttpWithHttps;

public class SoundcloudStreamInfoItemExtractor implements StreamInfoItemExtractor {

    protected final JsonObject itemObject;

    public SoundcloudStreamInfoItemExtractor(final JsonObject itemObject) {
        this.itemObject = itemObject;
    }

    @Override
    public String getUrl() {
        return replaceHttpWithHttps(itemObject.getString("permalink_url"));
    }

    @Override
    public String getName() {
        return itemObject.getString("title");
    }

    @Override
    public long getDuration() {
        return itemObject.getLong("duration") / 1000L;
    }

    @Override
    public String getUploaderName() {
        return itemObject.getObject("user").getString("username");
    }

    @Override
    public String getUploaderUrl() {
        return replaceHttpWithHttps(itemObject.getObject("user").getString("permalink_url"));
    }

    @Nullable
    @Override
    public String getUploaderAvatarUrl() {
        return null;
    }

    @Override
    public boolean isUploaderVerified() throws ParsingException {
        return itemObject.getObject("user").getBoolean("verified");
    }

    @Override
    public String getTextualUploadDate() {
        return itemObject.getString("created_at");
    }

    @Override
    public DateWrapper getUploadDate() throws ParsingException {
        return new DateWrapper(SoundcloudParsingHelper.parseDateFrom(getTextualUploadDate()));
    }

    @Override
    public long getViewCount() {
        return itemObject.getLong("playback_count");
    }

    @Override
    public String getThumbnailUrl() {
        String artworkUrl = itemObject.getString("artwork_url", EMPTY_STRING);
        if (artworkUrl.isEmpty()) {
            artworkUrl = itemObject.getObject("user").getString("avatar_url");
        }
        return artworkUrl.replace("large.jpg", "crop.jpg");
    }

    @Override
    public StreamType getStreamType() {
        return StreamType.AUDIO_STREAM;
    }

    @Override
    public boolean isAd() {
        return false;
    }
}
