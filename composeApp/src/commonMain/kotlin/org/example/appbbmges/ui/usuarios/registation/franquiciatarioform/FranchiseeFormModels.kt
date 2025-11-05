package org.example.appbbmges.ui.usuarios.registation.franquiciatarioform

data class FranchiseeFormData(
    val firstName: String = "",
    val lastNamePaternal: String = "",
    val lastNameMaternal: String = "",
    val gender: String = "",
    val birthDate: String = "",
    val nationality: String = "Mexicana",
    val taxId: String = "", // RFC
    val countryCode: String = "+52",
    val phone: String = "",
    val email: String = "",
    val addressStreet: String = "",
    val addressZip: String = "",
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val startDate: String = "",
    val active: Boolean = true,
    val username: String = "",
    val password: String = ""
)

data class FormErrors(
    val firstName: String? = null,
    val lastNamePaternal: String? = null,
    val lastNameMaternal: String? = null,
    val birthDate: String? = null,
    val taxId: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val addressStreet: String? = null,
    val addressZip: String? = null,
    val emergencyContactPhone: String? = null,
    val startDate: String? = null,
    val general: String? = null
) {
    val isValid: Boolean
        get() = firstName == null && lastNamePaternal == null &&
                lastNameMaternal == null && birthDate == null &&
                taxId == null && phone == null && email == null &&
                addressStreet == null && addressZip == null &&
                emergencyContactPhone == null && startDate == null &&
                general == null
}

enum class FranchiseeFormStep {
    PERSONAL_INFO,
    ADDRESS_INFO,
    ADDITIONAL_INFO,
    CONFIRMATION
}