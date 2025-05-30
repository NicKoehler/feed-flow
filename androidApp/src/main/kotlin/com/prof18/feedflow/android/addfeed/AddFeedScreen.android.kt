package com.prof18.feedflow.android.addfeed

import FeedFlowTheme
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prof18.feedflow.shared.domain.model.FeedAddedState
import com.prof18.feedflow.shared.presentation.AddFeedViewModel
import com.prof18.feedflow.shared.presentation.preview.categoriesExpandedState
import com.prof18.feedflow.shared.ui.feed.addfeed.AddFeedContent
import com.prof18.feedflow.shared.ui.preview.PreviewPhone
import com.prof18.feedflow.shared.ui.utils.LocalFeedFlowStrings
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddFeedScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<AddFeedViewModel>()
    var feedUrl by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var showLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val strings = LocalFeedFlowStrings.current

    val showNotificationToggle by viewModel.showNotificationToggleState.collectAsState()
    val isNotificationEnabled by viewModel.isNotificationEnabledState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.feedAddedState.collect { feedAddedState ->
            when (feedAddedState) {
                is FeedAddedState.Error -> {
                    showError = true
                    showLoading = false
                    errorMessage = when (feedAddedState) {
                        FeedAddedState.Error.InvalidUrl -> strings.invalidRssUrl
                        FeedAddedState.Error.InvalidTitleLink -> strings.missingTitleAndLink
                        FeedAddedState.Error.GenericError -> strings.addFeedGenericError
                    }
                }

                is FeedAddedState.FeedAdded -> {
                    feedUrl = ""
                    showLoading = false
                    val feedName = feedAddedState.feedName
                    val message = if (feedName != null) {
                        strings.feedAddedMessage(feedName)
                    } else {
                        strings.feedAddedMessageWithoutName
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT)
                        .show()
                }

                FeedAddedState.FeedNotAdded -> {
                    showLoading = false
                    showError = false
                    errorMessage = ""
                }

                FeedAddedState.Loading -> {
                    showLoading = true
                }
            }
        }
    }

    val categoriesState by viewModel.categoriesState.collectAsStateWithLifecycle()

    AddFeedContent(
        modifier = modifier,
        feedUrl = feedUrl,
        showError = showError,
        errorMessage = errorMessage,
        showLoading = showLoading,
        categoriesState = categoriesState,
        onFeedUrlUpdated = { url ->
            feedUrl = url
            viewModel.updateFeedUrlTextFieldValue(url)
        },
        addFeed = {
            viewModel.addFeed()
        },
        onExpandClick = {
            viewModel.onExpandCategoryClick()
        },
        onAddCategoryClick = { categoryName ->
            viewModel.addNewCategory(categoryName)
        },
        onDeleteCategoryClick = { categoryId ->
            viewModel.deleteCategory(categoryId.value)
        },
        onEditCategoryClick = { categoryId, newName ->
            viewModel.editCategory(categoryId, newName)
        },
        showNotificationToggle = showNotificationToggle,
        isNotificationEnabled = isNotificationEnabled,
        onNotificationToggleChanged = { enabled ->
            viewModel.updateNotificationStatus(enabled)
        },
        topAppBar = {
            TopAppBar(
                title = {
                    Text(LocalFeedFlowStrings.current.addFeed)
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigateBack()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
            )
        },
    )
}

@PreviewPhone
@Composable
private fun AddScreenContentPreview() {
    FeedFlowTheme {
        AddFeedContent(
            feedUrl = "https://www.ablog.com/feed",
            showError = false,
            showLoading = false,
            errorMessage = "",
            categoriesState = categoriesExpandedState,
            onFeedUrlUpdated = {},
            addFeed = { },
            onExpandClick = {},
            onAddCategoryClick = {},
            onDeleteCategoryClick = {},
            onEditCategoryClick = { _, _ -> },
            showNotificationToggle = true,
            isNotificationEnabled = false,
            onNotificationToggleChanged = {},
            topAppBar = {
                TopAppBar(
                    title = {
                        Text(LocalFeedFlowStrings.current.addFeed)
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {},
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                            )
                        }
                    },
                )
            },
        )
    }
}
