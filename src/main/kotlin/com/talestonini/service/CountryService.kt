package com.talestonini.service

import com.talestonini.model.CountryEntity
import kotlinx.serialization.Serializable

data class Country(val id: Int?, val code: String, val name: String) {
    constructor(code: String, name: String) : this(null, code, name)
}

@Serializable
data class CountryApiView(val id: Int, val code: String, val name: String)

class CountryService {

    companion object {
        fun toCountry(countryEntity: CountryEntity): Country =
            Country(
                countryEntity.code,
                countryEntity.name
            )

        fun toCountryApiView(countryEntity: CountryEntity): CountryApiView =
            CountryApiView(
                countryEntity.id.value,
                countryEntity.code,
                countryEntity.name
            )
    }

}