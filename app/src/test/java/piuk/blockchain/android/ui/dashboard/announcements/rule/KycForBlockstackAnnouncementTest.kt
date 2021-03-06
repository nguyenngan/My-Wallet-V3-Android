package piuk.blockchain.android.ui.dashboard.announcements.rule

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import piuk.blockchain.android.ui.dashboard.announcements.AnnouncementQueries
import piuk.blockchain.android.ui.dashboard.announcements.DismissRecorder

class KycForBlockstackAnnouncementTest {

    private val dismissRecorder: DismissRecorder = mock()
    private val dismissEntry: DismissRecorder.DismissEntry = mock()
    private val queries: AnnouncementQueries = mock()

    private lateinit var subject: KycForBlockstackAnnouncement

    @Before
    fun setUp() {
        whenever(dismissRecorder[KycForBlockstackAnnouncement.DISMISS_KEY]).thenReturn(dismissEntry)
        whenever(dismissEntry.prefsKey).thenReturn(KycForBlockstackAnnouncement.DISMISS_KEY)

        subject =
            KycForBlockstackAnnouncement(
                dismissRecorder = dismissRecorder,
                queries = queries
            )
    }

    @Test
    fun `should not show, when already shown`() {
        whenever(dismissEntry.isDismissed).thenReturn(true)

        subject.shouldShow()
            .test()
            .assertValue { !it }
            .assertValueCount(1)
            .assertComplete()
    }

    @Test
    fun `should show, when not already shown, and the Gold kyc process hasn't been completed`() {
        whenever(dismissEntry.isDismissed).thenReturn(false)
        whenever(queries.isGoldComplete()).thenReturn(Single.just(false))

        subject.shouldShow()
            .test()
            .assertValue { it }
            .assertValueCount(1)
            .assertComplete()
    }

    @Test
    fun `should not show, when not already shown, and the Gold kyc process has been completed`() {
        whenever(dismissEntry.isDismissed).thenReturn(false)
        whenever(queries.isGoldComplete()).thenReturn(Single.just(false))

        subject.shouldShow()
            .test()
            .assertValue { it }
            .assertValueCount(1)
            .assertComplete()
    }

    @Test
    fun `should not show on error`() {
        whenever(dismissEntry.isDismissed).thenReturn(false)
        whenever(queries.isGoldComplete()).thenReturn(Single.error(Throwable()))

        subject.shouldShow()
            .test()
            .assertValue { !it }
            .assertValueCount(1)
            .assertComplete()
    }
}
