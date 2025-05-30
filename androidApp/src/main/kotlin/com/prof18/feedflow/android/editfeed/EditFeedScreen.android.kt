package com.prof18.feedflow.android.editfeed

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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prof18.feedflow.core.model.FeedSourceSettings
import com.prof18.feedflow.shared.domain.model.FeedEditedState
import com.prof18.feedflow.shared.presentation.EditFeedViewModel
import com.prof18.feedflow.shared.presentation.preview.categoriesExpandedState
import com.prof18.feedflow.shared.ui.feed.editfeed.EditFeedContent
import com.prof18.feedflow.shared.ui.preview.PreviewPhone
import com.prof18.feedflow.shared.ui.utils.LocalFeedFlowStrings

@Composable
internal fun EditScreen(
    viewModel: EditFeedViewModel,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
) {
    val feedUrl by viewModel.feedUrlState.collectAsStateWithLifecycle()
    val feedName by viewModel.feedNameState.collectAsStateWithLifecycle()
    val feedSourceSettings by viewModel.feedSourceSettingsState.collectAsStateWithLifecycle()
    var showLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    val categoriesState by viewModel.categoriesState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val strings = LocalFeedFlowStrings.current

    val showNotificationToggle by viewModel.showNotificationToggleState.collectAsState()

    val latestNavigateBack by rememberUpdatedState(navigateBack)

    LaunchedEffect(Unit) {
        viewModel.feedEditedState.collect { feedAddedState ->
            when (feedAddedState) {
                is FeedEditedState.Error -> {
                    showError = true
                    showLoading = false
                    errorMessage = when (feedAddedState) {
                        FeedEditedState.Error.InvalidUrl -> strings.invalidRssUrl
                        FeedEditedState.Error.InvalidTitleLink -> strings.missingTitleAndLink
                        FeedEditedState.Error.GenericError -> strings.editFeedGenericError
                    }
                }

                is FeedEditedState.FeedEdited -> {
                    showLoading = false
                    val message = strings.feedEditedMessage(feedAddedState.feedName)
                    Toast.makeText(context, message, Toast.LENGTH_SHORT)
                        .show()
                    latestNavigateBack()
                }

                FeedEditedState.Idle -> {
                    showLoading = false
                    showError = false
                    errorMessage = ""
                }

                FeedEditedState.Loading -> {
                    showLoading = true
                }
            }
        }
    }

    EditFeedContent(
        modifier = modifier,
        feedUrl = feedUrl,
        feedName = feedName,
        showError = showError,
        errorMessage = errorMessage,
        showLoading = showLoading,
        categoriesState = categoriesState,
        canEditUrl = viewModel.canEditUrl(),
        feedSourceSettings = feedSourceSettings,
        onFeedUrlUpdated = { url ->
            viewModel.updateFeedUrlTextFieldValue(url)
        },
        onFeedNameUpdated = { name ->
            viewModel.updateFeedNameTextFieldValue(name)
        },
        onLinkOpeningPreferenceSelected = { preference ->
            viewModel.updateLinkOpeningPreference(preference)
        },
        onHiddenToggled = { hidden ->
            viewModel.updateIsHiddenFromTimeline(hidden)
        },
        onPinnedToggled = { pinned ->
            viewModel.updateIsPinned(pinned)
        },
        showNotificationToggle = showNotificationToggle,
        onNotificationToggleChanged = { isNotificationEnabled ->
            viewModel.updateIsNotificationEnabled(isNotificationEnabled)
        },
        editFeed = {
            viewModel.editFeed()
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
        topAppBar = {
            TopAppBar(
                title = {
                    Text(LocalFeedFlowStrings.current.editFeed)
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
private fun EditScreenPreview() {
    FeedFlowTheme {
        EditFeedContent(
            feedUrl = "https://www.ablog.com/feed",
            feedName = "Feed Name",
            showError = false,
            showLoading = false,
            errorMessage = "",
            canEditUrl = true,
            categoriesState = categoriesExpandedState,
            feedSourceSettings = FeedSourceSettings(),
            onFeedUrlUpdated = {},
            onFeedNameUpdated = {},
            onLinkOpeningPreferenceSelected = {},
            onHiddenToggled = {},
            onPinnedToggled = {},
            showNotificationToggle = true,
            onNotificationToggleChanged = {},
            editFeed = { },
            onExpandClick = {},
            onAddCategoryClick = {},
            onDeleteCategoryClick = {},
            onEditCategoryClick = { _, _ -> },
            topAppBar = {
                TopAppBar(
                    title = {
                        Text(LocalFeedFlowStrings.current.editFeed)
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
