# Bloom

A wellness companion app designed for women managing PCOS. Built with modern Android development practices, Bloom helps users track daily wellness goals, access health tips, and build sustainable habits.

## Overview

Bloom provides a clean, intuitive interface for tracking five core wellness areas: balanced meals, movement, hydration, relaxation, and quality sleep. The app features a personalized onboarding experience and integrates AdMob monetization while maintaining a smooth user experience.

## Features

**Onboarding Flow**  
Four-step introduction that collects user preferences and explains the app's value. Progress is persisted so returning users skip directly to the home screen.

**Daily Goal Tracking**  
Interactive goal cards let users increment progress with a tap. Progress persists throughout the day and automatically resets at midnight, giving users a fresh start each morning.

**Wellness Content**  
Daily tips and FAQ sections provide actionable health guidance. The tip card can be dismissed and stays hidden until the next day.

**Monetization**  
App open ads appear for returning users (never during first-time onboarding). Banner ads are positioned above the navigation bar with adaptive sizing.

## Technical Details

**Stack**  
- Kotlin with Jetpack Compose for the UI layer
- Material 3 design system with custom theming
- MVVM architecture pattern
- DataStore for local persistence
- Lifecycle-aware components throughout

**Project Structure**  
The codebase follows a standard Android app structure with clear separation between UI, business logic, and data layers. Reusable components are extracted into a dedicated package, and theme definitions use a centralized design system.

**Ad Implementation**  
The app open ad manager uses a state machine to handle preloading, eligibility checks, and lifecycle events. It includes debouncing to prevent double-shows during rapid app transitions. Banner ads are lifecycle-aware and gracefully handle load failures.

**Data Persistence**  
User preferences and goal progress are stored in DataStore. The app checks the last progress date on each launch to determine whether daily goals should reset, ensuring the midnight reset happens reliably even if the app isn't actively running at that time.

## Building the Project

**Requirements**  
- Android Studio Hedgehog or later  
- JDK 17  
- Gradle 8.x

**Build Commands**  
```bash
./gradlew assembleDebug
```

The output APK will be in `app/build/outputs/apk/debug/`

**Testing**  
```bash
./gradlew testDebugUnitTest
```

All 31 unit tests cover view models, data layer logic, and ad manager eligibility rules.

## Configuration

The debug build uses Google's test ad IDs. For a production release, add your AdMob credentials to `gradle.properties`:

```properties
ADMOB_APP_ID_RELEASE=ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX
ADMOB_APP_OPEN_RELEASE_ID=ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX
ADMOB_BANNER_RELEASE_ID=ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX
```

## Design

The app implements a custom design system with:
- Fraunces for display text (serif, variable weight)
- Plus Jakarta Sans for body text (sans-serif, variable weight)
- Warm cream background (#FCFAF8) with brown accents
- Rounded corners and soft shadows throughout

Fonts are loaded via Google Fonts Provider and require Google Mobile Services.

## Notes

This is a functional prototype demonstrating modern Android development patterns. The Insights, Learn, and Settings tabs are placeholders for future expansion. The navigation system properly handles back button behavior across all screens, including the bottom navigation.
