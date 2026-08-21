# Research: Podcast App Visual Mockup

**Date**: 2026-08-20
**Input**: spec.md (incl. 2026-08-20 offline-first update), constitution.md v1.0.0

All version numbers below were verified via web search on 2026-08-20 per the
user's explicit instruction to use latest versions and confirm
cross-compatibility. Android tooling moves fast — re-verify before
implementation if this plan is picked up materially later than that date.

## 1. Language, build tooling & Android SDK levels

- **Decision**: Kotlin 2.3.20, Android Gradle Plugin (AGP) 9.3.0, Gradle
  9.7.1, JDK 17, compileSdk 37 / targetSdk 37 (Android 17), minSdk 26
  (Android 8.0).
- **Rationale**: Kotlin 2.3.20 is the latest stable line (2.4.20 is still
  RC as of 2026-08-20). AGP 9.3.0 (July 2026) is the latest stable AGP and
  requires Gradle ≥9.5.0 — Gradle 9.7.1 (Aug 20, 2026) satisfies that.
  AGP 9.3.0 supports building against API level 37 max, which is required
  because Compose 1.12 (bundled in Compose BOM 2026.08.00, see §2) itself
  requires compileSdk 37 + AGP 9. Android 17/API 37 has been stable since
  June 16, 2026, and Google Play's compliance deadline for it isn't until
  August 2027, so targeting it now is safely ahead of the curve rather
  than bleeding-edge. minSdk 26 is a conventional modern floor that covers
  the large majority of active devices and avoids minSdk-gated API
  gymnastics (notification channels, WorkManager, etc.) without
  meaningfully narrowing reach.
- **Alternatives considered**: compileSdk 36/AGP 9.2.0 — rejected because
  it cannot host the latest Compose BOM, which conflicts with the "latest
  versions" directive. Kotlin 2.4.20-RC — rejected as pre-release; a
  mockup-phase app should not sit on a release candidate toolchain.

## 2. UI toolkit

- **Decision**: Jetpack Compose, via `androidx.compose:compose-bom:2026.08.00`
  (Compose 1.12 line). Compose compiler is Kotlin's built-in plugin
  (`org.jetbrains.kotlin.plugin.compose`) — no separate Compose Compiler
  version to track since Kotlin 2.0+.
- **Rationale**: Compose is Google's current, actively evolving UI
  toolkit and the natural fit for a "modern, sleek" visual mockup that
  needs fast, declarative iteration on layout and theming (Material 3
  expressive theming, animations). It also satisfies the constitution's
  Library-First principle well: Compose screens are naturally composed
  from small, previewable, independently testable functions.
- **Alternatives considered**: Classic Android Views/XML — rejected,
  slower to iterate visually and not the modern-idiom the feature asks
  for.

## 3. Navigation

- **Decision**: AndroidX Navigation 3 (`androidx.navigation3`, stable
  since Feb 2026, latest 1.1.6 as of Aug 12, 2026) with type-safe routes
  via `kotlinx.serialization`.
- **Rationale**: Navigation 3 is AndroidX's current Compose-first
  navigation library (the direct successor to Navigation Compose 2.x) and
  is what "latest version" should mean for a new Compose app started
  today. Type-safe routes remove a class of runtime navigation bugs,
  which matters once six-plus screens are wired together.
- **Alternatives considered**: `androidx.navigation:navigation-compose`
  2.9.8 — mature and still supported, but is the prior generation now
  that Navigation 3 is stable; picking it today would mean starting new
  on a library already superseded.

## 4. Local persistence (subscriptions, playback progress, settings)

- **Decision**: Room 3.0.1 (`androidx.room3`, Kotlin-only, KSP-based,
  coroutines-first) for structured data (podcasts, episodes,
  subscriptions, playback progress); AndroidX DataStore (Preferences)
  1.2.1 for simple app settings/preferences.
- **Rationale**: The spec's offline-first requirements (FR-017–FR-019)
  and the forward-compatibility requirement for a future premium
  cloud-sync feature (FR-020) both call for real local persistence now,
  not just in-memory UI state — otherwise "grow to meet a future cloud
  model without redesign" isn't achievable. Room gives a versioned,
  typed local schema that a future sync layer can be built against
  (e.g., a repository can later fan out writes to a remote data source
  behind the same interface) without restructuring how the UI reads
  data. DataStore is the standard modern replacement for
  SharedPreferences and fits small key-value settings better than a
  relational table.
- **Alternatives considered**: Plain in-memory `StateFlow`-backed mock
  repositories with no persistence — rejected because it does not satisfy
  FR-017 (data must survive app restarts to be meaningfully
  "offline-first") or FR-020 (an in-memory-only model would need a real
  rewrite, not just an addition, to grow into cloud sync later). Room 2.x
  (2.8.4, maintenance mode) — rejected in favor of Room 3.0.1 as the
  latest, actively developed line, per the "latest versions" directive.

## 5. Audio playback engine

- **Decision**: AndroidX Media3 (`androidx.media3`) 1.11.0 for the
  Player's actual playback of mock/sample audio.
- **Rationale**: Media3 is Google's current, actively maintained media
  playback stack (successor to ExoPlayer 2, which Android 17 is actively
  pushing apps to migrate away from for background-audio compliance). It
  gives real play/pause/seek/position behavior for the Player screen
  (FR-004–FR-008) using bundled sample audio files, without requiring any
  real content-ingestion backend — consistent with the spec's assumption
  that no live podcast source exists yet.
- **Alternatives considered**: Legacy `android.media.MediaPlayer` —
  rejected: lower-level, more boilerplate, and the platform itself is
  moving apps off it. Building a purely visual (non-functional) player —
  rejected because Media3 with local sample audio is not meaningfully
  more complex and gives truer visual/interaction feedback for the stated
  goal of "a good visual understanding for iteration."

## 6. Image loading (artwork + placeholders)

- **Decision**: Coil 3, `io.coil-kt.coil3:coil-compose` 3.5.0.
- **Rationale**: Coil is a Kotlin-coroutines-native, Compose-first image
  loader and the de facto standard for this in modern Android apps; it
  handles both real artwork and a placeholder/error drawable in one API,
  directly matching FR-003 and SC-002.
- **Alternatives considered**: Glide — mature but View-system-oriented
  and more configuration for Compose interop; not the "latest/modern"
  choice for a Compose-only app.

## 7. Serialization & coroutines

- **Decision**: `kotlinx.serialization` 1.11.0 (required by Navigation 3
  type-safe routes and used for seeding/encoding mock data);
  `kotlinx.coroutines` 1.11.0 (async work, Flow-based state, required by
  Room 3.0.1's coroutines-first API).
- **Rationale**: Both are the latest stable lines and are already
  transitively required by the navigation and persistence choices above,
  so no separate justification is needed beyond compatibility (Kotlin
  2.3.20 is within kotlinx.coroutines 1.11.0's supported range).

## 8. Dependency wiring (DI)

- **Decision**: No DI framework for this feature. Wire ViewModels and
  repositories with plain constructor injection and small factory
  functions (e.g., a lightweight `AppContainer`), behind the same
  interfaces a DI framework would eventually target.
- **Rationale**: Constitution Principle II (Simplicity/YAGNI) requires
  every dependency to be justified by a present need. This feature's
  dependency graph is small (a handful of repositories/controllers per
  feature module) and does not yet need a framework's compile-time
  graph validation or scoping. Because each module still exposes a clear
  public interface (Principle III), introducing Hilt later is additive,
  not a redesign.
- **Alternatives considered**: Hilt 2.56 — the current Google-recommended
  DI framework and a reasonable future choice once the dependency graph
  grows (e.g., when a real backend/cloud-sync data source is added);
  deferred for now rather than rejected outright. Koin — rejected because
  its runtime-resolution model trades away the compile-time safety that
  would matter once this app has more than a mockup's worth of wiring.

## 9. Testing stack

- **Decision**: JUnit 5 (Jupiter) + MockK 1.14.x + Turbine 1.0.0 for pure
  Kotlin/JVM unit tests (repositories, ViewModels, mapping logic) that
  run with no emulator, per constitution Principle III. Compose UI
  testing via `androidx.compose.ui:ui-test-junit4` +
  `androidx.compose.ui:ui-test-manifest` for the small set of
  instrumented screen-level tests, since Compose's test APIs still run
  on a JUnit4 rule under the hood even inside a JUnit5-based project.
- **Rationale**: Satisfies constitution Principle I (Test-First) and
  Principle III (JVM-testable modules) directly: business/state logic in
  `core/*` and `feature/*` ViewModels is fully testable without a device
  or emulator, while Compose UI tests cover the visual/interaction
  contracts (e.g., player control taps) that can only be verified against
  real composition.
- **Alternatives considered**: JUnit 4 throughout — rejected as the older
  generation; kept only where Compose's tooling still requires it.

## 10. Module architecture

- **Decision**: Multi-module Gradle project (`app` + `core/*` +
  `feature/*`), detailed in plan.md's Project Structure section.
- **Rationale**: Directly implements constitution Principle III
  (Library-First, Modular Design): each feature area is a self-contained,
  independently testable module with a defined public interface, and
  core logic (data, playback, design system) is decoupled from any single
  feature's UI.
- **Alternatives considered**: A single `app` module with internal
  packages — rejected as it cannot enforce module boundaries or
  independent JVM testability the way separate Gradle modules can, and
  would conflict with Principle III.
