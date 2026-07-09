package com.kozyrevda.menstrualcalendar.navigation

import androidx.compose.runtime.mutableStateListOf

/**
 * Простая state-based навигация без сторонних библиотек:
 * back-stack на mutableStateListOf, Compose сам перерисует текущий экран.
 */
class Navigator(start: Screen) {
    private val backStack = mutableStateListOf(start)

    val current: Screen get() = backStack.last()
    val canGoBack: Boolean get() = backStack.size > 1

    /** Открыть экран поверх текущего. */
    fun navigate(screen: Screen) {
        if (backStack.last() != screen) backStack.add(screen)
    }

    /** Переключить корневую вкладку: стек схлопывается до одной записи. */
    fun switchTab(tab: Screen) {
        backStack.clear()
        backStack.add(tab)
    }

    /** Назад; если стек пуст — остаёмся на месте. */
    fun back() {
        if (canGoBack) backStack.removeAt(backStack.lastIndex)
    }

    /** Полная замена стека (онбординг → главный экран). */
    fun replaceAll(screen: Screen) {
        backStack.clear()
        backStack.add(screen)
    }
}
