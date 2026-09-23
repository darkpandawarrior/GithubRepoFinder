# GithubRepoFinder

A 2023 GitHub repository search proof of concept in Kotlin, built around the [GitHub Search API](https://docs.github.com/en/rest/search).

## Features

- Search GitHub repositories against the Search API
- An online check before each search request
- A loading spinner, results list, error toast and empty-result message
- Sorting and pagination controls

## Stack

| Layer | Tech |
|---|---|
| UI | XML Views + ViewBinding, Material Components |
| Presentation | ViewModel + LiveData, lifecycle-aware collection |
| Concurrency | Kotlin Coroutines |
| Network | Retrofit 2 + Gson, OkHttp logging interceptor |
| Pattern | MVVM + a thin API repository |

## Structure

```
ui/                  → MainActivity, adapters, loading states
viewmodels/          → GithubSearchViewModel
repositories/        → GithubRepository (single source of truth)
api/                 → Retrofit service, REST client
models/              → search response + error models
utils/               → connectivity, keyboard, view-binding helpers
```

For a newer Kotlin Multiplatform project with Compose UI, local data and a browser preview, see [Doori](https://github.com/darkpandawarrior/Doori).
