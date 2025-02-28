package io.xhub.smwall.utlis;

public class StringUtils {
    private static final String CHANNEL_URL_EMBED = "https://www.youtube.com/embed/";
    private static final String YOUTUBE_WATCH_URL = "https://www.youtube.com/watch";

    public static String concat(String str1, String str2) {
        return str1 + str2;
    }

    public static String generateStringEmbedUrl(String channelId) {
        return CHANNEL_URL_EMBED.concat(channelId);
    }

    public static String prependHashtag(String hashtag) {
        return "#" + hashtag.substring(hashtag.indexOf('.') + 1);
    }

    public static String prependAtSign(String mention) {
        return "@" + mention;
    }

    public static String getYouTubeVideoUrlFromId(String videoId) {
        return YOUTUBE_WATCH_URL.concat("?v=" + videoId);
    }

    public static String getBase64Data(String contentType, String data) {
        return "data:" +
                contentType +
                ";base64," +
                data;
    }
}
