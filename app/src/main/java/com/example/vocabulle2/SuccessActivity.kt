package com.example.vocabulle2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import com.example.vocabulle2.ui.theme.Vocabulle2Theme

class SuccessActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Vocabulle2Theme {
                val french = intent.getStringExtra(MainActivity.isoFR)
                val dutch = intent.getStringExtra(MainActivity.isoNL)
                SuccessScreen(french, dutch)
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @SuppressLint("NotConstructor", "UnsafeIntentLaunch")
    @Composable
    fun SuccessScreen(french: String?, dutch: String?) {
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background, RectangleShape)
                .clickable(true, "suivant", null, {
                    intent = Intent(this@SuccessActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                })
        ) {
            Image(
                painter = painterResource(R.drawable.baseline_check_circle_128),
                contentDescription = null
            )
            Column { Text( text = french ?: "" ) }
            Column { Text( text = dutch ?: "" ) }
        }
    }
}