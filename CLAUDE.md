# PipePipeExtractor — Hướng dẫn cho Claude Code

## Bối cảnh dự án

Đây là **fork `dev.maxrave`** của PipePipeExtractor.

- **Origin (fork của anh):** `maxrave-dev/PipePipeExtractor`
- **Upstream (gốc):** `InfinityLoop1308/PipePipeExtractor`
- Fork đã **rename toàn bộ namespace**: `org.schabi.newpipe.extractor` → `dev.maxrave.pipepipe.extractor`
  (kèm Maven group `dev.maxrave`, proto `java_package` `dev.maxrave.pipepipe`).
- Định danh fork: version `v6.4.0`, group `dev.maxrave` (trong `build.gradle`).

## Nguyên tắc pull update từ upstream (QUAN TRỌNG)

Mỗi lần pull bản mới từ upstream `InfinityLoop1308` sẽ luôn gặp conflict, **chủ yếu do package rename**.
Khi resolve conflict, tuân thủ đúng thứ tự ưu tiên sau:

1. **Namespace LUÔN giữ `dev.maxrave.pipepipe`** — không bao giờ để sót `org.schabi.newpipe`
   (kể cả các dòng `import` nằm NGOÀI khối conflict do auto-merge kéo vào; phải quét sạch toàn repo:
   `grep -rn "org\.schabi\.newpipe" --include="*.java"` phải ra 0).
2. **Ưu tiên code của anh (HEAD)** ở chỗ logic xung khắc trực tiếp với feature riêng của fork.
3. **Nhận nâng cấp upstream** ở những file anh **chỉ rename** (không có logic riêng) — đó đúng là mục đích "pull new version".
4. **Bỏ import của dependency đã bị gỡ** (ví dụ `cache2k` đã bị upstream remove → import `org.cache2k.*` phải xoá, nếu không sẽ fail build).
5. Trước khi resolve một khối conflict, **verify symbol còn tồn tại** sau merge (method/field/constant) để tránh build gãy âm thầm — vì auto-merge có thể xoá định nghĩa mà code phía HEAD vẫn gọi.
6. **Java toolchain phải ≤ JVM của consumer.** App **SimpMusic** dùng lib này build bằng **JVM 21**.
   Upstream hay nâng JDK đột ngột (v5.1.1 nhảy Java 8 → 25 qua commit `1cce4d0c`): bytecode Java 25
   (class major 69) khiến SimpMusic (major 65) **không nạp được** → `UnsupportedClassVersionError` / dex fail.
   Mỗi lần merge PHẢI check `build.gradle` (`JavaLanguageVersion.of(...)`) + `jitpack.yml` (`openjdk...`)
   và **giữ ở Java 17**. SimpMusic chỉ consume artifact qua JitPack nên chỉ bytecode target ảnh hưởng;
   Gradle wrapper version không ảnh hưởng consumer (giữ `9.5.1` ok vì chạy được với JDK 17).

### Feature riêng của fork cần bảo vệ
- **WEB_REMIX client** (YouTube Music) để lấy Premium audio itag 141 (AAC 256kbps) và 774 (Opus 256kbps).
  Liên quan: `YoutubeStreamExtractor` (`fetchWebRemixJsonPlayer`, `webRemixCall`, `webRemixStreamingData`),
  `ClientsConstants` (`WEB_REMIX_HARDCODED_CLIENT_VERSION`), `InnertubeClientRequestInfo`.
- **WEB_REMIX được ƯU TIÊN SỐ 1 khi chọn stream** (kiến trúc từ v6.1.0): điểm gom stream cổ điển duy nhất
  là `YoutubeStreamExtractor.extractAdaptiveFormats()` — `collectAdaptiveItags(webRemixStreamingData, ...)`
  được gọi TRƯỚC `collectAdaptiveItags(configuredStreamingData, ...)`; dedup `containSimilarStream` giữ
  nguồn vào trước → itag Premium từ WEB_REMIX luôn thắng. Khi merge upstream PHẢI giữ webRemix quét trước.
- WEB_REMIX **chỉ được gọi khi có token** (`ServiceList.YouTube.getTokens()` không rỗng), chạy SONG SONG
  với `jsonPlayerCall` (client cấu hình được, mặc định `web_safari`) trong `onFetchPage`; await MỀM qua
  `awaitOptionalCall` — WebRemix fail/timeout không được phá extraction (client chính mới là bắt buộc).
- **CAVEAT SABR**: nhánh SABR-only (`selectedClient` = `mweb`/`web` + có `serverAbrStreamingUrl`) đọc CHỈ
  `configuredStreamingData` → **bypass WebRemix**. Client mặc định `web_safari` không đi SABR nên itag
  141/774 an toàn; nếu upstream/app sau này bật SABR cho client mặc định thì PHẢI port WebRemix tiếp.

### Tiền lệ đã chốt (merge upstream v5.1.1, commit 924f6457)
- `YoutubeStreamExtractor.onFetchPage`: **lai** — `fetchAndroidVRJsonPlayer` (upstream) khi token rỗng
  **+** `fetchWebRemixJsonPlayer` (fork) khi có token. Giữ fix `isFetchDislike` của upstream.
- Channel tab (`YoutubeChannelTabExtractor`, `YoutubeChannelTabLinkHandlerFactory`):
  **nhận nâng cấp upstream** (channel tab sorting #62 + channel search #65), bỏ bản cũ dùng `YoutubeFilters`.
- `localization` trong `onFetchPage`: theo upstream `new Localization("en")`.
- **Java toolchain hạ `25` → `17`** (`build.gradle` `JavaLanguageVersion.of(17)` + `jitpack.yml` `openjdk17`)
  để SimpMusic (JVM 21) nạp được bytecode. Gradle giữ `9.5.1`. Bump version fork → `v6.0.1`.

### Tiền lệ đã chốt (merge upstream v5.2.2-plus, commit 105ce8a8, fork v6.1.0)
- Upstream viết lại `YoutubeStreamExtractor` theo mô hình **single-client**: `configuredStreamingData`
  (chọn qua `NewPipe.setYoutubePlayerClient`, mặc định `web_safari`; chỉ nhận `mweb/web_safari/android_vr/tv_downgraded`).
  Xoá `safariStreamingData`/`androidStreamingData`/`tvHtml5SimplyEmbed*` và `NewPipe.isForceSabr()/setForceSabr()`
  — **SimpMusic còn gọi 2 hàm isForceSabr/setForceSabr sẽ fail compile khi lên v6.1.0**.
- WEB_REMIX được port theo mô tả ở mục trên (fetch song song + `extractAdaptiveFormats` webRemix-first).
- Xoá legacy JS pipeline theo upstream: `YoutubeSignatureUtils`, `YoutubeThrottlingParameterUtils`,
  `utils/JavaScript`, `utils/jsextractor/*`, dep `rhino` — thay bằng injectable `YoutubeJavaScriptDecoder`.
- KHÔNG nhận block `tasks...enabled = false` (disable test/check) của upstream root `build.gradle`
  — giữ task test để chạy `Itag774Test`.
- Commit `a5bb6e5b`: debug logs `YT_PERF` (per-call round-trip, `webRemix.response`, `streams.path`,
  itag trước/sau dedup, `extraction.done` = tổng thời gian + kết quả) — thuần additive, format
  `YT_PERF videoId=... stage=... durationMs=... detail=...`.

### Tiền lệ đã chốt (merge upstream v5.2.4-plus, commit 208e43b1, fork v6.2.0)
- Default client upstream đổi `web_safari` → **`android_vr`** (nhận theo upstream). `setYoutubePlayerClient`
  KHÔNG còn nhận `web_safari` — giá trị lạ **âm thầm fallback `android_vr`** (không lỗi compile).
  `android_vr` ∉ {mweb, web} nên nhánh SABR-only không kích hoạt → WebRemix vẫn ưu tiên ở path classic.
  SimpMusic không gọi `setYoutubePlayerClient` nên không ảnh hưởng.
- PoToken session-bound (opt-in qua `NewPipe.setYoutubeSessionPoTokenProvider`, không set = no-op):
  chỉ gắn cho client chính (web/mweb/configured); WebRemix call KHÔNG gắn PoToken (đi đường
  `getWebRemixPostResponseAsync` riêng, không qua `getJsonPlayerResponseAsync`).
- Test deps theo upstream: junit-jupiter aggregate `5.13.4` + junit-platform-launcher `1.13.4`;
  giữ `testRuntimeOnly org.json` (desktop test JVM). Dep mới `bcprov-jdk18on:1.85` (thay commons-codec).
- Logs YT_PERF fork giữ nguyên 100% (chỉ log `streams.path path=hls` dời theo điều kiện HLS mới).

### Tiền lệ đã chốt (merge upstream v5.2.5-plus, commit f8982ca9, fork v6.3.0)
- Upstream XÓA endpoint `android_vr`; default client mới là **`visionos`** (`setYoutubePlayerClient` chỉ nhận
  `mweb/visionos/tv_downgraded`, giá trị lạ âm thầm fallback `visionos`). Nhánh SABR-only giờ CHỈ còn `mweb`
  → `visionos` đi path classic → WebRemix vẫn ưu tiên. Có thêm call **Android Reel** fallback cho muxed video.
- Điểm gom stream classic đổi tên `extractAdaptiveFormats` → **`extractDirectFormats`** — WebRemix vẫn quét
  TRƯỚC configured (bất biến giữ qua tên mới).
- **SABR rewrite + drop media3** (#92): xóa ~40 class SABR cũ (Probe/RequestBuilder/StreamState/Format...),
  bỏ toàn bộ segment-cache/pump API của `YoutubeSabrSession` (thay bằng `requestOnce → RequestResult`);
  SABR exceptions/media/protocol dời vào subpackage `sabr.exception/.media/.protocol`.
  `NewPipe.setYoutubeSessionPoTokenProvider` bị thay bằng `setYoutubePoTokenResolver` (SimpMusic không dùng → không ảnh hưởng).
- Logs YT_PERF giữ 100%; thêm `call.jsonPlayer.done client=visionos|android_reel`;
  `streams.audio/video.final` dời xuống SAU fallback Android Reel.

### Tiền lệ đã chốt (merge upstream v5.4.0-plus, commit 6f170e44, fork v6.4.0)
- Upstream 50 commit (v5.3.0 → v5.4.0 + ~20 commit sau tag). Default client **vẫn `visionos`** (upstream đổi sang
  `tv_samsung` một đợt rồi sửa lại ở `2d8fae9e`); `setYoutubePlayerClient` vẫn chỉ nhận `mweb/visionos/tv_downgraded`.
  Client mới **`tv_samsung`** (TVHTML5 + deviceMake Samsung/Tizen) đi qua `fetchConfiguredJsonPlayer`; helper
  `isTvClient()` = `tv_downgraded|tv_samsung` thay cho so sánh `"tv_downgraded"` ở nhánh HLS live.
  Nhánh SABR-only vẫn CHỈ `mweb` → `visionos` đi `extractDirectFormats` → WebRemix vẫn ưu tiên.
- Conflict: 47 UU, trong đó **45 rename-only** (nhận upstream + sed namespace), 2 thật: `build.gradle` (giữ
  `dev.maxrave`, bump v6.4.0, KHÔNG nhận block disable test/check) và `YoutubeStreamExtractor` đúng 1 hunk
  (điều kiện HLS `isTvClient(selectedClient)` của upstream + giữ log fork `streams.path path=hls`).
  `YoutubeParsingHelper` auto-merge sạch (upstream chỉ THÊM method: `getChannelId*`, `getJsonPlayerResponse`,
  `createJsonPlayerBody` overload `reloadToken`, `createMwebPlayerRequest`). 6 file Bilibili auto-merge kéo
  `import org.schabi.*` vào ngoài khối conflict → sed (đúng bẫy mục 1). Upstream xoá
  `NiconicoTopLivesInfoItemExtractor` (fork chỉ rename) → nhận xoá.
- API mới (SimpMusic không bắt buộc dùng): bộ lọc keyword/chặn channel id (`CommentsFilterConfig`, `KeywordMatcher`,
  `ServiceScopedId`, `StreamingService.setCommentsFilterConfig`), `StreamInfo.isSupportBulletComments`,
  `YoutubeStreamExtractor.fetchSabrInfoForReload` + `SabrInfoReloader`. **`StreamingService.fetchDislike` default
  đổi `true → false`** (`0dfb2d88`): app không gọi `setFetchDislike(true)` thì extractor không fetch dislike nữa.
- SimpMusic đã inject local decoder (`YoutubeApiDecoder.setLocalDecoder`, `YoutubeJavaScriptDecoder`,
  `YoutubeJavaScriptPlayerManager`): ba file này upstream KHÔNG đổi đợt này; `StreamInfo.getInfo(service, url)`
  và `NewPipe.init(downloader)` giữ chữ ký → app compile sạch với v6.4.0.
- Logs YT_PERF giữ 100% (inventory stage và 39 lời gọi `logPerformance` khớp main); `call.jsonPlayer.done
  client=tv_samsung` tự có qua log sẵn trong `fetchConfiguredJsonPlayer`.
- Verify 17/10/2026 trên branch: `:extractor:compileJava` BUILD SUCCESSFUL; `Itag774Test` 2/2 PASSED, có cookie Premium thì
  `webRemix.response valid formats=18` và `streams.audio.final itags=[774,141,251,140,250,249,139]` (path classic, client visionos).

## Quy trình làm việc

- **KHÔNG tự build / compile / chạy gradle.** Anh tự build verify trong Android Studio.
  Claude chỉ verify bằng `grep`/đọc code (marker, namespace, symbol).
- Merge upstream làm trên **branch riêng** (vd `merge/upstream-vX.Y.Z`), commit trên branch,
  để anh build verify TRƯỚC, OK rồi mới đưa vào `main`.
- **Commit message thuần** — KHÔNG co-author footer, KHÔNG "Generated with Claude".
