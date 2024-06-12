import com.example.compasstest.MainViewModel
import com.example.compasstest.data.ApiService
import com.example.compasstest.model.AboutData
import com.example.compasstest.model.AboutDataDao
import com.example.compasstest.utils.RequestState
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any


class MainViewModelTest : KoinTest {
    private lateinit var viewModel: MainViewModel
    private val mockCachedDataDao: AboutDataDao by inject()
    private val mockApiService : ApiService by inject()


    @Before
    fun setup() {
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                modules(module {
                    single { mockApiService }
                    single { mockCachedDataDao }
                })
            }
            viewModel = MainViewModel(mockApiService, mockCachedDataDao)
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun testFetchDataFromAPISuccess() = runTest {
        val mockContent = "Mocked content"
        val mockAboutData = AboutData(1, mockContent)
        val successState = RequestState.Success(mockContent)
        val successFlow = flow { emit(mockAboutData) }

        // Stubear el método fetchContent() para que devuelva successState
        `when`(mockApiService.fetchContent()).thenReturn(successState.data)

        // Stubear el método getAboutData() para que devuelva successFlow
        `when`(mockCachedDataDao.getAboutData(1)).thenReturn(successFlow)

        // Llamar al método a testear
        viewModel.fetchData()

        // Verificar que se haya llamado a fetchContent() en mockApiService
        verify(mockApiService).fetchContent()

        // Verificar que se haya insertado AboutData en mockCachedDataDao
        verify(mockCachedDataDao).insertAboutData(any())

        // Verificar que se haya llamado a getAboutData() en mockCachedDataDao
        verify(mockCachedDataDao).getAboutData(1)

        // Verificar que el estado del ViewModel sea de éxito
        assert(viewModel.requestState.value is RequestState.Success)

        // Verificar que every10thCharacter no esté vacío
        assert(viewModel.every10thCharacter.value.isNotEmpty())

        // Verificar que wordCount no esté vacío
        assert(viewModel.wordCount.value.isNotEmpty())

        // Verificar que no se hayan realizado más interacciones con los mocks
        verifyNoMoreInteractions(mockApiService, mockCachedDataDao)
    }



    @Test
    fun testFetchDataFromAPIError() = runTest {
        val errorState = RequestState.Error(("API error"))

        `when`(mockApiService.fetchContent()).thenReturn(errorState.exception)

        viewModel.fetchData()

        verify(mockApiService).fetchContent()

        assert(viewModel.requestState.value is RequestState.Error)

        verifyNoMoreInteractions(mockApiService, mockCachedDataDao)
    }

    @Test
    fun testLoadAboutDataFromLocalDatabase() = runTest {
        val mockContent = "Mocked content"
        val mockAboutData = AboutData(1, mockContent)
        val successFlow = flow { emit(mockAboutData) }

        `when`(mockCachedDataDao.getAboutData(1)).thenReturn(successFlow)

        viewModel.loadAboutData()

        verify(mockCachedDataDao).getAboutData(1)

        assert(viewModel.requestState.value is RequestState.Success)
        assert(viewModel.every10thCharacter.value.isNotEmpty())
        assert(viewModel.wordCount.value.isNotEmpty())

        verifyNoMoreInteractions(mockApiService, mockCachedDataDao)
    }

}
