<!--
Sync Impact Report
Version change: (none) → 1.0.0 (initial ratification)
Modified principles: N/A (initial creation)
Added principles:
  - I. Test-First (NON-NEGOTIABLE)
  - II. Simplicity (YAGNI)
  - III. Library-First, Modular Design
  - IV. Observability & Structured Logging
Added sections:
  - Quality & Accuracy Gates
  - Development Workflow
  - Governance
Removed sections: none
Deferred placeholders: none
Templates requiring follow-up review (not modified by this command; out of scope
per constitution-command scope guard):
  - .specify/templates/plan-template.md — ⚠ verify Constitution Check gates reference these principles
  - .specify/templates/spec-template.md — ⚠ verify no conflicting assumptions
  - .specify/templates/tasks-template.md — ⚠ verify task categorization supports test-first ordering
  - .specify/templates/checklist-template.md — ⚠ verify alignment with Quality & Accuracy Gates
-->

# DeSponsor Constitution

## Core Principles

### I. Test-First (NON-NEGOTIABLE)
Tests MUST be written and approved before implementation begins, following the
Red-Green-Refactor cycle: write a failing test, implement the minimum code to
pass it, then refactor. Ad-detection logic and ML inference pipelines MUST have
regression test suites built on recorded, labeled audio fixtures that fail
before a change lands and pass after. No pull request MUST merge with failing,
skipped, or missing tests for the behavior it changes.

Rationale: Ad-skip accuracy is the core value proposition of DeSponsor. Untested
changes to detection or playback logic risk silent regressions in the exact
behavior users trust the app to get right.

### II. Simplicity (YAGNI)
Every change MUST start with the simplest design that satisfies the current,
concrete requirement. Configuration options, abstraction layers, or
extensibility hooks for use cases that do not yet exist MUST NOT be added.
Any new dependency, service, or abstraction MUST be justified by a present
need, stated in the PR description, not a hypothetical future one.

Rationale: A small team building a focused mobile app cannot absorb the
maintenance cost of premature generalization; complexity must earn its place.

### III. Library-First, Modular Design
Each feature area (ad detection, audio playback, subscription/feed management,
sync, etc.) MUST be built as a self-contained, independently testable module
with a clearly defined public interface, decoupled from Android UI and
lifecycle code wherever feasible. Modules MUST be testable outside the Android
framework (e.g., as JVM unit tests) whenever feasible, without requiring an
emulator or device.

Rationale: Keeps the detection engine and other core logic portable, fast to
test, and reusable if it is later extracted for another platform or a backend
service.

### IV. Observability & Structured Logging
Every ad-detection decision (detect / skip / no-skip) MUST emit structured,
privacy-respecting telemetry sufficient to diagnose false positives and false
negatives after the fact. Logs and telemetry MUST be structured (not
free-text) so detection quality can be measured over time, and MUST NOT
capture raw audio or personally identifying listening content beyond what is
strictly necessary for debugging.

Rationale: Because ad detection is ML-based and probabilistic, the team needs
a reliable feedback loop to catch model drift and user-impacting false
positives without compromising listener privacy.

## Quality & Accuracy Gates

Any change to ML-based ad detection (model updates, heuristic or threshold
changes) MUST be validated against a versioned, labeled evaluation dataset
before release, and MUST report false-positive and false-negative rates
against the current baseline. A regression in false-positive rate (skipping
real content) MUST NOT ship without explicit, documented sign-off, since
incorrectly skipping real content is a worse trust failure for users than
missing an ad. Any new Android platform capability that expands data access
or background execution (e.g., background audio processing, notification
access, network permissions) MUST be justified against its battery and
privacy impact before adoption.

## Development Workflow

All changes MUST go through code review before merging to the main branch. A
pull request MUST demonstrate: tests written before implementation (Principle
I), no unjustified complexity (Principle II), and structured logging for any
new or changed detection/decision path (Principle IV). Reviewers MUST verify
constitution compliance as part of review, not only functional correctness.

## Governance

This constitution supersedes all other engineering practices and conventions
for DeSponsor. Amendments require: (1) a documented rationale for the change,
(2) a version bump per semantic versioning — MAJOR for backward-incompatible
removal or redefinition of a principle, MINOR for adding a principle or
materially expanding guidance, PATCH for clarifications or wording fixes —
and (3) an updated Sync Impact Report prepended to this file. As a new
project under lightweight, sole-maintainer governance, amendments may be
approved directly by the project owner. All PRs and reviews MUST verify
compliance with this constitution, and any complexity that appears to
violate Principle II MUST be explicitly justified in the PR description.

**Version**: 1.0.0 | **Ratified**: 2026-08-20 | **Last Amended**: 2026-08-20
