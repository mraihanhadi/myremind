package com.example.myremind.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val CircleRed = Color(0xFFFF2B2B)

data class GroupMember(
    val name: String,
    val role: String? = null       // "Admin" atau null
)

data class GroupDetail(
    val id: String,
    val name: String,
    val description: String,
    val members: List<GroupMember>
)

@Composable
fun GroupInfoScreen(
    group: GroupDetail,
    onBack: () -> Unit,
    onMemberClick: (GroupMember) -> Unit,
    onAddMember: () -> Unit,
    onLeaveGroup: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }
                Text(
                    text = "Group",
                    color = TextWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(8.dp))

            // Avatar bulat + nama di tengah
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(CircleRed)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = group.name,
                    color = TextWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(24.dp))

            // Description
            Text(
                text = "Description",
                color = TextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = group.description,
                color = TextWhite,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(Modifier.height(24.dp))

            // Group Member
            Text(
                text = "Group Member",
                color = TextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(12.dp))

            group.members.forEach { member ->
                MemberPill(
                    name = member.name,
                    role = member.role,
                    onClick = { onMemberClick(member) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
            }

            // Add Member (pill putih)
            PillButtonWhite(
                text = "Add Member",
                modifier = Modifier.padding(top = 4.dp),
                onClick = onAddMember
            )

            Spacer(Modifier.weight(1f))

            // Leave Group (teks merah di atas pill putih)
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onLeaveGroup() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Leave Group",
                        color = Color(0xFFFF2B2B),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun MemberPill(
    name: String,
    role: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                color = Color(0xFF111111),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            if (!role.isNullOrBlank()) {
                Text(
                    text = role,
                    color = Color(0xFF111111),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun PillButtonWhite(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
            .fillMaxWidth(0.55f) // sedikit lebih pendek, sesuai mockup
            .height(44.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color(0xFF111111),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}
