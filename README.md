# Recipe Vault 🍲

A 26-day structured learning project built to deepen hands-on skills in **Jetpack Compose** and **Clean Architecture** on Android — using a real public API, not a toy dataset.

## Overview

Recipe Vault is a native Android app that lets users search, browse, and favorite recipes sourced from [TheMealDB](https://www.themealdb.com/) free API. The project is intentionally scoped as a guided, day-by-day build to practice modern Android architecture patterns end-to-end: UI state modeling, reactive search, type-safe navigation, offline-first persistence, and dependency injection.

- **Target device:** Samsung SM-T540 tablet (Android 12+)
- **Package:** `com.kiturk3.recipevault`
- **Data source:** [TheMealDB](https://www.themealdb.com/api.php) (free tier)

## Tech Stack

| Layer | Technology |
|---|---|
| UI | Jetpack Compose, Navigation-Compose 2.8.x (type-safe `@Serializable` routes) |
| DI | Hilt |
| Local persistence | Room |
| Networking | Retrofit |
| Async / reactive | Kotlin Coroutines & Flow |
| Language | Kotlin |

## Architecture

Recipe Vault follows **Clean Architecture** with clear separation of concerns:

```
com.kiturk3.recipevault
├── data/
│   └── repository/        # RecipeRepositoryImpl — concrete data layer implementation
├── domain/
│   ├── repository/        # RecipeRepository — interface contract
│   └── usecase/           # Business logic, single-responsibility use cases
└── presentation/ (ui)/
    ├── screens/            # Composable screens (list, detail, favorites)
    ├── viewmodel/          # ViewModels exposing RecipeUiState
    └── navigation/         # Type-safe nav graph
```

### Key architectural decisions

- **UI State:** A sealed `RecipeUiState` class (`Loading` / `Error` / `Success`) drives all screen rendering.
- **Search:** A `StateFlow<String>` query pipeline using `flatMapLatest` + `debounce(300ms)` for responsive, non-spammy live search against the API.
- **Navigation:** Type-safe Navigation-Compose routes defined with `@Serializable`, with arguments retrieved via `SavedStateHandle.toRoute<RecipeDetailRoute>()`.
- **Theming:** `dynamicColor = false` to enforce consistent brand colors instead of Android 12+ Material You wallpaper-based theming.
- **Persistence:** Room with `fallbackToDestructiveMigration()` during active development (favorites survive process death).

## Features

- ✅ Live recipe data from TheMealDB
- ✅ Debounced, reactive search-as-you-type
- ✅ Per-item favoriting with Room-backed persistence (survives process death)
- ✅ Type-safe list → detail navigation
- ✅ Configuration-change survival (rotation, etc.)
- 🚧 Offline-first caching layer (Room as source of truth) — in progress

## Getting Started

### Prerequisites
- Android Studio (latest stable)
- JDK 17+
- Android SDK 31+ (Android 12) as minimum target

### Setup
1. Clone the repo:
   ```bash
   git clone https://github.com/kiturk3/recipe-vault.git
   ```
2. Open in Android Studio and let Gradle sync.
3. Run on an emulator or physical device (Android 12+ recommended; primary test target is a Samsung SM-T540).

### Build notes
- Uses the Gradle version catalog (`libs.versions.toml`) for all plugin versions, including KSP (`2.2.10-2.0.2`) and `kotlin-serialization`, applied via `alias(libs.plugins.kotlin.serialization)` — avoid hardcoding plugin versions directly in module `build.gradle.kts` files.

## Project Status

This is an active, day-by-day learning build. Progress so far:

- **Days 1–15:** Core architecture, networking, search, navigation, and favorites implemented and verified working end-to-end.
- **Day 16:** Offline-first caching with Room — provided, pending final verification.

## Roadmap / Ideas for Later

- Full offline-first sync strategy
- Custom Canvas-drawn loading spinner via `rememberInfiniteTransition` (explored, not yet implemented)
- Expanded test coverage (unit tests for use cases, ViewModel tests)

## License

Personal learning project — license TBD.
