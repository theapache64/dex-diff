package com.github.theapache64.dexdiff.ui.home

import com.github.theapache64.dexdiff.app.App
import com.github.theapache64.dexdiff.data.repo.AppRepoImpl
import com.github.theapache64.expekt.should
import org.junit.jupiter.api.Test

class HomeViewModelTest {

    @Test
    fun `Normal flow`() {
        App.args = arrayOf(
            "src/test/resources/with-fullmode.apk",
            "src/test/resources/without-fullmode.apk"
        )

        val actualStatuses = mutableListOf<String>()

        HomeViewModel(
            AppRepoImpl(),
        ).status.observe {
            actualStatuses.add(it)
        }

        val expectedStatuses = listOf(
            HomeViewModel.INIT_MSG,
            HomeViewModel.DONE_MSG
        )


        actualStatuses.should.equal(expectedStatuses)
    }
}