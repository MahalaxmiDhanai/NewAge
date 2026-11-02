# Bloom - PCOS Wellness App

Android app for daily PCOS wellness tracking with onboarding flow, goal management, and AdMob integration.

## Tech Stack

- Kotlin 100%
- Jetpack Compose (Material 3)
- MVVM architecture
- DataStore Preferences
- AdMob SDK (test IDs)

## Build

```bash
./gradlew assembleDebug
```

Debug APK: `app/build/outputs/apk/debug/app-debug.apk`

## Setup

### Requirements
- Android Studio Hedgehog+
- JDK 17
- minSdk 24, targetSdk 35

### AdMob IDs

Debug build uses Google test IDs. For release, add to `gradle.properties`:

```properties
ADMOB_APP_ID_RELEASE=ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX
ADMOB_APP_OPEN_RELEASE_ID=ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX
ADMOB_BANNER_RELEASE_ID=ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX
```

## Architecture

```
UI Layer (Compose)
    ↓ collectAsStateWithLifecycle
ViewModels (state management)
    ↓ StateFlow / suspend functions
Repositories (data access)
    ↓ DataStore / static data
Data Layer
```

### Package Structure

```
com.bloom.app/
├── data/           # Repositories, DataStore, models
├── ads/            # AdMob management
├── ui/
│   ├── theme/     # Design system
│   ├── components/  # Reusable UI
│   ├── splash/
│   ├── onboarding/
│   └── home/
└── navigation/
```

## Ad Implementation

### App Open Ad

State machine that:
- Preloads ad after SDK init
- Shows on returning users (3s timeout on splash)
- Never shows for first-time users during onboarding
- Shows on background→foreground transitions
- Reloads after dismissal/failure
- Uses 1s debounce to prevent double-shows

Eligibility: `onboardingCompletedCache == true`

### Banner Ad

- Adaptive width based on screen size
- Fixed slot above bottom nav
- Collapses to 0dp if load fails
- Lifecycle-aware (pause/resume/destroy)

## Data Persistence

Onboarding state stored in DataStore:
- `onboarding_complete`: Boolean
- `user_name`: String  
- `reminder_time`: String

## Testing

Unit tests: 31 passing

```bash
./gradlew testDebugUnitTest
```

## Notes

- Fonts loaded via Google Fonts provider (requires GMS)
- Goal progress is in-memory only
- Insights/Learn/Settings tabs are placeholders
- Navigation back button properly handles all screens
