package dev.maxrave.pipepipe.extractor.services.youtube.extractors;

import com.grack.nanojson.JsonObject;

import dev.maxrave.pipepipe.extractor.bulletComments.BulletCommentsInfoItem;
import dev.maxrave.pipepipe.extractor.exceptions.ParsingException;

public class YoutubeSuperChatInfoItemExtractor extends YoutubeBulletCommentsInfoItemExtractor {
    private JsonObject data;
    public YoutubeSuperChatInfoItemExtractor(JsonObject item, long startTime, long offsetDuration) {
        super(item, startTime, offsetDuration);
        data = item;
    }

    @Override
    public String getCommentText() throws ParsingException {
        String superResult = super.getCommentText();
        if(superResult.length() == 0){
            return "";
        }
        return String.format("(%s) ", data.getObject("purchaseAmountText")
                .getString("simpleText")) + super.getCommentText();
    }

    @Override
    public int getArgbColor() throws ParsingException {
        return (int) data.getLong("bodyBackgroundColor");
    }

    @Override
    public BulletCommentsInfoItem.Position getPosition() throws ParsingException {
        return BulletCommentsInfoItem.Position.SUPERCHAT;
    }
}
