package org.example.appbbmges.ui.usuarios.registation.administrativeform

object AdministrativeFormValidation {
    fun validatePersonalInfo(data: AdministrativeFormData): AdministrativeFormErrors {
        return AdministrativeFormErrors(
            firstName = validateName(data.firstName, "nombre", true),
            lastNamePaternal = validateName(data.lastNamePaternal, "apellido paterno", false),
            lastNameMaternal = validateName(data.lastNameMaternal, "apellido materno", false),
            birthDate = validateBirthDate(data.birthDate),
            phone = validatePhone(data.phone),
            email = validateEmail(data.email)
        )
    }

    fun validateProfessionalInfo(data: AdministrativeFormData): AdministrativeFormErrors {
        return AdministrativeFormErrors(
            taxId = validateTaxId(data.taxId),
            nss = validateNSS(data.nss),
            salary = validateSalary(data.salary),
            startDate = validateStartDate(data.startDate)
        )
    }

    fun validateAddressInfo(data: AdministrativeFormData): AdministrativeFormErrors {
        return AdministrativeFormErrors(
            addressStreet = validateAddressStreet(data.addressStreet),
            addressZip = validateAddressZip(data.addressZip)
        )
    }

    fun validateAdditionalInfo(data: AdministrativeFormData): AdministrativeFormErrors {
        return AdministrativeFormErrors(
            emergencyContactPhone = validateEmergencyPhone(data.emergencyContactPhone)
        )
    }

    private fun validateName(name: String, fieldName: String, required: Boolean): String? {
        if (required && name.isBlank()) return "El $fieldName es obligatorio."
        if (name.isNotBlank()) {
            if (name.length !in 2..50) return "El $fieldName debe tener entre 2 y 50 caracteres."
            if (name.any { it.isDigit() }) return "El $fieldName no puede contener números."
            if (!name.matches("^[A-Za-zÀ-ÿ ]+$".toRegex())) return "El $fieldName solo puede contener letras y espacios."
        }
        return null
    }

    private fun validateBirthDate(date: String): String? {
        if (date.isBlank()) return null

        if (!date.matches("\\d{4}-\\d{2}-\\d{2}".toRegex())) {
            return "Formato de fecha inválido."
        }

        val age = AdministrativeFormUtils.calculateAge(date)
        return when {
            age == null -> "Formato de fecha inválido."
            age < 18.0 -> "La edad mínima es 18 años."
            age > 100.0 -> "Edad no válida."
            else -> null
        }
    }

    private fun validateTaxId(taxId: String): String? {
        if (taxId.isEmpty()) return null

        val cleanTaxId = taxId.uppercase().trim()

        return when {
            cleanTaxId.length !in 12..13 -> "El RFC debe tener 12 o 13 caracteres."
            cleanTaxId.length == 13 && !cleanTaxId.matches("^[A-Z]{4}\\d{6}[A-Z0-9]{3}$".toRegex()) ->
                "Formato de RFC inválido para persona física (13 caracteres)."
            cleanTaxId.length == 12 && !cleanTaxId.matches("^[A-Z]{3}\\d{6}[A-Z0-9]{3}$".toRegex()) ->
                "Formato de RFC inválido para persona moral (12 caracteres)."
            else -> null
        }
    }

    private fun validateNSS(nss: String): String? {
        if (nss.isEmpty()) return null

        val cleanNSS = nss.trim()

        return when {
            cleanNSS.length != 11 -> "El NSS debe tener 11 dígitos."
            !cleanNSS.all { it.isDigit() } -> "El NSS solo debe contener dígitos."
            else -> null
        }
    }

    private fun validateSalary(salary: String): String? {
        if (salary.isEmpty()) return null

        val salaryValue = salary.toDoubleOrNull()

        return when {
            salaryValue == null -> "El salario debe ser un número válido."
            salaryValue <= 0 -> "El salario debe ser mayor a 0."
            else -> null
        }
    }

    private fun validatePhone(phone: String): String? {
        if (phone.isEmpty()) return null
        return if (!phone.matches("^\\d{10}$".toRegex())) "El teléfono debe tener 10 dígitos."
        else null
    }

    private fun validateEmail(email: String): String? {
        if (email.isEmpty()) return null
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return if (!emailPattern.matches(email)) "Formato de email no válido."
        else null
    }

    private fun validateAddressStreet(address: String): String? {
        if (address.isEmpty()) return null
        return when {
            address.length !in 5..100 -> "La calle debe tener entre 5 y 100 caracteres."
            !address.matches("^[A-Za-z0-9À-ÿ\\s.,#-]+$".toRegex()) ->
                "La calle contiene caracteres no válidos. Solo se permiten letras, números, espacios y los símbolos: ., #-."
            else -> null
        }
    }

    private fun validateAddressZip(zip: String): String? {
        if (zip.isEmpty()) return null
        return if (zip.length != 5 || !zip.all { it.isDigit() }) "El código postal debe tener 5 dígitos."
        else null
    }

    private fun validateEmergencyPhone(phone: String): String? {
        if (phone.isEmpty()) return null
        return if (!phone.matches("^\\d{10}$".toRegex())) "El teléfono debe tener 10 dígitos."
        else null
    }

    private fun validateStartDate(date: String): String? {
        if (date.isBlank()) return null

        if (!date.matches("\\d{4}-\\d{2}-\\d{2}".toRegex())) {
            return "Formato de fecha inválido."
        }

        return null
    }
}