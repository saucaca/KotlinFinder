package org.example.library.domain.storage

import io.ktor.client.features.cookies.CookiesStorage
import io.ktor.http.Cookie
import io.ktor.http.Url


class PersistentCookiesStorage(
    private val storage: KeyValueStorage
): CookiesStorage {
    private val cookies: MutableMap<String, String>

    init {
        this.cookies = this.cookiesFromStorage()
    }

    override suspend fun get(requestUrl: Url): List<Cookie> {
        return this.cookies.map{ Cookie(name = it.key, value = it.value) }
    }

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie): Unit {
        this.cookies.set(key = cookie.name, value = cookie.value)

        this.storage.cookies = this.stringFromCookies(this.cookies)
    }

    override fun close() {}

    private fun cookiesFromStorage(): MutableMap<String, String> {
        val cookiesStr: String = this.storage.cookies ?: return mutableMapOf()
        val pairs: List<String> = cookiesStr.split(",")

        val cookies: MutableMap<String, String> = mutableMapOf()

        for (p: String in pairs) {
            val cookie: List<String> = p.split(":")

            if (cookie.count() == 2) {
                cookies.set(key = cookie[0], value = cookie[1])
            }
        }

        return cookies
    }

    private fun stringFromCookies(cookies: Map<String, String>): String {
        var str: String = ""

        for ((k, v) in cookies) {
            str = "$str,$k:$v"
        }

        return str
    }
}