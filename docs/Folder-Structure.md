# Watcha Folder Structure

```
app/src/main/java/com/fraziym/soft/watcha/
├── MainActivity.kt               # Main entry point activity with PiP handling
├── WatchaApp.kt                  # Application class initializing Room & Repositories
├── data/                         # Data layer
│   ├── local/                    # Room entities, DAOs, Database, DataStore
│   ├── repository/               # MediaRepository & SettingsRepository
│   └── scanner/                  # MediaScanner for Android MediaStore
├── domain/                       # Domain layer
│   ├── ai/                       # Future AI Engine interfaces
│   └── model/                    # MediaItemModel domain object
├── player/                       # Media3 ExoPlayer engine wrapper
│   └── WatchaPlayerManager.kt
└── ui/                          # UI layer
    ├── components/               # Floating Glass Nav, Media Cards, Header Bar, Controls
    ├── navigation/               # NavHost, Routes, WatchaNavigation
    ├── screens/                  # Home, Library, Player, Favorites, Settings
    └── theme/                    # Color, Glassmorphism, Theme, Type
```
