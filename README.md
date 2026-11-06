# GithubRepoFinder

An Android app that searches public GitHub repositories and lists the results in pages of ten. I wrote it in May 2023 as a small exercise in MVVM with a real REST API, and the last code commit is from that month. It is not maintained, and I keep it up as a record of how I structured a Views-and-LiveData app before moving to Compose and Kotlin Multiplatform.

You type a query, the app calls `GET /search/repositories` on `api.github.com`, and each result card shows the owner avatar, repo name, star count, description and language. Tapping a card opens a dialog with the avatar, repo name, owner, stars, description and language. Scrolling to the end loads the next page.

## Run it

You need Android Studio with JDK 17 (the module compiles at Java 17) and an Android 8.0 device or emulator. `minSdk` is 26 and `compileSdk` is 33.

```
./gradlew installDebug
```

or open the folder in Android Studio and press Run. The Gradle wrapper pins Gradle 8.0 and the Android Gradle Plugin is 8.0.1 with Kotlin 1.8.20, so a current Android Studio will offer to upgrade the project. I have not done that.

No API key is needed. The app sends unauthenticated requests, and GitHub gives unauthenticated clients 10 search requests per minute. I checked that today against `https://api.github.com/rate_limit`, which reports `"search": {"limit": 10, ...}`. If you page quickly you will hit it. The call comes back without a body and the app shows the same "No More Results" toast, so the limit looks like the end of the results.

## What it does

- Refuses an empty query with a toast. (`GithubSearchViewModel` declares a 500 ms `debouncePeriod` that nothing reads, so there is no debounce.)
- Checks connectivity before each request (`checkIfOnline` in the ViewModel) and shows "No Internet Connection" instead of firing the call. `ConnectivityLiveData` exists in `utils/` but nothing uses it.
- Shows a progress bar while a page loads and a message when a query returns nothing.
- Loads ten results per page and fetches the next page when you reach the end of the list, until the loaded count equals `total_count`. GitHub serves at most 1,000 results per search: page 101 at ten per page returns HTTP 422 (checked today), which the app shows as a "No More Results" toast.
- Opens a bottom sheet with six sort options (stars, watchers, score, name, created, updated) and ascending or descending order.

One limit worth knowing about: only stars and updated are sent to GitHub as the `sort` parameter. The other four are applied on the device, to the ten items of the page that just arrived, so "sort by name" orders within a page and not across the whole result set. The `order` parameter is sent either way. The sorting is in `GithubSearchViewModel.manageResponse` and `getReposService`.

## How it is built

```
api/               Retrofit interface (ApiService), RestClient, base URL constants
models/            search response, repo item, error body
repositories/      GithubRepository, a one-line wrapper over ApiService
viewmodels/        GithubSearchViewModel
viewmodelfactories/ its factory
presenter/         RepoSearchMethods, the interface the ViewModel and Activity share
ui/                MainActivity, the result adapter, loading states
utils/             connectivity LiveData, keyboard, ViewBinding helpers
```

XML layouts with ViewBinding, Material Components 1.8.0, ViewModel and LiveData from lifecycle 2.6.1, coroutines 1.7.0, Retrofit 2.9.0 with Gson, and the OkHttp logging interceptor.

The ViewModel holds a reference to the Activity (`context as MainActivity`), which is the first thing I would change. It ties the ViewModel to one Activity instance, which defeats the point of having a ViewModel.

## Tests

There are none that test behaviour. `ExampleUnitTest` asserts that 2 + 2 is 4 and `ExampleInstrumentedTest` is the Android Studio template.

## Where I went next

The structure here, with a repository over a service and a ViewModel exposing state, is the same one [Doori](https://github.com/darkpandawarrior/Doori) uses, with Compose Multiplatform, Room and a tested offline path in place of LiveData and a live-only network call.

Licensed under GPL v3, see `LICENSE`.
