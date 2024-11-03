@file:OptIn(ExperimentalLayoutApi::class)

package com.example.vocabulle2.activities

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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.example.vocabulle2.AppDatabase
import com.example.vocabulle2.IsoCodeList
import com.example.vocabulle2.R
import com.example.vocabulle2.TranslationEntity
import com.example.vocabulle2.ui.theme.Vocabulle2Theme
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset

class MainActivity : ComponentActivity() {
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "dutch_word.db"
        ).allowMainThreadQueries().build()
    }

    @SuppressLint("UnsafeIntentLaunch")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val isoCodes: List<String> = db.dao.getIsoCodes().filter { code -> code != "FR" };

        setContent {
            Vocabulle2Theme {
                Column (modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background, RectangleShape))
                {
                    Row (modifier = Modifier.weight(6F),
                        verticalAlignment = Alignment.CenterVertically) {
                        Flags(isoCodes)
                    }
                    Row (modifier = Modifier.weight(1F).fillMaxWidth().padding(20.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center) {
                        AddDataFromCSV()
                    }
                }
            }
        }
    }

    @Composable
    private fun Flags(isoCodes: List<String>) {
        FlowColumn(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyColumn (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                items(isoCodes) { code ->
                    Text(
                        text = IsoCodeList.toFlagEmoji(code),
                        fontSize = 80.sp,
                        modifier = Modifier.clickable {
                            intent = Intent(this@MainActivity, WordTestActivity::class.java)
                            val bundle = Bundle()
                            bundle.putString("ISO", code)
                            intent.putExtras(bundle)
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }

    @SuppressLint("UnsafeIntentLaunch")
    @Composable
    private fun AddDataFromCSV() {
        val context = LocalContext.current

        val columnMap: MutableMap<String, Int> = emptyMap<String, Int>().toMutableMap()
        var otherCode: String = ""

        val launcher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { result ->
                result?.let {
                    context.contentResolver.openInputStream(it).use { inputStream ->
                        BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF8"))).use { reader ->
                            // Get the column header
                            var line: String? = reader.readLine()
                            if (line != null) {
                                if (line.contains("FR", true)) {
                                    val isoCodes = line.split(";")
                                    isoCodes.forEachIndexed { index, code ->
                                        if (code != "FR") otherCode = code
                                        columnMap[code] = index
                                    }
                                } else {
                                    // ERROR
                                    Toast.makeText(
                                        context,
                                        "La première ligne doit contenir les codes iso",
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
                                    columnMap["FR"]?.let { iso -> resultLine[iso] }.toString()
                                val other: String =
                                    columnMap[otherCode]?.let { iso -> resultLine[iso] }.toString()
                                val exists = db.dao.findItemFromFrench(french, otherCode)
                                if (exists == null) db.dao.insert(TranslationEntity(null, french, other, otherCode))
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
}