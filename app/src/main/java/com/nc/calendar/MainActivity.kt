package com.nc.calendar

import android.os.Bundle
import android.util.AttributeSet
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.nc.calendar.ui.AppTheme
import com.nc.calendar.ui.PickerBottomSheet
import java.time.LocalDate

const val ITEM_SIZE_COEFFICIENT = 9

class MainActivity : AppCompatActivity() {
    private val viewModel: CalendarViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Screen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun Screen(
    viewModel: CalendarViewModel
) {
    val monthList by viewModel.monthList.collectAsState(emptyList())
    val showYear by viewModel.showTopPaneYear.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val showBottomSheet by viewModel.showBottomSheet.collectAsState()
    val selectedPosition by viewModel.selectedMonthPosition.collectAsState()
    val bottomSheetSelectedDate by viewModel.bottomSheetSelectedDate.collectAsState()
    val scrollState = rememberLazyListState()

    LaunchedEffect(monthList) {
        scrollState.animateScrollToItem(
            viewModel.calculatePositionToScroll()
        )
    }

    Column(modifier = Modifier.height(50.dp)) {
        Spacer(Modifier.height(30.dp))
        TopPane(
            modifier = Modifier.padding(start = 32.dp),
            selectedDate = selectedDate,
            todayNumber = viewModel.today.dayOfMonth.toString(),
            showYear = showYear,
            onMonthClicked = { viewModel.setShowBottomSheet(true) },
            onGoToTodayClicked = { viewModel.goToToday() }
        )
        MonthList(
            modifier = Modifier.padding(top = 20.dp),
            month = monthList,
            scrollState = scrollState,
            onDayClicked = { day -> viewModel.setLastSelectedDay(day) }
        )
        CustomCalendar(selectedDate = selectedDate)
        PickerBottomSheet(
            showBottomSheet = showBottomSheet,
            selectedDate = bottomSheetSelectedDate,
            onDismissRequest = {
                viewModel.setShowBottomSheet(false)
                viewModel.resetBottomSheetSelectedDate()
            },
            selectedPosition = selectedPosition,
            onArrowBackClicked = { viewModel.selectPreviousMonth() },
            onArrowForwardClicked = { viewModel.selectNextMonth() },
            onSelectedPositionChanged = { viewModel.selectMonthWithPosition(it) },
            onSelectClicked = {
                viewModel.applySelectedDate()
                viewModel.setShowBottomSheet(false)
            }
        )
    }
}

@Composable
fun CustomCalendar(modifier: Modifier = Modifier, selectedDate: LocalDate) {
    Box(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        val background = MaterialTheme.colorScheme.background
        val onBackground = MaterialTheme.colorScheme.onBackground
        AndroidView(
            factory = { context ->
                DayTimelineView(context).apply {
                    setColors(
                        backgroundColor = background,
                        onBackgroundColor = onBackground
                    )
                }
            },
            update = { view ->
                view.setDate(selectedDate)
            }
        )
    }
}

@Composable
private fun TopPane(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    todayNumber: String,
    showYear: Boolean,
    onMonthClicked: () -> Unit,
    onGoToTodayClicked: () -> Unit,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Row(
            modifier = Modifier.clickable(onClick = onMonthClicked),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = selectedDate.month.format(),
                style = MaterialTheme.typography.displaySmall.copy(fontSize = 18.sp)
            )
            AnimatedVisibility(
                visible = showYear,
                enter = expandHorizontally() + expandVertically(),
                exit = shrinkHorizontally() + shrinkVertically()
            ) {
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = selectedDate.formatYear(),
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 18.sp)
                )
            }
            Icon(Icons.Filled.ArrowDropDown, null)
        }

        TextButton(
            onClick = onGoToTodayClicked,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Text(text = todayNumber, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun MonthList(
    modifier: Modifier = Modifier,
    month: List<List<Day>>,
    scrollState: LazyListState,
    onDayClicked: (day: LocalDate) -> Unit
) {
    val dayItemWidth = LocalConfiguration.current.screenWidthDp.dp / ITEM_SIZE_COEFFICIENT
    LazyRow(
        modifier = modifier,
        state = scrollState,
        flingBehavior = rememberSnapFlingBehavior(scrollState),
    ) {
        items(items = month) { week ->
            WeekItem(
                Modifier.fillParentMaxWidth(),
                week = week,
                itemSize = dayItemWidth,
                onDayClicked = onDayClicked
            )
        }
    }
}

@Composable
private fun WeekItem(
    modifier: Modifier = Modifier,
    week: List<Day>,
    itemSize: Dp,
    onDayClicked: (day: LocalDate) -> Unit
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceAround) {
        for (day in week) {
            DayItem(
                day = day,
                itemSize = itemSize,
                onClicked = { onDayClicked(day.date) }
            )
        }
    }
}

@Composable
private fun DayItem(
    modifier: Modifier = Modifier,
    day: Day,
    itemSize: Dp,
    onClicked: () -> Unit
) {
    Column(
        modifier = modifier.width(itemSize),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = day.dayOfWeek)
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemSize),
            onClick = onClicked,
            colors = if (day.isSelected) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            else CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                text = day.dayOfMonth,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )
            if (day.isToday) Icon(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 4.dp)
                    .size(8.dp),
                imageVector = Icons.Filled.Circle,
                contentDescription = ""
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DayItemPreview() {
    DayItem(
        day = Day(
            "пн", dayOfMonth = "25", isSelected = false, isToday = false, date = LocalDate.now()
        ),
        itemSize = 40.dp
    ) {}
}

@Preview(showBackground = true)
@Composable
private fun WeekPreview() {
    val day =
        Day("пн", dayOfMonth = "25", isSelected = false, isToday = false, date = LocalDate.now())
    val day2 =
        Day("пн", dayOfMonth = "11", isSelected = false, isToday = false, date = LocalDate.now())
    WeekItem(week = listOf(day, day2, day, day2, day, day2, day), itemSize = 40.dp) {}
}

@Composable
@Preview(name = "preview", showBackground = true)
fun TopPanePreview() {
    TopPane(
        onMonthClicked = {},
        selectedDate = LocalDate.now(),
        onGoToTodayClicked = {},
        todayNumber = LocalDate.now().dayOfMonth.toString(),
        showYear = true,
    )
}