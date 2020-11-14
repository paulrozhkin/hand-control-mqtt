package com.handcontrol.server.cache

import org.slf4j.LoggerFactory

/**
 * Save current state of prosthesis by its id
 */
object ProsthesisCache {

    private val logger = LoggerFactory.getLogger(ProsthesisCache::class.java)
    private val prosthesis = hashMapOf<String, Boolean>()

    private const val ACTIVE: Boolean = true
    private const val INACTIVE: Boolean = false

    fun addActiveState(id: String) {
        prosthesis[id] = ACTIVE
        logger.info("Prosthesis {} is online", id)
    }

    fun addInactiveState(id: String) {
        prosthesis[id] = INACTIVE
        logger.info("Prosthesis {} is offline", id)
    }

    fun getStateById(id: String) : Boolean? = prosthesis[id]

    fun getAllActive() = prosthesis.filter { it.value == ACTIVE }.keys

    fun clear() {
        prosthesis.clear()
        logger.info("Prosthesis cache is clear")
    }

    //todo add coroutine for change state to false after 60 sec

}