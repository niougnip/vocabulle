@file:OptIn(ExperimentalLayoutApi::class)

package com.example.vocabulle2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import java.nio.charset.Charset
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "dutch_word.db"
        ).allowMainThreadQueries().build()
    }

//    val isoFR = "FR"
//    val isoNL = "NL"

//    private var words: MutableList<DutchWord> = emptyList<DutchWord>().toMutableList()
//    private var wordToFind: DutchWord? = null
//    private var isFrench = false
    private val listSize = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Vocabulle2Theme {
                SuggestionScreen()
            }
        }
    }

    @SuppressLint("UnsafeIntentLaunch")
    @Composable
    private fun AddDataFromCSV() {
        val context = LocalContext.current

        val columnMap: MutableMap<String, Int> = emptyMap<String, Int>().toMutableMap()

        val launcher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { result ->
                result?.let {
                    context.contentResolver.openInputStream(it).use { inputStream ->
                        BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF8"))).use { reader ->
                            // Get the column header
                            var line: String? = reader.readLine()
                            if (line != null) {
                                if (line.contains(isoFR, true) && line.contains(isoNL, true)) {
                                    val isoCodes = line.split(";")
                                    isoCodes.forEachIndexed { index, code ->
                                        columnMap[code] = index
                                    }
                                } else {
                                    // ERROR
                                    Toast.makeText(
                                        context,
                                        "La première ligne doit contenir les codes iso NL et FR",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } else {
                                // ERROR
                                Toast.makeText(context, "Le fichier est vide", Toast.LENGTH_LONG)
                                    .show()
                            }

                            // Get the items
                            while (line != null) {
                                line = reader.readLine()
                                if (line == null) break
                                val resultLine = line.split(";")
                                val french: String =
                                    columnMap[isoFR]?.let { iso -> resultLine[iso] }.toString()
                                val dutch: String =
                                    columnMap[isoNL]?.let { iso -> resultLine[iso] }.toString()
                                val exists = db.dao.findItemFromFrench(french)
                                if (exists == null) db.dao.insert(DutchWord(null, french, dutch))
                            }
                        }
                    }
                    intent = Intent(this@MainActivity, SuccessActivity::class.java)
                    finish()
                    startActivity(intent)
                }
            }

        return IconButton(
            onClick = { launcher.launch("text/*") }
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_add_512),
                contentDescription = "Add csv",
                tint = Color.White
            )
        }
    }

    @Composable
    fun SuggestionButton(value: DutchWord, isFrench: Boolean, onClick: () -> String) {
        var resultString by remember { mutableStateOf("") }
        val color = getColor(resultString)
        Button(
            onClick = { resultString = onClick() },
            colors= ButtonDefaults.buttonColors(color),
            modifier = Modifier.padding(5.dp)
        )
        {
            Text(
                text = if (isFrench) value.french else value.dutch,
                fontSize = 26.sp
            )
        }
    }

    @Composable
    private fun getColor(resultString: String): Color {
        val success: String = "SUCCESS"
        val error: String = "ERROR"
        if (resultString == success) {
            return Color(117, 201, 69, 255)
        } else if (resultString == error) {
            return Color(196, 57, 57, 255)
        } else {
            return MaterialTheme.colorScheme.primary
        }
    }

    @Composable
    fun WordToFind(value: DutchWord, isFrench: Boolean) {
        Box (
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(20.dp))
        {
            Image(
                painter = painterResource(R.drawable.phyla),
                contentDescription = null,
                modifier = Modifier
            )
            Text(
                text = if (isFrench) value.french else value.dutch,
                color = MaterialTheme.colorScheme.background,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 32.sp,
            )
        }
    }

    @SuppressLint("MutableCollectionMutableState", "UnsafeIntentLaunch")
    @Composable
    fun WordTest() {
        val success = "SUCCESS"
        val error = "ERROR"

//        var words : MutableList<DutchWord> = createList(listSize)
        val mutableWords = remember { mutableStateOf(createList(listSize)) }
        var wordToFind = pickWord(mutableWords.value)
        var isFrench = randomLanguage()

        WordToFind(wordToFind, isFrench)
        Column {
            Spacer(Modifier.height(250.dp))
        }
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(mutableWords.value) { word ->
                SuggestionButton(word, !isFrench) {
                    if (word.french == wordToFind.french) {
//                        mutableWords.value = createList(listSize)
//                        wordToFind = pickWord(mutableWords.value)
//                        isFrench = randomLanguage()
                        Log.d("BUTTON", mutableWords.value[1].french)

                        intent = Intent(this@MainActivity, SuccessActivity::class.java)
                        val bundle = Bundle()
                        bundle.putString(isoFR, wordToFind.french)
                        bundle.putString(isoNL, wordToFind.dutch)
                        intent.putExtras(bundle)
                        startActivity(intent)
                        finish()

                        return@SuggestionButton success
                    } else {
                        Log.d("BUTTON", mutableWords.value[1].french)
                        return@SuggestionButton error
                    }
                }
            }
        }
    }

    private fun createList(size: Int = 4) : MutableList<DutchWord> {
        val count = db.dao.countItems()

        val resultList : MutableList<DutchWord> = emptyList<DutchWord>().toMutableList()
        var index: Int
        if (count > 0) {
            for (i in 1..<size) {
                var word: DutchWord
                do {
                    index = Random.nextInt(0, count) + 1
                    word = db.dao.findByOffset(index)
                    if (word == null) continue
                    if (resultList.map { w -> w.french }.none { w -> w == word.french }) {
                        resultList += word
                    }
                } while (resultList.size <= i)
            }
//            wordToFind = words[Random.nextInt(0, listSize - 1)]
        }
//        isFrench = Random.nextBoolean()
        return resultList
    }

    private fun pickWord(list: MutableList<DutchWord>) : DutchWord {
        return list[Random.nextInt(0, listSize - 1)]
    }

    private fun randomLanguage() : Boolean {
        return Random.nextBoolean()
    }

    @Composable
    private fun HelloContent() {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hello!",
                modifier = Modifier.padding(bottom = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedTextField(
                value = "",
                onValueChange = { },
                label = { Text("Name") }
            )
        }
    }

    @Composable
    fun SuggestionScreen() {
        val count = db.dao.countItems()

        val padding = 20.dp

        return Column(
            Modifier.background(MaterialTheme.colorScheme.background, RectangleShape)
                .fillMaxHeight()
        ) {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.weight(1F).fillMaxWidth().padding(padding)
            ) {
                AddDataFromCSV()
            }
            FlowRow(
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(6F).fillMaxWidth().padding(padding)
            ) {
                if (count == 0) {
                    Text(
                        text = "La liste de mot est vide",
                        color = Color.White
                    )
                } else {
                    WordTest()
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.weight(1F).fillMaxWidth().padding(padding)
            ) {
                Text(
                    text = count.toString() + " mots",
                    color = Color.White
                )
            }
        }
    }

    companion object {
        val isoFR: String = "FR"
        val isoNL: String = "NL"
    }


}