package com.example.myremind.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.interaction.MutableInteractionSource



private val AvatarBg = Color(0xFFE9E1FF)

@Composable
fun ProfileScreen(
    username: String,
    email: String,
    onClickHome: () -> Unit,
    onClickAlarm: () -> Unit,
    onClickAdd: () -> Unit,
    onClickGroup: () -> Unit,
    onClickProfile: () -> Unit,
    onSignOut: () -> Unit
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
                text = "Profile",
                color = TextWhite,
                fontSize = 40.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 44.sp,
                modifier = Modifier
                    .padding(start = 24.dp, top = 32.dp, bottom = 24.dp)
            )

            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(AvatarBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = ScreenBlack,
                        modifier = Modifier.size(80.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = username,
                        color = TextWhite,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 36.sp
                    )

                    Spacer(Modifier.width(12.dp))
                }
            }

            Spacer(Modifier.height(32.dp))

            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Transparent)
                        .borderWithAccent()
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Icon",
                        tint = AccentYellow,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(Modifier.width(16.dp))

                
                Surface(
                    color = CardDark,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = email,
                            color = TextWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 24.sp,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(Modifier.width(12.dp))
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .wrapContentSize()
            ) {
                Surface(
                    color = AccentYellow,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickableNoRipple { onSignOut() }
                ) {
                    Text(
                        text = "Sign Out",
                        color = ScreenBlack,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }
            }
        }

        
        BottomBarWithFabSelectable(
            modifier = Modifier.align(Alignment.BottomCenter),
            selectedTab = BottomTab.PROFILE,
            onClickHome = onClickHome,
            onClickAlarm = onClickAlarm,
            onClickAdd = onClickAdd,
            onClickGroup = onClickGroup,
            onClickProfile = onClickProfile
        )
    }
}



@Composable
fun EditBadge(
    onClick: () -> Unit
) {
    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 3.dp,
            color = AccentYellow
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickableNoRipple { onClick() }
    ) {
        Box(
            modifier = Modifier
                .background(Color.Transparent)
                .padding(horizontal = 6.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = AccentYellow,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


@Composable
private fun Modifier.borderWithAccent(): Modifier {
    return this.then(
        androidx.compose.ui.Modifier
            .border(
                width = 4.dp,
                color = AccentYellow,
                shape = RoundedCornerShape(8.dp)
            )
    )
}



private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        onClick()
    }
}
