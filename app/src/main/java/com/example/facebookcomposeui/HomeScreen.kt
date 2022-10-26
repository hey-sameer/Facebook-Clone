package com.example.facebookcomposeui

import android.text.format.DateUtils
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.facebookcomposeui.ui.theme.BrandBlue
import com.example.facebookcomposeui.ui.theme.ButtonGray
import com.example.facebookcomposeui.util.CONSTANTS
import com.example.facebookcomposeui.util.Post
import java.util.*

@Composable
fun HomeScreen(navToSignInScreen: () -> Unit) {
    val homeScreenVM: HomeScreenViewModel = viewModel()
    val hsState by homeScreenVM.hsState.collectAsState()

    when (hsState) {
        is HomeScreenState.Loaded -> HomeScreenContents(
            (hsState as HomeScreenState.Loaded).avatarUrl,
            (hsState as HomeScreenState.Loaded).posts,
            onStatusSendClick = { status -> homeScreenVM.statusUpdate(status) }
        )
        HomeScreenState.Loading -> LoadingScreen()
        HomeScreenState.SignInRequired -> LaunchedEffect(Unit) {
            navToSignInScreen()
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface), contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeScreenContents(
    avatarUrl: String,
    posts: List<Post>,
    onStatusSendClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxSize()
    ) {
        LazyColumn(contentPadding = PaddingValues(bottom = 20.dp)) {
            item { TopAppBar() }
            stickyHeader { TabBar() }
            item { StatusUpdateBar(avatarUrl, onSend = onStatusSendClick) }
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item { CreateAStoryCard(avatarUrl) }

            item { Spacer(modifier = Modifier.height(4.dp)) }
            items(posts) { post -> PostBlock(post) }
        }
    }
}

@Composable
fun CreateAStoryCard(
    avatarUrl: String,
) {
    Card(Modifier.size(140.dp, 220.dp)) {
        Box {
            Box(Modifier.fillMaxSize()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(avatarUrl)
                        .crossfade(true).build(),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(56.dp)
                    .background(MaterialTheme.colors.surface)
                    .align(Alignment.BottomCenter)
            )
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(80.dp)
                    .background(Transparent)
                    .align(Alignment.BottomCenter)
            ) {
                IconButton(onClick = { /*TODO*/ }, modifier = Modifier.align(Alignment.TopCenter)) {
                    Icon(
                        imageVector = Icons.Rounded.Add, contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BrandBlue)
                            .border(2.dp, MaterialTheme.colors.surface, CircleShape),
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
                Text(
                    text = "Create a Story",
                    style = MaterialTheme.typography.h2.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
fun PostBlock(post: Post) {
    Card(modifier = Modifier.padding(vertical = 4.dp, horizontal = 0.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.authorAvatarUrl)
                        .crossfade(true)
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .build(),
                    contentDescription = stringResource(id = R.string.profile),
                    modifier = Modifier
                        .padding(0.dp)
                        .size(CONSTANTS.PROFILE_PHOTO_SIZE)
                        .weight(.8f)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Column(
                    Modifier
                        .weight(weight = 5f)
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = post.author,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = CONSTANTS.POST_NORMAL_FONT_SIZE
                        )
                    )
                    Text(
                        text = dateLabel(post.timeStamp),
                        modifier = Modifier.alpha(.44f),
                        style = TextStyle(fontSize = CONSTANTS.POST_SMALL_FONT_SIZE)
                    )
                }
                IconButton(onClick = { /*TODO*/ }, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Outlined.MoreHoriz,
                        contentDescription = "more options"
                    )
                }
            }
            Text(
                text = post.text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = CONSTANTS.POST_NORMAL_FONT_SIZE
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

fun dateLabel(timeStamp: Date): String {
    val now = Date()
    return if (now.time - timeStamp.time < 2 * DateUtils.MINUTE_IN_MILLIS)
        "Just now"
    else {
        DateUtils.getRelativeTimeSpanString(timeStamp.time, now.time, DateUtils.MINUTE_IN_MILLIS)
            .toString()
    }
}

@Composable
fun StatusUpdateBar(avatarUrl: String, onSend: (String) -> Unit) {
    Surface {
        Column()
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(avatarUrl)
                        .crossfade(true)
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .build(),
                    contentDescription = stringResource(id = R.string.profile),
                    modifier = Modifier
                        .size(CONSTANTS.PROFILE_PHOTO_SIZE)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                var text by remember {
                    mutableStateOf("")
                }
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = text,
                    onValueChange = {
                        text = it
                    },
                    placeholder = { Text(text = stringResource(id = R.string.whats_on_your_mind)) },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = BrandBlue
                    ),
                    maxLines = 1,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            onSend(text)
                            text = ""
                        }
                    )
                )
            }
            Divider(thickness = Dp.Hairline)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatusAction(
                    Icons.Rounded.VideoCall,
                    stringResource(id = R.string.live),
                    Modifier.weight(1f)
                )
                VerticalDivider(thickness = Dp.Hairline)
                StatusAction(
                    Icons.Rounded.PhotoAlbum,
                    stringResource(id = R.string.photo),
                    Modifier.weight(1f)
                )
                VerticalDivider(thickness = Dp.Hairline)
                StatusAction(
                    Icons.Rounded.Chat,
                    stringResource(id = R.string.chat),
                    Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatusAction(
    image: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = { },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colors.onSurface,
        ),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = image,
                contentDescription = contentDescription
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = contentDescription)
        }
    }
}

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface.copy(alpha = .12f),
    thickness: Dp = 1.dp,
    topIndent: Dp = 2.dp
) {
    val indentMod = if (topIndent.value != 0f) {
        Modifier.padding(top = topIndent)
    } else {
        Modifier
    }
    val targetThickness = if (thickness == Dp.Hairline) {
        (1f / LocalDensity.current.density).dp
    } else {
        thickness
    }
    Box(
        modifier
            .then(indentMod)
//            .fillMaxHeight()
            .height(48.dp)
            .width(targetThickness)
            .background(color = color)
    )
}

data class TabItem(
    val image: ImageVector,
    val contentDescription: String,
)

@Composable
fun TabBar() {
    var tabIndex by remember {
        mutableStateOf(0)
    }
    val tabs = listOf(
        TabItem(Icons.Rounded.Home, stringResource(id = R.string.home)),
        TabItem(Icons.Rounded.Tv, stringResource(id = R.string.reels)),
        TabItem(Icons.Rounded.Store, stringResource(id = R.string.marketplace)),
        TabItem(Icons.Rounded.Newspaper, stringResource(id = R.string.news)),
        TabItem(Icons.Rounded.Notifications, stringResource(id = R.string.notification)),
        TabItem(Icons.Rounded.Menu, stringResource(id = R.string.menu)),
    )
    Surface {
        TabRow(
            selectedTabIndex = tabIndex,
            backgroundColor = Color.Transparent,
            contentColor = BrandBlue,
            modifier = Modifier.padding(top = 8.dp),
        ) {
            tabs.forEachIndexed { index, tabItem ->
                Tab(
                    selected = tabIndex == index,
                    unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = .5f),
                    onClick = { tabIndex = index },
                    modifier = Modifier.heightIn(48.dp)

                ) {
                    Icon(
                        imageVector = tabItem.image,
                        contentDescription = tabItem.contentDescription
                    )
                }
            }
        }
    }

}

@Composable
private fun TopAppBar() {

    Surface {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "facebook compose",
                style = MaterialTheme.typography.h6.copy(color = BrandBlue),
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = { },
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .background(ButtonGray),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = stringResource(R.string.search)
                )

            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { },
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .background(ButtonGray),
            ) {
                Icon(
                    imageVector = Icons.Rounded.ChatBubble,
                    contentDescription = stringResource(R.string.chat)
                )

            }
        }

    }
}