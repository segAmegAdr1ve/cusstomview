package com.nc.calendar.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nc.calendar.R
import com.nc.calendar.format
import com.nc.calendar.formatYear
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickerBottomSheet(
    modifier: Modifier = Modifier,
    showBottomSheet: Boolean,
    selectedPosition: Int,
    selectedDate: LocalDate,
    onDismissRequest: () -> Unit,
    onArrowBackClicked: () -> Unit,
    onArrowForwardClicked: () -> Unit,
    onSelectedPositionChanged: (position: Int) -> Unit,
    onSelectClicked: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = modifier,
            onDismissRequest = onDismissRequest,
            sheetState = sheetState
        ) {
            BottomSheetTopPane(
                modifier = Modifier
                    .padding(start = 40.dp)
                    .fillMaxWidth(),
                selectedDate = selectedDate,
                onArrowBackClicked = onArrowBackClicked,
                onArrowForwardClicked = onArrowForwardClicked,
            )

            MonthFlowRow(
                modifier = modifier.fillMaxWidth(),
                selectedPosition = selectedPosition,
                onSelectedPositionChanged = onSelectedPositionChanged
            )

            Button(
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
                onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            onSelectClicked()
                        }
                    }
                }) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = LocalContext.current.getString(R.string.select_button_text),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun BottomSheetTopPane(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    onArrowBackClicked: () -> Unit,
    onArrowForwardClicked: () -> Unit
) {
    Row(modifier = modifier) {
        Text(
            text = selectedDate.month.format(),
            style = MaterialTheme.typography.displaySmall.copy(fontSize = 18.sp)
        )
        Text(
            text = selectedDate.formatYear(),
            style = MaterialTheme.typography.displaySmall.copy(fontSize = 18.sp),
            modifier = Modifier.padding(start = 8.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onArrowBackClicked) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = null,
            )
        }
        IconButton(onClick = onArrowForwardClicked) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
            )
        }
        Spacer(modifier = Modifier.width(40.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MonthFlowRow(
    modifier: Modifier = Modifier,
    selectedPosition: Int,
    onSelectedPositionChanged: (position: Int) -> Unit,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            8.dp,
            alignment = Alignment.CenterHorizontally
        ),
        maxItemsInEachRow = 4,
        maxLines = 3
    ) {
        Month.entries.forEachIndexed { position: Int, month: Month ->
            FilterChip(
                modifier = Modifier.padding(0.dp),
                selected = selectedPosition == position,
                onClick = { onSelectedPositionChanged(position) },
                label = {
                    Text(
                        modifier = Modifier.padding(0.dp),
                        text = month.format(),
                        style = MaterialTheme.typography.bodySmall,
                    )
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PickerBottomSheetPreview() {
    PickerBottomSheet(
        modifier = Modifier.fillMaxWidth(),
        showBottomSheet = true,
        selectedDate = LocalDate.now(),
        onDismissRequest = {},
        selectedPosition = 1,
        onArrowBackClicked = {},
        onArrowForwardClicked = {},
        onSelectedPositionChanged = {},
        onSelectClicked = {},
    )
}