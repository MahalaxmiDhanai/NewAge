# Top App Bar Navigation

## What Changed

Added a **top app bar with back arrow** to all secondary screens (Insights, Learn, Settings), just like standard Android apps.

## How It Works

### 1. **Dashboard (Home Tab)**
- No top bar (clean design as per Figma)
- Shows: Header with avatar, goals, FAQ, tips, bottom navigation

### 2. **Insights / Learn / Settings Tabs**
- **Top App Bar** with:
  - ← Back arrow button (left side)
  - Screen title (center, Serif italic font)
  - Cream background matching app theme
- "Coming soon" placeholder content

### 3. **Navigation Behavior**

| Action | Result |
|--------|--------|
| Tap Insights/Learn/Settings in bottom nav | Shows that screen with top bar + back arrow |
| Tap ← back arrow in top bar | Returns to Dashboard |
| Press device back button | Returns to Dashboard |
| Press back on Dashboard | Exits app |

## Code Structure

```kotlin
// HomeScreen.kt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceholderScreen(
    title: String,
    onBack: () -> Unit,  // ← New parameter
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { ... },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, ...)
                    }
                },
                ...
            )
        }
    ) { ... }
}

// In HomeScreen Composable:
BackHandler(enabled = selectedNavIndex != 0) {
    selectedNavIndex = 0  // Go back to Dashboard
}
```

## User Experience

✅ **Familiar Pattern**: Matches standard Android navigation (Gmail, YouTube, etc.)
✅ **Multiple Ways Back**: Top bar arrow OR device back button
✅ **Visual Feedback**: Clear back arrow indicates "you can go back"
✅ **Consistent Design**: Top bar uses app's Serif italic font + cream colors

## Visual Layout

```
┌─────────────────────────────┐
│  ←  Insights          (Top) │ ← New Top App Bar
├─────────────────────────────┤
│                             │
│      Coming soon            │ ← Content Area
│                             │
├─────────────────────────────┤
│  Home  Insights  Learn ...  │ ← Bottom Nav
└─────────────────────────────┘
```

## Testing

1. **From Dashboard:**
   - Tap "Insights" → See top bar with "← Insights" title
   - Tap "Learn" → See top bar with "← Learn" title  
   - Tap "Settings" → See top bar with "← Settings" title

2. **Back Navigation:**
   - Tap ← arrow → Returns to Dashboard ✓
   - Press device back → Returns to Dashboard ✓

3. **From Dashboard:**
   - Press device back → Exits app ✓

## Files Modified

- `app/src/main/java/com/bloom/app/ui/home/HomeScreen.kt`
  - Added `CenterAlignedTopAppBar` import
  - Added `BackHandler` for system back button
  - Updated `PlaceholderScreen` with top bar
  - Added `onBack` callback parameter

## Future Enhancement Ideas

When building actual Insights/Learn/Settings screens:
- Keep the same top bar pattern
- Add menu items to the top bar if needed (3-dot menu, search, etc.)
- Consider adding a subtitle under the title
- Could add custom actions (share, help, etc.)
