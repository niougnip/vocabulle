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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vocabulle2.R
import com.example.vocabulle2.ui.theme.RoundedCaret
import com.example.vocabulle2.ui.theme.Vocabulle2Theme

class SuccessActivity : ComponentActivity() {
    var isoCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Vocabulle2Theme {
                val french = intent.getStringExtra("FR")
                val other = intent.getStringExtra("OTHER")
                isoCode = intent.getStringExtra("ISO").toString()
                SuccessScreen(french, other)
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @SuppressLint("NotConstructor", "UnsafeIntentLaunch")
    @Composable
    fun SuccessScreen(french: String?, dutch: String?) {
        FlowColumn(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background, RectangleShape)
                .clickable(true, "suivant", null, {
                    intent = Intent(this@SuccessActivity, WordTestActivity::class.java)
                    val bundle = Bundle()
                    bundle.putString("ISO", isoCode)
                    intent.putExtras(bundle)
                    startActivity(intent)
                    finish()
                })
        ) {

            LazyColumn (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Image(
                        painter = painterResource(R.drawable.baseline_check_circle_128),
                        contentDescription = null
                    )
                }
                if (french != null) {
                    item { Spacer(Modifier.height(50.dp)) }
                    item { Text(text = french ?: "", fontSize = 32.sp, color = Color.White) }
                    item { Box(modifier = Modifier.padding(15.dp)) { RoundedCaret(90F) } }
                    item { Text(text = dutch ?: "", fontSize = 32.sp, color = Color.White) }
                }
            }
        }
    }
}