package com.example.compasstest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.compasstest.data.ApiService
import com.example.compasstest.model.AboutData
import com.example.compasstest.model.AboutDataDao
import com.example.compasstest.utils.RequestState
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var aboutDataDao: AboutDataDao

    private lateinit var mainViewModel: MainViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mainViewModel = MainViewModel(apiService, aboutDataDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testLoadAboutData_Success() = runTest {
        val aboutData = AboutData(1, "<p> Compass Hello World </p>")
        val flow = flowOf(aboutData)

        `when`(aboutDataDao.getAboutData(1)).thenReturn(flow)

        val requestStateFlow = mutableListOf<RequestState<String>>()
        val every10thCharacterFlow = mutableListOf<List<String>>()
        val wordCountFlow = mutableListOf<Map<String, Int>>()

        val requestStateJob = launch { mainViewModel.requestState.toList(requestStateFlow) }
        val every10thCharacterJob =
            launch { mainViewModel.every10thCharacter.toList(every10thCharacterFlow) }
        val wordCountJob = launch { mainViewModel.wordCount.toList(wordCountFlow) }

        mainViewModel.loadAboutData()

        advanceUntilIdle()

        val expected10thCharacters = listOf("s", "d")

        assertEquals(
            listOf(RequestState.Loading, RequestState.Success(aboutData.content)),
            requestStateFlow
        )
        assertEquals(
            expected10thCharacters,
            every10thCharacterFlow.flatten().filter { it.isNotEmpty() })

        assertEquals(
            listOf(mapOf("<p>" to 1, "Compass" to 1, "Hello" to 1, "World" to 1, "</p>" to 1)),
            wordCountFlow.filter { it.isNotEmpty() }
        )

        requestStateJob.cancel()
        every10thCharacterJob.cancel()
        wordCountJob.cancel()
    }

    @Test
    fun testFetchData_Success() = runTest {
        val content = "<p> Compass Another Test </p>"

        `when`(apiService.fetchContent()).thenReturn(content)

        val requestStateFlow = mutableListOf<RequestState<String>>()
        val every10thCharacterFlow = mutableListOf<List<String>>()
        val wordCountFlow = mutableListOf<Map<String, Int>>()

        val requestStateJob = launch { mainViewModel.requestState.toList(requestStateFlow) }
        val every10thCharacterJob =
            launch { mainViewModel.every10thCharacter.toList(every10thCharacterFlow) }
        val wordCountJob = launch { mainViewModel.wordCount.toList(wordCountFlow) }

        mainViewModel.fetchData()

        advanceUntilIdle()

        val expected10thCharacters = listOf("s", "s")

        assertEquals(listOf(RequestState.Loading, RequestState.Success(content)), requestStateFlow)
        assertEquals(
            expected10thCharacters,
            every10thCharacterFlow.flatten().filter { it.isNotEmpty() })
        assertEquals(
            listOf(mapOf("<p>" to 1, "Compass" to 1, "Another" to 1, "Test" to 1, "</p>" to 1)),
            wordCountFlow.filter { it.isNotEmpty() }
        )

        requestStateJob.cancel()
        every10thCharacterJob.cancel()
        wordCountJob.cancel()
    }

    @Test
    fun testFetchData_Error() = runTest {
        `when`(apiService.fetchContent()).thenThrow(RuntimeException("An error occurred with your network connection"))

        val requestStateFlow = mutableListOf<RequestState<String>>()

        val requestStateJob = launch { mainViewModel.requestState.toList(requestStateFlow) }

        mainViewModel.fetchData()

        advanceUntilIdle()

        assertEquals(
            listOf(RequestState.Loading, RequestState.Error("An error occurred with your network connection")),
            requestStateFlow
        )

        requestStateJob.cancel()
    }
}
