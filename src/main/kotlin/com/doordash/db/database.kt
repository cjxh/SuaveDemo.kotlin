package com.doordash.db

import com.doordash.db.tables.Albumdetails
import com.doordash.db.tables.Albums
import com.doordash.db.tables.Genres
import com.doordash.db.tables.records.AlbumdetailsRecord
import com.doordash.db.tables.records.AlbumsRecord
import com.doordash.db.tables.records.GenresRecord
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.sql.DriverManager

fun getContext(): DSLContext {
    val username = "suave"
    val password = "1234"
    val url = "jdbc:postgresql://localhost:5432/suavemusicstore"

    val conn = DriverManager.getConnection(url, username, password)
    return DSL.using(conn, SQLDialect.POSTGRES)
}

fun getGenres(context: DSLContext): Sequence<GenresRecord> {
    return context.fetch(Genres.GENRES).asSequence()
}

fun getAlbumsForGenre(genreName: String, context: DSLContext): Sequence<AlbumsRecord> {
    return context.select()
        .from(Albums.ALBUMS.join(Genres.GENRES).on(Albums.ALBUMS.GENREID.eq(Genres.GENRES.GENREID)))
        .where(Genres.GENRES.NAME.eq(genreName))
        .fetchInto(Albums.ALBUMS)
        .asSequence()
}

fun getAlbumDetails(id: Int, context: DSLContext): AlbumdetailsRecord? {
    return context
        .fetchOptional(Albumdetails.ALBUMDETAILS, Albumdetails.ALBUMDETAILS.ALBUMID.eq(id))
        .orElseGet { null }
}

fun getAlbumDetails(context: DSLContext): Sequence<AlbumdetailsRecord> {
    return context.fetch(Albumdetails.ALBUMDETAILS)
        .asSequence()
        .sortedBy { it.artist }
}

fun getAlbum(id: Int, context: DSLContext): AlbumsRecord? {
    return context
        .fetchOptional(Albums.ALBUMS, Albums.ALBUMS.ALBUMID.eq(id))
        .orElseGet { null }
}

fun deleteAlbum(album: AlbumsRecord) {
    album.delete()
}
