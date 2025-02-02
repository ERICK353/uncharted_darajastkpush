# DarajaSTK Push Library

The DarajaSTK Push Library is an Android library that simplifies integration with the Safaricom Daraja API for STK (Sim ToolKit) push functionality. It handles access token generation, STK push requests, and response handling, making it easy to implement mobile payment features in your Android app.

## Features

- **Access Token Generation**: Automatically fetches and manages OAuth 2.0 access tokens for Daraja API authentication.
- **STK Push Requests**: Initiates STK push requests with minimal configuration.
- **Coroutine Support**: Uses Kotlin coroutines for asynchronous API calls.
- **Error Handling**: Provides detailed error messages for failed requests.

## Installation

### Using JitPack

1. Add the JitPack repository to your project's `build.gradle` file:

    ```groovy
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories {
            google()
            mavenCentral()
            maven { url=uri("https://jitpack.io") }
        }
    }
    ```

2. Add the dependency to your app's `build.gradle` file:

    ```groovy
    dependencies {
        implementation ("com.github.ERICK353:uncharted_darajastkpush:v1.1.6")
    }
    ```

## Usage

### Initialize the Library

1. Create an instance of the `DarajaSTK` class with your consumer key and consumer secret:

    ```kotlin
    val darajaSTK = DarajaSTK(
        context = applicationContext,
        consumerKey = "YOUR_CONSUMER_KEY",
        consumerSecret = "YOUR_CONSUMER_SECRET"
    )
    ```

### Initiate an STK Push

2. Use the `initiateSTKPush` method to initiate an STK push request:

    ```kotlin
    CoroutineScope(Dispatchers.Main).launch {
        val result = darajaSTK.initiateSTKPush(
            businessShortCode = "174379",
            password = "YOUR_PASSWORD", // Generate this dynamically
            timestamp = "YOUR_TIMESTAMP", // Generate this dynamically
            transactionType = "CustomerPayBillOnline",
            amount = "1",
            partyA = "PHONENUMBER(Format:254{Phonenumber})e.g254770837414",
            partyB = "174379", // REPLACE WITH YOUR OWN PAYBILL/TILLNUMBER
            phoneNumber = "PHONENUMBER(Format:254{Phonenumber})e.g254770837414",
            callBackURL = "YOUR_CALLBACK_URL",
            accountReference = "Test123",
            transactionDesc = "Payment"
        )

        when (result) {
            is DarajaSTK.Result.Success -> {
                println("STK Push Successful: ${result.data.ResponseDescription}")
            }
            is DarajaSTK.Result.Error -> {
                println("STK Push Failed: ${result.message}")
            }
        }
    }
    ```

### Generate Password and Timestamp

The password and timestamp fields are required for the STK push request. You can generate them dynamically:

```kotlin
// Generate a Base64-encoded password
fun generatePassword(businessShortCode: String, passkey: String, timestamp: String): String {
    val str = "$businessShortCode$passkey$timestamp"
    return Base64.encodeToString(str.toByteArray(), Base64.NO_WRAP)
}
    ```

```kotlin
// Generate a timestamp in the format yyyyMMddHHmmss
fun generateTimestamp(): String {
    val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
    return dateFormat.format(Date())
}
   ```

## Example Usage

Here’s a complete example of using the library in an Android app:

```kotlin
@Composable
fun StkPushScreen() {
    var resultMessage by remember { mutableStateOf("") }
    val darajaSTK = remember {
        DarajaSTK(
            context = LocalContext.current,
            consumerKey = "YOUR_CONSUMER_KEY",
            consumerSecret = "YOUR_CONSUMER_SECRET"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                initiateStkPush(darajaSTK) { success, message ->
                    resultMessage = if (success) {
                        "STK Push initiated successfully: $message"
                    } else {
                        "Failed to initiate STK Push: $message"
                    }
                }
            }
        ) {
            Text("Initiate STK Push")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = resultMessage)
    }
}

private fun initiateStkPush(
    darajaSTK: DarajaSTK,
    onResult: (Boolean, String?) -> Unit
) {
    val phoneNumber = "254770837414"
    val amount = "1"
    val accountReference = "Test123"
    val transactionDesc = "Payment"
    val timestamp = generateTimestamp()
    val password = generatePassword(
        businessShortCode = "174379",
        passkey = "YOUR_PASSKEY",
        timestamp = timestamp
    )

    CoroutineScope(Dispatchers.Main).launch {
        val result = darajaSTK.initiateSTKPush(
            businessShortCode = "174379",
            password = password,
            timestamp = timestamp,
            transactionType = "CustomerPayBillOnline",
            amount = amount,
            partyA = phoneNumber,
            partyB = "174379",
            phoneNumber = phoneNumber,
            callBackURL = "YOUR_CALLBACK_URL",
            accountReference = accountReference,
            transactionDesc = transactionDesc
        )

        when (result) {
            is DarajaSTK.Result.Success -> {
                onResult(true, result.data.ResponseDescription)
            }
            is DarajaSTK.Result.Error -> {
                onResult(false, result.message)
            }
        }
    }
}

// Function to generate password dynamically
fun generatePassword(businessShortCode: String, passkey: String, timestamp: String): String {
    val str = "$businessShortCode$passkey$timestamp"
    return Base64.encodeToString(str.toByteArray(), Base64.NO_WRAP)
}

// Function to generate timestamp dynamically
fun generateTimestamp(): String {
    val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
    return dateFormat.format(Date())
}
  ```
## Configuration

Required Permissions in Android Manifest File
```kotlin

<uses-permission android:name="android.permission.INTERNET" />

  ```

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## Contributing
Contributions are welcome! Please open an issue or submit a pull request for any improvements or bug fixes.

---

## Support
For questions or issues, please open an issue on the [GitHub repository](https://github.com/ERICK353/uncharted_darajastkpush).

---

## Acknowledgments
- **Safaricom** for providing the Daraja API.
- **Retrofit** and **OkHttp** for simplifying network requests.
- 
## Changelog
- **v1.1.6**: Initial release with STK push functionality.
