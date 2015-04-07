package button.football

class Country {

    String name

    static hasMany = [teams: Team,
                      unifiedRankingTypes: UnifiedRankingType]

    static mapping = {
        unifiedRankingTypes joinTable: [name: 'mm_unif_rk_type_country', key: 'country_id']
        version false
    }

    static constraints = {
        name maxSize: 30, unique: true
    }
}
