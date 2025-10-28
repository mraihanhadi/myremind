package com.example.myremind.ui.view


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myremind.model.Group
import com.example.myremind.controller.GroupController

private val CircleRed = Color(0xFFFF2B2B)

data class GroupItem(
    val id: String,
    val name: String
)

@Composable
fun GroupScreen(
    groups: List<Group>, 
    onGroupClick: (groupId: Int) -> Unit,

    onClickHome: () -> Unit,
    onClickAlarm: () -> Unit,
    onClickAddCenter: () -> Unit,
    onClickGroup: () -> Unit,
    onClickProfile: () -> Unit,
    onClickAddRight: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 110.dp)
        ) {
            Text(
                text = "Group",
                color = TextWhite,
                fontSize = 36.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 20.dp)
            )

            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = groups,
                    key = { it.getGroupId() }
                ) { group ->
                    GroupCard(
                        name = group.getGroupName(),
                        onClick = {
                            onGroupClick(group.getGroupId())
                        }
                    )
                }
            }
        }

        BottomBarWithFabSelectable(
            modifier = Modifier.align(Alignment.BottomCenter),
            selectedTab = BottomTab.GROUP,
            onClickHome = onClickHome,
            onClickAlarm = onClickAlarm,
            onClickAdd = onClickAddCenter,
            onClickGroup = onClickGroup,
            onClickProfile = onClickProfile
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 98.dp)
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onClickAddRight) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}


@Composable
private fun GroupCard(
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CardDark,
        shape = RoundedCornerShape(22.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(CircleRed)
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = name,
                color = TextWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
