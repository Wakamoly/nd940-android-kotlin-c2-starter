package com.udacity.asteroidradar

object Constants {
    const val API_QUERY_DATE_FORMAT = "YYYY-MM-dd"
    const val DEFAULT_END_DATE_DAYS = 7
    const val BASE_URL = "https://api.nasa.gov/"

    /**
     * Normally not a great idea to expose our API key, but in this use-case without user auth
     * on our own back-end we'll allow it.
     */
    const val API_KEY = "9CL6E2NzNAwEofUZlbJ2EIvnEq6HEbVRYksDTJSX"
}