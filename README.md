# 🌸 Bloom — PCOS Wellness App

A complete Android app built with Kotlin + Jetpack Compose (Material 3) for managing PCOS daily wellness goals.

---

## Setup & Build

### Requirements
- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK 17** (bundled with Android Studio)
- **minSdk 24**, **targetSdk 35**
- Internet connection (for Gradle dependency download + Google Fonts at runtime)

### Build & Run
```bash
# Debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew testDebugUnitTest

# Install on connected device/emulator
./gradlew installDebug
```

On Windows (PowerShell):
```powershell
.\gradlew assembleDebug
.\gradlew testDebugUnitTest
```

The debug APK will be at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Swapping in Real AdMob IDs

1. Open `gradle.properties` (gitignored — create locally).
2. Add:
```properties
ADMOB_APP_ID_RELEASE=ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX
ADMOB_APP_OPEN_RELEASE_ID=ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX
ADMOB_BANNER_RELEASE_ID=ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX
```
3. The `release` build type in `app/build.gradle.kts` reads these automatically.
4. **Never commit real IDs** — `gradle.properties` is gitignored.

---

## Architecture

### Layers

```
UI (Compose screens)
    ↕  collectAsStateWithLifecycle
ViewModels (state holders, no business logic in composables)
    ↕  StateFlow / suspend functions
Repositories (HomeRepository, UserRepository)
    ↕  DataStore Preferences / static data
Data layer (OnboardingPreferences)
```

### Package Diagram

```
com.bloom.app/
├── BloomApplication.kt        # Manual DI, lifecycle setup
├── MainActivity.kt             # Edge-to-edge, NavGraph host
├── data/
│   ├── OnboardingPreferences  # DataStore keys & read/write
│   ├── UserRepository         # Name, onboarding flag, cache
│   └── HomeRepository         # FAQs, default goals (static)
├── ads/
│   ├── AdConfig               # BuildConfig field aliases
│   ├── AdsInitializer         # MobileAds.initialize wrapper
│   ├── AppOpenAdManager       # Full lifecycle state machine
│   ├── BannerAdView           # Compose adaptive banner
│   └── AdAnalytics            # Interface + Logcat impl
├── ui/
│   ├── theme/                 # Color, Type, Shape, Theme, Dimens
│   ├── components/            # BloomButton, GoalRow, RingIcon,
│   │                          #   FlowerLogo, SectionTitle, BottomBar
│   ├── splash/                # SplashScreen composable
│   ├── onboarding/            # OnboardingScreen + 4 step composables
│   └── home/                  # HomeScreen, HomeViewModel, HomeUiState
│       └── components/        # TodayGoalsCard, FaqPager,
│                              #   QuickActionCards, TodayTipCard
└── navigation/
    ├── Routes                 # Route constants
    └── NavGraph               # NavHost with composable destinations
```

### Data Flow

1. `BloomApplication` creates all singletons (repositories, AppOpenAdManager).
2. `MainActivity` sets Compose content and hands off the NavGraph.
3. `SplashScreen` reads `UserRepository.onboardingCompletedCache` to decide destination.
4. `OnboardingViewModel` writes name + completion flag via `UserRepository` → `OnboardingPreferences` (DataStore).
5. `HomeViewModel` reads from `UserRepository` (name) and `HomeRepository` (static data). Goal interaction is pure in-memory state mutation.

---

## Ad Implementation

### Test IDs Used

| Ad Type      | Test ID |
|-------------|---------|
| App ID       | `ca-app-pub-3940256099942544~3347511713` |
| App Open     | `ca-app-pub-3940256099942544/9257395921` |
| Banner       | `ca-app-pub-3940256099942544/9214589741` |

All IDs are in `BuildConfig` fields set per build type in `app/build.gradle.kts`.

### App Open Ad State Machine

```
App launch
    │
    ▼
BloomApplication.onCreate()
    ├── MobileAds.initialize() [background thread]
    └── appOpenAdManager.loadAd()  ← starts preloading

SplashScreen
    ├── Reads onboarding state
    ├── First-time user → skip ad, go to Onboarding
    └── Returning user → wait up to 3 s for ad
              ├── Ad ready → markColdStartHandled(), navigate Home,
              │             show ad (called from splash LaunchedEffect)
              └── Timeout → navigate Home directly

ProcessLifecycleOwner.onStart (foreground events)
    ├── coldStartHandled == true → skip once, clear flag
    ├── Debounce 1 s guard
    ├── isEligible() == false → skip (first-time user, onboarding in progress)
    └── showAdIfAvailable() → show if loaded, preload next after dismiss

showAdIfAvailable(activity, onComplete)
    ├── isShowingAd == true → onComplete() immediately
    ├── No valid / expired ad → loadAd(), onComplete()
    ├── !isEligible() → onComplete()
    └── Show ad
          ├── onAdShowed → isShowingAd = true
          ├── onAdDismissed → isShowingAd = false, loadAd(), onComplete()
          └── onAdFailedToShow → isShowingAd = false, loadAd(), onComplete()
```

**Eligibility rule:** `onboardingCompletedCache.value == true`
- This is a non-blocking StateFlow cache, never blocks the main thread.
- Updated immediately when onboarding completes.
- First-time users never see an App Open Ad — including foreground events during onboarding.

**Ad expiry:** 4 hours from load time. Expired ads are discarded and a fresh load is triggered.

**Activity safety:** Current activity tracked via `WeakReference`, cleared in `onActivityDestroyed` to prevent memory leaks during rotation.

### Banner Ad

- Wrapped in `BannerAdView` composable using `AndroidView`.
- Width computed from `LocalConfiguration.current.screenWidthDp` — no hardcoded values.
- `AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize()` used for adaptive sizing.
- Height reserved only after ad loads (no pre-reserved gap, no jump).
- Slot collapses to zero height on failure.
- Lifecycle-aware: pause/resume/destroy via `DisposableEffect` + `LifecycleEventObserver`.
- `AdView` is recreated on orientation change (screen width changes).

### Analytics

`AdAnalytics` interface with `log(event, params)`. Current implementation: `LogcatAdAnalytics` with tag `"BloomAds"`. Swappable for Firebase Analytics by providing a new implementation — no call sites need to change.

---

## Manual Test Scenarios

### 1. First Launch
- Clear app data / fresh install.
- Expect: Splash → Onboarding step 1 → … step 4 → Home. **No App Open Ad at any point.**
- Verify: No ad shown during or after onboarding first time.

### 2. Second Launch (returning user)
- After completing onboarding, kill and relaunch the app.
- Expect: Splash (up to 3 s wait) → App Open Ad (if loaded) → Home.
- With test IDs, the test App Open Ad should appear.

### 3. Airplane Mode
- Enable airplane mode, then launch.
- Expect: Ad fails silently, splash completes within MIN_SPLASH_MS, app navigates normally.
- No crash, no empty ad slot.

### 4. Background → Foreground (returning user)
- Open app (post-onboarding), press Home button, wait ~2 s, re-open.
- Expect: App Open Ad shown on foreground return.
- Subsequent foreground events: ad shown again after 1 s debounce (if a new ad has loaded).

### 5. During Onboarding
- Background the app mid-onboarding, re-open.
- Expect: **No App Open Ad** — user hasn't completed onboarding yet.

### 6. Banner Ad
- Navigate to Home Dashboard.
- Expect: Banner slot appears above the bottom nav once the ad loads.
- If no internet: slot collapses, content flows naturally without gap.

### 7. Rotation
- Rotate device on Home screen.
- Expect: No crash, banner reloads, pager state preserved via `rememberSaveable`.

---

## Assumptions & Limitations

| Item | Note |
|------|------|
| **Fonts** | Fraunces + Plus Jakarta Sans loaded via Google Fonts provider at runtime. Requires GMS. Falls back gracefully to system serif/sans if unavailable. |
| **Insights / Learn / Settings tabs** | Placeholder "Coming soon" screens — outside task scope. |
| **Static sample data** | FAQs and default goals are static. No persistence of goal progress between sessions. |
| **Reminders** | Selected reminder time stored in DataStore but no actual notification is scheduled (out of scope). |
| **First-time user ad decision** | The spec briefly mentions an ad between splash and onboarding but the App Open Ad section explicitly says first-time users must not be interrupted. This implementation follows the latter rule. |
| **Light theme only** | Dark theme not implemented. Noted here as a future enhancement. |
| **Test AdMob IDs** | All IDs are Google test IDs. Real IDs must be provided via `gradle.properties` before release. |
| **Goal progress persistence** | Goal progress (tapping rows) is in-memory only, resets on app restart. Persistent tracking is out of scope for this build. |
| **Cycle / Symptom screens** | Quick-action cards are present but tapping them does nothing (navigation to detail screens is out of scope). |
| **Process death** | DataStore preferences survive process death. In-memory goal state does not. |
| **WCAG / Accessibility** | `contentDescription` added to interactive elements. Full WCAG validation requires manual testing with TalkBack. |

---

## Build Output

After `./gradlew assembleDebug`:
```
app/build/outputs/apk/debug/app-debug.apk
```

Copied to project root:
```
app-debug.apk
```
