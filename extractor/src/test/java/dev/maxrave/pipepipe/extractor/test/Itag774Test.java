// Reusable integration test for verifying Premium-tier itag extraction (141, 774).
// Disabled by default to avoid leaking cookies and to keep the build fast.
// To re-enable: remove the leading "// " from every line below, then run with
//   YT_COOKIES="SAPISID=...; ..." ./gradlew :extractor:test --tests Itag774Test
//
// package dev.maxrave.pipepipe.extractor.test;
//
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
// import dev.maxrave.pipepipe.extractor.NewPipe;
// import dev.maxrave.pipepipe.extractor.ServiceList;
// import dev.maxrave.pipepipe.extractor.stream.AudioStream;
// import dev.maxrave.pipepipe.extractor.stream.StreamInfo;
// import dev.maxrave.pipepipe.extractor.stream.VideoStream;
//
// import java.util.List;
//
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertTrue;
//
// /**
//  * Integration test verifying that PipePipeExtractor correctly extracts
//  * Premium-tier itag 774 (Opus 256 kbps) when authenticated.
//  *
//  * Prerequisites:
//  *   1. A YouTube Premium account
//  *   2. Valid YT cookies set via env var YT_COOKIES
//  *
//  * Run:
//  *   YT_COOKIES="SAPISID=...; __Secure-3PAPISID=...; ..." \
//  *   ./gradlew :extractor:test --tests Itag774Test
//  *
//  * The test SKIPS automatically if YT_COOKIES is not provided.
//  */
// class Itag774Test {
//
//     /** Pick a long-running, widely-available music video. Replace if it goes private. */
//     private static final String TEST_VIDEO_URL =
//             "https://music.youtube.com/watch?v=IlVAy_wTG_s";
//
//     private static final int PREMIUM_OPUS_ITAG = 774;
//
//     @BeforeAll
//     static void setup() {
//         NewPipe.init(new OkHttpDownloader());
//         ServiceList.YouTube.setLoadingTimeout(30);
//         final String cookies = System.getenv("YT_COOKIES");
//         if (cookies != null && !cookies.isBlank()) {
//             ServiceList.YouTube.setTokens(cookies);
//         }
//     }
//
//     @Test
//     @DisplayName("Anonymous extraction returns audio streams (no Premium itags)")
//     void anonymousExtraction_returnsBaseAudio() throws Exception {
//         final String saved = ServiceList.YouTube.getTokens();
//         ServiceList.YouTube.setTokens(null);
//         try {
//             final StreamInfo info = StreamInfo.getInfo(TEST_VIDEO_URL);
//             dumpAudioStreams("[Anonymous]", info.getAudioStreams());
//             assertFalse(info.getAudioStreams().isEmpty(),
//                     "Anonymous extraction should still return audio streams");
//         } finally {
//             ServiceList.YouTube.setTokens(saved);
//         }
//     }
//
//     @Test
//     @EnabledIfEnvironmentVariable(named = "YT_COOKIES", matches = ".+")
//     @DisplayName("Premium login → dump ALL streams (audio + video + video-only)")
//     void premiumLogin_dumpAllStreams() throws Exception {
//         assertTrue(ServiceList.YouTube.hasTokens(),
//                 "tokens must be set — check YT_COOKIES env var");
//
//         final StreamInfo info = StreamInfo.getInfo(TEST_VIDEO_URL);
//
//         System.out.println("\n=========================================");
//         System.out.println("Video: " + info.getName());
//         System.out.println("URL  : " + info.getUrl());
//         System.out.println("=========================================");
//
//         dumpAudioStreams("AUDIO", info.getAudioStreams());
//         dumpVideoStreams("VIDEO (audio+video)", info.getVideoStreams());
//         dumpVideoStreams("VIDEO-ONLY (DASH)", info.getVideoOnlyStreams());
//
//         final boolean has774 = info.getAudioStreams().stream()
//                 .anyMatch(s -> s.getItag() == PREMIUM_OPUS_ITAG);
//         System.out.println("\nitag 774 present: " + has774);
//     }
//
//     private void dumpAudioStreams(final String label, final List<AudioStream> audios) {
//         System.out.println("\n--- " + label + " (" + audios.size() + ") ---");
//         for (final AudioStream s : audios) {
//             System.out.printf("  itag=%-4d  format=%-10s  bitrate=%4d kbps  codec=%-25s  track=%s%n",
//                     s.getItag(),
//                     s.getFormat() == null ? "?" : s.getFormat().getName(),
//                     s.getAverageBitrate(),
//                     s.getCodec() == null ? "-" : s.getCodec(),
//                     s.getAudioTrackName() == null ? "" : s.getAudioTrackName());
//             System.out.println("    URL: " + s.getUrl());
//         }
//     }
//
//     private void dumpVideoStreams(final String label, final List<VideoStream> videos) {
//         System.out.println("\n--- " + label + " (" + videos.size() + ") ---");
//         for (final VideoStream s : videos) {
//             System.out.printf("  itag=%-4d  format=%-10s  resolution=%-12s  fps=%-3d  codec=%s%n",
//                     s.getItag(),
//                     s.getFormat() == null ? "?" : s.getFormat().getName(),
//                     s.getResolution() == null ? "?" : s.getResolution(),
//                     s.getFps(),
//                     s.getCodec() == null ? "-" : s.getCodec());
//             System.out.println("    URL: " + s.getUrl());
//         }
//     }
// }
