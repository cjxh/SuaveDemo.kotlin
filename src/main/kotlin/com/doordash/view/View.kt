package com.doordash.view

import com.doordash.db.tables.records.AlbumdetailsRecord
import com.doordash.db.tables.records.AlbumsRecord
import com.doordash.path.withParam
import kotlinx.html.DIV
import kotlinx.html.FormMethod
import kotlinx.html.HTML
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.br
import kotlinx.html.div
import kotlinx.html.em
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.img
import kotlinx.html.li
import kotlinx.html.link
import kotlinx.html.p
import kotlinx.html.strong
import kotlinx.html.submitInput
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.title
import kotlinx.html.tr
import kotlinx.html.ul

fun HTML.layout(block: DIV.() -> Unit) {
    head {
        title("Suave Music Store")
        link(rel = "stylesheet", type = "text/css", href = "/assets/styles.css")
    }

    body {
        div {
            attributes["id"] = "header"

            h1 {
                a("/") { +"Http 4K Music Store" }
            }
        }

        div {
            attributes["id"] = "main"

            block()
        }

        div {
            attributes["id"] = "footer"

            +"Built with "
            a("https://www.http4k.org") { +"Http 4K" }
        }
    }
}

fun DIV.home() {
    +"Home"
}

fun DIV.store(genres: List<String>) {
    h2 { +"Browse Genres" }
    p { +"Select from ${genres.count()} genres" }
    ul {
        genres.map { genre ->
            val url = withParam("genre".to(genre), "/store/browse")
            li { a(url) { +genre } }
        }
    }
}

fun DIV.browse(genre: String, albums: List<AlbumsRecord>) {
    h2 { +"Genre $genre" }
    ul {
        albums.map { album ->
            li { a("/store/details/${album.albumid}") { +album.title } }
        }
    }
}

fun DIV.details(album: AlbumdetailsRecord) {
    h2 { +album.title }
    p { img(src = "/assets/${album.albumarturl}") }
    div(classes = "album-details") {
        val captions = listOf("Genre: ".to(album.genre), "Artist: ".to(album.artist), "Price: ".to(album.price))
        captions.map { caption ->
            p {
                em { +caption.first }
                +caption.second.toString()
            }
        }
    }
}

fun DIV.notFound() {
    h2 { +"Page not found" }
    p { +"Could not find the requested resource" }
    p {
        +"Back to "
        a("/") { +"Home" }
    }
}

fun String.truncate(chars: Int): String {
    return when (this.length) {
        in 0..chars -> this
        else -> "${this.substring(0, chars - 3)}..."
    }
}

fun DIV.manage(albums: List<AlbumdetailsRecord>) {
    h2 { +"Index" }
    table {
        tr {
            listOf("Artist", "Title", "Genre", "Price", "Action").map { th { +it } }
        }
        albums.map { album ->
            tr {
                val cells = listOf(album.artist.truncate(25), album.title.truncate(25), album.genre, album.price.toString())
                cells.map {
                    td { +it }
                }
                td { a("/admin/delete/${album.albumid}") { +"Delete" } }
            }
        }
    }
}

fun DIV.deleteAlbum(albumTitle: String) {
    h2 { +"Delete Confirmation" }
    p {
        +"Are you sure you want to delete the album titled"
        br {  }
        strong { +albumTitle }
        +"?"
    }
    form(method = FormMethod.post) { submitInput { value = "Delete" } }
    div { a("/admin/manage") { +"Back to list"} }
}
