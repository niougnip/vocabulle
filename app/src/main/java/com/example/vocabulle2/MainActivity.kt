@file:OptIn(ExperimentalLayoutApi::class)

package com.example.vocabulle2

import android.content.res.Resources.Theme
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vocabulle2.ui.theme.Vocabulle2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Vocabulle2Theme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val wtf = DutchWord(1, "un", "éen")
                    var wordList: List<DutchWord> = emptyList()

                    wordList += DutchWord(1, "un", "éen")
                    wordList += DutchWord(2, "deux", "twee")
                    wordList += DutchWord(3, "trois", "drie")
                    wordList += DutchWord(4, "vier", "quatre")

                    SuggestionScreen(wtf, wordList, true)
//                }
            }
        }
    }
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
fun AddCSVButton(onClick: () -> Unit) {
    IconButton(
        onClick = { onClick() }
    ) {
        Icon(
            painter = painterResource(R.drawable.baseline_file_add_24),
            contentDescription = "Add csv"
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
            AddCSVButton {
                Log.d("TEST", "add csv")
            }
        }
        FlowRow (
            modifier = Modifier.fillMaxSize().weight(6F),
            verticalArrangement = Arrangement.Center
        ) {
            WordToFind(wordToFind, isFrench)
            LazyColumn {
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
            Text("bob?")
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

@Composable
@Preview
fun SuggestionScreenPreview() {
    val wtf = DutchWord(1, "un", "éen")
    var wordList: List<DutchWord> = emptyList()

    wordList += DutchWord(1, "un", "éen")
    wordList += DutchWord(2, "deux", "twee")
    wordList += DutchWord(3, "trois", "drie")
    wordList += DutchWord(4, "vier", "quatre")

    SuggestionScreen(wtf, wordList, true)
}