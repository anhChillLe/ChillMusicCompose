package com.chillle.chillmusic.ui.screens

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chillle.chillmusic.data.repository.MusicStore
import com.chillle.chillmusic.viewmodel.PlayerViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainTab(modifier: Modifier = Modifier) {
    val state = object: PagerState(0) {
        override val pageCount = 3
    }
    val allMusic by MusicStore.songs.observeAsState()
    val shortMusic by MusicStore.shortMusic.observeAsState()
    val longMusic by MusicStore.longMusic.observeAsState()

    Scaffold(
        topBar = {
            MainTabRow(
                data = listOf(
                    "All Music",
                    "Music",
                    "Available List",
                ),
                pagerState = state,
            )
        },
        modifier = modifier
    ) {
        HorizontalPager(state = state, modifier = Modifier.padding(it),) { position ->
            when (position) {
                0 -> MusicPage(allMusic?: setOf())
                1 -> MusicPage(shortMusic ?: setOf())
                2 -> MusicPage(longMusic ?: setOf())
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainTabRow(data: List<String>, pagerState: PagerState, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val style = viewModel<PlayerViewModel>().style

    ScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        edgePadding = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        containerColor = style.backgroundColor,
        indicator = { tabPositions ->
            CustomIndicator(
                tabPositions = tabPositions,
                pagerState = pagerState
            )
        },
        divider = {},
    ) {
        data.forEachIndexed() { index, item ->
            val selected = pagerState.currentPage == index
            Tab(
                selected = selected,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                text = {
                    Text(
                        text = item,
                        color = if (selected)
                            style.contentColor
                        else
                            style.textColor
                    )
                })
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CustomIndicator(tabPositions: List<TabPosition>, pagerState: PagerState) {
    val viewModel = viewModel<PlayerViewModel>()

    val transition =
        updateTransition(pagerState.currentPage, label = "")//Do transition of current page
    val indicatorStart by transition.animateDp(//Indicator start transition animation
        transitionSpec = {
            if (initialState < targetState) {
                spring(dampingRatio = 1f, stiffness = 50f)//Using spring
            } else {
                spring(dampingRatio = 1f, stiffness = 100f)//Change stiffness according to your need
            }
        }, label = ""
    ) {
        tabPositions[it].left
    }

    val indicatorEnd by transition.animateDp(//Indicator end transition animation
        transitionSpec = {
            if (initialState < targetState) {
                spring(dampingRatio = 1f, stiffness = 50f)//Or you can change your anim here
            } else {
                spring(dampingRatio = 1f, stiffness = 50f)
            }
        }, label = ""
    ) {
        tabPositions[it].right
    }

    Box(
        Modifier
            .offset(x = indicatorStart)
            .wrapContentSize(align = Alignment.BottomStart)
            .width(indicatorEnd - indicatorStart)
            .height(2.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(viewModel.style.contentColor)
    )
}