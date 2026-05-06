// Reusable OkHttp-based Downloader for integration tests.
// Disabled by default. To re-enable: remove the leading "// " from every line below.
//
// package org.schabi.newpipe.extractor.test;
//
// import okhttp3.Call;
// import okhttp3.Callback;
// import okhttp3.OkHttpClient;
// import okhttp3.RequestBody;
// import okhttp3.ResponseBody;
// import org.schabi.newpipe.extractor.downloader.CancellableCall;
// import org.schabi.newpipe.extractor.downloader.Downloader;
// import org.schabi.newpipe.extractor.downloader.Request;
// import org.schabi.newpipe.extractor.downloader.Response;
// import org.schabi.newpipe.extractor.exceptions.ReCaptchaException;
//
// import javax.annotation.Nonnull;
// import java.io.IOException;
// import java.util.List;
// import java.util.Map;
// import java.util.concurrent.TimeUnit;
//
// /**
//  * Minimal OkHttp-based Downloader for tests.
//  * Not production-grade — no retry, no proxy, no streaming.
//  */
// public final class OkHttpDownloader extends Downloader {
//
//     private final OkHttpClient client;
//
//     public OkHttpDownloader() {
//         this.client = new OkHttpClient.Builder()
//                 .connectTimeout(30, TimeUnit.SECONDS)
//                 .readTimeout(30, TimeUnit.SECONDS)
//                 .writeTimeout(30, TimeUnit.SECONDS)
//                 .build();
//     }
//
//     // Always log for debugging
//     private static final boolean DEBUG = true;
//     private static final java.io.File LOG_FILE = new java.io.File("/tmp/pipepipe-debug.log");
//
//     private static synchronized void log(final String s) {
//         System.out.println(s);
//         try (java.io.FileWriter fw = new java.io.FileWriter(LOG_FILE, true)) {
//             fw.write(s);
//             fw.write('\n');
//         } catch (java.io.IOException ignored) {
//             // best effort
//         }
//     }
//
//     private okhttp3.Request toOkHttp(final Request request) {
//         final okhttp3.Request.Builder builder = new okhttp3.Request.Builder()
//                 .url(request.url());
//
//         for (final Map.Entry<String, List<String>> e : request.headers().entrySet()) {
//             for (final String v : e.getValue()) {
//                 builder.addHeader(e.getKey(), v);
//             }
//         }
//
//         final byte[] body = request.dataToSend();
//         final String method = request.httpMethod();
//         if ("POST".equalsIgnoreCase(method)) {
//             builder.post(RequestBody.create(body == null ? new byte[0] : body));
//         } else if ("HEAD".equalsIgnoreCase(method)) {
//             builder.head();
//         } else {
//             builder.get();
//         }
//         return builder.build();
//     }
//
//     private Response toExtractorResponse(final okhttp3.Response resp,
//                                          final String requestUrl) throws IOException, ReCaptchaException {
//         if (resp.code() == 429) {
//             throw new ReCaptchaException("reCaptcha required", requestUrl);
//         }
//         final ResponseBody respBody = resp.body();
//         final byte[] raw = respBody == null ? new byte[0] : respBody.bytes();
//         final String text = new String(raw, java.nio.charset.StandardCharsets.UTF_8);
//
//         if (DEBUG && requestUrl.contains("/youtubei/v1/")) {
//             final int idx = requestUrl.indexOf("/youtubei/v1/") + "/youtubei/v1/".length();
//             final int qIdx = requestUrl.indexOf('?', idx);
//             final String endpoint = requestUrl.substring(idx, qIdx > 0 ? qIdx : requestUrl.length());
//
//             log("");
//             log("########## " + resp.request().method() + " /youtubei/v1/" + endpoint
//                     + " => " + resp.code() + " ##########");
//
//             if ("player".equals(endpoint)) {
//                 log("---- Stack trace (caller chain) ----");
//                 final StackTraceElement[] st = Thread.currentThread().getStackTrace();
//                 for (int i = 2; i < Math.min(st.length, 25); i++) {
//                     final StackTraceElement el = st[i];
//                     if (el.getClassName().startsWith("org.schabi.newpipe")) {
//                         log("  at " + el.getClassName() + "." + el.getMethodName()
//                                 + "(" + el.getFileName() + ":" + el.getLineNumber() + ")");
//                     }
//                 }
//                 final int sdIdx = text.indexOf("\"streamingData\"");
//                 final int afIdx = text.indexOf("\"adaptiveFormats\"");
//                 final int psIdx = text.indexOf("\"playabilityStatus\"");
//                 log("hasStreamingData : " + (sdIdx >= 0));
//                 log("hasAdaptiveFormats: " + (afIdx >= 0));
//                 if (psIdx >= 0) {
//                     final int end = Math.min(psIdx + 200, text.length());
//                     log("playabilityStatus: " + text.substring(psIdx, end));
//                 }
//                 if (afIdx >= 0) {
//                     final java.util.regex.Pattern p = java.util.regex.Pattern.compile(
//                             "\\{\"itag\":(\\d+),\"mimeType\":\"([^\"]+)\"(?:[^}]*\"qualityLabel\":\"([^\"]+)\")?(?:[^}]*\"audioQuality\":\"([^\"]+)\")?(?:[^}]*\"averageBitrate\":(\\d+))?");
//                     final java.util.regex.Matcher m = p.matcher(text);
//                     log("---- All itags in adaptiveFormats ----");
//                     int count = 0;
//                     while (m.find()) {
//                         final String itag = m.group(1);
//                         final String mime = m.group(2);
//                         final String quality = m.group(3) != null ? m.group(3) : "-";
//                         final String audioQ = m.group(4) != null ? m.group(4) : "-";
//                         final String bitrate = m.group(5) != null ? m.group(5) : "-";
//                         log(String.format("  itag=%-4s  mime=%-50s  quality=%-25s  audioQ=%-30s  avgBitrate=%s",
//                                 itag, mime, quality, audioQ, bitrate));
//                         count++;
//                     }
//                     log("---- Total: " + count + " formats ----");
//                 }
//             } else {
//                 log("preview: " + text.substring(0, Math.min(200, text.length())));
//             }
//             log("");
//         }
//
//         return new Response(
//                 resp.code(),
//                 resp.message(),
//                 resp.headers().toMultimap(),
//                 text,
//                 raw,
//                 resp.request().url().toString());
//     }
//
//     @Override
//     public Response execute(@Nonnull final Request request)
//             throws IOException, ReCaptchaException {
//         try (okhttp3.Response resp = client.newCall(toOkHttp(request)).execute()) {
//             return toExtractorResponse(resp, request.url());
//         }
//     }
//
//     @Override
//     public CancellableCall executeAsync(@Nonnull final Request request,
//                                         final AsyncCallback callback) {
//         if (DEBUG && request.url().contains("/youtubei/v1/player")) {
//             log(">>> executeAsync called for /player <<<");
//             final StackTraceElement[] st = Thread.currentThread().getStackTrace();
//             for (int i = 2; i < Math.min(st.length, 30); i++) {
//                 final StackTraceElement el = st[i];
//                 if (el.getClassName().startsWith("org.schabi.newpipe")) {
//                     log("    at " + el.getClassName() + "." + el.getMethodName()
//                             + "(" + el.getFileName() + ":" + el.getLineNumber() + ")");
//                 }
//             }
//         }
//         final Call call = client.newCall(toOkHttp(request));
//         final CancellableCall cancellable = new CancellableCall(call);
//         call.enqueue(new Callback() {
//             @Override
//             public void onFailure(@Nonnull final Call c, @Nonnull final IOException e) {
//                 cancellable.setFinished();
//                 callback.onError(e);
//             }
//
//             @Override
//             public void onResponse(@Nonnull final Call c, @Nonnull final okhttp3.Response resp) {
//                 try (okhttp3.Response r = resp) {
//                     callback.onSuccess(toExtractorResponse(r, request.url()));
//                 } catch (Exception e) {
//                     callback.onError(e);
//                 } finally {
//                     cancellable.setFinished();
//                 }
//             }
//         });
//         return cancellable;
//     }
// }
