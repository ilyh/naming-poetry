# Import Performance Optimization Spec

**Date:** 2026-05-18
**Status:** draft

## Problem

`POST /api/name/admin/import` imports 9,248 poems from `sample_poems.json`. Each poem's content generates one `PoemWord` record per Chinese character — ~513k PoemWord inserts plus ~9k Poem inserts, totaling ~522k individual `save()` calls. Each `save()` is a separate transaction with its own connection acquire/commit cycle. The result: import takes too long and typically stops around 800 poems before the HTTP connection gives up.

## Solution

### Single-file change: `DataImportService.java`

**1. Add `@Transactional` on `importFromResource()`**

One transaction boundary for the entire import. Eliminates per-save commit overhead. Rollback on any exception.

**2. Batch Poem saves**

Accumulate new Poem entities into a list, flush via `poemRepository.saveAll(batch)` every 500 poems. Keeps memory bounded while avoiding 9k individual inserts.

**3. Batch PoemWord saves**

Accumulate PoemWord entities into a list, flush via `poemWordRepository.saveAll(batch)` every 500 words. Reduces 513k individual inserts to ~1026 batch inserts.

### Scope

- Only `DataImportService.java` is modified
- No API contract changes
- No DB schema changes
- No config changes

### Error handling

- Spring rolls back the entire transaction on any `RuntimeException`, leaving DB in pre-import state
- The controller already returns the exception message to the caller via `catch (Exception e)`

### Behavior unchanged

- Dedup logic (existingKeys check) remains identical
- Tag assignment logic unchanged
- Return message format unchanged
