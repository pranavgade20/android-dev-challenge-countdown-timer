/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.typography
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp() {
    var time by rememberSaveable { mutableStateOf(60) }
    var remaining by rememberSaveable { mutableStateOf(60 * 1000L) }
    var prev by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    var running by rememberSaveable { mutableStateOf(false) }
    val animatedProgress = animateFloatAsState(
        (remaining / 1000f) / time,
        ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value

    Surface(color = MaterialTheme.colors.background) {
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(contentAlignment = Alignment.TopCenter) {
                CircularProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    modifier = Modifier.padding(64.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    OutlinedTextField(
                        value = (remaining / 1000).toString(),
                        onValueChange = {
                            time = Integer.parseInt(it.replace(Regex("[^0123456789]"), ""))
                            remaining = time * 1000L
                        },
                        modifier = Modifier
                            .width(128.dp),
                        textStyle = typography.h2,
                        enabled = !running,
                        singleLine = true,
                        maxLines = 1,
                    )
                    Text(
                        text = String.format("%03d", remaining % 1000),
                        style = typography.h5,
                        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                    )
                }
            }

            Button(
                onClick = {
                    remaining = time * 1000L
                    running = false
                },
            ) {
                Text("Reset")
            }

            FloatingActionButton(
                modifier = Modifier
                    .padding(top = 256.dp)
                    .size(64.dp),
                onClick = {
                    running = !running
                    prev = System.currentTimeMillis()
                }
            ) {
                Icon(if (running) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, contentDescription = if (running) "Pause" else "Play")
            }
        }
    }

    LaunchedEffect("draw") {
        while (true) {
            if (running) {
                val current = System.currentTimeMillis()
                remaining -= (current - prev)
                prev = current
                if (remaining < 0) {
                    remaining = 0
                    running = false
                }
            }
            delay(1000 / 24)
        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
