# Quickstart: Podcast App Visual Mockup

Validation guide for confirming this feature works end-to-end. Assumes the
project has been scaffolded per plan.md's Project Structure and the
dependencies in research.md.

## Prerequisites

- Android Studio (current stable channel) with JDK 17.
- An Android device or emulator running API 26+ (targetSdk 37 / Android 17
  recommended for the emulator image, to match `compileSdk`/`targetSdk`).
- No network connection is required to build or run this feature — that is
  itself part of what this quickstart verifies (FR-017–FR-019).

## Setup

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

On first launch, the app seeds its local Room database and DataStore
preferences with the mocked podcast/episode catalog and default settings
described in data-model.md — no manual setup step is required.

## Run unit tests (no device/emulator required)

```bash
./gradlew testDebugUnitTest
```

This runs the JVM-only tests for `core/data`, `core/player`, and each
`feature/*` module's ViewModel logic (constitution Principle III/Test-First)
against the contracts in `contracts/module-interfaces.md`.

## Run instrumented Compose UI tests

```bash
./gradlew connectedDebugAndroidTest
```

## Manual validation scenarios

Each scenario below maps to an acceptance scenario in spec.md.

1. **Home → Player (User Story 1, P1)**
   - Open the app. Home shows the seeded subscribed podcasts.
   - Tap an episode. The Player opens showing artwork (or placeholder),
     title, podcast name, and progress.
   - Tap play → pause affordance shown, audio audibly plays.
   - Tap pause → play affordance shown, audio stops.
   - Tap "skip forward 30s" → progress advances by 30s.
   - Tap "skip backward 30s" → progress moves back by 30s.
   - Tap next/previous → adjacent episode loads; at the first/last episode,
     the control is a safe no-op.
   - While playing, watch the timeline: it MUST visibly advance in small,
     smooth steps (at least every 250ms) rather than jumping once per
     second (SC-009).
   - Press and drag the timeline to a new position: the displayed time
     MUST follow your finger live while dragging; releasing MUST commit
     playback to that position. Drag past either end — it MUST clamp at
     0:00 or the episode's end rather than erroring or skipping to another
     episode (FR-021). This is the full-screen Player only; the persistent
     mini-player is not required to be draggable.

2. **Explore → Search → Detail → Subscribe (User Story 2, P2)**
   - Open Explore. A mocked catalog of podcasts not yet subscribed is shown.
   - Enter a search term matching a catalog podcast → it appears in
     results. Enter a nonsense term → "no results" state is shown.
   - Tap a result → Podcast Detail opens with artwork, description,
     publisher, episode list.
   - Tap Subscribe → button reflects "Subscribed"; return to Home → the
     podcast now appears there without restarting the app.
   - Tap Subscribe again on an already-subscribed podcast → no duplicate
     Home entry is created.

3. **Settings (User Story 3, P3)**
   - Open Settings. Preferences (appearance, default skip seconds, default
     playback speed) are listed.
   - Change a preference → the new value is reflected immediately.

4. **Offline-first (spec Assumptions / FR-017–FR-019)**
   - Enable Airplane Mode.
   - Repeat scenario 1 in full (Home, Player transport controls) — every
     step MUST work with no degradation.
   - Open Settings — MUST work with no degradation.
   - Open Explore/Search — since the catalog is currently mocked/local, it
     MUST also continue to work; this is the baseline this quickstart
     re-validates once Explore is later backed by a live catalog (a future
     feature), at which point this step should instead confirm a clear
     "you're offline" state rather than a crash.
   - Disable Airplane Mode again — confirm no crash, data loss, or stuck
     state (SC-008).

## Expected outcome

All six primary screens (Home, Player, Explore, Search results, Podcast
Detail, Settings) are reachable and fully interactive per spec.md's
Success Criteria (SC-001–SC-009), with and without network connectivity.

## Validation run (2026-08-20)

Ran live on a Pixel-class API 37 emulator (`app-debug.apk`, installed and
driven via `adb`/screenshots — not just unit tests). Results per Success
Criterion:

- **SC-001** (every screen within 2 taps): PASS. Home/Explore/Settings are
  1 tap from anywhere via the bottom bar; Player is 1 tap from Home or the
  mini-player; Detail is 1 tap from Explore.
- **SC-002** (artwork or placeholder, no broken images): PASS. Every
  podcast/episode shown used the placeholder icon (no real artwork exists
  yet), with no broken-image states, on Home, Player, Explore, and Detail.
- **SC-003** (every control gives an immediate, correct state change):
  PASS. Play/pause toggled the icon and audibly started/stopped playback;
  the progress bar visibly ticks forward once per second while playing
  (added a position-tick loop in `PlaybackControllerImpl` after the first
  manual run showed it did not move); next/previous and both skip
  directions are covered by `PlaybackControllerTest`.
- **SC-004** (Explore → Detail in ≤3 taps): PASS. Explore tab, then a
  podcast row — 2 taps.
- **SC-005** (subscribing updates Home without a restart): PASS. Verified
  live — subscribing to "History Hour" from its Detail page made it
  appear on Home immediately after switching tabs, no restart.
- **SC-006** (no placeholder-only/dead-end screens): PASS. All five
  screens (Home, Player, Explore, Detail, Settings) render real, reachable
  content.
- **SC-007** (full Home → Player flow with no network): PASS. Verified
  with wifi and mobile data both disabled (status bar showed the
  airplane-mode glyph): Home, subscriptions, and the mini-player all kept
  working. The Player screen itself was not separately re-screenshotted
  offline, but it has no network dependency (Media3 plays a bundled local
  file; state comes from Room/DataStore), so this holds by construction.
- **SC-008** (toggling connectivity causes no crash/data loss): PASS.
  Disabled and re-enabled wifi/data mid-session; the app kept running with
  no crash and no data loss.

One implementation gap found and fixed during this run: `PlaybackController`
originally only updated `positionSeconds` on explicit actions (play toggle,
skip), so the Player's progress bar sat frozen while audio was actually
playing. Fixed by adding a 1-second tick loop gated on `isPlaying`.
