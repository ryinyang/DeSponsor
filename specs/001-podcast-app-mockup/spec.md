# Feature Specification: Podcast App Visual Mockup

**Feature Branch**: `001-podcast-app-mockup`

**Created**: 2026-08-20

**Status**: Draft

**Input**: User description: "Build a modern, sleak podcast app. The player should have all the buttons you'd expect, play, pause, next, previous, skip 30 seconds. The player should also have a space for the podcast's artwork (if any). The artwork space can be mocked for now. There should be a home page which lists out all the podcasts that the user is subscribed to. The actual subscription feature can be mocked for now. There should also be a settings page. There should be a explore page which will be populated with different new podcasts that the user may be interested in. The user should be able to search for podcasts and get a detail page for the podcast and subscribe. The explore page should be mocked for now. The goal for now is to mock up the app in its entirety to get a good visual understanding for iteration."

**Update (2026-08-20)**: "Update @specs/001-podcast-app-mockup to specify that we want to keep the app offline-first as much as possible. In the future we may add premium paid features to store user's data on cloud. Ensure that the app can grow to meet this future cloud model."

## Clarifications

### Session 2026-08-20

- Q: Should the 30-second skip control move playback forward only, or both forward and backward? → A: Both. The user MUST be able to skip forward 30 seconds and skip backward 30 seconds.

### Session 2026-08-21

- Q: How smooth should the timeline's progress update be while playing? → A: Fixed sub-second interval (100-250ms) — visibly smooth without per-frame overhead.
- Q: When the user drags on the timeline to scrub, should audio move live as they drag, or only jump once they release? → A: Displayed time updates live during drag; the actual playback seek commits only on release.
- Q: Should tap-and-drag scrubbing be available on the mini-player as well as the full-screen player, or only the full-screen player? → A: Full-screen player only; the mini-player shows progress but is not draggable.
- Q: What should happen if the user drags the scrubber past the very start or end of the episode's timeline? → A: Clamp to the episode's start (0:00) or end position — dragging further has no additional effect.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Listen to a subscribed podcast (Priority: P1)

A user opens the app, sees the podcasts they're subscribed to on the Home
page, picks an episode, and controls playback with a full set of player
transport controls (play, pause, next, previous, skip forward 30 seconds,
skip backward 30 seconds) while viewing the episode's artwork and
progress.

**Why this priority**: This is the core loop of a podcast app and the
surface the request describes in the most detail. It is the single most
important screen for evaluating whether the app's visual design and
interaction feel "modern and sleek."

**Independent Test**: Can be fully tested by opening the app to Home,
selecting a subscribed podcast's episode, and exercising every player
control (play, pause, next, previous, skip forward 30s, skip backward
30s) to confirm each produces a correct, visible state change — without
needing Explore, Search, or Settings to exist.

**Acceptance Scenarios**:

1. **Given** the Home page is showing the user's subscribed podcasts,
   **When** the user selects an episode, **Then** the player opens showing
   that episode's artwork (or a placeholder), title, podcast name, and
   playback progress.
2. **Given** an episode is loaded in the player and paused, **When** the
   user taps play, **Then** playback starts and the control changes to a
   pause affordance.
3. **Given** an episode is playing, **When** the user taps pause, **Then**
   playback stops and the control changes to a play affordance.
4. **Given** an episode is playing, **When** the user taps "skip forward
   30 seconds," **Then** playback position advances by 30 seconds and the
   progress indicator reflects the new position.
5. **Given** an episode is playing, **When** the user taps "skip backward
   30 seconds," **Then** playback position moves back by 30 seconds and
   the progress indicator reflects the new position.
6. **Given** an episode is loaded, **When** the user taps "next" or
   "previous," **Then** the player loads the adjacent episode (if one
   exists) and updates artwork, title, and progress accordingly.
7. **Given** the podcast/episode has no artwork available, **When** the
   player or Home list renders it, **Then** a clear placeholder graphic is
   shown instead of a broken or empty image.
8. **Given** an episode is loaded in the player, **When** the user presses
   and drags on the timeline, **Then** the displayed time updates live to
   follow the drag, and **When** the user releases, **Then** playback
   seeks to the released position and the progress indicator reflects it.

---

### User Story 2 - Discover and subscribe to a new podcast (Priority: P2)

A user browses the Explore page for podcasts they might like, searches for
a podcast by name, opens its detail page, and subscribes — after which it
appears on their Home page.

**Why this priority**: Discovery and subscribing are the second most
detailed part of the request and are required to make Home page content
feel meaningful (otherwise Home is just static seed data with no visible
path to grow).

**Independent Test**: Can be fully tested by opening Explore, using search
to find a podcast, opening its detail page, tapping Subscribe, and
confirming the podcast now appears on the Home page — independent of
whether the player or Settings are complete.

**Acceptance Scenarios**:

1. **Given** the user opens the Explore page, **When** the page loads,
   **Then** a list of suggested podcasts (mocked catalog) is displayed with
   artwork, title, and publisher.
2. **Given** the user is on Explore or Home, **When** they enter a search
   term, **Then** matching podcasts from the mocked catalog are shown as
   results.
3. **Given** search results are shown, **When** the user selects a result,
   **Then** a podcast detail page opens showing artwork, title,
   description, publisher, and its episode list.
4. **Given** the user is viewing a podcast detail page for a podcast they
   are not subscribed to, **When** they tap "Subscribe," **Then** the
   button reflects a subscribed state and the podcast appears on the Home
   page.
5. **Given** the user searches for a term that matches nothing in the
   mocked catalog, **When** results are displayed, **Then** a clear
   "no results" state is shown instead of an empty list.

---

### User Story 3 - Adjust app settings (Priority: P3)

A user opens the Settings page to view and adjust app preferences.

**Why this priority**: Settings is explicitly required but was given no
functional detail in the request, and no other flow depends on it — it is
the safest to build last without blocking the primary listening or
discovery experience.

**Independent Test**: Can be fully tested by navigating to the Settings
page and confirming it displays and allows interaction with a set of app
preferences, independent of Home, Explore, or the Player.

**Acceptance Scenarios**:

1. **Given** the user opens the Settings page, **When** it loads,
   **Then** a set of app preferences is displayed in a clearly organized
   list.
2. **Given** the Settings page is open, **When** the user changes a
   preference's value, **Then** the change is visibly reflected
   immediately.

---

### Edge Cases

- What happens when the user has no subscribed podcasts yet (new-user
  state on Home)? The Home page MUST show a clear empty state that points
  the user toward Explore.
- What happens when a search returns no matches? A clear "no results"
  state MUST be shown rather than an empty screen.
- What happens when artwork is unavailable for a podcast or episode? A
  placeholder graphic MUST be shown in its place.
- What happens when the user taps "skip forward 30 seconds" within the
  last 30 seconds of an episode? Playback MUST advance to at most the end
  of the episode without erroring.
- What happens when the user taps "skip backward 30 seconds" within the
  first 30 seconds of an episode? Playback MUST move back to at most the
  start of the episode (position zero) without erroring.
- What happens when the user drags the timeline scrubber past the start or
  end of the episode? The drag position MUST clamp at the episode's start
  (0:00) or end; dragging further MUST have no additional effect and MUST
  NOT trigger next/previous episode or error.
- What happens when the user taps "next" on the last episode, or
  "previous" on the first episode, in a podcast's episode list? The
  control MUST NOT error or crash; it should be disabled or a no-op at
  that boundary.
- What happens when the user tries to subscribe to a podcast they are
  already subscribed to? The detail page MUST show a "Subscribed" state
  and MUST NOT create a duplicate entry on Home.
- What happens when the user navigates to Explore, Search, or Settings
  while an episode is playing? The playback state MUST remain consistent
  and accessible (e.g., via a persistent mini-player) rather than being
  silently lost.
- What happens when the device has no network connection? Home, Player
  playback of already-available episodes, and Settings MUST continue to
  work fully. Only features that inherently depend on live/remote data
  (e.g., a future non-mocked Explore catalog or search) MAY be degraded,
  but MUST show a clear "you're offline" state rather than crashing,
  hanging, or failing silently.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST provide a Home page that lists all podcasts
  the user is currently subscribed to.
- **FR-002**: The system MUST show an empty state on Home when the user has
  no subscriptions, directing them to Explore.
- **FR-003**: Each podcast/episode listing MUST display artwork when
  available, or a placeholder graphic when it is not.
- **FR-004**: The system MUST provide a Player with the following
  transport controls: play, pause, next episode, previous episode, skip
  forward 30 seconds, and skip backward 30 seconds.
- **FR-005**: The Player MUST reflect playback state visually at all
  times (e.g., play vs. pause affordance, current progress/elapsed time).
  The progress/timeline indicator MUST update at least every 250ms during
  playback (not merely once per second) so movement appears smooth.
- **FR-006**: The Player MUST display the current episode's artwork (or
  placeholder), episode title, and podcast name.
- **FR-007**: Selecting "next" or "previous" MUST load the adjacent
  episode in the current podcast's episode list, when one exists, and MUST
  be a safe no-op at the start/end boundary.
- **FR-008**: Playback state MUST remain accessible while the user
  navigates to other pages (Home, Explore, Settings) rather than being
  discarded.
- **FR-009**: The system MUST provide an Explore page populated with a set
  of suggested podcasts the user is not yet subscribed to.
- **FR-010**: The system MUST allow the user to search for podcasts and
  display matching results, including a clear "no results" state when
  nothing matches.
- **FR-011**: Selecting a podcast (from Explore or search results) MUST
  open a Podcast Detail page showing its artwork, title, description,
  publisher, and episode list.
- **FR-012**: The Podcast Detail page MUST provide a Subscribe action that
  adds the podcast to the Home page and updates the action to reflect a
  "Subscribed" state.
- **FR-013**: The system MUST prevent duplicate Home page entries when the
  user subscribes to a podcast they are already subscribed to.
- **FR-014**: The system MUST provide a Settings page listing a set of
  app preferences that the user can view and change.
- **FR-015**: The system MUST provide navigation that lets the user reach
  Home, Explore, and Settings from anywhere in the app.
- **FR-016**: All podcast, episode, and subscription data presented in
  this feature MAY be sourced from a mocked/seeded dataset rather than a
  live backend, consistent with the mockup goal of this feature.
- **FR-017**: The system MUST be offline-first: Home, playback of
  already-available episodes in the Player, and Settings MUST remain fully
  usable with no network connection.
- **FR-018**: The system MUST clearly indicate to the user when a feature
  is unavailable or degraded specifically due to lack of network
  connectivity, rather than failing silently or crashing.
- **FR-019**: The system MUST NOT require a cloud account, sign-in, or
  network connection to use any of the core features defined in this
  specification (Home, Player, Explore, Search, Podcast Detail/Subscribe,
  Settings).
- **FR-020**: The data model for user data (subscriptions, playback
  progress/history, and settings) MUST be structured so that a future,
  optional premium capability to store this data in the cloud can be
  added later without requiring a redesign of existing local data or
  user-facing behavior.
- **FR-021**: The full-screen Player's timeline MUST support tap-and-drag
  scrubbing: the user MUST be able to press and drag the position on the
  timeline to a new point. While dragging, the displayed time/position
  indicator MUST update live to follow the drag; the actual playback seek
  MUST commit only when the user releases the drag. Dragging past the
  episode's start or end MUST clamp at 0:00 or the episode's end
  respectively, with no additional effect. The persistent mini-player
  MUST continue to display playback progress but is NOT required to
  support drag-to-scrub.

### Key Entities

- **Podcast**: A show the user can subscribe to; has a title, artwork
  (optional), publisher, description, and an ordered list of episodes.
- **Episode**: A single playable item belonging to a podcast; has a title,
  duration, playback position, and (by default) inherits its podcast's
  artwork.
- **Subscription**: The relationship between the user and a podcast that
  determines whether it appears on the Home page.
- **Search Result**: A transient list of podcasts matching a user's search
  term against the mocked catalog.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Every primary screen (Home, Player, Explore, Search results,
  Podcast Detail, Settings) is reachable within 2 taps from anywhere else
  in the app.
- **SC-002**: 100% of podcast and episode listings shown to the user
  display either real artwork or a clear placeholder — no broken or empty
  image states occur.
- **SC-003**: Every player control (play, pause, next, previous, skip
  forward 30s, skip backward 30s) produces an immediate, visibly correct
  state change each time it is used.
- **SC-004**: A user can go from opening Explore to viewing a specific
  podcast's detail page in 3 taps or fewer.
- **SC-005**: Subscribing to a podcast from its detail page causes it to
  appear on the Home page without requiring an app restart or reload.
- **SC-006**: A reviewer can visually evaluate every primary screen and
  interaction described in this spec end-to-end in a single sitting
  without hitting a placeholder-only or dead-end screen.
- **SC-007**: A user can complete the full Home → Player listening flow
  (browse subscriptions, play, pause, skip forward 30s, skip backward
  30s, next, previous) with no network connection at all.
- **SC-008**: Turning network connectivity off and back on while using the
  app causes no data loss, crash, or unrecoverable state for any locally
  available podcast, episode, subscription, or setting.
- **SC-009**: During playback, the timeline/progress indicator visibly
  advances at least every 250ms rather than jumping once per second.

## Assumptions

- The app targets a single user with no account/authentication system in
  this feature; that is out of scope.
- Home page subscription data is seeded/mocked rather than backed by a
  real subscription feature, per the request.
- The Explore page's podcast catalog is a static/mocked dataset rather
  than a live directory or a recommendation engine.
- Search operates only over the mocked podcast catalog (Explore + any
  seeded Home data), not an external/live search service.
- The Player operates on mock/sample audio content rather than real
  podcast RSS feeds or streamed episode audio, since no live content
  source exists yet in this mockup phase.
- "Skip 30 seconds" is two controls: skip forward 30 seconds and skip
  backward 30 seconds (see Clarifications). Skip backward stops at the
  start of the episode; skip forward stops at the end of the episode.
- Subscribing/unsubscribing updates app state for the current session (and
  may persist locally) but does not require a backend or account system.
- The Settings page includes a small set of representative app
  preferences (e.g., appearance, playback speed default, skip duration)
  since exact settings were not specified; the visual presentation of the
  page matters more than any specific preference's real-world effect at
  this stage.
- This feature is a visual/UX mockup: ad detection and skipping (the
  product's core ML feature) is explicitly out of scope here and will be
  addressed in a future feature.
- Accessibility and performance under real-world network conditions are
  not primary concerns for this visual mockup phase, but offline
  availability of core features (Home, Player, Settings) is a durable
  product principle established by this update, not just a side effect of
  using mocked data.
- "Offline-first" means core, already-available data and actions (Home,
  playback of episodes already on the device, Settings) work fully with
  no connection; discovery-oriented features that need live data (a
  future non-mocked Explore catalog, live search) are expected to require
  network once they are no longer mocked, but must degrade gracefully
  with clear messaging instead of crashing.
- Cloud sync and any premium account/billing system are explicitly out of
  scope for this feature. This feature only requires that the underlying
  data model and local storage of user data (subscriptions, playback
  progress, settings) not preclude adding an optional cloud-sync premium
  feature later. No cloud provider, sync protocol, or billing system is
  defined here — those are deferred to a future feature.
