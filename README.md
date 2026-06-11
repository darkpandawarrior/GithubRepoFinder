# GithubRepoFinder

A GitHub repository search app in Kotlin — clean MVVM proof-of-concept built around the [GitHub Search API](https://docs.github.com/en/rest/search).

## Features

- Search GitHub repositories against the Search API
- Connectivity-aware UI — a `ConnectivityLiveData` wrapper observes network state and gates requests
- Explicit loading / success / error / empty states
- Graceful API error parsing (rate limits, validation errors)

## Stack

| Layer | Tech |
|---|---|
| UI | XML Views + ViewBinding, Material Components |
| Presentation | ViewModel + LiveData, lifecycle-aware collection |
| Concurrency | Kotlin Coroutines |
| Network | Retrofit 2 + Gson, OkHttp logging interceptor |
| Pattern | MVVM + Repository |

## Structure

```
ui/                  → MainActivity, adapters, loading states
viewmodels/          → GithubSearchViewModel
repositories/        → GithubRepository (single source of truth)
api/                 → Retrofit service, REST client
models/              → search response + error models
utils/               → connectivity, keyboard, view-binding helpers
```

> 📌 For my current production-grade work — Compose Multiplatform, sensor-fusion location tracking, offline-first Room — see [MileTrackerDemo](https://github.com/darkpandawarrior/MileTrackerDemo).
