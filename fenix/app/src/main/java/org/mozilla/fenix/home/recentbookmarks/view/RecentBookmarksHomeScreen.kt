/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home.recentbookmarks.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import mozilla.components.browser.icons.compose.Loader
import mozilla.components.browser.icons.compose.Placeholder
import mozilla.components.browser.icons.compose.WithIcon
import org.mozilla.fenix.R
import org.mozilla.fenix.components.components
import org.mozilla.fenix.compose.Image
import org.mozilla.fenix.compose.annotation.LightDarkPreview
import org.mozilla.fenix.compose.inComposePreview
import org.mozilla.fenix.home.recentbookmarks.RecentBookmark
import org.mozilla.fenix.theme.FirefoxTheme

private val cardShape = RoundedCornerShape(8.dp)

private val imageWidth = 46.dp

private val imageModifier = Modifier
    .size(width = imageWidth, height = imageWidth)
    .clip(cardShape)

/**
 * A list of recent bookmarks.
 *
 * @param bookmarks List of [RecentBookmark]s to display.
 * @param menuItems List of [RecentBookmarksMenuItem] shown when long clicking a [RecentBookmarkItem]
 * @param backgroundColor The background [Color] of each bookmark.
 * @param onRecentBookmarkClick Invoked when the user clicks on a recent bookmark.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RecentBookmarksHomeScreen(
    bookmarks: List<RecentBookmark>,
    onRecentBookmarkClick: (RecentBookmark) -> Unit = {},
) {
    LazyRow(
        modifier = Modifier.semantics {
            testTagsAsResourceId = true
            testTag = "recent.bookmarks"
        },
    ) {
        items(bookmarks) { bookmark ->
            RecentBookmarkItem(
                bookmark = bookmark,
                onRecentBookmarkClick = onRecentBookmarkClick,
            )
        }
    }
}

/**
 * A recent bookmark item.
 *
 * @param bookmark The [RecentBookmark] to display.
 * @param menuItems The list of [RecentBookmarksMenuItem] shown when long clicking on the recent bookmark item.
 * @param backgroundColor The background [Color] of the recent bookmark item.
 * @param onRecentBookmarkClick Invoked when the user clicks on the recent bookmark item.
 */
@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class, ExperimentalUnitApi::class,
)
@Composable
private fun RecentBookmarkItem(
    bookmark: RecentBookmark,
    onRecentBookmarkClick: (RecentBookmark) -> Unit = {},
) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .combinedClickable(
                enabled = true,
                onClick = { onRecentBookmarkClick(bookmark) },
            ),
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RecentBookmarkImage(bookmark)

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = bookmark.title ?: bookmark.url ?: "",
                modifier = Modifier.semantics {
                    testTagsAsResourceId = true
                    testTag = "recent.bookmark.title"
                },
                color = colorResource(id = R.color.freespoke_font_color),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = TextStyle(
                    fontSize = TextUnit(10f, TextUnitType.Sp)
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RecentBookmarkImage(bookmark: RecentBookmark) {
    when {
        !bookmark.previewImageUrl.isNullOrEmpty() -> {
            val painter = rememberImagePainter(bookmark.previewImageUrl) {
                crossfade(true)
            }
            Image(
                painter = painter,
                contentDescription = null,
                modifier = imageModifier,
                contentScale = ContentScale.Crop,
            )
        }
        !bookmark.url.isNullOrEmpty() && !inComposePreview -> {
            components.core.icons.Loader(bookmark.url) {
                WithIcon { icon ->
                    Image(
                        painter = icon.painter,
                        contentDescription = null,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(cardShape),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center,
                    )
                }
            }
        }
        inComposePreview -> {
            PlaceholderBookmarkImage()
        }
    }
}

@Composable
private fun PlaceholderBookmarkImage() {
    Box(
        modifier = imageModifier.background(
            color = colorResource(id = R.color.bookmark_default_color),
        ),
    )
}

@Composable
@LightDarkPreview
private fun RecentBookmarksPreview() {
    FirefoxTheme {
        RecentBookmarksHomeScreen(
            bookmarks = listOf(
                RecentBookmark(
                    title = "Other Bookmark Title",
                    url = "https://www.example.com",
                    previewImageUrl = null,
                ),
                RecentBookmark(
                    title = "Other Bookmark Title",
                    url = "https://www.example.com",
                    previewImageUrl = null,
                ),
                RecentBookmark(
                    title = "Other Bookmark Title",
                    url = "https://www.example.com",
                    previewImageUrl = null,
                ),
                RecentBookmark(
                    title = "Other Bookmark Title",
                    url = "https://www.example.com",
                    previewImageUrl = null,
                ),
            )
        )
    }
}
