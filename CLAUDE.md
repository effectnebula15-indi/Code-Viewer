# CLAUDE.md

Native Android app: a JetBrains-style code viewer/editor with a project tree, tabs,
syntax highlighting, Markdown rendering and 5 themes. Kotlin + Jetpack Compose (Material 3).

## Golden rule
When you change the architecture, add a screen/component, or change build/run steps,
**update this file in the same commit.** Keep it a lookup table, not prose — under ~150 lines.
If something is obvious from reading the code, don't document it here.

## Build / run
```bash
export ANDROID_HOME=/home/user/android-sdk          # SDK lives here in the cloud env
gradle assembleDebug                                # -> app/build/outputs/apk/debug/app-debug.apk
cp app/build/outputs/apk/debug/app-debug.apk release/Code-Viewer.apk   # ship a copy
```
- JDK 17+, Gradle 8.14.x, AGP 8.7.0, Kotlin 2.0.21, compileSdk 34, minSdk 24.
- `gradlew` wrapper jar is committed; the system `gradle` also works.
- No unit tests yet. "Verify" = it compiles with no warnings + APK builds.

## Where things live  (app/src/main/java/com/codeviewer/app/)
| Area | Path | Notes |
|---|---|---|
| Entry | `MainActivity.kt` | Welcome vs IDE chosen by `rememberSaveable` state (no NavHost) |
| Screens | `ui/screens/` | `WelcomeScreen`, `FolderPickerScreen`, `IdeScreen` (the workspace) |
| Components | `ui/components/` | `ProjectTreePanel`, `CodeEditor`, `MarkdownView`, `EditorTabBar`, dialogs, `FileSearchDialog`, `FileIcons` |
| Theme | `ui/theme/` | `Theme.kt` = 5 ColorSchemes + `SyntaxColors` + `IdeColors`; `Color.kt`, `Font.kt`, `Type.kt` |
| Syntax | `syntax/` | `SyntaxHighlighter` + `languages/*` (one regex set per language) |
| Data | `data/` | `FileRepository` (file IO + create/delete), `ProjectStore` (recents), `EditorSettings` (font size, sort, custom order) |
| Utils | `util/` | `FileUtils` (ext→language, text detection), `PermissionHelper`, `Links` (URL detection) |
| Fonts | `res/font/` | JetBrains Mono (code) + Inter (UI) |

## Conventions / patterns
- All colours come from `MaterialTheme.colorScheme`, `LocalIdeColors.current` (chrome) and
  `LocalSyntaxColors.current` (code). Never hard-code hex in UI except the theme files.
- Code uses `JetBrainsMono`, UI uses `Inter` (`ui/theme/Font.kt`).
- IDE chrome (toolbar/status) are floating "bubbles"; editor tabs are a flat strip.
- IDE state (open tabs, expanded folders, active index) is newline-joined strings in
  `rememberSaveable` so it survives process death / recents resume.
- Editor font size, sort mode and custom file order persist via `EditorSettings` (DataStore).

## Common tasks (smallest edit that works)
- **Add a language**: new `syntax/languages/XxxLanguage.kt` (extensions + rules), register it in
  `SyntaxHighlighter.languages`, and map the extension in `FileUtils.getLanguageForExtension`.
  Friendly names (e.g. "kotlin") alias to extension keys in `SyntaxHighlighter.aliases`.
- **Add a theme**: add colours in `Color.kt`, then a `ColorScheme` + `SyntaxColors` + `IdeColors`
  and a branch in `CodeViewerTheme`/`AppTheme` (`Theme.kt`).
- **File operations**: `FileRepository` (createFile/createFolder/delete); tree menus live in
  `ProjectTreePanel`, dialogs in `IdeScreen`.
- **Tree sorting / custom order**: `SortMode` + sort logic in `ProjectTreePanel`; order persisted
  via `EditorSettings.setCustomOrderFor`.

## Working efficiently in this repo (keep token/context cost low)
- Read the specific file from the table above instead of scanning the tree.
- Edit surgically; rewrite a whole file only when changes are pervasive.
- Don't paste large build logs back — grep for `^e:`/`^w:`/`BUILD`.
