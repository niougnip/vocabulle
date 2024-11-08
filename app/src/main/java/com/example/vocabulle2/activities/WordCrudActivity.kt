package com.example.vocabulle2.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.vocabulle2.AppDatabase
import com.example.vocabulle2.LanguageUtils
import com.example.vocabulle2.TranslationEntity
import com.example.vocabulle2.ui.theme.ButtonSuccess
import com.example.vocabulle2.ui.theme.ButtonWarning
import com.example.vocabulle2.ui.theme.LightBackground
import com.example.vocabulle2.ui.theme.RoundedCaret
import com.example.vocabulle2.ui.theme.Vocabulle2Theme

class WordCrudActivity : ComponentActivity() {
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "dutch_word.db"
        ).allowMainThreadQueries().build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Vocabulle2Theme {
                val id = intent.getIntExtra("ID", -1)
                if (id == -1) goToMain()

                val word = db.dao.findById(id)
                if (word == null) goToMain()
                else FormScreen(word)
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @SuppressLint("NotConstructor", "UnsafeIntentLaunch")
    @Composable
    fun FormScreen(word: TranslationEntity) {
        var french by remember { mutableStateOf(word.french) }
        var other by remember { mutableStateOf(word.other) }

        Column (
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background, RectangleShape)
        ) {
            Row(modifier = Modifier.fillMaxWidth().weight(1F).padding(20.dp)) {
                Box(modifier = Modifier
                    .padding(0.dp, 15.dp)
                    .clickable {
                        goToWordTest(word.isoCode)
                    }
                ) { RoundedCaret(180F, 0.5F) }
            }
            FlowColumn(
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(6F)
            ) {
                LazyColumn (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        TextField(
                            value = french,
                            onValueChange = { french = it },
                            colors = TextFieldDefaults.colors(unfocusedContainerColor = LightBackground, focusedContainerColor = LightBackground),
                            label = { Text("Français") },
                            modifier =  Modifier.padding(0.dp, 10.dp)
                        )
                    }
                    item {
                        TextField(
                            value = other,
                            onValueChange = { other = it },
                            colors = TextFieldDefaults.colors(unfocusedContainerColor = LightBackground, focusedContainerColor = LightBackground),
                            label = { Text(LanguageUtils.getLanguage(word.isoCode)) },
                            modifier =  Modifier.padding(0.dp, 10.dp)
                        )
                    }
                    item {
                        ButtonSuccess ("Sauver"
                        ) {
                            db.dao.save(word)
                            goToWordTest(word.isoCode)
                        }
                        ButtonWarning ("Supprimer"
                        ) {
                            db.dao.delete(word)
                            goToWordTest(word.isoCode)
                        }
                    }
                }
            }
            Spacer(Modifier.weight(1F))
        }
    }

    @SuppressLint("UnsafeIntentLaunch")
    private fun goToWordTest(isoCode: String) {
        intent = Intent(this@WordCrudActivity, WordTestActivity::class.java)
        val bundle = Bundle()
        bundle.putString("ISO", isoCode)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }

    @SuppressLint("UnsafeIntentLaunch")
    private fun goToMain() {
        intent = Intent(this@WordCrudActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}