# Code-Viewer

A native Android application for browsing, viewing, and editing source code files directly on your device — with a **JetBrains-inspired IDE interface**.

Built with **Kotlin** and **Jetpack Compose**, using the **JetBrains Mono** and **Inter** typefaces.

## Features

### Projects
- Open any folder on your device as a project (JetBrains-style Welcome screen)
- Recent projects list for quick re-opening
- Collapsible project tree with file-type badges
- **Create and delete files and folders** — `+` in the tree header, or long-press any item for New File / New Folder / Delete
- **Close project** from the toolbar overflow menu (with an unsaved-changes prompt)

### IDE Workspace
- Floating "bubble" toolbar and status bar over an edge-to-edge editor for more readable space
- Editor tabs for multiple open files, with unsaved-change indicators
- Project tree panel that toggles open/closed
- JetBrains Mono for code, Inter for the interface

### Code Editor
- Syntax highlighting for 10 languages
- Line-number gutter that stays aligned while scrolling vertically and horizontally
- Edit mode with a contextual action bar: Select All, **Delete selection**, Undo, Save
- In-file search with match highlighting and navigation
- **Clickable links** — bare URLs in any file open in the browser

### Markdown
- `.md` files render as formatted Markdown in view mode (headings, bold/italic, inline code,
  fenced code blocks with syntax highlighting, lists, block quotes, rules and links)
- Switch to edit mode to edit the raw Markdown source

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
| Light | IntelliJ-style light theme |
| Dark | Darcula-style dark theme |
| Dark Purple | High-contrast dark with vivid purple accents |
| Ultra Dark | Pure AMOLED black |
| Dark Red | High-contrast dark with vivid red accents |

Each theme includes a matching syntax-highlighting palette and IDE chrome colours. Theme transitions are animated and the choice is persisted across app restarts.

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
- **Persistence:** Jetpack DataStore (theme + recent projects)
- **Architecture:** Single Activity; state-driven Welcome / IDE screens that survive process death
- **Fonts:** JetBrains Mono (code) + Inter (UI)
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)

## Project Structure

```
app/src/main/java/com/codeviewer/app/
├── MainActivity.kt          # Welcome ⇄ IDE state navigation
├── CodeViewerApp.kt
├── data/                    # FileRepository, ProjectStore
├── syntax/                  # Syntax highlighting engine
│   └── languages/           # Language definitions (regex-based)
├── ui/
│   ├── theme/               # 5 themes, colors, fonts, IDE chrome
│   ├── components/          # Tree, tabs, editor, markdown, file badges
│   └── screens/             # Welcome, FolderPicker, IdeScreen
└── util/                    # File utilities, permissions, link detection
```

## License

This project is open source.
