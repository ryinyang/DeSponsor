# Data Model: Podcast App Visual Mockup

Derived from spec.md's Key Entities and Requirements (FR-001–FR-020). This
describes the shape of the data, not persistence implementation details
(those are covered in research.md §4). Entities map to Room tables where
noted; all entities are designed so a future cloud-sync layer can add
remote fields (e.g., a `syncedAt`/`remoteId`) without changing existing
fields — satisfying FR-020.

## Podcast

Represents a show the user can subscribe to.

| Field | Type | Notes |
|---|---|---|
| `id` | String (stable local ID) | Primary key. |
| `title` | String | Required. |
| `publisher` | String | Required. |
| `description` | String | Required; shown on Podcast Detail (FR-011). |
| `artworkUrl` | String? | Nullable — absence triggers placeholder (FR-003, edge case: missing artwork). |
| `isSubscribed` | Boolean | Derived from/backed by Subscription; drives Home membership (FR-001, FR-012, FR-013). |

**Validation**: `title` and `publisher` non-blank. `id` unique.

## Episode

A single playable item belonging to a Podcast.

| Field | Type | Notes |
|---|---|---|
| `id` | String | Primary key. |
| `podcastId` | String | Foreign key → Podcast.id. |
| `title` | String | Required. |
| `durationSeconds` | Int | Required, > 0. |
| `orderIndex` | Int | Determines next/previous ordering within a podcast (FR-007). |
| `artworkUrl` | String? | Nullable; falls back to parent Podcast's `artworkUrl`, then placeholder. |
| `sampleAudioRef` | String | Reference to bundled mock/sample audio asset (research.md §5); not a live stream URL. |

**Validation**: `podcastId` must reference an existing Podcast.
`orderIndex` unique within a given `podcastId`.

**Relationships**: Many Episodes → one Podcast, ordered by `orderIndex`.

## Subscription

The relationship between the user and a Podcast that determines Home page
membership (Key Entity in spec.md).

| Field | Type | Notes |
|---|---|---|
| `podcastId` | String | Primary key / foreign key → Podcast.id. |
| `subscribedAt` | Instant/epoch millis | Set when the user subscribes (FR-012); used for Home ordering (e.g., most recently subscribed first). |

**Validation**: One row per `podcastId` (subscribing twice is a no-op per
FR-013 / edge case "already subscribed").

**State transitions**: `(none)` → `subscribed` (row created, via Subscribe
action on Podcast Detail). `subscribed` → `(none)` is out of scope to
*require* by this spec but MAY be supported symmetrically (see spec
Assumptions on unsubscribe).

## PlaybackState

Represents the Player's current session — not a spec "Key Entity" by name,
but required to support FR-004–FR-008 and the edge cases around
navigating away mid-playback and boundary next/previous.

| Field | Type | Notes |
|---|---|---|
| `currentEpisode` | Episode? | Null when nothing has been loaded yet; carries title/artwork for the Player screen without a repository lookup. |
| `podcastTitle` | String? | The current episode's podcast name, for display (FR-006). |
| `isPlaying` | Boolean | Drives play/pause affordance (FR-005). |
| `positionSeconds` | Int | Current elapsed position; updated by playback ticks and by skip forward/backward 30s (FR-004). |

**Validation**: `positionSeconds` clamped to `[0, episode.durationSeconds]`
(edge case: skip forward 30s near the end must not exceed episode end;
skip backward 30s near the start must not go below zero).

**Lifecycle**: Survives navigation to Home/Explore/Settings within the
app session (FR-008) via a shared/session-scoped holder (e.g., a
Player-owning ViewModel scoped above the navigation graph) — not
necessarily persisted to disk between app restarts, since FR-017 only
requires already-available local data (subscriptions, settings, episode
metadata) to work offline, not that mid-episode position survive a full
process death (no requirement in spec demands that).

## SearchResult (transient)

Not persisted; a filtered view over Podcast for a given query.

| Field | Type | Notes |
|---|---|---|
| `query` | String | Current search term. |
| `results` | List\<Podcast\> | Podcasts from the mocked catalog whose title/publisher match `query`; empty list renders the "no results" state (FR-010). |

## AppSettings

Referenced by FR-014 (Settings page) and stored via DataStore Preferences
(research.md §4), not Room, since it's simple key-value state rather than
relational data.

| Field | Type | Notes |
|---|---|---|
| `appearanceTheme` | Enum (System / Light / Dark) | Reasonable representative preference (spec Assumptions). |
| `defaultSkipSeconds` | Int | Default 30; shared magnitude for both skip forward and skip backward (FR-004); editable per spec Assumptions on Settings content. |
| `defaultPlaybackSpeed` | Float | e.g., 1.0x default. |

**Validation**: `defaultPlaybackSpeed` > 0. `defaultSkipSeconds` > 0.

## Forward-compatibility note (FR-020)

Every entity above uses a locally-stable `id`/primary key that is
independent of any remote system, and no field assumes a single-writer
local-only world (e.g., `subscribedAt` is a timestamp, not an
auto-increment row number, so it remains meaningful if merged with a
remote copy later). Adding cloud sync in a future feature is expected to
mean *adding* fields/tables (e.g., a `remoteId`, `lastSyncedAt`, or a
separate sync-metadata table) and a new remote data source behind the
existing repository interfaces (see contracts/module-interfaces.md), not
renaming or restructuring the fields above.
