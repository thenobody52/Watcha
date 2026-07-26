# Architecture Documentation

Watcha follows **Clean Architecture**, **MVVM (Model-View-ViewModel)**, and the **Repository Pattern**.

```
                         +-------------------+
                         |   UI Layer        |
                         | (Jetpack Compose) |
                         +---------+---------+
                                   |
                                   v
                         +-------------------+
                         |   ViewModels      |
                         | (StateFlow/UiState|
                         +---------+---------+
                                   |
                                   v
                         +-------------------+
                         |  Repository Layer |
                         | (Data Streams)    |
                         +----+---------+----+
                              |         |
               +--------------+         +--------------+
               v                                       v
     +--------------------+                  +--------------------+
     |    Local DAO       |                  |    Media Scanner   |
     |  (Room Database)   |                  | (Android MediaStore|
     +--------------------+                  +--------------------+
```

## Layers

- **UI Layer (`ui/`)**: Pure Jetpack Compose views with glassmorphic cards and floating navigation bars.
- **Data Layer (`data/`)**: Room database entities, DAOs, DataStore preferences, and `MediaScanner` querying `MediaStore`.
- **Domain Layer (`domain/`)**: Pure Kotlin data models (`MediaItemModel`) and Future AI Engine architecture specifications (`FutureAiEngine`).
- **Player Engine (`player/`)**: Encapsulates `androidx.media3.exoplayer.ExoPlayer` instance, speed, aspect ratio modes, sleep timers, and PiP configuration.
