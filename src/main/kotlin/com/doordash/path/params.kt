package com.doordash.path

fun withParam(param: Pair<String, String>, path: String): String {
    return "$path?${param.first}=${param.second}"
}
