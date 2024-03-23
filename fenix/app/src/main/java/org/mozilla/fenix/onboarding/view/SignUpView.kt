/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.onboarding.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsWithImePadding
import org.mozilla.fenix.R
import org.mozilla.fenix.compose.button.PrimaryButtonOnboarding
import org.mozilla.fenix.onboarding.viewmodel.AccountViewModel
import org.mozilla.fenix.theme.FirefoxTheme

@Composable
fun SignUpView(
    onDismiss: () -> Unit,
    updatedOnboardingState: (UpgradeOnboardingState) -> Unit,
    viewModel: AccountViewModel,
    modifier: Modifier = Modifier,
    forcePadding: Boolean = false,
) {
    val signUpUiState by viewModel.uiState.collectAsState()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    if (signUpUiState.isSuccessfulSignUp) {
        updatedOnboardingState.invoke(UpgradeOnboardingState.Subscriptions)
    } else {
        Column(
            modifier = modifier,
        ) {
            val scrollState = rememberScrollState()
            val paddingModifier = if (forcePadding) {
                Modifier.padding(bottom = 80.dp)
            } else {
                Modifier
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .fillMaxWidth()
                    .background(FirefoxTheme.colors.layerOnboarding)
                    .then(paddingModifier),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                createCloseIcon(modifier = Modifier.align(Alignment.End), onDismiss)

                Spacer(modifier = Modifier.height(10.dp))

                addFreespokeLogo()

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    modifier = Modifier.padding(horizontal = 40.dp),
                    text = stringResource(id = R.string.join_freespoke_revolution),
                    color = FirefoxTheme.colors.onboardingTextColor,
                    textAlign = TextAlign.Center,
                    style = FirefoxTheme.typography.onboardingHeadLine2,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    modifier = Modifier.padding(horizontal = 40.dp),
                    text = stringResource(id = R.string.signup_subtitle),
                    color = FirefoxTheme.colors.freespokeDescriptionColor,
                    textAlign = TextAlign.Center,
                    style = FirefoxTheme.typography.body1,
                )

                Spacer(modifier = Modifier.height(32.dp))

                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = FirefoxTheme.colors.dividerColor,
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    modifier = Modifier,
                    shape = RoundedCornerShape(8.dp),
                    value = firstName,
                    onValueChange = {
                        firstName = it
                    },
                    isError = signUpUiState.errorData.firstName.isNotEmpty(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = FirefoxTheme.colors.layer2),
                    label = { Text(stringResource(id = R.string.addresses_first_name)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_person),
                            contentDescription = "",
                            tint = FirefoxTheme.colors.iconPrimary,
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                )
                if (signUpUiState.errorData.firstName.isNotEmpty()) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp),
                        text = signUpUiState.errorData.firstName.first(),
                        color = FirefoxTheme.colors.errorColor,
                        textAlign = TextAlign.Start,
                        style = FirefoxTheme.typography.caption,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = lastName,
                    onValueChange = {
                        lastName = it
                    },
                    isError = signUpUiState.errorData.lastName.isNotEmpty(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = FirefoxTheme.colors.layer2),
                    label = { Text(stringResource(id = R.string.addresses_last_name)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_person),
                            contentDescription = "",
                            tint = FirefoxTheme.colors.iconPrimary,
                        )
                    },
                    modifier = Modifier.navigationBarsWithImePadding(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                )

                if (signUpUiState.errorData.lastName.isNotEmpty()) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp),
                        text = signUpUiState.errorData.lastName.first(),
                        color = FirefoxTheme.colors.errorColor,
                        textAlign = TextAlign.Start,
                        style = FirefoxTheme.typography.caption,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                    },
                    isError = signUpUiState.errorData.email.isNotEmpty(),
                    label = { Text(stringResource(id = R.string.addresses_email)) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = FirefoxTheme.colors.layer2),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_email),
                            contentDescription = "",
                            tint = FirefoxTheme.colors.iconPrimary,
                        )
                    },
                    modifier = Modifier.navigationBarsWithImePadding(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                    ),
                )

                if (signUpUiState.errorData.email.isNotEmpty()) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp),
                        text = signUpUiState.errorData.email.first(),
                        color = FirefoxTheme.colors.errorColor,
                        textAlign = TextAlign.Start,
                        style = FirefoxTheme.typography.caption,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    isError = signUpUiState.errorData.password.isNotEmpty(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = FirefoxTheme.colors.layer2),
                    label = { Text(stringResource(id = R.string.password)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_password),
                            contentDescription = "",
                            tint = FirefoxTheme.colors.iconPrimary,
                        )
                    },
                    modifier = Modifier.navigationBarsWithImePadding(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                )

                if (signUpUiState.errorData.password.isNotEmpty()) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp),
                        text = signUpUiState.errorData.password.first(),
                        color = FirefoxTheme.colors.errorColor,
                        textAlign = TextAlign.Start,
                        style = FirefoxTheme.typography.caption,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .height(180.dp)
                    .background(FirefoxTheme.colors.layer2),
            ) {
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = FirefoxTheme.colors.dividerColor,
                )

                Spacer(modifier = Modifier.height(40.dp))

                PrimaryButtonOnboarding(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp),
                    text = stringResource(id = R.string.onboarding_next_button),
                    onClick = {
                        viewModel.signUp(firstName, lastName, email, password)
                    },
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                        .clickable { updatedOnboardingState(UpgradeOnboardingState.Login) },
                    text = stringResource(id = R.string.login_with_account),
                    color = FirefoxTheme.colors.textPrimary,
                    textAlign = TextAlign.Center,
                    style = FirefoxTheme.typography.subtitle1,
                )
            }
        }
    }
}
