package com.shub39.rush.ui.page

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.rush.ui.component.SearchSheet
import com.shub39.rush.viewmodel.RushViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun RushApp(
    rushViewModel: RushViewModel = koinViewModel()
) {
    val searchSheetState by rushViewModel.searchSheet.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()
    val pagerState = rememberPagerState(1) { 2 }

    if (searchSheetState) {
        SearchSheet(
            rushViewModel = rushViewModel,
            coroutineScope = coroutineScope,
            pagerState = pagerState
        )
    }

    BackHandler(pagerState.currentPage == 0) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(1)
        }
    }

    HorizontalPager(pagerState) {
        when (it) {
            1 -> Scaffold(
                topBar = { TopBar(navController = navController) },
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = "saved",
                    modifier = Modifier.padding(innerPadding),
                    enterTransition = { fadeIn(animationSpec = tween(200)) },
                    exitTransition = { fadeOut(animationSpec = tween(200)) },
                    popEnterTransition = { fadeIn(animationSpec = tween(200)) },
                    popExitTransition = { fadeOut(animationSpec = tween(200)) }
                ) {
                    composable("saved") {
                        SavedPage(
                            rushViewModel = rushViewModel,
                            pagerState = pagerState
                        )
                    }

                    composable("settings") {
                        SettingPage(rushViewModel = rushViewModel)
                    }

                    composable("share") {
                        SharePage(
                            onDismiss = {
                                navController.navigateUp()
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(0)
                                }
                            },
                            rushViewModel = rushViewModel
                        )
                    }
                }
            }

            0 -> LyricsPage(
                rushViewModel = rushViewModel,
                navController = navController,
                onShare = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                }
            )
        }
    }
}