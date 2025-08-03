package com.cypress.cymediaplayer.composables.remoteControlComposables

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cypress.cymediaplayer.R
import com.cypress.cymediaplayer.viewModels.RemoteControlViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteControlComposable(onNavigation : () -> Unit , onBackPressed: () -> Unit) {

    val remoteControlViewModel : RemoteControlViewModel = koinViewModel()

    BackHandler {
        onBackPressed()
    }

    var status by remember { mutableStateOf("TV Off") }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEFEFEF))
                .focusGroup()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth().
                    weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // Status Display
//                Text(text = status, fontSize = 22.sp, fontWeight = FontWeight.Medium)

                // Power Button
                Button(
                    onClick = {if (status == "TV On"){
                                    status = "TV Off"
                                    remoteControlViewModel.send(status)
                                } else {
                                    status = "TV On"
                                    remoteControlViewModel.send(status)
                                }
                              },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                ) {
                    Text("Power", color = Color.White, fontWeight = FontWeight.Bold)
                }

                // Volume & Channel Controls
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Volume")
                        IconButton(onClick = {
                            status = "Volume Up"
                            remoteControlViewModel.send(status)
                        }) {
                            Text("＋", fontSize = 24.sp)
                        }
                        IconButton(onClick = {
                            status = "Volume Down"
                            remoteControlViewModel.send(status)
                        }) {
                            Text("－", fontSize = 24.sp)
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Channel")
                        IconButton(onClick = {
                            status = "Channel Up"
                            remoteControlViewModel.send(status)
                        }) {
                            Text("▲", fontSize = 24.sp)
                        }
                        IconButton(onClick = {
                            status = "Channel Down"
                            remoteControlViewModel.send(status)
                        }) {
                            Text("▼", fontSize = 24.sp)
                        }
                    }
                }

                // Navigation Pad
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .background(Color.Gray.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = {
                                status = "Up"
                                remoteControlViewModel.send(status)
                            }) { Text("↑") }
                            Row {
                                IconButton(onClick = {
                                    status = "Left"
                                    remoteControlViewModel.send(status)
                                }) { Text("←") }
                                Button(
                                    onClick = {
                                        status = "OK"
                                        remoteControlViewModel.send(status) },
                                    shape = CircleShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                                ) {
                                    Text("OK", color = Color.White)
                                }
                                IconButton(onClick = {
                                    status = "Right"
                                    remoteControlViewModel.send(status)
                                }) { Text("→") }
                            }
                            IconButton(onClick = {
                                status = "Down"
                                remoteControlViewModel.send(status)
                            }) { Text("↓") }
                        }
                    }
                }

                // Extra Controls (Home & Back)
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                ) {
                    Button(onClick = {
                        status = "Home"
                        remoteControlViewModel.send(status)},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.DarkGray,
                            contentColor = Color.White
                        )) {
                        Text("Home")
                    }
                    Button(onClick = {
                        status = "Back"
                        remoteControlViewModel.send(status)},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.DarkGray,
                            contentColor = Color.White
                        )) {
                        Text("Back")
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    onNavigation()
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(painter = painterResource(id = R.drawable.outline_qr_code_2_24),
                    contentDescription = "remote control" ,
                    tint = Color.White)
            }
        }
    }


}