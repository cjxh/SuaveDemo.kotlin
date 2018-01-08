# Suave Music Store Demo in Kotlin

This is the same [Suave demo app][Suave music store] (a Music web store), but written in Kotlin. This app is written in
a purely functional style. It uses [Http 4K][Http 4k] to treat the server as a function, and [Jooq] for typed database 
queries.

See my other [F# implementation][F# demo app] of this app in F# for a comparison between Kotlin and F#.

## Running

1. Create a Docker image of the database: `docker build -t theimowski/suavemusicstore_db:0.1 postgres`
2. Start a container with the image: `docker run --name suavemusicstore_db -e POSTGRES_PASSWORD=mysecretpassword -d -p 5432:5432 theimowski/suavemusicstore_db:0.1`
3. Run the app: `gradlew run`
4. App should be available at `http://localhost:8000`

[Suave Music Store]: https://theimowski.gitbooks.io/suave-music-store
[F# demo app]: https://www.github.com/danschultz/suavedemo.fsharp
[Http 4k]: https://www.http4k.org/
[Jooq]: https://www.jooq.org/
