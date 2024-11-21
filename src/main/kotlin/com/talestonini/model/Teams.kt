package com.talestonini.model

import org.jetbrains.exposed.dao.id.IntIdTable

object Teams : IntIdTable() {
    val name = varchar("NAME", 30)
    val codType = reference("COD_TYPE", TeamTypes.code)
    val fullName = varchar("FULL_NAME", 60)
    val foundation = varchar("FOUNDATION", 4)
    val city = varchar("CITY", 20)
    val codCountry = reference("COD_COUNTRY", Countries.code)
    val logoImgFile = varchar("LOGO_IMG_FILE", 255)

    override val tableName: String
        get() = "TEAM"
}