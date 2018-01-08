package com.doordash

import com.doordash.db.getAlbum
import com.doordash.db.getAlbumDetails
import com.doordash.db.getAlbumsForGenre
import com.doordash.db.getContext
import com.doordash.db.getGenres
import com.doordash.view.browse
import com.doordash.view.deleteAlbum
import com.doordash.view.details
import com.doordash.view.home
import com.doordash.view.layout
import com.doordash.view.manage
import com.doordash.view.store
import kotlinx.html.DIV
import kotlinx.html.html
import kotlinx.html.stream.createHTML
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.MULTIPLE_CHOICES
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.filter.ServerFilters
import org.http4k.lens.Path
import org.http4k.lens.Query
import org.http4k.lens.int
import org.http4k.routing.ResourceLoader
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.static
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun html(content: DIV.() -> Unit): Response {
    val body = createHTML().html {
        layout {
            content()
        }
    }
    return Response(OK).body(body)
}

fun never(): Response {
    return Response(NOT_FOUND)
}

val overview = { _: Request ->
    val genres = getGenres(getContext()).map { genre -> genre.name }.toList()
    html { store(genres) }
}

val browse = { request: Request ->
    val genre = Query.required("genre")(request)
    val albums = getAlbumsForGenre(genre, getContext()).toList()
    html { browse(genre, albums) }
}

val details = { request: Request ->
    val id = Path.int().of("id")(request)
    val album = getAlbumDetails(id, getContext())
    album?.let { html { details(it) } } ?: never()
}

val manage = { request: Request ->
    val albums = getAlbumDetails(getContext()).toList()
    html { manage(albums) }
}

val deleteAlbum = { request: Request ->
    val id = Path.int().of("id")(request)
    val album = getAlbum(id, getContext())
    album?.let { html { deleteAlbum(it.title) } } ?: never()
}

val deleteAlbumAction = { request: Request ->
    val id = Path.int().of("id")(request)
    val album = getAlbum(id, getContext())
    album?.let {
        com.doordash.db.deleteAlbum(album)
        Response(FOUND).header("Location", "/admin/manage")
    } ?: never()
}

fun main(args: Array<String>) {
    val routing = routes(
        "/" bind Method.GET to { _ -> html { home() } },
        "/store" bind Method.GET to overview,
        "/store" bind routes(
            "/browse" bind Method.GET to browse,
            "/details/{id}" bind Method.GET to details
        ),
        "/admin" bind routes(
            "/manage" bind Method.GET to manage,
            "/delete/{id}" bind Method.GET to deleteAlbum,
            "/delete/{id}" bind Method.POST to deleteAlbumAction
        ),
        "/assets" bind static(ResourceLoader.Directory("src/main/resources/com/doordash/assets"))
    )

    ServerFilters
        .CatchLensFailure
        .then(routing)
        .asServer(Jetty(8000))
        .start()
}
