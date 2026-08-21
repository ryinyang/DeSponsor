---

description: "Task list for feature implementation"
---

# Tasks: Podcast App Visual Mockup

**Input**: Design documents from `/specs/001-podcast-app-mockup/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/module-interfaces.md, quickstart.md

**Tests**: Constitution Principle I (Test-First, NON-NEGOTIABLE) requires tests
before implementation for every module. Test tasks below are mandatory, not
optional, and MUST fail before their matching implementation task starts.

**Organization**: Tasks are grouped by user story so each story can be built,
tested, and demoed on its own.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no open dependency)
- **[Story]**: Which user story this task belongs to (US1, US2, US3)
- Each task names the exact file(s) it touches

## Path Conventions

Multi-module Android/Gradle project, per plan.md's Project Structure:
`app/`, `core/{model,data,player,designsystem}/`, `feature/{home,player,explore,detail,settings}/`.
Kotlin sources live under each module's `src/main/kotlin/com/desponsor/...`,
JVM tests under `src/test/kotlin/...`, and Compose UI tests under
`src/androidTest/kotlin/...`.

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Start the project and lock in the toolchain from research.md.

- [X] T001 Create the multi-module Gradle project skeleton (`settings.gradle.kts`,
      `app/`, `core/model/`, `core/data/`, `core/player/`,
      `core/designsystem/`, `feature/home/`, `feature/player/`,
      `feature/explore/`, `feature/detail/`, `feature/settings/`, each with an
      empty `build.gradle.kts`) per plan.md's Project Structure.
- [X] T002 Add a Gradle version catalog at `gradle/libs.versions.toml` with
      Kotlin 2.3.20, AGP 9.3.0, Compose BOM 2026.08.00, Navigation 3 1.1.6,
      Room 3.0.1, DataStore 1.2.1, Media3 1.11.0, Coil 3.5.0,
      kotlinx.serialization 1.11.0, kotlinx.coroutines 1.11.0 (research.md).
- [X] T003 [P] Set `compileSdk 37`, `targetSdk 37`, `minSdk 26`, and the JDK 17
      toolchain in every module's `build.gradle.kts` (research.md §1).
- [X] T004 [P] Add ktlint (or detekt) to the root `build.gradle.kts` for
      consistent formatting across all modules.
- [X] T005 [P] Add JUnit 5, MockK, Turbine, and
      `androidx.compose.ui:ui-test-junit4` test dependencies to the version
      catalog and to each module's `build.gradle.kts` test/androidTest
      configurations (research.md §9).

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Build the shared data, playback, and design-system layers every
user story needs. No user story can start before this phase is done.

**⚠️ CRITICAL**: Do not start Phase 3, 4, or 5 until this phase is complete.

- [X] T006 [P] Create the `Podcast`, `Episode`, `Subscription`, `PlaybackState`,
      `SearchResult`, and `AppSettings` data classes in
      `core/model/src/main/kotlin/com/desponsor/core/model/` per data-model.md.
- [X] T007 [P] Write JVM unit tests for the `core/model` validation rules
      (non-blank `title`/`publisher`, unique `orderIndex` per podcast,
      `positionSeconds` clamped to `[0, durationSeconds]`) in
      `core/model/src/test/kotlin/com/desponsor/core/model/ValidationTest.kt`.
      These tests MUST fail until T008 is done.
- [X] T008 Add validation helper functions for the data classes in
      `core/model/src/main/kotlin/com/desponsor/core/model/Validation.kt` so
      the tests in T007 pass.
- [X] T009 Set up the Room database — `PodcastEntity`, `EpisodeEntity`,
      `SubscriptionEntity`, DAOs, and the `AppDatabase` class — in
      `core/data/src/main/kotlin/com/desponsor/core/data/local/` (research.md §4).
- [X] T010 Set up a DataStore Preferences store for `AppSettings` in
      `core/data/src/main/kotlin/com/desponsor/core/data/local/SettingsDataStore.kt`.
- [X] T011 [P] Write JVM unit tests for `PodcastRepository` — subscribe is
      idempotent (FR-013), `observeSubscribedPodcasts()` reacts to
      subscribe/unsubscribe, `search()` returns an empty list for no match —
      in `core/data/src/test/kotlin/com/desponsor/core/data/PodcastRepositoryTest.kt`
      per contracts/module-interfaces.md. These tests MUST fail until T013 is done.
- [X] T012 [P] Write JVM unit tests for `SettingsRepository` — `update()` is
      reflected by `observeSettings()` right away — in
      `core/data/src/test/kotlin/com/desponsor/core/data/SettingsRepositoryTest.kt`.
      These tests MUST fail until T014 is done.
- [X] T013 Implement `PodcastRepository` (interface + Room-backed class) in
      `core/data/src/main/kotlin/com/desponsor/core/data/PodcastRepository.kt`
      so the T011 tests pass.
- [X] T014 Implement `SettingsRepository` (interface + DataStore-backed class)
      in `core/data/src/main/kotlin/com/desponsor/core/data/SettingsRepository.kt`
      so the T012 tests pass.
- [X] T015 Seed a small mocked podcast and episode catalog, and default
      `AppSettings`, on first run in
      `core/data/src/main/kotlin/com/desponsor/core/data/MockCatalogSeed.kt`
      (FR-016).
- [X] T016 [P] Write JVM unit tests for `PlaybackController` — play/pause
      state, `skipForward30()` clamps at episode end, `skipBackward30()`
      clamps at zero, `next()`/`previous()` are no-ops at list ends — in
      `core/player/src/test/kotlin/com/desponsor/core/player/PlaybackControllerTest.kt`
      per contracts/module-interfaces.md. These tests MUST fail until T017 is done.
- [X] T017 Implement `PlaybackController` (interface + Media3-backed class) in
      `core/player/src/main/kotlin/com/desponsor/core/player/PlaybackController.kt`
      so the T016 tests pass.
- [X] T018 Add bundled mock/sample audio files under
      `core/player/src/main/res/raw/` and reference them from
      `Episode.sampleAudioRef` in the T015 seed data (research.md §5).
- [X] T019 [P] Build `core/designsystem`: a Material 3 theme, an artwork
      placeholder composable, a transport-control button row, and
      empty-state/no-results-state composables, in
      `core/designsystem/src/main/kotlin/com/desponsor/core/designsystem/`.
- [X] T020 Set up the Navigation 3 graph skeleton (routes for Home, Player,
      Explore, Search results, Podcast Detail, Settings, using type-safe
      `kotlinx.serialization` routes) and a session-scoped holder for
      `PlaybackController` above the graph (FR-008), in
      `app/src/main/kotlin/com/desponsor/app/navigation/AppNavGraph.kt`.
- [X] T021 Wire a manual `AppContainer` that builds and hands out
      `PodcastRepository`, `SettingsRepository`, and `PlaybackController` to
      the feature modules, in
      `app/src/main/kotlin/com/desponsor/app/AppContainer.kt` (research.md §8).

**Checkpoint**: Foundation ready — user story work can now start.

---

## Phase 3: User Story 1 - Listen to a subscribed podcast (Priority: P1) 🎯 MVP

**Goal**: Home lists subscribed podcasts; picking an episode opens the
Player with working play, pause, next, previous, skip forward 30s, and
skip backward 30s controls.

**Independent Test**: Open the app to Home, pick a subscribed podcast's
episode, and use every Player control. Each tap MUST produce a correct,
visible change. This works without Explore, Search, Detail, or Settings.

### Tests for User Story 1 ⚠️

> Write these tests first. Confirm they fail before you start implementation.

- [X] T022 [P] [US1] Write a Compose UI test for the Home screen — shows
      subscribed podcasts; shows the empty state with no subscriptions
      (FR-002) — in
      `feature/home/src/androidTest/kotlin/com/desponsor/feature/home/HomeScreenTest.kt`.
- [X] T023 [P] [US1] Write a JVM unit test for `HomeViewModel` — maps
      `PodcastRepository.observeSubscribedPodcasts()` to UI state, including
      the empty state — in
      `feature/home/src/test/kotlin/com/desponsor/feature/home/HomeViewModelTest.kt`.
- [X] T024 [P] [US1] Write a JVM unit test for `PlayerViewModel` — loading an
      episode, play/pause/skip-forward/skip-backward/next/previous each call
      the matching `PlaybackController` method, and next/previous are
      no-ops at the list ends — in
      `feature/player/src/test/kotlin/com/desponsor/feature/player/PlayerViewModelTest.kt`.
- [X] T025 [P] [US1] Write a Compose UI test for the Player screen — shows
      artwork or a placeholder, title, podcast name, and progress; each
      transport control tap changes the visible state — in
      `feature/player/src/androidTest/kotlin/com/desponsor/feature/player/PlayerScreenTest.kt`.

### Implementation for User Story 1

- [X] T026 [US1] Implement `HomeViewModel` in
      `feature/home/src/main/kotlin/com/desponsor/feature/home/HomeViewModel.kt`
      so the T023 test passes.
- [X] T027 [US1] Implement the Home screen composable (podcast list, empty
      state, artwork via Coil with a placeholder) in
      `feature/home/src/main/kotlin/com/desponsor/feature/home/HomeScreen.kt`
      so the T022 test passes.
- [X] T028 [US1] Implement `PlayerViewModel` in
      `feature/player/src/main/kotlin/com/desponsor/feature/player/PlayerViewModel.kt`
      so the T024 test passes.
- [X] T029 [US1] Implement the Player screen composable (artwork, title,
      podcast name, progress bar, play/pause/skip-forward-30/skip-backward-30/
      next/previous controls) in
      `feature/player/src/main/kotlin/com/desponsor/feature/player/PlayerScreen.kt`
      so the T025 test passes.
- [X] T030 [US1] Add the Home → Player navigation route (selecting an
      episode opens the Player with that episode's id) to
      `app/src/main/kotlin/com/desponsor/app/navigation/AppNavGraph.kt`.
- [X] T031 [US1] Add a persistent mini-player that stays visible while the
      user browses other screens (FR-008), in
      `app/src/main/kotlin/com/desponsor/app/navigation/MiniPlayerHost.kt`.

**Checkpoint**: User Story 1 works fully on its own.

---

## Phase 4: User Story 2 - Discover and subscribe to a new podcast (Priority: P2)

**Goal**: A user browses Explore, searches, opens a Podcast Detail page,
and subscribes — after which the podcast shows up on Home.

**Independent Test**: Open Explore, search for a podcast, open its detail
page, tap Subscribe, and confirm it now appears on Home. This works
without the Player or Settings being complete.

### Tests for User Story 2 ⚠️

> Write these tests first. Confirm they fail before you start implementation.

- [X] T032 [P] [US2] Write a JVM unit test for `ExploreViewModel` — lists
      the mocked catalog; filters it by a search term; returns an empty
      list for no match (FR-009, FR-010) — in
      `feature/explore/src/test/kotlin/com/desponsor/feature/explore/ExploreViewModelTest.kt`.
- [X] T033 [P] [US2] Write a JVM unit test for `DetailViewModel` — loads a
      podcast's detail and episode list; subscribing twice does not
      duplicate the subscription (FR-013) — in
      `feature/detail/src/test/kotlin/com/desponsor/feature/detail/DetailViewModelTest.kt`.
- [X] T034 [P] [US2] Write a Compose UI test for the Explore screen —
      catalog list, search bar, and the "no results" state — in
      `feature/explore/src/androidTest/kotlin/com/desponsor/feature/explore/ExploreScreenTest.kt`.
- [X] T035 [P] [US2] Write a Compose UI test for the Podcast Detail screen —
      artwork, description, episode list, and the Subscribe button state — in
      `feature/detail/src/androidTest/kotlin/com/desponsor/feature/detail/DetailScreenTest.kt`.

### Implementation for User Story 2

- [X] T036 [US2] Implement `ExploreViewModel` (catalog + search state) in
      `feature/explore/src/main/kotlin/com/desponsor/feature/explore/ExploreViewModel.kt`
      so the T032 test passes.
- [X] T037 [US2] Implement the Explore screen composable with a search bar
      and a "no results" state in
      `feature/explore/src/main/kotlin/com/desponsor/feature/explore/ExploreScreen.kt`
      so the T034 test passes.
- [X] T038 [US2] Implement `DetailViewModel` in
      `feature/detail/src/main/kotlin/com/desponsor/feature/detail/DetailViewModel.kt`
      so the T033 test passes.
- [X] T039 [US2] Implement the Podcast Detail screen composable with a
      Subscribe button in
      `feature/detail/src/main/kotlin/com/desponsor/feature/detail/DetailScreen.kt`
      so the T035 test passes.
- [X] T040 [US2] Add the Explore → search result → Detail → Subscribe
      routes to `app/src/main/kotlin/com/desponsor/app/navigation/AppNavGraph.kt`,
      and confirm a subscribed podcast shows up on Home without a restart
      (SC-005).

**Checkpoint**: User Stories 1 and 2 both work fully on their own.

---

## Phase 5: User Story 3 - Adjust app settings (Priority: P3)

**Goal**: A user opens Settings, sees app preferences, and changes them.

**Independent Test**: Open Settings and change a preference. The new value
MUST show right away. This works without Home, Explore, or the Player.

### Tests for User Story 3 ⚠️

> Write these tests first. Confirm they fail before you start implementation.

- [X] T041 [P] [US3] Write a JVM unit test for `SettingsViewModel` — an
      update to a preference is reflected right away — in
      `feature/settings/src/test/kotlin/com/desponsor/feature/settings/SettingsViewModelTest.kt`.
- [X] T042 [P] [US3] Write a Compose UI test for the Settings screen —
      lists appearance, default skip seconds, and default playback speed;
      changing a value updates the displayed value — in
      `feature/settings/src/androidTest/kotlin/com/desponsor/feature/settings/SettingsScreenTest.kt`.

### Implementation for User Story 3

- [X] T043 [US3] Implement `SettingsViewModel` in
      `feature/settings/src/main/kotlin/com/desponsor/feature/settings/SettingsViewModel.kt`
      so the T041 test passes.
- [X] T044 [US3] Implement the Settings screen composable in
      `feature/settings/src/main/kotlin/com/desponsor/feature/settings/SettingsScreen.kt`
      so the T042 test passes.
- [X] T045 [US3] Add the Settings route to
      `app/src/main/kotlin/com/desponsor/app/navigation/AppNavGraph.kt` and
      confirm Home, Explore, and Settings are each reachable within 2 taps
      from anywhere in the app (SC-001).

**Checkpoint**: All three user stories now work, each on its own.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Confirm the whole mockup meets the spec, end to end.

- [X] T046 [P] Turn on Airplane Mode and confirm Home, Player, and Settings
      still work fully (FR-017–FR-019); record the result in
      `specs/001-podcast-app-mockup/quickstart.md`.
- [X] T047 [P] Run every manual scenario in
      `specs/001-podcast-app-mockup/quickstart.md` end to end and fix any
      gap found.
- [X] T048 [P] Run the ktlint/detekt check across all modules and fix any
      warnings.
- [X] T049 Check each Success Criterion (SC-001–SC-008) in spec.md against
      the running app and note the result in
      `specs/001-podcast-app-mockup/quickstart.md`.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — start right away.
- **Foundational (Phase 2)**: Needs Phase 1 done. Blocks every user story.
- **User Story 1 (Phase 3)**: Needs Phase 2 done. No dependency on US2/US3.
- **User Story 2 (Phase 4)**: Needs Phase 2 done. Uses `PodcastRepository`
  from Phase 2 (not from US1); does not need US1's screens to work, though
  T040 confirms the result shows on the Home screen built in US1.
- **User Story 3 (Phase 5)**: Needs Phase 2 done. No dependency on US1/US2.
- **Polish (Phase 6)**: Needs the user stories you plan to ship to be done.

### Within Each User Story

- Write tests first. Confirm they fail.
- ViewModel before screen composable.
- Screen composable before its navigation route is wired in.

### Parallel Opportunities

- T003, T004, T005 (Setup) can run together.
- T006, T007, T011, T012, T016, T019 (Foundational, different files) can
  run together once their own prerequisites are met.
- Once Phase 2 is done, US1, US2, and US3 can be built at the same time by
  different people, since each targets separate modules.
- Within a story, all `[P]` test tasks can run together.

---

## Parallel Example: User Story 1

```bash
# Launch all User Story 1 tests together:
Task: "Compose UI test for Home screen in feature/home/src/androidTest/kotlin/com/desponsor/feature/home/HomeScreenTest.kt"
Task: "JVM unit test for HomeViewModel in feature/home/src/test/kotlin/com/desponsor/feature/home/HomeViewModelTest.kt"
Task: "JVM unit test for PlayerViewModel in feature/player/src/test/kotlin/com/desponsor/feature/player/PlayerViewModelTest.kt"
Task: "Compose UI test for Player screen in feature/player/src/androidTest/kotlin/com/desponsor/feature/player/PlayerScreenTest.kt"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Finish Phase 1: Setup.
2. Finish Phase 2: Foundational — this blocks every story.
3. Finish Phase 3: User Story 1.
4. Stop. Test User Story 1 on its own, per quickstart.md scenario 1.
5. Demo the Home → Player flow if it is ready.

### Incremental Delivery

1. Setup + Foundational → the foundation is ready.
2. Add User Story 1 → test it alone → demo it (this is the MVP).
3. Add User Story 2 → test it alone → demo it.
4. Add User Story 3 → test it alone → demo it.
5. Each story adds value without breaking the stories before it.

### Parallel Team Strategy

With more than one developer:

1. The team finishes Setup and Foundational together.
2. Once Foundational is done:
   - Developer A takes User Story 1.
   - Developer B takes User Story 2.
   - Developer C takes User Story 3.
3. Each story is built and tested on its own, then merged.

---

## Notes

- `[P]` tasks touch different files and have no open dependency.
- `[Story]` labels trace each task back to its user story.
- Every story must be usable and testable on its own.
- Write tests first. Confirm they fail before you write the matching code
  (constitution Principle I, NON-NEGOTIABLE).
- Commit after each task or each small group of related tasks.
- You may stop at any checkpoint to test a story on its own.
- Avoid: vague tasks, two tasks touching the same file marked `[P]`, and
  cross-story dependencies that break a story's independence.
