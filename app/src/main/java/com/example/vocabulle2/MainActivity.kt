@file:OptIn(ExperimentalLayoutApi::class)

package com.example.vocabulle2

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.example.vocabulle2.ui.theme.Vocabulle2Theme
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Vocabulle2Theme {
                val wtf = DutchWord(1, "un", "éen")
                var wordList: List<DutchWord> = emptyList()

                wordList += DutchWord(1, "un", "éen")
                wordList += DutchWord(2, "deux", "twee")
                wordList += DutchWord(3, "trois", "drie")
                wordList += DutchWord(4, "vier", "quatre")

                SuggestionScreen(wtf, wordList, true)
            }
        }
    }
}

@Composable
private fun AddDataFromCSV() {
    val context = LocalContext.current
    val resultList : MutableList<List<String>> = emptyList<List<String>>().toMutableList()

    var FR = "FR"
    var NL = "NL"
    val columnMap : MutableMap<String, Int> = emptyMap<String, Int>().toMutableMap()

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { result ->
        result?.let {
            context.contentResolver.openInputStream(it).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String? = reader.readLine()

                    // Get the column header
                    if (line != null) {
                        if (line.contains(FR, true) && line.contains(NL, true)) {
                            val isoCodes = line.split(";")
                            isoCodes.forEachIndexed { index, code -> columnMap[code] = index }
                        } else {
                            // ERROR
                            Toast.makeText(context, "La première ligne doit contenir les codes iso NL et FR", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        // ERROR
                        Toast.makeText(context, "Le fichier est vide", Toast.LENGTH_LONG).show()
                    }

                    while (line != null) {
                        line = reader.readLine()
                        resultList += line.split(";")
                        Log.d("DATA", line)
                    }
                }
            }
        }

        val db = getDatabase(context)

    }

    return IconButton(
        onClick = { launcher.launch("text/*") }
    ) {
        Icon(
            painter = painterResource(R.drawable.baseline_file_add_24),
            contentDescription = "Add csv"
        )
    }
}

fun getDatabase(context: Context) : AppDatabase {
    val db by lazy {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "dutch_word.db"
        ).allowMainThreadQueries().build()
    }
    return db;
}

@Composable
fun SuggestionButton(value: DutchWord, isFrench: Boolean, onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        Modifier.padding(5.dp)
    )
    {
        Text(
            text = if (isFrench) value.french else value.dutch,
            fontSize = 26.sp
        )
    }
}

@Composable
fun WordToFind(value: DutchWord, isFrench: Boolean) {
    Text(
        text = if (isFrench) value.french else value.dutch,
        color = MaterialTheme.colorScheme.tertiary,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        fontSize = 32.sp
    )
}

@Composable
fun SuggestionScreen(wordToFind: DutchWord, suggestions: List<DutchWord>, isFrench: Boolean) {
    Column (
        Modifier.background(MaterialTheme.colorScheme.background, RectangleShape).fillMaxHeight()
    ) {
        Row (
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.weight(1F).fillMaxWidth().padding(15.dp)
        ) {
            AddDataFromCSV()
        }
        FlowRow (
            modifier = Modifier.fillMaxSize().weight(6F),
            verticalArrangement = Arrangement.Center
        ) {
            WordToFind(wordToFind, isFrench)
            LazyColumn (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                items(suggestions) {
                    suggestion -> SuggestionButton(suggestion, !isFrench) {
                        if (suggestion.french == wordToFind.french) {
                            // Success
                        }
                        else {
                            // Error
                        }
                    }
                }
            }
        }
        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.weight(1F).fillMaxWidth().padding(15.dp)
        ) {
            Text(
                text = "bob?",
                color = Color.White
            )
        }
    }
}

@Composable
fun SuccessScreen(word: DutchWord) {
    Column {
        Icon(
            painter = painterResource(R.drawable.baseline_check_circle_128),
            contentDescription = null
        )
        Text(
            text = word.french
        )
        Text (
            text = word.dutch
        )
    }
}
