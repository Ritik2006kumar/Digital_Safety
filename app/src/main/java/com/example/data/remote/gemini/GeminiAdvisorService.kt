package com.example.data.remote.gemini

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiAdvisorService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun consultSecurityAdvisor(
        prompt: String,
        contextSnapshot: String? = null
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Provide high quality local security expert analysis fallback if API key is not yet configured in Secrets
            return@withContext getLocalExpertAdvice(prompt)
        }

        try {
            val systemInstructions = """
                You are a senior Android Cybersecurity & Digital Privacy Analyst named "ShieldAI" embedded in the Digital Safety app.
                Provide direct, actionable, objective, and clear security insights.
                Focus on:
                1. Explaining why apps request sensitive permissions (Camera, Mic, Contacts, Location, SMS) and how to protect privacy.
                2. Identifying phishing, social engineering, OTP scams, UPI fraud, and deceptive APK payloads.
                3. Best practices for Wi-Fi encryption, Private DNS, screen lock security, and device integrity.
                Keep responses structured, concise, and easy to read with bullet points.
            """.trimIndent()

            val fullPrompt = if (!contextSnapshot.isNullOrBlank()) {
                "CURRENT DEVICE SECURITY CONTEXT:\n$contextSnapshot\n\nUSER QUESTION:\n$prompt"
            } else {
                prompt
            }

            val requestJson = JSONObject().apply {
                val contents = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", fullPrompt))
                        })
                    })
                }
                put("contents", contents)
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemInstructions))
                    })
                })
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = requestJson.toString().toRequestBody(mediaType)
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val json = JSONObject(responseBody)
                val candidates = json.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val contentObj = firstCandidate?.optJSONObject("content")
                val parts = contentObj?.optJSONArray("parts")
                val text = parts?.optJSONObject(0)?.optString("text")
                if (!text.isNullOrBlank()) {
                    return@withContext text
                }
            }
            return@withContext getLocalExpertAdvice(prompt)
        } catch (e: Exception) {
            return@withContext getLocalExpertAdvice(prompt)
        }
    }

    private fun getLocalExpertAdvice(prompt: String): String {
        val lower = prompt.lowercase()
        val isHindi = lower.contains("kya") || lower.contains("kaise") || lower.contains("nahi") || 
                      lower.contains("hai") || lower.contains("kyu") || lower.contains("kyo") || 
                      lower.contains("batao") || lower.contains("karo") || lower.contains("kuch")

        return when {
            // General query about response / how to use the app
            lower.contains("response") || lower.contains("kaam") || lower.contains("help") || lower.contains("kaise use") || lower.contains("start") -> {
                if (isHindi) {
                    """
                    🛡️ **ShieldAI Security Assistant:**
                    Aapka sawal mil gaya hai! Main aapka Android Digital Safety & Privacy Advisor hoon.
                    
                    **Aap yahan kya-kya pooch sakte hain:**
                    • **Apps & Permissions:** Kisi bhi app ki permissions (Camera, Mic, SMS, Location) safe hain ya nahi.
                    • **Scams & Fraud:** Fake bank SMS, lottery messages, ya suspicious links check karne ke liye.
                    • **Device Security:** Phone hack hone se kaise bachayein aur screen lock/DNS kaise setup karein.
                    • **Vault:** Apne passwords aur sensitive notes ko AES-256 encrypted vault me kaise save karein.
                    
                    Neeche diye gaye chips par click karein ya apna sawal type karein!
                    """.trimIndent()
                } else {
                    """
                    🛡️ **ShieldAI Security Assistant:**
                    I am your active on-device cybersecurity & privacy advisor.
                    
                    **What you can do:**
                    • **Scan Apps & Permissions:** Check which apps can access your Camera, Mic, SMS, or Location.
                    • **Scam & URL Analyzer:** Paste suspicious messages or links to detect phishing and fraud.
                    • **Encrypted Vault:** Store confidential notes, recovery keys, and passwords with AES-256 encryption.
                    • **Wi-Fi & DNS Audit:** Inspect your connection encryption and configure Private DNS.
                    
                    Type any cybersecurity question or tap a suggestion chip below!
                    """.trimIndent()
                }
            }
            
            // Hack / Phone security check
            lower.contains("hack") || lower.contains("virus") || lower.contains("malware") || lower.contains("safe") -> {
                if (isHindi) {
                    """
                    🔒 **Device Security & Anti-Malware Advice:**
                    • **App Scan:** Dashboard par jaakar **"Quick Audit Scan"** run karein taaki high-risk apps identify ho sakein.
                    • **Unknown Sources:** Settings me "Install unknown apps" ko disable rakhein taaki koi unauthorized APK install na ho.
                    • **Screen Lock & ADB:** Settings me screen lock zaroor lagayein aur developer options me USB Debugging band rakhein.
                    • **App Permissions:** Aise apps jinko SMS, Camera ya Accessibility ki zaroorat nahi hai, unki permissions revoke karein.
                    """.trimIndent()
                } else {
                    """
                    🔒 **Device Security & Malware Protection:**
                    • **Run Full Scan:** Use the **"Quick Audit Scan"** on the Dashboard to inspect installed apps for suspicious behavior.
                    • **Sideloading Protection:** Keep "Install from unknown sources" turned OFF in Android settings.
                    • **Disable ADB:** Ensure USB Debugging is disabled unless actively developing.
                    • **Revoke High-Risk Permissions:** Check the Sensitive Permissions tab and remove SMS/Accessibility access from utility apps.
                    """.trimIndent()
                }
            }

            // Location
            lower.contains("location") || lower.contains("gps") -> {
                if (isHindi) {
                    """
                    📍 **Location Permission Analysis:**
                    • **Kyu zaroorat hoti hai:** Maps, cab aur food delivery apps ko delivery ke liye GPS chahiye hota hai.
                    • **Kab suspicious hai:** Torch, calculator, ringtone ya photo editing apps ko location ki bilkul zaroorat nahi hoti.
                    • **Safe Setting:** Permission ko **"While using the app"** ya **"Approximate location"** par set karein.
                    """.trimIndent()
                } else {
                    """
                    📍 **Location Permission Analysis:**
                    • **Why apps need it:** Navigation, ride-sharing, and delivery apps require GPS to locate you.
                    • **When it's suspicious:** Calculators, wallpapers, flashlight, or photo editors do NOT need location access.
                    • **Recommendation:** Set permission to **"While using the app"** or **"Approximate location"** rather than "Always allow".
                    """.trimIndent()
                }
            }

            // SMS / OTP
            lower.contains("sms") || lower.contains("otp") || lower.contains("bank") || lower.contains("paisa") -> {
                if (isHindi) {
                    """
                    💬 **SMS & OTP Fraud Protection:**
                    • **Sabse Bada Risk:** SMS permission milne par apps aapke bank OTPs aur 2FA codes padh sakti hain.
                    • **Golden Rule:** Sirf aapke default messaging app ke paas SMS permission honi chahiye.
                    • **Action:** Sensitive Permissions tab me jakar SMS access check karein aur anjaan apps se turant hata dein.
                    • **Scam Alert:** Bank kabhi bhi SMS me link bhej kar KYC update ya PIN verification nahi mangta!
                    """.trimIndent()
                } else {
                    """
                    💬 **SMS & OTP Protection Advisory:**
                    • **High Risk:** SMS permissions allow apps to read incoming 2FA verification codes, banking alerts, and OTPs.
                    • **Rule of Thumb:** Only your default messaging app should have SMS permissions.
                    • **Action:** Go to **Sensitive Permissions > SMS** and revoke access from third-party utilities immediately.
                    • **Warning:** Banks will never send SMS links asking for KYC verification or PIN entry.
                    """.trimIndent()
                }
            }

            // Wi-Fi / DNS
            lower.contains("wifi") || lower.contains("dns") || lower.contains("network") || lower.contains("internet") -> {
                if (isHindi) {
                    """
                    🌐 **Wi-Fi & Network Privacy Best Practices:**
                    • **Public Wi-Fi:** Hotel ya cafe ke open Wi-Fi par banking ya sensitive transactions bina VPN ke na karein.
                    • **Private DNS:** Android **Settings > Network & internet > Private DNS** me jayein aur `dns.google` ya `one.one.one.one` daalein.
                    • Isse aapka internet traffic encrypt ho jata hai aur ISP tracking rukti hai.
                    """.trimIndent()
                } else {
                    """
                    🌐 **Network Privacy Best Practices:**
                    • **Public Wi-Fi:** Open Wi-Fi hot spots can be spoofed or unencrypted. Avoid making financial transactions without a VPN.
                    • **Private DNS:** Go to **Android Settings > Network & Internet > Private DNS** and set it to `dns.google` or `one.one.one.one` to prevent ISP tracking.
                    • Encrypted DNS ensures domain lookups cannot be eavesdropped by rogue Wi-Fi routers.
                    """.trimIndent()
                }
            }

            // Camera / Mic
            lower.contains("camera") || lower.contains("microphone") || lower.contains("mic") || lower.contains("photo") -> {
                if (isHindi) {
                    """
                    🎙️ **Microphone & Camera Security:**
                    • **Green Privacy Indicator:** Android screen ke top corner me green dot dikhe toh samjhein koi app camera ya mic use kar rahi hai.
                    • **Audit:** App me **Sensitive Permissions** screen par check karein ki kin apps ke paas background recording permission hai.
                    """.trimIndent()
                } else {
                    """
                    🎙️ **Microphone & Camera Security:**
                    • **Green Indicator Dot:** Watch for the green privacy dot in the top corner of your Android screen indicating active recording.
                    • **Audit:** Check our **Sensitive Permissions** tab to see which background apps have granted microphone access.
                    """.trimIndent()
                }
            }

            // Vault / Password
            lower.contains("vault") || lower.contains("password") || lower.contains("pin") || lower.contains("encrypt") -> {
                if (isHindi) {
                    """
                    🔐 **Encrypted Vault Features:**
                    • Vault me aap apne important passwords, recovery phrases aur confidential notes save kar sakte hain.
                    • Yeh data **AES-256 encryption** se safe rehta hai jo aapke master PIN (Default: `1234`) se protect hota hai.
                    """.trimIndent()
                } else {
                    """
                    🔐 **Encrypted Vault Features:**
                    • Use the Encrypted Vault to store confidential notes, recovery seed phrases, and 2FA backup codes.
                    • Data is secured with military-grade **AES-256 encryption** protected by your master PIN (Default: `1234`).
                    """.trimIndent()
                }
            }

            else -> {
                if (isHindi) {
                    """
                    🛡️ **Digital Safety Security Advisor:**
                    • Phone par biometric ya strong screen lock hamesha enable rakhein.
                    • Anjaan messages me aaye huye suspicious links par kabhi click na karein.
                    • Apps ki permission regular check karein — khaaskar SMS, Camera aur Location.
                    • Kisi specific permission ya scam message ke baare me poochne ke liye yahan type karein!
                    """.trimIndent()
                } else {
                    """
                    🛡️ **Security Recommendation:**
                    • Keep your device lock screen enabled with biometric authentication or a strong PIN.
                    • Review high-risk apps that request unexpected permissions (Contacts, Storage, SMS).
                    • Never click links from unknown senders offering rewards or threatening urgent account suspension.
                    • Ask me anything about specific permissions, scams, or Android privacy!
                    """.trimIndent()
                }
            }
        }
    }
}
