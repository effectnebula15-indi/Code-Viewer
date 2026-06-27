# Code-Viewer

A native Android application for browsing, viewing, and editing source code files directly on your device.

Built with **Kotlin** and **Jetpack Compose** using **Material 3** design.

## Features

### File Browser
- Navigate the device file system with a clean, intuitive interface
- Breadcrumb navigation for quick path traversal
- File type icons (folders, code files, generic files)
- Sorted listing: directories first, then files alphabetically
- File size display

### Code Editor
- Syntax highlighting for 10 languages
- Line numbers gutter
- Edit mode with save and undo
- In-file search with match navigation
- Horizontal scrolling for long lines

### Supported Languages
| Language | Extensions |
|----------|-----------|
| Kotlin | `.kt`, `.kts` |
| Java | `.java` |
| Python | `.py` |
| JavaScript | `.js`, `.jsx` |
| TypeScript | `.ts`, `.tsx` |
| HTML | `.html`, `.htm` |
| XML | `.xml`, `.svg` |
| JSON | `.json` |
| CSS | `.css`, `.scss`, `.less` |
| Generic | `.txt`, `.md`, `.log`, etc. |

### 5 Color Themes
| Theme | Description |
|-------|-------------|
| Light | White background, dark text |
| Dark | Standard dark theme |
| Dark Purple | Dark background with purple accents |
| Ultra Dark | Pure AMOLED black |
| Dark Red | Dark background with red accents |

All theme transitions are animated. Theme preference is persisted across app restarts.

## Screenshots

*Install the APK to see the app in action.*

## Installation

1. Download the latest APK from the [Releases](https://github.com/effectnebula15-indi/Code-Viewer/releases) page
2. Enable "Install from unknown sources" on your Android device
3. Install the APK

**Requirements:** Android 7.0 (API 24) or higher

## Building from Source

```bash
# Clone the repository
git clone https://github.com/effectnebula15-indi/Code-Viewer.git
cd Code-Viewer

# Set up Android SDK path
echo "sdk.dir=/path/to/your/android-sdk" > local.properties

# Build debug APK
./gradlew assembleDebug

# APK will be at: app/build/outputs/apk/debug/app-debug.apk
```

**Build requirements:**
- Java 17+
- Android SDK with API 34
- Gradle 8.14+

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Navigation:** Jetpack Navigation Compose
- **Persistence:** Jetpack DataStore
- **Architecture:** Single Activity, Composable-based screens
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)

## Project Structure

```
app/src/main/java/com/codeviewer/app/
├── MainActivity.kt
├── CodeViewerApp.kt
├── data/                  # File system operations
├── syntax/                # Syntax highlighting engine
│   └── languages/         # Language definitions (regex-based)
├── ui/
│   ├── theme/             # 5 themes, colors, typography
│   ├── navigation/        # App navigation graph
│   ├── components/        # Reusable UI components
│   └── screens/           # FileBrowser, CodeViewer, Settings
└── util/                  # File utilities, permissions
```

## License

This project is open source.
