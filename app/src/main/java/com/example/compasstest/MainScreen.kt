import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compasstest.MainViewModel
import com.example.compasstest.utils.RequestState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun MainScreen() {
    val viewModel: MainViewModel = getViewModel()
    val every10thCharacter by viewModel.every10thCharacter.collectAsState()
    val wordCount by viewModel.wordCount.collectAsState()
    val requestState by viewModel.requestState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { viewModel.fetchData() }) {
            Text("Fetch Data")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (requestState) {
            is RequestState.Loading -> {
                CircularProgressIndicator()
            }
            is RequestState.Success -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    }) {
                        Text("Every 10th Character")
                    }

                    Button(onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(every10thCharacter.size + 1)
                        }
                    }) {
                        Text("Word Count")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Text("Every 10th Character:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    items(every10thCharacter) { word ->
                        Text(word)
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Word Count:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    items(wordCount.toList()) { (word, count) ->
                        Text("$word: $count")
                    }
                }
            }
            is RequestState.Error -> {
                Text("Error: ${(requestState as RequestState.Error).exception.toString()}")
            }
            else -> {
                Text("Press the button to fetch data")
            }
        }
    }
}
