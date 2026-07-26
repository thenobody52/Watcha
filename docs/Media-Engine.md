# Media Engine Specifications

Watcha leverages `androidx.media3.exoplayer` to deliver native video and audio playback capabilities.

## Technical Highlights

- **Hardware Acceleration:** Uses `DefaultRenderersFactory` with `EXTENSION_RENDERER_MODE_ON` for hardware/software decoding fallback.
- **Aspect Ratio Scaling:**
  - `RESIZE_MODE_FIT`: Default aspect ratio.
  - `RESIZE_MODE_ZOOM`: Crop to fill screen without letterboxing.
  - `RESIZE_MODE_FILL`: Stretch to fit display dimensions.
- **Sleep Timer Engine:** Uses Kotlin Coroutine timer loops to automatically invoke `exoPlayer.pause()` upon timer expiration.
- **MediaSession Integration:** Exposes system controls to notification bars, lockscreen widgets, and bluetooth headset buttons.
