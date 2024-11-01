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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.rotationMatrix
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
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
        FlowColumn(
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
                item { Spacer(Modifier.height(50.dp)) }
                item { Text( text = french ?: "", fontSize = 32.sp, color = Color.White ) }
                item {
                    Box(
                        modifier = Modifier.rotate(90F).padding(10.dp)
                            .drawWithCache {
                                val roundedPolygon = RoundedPolygon(
                                    numVertices = 3,
                                    radius = size.minDimension / 2,
                                    centerX = size.width / 2,
                                    centerY = size.height / 2,
                                    rounding = CornerRounding(
                                        size.minDimension / 10f,
                                        smoothing = 0.1f
                                    )
                                )
                                val roundedPolygonPath = roundedPolygon.toPath().asComposePath()
                                onDrawBehind {
                                    drawPath(roundedPolygonPath, color = Color.White)
                                }
                            }
                            .size(30.dp)
                    )
                }
                item { Text( text = dutch ?: "", fontSize = 32.sp, color = Color.White ) }
            }
//            Row (horizontalArrangement = Arrangement.Center) { Text( text = dutch ?: "" ) }
        }
    }
}