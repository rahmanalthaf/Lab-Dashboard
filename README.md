# Lab Dashboard — Fully Interactive, Database-Backed Android App

Kotlin + Jetpack Compose + Room. Every screen reads from and writes to a
real on-device SQLite database (via Room) — nothing here is static or
hardcoded. Add, edit, or delete a student/experiment/mark and every
screen (dashboard, chart, leaderboard) updates immediately because they
all observe the same database through reactive Flows.

## Professional / responsive additions in this version

- **Adaptive layout** — a bottom navigation bar on phones, and a side
  navigation rail on tablets or landscape (≥600dp wide), so the app
  uses the extra space instead of stretching a phone layout.
- **Dark mode** — follows the system light/dark setting automatically.
- **App icon** — a proper adaptive launcher icon instead of the
  default Android placeholder.
- **Splash screen** on launch.
- **CSV export** — tap the share icon in the top bar to generate a
  report (one row per student, one column per experiment, plus total %
  and grade) and send it via the Android share sheet (email, Drive,
  WhatsApp, etc). This fills the export gap from the previous version.
- **Duplicate roll-number validation** when adding/editing a student.
- **Snackbar confirmations** on add / edit / delete, so actions give
  visible feedback instead of just silently updating a list.
- **Sortable student list** — by name, roll number, or average score —
  with each student's live average and grade shown inline.
- **Per-experiment class average** shown on the Experiments screen.

## What changed vs. the previous build

The previous project's README admitted two known gaps: the score-entry
UI was stubbed as a comment, and the charts were static placeholder
shapes. Both are now implemented:

- **Score Entry** tab: a tappable grid (students × experiments) backed
  by Room. Tap any cell to enter/edit/clear that student's marks.
- **Dashboard chart**: a real bar chart driven by each student's live
  computed percentage — drawn with Compose Canvas, so it has no
  external charting dependency that could fail to resolve during a CI
  build.
- **Students / Experiments** tabs: full add / edit / delete, with a
  confirmation dialog before delete (marks tied to a deleted student
  or experiment are removed too, via Room's cascade delete).
- **Leaderboard**: auto-ranked by percentage, with a search box and a
  color-coded grade badge (O/A+/A/B+/B/C/F).

## Grading scale used

O ≥ 90%, A+ ≥ 80%, A ≥ 70%, B+ ≥ 60%, B ≥ 50%, C ≥ 40%, else F — based
on the sum of marks obtained across all experiments divided by the sum
of each experiment's max marks. This lives in one place
(`gradeForPercentage` in `LabViewModel.kt`) if you need different
cutoffs.

## Two starter students/experiments are pre-seeded

Only so the app isn't blank on first launch — delete or rename them,
they're not fixed data. Everything is editable through the UI.

## Note on the app icon

The launcher icon is an adaptive icon (vector-based, works on Android
8.0/API 26+, which covers the vast majority of devices). On older
Android versions it will fall back to the system's default icon rather
than showing custom artwork — the app itself is unaffected, this only
matters for the home-screen icon on very old devices.

## Building the APK

Same two options as before:

### Option A — build in the cloud, free, no install (recommended)
This project includes `.github/workflows/build-apk.yml`.
1. Create a free GitHub account and a new **empty** repo.
2. Upload this project's contents to it (drag-and-drop via "uploading
   an existing file", or `git init && git add . && git commit -m init
   && git remote add origin <url> && git push -u origin main`).
3. Go to the **Actions** tab → enable workflows if prompted → **Run
   workflow** (or just push, which triggers it automatically).
4. Wait ~3-5 minutes → open the finished run → **Artifacts** →
   download `app-debug-apk`.
5. Transfer the APK to your device and install (allow "install from
   unknown sources" if prompted).

Note: this workflow installs Gradle itself in the cloud runner (via
`gradle/actions/setup-gradle`) rather than relying on a bundled
`gradlew` wrapper — this .zip doesn't include the wrapper's binary jar,
so this is the more reliable path.

### Option B — Android Studio locally
1. Install Android Studio (Hedgehog or newer).
2. **File → Open** → select the unzipped `StudentLabDashboard` folder.
3. Let Gradle sync (accept any SDK download prompts).
4. **Build → Build Bundle(s)/APK(s) → Build APK(s)**, or click the
   green **Run ▶** with a device/emulator selected.
5. Find the APK at `app/build/outputs/apk/debug/app-debug.apk`.

## Project structure

```
app/src/main/java/com/labdashboard/app/
  data/          Room entities (Student, Experiment, Mark), DAOs, database
  repository/    LabRepository — single source of truth over the DAOs
  viewmodel/     LabViewModel — reactive derived state (percentages, grades, stats)
  ui/
    screens/     Dashboard, Students, Experiments, ScoreEntry, Leaderboard
    components/  Dialogs, stat cards, grade badge, bar chart
    theme/       Color / typography / Material3 theme
```

## Extending it further

- **Excel (.xlsx) export**: currently CSV rather than a binary Excel
  file, to keep the dependency surface small and CI-reliable. CSV
  opens fine in Excel/Sheets/Numbers if that's all you need; ask if
  you specifically want a native .xlsx via Apache POI instead.
- **Multiple sections/batches**: would mean adding a `Section` entity
  and a foreign key on `Student` — straightforward given the current
  structure.
- **Cloud sync**: current version is local-only (Room/SQLite on-device).
  Firebase Firestore is the most common path if multiple devices need
  to share the same data.
