package com.example.vocabulle2.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.example.vocabulle2.AppDatabase
import com.example.vocabulle2.IsoCodeList
import com.example.vocabulle2.R
import com.example.vocabulle2.TranslationEntity
import com.example.vocabulle2.ui.theme.RoundedCaret
import com.example.vocabulle2.ui.theme.Success
import com.example.vocabulle2.ui.theme.Vocabulle2Theme
import com.example.vocabulle2.ui.theme.Warning
import kotlin.random.Random

class WordTestActivity: ComponentActivity() {
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "dutch_word.db"
        ).allowMainThreadQueries().build()
    }

    private val success = "SUCCESS"
    private val error = "ERROR"

    private var isoCode = "NL"

    private val listSize = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        isoCode = intent.getStringExtra("ISO").toString()

        setContent {
            Vocabulle2Theme {
                SuggestionScreen()
            }
        }
    }

    @Composable
    fun SuggestionButton(value: String, onClick: () -> String) {
        var resultString by remember { mutableStateOf("") }
        val color = getColor(resultString)
        Button(
            onClick = { resultString = onClick() },
            colors= ButtonDefaults.buttonColors(color),
            modifier = Modifier.padding(10.dp)
        )
        {
            Text(
                text = value,
                fontSize = 26.sp,
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    private fun getColor(resultString: String): Color {
        val success: String = "SUCCESS"
        val error: String = "ERROR"
        if (resultString == success) {
            return Success
        } else if (resultString == error) {
            return Warning
        } else {
            return MaterialTheme.colorScheme.primary
        }
    }

    @Composable
    fun WordToFind(value: String) {
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
                text = value,
                color = MaterialTheme.colorScheme.background,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 32.sp
            )
        }
    }

    @SuppressLint("MutableCollectionMutableState")
    @Composable
    fun WordTest() {

        val mutableWords = remember { mutableStateOf(createList(listSize)) }
        val wordToFind = pickWord(mutableWords.value)
        val isFrench = randomTranslation()
        val wordValue: String = if (isFrench) wordToFind.french else wordToFind.other

        WordToFind(wordValue)
        Column {
            Spacer(Modifier.height(250.dp))
        }
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(mutableWords.value) { word ->
                SuggestionButton(if (isFrench) word.other else word.french) {
                    if (word.french == wordToFind.french) {
                        showSuccessScreen(wordToFind)
                        return@SuggestionButton success
                    } else {
                        return@SuggestionButton error
                    }
                }
            }
        }
    }

    @SuppressLint("UnsafeIntentLaunch")
    private fun showSuccessScreen(word: TranslationEntity) {
        intent = Intent(this@WordTestActivity, SuccessActivity::class.java)
        val bundle = Bundle()
        word.uid?.let { bundle.putInt("ID", it) }
        bundle.putString("FR", word.french)
        bundle.putString("OTHER", word.other)
        bundle.putString("ISO", isoCode)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }

    private fun createList(size: Int = 4) : MutableList<TranslationEntity> {
        val count = db.dao.countItems(isoCode)

        val resultList : MutableList<TranslationEntity> = emptyList<TranslationEntity>().toMutableList()
        var index: Int
        if (count > 0) {
            for (i in 1..<size) {
                var word: TranslationEntity
                do {
                    index = Random.nextInt(0, count) + 1
                    word = db.dao.findByOffset(isoCode, index)
                    if (word == null) continue
                    if (resultList.map { w -> w.french }.none { w -> w == word.french }) {
                        resultList += word
                    }
                } while (resultList.size <= i)
            }
        }
        return resultList
    }

    private fun pickWord(list: MutableList<TranslationEntity>) : TranslationEntity {
        return list[Random.nextInt(0, listSize - 1)]
    }

    private fun randomTranslation() : Boolean {
        return Random.nextBoolean()
    }

    @SuppressLint("UnsafeIntentLaunch")
    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun SuggestionScreen() {
        val count = db.dao.countItems(isoCode)

        val padding = 20.dp

        return Column(
            Modifier.background(MaterialTheme.colorScheme.background, RectangleShape)
                .fillMaxHeight()
        ) {
            Row(
                modifier = Modifier.weight(1F).padding(padding)
            ) {
                Box(modifier = Modifier.padding(0.dp, 15.dp).clickable {
                    intent = Intent(this@WordTestActivity, MainActivity::class.java)
                    val bundle = Bundle()
                    bundle.putBoolean("SHOW_SCREEN", true)
                    intent.putExtras(bundle)
                    startActivity(intent)
                    finish()
                }) { RoundedCaret(180F, 0.5F) }
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
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = count.toString() + " mots",
                        color = Color.White
                    )
                }
            }
        }
    }
}