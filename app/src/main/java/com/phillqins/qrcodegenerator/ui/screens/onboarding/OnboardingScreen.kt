package com.phillqins.qrcodegenerator.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phillqins.qrcodegenerator.R
import com.phillqins.qrcodegenerator.domain.model.OnboardingText
import com.phillqins.qrcodegenerator.ui.theme.QRCodeGeneratorTheme
import com.phillqins.qrcodegenerator.ui.utils.CollectOneTimeEvent
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingScreenRoot(
    onGotoHome: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    CollectOneTimeEvent(viewModel.event) { event ->
        when(event){
            OnboardingEvent.OnClickContinue -> onGotoHome()
        }
    }
    OnboardingScreen(
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun OnboardingScreen(
    state: OnboardingUiState,
    onAction: (OnboardingAction) -> Unit
) {
    val onboardingTexts = listOf(
        OnboardingText(stringResource(R.string.onboarding_title_1), stringResource(R.string.onboarding_text_1)),
        OnboardingText(stringResource(R.string.onboarding_title_2), stringResource(R.string.onboarding_text_2)),
        OnboardingText(stringResource(R.string.onboarding_title_3), stringResource(R.string.onboarding_text_3))
    )

    val pagerState = rememberPagerState(pageCount = { onboardingTexts.size })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            modifier = Modifier.align(Alignment.Start),
            text = stringResource(R.string.welcome_text),
            style = TextStyle.Default.copy(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.weight(0.4f))

        Image(
            painter = painterResource(R.drawable.qrkod),
            contentDescription = "QR Code Icon"
        )

        Spacer(modifier = Modifier.weight(0.1f))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.weight(0.2f))

                OnboardingText(
                    onboardingText = onboardingTexts[page]
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        Indicator(
            pagerState = pagerState,
            itemSize = onboardingTexts.size
        )

        Spacer(modifier = Modifier.weight(0.3f))

        Row(
            modifier = Modifier.align(Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onAction(OnboardingAction.OnClickContinue) }
            ) {
                Text(text = stringResource(R.string.continue_button))
            }
        }

        Spacer(modifier = Modifier.weight(0.1f))
    }
}

@Composable
private fun OnboardingText(onboardingText: OnboardingText){
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = onboardingText.title,
            style = TextStyle.Default.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = onboardingText.description,
            textAlign = TextAlign.Center
        )
    }

}

@Composable
private fun Indicator(
    pagerState: PagerState,
    itemSize: Int,
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(itemSize) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (pagerState.currentPage == index)
                            MaterialTheme.colorScheme.primary
                        else
                            Color.LightGray
                    )
            )
            if (index < itemSize - 1) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    QRCodeGeneratorTheme(){
       OnboardingScreen(
           state = OnboardingUiState(),
           onAction = {}
       )
   }
}