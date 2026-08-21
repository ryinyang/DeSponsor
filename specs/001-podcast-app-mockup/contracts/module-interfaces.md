# Module Interface Contracts: Podcast App Visual Mockup

This app has no external/network API — it is a local, offline-first mockup
(spec.md Assumptions). Per the constitution's Library-First, Modular
Design principle, the "contracts" that matter here are the public
interfaces each `core/*` module exposes to `feature/*` modules. These are
the boundaries `/speckit-tasks` should generate contract-test tasks
against, and the seams a future cloud-sync data source (FR-020) plugs
into without callers changing.

All interfaces are plain Kotlin, coroutines/Flow-based, and contain no
Android UI types — this is what makes them independently JVM-testable
(constitution Principle III).

## PodcastRepository (`core/data`)

```kotlin
interface PodcastRepository {
    // All subscribed podcasts, for Home (FR-001). Empty flow → empty state (FR-002).
    fun observeSubscribedPodcasts(): Flow<List<Podcast>>

    // Full mocked catalog, for Explore (FR-009).
    fun observeExploreCatalog(): Flow<List<Podcast>>

    // Search over the mocked catalog (FR-010). Empty results list → "no results" state.
    fun search(query: String): Flow<List<Podcast>>

    // Single podcast + its episodes, for Podcast Detail (FR-011).
    suspend fun getPodcastDetail(podcastId: String): PodcastDetail?

    // Idempotent: subscribing twice is a no-op (FR-012, FR-013).
    suspend fun subscribe(podcastId: String)

    // Symmetric unsubscribe (spec Assumptions); idempotent if already unsubscribed.
    suspend fun unsubscribe(podcastId: String)
}
```

**Contract rules**:
- `observeSubscribedPodcasts()` MUST reflect a `subscribe()`/`unsubscribe()`
  call without requiring an app restart (SC-005).
- `subscribe()` on an already-subscribed `podcastId` MUST NOT create a
  duplicate Subscription row (FR-013).
- All methods MUST return/emit using only locally available (mocked/
  persisted) data — no network call is part of this contract (FR-017,
  FR-019).

## PlaybackController (`core/player`)

```kotlin
interface PlaybackController {
    fun observeState(): StateFlow<PlaybackState>

    fun play()
    fun pause()
    fun skipForward30()
    fun skipBackward30()
    fun next()      // no-op at end of episode list (edge case)
    fun previous()  // no-op at start of episode list (edge case)

    // Loads an episode and starts from position 0. `podcastTitle` and `queue`
    // (that episode's podcast's ordered episode list) are supplied by the
    // caller (which already has them — e.g. Home or Player), so
    // PlaybackController stays decoupled from core/data and only knows about
    // playback mechanics, while PlaybackState still carries enough (episode +
    // podcast title) for the Player screen to render without a lookup.
    suspend fun loadEpisode(episode: Episode, podcastTitle: String, queue: List<Episode>)
}
```

**Contract rules**:
- Every method MUST produce a new value on `observeState()` synchronously
  enough to be perceived as immediate (SC-003) — no network round trip is
  part of this contract, since playback is against local sample audio
  (research.md §5).
- `skipForward30()` MUST clamp `positionSeconds` to the episode's
  `durationSeconds`, never exceeding it (edge case).
- `skipBackward30()` MUST clamp `positionSeconds` to zero, never going
  below the start of the episode (edge case).
- `next()`/`previous()` at a list boundary MUST leave `PlaybackState`
  unchanged rather than erroring (edge case).
- State MUST remain readable (last known value) while the caller
  navigates across `feature/*` screens (FR-008) — this interface is
  intended to be owned by a session-scoped holder above the navigation
  graph, not per-screen.

## SettingsRepository (`core/data`)

```kotlin
interface SettingsRepository {
    fun observeSettings(): Flow<AppSettings>
    suspend fun update(settings: AppSettings)
}
```

**Contract rules**:
- `update()` MUST be reflected by `observeSettings()` immediately (FR-014
  acceptance scenario 2 — "the change is visibly reflected immediately").
- Both methods MUST work with no network connection (FR-017).

## Forward-compatibility (FR-020)

Each interface above is implemented in this feature entirely against
local/mocked data sources (Room + DataStore + bundled sample audio, per
research.md). A future premium cloud-sync feature is expected to add a
*new* implementation (e.g., a repository that merges a local Room source
with a remote source) behind these same interfaces, or to extend them
with additive methods — not to change the method signatures or callers
listed here.
