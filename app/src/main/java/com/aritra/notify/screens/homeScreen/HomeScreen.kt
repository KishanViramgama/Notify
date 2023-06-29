@file:OptIn(ExperimentalMaterial3Api::class)

package com.aritra.notify.screens.homeScreen


import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.DismissDirection.*
import androidx.compose.material3.DismissValue.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aritra.notify.R
import com.aritra.notify.components.TopBar
import com.aritra.notify.data.models.Note


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    onFabClicked: () -> Unit,
    navigateToUpdateNoteScreen: (noteId: Int) -> Unit
) {

    val viewModel: HomeScreenViewModel = viewModel()
    val notesModel = viewModel.notesModel
    LaunchedEffect(Unit) {
        viewModel.getAllNotes()
    }

    Scaffold(
        topBar = { TopBar() },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(0.dp,0.dp,20.dp,15.dp),
                onClick = { onFabClicked() },
                containerColor = Color.Black
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add FAB",
                    tint = Color.White,
                )
            }
        },
    ) {
        Surface(
            color = colorResource(id = R.color.Background),
            modifier = Modifier.padding(it)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(0.dp,20.dp,0.dp,0.dp)
            ) {
                if (notesModel.isNotEmpty()) {
                    items(notesModel) { notesModel ->
                        SwapDelete(notesModel, viewModel, navigateToUpdateNoteScreen)
                    }
                } else {
                    item {
                        NoList()
                    }
                }
            }
        }
    }
}

@Composable
fun SwapDelete(
    notesModel: Note,
    viewModel: HomeScreenViewModel,
    navigateToUpdateNoteScreen: (noteId: Int) -> Unit
) {

    val dismissState = rememberDismissState(
        confirmValueChange = {
            if (it == DismissedToEnd)
                viewModel.deleteNote(notesModel)
            it != DismissedToEnd
        }
    )
    SwipeToDismiss(
        directions = setOf(StartToEnd),
        state = dismissState,
        background = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    Default -> Color.LightGray
                    DismissedToEnd -> Color.Red
                    DismissedToStart -> return@SwipeToDismiss
                }
            )
            val alignment = when (direction) {
                StartToEnd -> Alignment.CenterStart
                EndToStart -> return@SwipeToDismiss
            }
            val scale by animateFloatAsState(
                if (dismissState.targetValue == Default) 0.75f else 1f
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "delete",
                    modifier = Modifier.scale(scale)
                )
            }
        }, dismissContent = {
            NotesCard(notesModel, navigateToUpdateNoteScreen)
        })
}

@Composable
fun NotesCard(
    noteModel: Note,
    navigateToUpdateNoteScreen: (noteId: Int) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable { navigateToUpdateNoteScreen(noteModel.id) },
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(
                text = noteModel.title,
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.poppins_medium))
            )
            Text(
                text = noteModel.note,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.poppins_light))
            )
        }
    }
}

@Composable
fun NoList() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty_list))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp, 150.dp, 0.dp, 0.dp)
    ) {
        LottieAnimation(
            composition = composition,
            modifier = Modifier.size(300.dp),
            isPlaying = true
        )
        Text(
            text = "No notes here yet",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(R.font.poppins_semibold)),
            fontSize = 20.sp
        )
    }
}

