package dev.maxrave.pipepipe.extractor.services.peertube.extractors;

import com.grack.nanojson.JsonObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import dev.maxrave.pipepipe.extractor.ServiceList;
import dev.maxrave.pipepipe.extractor.comments.CommentsInfoItemExtractor;
import dev.maxrave.pipepipe.extractor.exceptions.ParsingException;
import dev.maxrave.pipepipe.extractor.localization.DateWrapper;
import dev.maxrave.pipepipe.extractor.services.peertube.PeertubeParsingHelper;
import dev.maxrave.pipepipe.extractor.stream.Description;
import dev.maxrave.pipepipe.extractor.utils.JsonUtils;

import java.util.Objects;

import static dev.maxrave.pipepipe.extractor.utils.Utils.EMPTY_STRING;

public class PeertubeCommentsInfoItemExtractor implements CommentsInfoItemExtractor {
    private final JsonObject item;
    private final String url;
    private final String baseUrl;

    public PeertubeCommentsInfoItemExtractor(final JsonObject item,
                                             final PeertubeCommentsExtractor extractor)
            throws ParsingException {
        this.item = item;
        this.url = extractor.getUrl();
        this.baseUrl = extractor.getBaseUrl();
    }

    @Override
    public String getUrl() throws ParsingException {
        return url;
    }

    @Override
    public String getThumbnailUrl() {
        return PeertubeParsingHelper.getBiggestImageUrl(
                PeertubeParsingHelper.getAvatarsFromOwnerAccountOrVideoChannelObject(
                        baseUrl, item.getObject("account")));
    }

    @Override
    public String getName() throws ParsingException {
        return JsonUtils.getString(item, "account.displayName");
    }

    @Override
    public String getTextualUploadDate() throws ParsingException {
        return JsonUtils.getString(item, "createdAt");
    }

    @Override
    public DateWrapper getUploadDate() throws ParsingException {
        final String textualUploadDate = getTextualUploadDate();
        return new DateWrapper(PeertubeParsingHelper.parseDateFrom(textualUploadDate));
    }

    @Override
    public Description getCommentText() throws ParsingException {
        final String htmlText = JsonUtils.getString(item, "text");
        try {
            final Document doc = Jsoup.parse(htmlText);
            return new Description(doc.body().text(), Description.PLAIN_TEXT);
        } catch (final Exception e) {
            return new Description(htmlText.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", EMPTY_STRING),
                    Description.PLAIN_TEXT);
        }
    }

    @Override
    public String getCommentId() {
        return Objects.toString(item.getLong("id"), null);
    }

    @Override
    public String getUploaderAvatarUrl() {
        return PeertubeParsingHelper.getBiggestImageUrl(
                PeertubeParsingHelper.getAvatarsFromOwnerAccountOrVideoChannelObject(
                        baseUrl, item.getObject("account")));
    }

    @Override
    public String getUploaderName() throws ParsingException {
        return JsonUtils.getString(item, "account.name") + "@"
                + JsonUtils.getString(item, "account.host");
    }

    @Override
    public String getUploaderUrl() throws ParsingException {
        final String name = JsonUtils.getString(item, "account.name");
        final String host = JsonUtils.getString(item, "account.host");
        return ServiceList.PeerTube.getChannelLHFactory()
                .fromId("accounts/" + name + "@" + host, baseUrl).getUrl();
    }
}
