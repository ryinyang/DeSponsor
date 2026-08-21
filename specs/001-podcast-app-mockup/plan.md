# Implementation Plan: Podcast App Visual Mockup

**Branch**: `001-podcast-app-mockup` | **Date**: 2026-08-20 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-podcast-app-mockup/spec.md`

**Note**: This template is filled in by the `/speckit-plan` command; its definition describes the execution workflow.

## Summary

Build a full visual mockup of DeSponsor's Android podcast app: a Home page
of subscribed podcasts, a Player with real transport controls (play,
pause, next, previous, skip forward 30s, skip backward 30s) backed by
local sample audio, an Explore
page with a mocked discovery catalog, Search, a Podcast Detail page with
Subscribe, and a Settings page — all offline-first, using local
persistence (Room + DataStore) structured so a future premium cloud-sync
feature can be added later without redesigning the data model (FR-020).
Technical approach: a multi-module Jetpack Compose app on the latest
stable Android toolchain (Kotlin 2.3.20, AGP 9.3.0, Compose BOM
2026.08.00), with `core/*` modules that are independently JVM-testable per
constitution Principle III, and no live backend/network dependency
anywhere in this feature.

## Technical Context

**Language/Version**: Kotlin 2.3.20 (JDK 17 toolchain)

**Primary Dependencies**: Jetpack Compose (BOM 2026.08.00 / Compose 1.12),
AndroidX Navigation 3 1.1.6, Room 3.0.1, AndroidX DataStore (Preferences)
1.2.1, AndroidX Media3 1.11.0, Coil 3.5.0, kotlinx.serialization 1.11.0,
kotlinx.coroutines 1.11.0 — see research.md for rationale and compatibility
notes on each.

**Storage**: Local only — Room 3.0.1 (podcasts, episodes, subscriptions,
playback progress) + DataStore Preferences 1.2.1 (app settings). No
remote/cloud storage in this feature (see spec.md Assumptions and FR-020).

**Testing**: JUnit 5 + MockK + Turbine for JVM unit tests of `core/*` and
`feature/*` ViewModels (no emulator required); Compose UI tests
(`androidx.compose.ui:ui-test-junit4`) for instrumented screen-level
verification. See research.md §9.

**Target Platform**: Android, compileSdk 37 / targetSdk 37 (Android 17),
minSdk 26 (Android 8.0+).

**Project Type**: Mobile app (single Android app, multi-module Gradle
project — see Project Structure below).

**Performance Goals**: Screen navigation and player control taps produce a
visibly correct state change within a single frame (~16ms budget / 60fps),
per SC-003; no specific throughput/concurrency targets apply to a
single-user local app.

**Constraints**: Offline-first — Home, Player (already-available
episodes), and Settings MUST work fully with no network connection
(FR-017–FR-019); no cloud account or sign-in required anywhere in this
feature (FR-019); local data model must not preclude adding an optional
cloud-sync feature later without a redesign (FR-020).

**Scale/Scope**: 6 primary screens (Home, Player, Explore, Search results,
Podcast Detail, Settings) over a small seeded/mocked dataset (a handful of
podcasts with a few episodes each); single local user, no multi-account or
concurrency concerns.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle / Gate | Status | Notes |
|---|---|---|
| I. Test-First (NON-NEGOTIABLE) | PASS | Plan isolates business/state logic into JVM-testable `core/*` and `feature/*` ViewModel layers (research.md §9, contracts/module-interfaces.md) so tests can be written and run before/without UI implementation. Actual test-first sequencing is enforced at the `/speckit-tasks`/implementation stage, not by this plan alone. |
| II. Simplicity (YAGNI) | PASS | No DI framework added yet (research.md §8); module count is driven by the constitution's own modularity requirement, not spreading logic. Cloud sync is explicitly *not* built now — only the data model is shaped so it can be added later (FR-020), which is the minimal step that satisfies that requirement without speculative infrastructure. |
| III. Library-First, Modular Design | PASS | Multi-module structure (below) gives each feature area a self-contained module with a defined public interface (contracts/module-interfaces.md); `core/*` modules are plain-Kotlin/JVM-testable without an emulator. |
| IV. Observability & Structured Logging | N/A for this feature | This principle's mandatory scope is ad-detection decisions; ad detection is explicitly out of scope for this mockup (spec.md Assumptions). Revisit when the ad-detection feature is planned — no logging infrastructure is being pre-built here (would violate Principle II). |
| Quality & Accuracy Gates | N/A for this feature | Concerns ML ad-detection model changes; none occur in this feature. |
| Development Workflow (code review, compliance) | PASS | Process gate; unaffected by this plan, applies at PR time. |

No violations requiring justification — Complexity Tracking table below is
empty.

## Project Structure

### Documentation (this feature)

```text
specs/001-podcast-app-mockup/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
├── contracts/           # Phase 1 output (/speckit-plan command)
│   └── module-interfaces.md
└── tasks.md             # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

### Source Code (repository root)

```text
settings.gradle.kts
build.gradle.kts

app/                              # Android application module: DI wiring (manual, research.md §8),
│                                  # navigation host (Navigation 3), theming, app entry point.
└── src/{main,test,androidTest}/

core/
├── model/                        # Pure Kotlin data classes (Podcast, Episode, Subscription,
│   └── src/{main,test}/          # PlaybackState, AppSettings) — data-model.md. No Android deps.
├── data/                         # PodcastRepository, SettingsRepository implementations
│   └── src/{main,test}/          # (Room 3.0.1 + DataStore + mocked/seeded catalog).
├── player/                       # PlaybackController implementation wrapping Media3.
│   └── src/{main,test}/
└── designsystem/                 # Compose theme, reusable components (artwork placeholder,
    └── src/{main,test}/          # transport control buttons, empty/no-results states).

feature/
├── home/          # Home screen + ViewModel (User Story 1 support)
│   └── src/{main,test,androidTest}/
├── player/         # Player screen + mini-player UI (User Story 1)
│   └── src/{main,test,androidTest}/
├── explore/        # Explore + Search (User Story 2)
│   └── src/{main,test,androidTest}/
├── detail/         # Podcast Detail + Subscribe (User Story 2)
│   └── src/{main,test,androidTest}/
└── settings/        # Settings screen (User Story 3)
    └── src/{main,test,androidTest}/
```

**Structure Decision**: Multi-module Android/Gradle project (`app` +
`core/*` + `feature/*`), matching Android's current standard
multi-module Compose architecture. This directly satisfies constitution
Principle III (each feature area is a self-contained, independently
testable module with a defined public interface) and keeps `core/model`
and the repository/controller logic in `core/data` and `core/player`
testable on the JVM with no emulator (Principle III), while
`feature/*` modules stay independently buildable/testable per user story,
matching this spec's P1/P2/P3 story slicing. No separate `build-logic`
convention-plugin module is introduced yet — per-module `build.gradle.kts`
files are simple enough at this scale (Principle II); revisit only if
duplication becomes a real maintenance cost.

## Complexity Tracking

*No Constitution Check violations — table intentionally empty.*
